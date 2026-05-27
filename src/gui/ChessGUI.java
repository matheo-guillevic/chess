package gui;

import engine.Coup;
import engine.Game;
import ia.JoueurAutomatique;
import json.ChargementPiecesResultat;
import json.PiecePersonnaliseeInfo;
import piece.Couleur;
import piece.Piece;
import piece.PiecePersonnalisee;
import piece.Pion;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    /** Taille des images de pieces personnalisees sur l'echiquier. */
    private static final int CUSTOM_IMAGE_SIZE = 54;
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
    /** Cache des images de pieces personnalisees deja chargees. */
    private final Map<String, Icon> imageCache = new HashMap<>();

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
            Icon icone = chargerIconeCatalogue(value);
            String texte = value.getNom() + " (" + value.getCouleur() + ", " + value.getCoordonnee() + ")";
            JLabel label = new JLabel(icone == null ? value.getSymbole() + "  " + texte : texte, icone, JLabel.LEFT);
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
            Icon icone = chargerIconePiecePersonnalisee(piecePersonnalisee);
            if (icone != null) {
                button.setText("");
                button.setIcon(icone);
                button.setToolTipText(piecePersonnalisee.getNom());
                return;
            }

            button.setFont(fontPourSymbole(symbole, CUSTOM_SYMBOL_SIZE));
            button.setText(symbole);
            button.setToolTipText(symbole + " " + piecePersonnalisee.getNom());
            button.setForeground(piece.getCouleur() == Couleur.BLANC ? Color.WHITE : Color.BLACK);
            return;
        }

        button.setFont(fontPourSymbole(symbole, BOARD_SYMBOL_SIZE));
        button.setText(symbole);
        button.setToolTipText(null);
        button.setForeground(piece.getCouleur() == Couleur.BLANC ? Color.WHITE : Color.BLACK);
    }

    private Icon chargerIconePiecePersonnalisee(PiecePersonnalisee piece) {
        String image = piece.getImage();
        if (image == null || image.isBlank()) {
            return null;
        }
        return imageCache.computeIfAbsent(image, this::chargerIcone);
    }

    private Icon chargerIconeCatalogue(PiecePersonnaliseeInfo info) {
        String image = info.getImage();
        if (image == null || image.isBlank()) {
            return null;
        }
        return imageCache.computeIfAbsent("catalogue:" + image, key -> chargerIcone(image));
    }

    private Icon chargerIcone(String image) {
        if (image.toLowerCase().endsWith(".svg")) {
            return chargerIconeSvg(image);
        }

        ImageIcon icon = chargerIconeDepuisClasspath(image);
        if (icon == null) {
            icon = chargerIconeDepuisFichier(image);
        }
        if (icon == null || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }

        Image scaled = icon.getImage().getScaledInstance(CUSTOM_IMAGE_SIZE, CUSTOM_IMAGE_SIZE, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon chargerIconeDepuisClasspath(String image) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(image);
        if (url == null && image.startsWith("resources/")) {
            url = classLoader.getResource(image.substring("resources/".length()));
        }
        if (url == null && !image.startsWith("images/")) {
            url = classLoader.getResource("images/" + image);
        }
        return url == null ? null : new ImageIcon(url);
    }

    private ImageIcon chargerIconeDepuisFichier(String image) {
        File fichier = new File(image);
        if (!fichier.isFile()) {
            fichier = new File("resources", image);
        }
        return fichier.isFile() ? new ImageIcon(fichier.getAbsolutePath()) : null;
    }

    private Icon chargerIconeSvg(String image) {
        String contenu = lireTexteImage(image);
        if (contenu == null) {
            return null;
        }
        return new SvgIcon(contenu, couleurSvg(image), CUSTOM_IMAGE_SIZE);
    }

    private String lireTexteImage(String image) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String cheminClasspath = image.startsWith("resources/") ? image.substring("resources/".length()) : image;
        try (InputStream input = classLoader.getResourceAsStream(cheminClasspath)) {
            if (input != null) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            return null;
        }

        File fichier = new File(image);
        if (!fichier.isFile()) {
            fichier = new File("resources", image);
        }
        if (!fichier.isFile()) {
            return null;
        }
        try {
            return Files.readString(fichier.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private Color couleurSvg(String image) {
        return image.toLowerCase().contains("white") ? Color.WHITE : Color.BLACK;
    }

    private static class SvgIcon implements Icon {
        private static final Pattern VIEW_BOX = Pattern.compile("viewBox=\"([^\"]+)\"");
        private static final Pattern PATH_TAG = Pattern.compile("<path\\b[^>]*>");
        private static final Pattern PATH_DATA = Pattern.compile("d=\"([^\"]+)\"");
        private static final Pattern TOKEN = Pattern.compile("[AaCcHhLlMmQqSsTtVvZz]|[-+]?(?:\\d*\\.\\d+|\\d+)(?:[eE][-+]?\\d+)?");

        private final List<SvgPath> paths = new ArrayList<>();
        private final Color couleur;
        private final int taille;
        private double viewX;
        private double viewY;
        private double viewWidth = 24;
        private double viewHeight = 24;

        SvgIcon(String svg, Color couleur, int taille) {
            this.couleur = couleur;
            this.taille = taille;
            lireViewBox(svg);
            lirePaths(svg);
        }

        @Override
        public int getIconWidth() {
            return taille;
        }

        @Override
        public int getIconHeight() {
            return taille;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(couleur);

            double scale = Math.min(taille / viewWidth, taille / viewHeight);
            double offsetX = (taille - viewWidth * scale) / 2.0;
            double offsetY = (taille - viewHeight * scale) / 2.0;
            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);
            g2.translate(-viewX, -viewY);
            g2.setStroke(new BasicStroke((float) (2.0 / scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (SvgPath path : paths) {
                if (path.stroke) {
                    g2.draw(path.path);
                } else {
                    g2.fill(path.path);
                }
            }
            g2.dispose();
        }

        private void lireViewBox(String svg) {
            Matcher matcher = VIEW_BOX.matcher(svg);
            if (!matcher.find()) return;

            String[] valeurs = matcher.group(1).trim().split("[ ,]+");
            if (valeurs.length != 4) return;
            try {
                viewX = Double.parseDouble(valeurs[0]);
                viewY = Double.parseDouble(valeurs[1]);
                viewWidth = Double.parseDouble(valeurs[2]);
                viewHeight = Double.parseDouble(valeurs[3]);
            } catch (NumberFormatException e) {
                viewX = 0;
                viewY = 0;
                viewWidth = 24;
                viewHeight = 24;
            }
        }

        private void lirePaths(String svg) {
            Matcher tags = PATH_TAG.matcher(svg);
            while (tags.find()) {
                String tag = tags.group();
                Matcher data = PATH_DATA.matcher(tag);
                if (data.find()) {
                    paths.add(new SvgPath(parserPath(data.group(1)), tag.contains("stroke=")));
                }
            }
        }

        private Path2D parserPath(String data) {
            List<String> tokens = new ArrayList<>();
            Matcher matcher = TOKEN.matcher(data);
            while (matcher.find()) {
                tokens.add(matcher.group());
            }

            Path2D.Double path = new Path2D.Double();
            PathCursor cursor = new PathCursor();
            char commande = ' ';
            int i = 0;
            while (i < tokens.size()) {
                String token = tokens.get(i);
                if (estCommande(token)) {
                    commande = token.charAt(0);
                    i++;
                    if (commande == 'Z' || commande == 'z') {
                        path.closePath();
                        cursor.x = cursor.startX;
                        cursor.y = cursor.startY;
                    }
                    continue;
                }

                switch (commande) {
                    case 'M':
                    case 'm':
                        i = lireMove(tokens, i, path, cursor, commande == 'm');
                        commande = commande == 'm' ? 'l' : 'L';
                        break;
                    case 'L':
                    case 'l':
                        i = lireLine(tokens, i, path, cursor, commande == 'l');
                        break;
                    case 'H':
                    case 'h':
                        i = lireHorizontal(tokens, i, path, cursor, commande == 'h');
                        break;
                    case 'V':
                    case 'v':
                        i = lireVertical(tokens, i, path, cursor, commande == 'v');
                        break;
                    case 'C':
                    case 'c':
                        i = lireCurve(tokens, i, path, cursor, commande == 'c');
                        break;
                    default:
                        i++;
                        break;
                }
            }
            return path;
        }

        private int lireMove(List<String> tokens, int i, Path2D.Double path, PathCursor cursor, boolean relatif) {
            if (i + 1 >= tokens.size()) return tokens.size();
            double x = nombre(tokens.get(i++));
            double y = nombre(tokens.get(i++));
            if (relatif) {
                x += cursor.x;
                y += cursor.y;
            }
            path.moveTo(x, y);
            cursor.x = x;
            cursor.y = y;
            cursor.startX = x;
            cursor.startY = y;
            return i;
        }

        private int lireLine(List<String> tokens, int i, Path2D.Double path, PathCursor cursor, boolean relatif) {
            if (i + 1 >= tokens.size()) return tokens.size();
            double x = nombre(tokens.get(i++));
            double y = nombre(tokens.get(i++));
            if (relatif) {
                x += cursor.x;
                y += cursor.y;
            }
            path.lineTo(x, y);
            cursor.x = x;
            cursor.y = y;
            return i;
        }

        private int lireHorizontal(List<String> tokens, int i, Path2D.Double path, PathCursor cursor, boolean relatif) {
            if (i >= tokens.size()) return tokens.size();
            double x = nombre(tokens.get(i++));
            if (relatif) x += cursor.x;
            path.lineTo(x, cursor.y);
            cursor.x = x;
            return i;
        }

        private int lireVertical(List<String> tokens, int i, Path2D.Double path, PathCursor cursor, boolean relatif) {
            if (i >= tokens.size()) return tokens.size();
            double y = nombre(tokens.get(i++));
            if (relatif) y += cursor.y;
            path.lineTo(cursor.x, y);
            cursor.y = y;
            return i;
        }

        private int lireCurve(List<String> tokens, int i, Path2D.Double path, PathCursor cursor, boolean relatif) {
            if (i + 5 >= tokens.size()) return tokens.size();
            double x1 = nombre(tokens.get(i++));
            double y1 = nombre(tokens.get(i++));
            double x2 = nombre(tokens.get(i++));
            double y2 = nombre(tokens.get(i++));
            double x = nombre(tokens.get(i++));
            double y = nombre(tokens.get(i++));
            if (relatif) {
                x1 += cursor.x;
                y1 += cursor.y;
                x2 += cursor.x;
                y2 += cursor.y;
                x += cursor.x;
                y += cursor.y;
            }
            path.curveTo(x1, y1, x2, y2, x, y);
            cursor.x = x;
            cursor.y = y;
            return i;
        }

        private boolean estCommande(String token) {
            return token.length() == 1 && Character.isLetter(token.charAt(0));
        }

        private double nombre(String token) {
            return Double.parseDouble(token);
        }
    }

    private static class SvgPath {
        private final Path2D path;
        private final boolean stroke;

        SvgPath(Path2D path, boolean stroke) {
            this.path = path;
            this.stroke = stroke;
        }
    }

    private static class PathCursor {
        private double x;
        private double y;
        private double startX;
        private double startY;
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
