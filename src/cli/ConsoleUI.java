package cli;

import engine.ChargementPiecesResultat;
import engine.Coup;
import engine.Game;
import piece.Couleur;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final Game game;
    private final boolean proposerPiecesPersonnalisees;
    private final Couleur couleurIA;

    public ConsoleUI(Game game) {
        this(game, true, null);
    }

    public ConsoleUI(Game game, boolean proposerPiecesPersonnalisees) {
        this(game, proposerPiecesPersonnalisees, null);
    }

    public ConsoleUI(Game game, boolean proposerPiecesPersonnalisees, Couleur couleurIA) {
        this.game = game;
        this.proposerPiecesPersonnalisees = proposerPiecesPersonnalisees;
        this.couleurIA = couleurIA;
    }

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

        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(Path.of(chemin));
        System.out.println(resultat.getPiecesAjoutees() + " piece(s) personnalisee(s) ajoutee(s).");
        for (String erreur : resultat.getErreurs()) {
            System.out.println("- " + erreur);
        }
    }
}
