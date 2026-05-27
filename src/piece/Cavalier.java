package piece;
import plateau.Grid;

/**
 * Piece de type cavalier.
 *
 * <p>Le cavalier se deplace en L et peut franchir les autres pieces.</p>
 */
public class Cavalier extends Piece {
    /**
     * Cree un cavalier.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param color couleur du cavalier
     */
    public Cavalier(int x, int y, Color color) { super(x, y, color); }

    @Override
    public boolean isValidMove(int newX, int newY, Grid grid) {
        if ((Math.abs(newX - x) == 2 && Math.abs(newY - y) == 1) || 
            (Math.abs(newX - x) == 1 && Math.abs(newY - y) == 2)) {
            return isDestinationValid(newX, newY, grid);
        }
        return false;
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(color == Color.BLANC ? KNIGHT_WHITE : KNIGHT_BLACK));
    }
}
