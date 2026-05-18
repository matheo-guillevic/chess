package engine;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import piece.Couleur;
import piece.PiecePersonnalisee;
import piece.ReglesDeplacement;
import plateau.Grille;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChargeurPiecesPersonnalisees {
    private final Gson gson = new Gson();

    public ChargementPiecesResultat charger(Path fichier, Grille grille) {
        ChargementPiecesResultat resultat = new ChargementPiecesResultat();

        if (!Files.isRegularFile(fichier)) {
            resultat.ajouterErreur("Fichier introuvable : " + fichier);
            return resultat;
        }

        try (Reader reader = Files.newBufferedReader(fichier)) {
            FichierPiecesPersonnalisees donnees = gson.fromJson(reader, FichierPiecesPersonnalisees.class);
            if (donnees == null || donnees.piecesPersonnalisees == null) {
                resultat.ajouterErreur("Le fichier ne contient pas de tableau piecesPersonnalisees.");
                return resultat;
            }

            for (DefinitionPiece definition : donnees.piecesPersonnalisees) {
                ajouterPiece(definition, grille, resultat);
            }
        } catch (IOException e) {
            resultat.ajouterErreur("Impossible de lire le fichier : " + e.getMessage());
        } catch (JsonSyntaxException e) {
            resultat.ajouterErreur("JSON invalide : " + e.getMessage());
        }

        return resultat;
    }

    private void ajouterPiece(DefinitionPiece definition, Grille grille, ChargementPiecesResultat resultat) {
        String nom = definition != null && definition.nom != null ? definition.nom : "Piece personnalisee";

        if (definition == null || definition.positionInitiale == null || definition.reglesDeplacement == null) {
            resultat.ajouterErreur(nom + " ignoree : definition incomplete.");
            return;
        }

        int x = definition.positionInitiale.x;
        int y = definition.positionInitiale.y;
        if (!grille.isInside(x, y)) {
            resultat.ajouterErreur(nom + " ignoree : position hors plateau (" + x + ", " + y + ").");
            return;
        }

        if (!grille.isEmpty(x, y)) {
            resultat.ajouterErreur(nom + " ignoree : la case " + coordonnee(x, y) + " est deja occupee.");
            return;
        }

        if (definition.couleur == null || definition.couleur.isBlank()) {
            resultat.ajouterErreur(nom + " ignoree : couleur manquante.");
            return;
        }

        Couleur couleur;
        try {
            couleur = Couleur.valueOf(definition.couleur.toUpperCase());
        } catch (RuntimeException e) {
            resultat.ajouterErreur(nom + " ignoree : couleur invalide.");
            return;
        }

        String symbole = convertirSymbole(definition.codeUnicode);
        grille.setPiece(new PiecePersonnalisee(nom, symbole, x, y, couleur, definition.reglesDeplacement), x, y);
        resultat.incrementerPiecesAjoutees();
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

    private static class FichierPiecesPersonnalisees {
        private DefinitionPiece[] piecesPersonnalisees;
    }

    private static class DefinitionPiece {
        private String nom;
        private String codeUnicode;
        private String couleur;
        private Position positionInitiale;
        private ReglesDeplacement reglesDeplacement;
    }

    private static class Position {
        private int x;
        private int y;
    }
}
