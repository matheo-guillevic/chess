import javax.swing.SwingUtilities;
import gui.ChessGUI;
// import cli.ConsoleUI;

import engine.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();

        // Pour lancer en mode console :
        // ConsoleUI console = new ConsoleUI(game);
        // console.start();

        // Par défaut, lance le mode graphique
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI(game);
            gui.setVisible(true);
        });
    }
}