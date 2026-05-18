package piece;
import plateau.Grille;

/**
 * Piece de type pion.
 *
 * <p>Le pion avance d'une case, peut avancer de deux cases depuis sa ligne de
 * depart et capture en diagonale. Les regles speciales comme la prise en
 * passant et la promotion sont appliquees dans le moteur de partie.</p>
 */
public class Pion extends Piece {
    /**
     * Cree un pion.
     *
     * @param x colonne initiale
     * @param y ligne initiale
     * @param couleur couleur du pion
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() { 
        return new String(Character.toChars(couleur == Couleur.BLANC ? PAWN_WHITE : PAWN_BLACK)); 
    }
}
