package piece;

public class ReglesDeplacement {
    private String description;
    private int distanceMaxLigne;
    private int distanceMaxDiagonale;
    private boolean peutSauter;

    public ReglesDeplacement(String description, int distanceMaxLigne, int distanceMaxDiagonale, boolean peutSauter) {
        this.description = description;
        this.distanceMaxLigne = distanceMaxLigne;
        this.distanceMaxDiagonale = distanceMaxDiagonale;
        this.peutSauter = peutSauter;
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
}
