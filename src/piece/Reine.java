package piece;
import plateau.Grid;

/**
 * Piece de type reine combinant les deplacements de la tour et du fou.
 */
public class Reine extends Piece {
    /**
     * Cree une reine.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param color couleur de la reine
     */
    public Reine(int x, int y, Color color) { super(x, y, color); }

    @Override
    public boolean isValidMove(int newX, int newY, Grid grid) {
        if (x != newX && y != newY && Math.abs(newX - x) != Math.abs(newY - y)) return false;
        if (!isPathClear(newX, newY, grid)) return false;
        return isDestinationValid(newX, newY, grid);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(color == Color.BLANC ? QUEEN_WHITE : QUEEN_BLACK));
    }
}
