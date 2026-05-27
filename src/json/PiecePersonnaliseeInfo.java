package json;

/**
 * Informations de previsualisation d'une piece personnalisee avant son
 * chargement effectif sur le plateau.
 */
public class PiecePersonnaliseeInfo {
    private final String nom;
    private final String symbole;
    private final String image;
    private final String couleur;
    private final int x;
    private final int y;
    private final String description;

    /**
     * Cree une entree de catalogue de piece personnalisee.
     *
     * @param nom nom de la piece
     * @param symbole symbole affiche
     * @param image chemin de l'image, ou {@code null}
     * @param couleur couleur declaree dans le JSON
     * @param x colonne initiale
     * @param y ligne initiale
     * @param description description des capacites
     */
    public PiecePersonnaliseeInfo(String nom, String symbole, String image, String couleur, int x, int y, String description) {
        this.nom = nom;
        this.symbole = symbole;
        this.image = image;
        this.couleur = couleur;
        this.x = x;
        this.y = y;
        this.description = description;
    }

    /**
     * Renvoie le nom.
     *
     * @return nom de la piece
     */
    public String getNom() { return nom; }
    /**
     * Renvoie le symbole.
     *
     * @return symbole affiche
     */
    public String getSymbole() { return symbole; }
    /**
     * Renvoie le chemin de l'image.
     *
     * @return chemin de l'image, ou {@code null} si aucune image n'est definie
     */
    public String getImage() { return image; }
    /**
     * Renvoie la couleur declaree.
     *
     * @return couleur declaree
     */
    public String getCouleur() { return couleur; }
    /**
     * Renvoie la colonne initiale.
     *
     * @return colonne initiale
     */
    public int getX() { return x; }
    /**
     * Renvoie la ligne initiale.
     *
     * @return ligne initiale
     */
    public int getY() { return y; }
    /**
     * Renvoie la description.
     *
     * @return description des capacites
     */
    public String getDescription() { return description; }

    /**
     * Renvoie la coordonnee initiale.
     *
     * @return coordonnee echiqueenne de la position initiale
     */
    public String getCoordonnee() {
        return String.valueOf((char) ('A' + x)) + (y + 1);
    }

    @Override
    public String toString() {
        return symbole + " " + nom + " (" + couleur + ", " + getCoordonnee() + ") - " + description;
    }
}
