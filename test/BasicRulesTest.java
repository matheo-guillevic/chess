import engine.Game;
import piece.Color;
import piece.Pion;
import piece.Roi;

/**
 * Tests des regles de base du moteur : position initiale, mouvement, capture
 * et refus d'un coup laissant l'echec.
 */
public class BasicRulesTest implements TestSuite {
    /**
     * Execute les tests des regles de base.
     *
     * @param support utilitaires de test
     */
    @Override
    public void run(TestSupport support) {
        testInitialBoard(support);
        testBasicPawnMove(support);
        testClassicCapture(support);
        testCheckBlocksUnrelatedMove(support);
    }

    private void testInitialBoard(TestSupport support) {
        Game game = new Game();

        support.assertTrue("position initiale : roi blanc en e1", game.getGrid().getPiece(4, 0) instanceof Roi);
        support.assertTrue("position initiale : pion blanc en a2", game.getGrid().getPiece(0, 1) instanceof Pion);
        support.assertEquals("position initiale : trait aux blancs", Color.BLANC, game.getCurrentTurn());
    }

    private void testBasicPawnMove(TestSupport support) {
        Game game = new Game();

        support.assertTrue("e2 e4 est legal", game.tryMove(4, 1, 4, 3));
        support.assertTrue("le pion blanc arrive en e4", game.getGrid().getPiece(4, 3) instanceof Pion);
        support.assertEquals("le trait passe aux noirs", Color.NOIR, game.getCurrentTurn());
    }

    private void testClassicCapture(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e4");
        support.play(game, "d7 d5");

        support.assertTrue("capture classique e4xd5", game.tryMove(4, 3, 3, 4));
        support.assertTrue("le pion blanc occupe d5 apres capture", game.getGrid().getPiece(3, 4) instanceof Pion);
        support.assertEquals("la piece capturee etait noire", Color.BLANC, game.getGrid().getPiece(3, 4).getCouleur());
    }

    private void testCheckBlocksUnrelatedMove(TestSupport support) {
        Game game = new Game();

        support.play(game, "e2 e3");
        support.play(game, "d7 d5");
        support.play(game, "f1 b5");

        support.assertTrue("les noirs sont en echec", game.isKingInCheck(Color.NOIR));
        support.assertFalse("un coup qui ne pare pas l'echec est refuse", game.tryMove(0, 6, 0, 5));
    }
}
