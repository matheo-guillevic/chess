package engine;

/**
 * Decrit un coup par ses coordonnees de depart et d'arrivee.
 */
public class Coup {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    /**
     * Cree un coup.
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     */
    public Coup(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Renvoie la colonne de depart.
     *
     * @return colonne de depart
     */
    public int getStartX() { return startX; }
    /**
     * Renvoie la ligne de depart.
     *
     * @return ligne de depart
     */
    public int getStartY() { return startY; }
    /**
     * Renvoie la colonne d'arrivee.
     *
     * @return colonne d'arrivee
     */
    public int getEndX() { return endX; }
    /**
     * Renvoie la ligne d'arrivee.
     *
     * @return ligne d'arrivee
     */
    public int getEndY() { return endY; }

    /**
     * @return representation lisible du coup, par exemple {@code e2 e4}
     */
    @Override
    public String toString() {
        return coordonnee(startX, startY) + " " + coordonnee(endX, endY);
    }

    private String coordonnee(int x, int y) {
        return String.valueOf((char) ('a' + x)) + (y + 1);
    }
}
