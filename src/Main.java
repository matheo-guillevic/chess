import javax.swing.SwingUtilities;
import gui.ChessGUI;
import engine.ChargementPiecesResultat;
import engine.Game;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();

        boolean useGui = hasArg(args, "--gui");
        Path fichierPieces = getArgValue(args, "--pieces");
        if (fichierPieces != null) {
            afficherResultatChargement(game.chargerPiecesPersonnalisees(fichierPieces));
        }

        if (useGui) {
            // Lancement en mode graphique
            SwingUtilities.invokeLater(() -> {
                ChessGUI gui = new ChessGUI(game);
                gui.setVisible(true);
            });
        } else {
            // Lancement en mode console
            cli.ConsoleUI console = new cli.ConsoleUI(game, fichierPieces == null);
            console.start();
        }
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

    private static void afficherResultatChargement(ChargementPiecesResultat resultat) {
        System.out.println(resultat.getPiecesAjoutees() + " piece(s) personnalisee(s) ajoutee(s).");
        for (String erreur : resultat.getErreurs()) {
            System.out.println("- " + erreur);
        }
    }
}
