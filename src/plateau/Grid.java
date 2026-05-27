package plateau;

import piece.Piece;

/**
 * Representation de l'echiquier sous forme de matrice 8x8.
 *
 * <p>Les coordonnees internes utilisent {@code x} pour les colonnes A-H et
 * {@code y} pour les lignes 1-8, toutes deux indexees de 0 a 7.</p>
 */
public class Grid {
    private final Piece[][] board;

    /**
     * Cree une grille vide.
     */
    public Grid(){
        this.board = new Piece[8][8];
    }

    /**
     * Place une piece sur une case et synchronise sa position interne.
     *
     * @param piece piece a placer, ou {@code null} pour vider la case
     * @param x colonne cible
     * @param y ligne cible
     */
    public void setPiece(Piece piece, int x, int y) {
        if (piece != null) {
            piece.setX(x);
            piece.setY(y);
        }
        board[x][y] = piece;
    }

    /**
     * Recupere la piece d'une case.
     *
     * @param x colonne demandee
     * @param y ligne demandee
     * @return piece presente, ou {@code null} si la case est vide ou hors plateau
     */
    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return board[x][y];
    }

    /**
     * Indique si des coordonnees appartiennent au plateau.
     *
     * @param x colonne a tester
     * @param y ligne a tester
     * @return {@code true} si la case existe
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    /**
     * Indique si une case existe et ne contient aucune piece.
     *
     * @param x colonne a tester
     * @param y ligne a tester
     * @return {@code true} si la case est vide
     */
    public boolean isEmpty(int x, int y) {
        return isInside(x, y) && board[x][y] == null;
    }

    /**
     * Deplace une piece sans valider les regles de jeu.
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     */
    public void movePiece(int startX, int startY, int endX, int endY) {
        Piece piece = getPiece(startX, startY);
        setPiece(null, startX, startY);
        setPiece(piece, endX, endY);
    }

    /**
     * Affiche l'echiquier dans le terminal avec coordonnees.
     */
    public void afficher() {
        System.out.println();
        for (int y = 7; y >= 0; y--) { 
            System.out.print((y + 1) + "|");
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x, y);
                if (piece == null) {
                    System.out.print(" |");
                } else {
                    System.out.print(piece.getSymbol() + "|");
                }
            }
            System.out.println();
        }
        System.out.println("  A B C D E F G H\n");
    }
}
