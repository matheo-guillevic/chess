import javax.swing.SwingUtilities;
import gui.ChessGUI;
import engine.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();

        boolean useGui = args.length > 0 && args[0].equals("--gui");

        if (useGui) {
            // Lancement en mode graphique
            SwingUtilities.invokeLater(() -> {
                ChessGUI gui = new ChessGUI(game);
                gui.setVisible(true);
            });
        } else {
            // Lancement en mode console
            cli.ConsoleUI console = new cli.ConsoleUI(game);
            console.start();
        }
    }
}