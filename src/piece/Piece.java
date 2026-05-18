package piece;

import plateau.Grille;

/**
 * Classe de base de toutes les pieces posees sur l'echiquier.
 *
 * <p>Elle centralise la position, la couleur, l'etat "a deja bouge" et les
 * constantes Unicode des pieces classiques. Les sous-classes implementent leur
 * propre validation de mouvement via {@link #isValidMove(int, int, Grille)}.</p>
 */
public abstract class Piece {

    /** Code Unicode de la tour blanche. */
    public static final int ROOK_WHITE = 0x2656; 
    /** Code Unicode de la tour noire. */
    public static final int ROOK_BLACK = 0x265C;
    /** Code Unicode du cavalier blanc. */
    public static final int KNIGHT_WHITE = 0x2658;
    /** Code Unicode du cavalier noir. */
    public static final int KNIGHT_BLACK = 0x265E;
    /** Code Unicode du fou blanc. */
    public static final int BISHOP_WHITE = 0x2657;
    /** Code Unicode du fou noir. */
    public static final int BISHOP_BLACK = 0x265D;
    /** Code Unicode du pion blanc. */
    public static final int PAWN_WHITE = 0x2659;
    /** Code Unicode du pion noir. */
    public static final int PAWN_BLACK = 0x265F;
    /** Code Unicode de la reine blanche. */
    public static final int QUEEN_WHITE = 0x2655;
    /** Code Unicode de la reine noire. */
    public static final int QUEEN_BLACK = 0x265B;
    /** Code Unicode du roi blanc. */
    public static final int KING_WHITE = 0x2654;
    /** Code Unicode du roi noir. */
    public static final int KING_BLACK = 0x265A;

    /** Colonne actuelle de la piece. */
    protected int x;
    /** Ligne actuelle de la piece. */
    protected int y;
    /** Couleur de la piece. */
    protected final Couleur couleur;
    private boolean aDejaBouge;

    /**
     * Cree une piece a une position donnee.
     *
     * @param x colonne de depart, entre 0 et 7
     * @param y ligne de depart, entre 0 et 7
     * @param couleur couleur de la piece
     */
    public Piece(int x, int y, Couleur couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
    }

    /**
     * Renvoie la colonne actuelle.
     *
     * @return colonne actuelle de la piece
     */
    public int getX() { return x; }
    /**
     * Renvoie la ligne actuelle.
     *
     * @return ligne actuelle de la piece
     */
    public int getY() { return y; }
    /**
     * Modifie la colonne actuelle.
     *
     * @param x nouvelle colonne de la piece
     */
    public void setX(int x) { this.x = x; }
    /**
     * Modifie la ligne actuelle.
     *
     * @param y nouvelle ligne de la piece
     */
    public void setY(int y) { this.y = y; }
    /**
     * Renvoie la couleur.
     *
     * @return couleur de la piece
     */
    public Couleur getCouleur() { return couleur; }
    /**
     * Indique si la piece a deja bouge.
     *
     * @return {@code true} si la piece a deja ete deplacee
     */
    public boolean aDejaBouge() { return aDejaBouge; }
    /**
     * Modifie l'etat de deplacement.
     *
     * @param aDejaBouge nouvel etat de deplacement de la piece
     */
    public void setADejaBouge(boolean aDejaBouge) { this.aDejaBouge = aDejaBouge; }

    /**
     * Verifie si le mouvement brut de la piece est autorise.
     *
     * <p>Cette methode ne gere pas les contraintes globales de partie comme
     * l'echec au roi, le roque ou la prise en passant. Ces regles sont gerees
     * par le moteur {@code Game}.</p>
     *
     * @param newX colonne d'arrivee
     * @param newY ligne d'arrivee
     * @param grille plateau courant
     * @return {@code true} si la piece peut se deplacer vers la case indiquee
     */
    public abstract boolean isValidMove(int newX, int newY, Grille grille);

    /**
     * Renvoie le symbole affiche.
     *
     * @return symbole affiche pour cette piece
     */
    public abstract String getSymbol();

    /**
     * Verifie si le chemin entre la position courante et la destination est libre.
     *
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @param grille plateau courant
     * @return {@code true} si aucune piece ne bloque le trajet
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
     * Verifie que la case d'arrivee ne contient pas une piece alliee.
     *
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @param grille plateau courant
     * @return {@code true} si la destination est vide ou occupee par un ennemi
     */
    protected boolean isDestinationValid(int endX, int endY, Grille grille) {
        Piece target = grille.getPiece(endX, endY);
        return target == null || target.getCouleur() != this.couleur;
    }
}
