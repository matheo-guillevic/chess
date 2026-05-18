package cli;

import engine.ChargementPiecesResultat;
import engine.Coup;
import engine.Game;
import engine.PiecePersonnaliseeInfo;
import piece.Couleur;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Interface utilisateur en ligne de commande.
 *
 * <p>Elle gere le chargement interactif des pieces personnalisees, la saisie
 * des coups au format {@code e2 e4}, l'affichage du plateau et le mode IA.</p>
 */
public class ConsoleUI {
    private final Game game;
    private final boolean proposerPiecesPersonnalisees;
    private final Couleur couleurIA;

    /**
     * Cree une interface console avec chargement JSON propose et sans IA.
     *
     * @param game partie a piloter
     */
    public ConsoleUI(Game game) {
        this(game, true, null);
    }

    /**
     * Cree une interface console.
     *
     * @param game partie a piloter
     * @param proposerPiecesPersonnalisees {@code true} pour proposer le chargement JSON au lancement
     */
    public ConsoleUI(Game game, boolean proposerPiecesPersonnalisees) {
        this(game, proposerPiecesPersonnalisees, null);
    }

    /**
     * Cree une interface console complete.
     *
     * @param game partie a piloter
     * @param proposerPiecesPersonnalisees {@code true} pour proposer le chargement JSON
     * @param couleurIA couleur jouee par l'ordinateur, ou {@code null} sans IA
     */
    public ConsoleUI(Game game, boolean proposerPiecesPersonnalisees, Couleur couleurIA) {
        this.game = game;
        this.proposerPiecesPersonnalisees = proposerPiecesPersonnalisees;
        this.couleurIA = couleurIA;
    }

    /**
     * Lance la boucle de jeu console.
     */
    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Jeu d'Echecs ===");
            if (proposerPiecesPersonnalisees) {
                demanderChargementPiecesPersonnalisees(scanner);
            }
            System.out.println("Entrez vos coups sous le format 'e2 e4'. Tapez 'quit' pour quitter.");
            if (couleurIA != null) {
                System.out.println("Mode joueur vs ordinateur : l'ordinateur joue " + couleurIA + ".");
            }

            while (!game.isFinished()) {
                if (jouerTourIA()) {
                    continue;
                }

                game.getGrille().afficher();
                afficherEtatRoi();
                System.out.println("Tour du joueur : " + game.getCurrentTurn());
                System.out.print("Entrez votre coup : ");
                
                if (!scanner.hasNextLine()) break;
                String input = scanner.nextLine().trim().toLowerCase();
                
                if (input.equals("quit") || input.equals("exit")) {
                    break;
                }

                if (input.length() != 5 || input.charAt(2) != ' ') {
                    System.out.println("Format invalide. Utilisez le format 'e2 e4'.");
                    continue;
                }

                int startX = input.charAt(0) - 'a';
                int startY = input.charAt(1) - '1';
                int endX = input.charAt(3) - 'a';
                int endY = input.charAt(4) - '1';

                if (startX < 0 || startX > 7 || startY < 0 || startY > 7 ||
                    endX < 0 || endX > 7 || endY < 0 || endY > 7) {
                    System.out.println("Coordonnees hors de la grille.");
                    continue;
                }

                boolean success = game.tryMove(startX, startY, endX, endY);
                if (!success) {
                    System.out.println("Coup invalide.");
                } else if (game.isFinished()) {
                    game.getGrille().afficher();
                    afficherResultatFin();
                }
            }
        }
        System.out.println("Fin de la partie.");
    }

    private boolean jouerTourIA() {
        if (couleurIA == null || game.getCurrentTurn() != couleurIA || game.isFinished()) {
            return false;
        }

        Optional<Coup> coup = game.jouerCoupAutomatique();
        if (coup.isPresent()) {
            System.out.println("Ordinateur (" + couleurIA + ") joue : " + coup.get());
        }

        if (game.isFinished()) {
            game.getGrille().afficher();
            afficherResultatFin();
        }
        return true;
    }

    private void afficherEtatRoi() {
        if (game.isKingInCheck(game.getCurrentTurn())) {
            System.out.println("Echec au roi " + game.getCurrentTurn() + " !");
        }
    }

    private void afficherResultatFin() {
        if (game.getWinner() == null) {
            System.out.println("Pat : match nul.");
        } else {
            System.out.println("Le joueur " + game.getWinner() + " a gagne !");
        }
    }

    private void demanderChargementPiecesPersonnalisees(Scanner scanner) {
        System.out.print("Voulez-vous charger un fichier de pieces personnalisees ? (o/N) : ");
        if (!scanner.hasNextLine()) return;

        String reponse = scanner.nextLine().trim().toLowerCase();
        if (!reponse.equals("o") && !reponse.equals("oui")) return;

        System.out.print("Chemin du fichier JSON [pieces_perso.json] : ");
        if (!scanner.hasNextLine()) return;

        String chemin = scanner.nextLine().trim();
        if (chemin.isEmpty()) {
            chemin = "pieces_perso.json";
        }

        Path fichier = Path.of(chemin);
        List<PiecePersonnaliseeInfo> catalogue = game.lireCataloguePiecesPersonnalisees(fichier);
        Set<String> selection = choisirPiecesPersonnalisees(scanner, catalogue);
        if (selection == null) {
            System.out.println("Chargement annule.");
            return;
        }

        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(fichier, selection);
        System.out.println(resultat.getPiecesAjoutees() + " piece(s) personnalisee(s) ajoutee(s).");
        for (String erreur : resultat.getErreurs()) {
            System.out.println("- " + erreur);
        }
    }

    private Set<String> choisirPiecesPersonnalisees(Scanner scanner, List<PiecePersonnaliseeInfo> catalogue) {
        if (catalogue.isEmpty()) {
            System.out.println("Aucune piece personnalisee lisible dans ce fichier.");
            return null;
        }

        System.out.println();
        System.out.println("Pieces disponibles :");
        for (int i = 0; i < catalogue.size(); i++) {
            PiecePersonnaliseeInfo info = catalogue.get(i);
            System.out.println((i + 1) + " - " + info);
        }
        System.out.println("Entrez les numeros a charger separes par des virgules, ou 'all' pour tout charger.");
        System.out.print("Votre selection [all] : ");
        if (!scanner.hasNextLine()) return null;

        String input = scanner.nextLine().trim().toLowerCase();
        if (input.isEmpty() || input.equals("all") || input.equals("tout")) {
            return Set.of();
        }

        Set<String> selection = new HashSet<>();
        String[] morceaux = input.split(",");
        for (String morceau : morceaux) {
            try {
                int index = Integer.parseInt(morceau.trim()) - 1;
                if (index >= 0 && index < catalogue.size()) {
                    PiecePersonnaliseeInfo info = catalogue.get(index);
                    selection.add(info.getNom());
                    System.out.println("Selection : " + info.getSymbole() + " " + info.getNom() + " - " + info.getDescription());
                }
            } catch (NumberFormatException e) {
                System.out.println("Selection ignoree : " + morceau.trim());
            }
        }

        return selection.isEmpty() ? null : selection;
    }
}
