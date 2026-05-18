import engine.ChargementPiecesResultat;
import engine.Coup;
import engine.Game;
import piece.Couleur;
import piece.Piece;
import piece.PiecePersonnalisee;
import piece.Pion;
import piece.Reine;
import piece.Roi;
import plateau.Grille;

import java.nio.file.Path;
import java.util.Optional;

public class ChessEngineTest {
    private int tests;
    private int passed;

    public static void main(String[] args) {
        ChessEngineTest suite = new ChessEngineTest();
        suite.run();
    }

    private void run() {
        testInitialBoard();
        testBasicPawnMove();
        testClassicCapture();
        testCheckBlocksUnrelatedMove();
        testCastling();
        testQueensideCastling();
        testEnPassant();
        testEnPassantOnlyImmediate();
        testPromotion();
        testBlackPromotion();
        testCustomJsonPieces();
        testAutomaticPlayer();

        System.out.println(passed + "/" + tests + " tests reussis.");
        if (passed != tests) {
            System.exit(1);
        }
    }

    private void testInitialBoard() {
        Game game = new Game();

        assertTrue("position initiale : roi blanc en e1", game.getGrille().getPiece(4, 0) instanceof Roi);
        assertTrue("position initiale : pion blanc en a2", game.getGrille().getPiece(0, 1) instanceof Pion);
        assertEquals("position initiale : trait aux blancs", Couleur.BLANC, game.getCurrentTurn());
    }

    private void testBasicPawnMove() {
        Game game = new Game();

        assertTrue("e2 e4 est legal", game.tryMove(4, 1, 4, 3));
        assertTrue("le pion blanc arrive en e4", game.getGrille().getPiece(4, 3) instanceof Pion);
        assertEquals("le trait passe aux noirs", Couleur.NOIR, game.getCurrentTurn());
    }

    private void testClassicCapture() {
        Game game = new Game();

        play(game, "e2 e4");
        play(game, "d7 d5");

        assertTrue("capture classique e4xd5", game.tryMove(4, 3, 3, 4));
        assertTrue("le pion blanc occupe d5 apres capture", game.getGrille().getPiece(3, 4) instanceof Pion);
        assertEquals("la piece capturee etait noire", Couleur.BLANC, game.getGrille().getPiece(3, 4).getCouleur());
    }

    private void testCheckBlocksUnrelatedMove() {
        Game game = new Game();

        play(game, "e2 e3");
        play(game, "d7 d5");
        play(game, "f1 b5");

        assertTrue("les noirs sont en echec", game.isKingInCheck(Couleur.NOIR));
        assertFalse("un coup qui ne pare pas l'echec est refuse", game.tryMove(0, 6, 0, 5));
    }

    private void testCastling() {
        Game game = new Game();

        play(game, "e2 e4");
        play(game, "e7 e5");
        play(game, "g1 f3");
        play(game, "b8 c6");
        play(game, "f1 e2");
        play(game, "g8 f6");

        assertTrue("petit roque blanc legal", game.tryMove(4, 0, 6, 0));
        assertTrue("roi blanc en g1 apres roque", game.getGrille().getPiece(6, 0) instanceof Roi);
        assertTrue("tour blanche en f1 apres roque", game.getGrille().getPiece(5, 0) != null);
    }

    private void testQueensideCastling() {
        Game game = new Game();
        Grille grille = game.getGrille();
        clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new piece.Tour(0, 0, Couleur.BLANC), 0, 0);
        grille.setPiece(new Roi(4, 7, Couleur.NOIR), 4, 7);

        assertTrue("grand roque blanc legal", game.tryMove(4, 0, 2, 0));
        assertTrue("roi blanc en c1 apres grand roque", grille.getPiece(2, 0) instanceof Roi);
        assertTrue("tour blanche en d1 apres grand roque", grille.getPiece(3, 0) instanceof piece.Tour);
    }

    private void testEnPassant() {
        Game game = new Game();

        play(game, "e2 e4");
        play(game, "a7 a6");
        play(game, "e4 e5");
        play(game, "d7 d5");

        assertTrue("prise en passant blanche legal", game.tryMove(4, 4, 3, 5));
        assertTrue("pion blanc arrive en d6", game.getGrille().getPiece(3, 5) instanceof Pion);
        assertNull("pion noir capture en d5", game.getGrille().getPiece(3, 4));
    }

    private void testEnPassantOnlyImmediate() {
        Game game = new Game();

        play(game, "e2 e4");
        play(game, "a7 a6");
        play(game, "e4 e5");
        play(game, "d7 d5");
        play(game, "h2 h3");
        play(game, "a6 a5");

        assertFalse("prise en passant refusee si elle n'est pas immediate", game.tryMove(4, 4, 3, 5));
    }

    private void testPromotion() {
        Game game = new Game();
        Grille grille = game.getGrille();
        clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(7, 7, Couleur.NOIR), 7, 7);
        grille.setPiece(new Pion(0, 6, Couleur.BLANC), 0, 6);

        assertTrue("promotion du pion blanc en a8", game.tryMove(0, 6, 0, 7));
        assertTrue("le pion promu devient une reine", grille.getPiece(0, 7) instanceof Reine);
    }

    private void testBlackPromotion() {
        Game game = new Game();
        Grille grille = game.getGrille();
        clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(0, 7, Couleur.NOIR), 0, 7);
        grille.setPiece(new Pion(7, 1, Couleur.NOIR), 7, 1);

        play(game, "e1 e2");
        assertTrue("promotion du pion noir en h1", game.tryMove(7, 1, 7, 0));
        assertTrue("le pion noir promu devient une reine", grille.getPiece(7, 0) instanceof Reine);
        assertEquals("la reine promue est noire", Couleur.NOIR, grille.getPiece(7, 0).getCouleur());
    }

    private void testCustomJsonPieces() {
        Game game = new Game();
        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(Path.of("pieces_perso.json"));

        assertEquals("deux pieces personnalisees chargees", 2, resultat.getPiecesAjoutees());
        assertFalse("aucune erreur de chargement JSON", resultat.hasErreurs());
        assertTrue("piece personnalisee blanche en d3", game.getGrille().getPiece(3, 2) instanceof PiecePersonnalisee);
        assertTrue("piece personnalisee noire en d6", game.getGrille().getPiece(3, 5) instanceof PiecePersonnalisee);
    }

    private void testAutomaticPlayer() {
        Game game = new Game();
        Optional<Coup> coup = game.jouerCoupAutomatique();

        assertTrue("l'IA choisit un coup au depart", coup.isPresent());
        assertEquals("apres le coup automatique blanc, trait aux noirs", Couleur.NOIR, game.getCurrentTurn());
    }

    private void play(Game game, String move) {
        int startX = move.charAt(0) - 'a';
        int startY = move.charAt(1) - '1';
        int endX = move.charAt(3) - 'a';
        int endY = move.charAt(4) - '1';
        assertTrue("coup attendu legal : " + move, game.tryMove(startX, startY, endX, endY));
    }

    private void clearBoard(Grille grille) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                grille.setPiece(null, x, y);
            }
        }
    }

    private void assertTrue(String name, boolean condition) {
        tests++;
        if (condition) {
            passed++;
            System.out.println("[OK] " + name);
        } else {
            System.out.println("[FAIL] " + name);
        }
    }

    private void assertFalse(String name, boolean condition) {
        assertTrue(name, !condition);
    }

    private void assertNull(String name, Object value) {
        assertTrue(name, value == null);
    }

    private void assertEquals(String name, Object expected, Object actual) {
        assertTrue(name, expected == null ? actual == null : expected.equals(actual));
    }
}
