package engine;

public class Coup {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    public Coup(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }

    @Override
    public String toString() {
        return coordonnee(startX, startY) + " " + coordonnee(endX, endY);
    }

    private String coordonnee(int x, int y) {
        return String.valueOf((char) ('a' + x)) + (y + 1);
    }
}
