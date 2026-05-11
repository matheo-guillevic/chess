package piece;
import plateau.Grille;

public class Pion extends Piece {
    public Pion(int x, int y, Couleur couleur) { super(x, y, couleur); }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        int dir = (couleur == Couleur.BLANC) ? 1 : -1;
        int startY = (couleur == Couleur.BLANC) ? 1 : 6;

        // Avancer d'une case
        if (newX == x && newY == y + dir && grille.getPiece(newX, newY) == null) {
            return true;
        }
        // Avancer de deux cases (premier mouvement)
        if (newX == x && newY == y + 2 * dir && y == startY && grille.getPiece(newX, y + dir) == null && grille.getPiece(newX, newY) == null) {
            return true;
        }
        // Manger en diagonale
        if (Math.abs(newX - x) == 1 && newY == y + dir) {
            Piece target = grille.getPiece(newX, newY);
            return target != null && target.getCouleur() != this.couleur;
        }
        return false;
    }

    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? PAWN_WHITE : PAWN_BLACK)); 
    }
}
