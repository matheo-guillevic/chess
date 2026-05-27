package piece;
import plateau.Grid;

/**
 * Piece de type tour.
 *
 * <p>Elle se deplace horizontalement ou verticalement sans traverser les
 * pieces.</p>
 */
public class Tour extends Piece {
    /**
     * Cree une tour.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param color couleur de la tour
     */
    public Tour(int x, int y, Color color) { super(x, y, color); }

    @Override
    public boolean isValidMove(int newX, int newY, Grid grid) {
        if (x != newX && y != newY) return false;
        if (!isPathClear(newX, newY, grid)) return false;
        return isDestinationValid(newX, newY, grid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() { 
        return new String(Character.toChars(color == Color.BLANC ? ROOK_WHITE : ROOK_BLACK));
    }
}
