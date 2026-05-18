package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChargementPiecesResultat {
    private int piecesAjoutees;
    private final List<String> erreurs = new ArrayList<>();

    public void incrementerPiecesAjoutees() {
        piecesAjoutees++;
    }

    public void ajouterErreur(String erreur) {
        erreurs.add(erreur);
    }

    public int getPiecesAjoutees() {
        return piecesAjoutees;
    }

    public List<String> getErreurs() {
        return Collections.unmodifiableList(erreurs);
    }

    public boolean hasErreurs() {
        return !erreurs.isEmpty();
    }
}
