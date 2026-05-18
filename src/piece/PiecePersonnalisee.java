package piece;

import plateau.Grille;

public class PiecePersonnalisee extends Piece {
    private final String nom;
    private final String symbole;
    private final ReglesDeplacement regles;

    public PiecePersonnalisee(String nom, String symbole, int x, int y, Couleur couleur, ReglesDeplacement regles) {
        super(x, y, couleur);
        this.nom = nom;
        this.symbole = symbole;
        this.regles = regles;
    }

    public String getNom() {
        return nom;
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
        boolean deplacementValide = false;

        if (ligneDroite && regles.getDistanceMaxLigne() > 0) {
            int distance = Math.max(deltaX, deltaY);
            deplacementValide = distance <= regles.getDistanceMaxLigne();
        }

        if (diagonale && regles.getDistanceMaxDiagonale() > 0) {
            deplacementValide = deplacementValide || deltaX <= regles.getDistanceMaxDiagonale();
        }

        if (!deplacementValide) return false;
        return regles.peutSauter() || isPathClear(newX, newY, grille);
    }

    @Override
    public String getSymbol() {
        return symbole;
    }
}
