package engine;

import piece.Couleur;
import piece.PiecePersonnalisee;
import piece.ReglesDeplacement;
import plateau.Grille;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ChargeurPiecesPersonnalisees {
    public ChargementPiecesResultat charger(Path fichier, Grille grille) {
        ChargementPiecesResultat resultat = new ChargementPiecesResultat();
        Path fichierResolue = resoudreFichier(fichier);

        if (fichierResolue == null) {
            resultat.ajouterErreur("Fichier introuvable : " + fichier + " (repertoire courant : " + System.getProperty("user.dir") + ")");
            return resultat;
        }

        try {
            Object racine = new SimpleJsonParser(Files.readString(fichierResolue)).parse();
            if (!(racine instanceof Map<?, ?>)) {
                resultat.ajouterErreur("Le fichier JSON doit contenir un objet racine.");
                return resultat;
            }

            Object pieces = ((Map<?, ?>) racine).get("piecesPersonnalisees");
            if (!(pieces instanceof List<?>)) {
                resultat.ajouterErreur("Le fichier ne contient pas de tableau piecesPersonnalisees.");
                return resultat;
            }

            for (Object piece : (List<?>) pieces) {
                ajouterPiece(asMap(piece), grille, resultat);
            }
        } catch (IOException e) {
            resultat.ajouterErreur("Impossible de lire le fichier : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            resultat.ajouterErreur("JSON invalide : " + e.getMessage());
        }

        return resultat;
    }

    private Path resoudreFichier(Path fichier) {
        if (fichier == null) return null;

        if (Files.isRegularFile(fichier)) {
            return fichier;
        }

        Path nomFichier = fichier.getFileName();
        if (nomFichier == null) return null;

        Path depuisUserDir = Paths.get(System.getProperty("user.dir")).resolve(fichier);
        if (Files.isRegularFile(depuisUserDir)) {
            return depuisUserDir;
        }

        Path courant = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (courant != null) {
            Path candidat = courant.resolve(fichier);
            if (Files.isRegularFile(candidat)) {
                return candidat;
            }

            candidat = courant.resolve(nomFichier);
            if (Files.isRegularFile(candidat)) {
                return candidat;
            }

            courant = courant.getParent();
        }

        URL ressource = Thread.currentThread().getContextClassLoader().getResource(fichier.toString());
        if (ressource == null) {
            ressource = Thread.currentThread().getContextClassLoader().getResource(nomFichier.toString());
        }

        if (ressource != null && "file".equals(ressource.getProtocol())) {
            try {
                Path depuisClasspath = Paths.get(ressource.toURI());
                if (Files.isRegularFile(depuisClasspath)) {
                    return depuisClasspath;
                }
            } catch (URISyntaxException e) {
                return null;
            }
        }

        return null;
    }

    private void ajouterPiece(Map<?, ?> definition, Grille grille, ChargementPiecesResultat resultat) {
        String nom = asString(definition.get("nom"));
        if (nom == null || nom.isBlank()) {
            nom = "Piece personnalisee";
        }

        Map<?, ?> position = asMap(definition.get("positionInitiale"));
        Map<?, ?> reglesMap = asMap(definition.get("reglesDeplacement"));
        if (position == null || reglesMap == null) {
            resultat.ajouterErreur(nom + " ignoree : definition incomplete.");
            return;
        }

        Integer x = asInteger(position.get("x"));
        Integer y = asInteger(position.get("y"));
        if (x == null || y == null) {
            resultat.ajouterErreur(nom + " ignoree : position initiale invalide.");
            return;
        }

        if (!grille.isInside(x, y)) {
            resultat.ajouterErreur(nom + " ignoree : position hors plateau (" + x + ", " + y + ").");
            return;
        }

        if (!grille.isEmpty(x, y)) {
            resultat.ajouterErreur(nom + " ignoree : la case " + coordonnee(x, y) + " est deja occupee.");
            return;
        }

        String couleurTexte = asString(definition.get("couleur"));
        if (couleurTexte == null || couleurTexte.isBlank()) {
            resultat.ajouterErreur(nom + " ignoree : couleur manquante.");
            return;
        }

        Couleur couleur;
        try {
            couleur = Couleur.valueOf(couleurTexte.toUpperCase());
        } catch (RuntimeException e) {
            resultat.ajouterErreur(nom + " ignoree : couleur invalide.");
            return;
        }

        ReglesDeplacement regles = new ReglesDeplacement(
                asString(reglesMap.get("description")),
                valueOrZero(asInteger(reglesMap.get("distanceMaxLigne"))),
                valueOrZero(asInteger(reglesMap.get("distanceMaxDiagonale"))),
                Boolean.TRUE.equals(reglesMap.get("peutSauter")),
                Boolean.TRUE.equals(reglesMap.get("ecraseLigne")),
                Boolean.TRUE.equals(reglesMap.get("deplacementCavalier"))
        );

        String symbole = convertirSymbole(asString(definition.get("codeUnicode")));
        grille.setPiece(new PiecePersonnalisee(nom, symbole, x, y, couleur, regles), x, y);
        resultat.incrementerPiecesAjoutees();
    }

    private Map<?, ?> asMap(Object value) {
        return value instanceof Map<?, ?> ? (Map<?, ?>) value : null;
    }

    private String asString(Object value) {
        return value instanceof String ? (String) value : null;
    }

    private Integer asInteger(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private String convertirSymbole(String codeUnicode) {
        if (codeUnicode == null || codeUnicode.isBlank()) {
            return "?";
        }

        String valeur = codeUnicode.trim();
        try {
            if (valeur.startsWith("0x") || valeur.startsWith("0X")) {
                return new String(Character.toChars(Integer.parseInt(valeur.substring(2), 16)));
            }
            if (valeur.startsWith("U+")) {
                return new String(Character.toChars(Integer.parseInt(valeur.substring(2), 16)));
            }
            if (valeur.matches("\\d+")) {
                return new String(Character.toChars(Integer.parseInt(valeur)));
            }
        } catch (IllegalArgumentException e) {
            return "?";
        }

        return valeur;
    }

    private String coordonnee(int x, int y) {
        return String.valueOf((char) ('A' + x)) + (y + 1);
    }
}
