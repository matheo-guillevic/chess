package engine;

import json.ChargementPiecesResultat;
import json.ChargeurPiecesPersonnalisees;
import json.PiecePersonnaliseeInfo;
import piece.*;
import plateau.Grille;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Moteur principal d'une partie d'echecs.
 *
 * <p>Cette classe centralise l'etat de la partie, le joueur courant, la
 * validation des coups legaux, les regles speciales et les pieces
 * personnalisees.</p>
 */
public class Game {
    /** Plateau contenant les pieces de la partie. */
    private final Grille grille;
    /** Couleur du joueur qui doit jouer. */
    private Couleur currentTurn;
    /** Indique si la partie est terminee par mat ou pat. */
    private boolean isFinished;
    /** Couleur gagnante, ou {@code null} si la partie est nulle ou en cours. */
    private Couleur winner;
    /** Pion pouvant etre capture en passant au prochain coup uniquement. */
    private Piece pionVulnerableEnPassant;
    /** Colonne de la case d'arrivee autorisee pour une prise en passant. */
    private int caseEnPassantX = -1;
    /** Ligne de la case d'arrivee autorisee pour une prise en passant. */
    private int caseEnPassantY = -1;

    /**
     * Cree une nouvelle partie avec la position initiale standard.
     */
    public Game() {
        this(true);
    }

    private Game(boolean initialiserPieces) {
        this.grille = new Grille();
        this.currentTurn = Couleur.BLANC;
        this.isFinished = false;
        if (initialiserPieces) {
            initPiece();
        }
    }

    /**
     * Renvoie le plateau courant.
     *
     * @return grille courante de la partie
     */
    public Grille getGrille() { return grille; }
    /**
     * Renvoie le joueur actif.
     *
     * @return couleur dont c'est le tour
     */
    public Couleur getCurrentTurn() { return currentTurn; }
    /**
     * Indique si la partie est terminee.
     *
     * @return {@code true} si la partie est terminee
     */
    public boolean isFinished() { return isFinished; }
    /**
     * Renvoie le gagnant.
     *
     * @return couleur gagnante, ou {@code null} en cas de pat ou partie non terminee
     */
    public Couleur getWinner() { return winner; }

    /**
     * Cree une copie independante de la partie courante.
     *
     * <p>Cette copie sert notamment aux strategies de recherche qui doivent
     * tester des coups sans modifier la partie jouee.</p>
     *
     * @return nouvelle partie contenant le meme etat que la partie courante
     */
    public Game copier() {
        Game copie = new Game(false);
        copie.currentTurn = currentTurn;
        copie.isFinished = isFinished;
        copie.winner = winner;
        copie.caseEnPassantX = caseEnPassantX;
        copie.caseEnPassantY = caseEnPassantY;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = grille.getPiece(x, y);
                if (piece == null) continue;

                Piece pieceCopie = copierPiece(piece);
                copie.grille.setPiece(pieceCopie, x, y);
                if (piece == pionVulnerableEnPassant) {
                    copie.pionVulnerableEnPassant = pieceCopie;
                }
            }
        }
        return copie;
    }

    /**
     * Charge toutes les pieces personnalisees d'un fichier JSON.
     *
     * @param fichier chemin du fichier JSON
     * @return resultat du chargement
     */
    public ChargementPiecesResultat chargerPiecesPersonnalisees(Path fichier) {
        return new ChargeurPiecesPersonnalisees().charger(fichier, grille);
    }

    /**
     * Charge uniquement certaines pieces personnalisees d'un fichier JSON.
     *
     * @param fichier chemin du fichier JSON
     * @param nomsSelectionnes noms exacts des pieces a charger, ou ensemble vide pour tout charger
     * @return resultat du chargement
     */
    public ChargementPiecesResultat chargerPiecesPersonnalisees(Path fichier, Set<String> nomsSelectionnes) {
        return new ChargeurPiecesPersonnalisees().charger(fichier, grille, nomsSelectionnes);
    }

    /**
     * Lit le catalogue des pieces personnalisables sans les placer sur le plateau.
     *
     * @param fichier chemin du fichier JSON
     * @return liste des pieces disponibles pour previsualisation
     */
    public List<PiecePersonnaliseeInfo> lireCataloguePiecesPersonnalisees(Path fichier) {
        return new ChargeurPiecesPersonnalisees().lireCatalogue(fichier);
    }

    /**
     * Place les pieces classiques dans leur position initiale.
     */
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

    /**
     * Tente de jouer un coup en promouvant les pions en reine par defaut.
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @return {@code true} si le coup est legal et applique
     */
    public boolean tryMove(int startX, int startY, int endX, int endY) {
        return tryMove(startX, startY, endX, endY, "reine");
    }

    /**
     * Tente de jouer un coup avec choix explicite de promotion.
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @param promotion type de promotion : reine, tour, fou ou cavalier
     * @return {@code true} si le coup est legal et applique
     */
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

    /**
     * Genere tous les coups legaux d'une couleur.
     *
     * @param couleur couleur analysee
     * @return liste des coups autorises
     */
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

    /**
     * Indique si le roi d'une couleur est actuellement attaque.
     *
     * @param couleur couleur du roi a tester
     * @return {@code true} si le roi est en echec
     */
    public boolean isKingInCheck(Couleur couleur) {
        Piece roi = trouverRoi(couleur);
        if (roi == null) return false;
        return isSquareAttacked(roi.getX(), roi.getY(), adversaire(couleur));
    }

    /**
     * Verifie qu'un coup respecte les regles globales de la partie.
     *
     * <p>Cette validation complete la validation propre a chaque piece avec les
     * contraintes du moteur : limites du plateau, bonne couleur, interdiction de
     * capturer directement un roi, roque, prise en passant et interdiction de
     * laisser son propre roi en echec.</p>
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @param couleur couleur du joueur qui tente le coup
     * @return {@code true} si le coup est legal dans la position courante
     */
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

    /**
     * Verifie le mouvement brut d'une piece avant le test d'echec au roi.
     *
     * @param piece piece deplacee
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @return {@code true} si le mouvement est autorise par la piece ou par une
     *         regle speciale
     */
    private boolean isPseudoMoveValid(Piece piece, int startX, int startY, int endX, int endY) {
        if (piece.isValidMove(endX, endY, grille)) return true;
        return isRoqueValide(piece, startX, startY, endX, endY)
                || isPriseEnPassantValide(piece, startX, startY, endX, endY);
    }

    /**
     * Verifie si un deplacement correspond a un roque legal.
     *
     * @param piece piece deplacee, normalement un roi
     * @param startX colonne de depart du roi
     * @param startY ligne de depart du roi
     * @param endX colonne d'arrivee du roi
     * @param endY ligne d'arrivee du roi
     * @return {@code true} si le petit ou grand roque est autorise
     */
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

    /**
     * Verifie si un deplacement de pion correspond a une prise en passant.
     *
     * @param piece piece deplacee
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @return {@code true} si la prise en passant est possible immediatement
     */
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

    /**
     * Applique temporairement un coup pour tester ses consequences.
     *
     * <p>La simulation sauvegarde toutes les informations necessaires a la
     * restauration : piece deplacee, capture classique, prise en passant, roque
     * et pieces ecrasees par une piece personnalisee.</p>
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @return etat a fournir a {@link #restaurerSimulation(EtatSimulation)}
     */
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

        if (isEcrasementLigne(piece, startX, startY, endX, endY)) {
            supprimerPiecesEcrasees(startX, startY, endX, endY, etat.piecesEcrasees);
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

    /**
     * Annule une simulation et restaure exactement l'etat sauvegarde.
     *
     * @param etat etat produit par {@link #executerSimulation(int, int, int, int)}
     */
    private void restaurerSimulation(EtatSimulation etat) {
        grille.setPiece(etat.piece, etat.startX, etat.startY);
        grille.setPiece(etat.capture, etat.endX, etat.endY);
        etat.piece.setADejaBouge(etat.pieceAvaitBouge);

        if (etat.captureEnPassant != null) {
            grille.setPiece(etat.captureEnPassant, etat.captureEnPassantX, etat.captureEnPassantY);
        }

        for (PieceEcrasee pieceEcrasee : etat.piecesEcrasees) {
            grille.setPiece(pieceEcrasee.piece, pieceEcrasee.x, pieceEcrasee.y);
        }

        if (etat.rook != null) {
            grille.setPiece(null, etat.rookEndX, etat.startY);
            grille.setPiece(etat.rook, etat.rookStartX, etat.startY);
            etat.rook.setADejaBouge(etat.rookAvaitBouge);
        }
    }

    /**
     * Execute reellement un coup deja valide.
     *
     * <p>Cette methode applique les effets concrets du coup : capture en
     * passant, ecrasement de ligne, roque, mise a jour de l'etat "a deja bouge",
     * vulnerabilite a la prise en passant et promotion.</p>
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @param promotion type de piece souhaite pour une promotion
     */
    private void executerDeplacement(int startX, int startY, int endX, int endY, String promotion) {
        Piece piece = grille.getPiece(startX, startY);
        boolean priseEnPassant = isPriseEnPassantValide(piece, startX, startY, endX, endY);
        boolean roque = isRoqueValide(piece, startX, startY, endX, endY);

        if (priseEnPassant) {
            grille.setPiece(null, endX, startY);
        }

        if (isEcrasementLigne(piece, startX, startY, endX, endY)) {
            supprimerPiecesEcrasees(startX, startY, endX, endY, null);
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

    /**
     * Cree la piece issue d'une promotion de pion.
     *
     * @param promotion choix textuel de promotion
     * @param x colonne de la piece promue
     * @param y ligne de la piece promue
     * @param couleur couleur de la piece promue
     * @return nouvelle piece promue, une reine si le choix est absent ou inconnu
     */
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

    /**
     * Cree une copie d'une piece concrete.
     *
     * @param piece piece a copier
     * @return nouvelle instance equivalente a la piece fournie
     */
    private Piece copierPiece(Piece piece) {
        Piece copie;
        if (piece instanceof Pion) {
            copie = new Pion(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece instanceof Cavalier) {
            copie = new Cavalier(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece instanceof Fou) {
            copie = new Fou(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece instanceof Tour) {
            copie = new Tour(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece instanceof Reine) {
            copie = new Reine(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece instanceof Roi) {
            copie = new Roi(piece.getX(), piece.getY(), piece.getCouleur());
        } else if (piece instanceof PiecePersonnalisee) {
            PiecePersonnalisee personnalisee = (PiecePersonnalisee) piece;
            copie = new PiecePersonnalisee(
                    personnalisee.getNom(),
                    personnalisee.getSymbol(),
                    personnalisee.getImage(),
                    piece.getX(),
                    piece.getY(),
                    piece.getCouleur(),
                    personnalisee.getRegles()
            );
        } else {
            throw new IllegalArgumentException("Type de piece non pris en charge : " + piece.getClass().getName());
        }
        copie.setADejaBouge(piece.aDejaBouge());
        return copie;
    }

    /**
     * Indique si une piece personnalisee ecrase les pieces sur une ligne.
     *
     * @param piece piece deplacee
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @return {@code true} si le coup active la capacite d'ecrasement
     */
    private boolean isEcrasementLigne(Piece piece, int startX, int startY, int endX, int endY) {
        if (!(piece instanceof PiecePersonnalisee)) return false;
        PiecePersonnalisee piecePersonnalisee = (PiecePersonnalisee) piece;
        return piecePersonnalisee.getRegles().ecraseLigne() && (startX == endX || startY == endY);
    }

    /**
     * Supprime les pieces situees entre le depart et l'arrivee d'un ecrasement.
     *
     * @param startX colonne de depart
     * @param startY ligne de depart
     * @param endX colonne d'arrivee
     * @param endY ligne d'arrivee
     * @param sauvegarde liste recevant les pieces supprimees en simulation, ou
     *        {@code null} lors d'un vrai deplacement
     */
    private void supprimerPiecesEcrasees(int startX, int startY, int endX, int endY, List<PieceEcrasee> sauvegarde) {
        int stepX = Integer.compare(endX, startX);
        int stepY = Integer.compare(endY, startY);
        int currX = startX + stepX;
        int currY = startY + stepY;

        while (currX != endX || currY != endY) {
            Piece piece = grille.getPiece(currX, currY);
            if (piece != null) {
                if (sauvegarde != null) {
                    sauvegarde.add(new PieceEcrasee(piece, currX, currY));
                }
                grille.setPiece(null, currX, currY);
            }
            currX += stepX;
            currY += stepY;
        }
    }

    /**
     * Met a jour l'etat de fin de partie apres un coup joue.
     *
     * <p>Si le joueur courant n'a plus de coup legal, la partie se termine. Le
     * gagnant est l'adversaire en cas d'echec et mat, ou {@code null} en cas de
     * pat.</p>
     */
    private void mettreAJourFinDePartie() {
        if (!getCoupsValides(currentTurn).isEmpty()) return;
        isFinished = true;
        winner = isKingInCheck(currentTurn) ? adversaire(currentTurn) : null;
    }

    /**
     * Indique si une case est attaquee par au moins une piece d'une couleur.
     *
     * @param x colonne de la case testee
     * @param y ligne de la case testee
     * @param attaquant couleur des pieces attaquantes
     * @return {@code true} si la case est menacee
     */
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

    /**
     * Verifie si une piece attaque une case donnee.
     *
     * <p>Les pions et les rois sont traites a part pour raisonner en termes de
     * cases attaquees, independamment de certains details de deplacement.</p>
     *
     * @param piece piece attaquante potentielle
     * @param x colonne de la case testee
     * @param y ligne de la case testee
     * @return {@code true} si la piece attaque la case
     */
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

    /**
     * Recherche le roi d'une couleur sur le plateau.
     *
     * @param couleur couleur du roi recherche
     * @return piece roi trouvee, ou {@code null} si elle est absente
     */
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

    /**
     * Renvoie la couleur opposee.
     *
     * @param couleur couleur source
     * @return {@link Couleur#NOIR} pour les blancs, {@link Couleur#BLANC} pour
     *         les noirs
     */
    private Couleur adversaire(Couleur couleur) {
        return couleur == Couleur.BLANC ? Couleur.NOIR : Couleur.BLANC;
    }

    /**
     * Etat sauvegarde pendant une simulation de coup.
     *
     * <p>Cette structure permet de revenir a la position precedente apres un
     * test de legalite.</p>
     */
    private static class EtatSimulation {
        /** Colonne de depart du coup simule. */
        private final int startX;
        /** Ligne de depart du coup simule. */
        private final int startY;
        /** Colonne d'arrivee du coup simule. */
        private final int endX;
        /** Ligne d'arrivee du coup simule. */
        private final int endY;
        /** Piece deplacee pendant la simulation. */
        private Piece piece;
        /** Piece capturee sur la case d'arrivee, le cas echeant. */
        private Piece capture;
        /** Etat "a deja bouge" de la piece avant la simulation. */
        private boolean pieceAvaitBouge;
        /** Piece capturee par prise en passant, le cas echeant. */
        private Piece captureEnPassant;
        /** Colonne de la piece capturee en passant. */
        private int captureEnPassantX = -1;
        /** Ligne de la piece capturee en passant. */
        private int captureEnPassantY = -1;
        /** Pieces retirees par une capacite d'ecrasement pendant la simulation. */
        private final List<PieceEcrasee> piecesEcrasees = new ArrayList<>();
        /** Tour deplacee pendant un roque simule. */
        private Piece rook;
        /** Colonne de depart de la tour pendant un roque simule. */
        private int rookStartX = -1;
        /** Colonne d'arrivee de la tour pendant un roque simule. */
        private int rookEndX = -1;
        /** Etat "a deja bouge" de la tour avant le roque simule. */
        private boolean rookAvaitBouge;

        /**
         * Cree une sauvegarde pour un coup simule.
         *
         * @param startX colonne de depart
         * @param startY ligne de depart
         * @param endX colonne d'arrivee
         * @param endY ligne d'arrivee
         */
        private EtatSimulation(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    /**
     * Piece retiree temporairement par une capacite d'ecrasement.
     */
    private static class PieceEcrasee {
        /** Piece retiree du plateau. */
        private final Piece piece;
        /** Colonne ou se trouvait la piece. */
        private final int x;
        /** Ligne ou se trouvait la piece. */
        private final int y;

        /**
         * Memorise une piece ecrasee et sa position.
         *
         * @param piece piece retiree
         * @param x colonne d'origine
         * @param y ligne d'origine
         */
        private PieceEcrasee(Piece piece, int x, int y) {
            this.piece = piece;
            this.x = x;
            this.y = y;
        }
    }
}
