package engine;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class JoueurAutomatique {
    private final Random random = new Random();

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
