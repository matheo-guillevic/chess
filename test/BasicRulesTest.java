import engine.Game;
import piece.Couleur;
import piece.Pion;
import piece.Roi;

public class BasicRulesTest implements TestSuite {
    @Override
    public void run(TestSupport support) {
        testInitialBoard(support);
        testBasicPawnMove(support);
        testClassicCapture(support);
        testCheckBlocksUnrelatedMove(support);
    }

    private void testInitialBoard(TestSupport support) {
        Game game = new Game();

        support.assertTrue("position initiale : roi blanc en e1", game.getGrille().getPiece(4, 0) instanceof Roi);
        support.assertTrue("position initiale : pion blanc en a2", game.getGrille().getPiece(0, 1) instanceof Pion);
        support.assertEquals("position initiale : trait aux blancs", Couleur.BLANC, game.getCurrentTurn());
    }

    private void testBasicPawnMove(TestSupport support) {
        Game game = new Game();

        support.assertTrue("e2 e4 est legal", game.tryMove(4, 1, 4, 3));
        support.assertTrue("le pion blanc arrive en e4", game.getGrille().getPiece(4, 3) instanceof Pion);
        support.assertEquals("le trait passe aux noirs", Couleur.NOIR, game.getCurrentTurn());
    }

    private void testClassicCapture(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e4");
        support.play(game, "d7 d5");

        support.assertTrue("capture classique e4xd5", game.tryMove(4, 3, 3, 4));
        support.assertTrue("le pion blanc occupe d5 apres capture", game.getGrille().getPiece(3, 4) instanceof Pion);
        support.assertEquals("la piece capturee etait noire", Couleur.BLANC, game.getGrille().getPiece(3, 4).getCouleur());
    }

    private void testCheckBlocksUnrelatedMove(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e3");
        support.play(game, "d7 d5");
        support.play(game, "f1 b5");

        support.assertTrue("les noirs sont en echec", game.isKingInCheck(Couleur.NOIR));
        support.assertFalse("un coup qui ne pare pas l'echec est refuse", game.tryMove(0, 6, 0, 5));
    }
}
