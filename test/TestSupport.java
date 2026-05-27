import engine.Game;
import plateau.Grid;

/**
 * Utilitaires partages par les tests manuels du projet.
 */
public class TestSupport {
    private int tests;
    private int passed;

    /**
     * Joue un coup attendu comme legal.
     *
     * @param game partie cible
     * @param move coup au format {@code e2 e4}
     */
    public void play(Game game, String move) {
        int startX = move.charAt(0) - 'a';
        int startY = move.charAt(1) - '1';
        int endX = move.charAt(3) - 'a';
        int endY = move.charAt(4) - '1';
        assertTrue("coup attendu legal : " + move, game.tryMove(startX, startY, endX, endY));
    }

    /**
     * Vide totalement un plateau.
     *
     * @param grid plateau a vider
     */
    public void clearBoard(Grid grid) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                grid.setPiece(null, x, y);
            }
        }
    }

    /**
     * Verifie qu'une condition est vraie.
     *
     * @param name libelle du test
     * @param condition condition attendue
     */
    public void assertTrue(String name, boolean condition) {
        tests++;
        if (condition) {
            passed++;
            System.out.println("[OK] " + name);
        } else {
            System.out.println("[FAIL] " + name);
        }
    }

    /**
     * Verifie qu'une condition est fausse.
     *
     * @param name libelle du test
     * @param condition condition a inverser
     */
    public void assertFalse(String name, boolean condition) {
        assertTrue(name, !condition);
    }

    /**
     * Verifie qu'une valeur est nulle.
     *
     * @param name libelle du test
     * @param value valeur testee
     */
    public void assertNull(String name, Object value) {
        assertTrue(name, value == null);
    }

    /**
     * Verifie l'egalite de deux valeurs.
     *
     * @param name libelle du test
     * @param expected valeur attendue
     * @param actual valeur obtenue
     */
    public void assertEquals(String name, Object expected, Object actual) {
        assertTrue(name, expected == null ? actual == null : expected.equals(actual));
    }

    /**
     * @return nombre total d'assertions executees
     */
    public int getTests() {
        return tests;
    }

    /**
     * @return nombre d'assertions reussies
     */
    public int getPassed() {
        return passed;
    }
}
