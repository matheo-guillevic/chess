package piece;
import plateau.Grille;

public class Tour extends Piece {
    public Tour(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if (x != newX && y != newY) return false;
        if (!isPathClear(newX, newY, grille)) return false;
        return isDestinationValid(newX, newY, grille);
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? ROOK_WHITE : ROOK_BLACK)); 
    }
}
