import engine.Game;
import plateau.Grille;

public class TestSupport {
    private int tests;
    private int passed;

    public void play(Game game, String move) {
        int startX = move.charAt(0) - 'a';
        int startY = move.charAt(1) - '1';
        int endX = move.charAt(3) - 'a';
        int endY = move.charAt(4) - '1';
        assertTrue("coup attendu legal : " + move, game.tryMove(startX, startY, endX, endY));
    }

    public void clearBoard(Grille grille) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                grille.setPiece(null, x, y);
            }
        }
    }

    public void assertTrue(String name, boolean condition) {
        tests++;
        if (condition) {
            passed++;
            System.out.println("[OK] " + name);
        } else {
            System.out.println("[FAIL] " + name);
        }
    }

    public void assertFalse(String name, boolean condition) {
        assertTrue(name, !condition);
    }

    public void assertNull(String name, Object value) {
        assertTrue(name, value == null);
    }

    public void assertEquals(String name, Object expected, Object actual) {
        assertTrue(name, expected == null ? actual == null : expected.equals(actual));
    }

    public int getTests() {
        return tests;
    }

    public int getPassed() {
        return passed;
    }
}
