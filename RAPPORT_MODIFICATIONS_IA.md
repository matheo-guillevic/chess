# Rapport des modifications IA

Ce fichier sert de trace courte des changements effectues par l'IA dans le projet.
Il sera mis a jour a chaque prochaine modification de code.

## Historique

### 2026-05-18
- Finalisation de la partie JSON : chargement des pieces personnalisees, validation des donnees et placement sur le plateau.
- Ajout d'un chargeur JSON robuste, utilisable depuis IntelliJ, les scripts et les ressources du classpath.
- Suppression de la dependance obligatoire a Gson au profit d'un parseur JSON interne simple.
- Ajout des mouvements speciaux : echec au roi, roque, prise en passant et promotion.
- Ajout du choix de promotion dans le moteur et dans l'interface graphique.
- Ajout d'une IA jouable avec choix de coups valides et MinMax avec alpha-beta.
- Integration des fonctionnalites CLI dans la GUI : menus, chargement JSON, mode IA et statut de partie.
- Modification du demarrage : choix du mode GUI ou CLI depuis `Main`, avec `--gui` et `--cli` disponibles.
- Ajout et subdivision des tests automatises par domaine : regles de base, mouvements speciaux, JSON et IA.
- Mise a jour des scripts `run.sh`, `run_gui.sh` et `run_tests.sh` pour compiler, copier les ressources et transmettre les arguments.
- Ajout des pieces personnalisees Bus et Minotaure, avec nouvelles regles JSON `ecraseLigne` et `deplacementCavalier`, plus tests dedies.
- Ajout d'un menu CLI et GUI de selection unitaire des pieces personnalisees avec previsualisation du symbole, de la position et du fonctionnement.
- Ajout d'une Javadoc complete sur les classes du projet et d'un script `generate_javadoc.sh` pour generer `doc/javadoc/index.html`.
