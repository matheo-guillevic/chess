import engine.ChargementPiecesResultat;
import engine.Game;
import piece.PiecePersonnalisee;

import java.nio.file.Path;

public class JsonPiecesTest implements TestSuite {
    @Override
    public void run(TestSupport support) {
        testCustomJsonPieces(support);
    }

    private void testCustomJsonPieces(TestSupport support) {
        Game game = new Game();
        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(Path.of("pieces_perso.json"));

        support.assertEquals("deux pieces personnalisees chargees", 2, resultat.getPiecesAjoutees());
        support.assertFalse("aucune erreur de chargement JSON", resultat.hasErreurs());
        support.assertTrue("piece personnalisee blanche en d3", game.getGrille().getPiece(3, 2) instanceof PiecePersonnalisee);
        support.assertTrue("piece personnalisee noire en d6", game.getGrille().getPiece(3, 5) instanceof PiecePersonnalisee);
    }
}
