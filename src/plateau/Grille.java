package plateau;

import piece.Piece;

public class Grille {
    private final Piece[][] board;

    public Grille(){
        this.board = new Piece[8][8];
    }

    public void setPiece(Piece piece, int x, int y) {
        if (piece != null) {
            piece.setX(x);
            piece.setY(y);
        }
        board[x][y] = piece;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return board[x][y];
    }

    public void movePiece(int startX, int startY, int endX, int endY) {
        Piece piece = getPiece(startX, startY);
        setPiece(null, startX, startY);
        setPiece(piece, endX, endY);
    }

    public void afficher() {
        System.out.println();
        for (int y = 7; y >= 0; y--) { 
            System.out.print((y + 1) + "|");
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x, y);
                if (piece == null) {
                    System.out.print(" |");
                } else {
                    System.out.print(piece.getSymbol() + "|");
                }
            }
            System.out.println();
        }
        System.out.println("  A B C D E F G H\n");
    }
}
