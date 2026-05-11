package piece;
import plateau.Grille;

public class Roi extends Piece {
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
