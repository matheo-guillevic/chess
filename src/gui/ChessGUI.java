package gui;

import piece.Piece;
import piece.Couleur;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import engine.Game;

public class ChessGUI extends JFrame {
    private JButton[][] buttons = new JButton[8][8];
    private Game game;
    
    private int selectedX = -1;
    private int selectedY = -1;

    public ChessGUI(Game game) {
        this.game = game;

        setTitle("Jeu d'Echecs - Tour : " + game.getCurrentTurn());
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));
        setLocationRelativeTo(null); // Centrer

        initializeBoard();
        updateBoardDisplay();
    }

    private void initializeBoard() {
        for (int y = 7; y >= 0; y--) { // 8 à 1
            for (int x = 0; x < 8; x++) { // a à h
                JButton button = new JButton();
                button.setFont(new Font("Serif", Font.BOLD, 50));
                button.setFocusPainted(false);
                
                if ((x + y) % 2 == 0) {
                    button.setBackground(new Color(118, 150, 86)); // Vert foncé
                } else {
                    button.setBackground(new Color(238, 238, 210)); // Crème clair
                }

                int finalX = x;
                int finalY = y;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleSquareClick(finalX, finalY);
                    }
                });

                buttons[x][y] = button;
                add(button);
            }
        }
    }

    private void handleSquareClick(int x, int y) {
        if (game.isFinished()) return;

        if (selectedX == -1 && selectedY == -1) {
            // Premier clic : sélection de la pièce
            Piece p = game.getGrille().getPiece(x, y);
            if (p != null && p.getCouleur() == game.getCurrentTurn()) {
                selectedX = x;
                selectedY = y;
                buttons[x][y].setBackground(Color.YELLOW);
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
                    JOptionPane.showMessageDialog(this, "Le joueur " + game.getWinner() + " a gagné !");
                    setTitle("Jeu d'Echecs - " + game.getWinner() + " GAGNE !");
                } else {
                    setTitle("Jeu d'Echecs - Tour : " + game.getCurrentTurn());
                }
            } else {
                // Coup invalide, mais est-ce une autre de nos pièces ?
                Piece target = game.getGrille().getPiece(x, y);
                if (target != null && target.getCouleur() == game.getCurrentTurn()) {
                    // On change la sélection
                    resetBackgrounds();
                    selectedX = x;
                    selectedY = y;
                    buttons[x][y].setBackground(Color.YELLOW);
                } else {
                    JOptionPane.showMessageDialog(this, "Coup invalide !");
                    selectedX = -1;
                    selectedY = -1;
                    resetBackgrounds();
                }
            }
        }
    }

    private void resetBackgrounds() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                if ((x + y) % 2 == 0) {
                    buttons[x][y].setBackground(new Color(118, 150, 86));
                } else {
                    buttons[x][y].setBackground(new Color(238, 238, 210));
                }
            }
        }
    }

    private void updateBoardDisplay() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                Piece p = game.getGrille().getPiece(x, y);
                if (p == null) {
                    buttons[x][y].setText("");
                } else {
                    buttons[x][y].setText(p.getSymbol());
                    if (p.getCouleur() == Couleur.BLANC) {
                        buttons[x][y].setForeground(Color.WHITE);
                    } else {
                        buttons[x][y].setForeground(Color.BLACK);
                    }
                }
            }
        }
    }
}
