package engine;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Joueur automatique utilise pour faire jouer l'ordinateur.
 *
 * <p>Cette classe ne modifie pas directement la partie : elle choisit seulement
 * un {@link Coup}. L'execution du coup reste geree par {@link Game}, notamment
 * par {@link Game#jouerCoupAutomatique()}.</p>
 *
 * <p>La strategie privilegie la recherche MinMax alpha-beta fournie par le
 * moteur. Si cette recherche ne renvoie aucun coup, le joueur automatique se
 * rabat sur un choix aleatoire parmi les coups legaux du joueur courant.</p>
 */
public class JoueurAutomatique {
    /** Generateur utilise pour choisir un coup de secours parmi les coups legaux. */
    private final Random random = new Random();

    /**
     * Cree un joueur automatique sans etat de partie propre.
     *
     * <p>Une meme instance peut etre reutilisee sur plusieurs parties, car la
     * position analysee est toujours fournie par le parametre {@link Game} de
     * {@link #choisirCoup(Game)}.</p>
     */
    public JoueurAutomatique() {
    }

    /**
     * Choisit un coup pour le joueur dont c'est le tour dans la partie fournie.
     *
     * <p>La methode demande d'abord au moteur de choisir le meilleur coup avec
     * une profondeur de recherche fixee a 2 demi-coups. Si aucun coup n'est
     * obtenu, elle recupere la liste des coups valides et en selectionne un au
     * hasard. Si cette liste est vide, aucun coup ne peut etre joue.</p>
     *
     * @param game partie en cours utilisee pour analyser le plateau et le trait
     * @return coup choisi, ou {@link Optional#empty()} si aucun coup legal
     *         n'existe
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
