package engine;

import piece.*;
import plateau.Grille;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {
    private final Grille grille;
    private Couleur currentTurn;
    private boolean isFinished;
    private Couleur winner;
    private Piece pionVulnerableEnPassant;
    private int caseEnPassantX = -1;
    private int caseEnPassantY = -1;

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

    public ChargementPiecesResultat chargerPiecesPersonnalisees(Path fichier) {
        return new ChargeurPiecesPersonnalisees().charger(fichier, grille);
    }

    public void initPiece() {
        grille.setPiece(new Tour(0, 0, Couleur.BLANC), 0, 0);
        grille.setPiece(new Cavalier(1, 0, Couleur.BLANC), 1, 0);
        grille.setPiece(new Fou(2, 0, Couleur.BLANC), 2, 0);
        grille.setPiece(new Reine(3, 0, Couleur.BLANC), 3, 0);
        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Fou(5, 0, Couleur.BLANC), 5, 0);
        grille.setPiece(new Cavalier(6, 0, Couleur.BLANC), 6, 0);
        grille.setPiece(new Tour(7, 0, Couleur.BLANC), 7, 0);
        for (int i = 0; i < 8; i++) grille.setPiece(new Pion(i, 1, Couleur.BLANC), i, 1);

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

    public boolean tryMove(int startX, int startY, int endX, int endY) {
        return tryMove(startX, startY, endX, endY, "reine");
    }

    public boolean tryMove(int startX, int startY, int endX, int endY, String promotion) {
        if (isFinished) return false;

        Piece piece = grille.getPiece(startX, startY);
        if (piece == null || piece.getCouleur() != currentTurn) return false;
        if (!isLegalMove(startX, startY, endX, endY, currentTurn)) return false;

        executerDeplacement(startX, startY, endX, endY, promotion);
        currentTurn = adversaire(currentTurn);
        mettreAJourFinDePartie();
        return true;
    }

    public Optional<Coup> jouerCoupAutomatique() {
        Optional<Coup> coup = new JoueurAutomatique().choisirCoup(this);
        coup.ifPresent(c -> tryMove(c.getStartX(), c.getStartY(), c.getEndX(), c.getEndY()));
        return coup;
    }

    public Optional<Coup> choisirMeilleurCoup(int profondeur) {
        List<Coup> coups = getCoupsValides(currentTurn);
        if (coups.isEmpty()) return Optional.empty();

        Couleur joueur = currentTurn;
        Coup meilleurCoup = coups.get(0);
        int meilleurScore = Integer.MIN_VALUE;

        for (Coup coup : coups) {
            EtatSimulation etat = executerSimulation(coup.getStartX(), coup.getStartY(), coup.getEndX(), coup.getEndY());
            currentTurn = adversaire(currentTurn);
            int score = alphaBeta(profondeur - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, joueur);
            currentTurn = adversaire(currentTurn);
            restaurerSimulation(etat);

            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurCoup = coup;
            }
        }

        return Optional.of(meilleurCoup);
    }

    public List<Coup> getCoupsValides(Couleur couleur) {
        List<Coup> coups = new ArrayList<>();
        for (int startY = 0; startY < 8; startY++) {
            for (int startX = 0; startX < 8; startX++) {
                Piece piece = grille.getPiece(startX, startY);
                if (piece == null || piece.getCouleur() != couleur) continue;

                for (int endY = 0; endY < 8; endY++) {
                    for (int endX = 0; endX < 8; endX++) {
                        if (isLegalMove(startX, startY, endX, endY, couleur)) {
                            coups.add(new Coup(startX, startY, endX, endY));
                        }
                    }
                }
            }
        }
        return coups;
    }

    public boolean isKingInCheck(Couleur couleur) {
        Piece roi = trouverRoi(couleur);
        if (roi == null) return false;
        return isSquareAttacked(roi.getX(), roi.getY(), adversaire(couleur));
    }

    private boolean isLegalMove(int startX, int startY, int endX, int endY, Couleur couleur) {
        if (!grille.isInside(startX, startY) || !grille.isInside(endX, endY)) return false;

        Piece piece = grille.getPiece(startX, startY);
        if (piece == null || piece.getCouleur() != couleur) return false;

        Piece destination = grille.getPiece(endX, endY);
        if (destination instanceof Roi) return false;
        if (!isPseudoMoveValid(piece, startX, startY, endX, endY)) return false;

        EtatSimulation etat = executerSimulation(startX, startY, endX, endY);
        boolean roiEnEchec = isKingInCheck(couleur);
        restaurerSimulation(etat);
        return !roiEnEchec;
    }

    private boolean isPseudoMoveValid(Piece piece, int startX, int startY, int endX, int endY) {
        if (piece.isValidMove(endX, endY, grille)) return true;
        return isRoqueValide(piece, startX, startY, endX, endY)
                || isPriseEnPassantValide(piece, startX, startY, endX, endY);
    }

    private boolean isRoqueValide(Piece piece, int startX, int startY, int endX, int endY) {
        if (!(piece instanceof Roi) || piece.aDejaBouge()) return false;
        if (startY != endY || startX != 4 || Math.abs(endX - startX) != 2) return false;
        if (isKingInCheck(piece.getCouleur())) return false;

        int direction = Integer.compare(endX, startX);
        int rookX = direction > 0 ? 7 : 0;
        Piece tour = grille.getPiece(rookX, startY);
        if (!(tour instanceof Tour) || tour.getCouleur() != piece.getCouleur() || tour.aDejaBouge()) return false;

        int firstEmptyX = direction > 0 ? startX + 1 : rookX + 1;
        int lastEmptyX = direction > 0 ? rookX - 1 : startX - 1;
        for (int x = firstEmptyX; x <= lastEmptyX; x++) {
            if (grille.getPiece(x, startY) != null) return false;
        }

        Couleur adversaire = adversaire(piece.getCouleur());
        return !isSquareAttacked(startX + direction, startY, adversaire)
                && !isSquareAttacked(endX, endY, adversaire);
    }

    private boolean isPriseEnPassantValide(Piece piece, int startX, int startY, int endX, int endY) {
        if (!(piece instanceof Pion)) return false;
        if (pionVulnerableEnPassant == null) return false;
        int direction = piece.getCouleur() == Couleur.BLANC ? 1 : -1;
        return endX == caseEnPassantX
                && endY == caseEnPassantY
                && Math.abs(endX - startX) == 1
                && endY == startY + direction
                && grille.getPiece(endX, endY) == null
                && pionVulnerableEnPassant.getX() == endX
                && pionVulnerableEnPassant.getY() == startY
                && pionVulnerableEnPassant.getCouleur() != piece.getCouleur();
    }

    private EtatSimulation executerSimulation(int startX, int startY, int endX, int endY) {
        EtatSimulation etat = new EtatSimulation(startX, startY, endX, endY);
        Piece piece = grille.getPiece(startX, startY);
        etat.piece = piece;
        etat.capture = grille.getPiece(endX, endY);
        etat.pieceAvaitBouge = piece.aDejaBouge();

        if (isPriseEnPassantValide(piece, startX, startY, endX, endY)) {
            etat.captureEnPassantX = endX;
            etat.captureEnPassantY = startY;
            etat.captureEnPassant = grille.getPiece(endX, startY);
            grille.setPiece(null, endX, startY);
        }

        if (isRoqueValide(piece, startX, startY, endX, endY)) {
            int direction = Integer.compare(endX, startX);
            etat.rookStartX = direction > 0 ? 7 : 0;
            etat.rookEndX = startX + direction;
            etat.rook = grille.getPiece(etat.rookStartX, startY);
            etat.rookAvaitBouge = etat.rook.aDejaBouge();
            grille.movePiece(etat.rookStartX, startY, etat.rookEndX, startY);
        }

        grille.movePiece(startX, startY, endX, endY);
        return etat;
    }

    private void restaurerSimulation(EtatSimulation etat) {
        grille.setPiece(etat.piece, etat.startX, etat.startY);
        grille.setPiece(etat.capture, etat.endX, etat.endY);
        etat.piece.setADejaBouge(etat.pieceAvaitBouge);

        if (etat.captureEnPassant != null) {
            grille.setPiece(etat.captureEnPassant, etat.captureEnPassantX, etat.captureEnPassantY);
        }

        if (etat.rook != null) {
            grille.setPiece(null, etat.rookEndX, etat.startY);
            grille.setPiece(etat.rook, etat.rookStartX, etat.startY);
            etat.rook.setADejaBouge(etat.rookAvaitBouge);
        }
    }

    private void executerDeplacement(int startX, int startY, int endX, int endY, String promotion) {
        Piece piece = grille.getPiece(startX, startY);
        boolean priseEnPassant = isPriseEnPassantValide(piece, startX, startY, endX, endY);
        boolean roque = isRoqueValide(piece, startX, startY, endX, endY);

        if (priseEnPassant) {
            grille.setPiece(null, endX, startY);
        }

        if (roque) {
            int direction = Integer.compare(endX, startX);
            int rookStartX = direction > 0 ? 7 : 0;
            int rookEndX = startX + direction;
            Piece tour = grille.getPiece(rookStartX, startY);
            grille.movePiece(rookStartX, startY, rookEndX, startY);
            tour.setADejaBouge(true);
        }

        grille.movePiece(startX, startY, endX, endY);
        piece.setADejaBouge(true);

        pionVulnerableEnPassant = null;
        caseEnPassantX = -1;
        caseEnPassantY = -1;
        if (piece instanceof Pion && Math.abs(endY - startY) == 2) {
            pionVulnerableEnPassant = piece;
            caseEnPassantX = startX;
            caseEnPassantY = (startY + endY) / 2;
        }

        if (piece instanceof Pion && (endY == 0 || endY == 7)) {
            Piece piecePromue = creerPiecePromotion(promotion, endX, endY, piece.getCouleur());
            grille.setPiece(piecePromue, endX, endY);
            piecePromue.setADejaBouge(true);
        }
    }

    private Piece creerPiecePromotion(String promotion, int x, int y, Couleur couleur) {
        String choix = promotion == null ? "reine" : promotion.trim().toLowerCase();
        switch (choix) {
            case "tour":
                return new Tour(x, y, couleur);
            case "fou":
                return new Fou(x, y, couleur);
            case "cavalier":
                return new Cavalier(x, y, couleur);
            case "reine":
            default:
                return new Reine(x, y, couleur);
        }
    }

    private void mettreAJourFinDePartie() {
        if (!getCoupsValides(currentTurn).isEmpty()) return;
        isFinished = true;
        winner = isKingInCheck(currentTurn) ? adversaire(currentTurn) : null;
    }

    private int alphaBeta(int profondeur, int alpha, int beta, Couleur joueur) {
        List<Coup> coups = getCoupsValides(currentTurn);
        if (profondeur == 0 || coups.isEmpty()) {
            if (coups.isEmpty() && isKingInCheck(currentTurn)) {
                return currentTurn == joueur ? -100_000 : 100_000;
            }
            return evaluerPosition(joueur);
        }

        boolean maximise = currentTurn == joueur;
        if (maximise) {
            int valeur = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                EtatSimulation etat = executerSimulation(coup.getStartX(), coup.getStartY(), coup.getEndX(), coup.getEndY());
                currentTurn = adversaire(currentTurn);
                valeur = Math.max(valeur, alphaBeta(profondeur - 1, alpha, beta, joueur));
                currentTurn = adversaire(currentTurn);
                restaurerSimulation(etat);
                alpha = Math.max(alpha, valeur);
                if (alpha >= beta) break;
            }
            return valeur;
        }

        int valeur = Integer.MAX_VALUE;
        for (Coup coup : coups) {
            EtatSimulation etat = executerSimulation(coup.getStartX(), coup.getStartY(), coup.getEndX(), coup.getEndY());
            currentTurn = adversaire(currentTurn);
            valeur = Math.min(valeur, alphaBeta(profondeur - 1, alpha, beta, joueur));
            currentTurn = adversaire(currentTurn);
            restaurerSimulation(etat);
            beta = Math.min(beta, valeur);
            if (alpha >= beta) break;
        }
        return valeur;
    }

    private int evaluerPosition(Couleur joueur) {
        int score = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = grille.getPiece(x, y);
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

    private boolean isSquareAttacked(int x, int y, Couleur attaquant) {
        for (int startY = 0; startY < 8; startY++) {
            for (int startX = 0; startX < 8; startX++) {
                Piece piece = grille.getPiece(startX, startY);
                if (piece != null && piece.getCouleur() == attaquant && attaqueCase(piece, x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean attaqueCase(Piece piece, int x, int y) {
        int deltaX = Math.abs(x - piece.getX());
        int deltaY = Math.abs(y - piece.getY());

        if (piece instanceof Pion) {
            int direction = piece.getCouleur() == Couleur.BLANC ? 1 : -1;
            return deltaX == 1 && y == piece.getY() + direction;
        }

        if (piece instanceof Roi) {
            return deltaX <= 1 && deltaY <= 1 && (deltaX != 0 || deltaY != 0);
        }

        return piece.isValidMove(x, y, grille);
    }

    private Piece trouverRoi(Couleur couleur) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = grille.getPiece(x, y);
                if (piece instanceof Roi && piece.getCouleur() == couleur) {
                    return piece;
                }
            }
        }
        return null;
    }

    private Couleur adversaire(Couleur couleur) {
        return couleur == Couleur.BLANC ? Couleur.NOIR : Couleur.BLANC;
    }

    private static class EtatSimulation {
        private final int startX;
        private final int startY;
        private final int endX;
        private final int endY;
        private Piece piece;
        private Piece capture;
        private boolean pieceAvaitBouge;
        private Piece captureEnPassant;
        private int captureEnPassantX = -1;
        private int captureEnPassantY = -1;
        private Piece rook;
        private int rookStartX = -1;
        private int rookEndX = -1;
        private boolean rookAvaitBouge;

        private EtatSimulation(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }
}
