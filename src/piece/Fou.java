package piece;
import plateau.Grille;

/**
 * Piece de type fou, deplacable en diagonale sans franchir d'obstacle.
 */
public class Fou extends Piece {
    /**
     * Cree un fou.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param couleur couleur du fou
     */
    public Fou(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if (Math.abs(newX - x) != Math.abs(newY - y)) return false;
        if (!isPathClear(newX, newY, grille)) return false;
        return isDestinationValid(newX, newY, grille);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? BISHOP_WHITE : BISHOP_BLACK)); 
    }
}
