package engine;

import piece.*;
import plateau.Grille;

public class Game {
    private final Grille grille;
    private Couleur currentTurn;
    private boolean isFinished;
    private Couleur winner;

    public Game() {
        this.grille = new Grille();
        this.currentTurn = Couleur.BLANC;
        this.isFinished = false;
        initPiece();
    }

    public Grille getGrille() { return grille; }
    public Couleur getCurrentTurn() { return currentTurn; }
    public boolean isFinished() { return isFinished; }
    public Couleur getWinner() { return winner; }

    public void initPiece() {
        // Pièces Blanches (y = 0 et 1)
        grille.setPiece(new Tour(0, 0, Couleur.BLANC), 0, 0);
        grille.setPiece(new Cavalier(1, 0, Couleur.BLANC), 1, 0);
        grille.setPiece(new Fou(2, 0, Couleur.BLANC), 2, 0);
        grille.setPiece(new Reine(3, 0, Couleur.BLANC), 3, 0);
        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Fou(5, 0, Couleur.BLANC), 5, 0);
        grille.setPiece(new Cavalier(6, 0, Couleur.BLANC), 6, 0);
        grille.setPiece(new Tour(7, 0, Couleur.BLANC), 7, 0);
        for (int i = 0; i < 8; i++) grille.setPiece(new Pion(i, 1, Couleur.BLANC), i, 1);

        // Pièces Noires (y = 7 et 6)
        grille.setPiece(new Tour(0, 7, Couleur.NOIR), 0, 7);
        grille.setPiece(new Cavalier(1, 7, Couleur.NOIR), 1, 7);
        grille.setPiece(new Fou(2, 7, Couleur.NOIR), 2, 7);
        grille.setPiece(new Reine(3, 7, Couleur.NOIR), 3, 7);
        grille.setPiece(new Roi(4, 7, Couleur.NOIR), 4, 7);
        grille.setPiece(new Fou(5, 7, Couleur.NOIR), 5, 7);
        grille.setPiece(new Cavalier(6, 7, Couleur.NOIR), 6, 7);
        grille.setPiece(new Tour(7, 7, Couleur.NOIR), 7, 7);
        for (int i = 0; i < 8; i++) grille.setPiece(new Pion(i, 6, Couleur.NOIR), i, 6);
    }

    /**
     * Tente un déplacement. Encapsule toute la logique de validation et de tour.
     * @return true si le coup est valide et effectué, false sinon.
     */
    public boolean tryMove(int startX, int startY, int endX, int endY) {
        if (isFinished) return false;

        Piece pieceToMove = grille.getPiece(startX, startY);
        // On ne peut bouger que si on a sélectionné une de ses propres pièces
        if (pieceToMove == null || pieceToMove.getCouleur() != currentTurn) return false;

        if (pieceToMove.isValidMove(endX, endY, grille)) {
            Piece captured = grille.getPiece(endX, endY);
            grille.movePiece(startX, startY, endX, endY);

            if (captured instanceof Roi) {
                isFinished = true;
                winner = currentTurn;
            } else {
                currentTurn = (currentTurn == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
            }
            return true;
        }
        return false;
    }
}
