import engine.Coup;
import engine.Game;
import ia.JoueurAutomatique;
import piece.Color;

import java.util.Optional;

/**
 * Tests du joueur automatique.
 */
public class AiTest implements TestSuite {
    /**
     * Execute les tests lies a l'IA.
     *
     * @param support utilitaires de test
     */
    @Override
    public void run(TestSupport support) {
        testAutomaticPlayer(support);
    }

    private void testAutomaticPlayer(TestSupport support) {
        Game game = new Game();
        Optional<Coup> coup = new JoueurAutomatique().jouer(game);

        support.assertTrue("l'IA choisit un coup au depart", coup.isPresent());
        support.assertEquals("apres le coup automatique blanc, trait aux noirs", Color.NOIR, game.getCurrentTurn());
    }
}
