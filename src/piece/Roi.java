package piece;
import plateau.Grille;

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
     * @param couleur couleur du roi
     */
    public Roi(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if (Math.abs(newX - x) > 1 || Math.abs(newY - y) > 1) return false;
        return isDestinationValid(newX, newY, grille);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? KING_WHITE : KING_BLACK)); 
    }
}
