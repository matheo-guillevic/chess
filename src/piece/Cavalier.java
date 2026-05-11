package piece;
import plateau.Grille;

public class Cavalier extends Piece {
    public Cavalier(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if ((Math.abs(newX - x) == 2 && Math.abs(newY - y) == 1) || 
            (Math.abs(newX - x) == 1 && Math.abs(newY - y) == 2)) {
            return isDestinationValid(newX, newY, grille);
        }
        return false;
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? KNIGHT_WHITE : KNIGHT_BLACK)); 
    }
}
