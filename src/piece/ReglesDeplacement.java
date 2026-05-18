package piece;

/**
 * Ensemble des parametres de mouvement lus depuis le JSON des pieces
 * personnalisees.
 */
public class ReglesDeplacement {
    private String description;
    private int distanceMaxLigne;
    private int distanceMaxDiagonale;
    private boolean peutSauter;
    private boolean ecraseLigne;
    private boolean deplacementCavalier;

    /**
     * Cree des regles dynamiques sans capacites speciales avancees.
     *
     * @param description description lisible du fonctionnement
     * @param distanceMaxLigne distance maximale en ligne droite
     * @param distanceMaxDiagonale distance maximale en diagonale
     * @param peutSauter {@code true} si la piece ignore les obstacles
     */
    public ReglesDeplacement(String description, int distanceMaxLigne, int distanceMaxDiagonale, boolean peutSauter) {
        this(description, distanceMaxLigne, distanceMaxDiagonale, peutSauter, false, false);
    }

    /**
     * Cree des regles dynamiques completes.
     *
     * @param description description lisible du fonctionnement
     * @param distanceMaxLigne distance maximale en ligne droite
     * @param distanceMaxDiagonale distance maximale en diagonale
     * @param peutSauter {@code true} si la piece ignore les obstacles
     * @param ecraseLigne {@code true} si la piece supprime les ennemis sur son trajet en ligne
     * @param deplacementCavalier {@code true} si la piece autorise aussi le saut en L
     */
    public ReglesDeplacement(String description, int distanceMaxLigne, int distanceMaxDiagonale, boolean peutSauter,
                             boolean ecraseLigne, boolean deplacementCavalier) {
        this.description = description;
        this.distanceMaxLigne = distanceMaxLigne;
        this.distanceMaxDiagonale = distanceMaxDiagonale;
        this.peutSauter = peutSauter;
        this.ecraseLigne = ecraseLigne;
        this.deplacementCavalier = deplacementCavalier;
    }

    /**
     * Renvoie la description.
     *
     * @return description lisible du fonctionnement
     */
    public String getDescription() {
        return description;
    }

    /**
     * Renvoie la portee en ligne droite.
     *
     * @return distance maximale autorisee en ligne droite
     */
    public int getDistanceMaxLigne() {
        return distanceMaxLigne;
    }

    /**
     * Renvoie la portee en diagonale.
     *
     * @return distance maximale autorisee en diagonale
     */
    public int getDistanceMaxDiagonale() {
        return distanceMaxDiagonale;
    }

    /**
     * Indique si les obstacles sont ignores.
     *
     * @return {@code true} si la piece peut franchir les obstacles
     */
    public boolean peutSauter() {
        return peutSauter;
    }

    /**
     * Indique si la piece ecrase en ligne droite.
     *
     * @return {@code true} si la piece ecrase les ennemis sur son trajet en ligne
     */
    public boolean ecraseLigne() {
        return ecraseLigne;
    }

    /**
     * Indique si le saut de cavalier est autorise.
     *
     * @return {@code true} si la piece peut effectuer un deplacement de cavalier
     */
    public boolean deplacementCavalier() {
        return deplacementCavalier;
    }
}
