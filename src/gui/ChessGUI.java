package gui;

import engine.ChargementPiecesResultat;
import engine.Coup;
import engine.Game;
import engine.PiecePersonnaliseeInfo;
import ia.JoueurAutomatique;
import piece.Couleur;
import piece.Piece;
import piece.PiecePersonnalisee;
import piece.Pion;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Fenetre Swing principale du jeu d'echecs.
 *
 * <p>Elle permet de jouer sur un echiquier cliquable, de charger des pieces
 * personnalisees avec previsualisation, de choisir la promotion et d'activer le
 * mode IA.</p>
 */
public class ChessGUI extends JFrame {
    /** Taille des symboles dans les cases. */
    private static final int BOARD_SYMBOL_SIZE = 60;
    /** Taille plus compacte pour les emojis, dont les metriques sont souvent hautes. */
    private static final int CUSTOM_SYMBOL_SIZE = 36;
    /** Polices essayees quand un symbole n'est pas couvert par la police d'echecs. */
    private static final String[] SYMBOL_FONTS = {
            "Noto Color Emoji",
            "Noto Emoji",
            "Segoe UI Emoji",
            "Apple Color Emoji",
            "Twemoji Mozilla",
            "Symbola",
            "Noto Sans Symbols2",
            "Noto Sans Symbols",
            "Dialog",
            "SansSerif"
    };
    /** Boutons de l'echiquier indexes par coordonnees internes. */
    private final JButton[][] buttons = new JButton[8][8];
    /** Partie affichee et pilotee par la fenetre. */
    private Game game;
    /** Police capable d'afficher les symboles d'echecs Unicode. */
    private Font chessFont;
    /** Barre de statut affichee sous l'echiquier. */
    private JLabel statusLabel;
    /** Couleur controlee par l'ordinateur, ou {@code null} en humain contre humain. */
    private Couleur couleurIA;
    /** Joueur automatique utilise lorsque le mode IA est actif. */
    private final JoueurAutomatique joueurAutomatique = new JoueurAutomatique();

    /** Colonne de la piece actuellement selectionnee, ou -1. */
    private int selectedX = -1;
    /** Ligne de la piece actuellement selectionnee, ou -1. */
    private int selectedY = -1;

    /**
     * Cree une fenetre sans IA.
     *
     * @param game partie a afficher
     */
    public ChessGUI(Game game) {
        this(game, null);
    }

    /**
     * Cree une fenetre.
     *
     * @param game partie a afficher
     * @param couleurIA couleur controlee par l'ordinateur, ou {@code null}
     */
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
        List<PiecePersonnaliseeInfo> catalogue = game.lireCataloguePiecesPersonnalisees(fichier);
        Set<String> selection = choisirPiecesPersonnalisees(catalogue);
        if (selection == null) return;

        ChargementPiecesResultat chargement = game.chargerPiecesPersonnalisees(fichier, selection);
        updateBoardDisplay();

        StringBuilder message = new StringBuilder();
        message.append(chargement.getPiecesAjoutees()).append(" piece(s) personnalisee(s) ajoutee(s).");
        for (String erreur : chargement.getErreurs()) {
            message.append("\n- ").append(erreur);
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Chargement JSON", JOptionPane.INFORMATION_MESSAGE);
    }

    private Set<String> choisirPiecesPersonnalisees(List<PiecePersonnaliseeInfo> catalogue) {
        if (catalogue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune piece personnalisée lisible dans ce fichier.", "Chargement JSON", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        JDialog dialog = new JDialog(this, "Choisir les pieces personnalisees", true);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultListModel<PiecePersonnaliseeInfo> model = new DefaultListModel<>();
        for (PiecePersonnaliseeInfo info : catalogue) {
            model.addElement(info);
        }

        JList<PiecePersonnaliseeInfo> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setSelectedIndices(tousLesIndices(catalogue.size()));
        list.setCellRenderer((jList, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getSymbole() + "  " + value.getNom() + " (" + value.getCouleur() + ", " + value.getCoordonnee() + ")");
            label.setOpaque(true);
            label.setFont(fontPourSymbole(value.getSymbole(), 18));
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            label.setBackground(isSelected ? new Color(184, 207, 229) : Color.WHITE);
            return label;
        });

        JTextArea preview = new JTextArea();
        preview.setEditable(false);
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        preview.setFont(new Font("Dialog", Font.PLAIN, 14));
        preview.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        list.addListSelectionListener(e -> {
            List<PiecePersonnaliseeInfo> selected = list.getSelectedValuesList();
            preview.setText(creerPreview(selected.isEmpty() ? catalogue : selected));
        });
        preview.setText(creerPreview(list.getSelectedValuesList()));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(list),
                new JScrollPane(preview)
        );
        splitPane.setResizeWeight(0.45);
        splitPane.setPreferredSize(new Dimension(720, 360));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton toutSelectionner = new JButton("Tout selectionner");
        JButton charger = new JButton("Charger");
        JButton annuler = new JButton("Annuler");
        final boolean[] accepte = {false};

        toutSelectionner.addActionListener(e -> list.setSelectedIndices(tousLesIndices(catalogue.size())));
        charger.addActionListener(e -> {
            accepte[0] = true;
            dialog.dispose();
        });
        annuler.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(toutSelectionner);
        buttonsPanel.add(charger);
        buttonsPanel.add(annuler);

        dialog.add(splitPane, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (!accepte[0]) return null;

        Set<String> selection = new HashSet<>();
        for (PiecePersonnaliseeInfo info : list.getSelectedValuesList()) {
            selection.add(info.getNom());
        }
        if (selection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selectionnez au moins une piece.", "Chargement JSON", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return selection;
    }

    private int[] tousLesIndices(int taille) {
        int[] indices = new int[taille];
        for (int i = 0; i < taille; i++) {
            indices[i] = i;
        }
        return indices;
    }

    private String creerPreview(List<PiecePersonnaliseeInfo> infos) {
        List<String> lignes = new ArrayList<>();
        for (PiecePersonnaliseeInfo info : infos) {
            lignes.add(info.getSymbole() + " " + info.getNom());
            lignes.add("Couleur : " + info.getCouleur());
            lignes.add("Position : " + info.getCoordonnee());
            lignes.add("Fonctionnement : " + info.getDescription());
            lignes.add("");
        }
        return String.join("\n", lignes);
    }

    private void definirIA(Couleur couleurIA) {
        this.couleurIA = couleurIA;
        updateBoardDisplay();
        jouerTourIASiNecessaire();
    }

    private void detectCompatibleFont() {
        chessFont = new Font("SansSerif", Font.PLAIN, BOARD_SYMBOL_SIZE);
        String[] preferredFonts = {"DejaVu Sans", "FreeSerif", "Symbola", "Noto Sans Symbols", "Segoe UI Symbol", "Arial Unicode MS"};
        for (String fontName : preferredFonts) {
            Font f = new Font(fontName, Font.PLAIN, BOARD_SYMBOL_SIZE);
            if (f.canDisplay('\u265F')) {
                chessFont = f;
                break;
            }
        }
    }

    private Font fontPourSymbole(String symbole, int taille) {
        if (symbole == null || symbole.isEmpty()) {
            return chessFont.deriveFont((float) taille);
        }

        if (chessFont.canDisplayUpTo(symbole) == -1) {
            return chessFont.deriveFont((float) taille);
        }

        for (String fontName : SYMBOL_FONTS) {
            Font font = new Font(fontName, Font.PLAIN, taille);
            if (font.canDisplayUpTo(symbole) == -1) {
                return font;
            }
        }

        return new Font("Dialog", Font.PLAIN, taille);
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
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setHorizontalAlignment(SwingConstants.CENTER);
                button.setVerticalAlignment(SwingConstants.CENTER);

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
            Optional<Coup> coup = joueurAutomatique.jouer(game);
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
                    buttons[x][y].setIcon(null);
                    buttons[x][y].setFont(chessFont);
                } else {
                    afficherPiece(buttons[x][y], piece);
                }
            }
        }
        updateTitleAndStatus();
    }

    private void afficherPiece(JButton button, Piece piece) {
        String symbole = piece.getSymbol();
        button.setIcon(null);

        if (piece instanceof PiecePersonnalisee) {
            PiecePersonnalisee piecePersonnalisee = (PiecePersonnalisee) piece;
            button.setText("");
            button.setIcon(new PiecePersonnaliseeIcon(piecePersonnalisee.getNom(), piece.getCouleur()));
            button.setToolTipText(symbole + " " + piecePersonnalisee.getNom());
            return;
        }

        button.setFont(fontPourSymbole(symbole, BOARD_SYMBOL_SIZE));
        button.setText(symbole);
        button.setToolTipText(null);
        button.setForeground(piece.getCouleur() == Couleur.BLANC ? Color.WHITE : Color.BLACK);
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

    private static class PiecePersonnaliseeIcon implements Icon {
        private static final int SIZE = 54;
        private final String nom;
        private final Couleur couleur;

        PiecePersonnaliseeIcon(String nom, Couleur couleur) {
            this.nom = nom == null ? "" : nom.toLowerCase();
            this.couleur = couleur;
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (nom.contains("bus")) {
                paintBus(g2);
            } else if (nom.contains("minotaure")) {
                paintMinotaure(g2);
            } else if (nom.contains("lion")) {
                paintLion(g2);
            } else {
                paintDefault(g2);
            }

            g2.dispose();
        }

        private void paintBus(Graphics2D g2) {
            Color body = couleur == Couleur.BLANC ? new Color(255, 202, 40) : new Color(244, 143, 27);
            g2.setColor(body);
            g2.fillRoundRect(7, 13, 40, 27, 8, 8);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(7, 13, 40, 27, 8, 8);
            g2.setColor(new Color(187, 222, 251));
            g2.fillRect(12, 18, 9, 9);
            g2.fillRect(24, 18, 9, 9);
            g2.fillRect(36, 18, 7, 9);
            g2.setColor(Color.BLACK);
            g2.drawLine(7, 30, 47, 30);
            g2.fillOval(13, 35, 9, 9);
            g2.fillOval(33, 35, 9, 9);
            g2.setColor(Color.WHITE);
            g2.fillOval(16, 38, 3, 3);
            g2.fillOval(36, 38, 3, 3);
        }

        private void paintLion(Graphics2D g2) {
            g2.setColor(new Color(183, 100, 25));
            int[] xs = {27, 33, 40, 39, 47, 41, 43, 35, 32, 27, 22, 19, 11, 13, 7, 15, 14, 21};
            int[] ys = {5, 13, 11, 19, 24, 29, 38, 37, 47, 40, 47, 37, 38, 29, 24, 19, 11, 13};
            g2.fillPolygon(xs, ys, xs.length);
            g2.setColor(new Color(255, 183, 77));
            g2.fillOval(13, 13, 28, 28);
            g2.setColor(Color.BLACK);
            g2.drawOval(13, 13, 28, 28);
            g2.fillOval(21, 24, 4, 4);
            g2.fillOval(31, 24, 4, 4);
            g2.drawArc(23, 29, 10, 7, 200, 140);
            g2.setColor(new Color(93, 64, 55));
            g2.fillOval(26, 29, 5, 4);
        }

        private void paintMinotaure(Graphics2D g2) {
            g2.setColor(new Color(255, 238, 176));
            g2.fillArc(2, 6, 21, 25, 120, 210);
            g2.fillArc(31, 6, 21, 25, -150, 210);
            g2.setColor(Color.BLACK);
            g2.drawArc(2, 6, 21, 25, 120, 210);
            g2.drawArc(31, 6, 21, 25, -150, 210);

            g2.setColor(new Color(121, 85, 72));
            g2.fillOval(13, 13, 28, 31);
            g2.setColor(new Color(161, 113, 83));
            g2.fillOval(18, 27, 18, 15);
            g2.setColor(Color.BLACK);
            g2.drawOval(13, 13, 28, 31);
            g2.fillOval(21, 25, 4, 4);
            g2.fillOval(31, 25, 4, 4);
            g2.fillOval(23, 34, 3, 3);
            g2.fillOval(30, 34, 3, 3);
        }

        private void paintDefault(Graphics2D g2) {
            g2.setColor(couleur == Couleur.BLANC ? Color.WHITE : Color.BLACK);
            g2.fillOval(10, 8, 34, 34);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(10, 8, 34, 34);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString("?", 22, 32);
        }
    }
}
