package piece;

import plateau.Grille;

public abstract class Piece {

    public static final int ROOK_WHITE = 0x2656; 
    public static final int ROOK_BLACK = 0x265C;
    public static final int KNIGHT_WHITE = 0x2658;
    public static final int KNIGHT_BLACK = 0x265E;
    public static final int BISHOP_WHITE = 0x2657;
    public static final int BISHOP_BLACK = 0x265D;
    public static final int PAWN_WHITE = 0x2659;
    public static final int PAWN_BLACK = 0x265F;
    public static final int QUEEN_WHITE = 0x2655;
    public static final int QUEEN_BLACK = 0x265B;
    public static final int KING_WHITE = 0x2654;
    public static final int KING_BLACK = 0x265A;

    protected int x;
    protected int y;
    protected final Couleur couleur;

    public Piece(int x, int y, Couleur couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Couleur getCouleur() { return couleur; }

    public abstract boolean isValidMove(int newX, int newY, Grille grille);
    public abstract String getSymbol();

    /**
     * Helper pour vérifier si le chemin est libre (utilisé par Tour, Fou, Reine).
     */
    protected boolean isPathClear(int endX, int endY, Grille grille) {
        int stepX = Integer.compare(endX, x);
        int stepY = Integer.compare(endY, y);
        
        int currX = x + stepX;
        int currY = y + stepY;
        while (currX != endX || currY != endY) {
            if (grille.getPiece(currX, currY) != null) return false;
            currX += stepX;
            currY += stepY;
        }
        return true;
    }
    
    /**
     * Helper pour vérifier que la case d'arrivée ne contient pas une pièce alliée.
     */
    protected boolean isDestinationValid(int endX, int endY, Grille grille) {
        Piece target = grille.getPiece(endX, endY);
        return target == null || target.getCouleur() != this.couleur;
    }
}
