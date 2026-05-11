package plateau;

import piece.Piece;

public class Grille {
    private Piece[][] board;

    public Grille(){
        this.board = new Piece[8][8];
    }

    public void setPiece(Piece p, int x, int y) {
        if (p != null) {
            p.setX(x);
            p.setY(y);
        }
        board[x][y] = p;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return board[x][y];
    }

    public void movePiece(int startX, int startY, int endX, int endY) {
        Piece p = getPiece(startX, startY);
        setPiece(null, startX, startY);
        setPiece(p, endX, endY);
    }

    public void afficher() {
        System.out.println();
        for (int y = 7; y >= 0; y--) { 
            System.out.print((y + 1) + "|");
            for (int x = 0; x < 8; x++) {
                Piece p = getPiece(x, y);
                if (p == null) {
                    System.out.print(" |");
                } else {
                    System.out.print(p.getSymbol() + "|");
                }
            }
            System.out.println();
        }
        System.out.println("  A B C D E F G H\n");
    }
}
