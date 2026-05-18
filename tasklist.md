# Liste des Tâches : Projet Jeu d'Échecs

Ce document regroupe l'ensemble des tâches à effectuer pour réaliser le projet d'échecs en respectant le cahier des charges.

## 🎯 Partie 1 : Jeu d'Échecs Basique (Terminal)

### 1. Initialisation et Architecture de base
- [x] Initialiser le dépôt Git.
- [x] Créer la structure des packages (`src/engine`, `src/piece`, `src/plateau`, `src/cli`, `src/gui`).
- [x] Créer l'énumération `Couleur` (BLANC, NOIR).
- [x] Créer la classe abstraite `Piece` (ou interface) avec les propriétés communes (couleur, symbole, etc.).
- [x] Créer les sous-classes pour chaque type de pièce : `Pion`, `Tour`, `Cavalier`, `Fou`, `Reine`, `Roi`.
- [x] Créer la classe `Grille` (ou `Plateau`) représentant le tableau 8x8.
- [x] Créer la classe `Game` (ou `Partie`) pour gérer le déroulement.

### 2. Affichage dans le terminal
- [x] Intégrer les constantes Unicode fournies pour chaque pièce.
- [x] Développer une méthode dans `Grille` pour afficher l'état actuel de l'échiquier.
- [x] Afficher les bordures et les coordonnées (A-H pour les colonnes, 1-8 pour les lignes).
- [x] Positionner correctement les pièces pour le début de la partie.

### 3. Logique de déplacement (Règles de base)
*Note : Le roque, la prise en passant, la promotion et l'échec du roi ne sont pas requis dans cette partie.*
- [x] **Règles communes** : Empêcher de sortir du plateau ou de manger une pièce de sa propre couleur.
- [x] **Prise** : Implémenter le mécanisme de capture lorsqu'une pièce atterrit sur une case occupée par un ennemi.
- [x] **Pion** : Avancer d'1 case, avancer de 2 cases au premier coup, prise en diagonale d'1 case vers l'avant.
- [x] **Tour** : Déplacement rectiligne horizontal ou vertical sans sauter par-dessus d'autres pièces.
- [x] **Cavalier** : Déplacement en "L" (2 cases dans un sens, 1 case perpendiculaire), peut sauter par-dessus les autres.
- [x] **Fou** : Déplacement en diagonale sans sauter.
- [x] **Reine** : Combinaison des déplacements de la Tour et du Fou.
- [x] **Roi** : Déplacement d'1 case dans n'importe quelle direction.

### 4. Boucle de jeu et Interaction Utilisateur
- [x] Créer une boucle principale dans `ConsoleUI` (ou `Game`) demandant à chaque tour le mouvement du joueur actif.
- [x] Parser la saisie de l'utilisateur (ex: "A2 A4").
- [x] Convertir la saisie en coordonnées utilisables par la grille de code (ex: `A2` -> `[1][0]`).
- [x] Valider le mouvement :
  - *Valide* : Mettre à jour la grille, afficher le plateau, changer de joueur actif.
  - *Invalide* : Afficher un message d'erreur et redemander une saisie au même joueur.
- [x] S'assurer que le jeu peut tourner indéfiniment (partie complète).

---

## 🚀 Partie 2 : Pièces Personnalisées via JSON

### 1. Intégration du JSON
- [x] Ajouter une librairie de traitement JSON au projet (ex: **GSON** ou **Jackson**).
- [x] Définir le schéma JSON pour la déclaration d'une pièce personnalisée (Nom, Code Unicode, Couleur, Position initiale, Règles de déplacement).
- [x] Créer un ou plusieurs fichiers `.json` d'exemple (ex: ajouter le "LION").

### 2. Lecture et Désérialisation
- [x] Ajouter une étape au lancement de l'application : "Voulez-vous charger un fichier de pièces personnalisées ?".
- [x] Lire le fichier JSON et désérialiser les objets.
- [x] Placer ces nouvelles pièces sur le plateau au début de la partie sans écraser la logique existante.

### 3. Moteur de règles dynamiques
- [x] Créer une classe `PiecePersonnalisee` héritant de `Piece`.
- [x] Traduire les règles lues dans le JSON en algorithmes de validation de mouvement (ex: se déplace de `x` cases en diagonale, peut sauter, etc.).
- [x] Valider que les règles générées fonctionnent avec le système de mouvement de la Partie 1.

---

## 🌟 Bonus (Optionnel)

Ces tâches sont à réaliser **uniquement** si les parties 1 et 2 sont totalement terminées et fonctionnelles.

### 1. Mouvements Complexes
- [x] Gestion de l'échec au Roi (interdire un mouvement qui met son propre roi en échec).
- [x] Gestion du roque (petit roque et grand roque).
- [x] Gestion de la prise en passant du Pion.
- [x] Gestion de la promotion du Pion arrivé sur la dernière ligne.

### 2. Interface Graphique
- [x] Intégrer une librairie graphique (comme Swing, JavaFX ou autre).
- [x] Rendre l'échiquier cliquable (sélection d'une case de départ puis d'une case d'arrivée).
- [x] Afficher les pièces avec des images ou de belles polices Unicode dans la fenêtre.

### 3. Intelligence Artificielle (IA)
- [x] Implémenter un mode "Joueur vs Ordinateur".
- [x] Mettre en place un algorithme basique (ex: choix aléatoire parmi les coups valides).
- [x] (Avancé) Mettre en place un algorithme MinMax avec Alpha-Beta Pruning pour évaluer les meilleurs coups.

### 4. Tests automatisés
- [x] Ajouter un jeu de tests du moteur d'échecs.
- [x] Couvrir les mouvements de base, l'échec, le roque, la prise en passant, la promotion, les pièces JSON et l'IA.
