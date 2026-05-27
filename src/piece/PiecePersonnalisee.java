package piece;

import plateau.Grille;

/**
 * Piece chargee depuis un fichier JSON.
 *
 * <p>Son comportement est decrit par {@link ReglesDeplacement}, ce qui permet
 * d'ajouter des pieces comme le lion, le bus ou le minotaure sans creer de
 * nouvelle classe Java dediee.</p>
 */
public class PiecePersonnalisee extends Piece {
    private final String nom;
    private final String symbole;
    private final String image;
    private final ReglesDeplacement regles;

    /**
     * Cree une piece personnalisee.
     *
     * @param nom nom affiche de la piece
     * @param symbole symbole texte ou Unicode
     * @param x colonne initiale
     * @param y ligne initiale
     * @param couleur couleur de la piece
     * @param regles regles dynamiques de deplacement
     */
    public PiecePersonnalisee(String nom, String symbole, int x, int y, Couleur couleur, ReglesDeplacement regles) {
        this(nom, symbole, null, x, y, couleur, regles);
    }

    /**
     * Cree une piece personnalisee avec une image optionnelle.
     *
     * @param nom nom affiche de la piece
     * @param symbole symbole texte ou Unicode utilise en secours
     * @param image chemin de l'image, ou {@code null} si aucun fichier n'est defini
     * @param x colonne initiale
     * @param y ligne initiale
     * @param couleur couleur de la piece
     * @param regles regles dynamiques de deplacement
     */
    public PiecePersonnalisee(String nom, String symbole, String image, int x, int y, Couleur couleur, ReglesDeplacement regles) {
        super(x, y, couleur);
        this.nom = nom;
        this.symbole = symbole;
        this.image = image;
        this.regles = regles;
    }

    /**
     * Renvoie le nom.
     *
     * @return nom affiche de la piece
     */
    public String getNom() {
        return nom;
    }

    /**
     * Renvoie les regles dynamiques.
     *
     * @return regles dynamiques associees a la piece
     */
    public ReglesDeplacement getRegles() {
        return regles;
    }

    /**
     * Renvoie le chemin de l'image associee.
     *
     * @return chemin de l'image, ou {@code null} si la piece utilise son symbole
     */
    public String getImage() {
        return image;
    }

    @Override
    public boolean isValidMove(int newX, int newY, Grille grille) {
        if (!grille.isInside(newX, newY)) return false;
        if (!isDestinationValid(newX, newY, grille)) return false;

        int deltaX = Math.abs(newX - x);
        int deltaY = Math.abs(newY - y);
        if (deltaX == 0 && deltaY == 0) return false;

        boolean ligneDroite = deltaX == 0 || deltaY == 0;
        boolean diagonale = deltaX == deltaY;
        boolean cavalier = (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
        boolean deplacementValide = false;

        if (ligneDroite && regles.getDistanceMaxLigne() > 0) {
            int distance = Math.max(deltaX, deltaY);
            deplacementValide = distance <= regles.getDistanceMaxLigne();
        }

        if (diagonale && regles.getDistanceMaxDiagonale() > 0) {
            deplacementValide = deplacementValide || deltaX <= regles.getDistanceMaxDiagonale();
        }

        boolean utiliseSautCavalier = cavalier && regles.deplacementCavalier();
        if (utiliseSautCavalier) {
            deplacementValide = true;
        }

        if (!deplacementValide) return false;
        if (utiliseSautCavalier) return true;
        if (regles.ecraseLigne() && ligneDroite) return isPathCrushable(newX, newY, grille);
        return regles.peutSauter() || isPathClear(newX, newY, grille);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() {
        return symbole;
    }

    private boolean isPathCrushable(int endX, int endY, Grille grille) {
        int stepX = Integer.compare(endX, x);
        int stepY = Integer.compare(endY, y);

        int currX = x + stepX;
        int currY = y + stepY;
        while (currX != endX || currY != endY) {
            Piece piece = grille.getPiece(currX, currY);
            if (piece != null && piece.getCouleur() == couleur) return false;
            if (piece instanceof Roi) return false;
            currX += stepX;
            currY += stepY;
        }
        return true;
    }
}
