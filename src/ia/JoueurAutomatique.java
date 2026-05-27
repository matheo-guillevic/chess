package ia;

import engine.Coup;
import engine.Game;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Joueur automatique utilise pour faire jouer l'ordinateur.
 *
 * <p>Cette classe porte la logique d'un adversaire IA. Elle choisit un coup
 * avec une recherche MinMax alpha-beta, puis applique ce coup sur la partie
 * fournie lorsque {@link #jouer(Game)} est utilise.</p>
 */
public class JoueurAutomatique {
    private static final int PROFONDEUR_RECHERCHE = 2;

    /** Recherche strategique utilisee pour choisir le meilleur coup. */
    private final RechercheMinMax recherche = new RechercheMinMax();
    /** Generateur utilise pour choisir un coup de secours parmi les coups legaux. */
    private final Random random = new Random();

    /**
     * Fait jouer automatiquement le joueur courant.
     *
     * @param game partie sur laquelle appliquer le coup
     * @return coup joue, ou {@link Optional#empty()} si aucun coup legal n'existe
     */
    public Optional<Coup> jouer(Game game) {
        Optional<Coup> coup = choisirCoup(game);
        coup.ifPresent(c -> game.tryMove(c.getStartX(), c.getStartY(), c.getEndX(), c.getEndY()));
        return coup;
    }

    /**
     * Choisit un coup pour le joueur dont c'est le tour dans la partie fournie.
     *
     * @param game partie analysee
     * @return coup choisi, ou {@link Optional#empty()} si aucun coup legal
     *         n'existe
     */
    public Optional<Coup> choisirCoup(Game game) {
        Optional<Coup> meilleurCoup = recherche.choisirMeilleurCoup(game, PROFONDEUR_RECHERCHE);
        if (meilleurCoup.isPresent()) {
            return meilleurCoup;
        }

        List<Coup> coups = game.getCoupsValides(game.getCurrentTurn());
        if (coups.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(coups.get(random.nextInt(coups.size())));
    }
}
