package piece;
import plateau.Grille;

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
     * @param couleur couleur de la tour
     */
    public Tour(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if (x != newX && y != newY) return false;
        if (!isPathClear(newX, newY, grille)) return false;
        return isDestinationValid(newX, newY, grille);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? ROOK_WHITE : ROOK_BLACK)); 
    }
}
