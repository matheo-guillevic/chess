package engine;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Joueur automatique utilise pour le mode joueur contre ordinateur.
 *
 * <p>Il tente d'abord de choisir un coup via le MinMax du moteur, puis bascule
 * sur un choix aleatoire parmi les coups valides si aucun meilleur coup n'est
 * disponible.</p>
 */
public class JoueurAutomatique {
    private final Random random = new Random();

    /**
     * Cree un joueur automatique.
     */
    public JoueurAutomatique() {
    }

    /**
     * Choisit un coup pour le joueur courant.
     *
     * @param game partie en cours
     * @return coup choisi, ou {@link Optional#empty()} si aucun coup n'existe
     */
    public Optional<Coup> choisirCoup(Game game) {
        Optional<Coup> meilleurCoup = game.choisirMeilleurCoup(2);
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
