package engine;

public class PiecePersonnaliseeInfo {
    private final String nom;
    private final String symbole;
    private final String couleur;
    private final int x;
    private final int y;
    private final String description;

    public PiecePersonnaliseeInfo(String nom, String symbole, String couleur, int x, int y, String description) {
        this.nom = nom;
        this.symbole = symbole;
        this.couleur = couleur;
        this.x = x;
        this.y = y;
        this.description = description;
    }

    public String getNom() { return nom; }
    public String getSymbole() { return symbole; }
    public String getCouleur() { return couleur; }
    public int getX() { return x; }
    public int getY() { return y; }
    public String getDescription() { return description; }

    public String getCoordonnee() {
        return String.valueOf((char) ('A' + x)) + (y + 1);
    }

    @Override
    public String toString() {
        return symbole + " " + nom + " (" + couleur + ", " + getCoordonnee() + ") - " + description;
    }
}
