package piece;
import plateau.Grid;

/**
 * Piece de type fou, deplacable en diagonale sans franchir d'obstacle.
 */
public class Fou extends Piece {
    /**
     * Cree un fou.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param color couleur du fou
     */
    public Fou(int x, int y, Color color) { super(x, y, color); }

    @Override
    public boolean isValidMove(int newX, int newY, Grid grid) {
        if (Math.abs(newX - x) != Math.abs(newY - y)) return false;
        if (!isPathClear(newX, newY, grid)) return false;
        return isDestinationValid(newX, newY, grid);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(color == Color.BLANC ? BISHOP_WHITE : BISHOP_BLACK));
    }
}
