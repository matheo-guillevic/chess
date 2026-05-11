package piece;
import plateau.Grille;

public class Reine extends Piece {
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
