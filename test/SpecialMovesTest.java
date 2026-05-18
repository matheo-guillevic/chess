import engine.Game;
import piece.Couleur;
import piece.Pion;
import piece.Reine;
import piece.Roi;
import plateau.Grille;

public class SpecialMovesTest implements TestSuite {
    @Override
    public void run(TestSupport support) {
        testCastling(support);
        testQueensideCastling(support);
        testEnPassant(support);
        testEnPassantOnlyImmediate(support);
        testPromotion(support);
        testPromotionChoice(support);
        testBlackPromotion(support);
    }

    private void testCastling(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e4");
        support.play(game, "e7 e5");
        support.play(game, "g1 f3");
        support.play(game, "b8 c6");
        support.play(game, "f1 e2");
        support.play(game, "g8 f6");

        support.assertTrue("petit roque blanc legal", game.tryMove(4, 0, 6, 0));
        support.assertTrue("roi blanc en g1 apres roque", game.getGrille().getPiece(6, 0) instanceof Roi);
        support.assertTrue("tour blanche en f1 apres roque", game.getGrille().getPiece(5, 0) != null);
    }

    private void testQueensideCastling(TestSupport support) {
        Game game = new Game();
        Grille grille = game.getGrille();
        support.clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new piece.Tour(0, 0, Couleur.BLANC), 0, 0);
        grille.setPiece(new Roi(4, 7, Couleur.NOIR), 4, 7);

        support.assertTrue("grand roque blanc legal", game.tryMove(4, 0, 2, 0));
        support.assertTrue("roi blanc en c1 apres grand roque", grille.getPiece(2, 0) instanceof Roi);
        support.assertTrue("tour blanche en d1 apres grand roque", grille.getPiece(3, 0) instanceof piece.Tour);
    }

    private void testEnPassant(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e4");
        support.play(game, "a7 a6");
        support.play(game, "e4 e5");
        support.play(game, "d7 d5");

        support.assertTrue("prise en passant blanche legal", game.tryMove(4, 4, 3, 5));
        support.assertTrue("pion blanc arrive en d6", game.getGrille().getPiece(3, 5) instanceof Pion);
        support.assertNull("pion noir capture en d5", game.getGrille().getPiece(3, 4));
    }

    private void testEnPassantOnlyImmediate(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e4");
        support.play(game, "a7 a6");
        support.play(game, "e4 e5");
        support.play(game, "d7 d5");
        support.play(game, "h2 h3");
        support.play(game, "a6 a5");

        support.assertFalse("prise en passant refusee si elle n'est pas immediate", game.tryMove(4, 4, 3, 5));
    }

    private void testPromotion(TestSupport support) {
        Game game = new Game();
        Grille grille = game.getGrille();
        support.clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(7, 7, Couleur.NOIR), 7, 7);
        grille.setPiece(new Pion(0, 6, Couleur.BLANC), 0, 6);

        support.assertTrue("promotion du pion blanc en a8", game.tryMove(0, 6, 0, 7));
        support.assertTrue("le pion promu devient une reine", grille.getPiece(0, 7) instanceof Reine);
    }

    private void testPromotionChoice(TestSupport support) {
        Game game = new Game();
        Grille grille = game.getGrille();
        support.clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(7, 7, Couleur.NOIR), 7, 7);
        grille.setPiece(new Pion(1, 6, Couleur.BLANC), 1, 6);

        support.assertTrue("promotion choisie en cavalier", game.tryMove(1, 6, 1, 7, "cavalier"));
        support.assertTrue("le pion promu devient un cavalier", grille.getPiece(1, 7) instanceof piece.Cavalier);
    }

    private void testBlackPromotion(TestSupport support) {
        Game game = new Game();
        Grille grille = game.getGrille();
        support.clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(0, 7, Couleur.NOIR), 0, 7);
        grille.setPiece(new Pion(7, 1, Couleur.NOIR), 7, 1);

        support.play(game, "e1 e2");
        support.assertTrue("promotion du pion noir en h1", game.tryMove(7, 1, 7, 0));
        support.assertTrue("le pion noir promu devient une reine", grille.getPiece(7, 0) instanceof Reine);
        support.assertEquals("la reine promue est noire", Couleur.NOIR, grille.getPiece(7, 0).getCouleur());
    }
}
