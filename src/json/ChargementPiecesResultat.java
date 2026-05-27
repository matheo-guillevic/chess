package json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultat d'une tentative de chargement de pieces personnalisees.
 */
public class ChargementPiecesResultat {
    private int piecesAjoutees;
    private final List<String> erreurs = new ArrayList<>();

    /**
     * Cree un resultat vide.
     */
    public ChargementPiecesResultat() {
    }

    /**
     * Incremente le nombre de pieces ajoutees avec succes.
     */
    public void incrementerPiecesAjoutees() {
        piecesAjoutees++;
    }

    /**
     * Ajoute une erreur non bloquante rencontree pendant le chargement.
     *
     * @param erreur message explicatif
     */
    public void ajouterErreur(String erreur) {
        erreurs.add(erreur);
    }

    /**
     * Renvoie le nombre de pieces ajoutees.
     *
     * @return nombre de pieces effectivement posees sur le plateau
     */
    public int getPiecesAjoutees() {
        return piecesAjoutees;
    }

    /**
     * Renvoie les erreurs de chargement.
     *
     * @return liste immuable des erreurs rencontrees
     */
    public List<String> getErreurs() {
        return Collections.unmodifiableList(erreurs);
    }

    /**
     * Indique si des erreurs existent.
     *
     * @return {@code true} si au moins une erreur a ete signalee
     */
    public boolean hasErreurs() {
        return !erreurs.isEmpty();
    }
}
