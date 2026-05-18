package piece;
import plateau.Grille;

/**
 * Piece de type reine combinant les deplacements de la tour et du fou.
 */
public class Reine extends Piece {
    /**
     * Cree une reine.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param couleur couleur de la reine
     */
    public Reine(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if (x != newX && y != newY && Math.abs(newX - x) != Math.abs(newY - y)) return false;
        if (!isPathClear(newX, newY, grille)) return false;
        return isDestinationValid(newX, newY, grille);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? QUEEN_WHITE : QUEEN_BLACK)); 
    }
}
