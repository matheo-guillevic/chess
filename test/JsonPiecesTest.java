import engine.ChargementPiecesResultat;
import engine.Game;
import piece.Couleur;
import piece.PiecePersonnalisee;
import piece.Pion;
import piece.ReglesDeplacement;
import piece.Roi;
import plateau.Grille;

import java.nio.file.Path;

public class JsonPiecesTest implements TestSuite {
    @Override
    public void run(TestSupport support) {
        testCustomJsonPieces(support);
        testBusCrushesLine(support);
        testMinotaurMoves(support);
    }

    private void testCustomJsonPieces(TestSupport support) {
        Game game = new Game();
        ChargementPiecesResultat resultat = game.chargerPiecesPersonnalisees(Path.of("pieces_perso.json"));

        support.assertEquals("six pieces personnalisees chargees", 6, resultat.getPiecesAjoutees());
        support.assertFalse("aucune erreur de chargement JSON", resultat.hasErreurs());
        support.assertTrue("piece personnalisee blanche en d3", game.getGrille().getPiece(3, 2) instanceof PiecePersonnalisee);
        support.assertTrue("piece personnalisee noire en d6", game.getGrille().getPiece(3, 5) instanceof PiecePersonnalisee);
        support.assertTrue("bus blanc en a3", game.getGrille().getPiece(0, 2) instanceof PiecePersonnalisee);
        support.assertTrue("minotaure blanc en h3", game.getGrille().getPiece(7, 2) instanceof PiecePersonnalisee);
    }

    private void testBusCrushesLine(TestSupport support) {
        Game game = new Game();
        Grille grille = game.getGrille();
        support.clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(7, 7, Couleur.NOIR), 7, 7);
        grille.setPiece(new PiecePersonnalisee(
                "Bus Blanc",
                "B",
                0,
                0,
                Couleur.BLANC,
                new ReglesDeplacement("Ecrase en ligne", 7, 0, false, true, false)
        ), 0, 0);
        grille.setPiece(new Pion(0, 2, Couleur.NOIR), 0, 2);
        grille.setPiece(new Pion(0, 3, Couleur.NOIR), 0, 3);

        support.assertTrue("le bus roule de a1 a a5", game.tryMove(0, 0, 0, 4));
        support.assertTrue("le bus arrive en a5", grille.getPiece(0, 4) instanceof PiecePersonnalisee);
        support.assertNull("le bus ecrase la piece en a3", grille.getPiece(0, 2));
        support.assertNull("le bus ecrase la piece en a4", grille.getPiece(0, 3));
    }

    private void testMinotaurMoves(TestSupport support) {
        Game game = new Game();
        Grille grille = game.getGrille();
        support.clearBoard(grille);

        grille.setPiece(new Roi(4, 0, Couleur.BLANC), 4, 0);
        grille.setPiece(new Roi(7, 7, Couleur.NOIR), 7, 7);
        grille.setPiece(new PiecePersonnalisee(
                "Minotaure Blanc",
                "M",
                3,
                3,
                Couleur.BLANC,
                new ReglesDeplacement("Charge et saute", 3, 1, false, false, true)
        ), 3, 3);

        support.assertTrue("le minotaure saute comme un cavalier", game.tryMove(3, 3, 5, 4));
    }
}
