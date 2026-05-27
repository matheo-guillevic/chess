package ia;

import engine.Game;
import piece.*;

/**
 * Evalue une position pour le joueur automatique.
 */
class EvaluateurPosition {
    /**
     * Calcule une evaluation materielle simple de la position.
     *
     * @param game partie a evaluer
     * @param joueur couleur dont le point de vue sert au calcul
     * @return score positif si la position favorise {@code joueur}, negatif
     *         sinon
     */
    int evaluer(Game game, Couleur joueur) {
        int score = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = game.getGrille().getPiece(x, y);
                if (piece == null) continue;
                int valeur = valeurPiece(piece);
                score += piece.getCouleur() == joueur ? valeur : -valeur;
            }
        }
        return score;
    }

    private int valeurPiece(Piece piece) {
        if (piece instanceof Pion) return 100;
        if (piece instanceof Cavalier || piece instanceof Fou) return 300;
        if (piece instanceof Tour) return 500;
        if (piece instanceof Reine) return 900;
        if (piece instanceof Roi) return 20_000;
        return 400;
    }
}
