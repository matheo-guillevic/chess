package piece;

public class ReglesDeplacement {
    private String description;
    private int distanceMaxLigne;
    private int distanceMaxDiagonale;
    private boolean peutSauter;
    private boolean ecraseLigne;
    private boolean deplacementCavalier;

    public ReglesDeplacement(String description, int distanceMaxLigne, int distanceMaxDiagonale, boolean peutSauter) {
        this(description, distanceMaxLigne, distanceMaxDiagonale, peutSauter, false, false);
    }

    public ReglesDeplacement(String description, int distanceMaxLigne, int distanceMaxDiagonale, boolean peutSauter,
                             boolean ecraseLigne, boolean deplacementCavalier) {
        this.description = description;
        this.distanceMaxLigne = distanceMaxLigne;
        this.distanceMaxDiagonale = distanceMaxDiagonale;
        this.peutSauter = peutSauter;
        this.ecraseLigne = ecraseLigne;
        this.deplacementCavalier = deplacementCavalier;
    }

    public String getDescription() {
        return description;
    }

    public int getDistanceMaxLigne() {
        return distanceMaxLigne;
    }

    public int getDistanceMaxDiagonale() {
        return distanceMaxDiagonale;
    }

    public boolean peutSauter() {
        return peutSauter;
    }

    public boolean ecraseLigne() {
        return ecraseLigne;
    }

    public boolean deplacementCavalier() {
        return deplacementCavalier;
    }
}
