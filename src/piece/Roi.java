package piece;
import plateau.Grid;

/**
 * Piece de type roi.
 *
 * <p>Le roi se deplace d'une case. Le moteur de partie ajoute ensuite les
 * contraintes d'echec, de mat et de roque.</p>
 */
public class Roi extends Piece {
    /**
     * Cree un roi.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param color couleur du roi
     */
    public Roi(int x, int y, Color color) { super(x, y, color); }

    @Override
    public boolean isValidMove(int newX, int newY, Grid grid) {
        if (Math.abs(newX - x) > 1 || Math.abs(newY - y) > 1) return false;
        return isDestinationValid(newX, newY, grid);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(color == Color.BLANC ? KING_WHITE : KING_BLACK));
    }
}
