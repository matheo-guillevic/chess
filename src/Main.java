import javax.swing.SwingUtilities;
import gui.ChessGUI;
import engine.ChargementPiecesResultat;
import engine.Game;
import piece.Couleur;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();

        boolean useCli = choisirModeCli(args);
        Couleur couleurIA = getCouleurIA(args);
        Path fichierPieces = getArgValue(args, "--pieces");
        if (fichierPieces != null) {
            afficherResultatChargement(game.chargerPiecesPersonnalisees(fichierPieces));
        }

        if (useCli) {
            // Lancement en mode console
            cli.ConsoleUI console = new cli.ConsoleUI(game, fichierPieces == null, couleurIA);
            console.start();
        } else {
            // Lancement en mode graphique
            SwingUtilities.invokeLater(() -> {
                ChessGUI gui = new ChessGUI(game, couleurIA);
                gui.setVisible(true);
            });
        }
    }

    private static boolean choisirModeCli(String[] args) {
        if (hasArg(args, "--cli")) return true;
        if (hasArg(args, "--gui")) return false;

        System.out.println("=== Jeu d'Echecs ===");
        System.out.println("Choisissez le mode de demarrage :");
        System.out.println("1 - Interface graphique (GUI)");
        System.out.println("2 - Console (CLI)");
        System.out.print("Votre choix [1] : ");

        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) {
            return false;
        }

        String choix = scanner.nextLine().trim().toLowerCase();
        return choix.equals("2") || choix.equals("cli") || choix.equals("console");
    }

    private static boolean hasArg(String[] args, String arg) {
        for (String value : args) {
            if (value.equals(arg)) return true;
        }
        return false;
    }

    private static Path getArgValue(String[] args, String arg) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(arg)) return Path.of(args[i + 1]);
        }
        return null;
    }

    private static Couleur getCouleurIA(String[] args) {
        if (hasArg(args, "--ai-white")) return Couleur.BLANC;
        if (hasArg(args, "--ai") || hasArg(args, "--ai-black")) return Couleur.NOIR;
        return null;
    }

    private static void afficherResultatChargement(ChargementPiecesResultat resultat) {
        System.out.println(resultat.getPiecesAjoutees() + " piece(s) personnalisee(s) ajoutee(s).");
        for (String erreur : resultat.getErreurs()) {
            System.out.println("- " + erreur);
        }
    }
}
