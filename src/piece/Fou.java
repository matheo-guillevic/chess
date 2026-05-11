package piece;
import plateau.Grille;

public class Fou extends Piece {
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
