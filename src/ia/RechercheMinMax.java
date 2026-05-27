package ia;

import engine.Coup;
import engine.Game;
import piece.Couleur;

import java.util.List;
import java.util.Optional;

/**
 * Recherche le meilleur coup avec l'algorithme MinMax et l'elagage alpha-beta.
 */
class RechercheMinMax {
    private final EvaluateurPosition evaluateur = new EvaluateurPosition();

    /**
     * Choisit le meilleur coup disponible pour le joueur courant.
     *
     * @param game partie a analyser
     * @param profondeur profondeur de recherche en demi-coups
     * @return meilleur coup trouve, ou {@link Optional#empty()} si aucun coup
     *         legal n'existe
     */
    Optional<Coup> choisirMeilleurCoup(Game game, int profondeur) {
        List<Coup> coups = game.getCoupsValides(game.getCurrentTurn());
        if (coups.isEmpty()) return Optional.empty();

        Couleur joueur = game.getCurrentTurn();
        Coup meilleurCoup = coups.get(0);
        int meilleurScore = Integer.MIN_VALUE;

        for (Coup coup : coups) {
            Game simulation = game.copier();
            simulation.tryMove(coup.getStartX(), coup.getStartY(), coup.getEndX(), coup.getEndY());
            int score = alphaBeta(simulation, profondeur - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, joueur);

            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurCoup = coup;
            }
        }

        return Optional.of(meilleurCoup);
    }

    private int alphaBeta(Game game, int profondeur, int alpha, int beta, Couleur joueur) {
        List<Coup> coups = game.getCoupsValides(game.getCurrentTurn());
        if (profondeur == 0 || coups.isEmpty()) {
            if (coups.isEmpty() && game.isKingInCheck(game.getCurrentTurn())) {
                return game.getCurrentTurn() == joueur ? -100_000 : 100_000;
            }
            return evaluateur.evaluer(game, joueur);
        }

        boolean maximise = game.getCurrentTurn() == joueur;
        if (maximise) {
            int valeur = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                Game simulation = game.copier();
                simulation.tryMove(coup.getStartX(), coup.getStartY(), coup.getEndX(), coup.getEndY());
                valeur = Math.max(valeur, alphaBeta(simulation, profondeur - 1, alpha, beta, joueur));
                alpha = Math.max(alpha, valeur);
                if (alpha >= beta) break;
            }
            return valeur;
        }

        int valeur = Integer.MAX_VALUE;
        for (Coup coup : coups) {
            Game simulation = game.copier();
            simulation.tryMove(coup.getStartX(), coup.getStartY(), coup.getEndX(), coup.getEndY());
            valeur = Math.min(valeur, alphaBeta(simulation, profondeur - 1, alpha, beta, joueur));
            beta = Math.min(beta, valeur);
            if (alpha >= beta) break;
        }
        return valeur;
    }
}
