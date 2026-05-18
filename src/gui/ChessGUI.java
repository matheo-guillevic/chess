package gui;

import piece.Piece;
import piece.Couleur;
import engine.Coup;
import javax.swing.*;
import java.awt.*;
import engine.Game;

public class ChessGUI extends JFrame {
    private JButton[][] buttons = new JButton[8][8];
    private Game game;
    private Font chessFont;
    
    private int selectedX = -1;
    private int selectedY = -1;

    public ChessGUI(Game game) {
        this.game = game;

        setTitle("Jeu d'Echecs - Tour : " + game.getCurrentTurn());
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Centrer

        detectCompatibleFont();

        // Ajout du damier au centre avec des bordures
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        initializeBoard(boardPanel);

        // Panneaux pour les coordonnées
        JPanel topLabels = createHorizontalLabels();
        JPanel bottomLabels = createHorizontalLabels();
        JPanel leftLabels = createVerticalLabels();
        JPanel rightLabels = createVerticalLabels();

        add(topLabels, BorderLayout.NORTH);
        add(bottomLabels, BorderLayout.SOUTH);
        add(leftLabels, BorderLayout.WEST);
        add(rightLabels, BorderLayout.EAST);
        add(boardPanel, BorderLayout.CENTER);

        updateBoardDisplay();
    }

    private void detectCompatibleFont() {
        chessFont = new Font("SansSerif", Font.PLAIN, 60); // Police par défaut
        String[] preferredFonts = {"DejaVu Sans", "FreeSerif", "Symbola", "Noto Sans Symbols", "Segoe UI Symbol", "Arial Unicode MS"};
        for (String fontName : preferredFonts) {
            Font f = new Font(fontName, Font.PLAIN, 60);
            if (f.canDisplay('\u265F')) { // Vérifie si la police peut afficher le pion noir
                chessFont = f;
                break;
            }
        }
    }

    private JPanel createHorizontalLabels() {
        JPanel panel = new JPanel(new GridLayout(1, 8));
        panel.setPreferredSize(new Dimension(800, 30));
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String l : letters) {
            JLabel label = new JLabel(l, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            panel.add(label);
        }
        return panel;
    }

    private JPanel createVerticalLabels() {
        JPanel panel = new JPanel(new GridLayout(8, 1));
        panel.setPreferredSize(new Dimension(30, 800));
        for (int i = 8; i >= 1; i--) {
            JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            panel.add(label);
        }
        return panel;
    }

    private void initializeBoard(JPanel boardPanel) {
        for (int y = 7; y >= 0; y--) { // 8 à 1
            for (int x = 0; x < 8; x++) { // a à h
                JButton button = new JButton();
                button.setFont(chessFont);
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBorderPainted(false);
                
                int finalX = x;
                int finalY = y;
                button.addActionListener(e -> handleSquareClick(finalX, finalY));

                buttons[x][y] = button;
                boardPanel.add(button);
            }
        }
        resetBackgrounds();
    }

    private void handleSquareClick(int x, int y) {
        if (game.isFinished()) return;

        if (selectedX == -1 && selectedY == -1) {
            // Premier clic : sélection de la pièce
            Piece piece = game.getGrille().getPiece(x, y);
            if (piece != null && piece.getCouleur() == game.getCurrentTurn()) {
                selectedX = x;
                selectedY = y;
                buttons[x][y].setBackground(new Color(255, 235, 59)); // Jaune vif pour la sélection
                showValidMoves(piece);
            }
        } else {
            // Un clic sur la même case annule la sélection
            if (x == selectedX && y == selectedY) {
                selectedX = -1;
                selectedY = -1;
                resetBackgrounds();
                return;
            }

            // Deuxième clic : tentative de déplacement
            boolean success = game.tryMove(selectedX, selectedY, x, y);
            
            if (success) {
                selectedX = -1;
                selectedY = -1;
                resetBackgrounds();
                updateBoardDisplay();
                
                if (game.isFinished()) {
                    if (game.getWinner() == null) {
                        JOptionPane.showMessageDialog(this, "Pat : match nul.");
                        setTitle("Jeu d'Echecs - Match nul");
                    } else {
                        JOptionPane.showMessageDialog(this, "Échec et Mat ! Le joueur " + game.getWinner() + " a gagné !");
                        setTitle("Jeu d'Echecs - " + game.getWinner() + " GAGNE !");
                    }
                } else {
                    updateTitle();
                }
            } else {
                // Coup invalide, mais est-ce une autre de nos pièces ?
                Piece target = game.getGrille().getPiece(x, y);
                if (target != null && target.getCouleur() == game.getCurrentTurn()) {
                    // On change la sélection
                    resetBackgrounds();
                    selectedX = x;
                    selectedY = y;
                    buttons[x][y].setBackground(new Color(255, 235, 59));
                    showValidMoves(target);
                } else {
                    selectedX = -1;
                    selectedY = -1;
                    resetBackgrounds();
                }
            }
        }
    }

    private void showValidMoves(Piece piece) {
        for (Coup coup : game.getCoupsValides(game.getCurrentTurn())) {
            if (coup.getStartX() == piece.getX() && coup.getStartY() == piece.getY()) {
                int x = coup.getEndX();
                int y = coup.getEndY();
                Piece target = game.getGrille().getPiece(x, y);
                Color baseColor = ((x + y) % 2 == 0) ? new Color(181, 136, 99) : new Color(240, 217, 181);

                if (target != null) {
                    buttons[x][y].setBackground(blend(new Color(244, 67, 54), baseColor, 0.6f));
                } else {
                    buttons[x][y].setBackground(blend(new Color(139, 195, 74), baseColor, 0.5f));
                }
            }
        }
    }

    private Color blend(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * ratio + c2.getRed() * (1 - ratio));
        int g = (int) (c1.getGreen() * ratio + c2.getGreen() * (1 - ratio));
        int b = (int) (c1.getBlue() * ratio + c2.getBlue() * (1 - ratio));
        return new Color(Math.min(255, Math.max(0, r)), 
                         Math.min(255, Math.max(0, g)), 
                         Math.min(255, Math.max(0, b)));
    }

    private void resetBackgrounds() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                if ((x + y) % 2 == 0) {
                    buttons[x][y].setBackground(new Color(181, 136, 99)); // Marron
                } else {
                    buttons[x][y].setBackground(new Color(240, 217, 181)); // Crème
                }
            }
        }
    }

    private void updateBoardDisplay() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                Piece piece = game.getGrille().getPiece(x, y);
                if (piece == null) {
                    buttons[x][y].setText("");
                } else {
                    buttons[x][y].setText(piece.getSymbol());
                    if (piece.getCouleur() == Couleur.BLANC) {
                        buttons[x][y].setForeground(Color.WHITE);
                    } else {
                        buttons[x][y].setForeground(Color.BLACK);
                    }
                }
            }
        }
        updateTitle();
    }

    private void updateTitle() {
        String titre = "Jeu d'Echecs - Tour : " + game.getCurrentTurn();
        if (game.isKingInCheck(game.getCurrentTurn())) {
            titre += " (echec)";
        }
        setTitle(titre);
    }
}
