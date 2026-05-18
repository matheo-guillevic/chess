package gui;

import engine.ChargementPiecesResultat;
import engine.Coup;
import engine.Game;
import piece.Couleur;
import piece.Piece;
import piece.Pion;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class ChessGUI extends JFrame {
    private final JButton[][] buttons = new JButton[8][8];
    private Game game;
    private Font chessFont;
    private JLabel statusLabel;
    private Couleur couleurIA;

    private int selectedX = -1;
    private int selectedY = -1;

    public ChessGUI(Game game) {
        this(game, null);
    }

    public ChessGUI(Game game, Couleur couleurIA) {
        this.game = game;
        this.couleurIA = couleurIA;

        setTitle("Jeu d'Echecs - Tour : " + game.getCurrentTurn());
        setSize(820, 860);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        detectCompatibleFont();
        setJMenuBar(createMenuBar());

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        initializeBoard(boardPanel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(createHorizontalLabels(), BorderLayout.NORTH);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(createHorizontalLabels(), BorderLayout.NORTH);
        add(createVerticalLabels(), BorderLayout.WEST);
        add(createVerticalLabels(), BorderLayout.EAST);
        add(boardPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        updateBoardDisplay();
        jouerTourIASiNecessaire();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu partieMenu = new JMenu("Partie");
        JMenuItem nouvellePartie = new JMenuItem("Nouvelle partie");
        nouvellePartie.addActionListener(e -> nouvellePartie());
        JMenuItem chargerPieces = new JMenuItem("Charger pieces personnalisees...");
        chargerPieces.addActionListener(e -> chargerPiecesPersonnalisees());
        JMenuItem quitter = new JMenuItem("Quitter");
        quitter.addActionListener(e -> dispose());
        partieMenu.add(nouvellePartie);
        partieMenu.add(chargerPieces);
        partieMenu.addSeparator();
        partieMenu.add(quitter);

        JMenu modeMenu = new JMenu("Mode");
        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButtonMenuItem humainVsHumain = new JRadioButtonMenuItem("Humain vs humain", couleurIA == null);
        humainVsHumain.addActionListener(e -> definirIA(null));
        JRadioButtonMenuItem iaNoire = new JRadioButtonMenuItem("Ordinateur joue noir", couleurIA == Couleur.NOIR);
        iaNoire.addActionListener(e -> definirIA(Couleur.NOIR));
        JRadioButtonMenuItem iaBlanche = new JRadioButtonMenuItem("Ordinateur joue blanc", couleurIA == Couleur.BLANC);
        iaBlanche.addActionListener(e -> definirIA(Couleur.BLANC));
        modeGroup.add(humainVsHumain);
        modeGroup.add(iaNoire);
        modeGroup.add(iaBlanche);
        modeMenu.add(humainVsHumain);
        modeMenu.add(iaNoire);
        modeMenu.add(iaBlanche);

        menuBar.add(partieMenu);
        menuBar.add(modeMenu);
        return menuBar;
    }

    private void nouvellePartie() {
        game = new Game();
        selectedX = -1;
        selectedY = -1;
        resetBackgrounds();
        updateBoardDisplay();
        jouerTourIASiNecessaire();
    }

    private void chargerPiecesPersonnalisees() {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        chooser.setDialogTitle("Charger un fichier JSON de pieces personnalisees");
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        Path fichier = chooser.getSelectedFile().toPath();
        ChargementPiecesResultat chargement = game.chargerPiecesPersonnalisees(fichier);
        updateBoardDisplay();

        StringBuilder message = new StringBuilder();
        message.append(chargement.getPiecesAjoutees()).append(" piece(s) personnalisee(s) ajoutee(s).");
        for (String erreur : chargement.getErreurs()) {
            message.append("\n- ").append(erreur);
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Chargement JSON", JOptionPane.INFORMATION_MESSAGE);
    }

    private void definirIA(Couleur couleurIA) {
        this.couleurIA = couleurIA;
        updateBoardDisplay();
        jouerTourIASiNecessaire();
    }

    private void detectCompatibleFont() {
        chessFont = new Font("SansSerif", Font.PLAIN, 60);
        String[] preferredFonts = {"DejaVu Sans", "FreeSerif", "Symbola", "Noto Sans Symbols", "Segoe UI Symbol", "Arial Unicode MS"};
        for (String fontName : preferredFonts) {
            Font f = new Font(fontName, Font.PLAIN, 60);
            if (f.canDisplay('\u265F')) {
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
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
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
        if (game.isFinished() || isTourIA()) return;

        if (selectedX == -1 && selectedY == -1) {
            selectionnerPiece(x, y);
            return;
        }

        if (x == selectedX && y == selectedY) {
            annulerSelection();
            return;
        }

        Piece target = game.getGrille().getPiece(x, y);
        if (target != null && target.getCouleur() == game.getCurrentTurn()) {
            annulerSelection();
            selectionnerPiece(x, y);
            return;
        }

        String promotion = "reine";
        if (estPromotionDemandee(selectedX, selectedY, x, y)) {
            promotion = demanderPromotion();
            if (promotion == null) return;
        }

        boolean success = game.tryMove(selectedX, selectedY, x, y, promotion);
        if (success) {
            annulerSelection();
            updateBoardDisplay();
            afficherFinSiNecessaire();
            jouerTourIASiNecessaire();
        } else {
            annulerSelection();
        }
    }

    private void selectionnerPiece(int x, int y) {
        Piece piece = game.getGrille().getPiece(x, y);
        if (piece != null && piece.getCouleur() == game.getCurrentTurn()) {
            selectedX = x;
            selectedY = y;
            buttons[x][y].setBackground(new Color(255, 235, 59));
            showValidMoves(piece);
        }
    }

    private void annulerSelection() {
        selectedX = -1;
        selectedY = -1;
        resetBackgrounds();
    }

    private boolean estPromotionDemandee(int startX, int startY, int endX, int endY) {
        Piece piece = game.getGrille().getPiece(startX, startY);
        if (!(piece instanceof Pion) || (endY != 0 && endY != 7)) return false;

        for (Coup coup : game.getCoupsValides(game.getCurrentTurn())) {
            if (coup.getStartX() == startX && coup.getStartY() == startY
                    && coup.getEndX() == endX && coup.getEndY() == endY) {
                return true;
            }
        }
        return false;
    }

    private String demanderPromotion() {
        String[] choix = {"Reine", "Tour", "Fou", "Cavalier"};
        String selection = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez la piece de promotion :",
                "Promotion du pion",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choix,
                choix[0]
        );

        return selection == null ? null : selection.toLowerCase();
    }

    private void jouerTourIASiNecessaire() {
        if (!isTourIA()) return;

        SwingUtilities.invokeLater(() -> {
            if (!isTourIA()) return;
            Optional<Coup> coup = game.jouerCoupAutomatique();
            updateBoardDisplay();
            coup.ifPresent(value -> statusLabel.setText("Ordinateur (" + couleurIA + ") joue : " + value));
            afficherFinSiNecessaire();
        });
    }

    private boolean isTourIA() {
        return couleurIA != null && !game.isFinished() && game.getCurrentTurn() == couleurIA;
    }

    private void afficherFinSiNecessaire() {
        if (!game.isFinished()) return;

        if (game.getWinner() == null) {
            JOptionPane.showMessageDialog(this, "Pat : match nul.");
            setTitle("Jeu d'Echecs - Match nul");
            statusLabel.setText("Match nul.");
        } else {
            JOptionPane.showMessageDialog(this, "Echec et mat : le joueur " + game.getWinner() + " a gagne.");
            setTitle("Jeu d'Echecs - " + game.getWinner() + " gagne");
            statusLabel.setText("Le joueur " + game.getWinner() + " a gagne.");
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
                    buttons[x][y].setBackground(new Color(181, 136, 99));
                } else {
                    buttons[x][y].setBackground(new Color(240, 217, 181));
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
                    buttons[x][y].setForeground(piece.getCouleur() == Couleur.BLANC ? Color.WHITE : Color.BLACK);
                }
            }
        }
        updateTitleAndStatus();
    }

    private void updateTitleAndStatus() {
        String titre = "Jeu d'Echecs - Tour : " + game.getCurrentTurn();
        String status = "Tour : " + game.getCurrentTurn();
        if (game.isKingInCheck(game.getCurrentTurn())) {
            titre += " (echec)";
            status += " - echec au roi";
        }
        if (couleurIA != null) {
            status += " - ordinateur : " + couleurIA;
        }
        setTitle(titre);
        statusLabel.setText(status);
    }
}
