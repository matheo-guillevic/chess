import engine.Game;
import json.ChargementPiecesResultat;
import piece.*;
import plateau.Grid;

import java.nio.file.Path;
import java.util.Set;

/**
 * Tests du chargement JSON et des pieces personnalisees.
 */
public class JsonPiecesTest implements TestSuite {
    private static final Path PIECES_PERSO = Path.of("resources/pieces_perso.json");

    /**
     * Execute les tests lies aux pieces personnalisees.
     *
     * @param support utilitaires de test
     */
    @Override
    public void run(TestSupport support) {
        testCustomJsonPieces(support);
        testSelectiveCustomJsonPieces(support);
        testBusCrushesLine(support);
        testMinotaurMoves(support);
    }

    private void testCustomJsonPieces(TestSupport support) {
        Game game = new Game();
        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(PIECES_PERSO);

        support.assertEquals("six pieces personnalisees chargees", 6, resultat.getPiecesAjoutees());
        support.assertFalse("aucune erreur de chargement JSON", resultat.hasErreurs());
        support.assertTrue("piece personnalisee blanche en d3", game.getGrid().getPiece(3, 2) instanceof PiecePersonnalisee);
        support.assertTrue("piece personnalisee noire en d6", game.getGrid().getPiece(3, 5) instanceof PiecePersonnalisee);
        support.assertTrue("bus blanc en a3", game.getGrid().getPiece(0, 2) instanceof PiecePersonnalisee);
        support.assertTrue("minotaure blanc en h3", game.getGrid().getPiece(7, 2) instanceof PiecePersonnalisee);
    }

    private void testSelectiveCustomJsonPieces(TestSupport support) {
        Game game = new Game();
        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(
                PIECES_PERSO,
                Set.of("Bus Blanc", "Bus Noir")
        );

        support.assertEquals("chargement selectif : deux bus ajoutes", 2, resultat.getPiecesAjoutees());
        support.assertTrue("bus blanc charge en a3", game.getGrid().getPiece(0, 2) instanceof PiecePersonnalisee);
        support.assertTrue("bus noir charge en a6", game.getGrid().getPiece(0, 5) instanceof PiecePersonnalisee);
        support.assertNull("lion blanc non charge en d3", game.getGrid().getPiece(3, 2));
        support.assertNull("minotaure blanc non charge en h3", game.getGrid().getPiece(7, 2));
    }

    private void testBusCrushesLine(TestSupport support) {
        Game game = new Game();
        Grid grid = game.getGrid();
        support.clearBoard(grid);

        grid.setPiece(new Roi(4, 0, Color.BLANC), 4, 0);
        grid.setPiece(new Roi(7, 7, Color.NOIR), 7, 7);
        grid.setPiece(new PiecePersonnalisee(
                "Bus Blanc",
                "B",
                0,
                0,
                Color.BLANC,
                new ReglesDeplacement("Ecrase en ligne", 7, 0, false, true, false)
        ), 0, 0);
        grid.setPiece(new Pion(0, 2, Color.NOIR), 0, 2);
        grid.setPiece(new Pion(0, 3, Color.NOIR), 0, 3);

        support.assertTrue("le bus roule de a1 a a5", game.tryMove(0, 0, 0, 4));
        support.assertTrue("le bus arrive en a5", grid.getPiece(0, 4) instanceof PiecePersonnalisee);
        support.assertNull("le bus ecrase la piece en a3", grid.getPiece(0, 2));
        support.assertNull("le bus ecrase la piece en a4", grid.getPiece(0, 3));
    }

    private void testMinotaurMoves(TestSupport support) {
        Game game = new Game();
        Grid grid = game.getGrid();
        support.clearBoard(grid);

        grid.setPiece(new Roi(4, 0, Color.BLANC), 4, 0);
        grid.setPiece(new Roi(7, 7, Color.NOIR), 7, 7);
        grid.setPiece(new PiecePersonnalisee(
                "Minotaure Blanc",
                "M",
                3,
                3,
                Color.BLANC,
                new ReglesDeplacement("Charge et saute", 3, 1, false, false, true)
        ), 3, 3);

        support.assertTrue("le minotaure saute comme un cavalier", game.tryMove(3, 3, 5, 4));
    }
}
