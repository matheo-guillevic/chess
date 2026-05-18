# Regles des mouvements speciaux

## Castling / Roque

Le roque se joue en deplacant le roi de deux cases vers la tour.

- Petit roque blanc : `e1 g1`
- Grand roque blanc : `e1 c1`
- Petit roque noir : `e8 g8`
- Grand roque noir : `e8 c8`

Conditions appliquees par le moteur :
- le roi et la tour concernee ne doivent jamais avoir bouge ;
- aucune piece ne doit se trouver entre le roi et la tour ;
- le roi ne doit pas etre en echec ;
- le roi ne doit pas traverser ou atteindre une case attaquee.

## Pawn Promotion / Promotion du pion

Un pion est automatiquement promu en reine quand il atteint la derniere rangee.

- Promotion blanche : un pion blanc arrive sur la rangee 8.
- Promotion noire : un pion noir arrive sur la rangee 1.

## En Passant Capture / Prise en passant

La prise en passant est possible uniquement juste apres qu'un pion adverse avance de deux cases et arrive a cote d'un pion.

Exemple :
- blanc joue `e2 e4`
- blanc joue plus tard `e4 e5`
- noir joue `d7 d5`
- blanc peut immediatement jouer `e5 d6`

Si le joueur joue autre chose avant, la prise en passant n'est plus autorisee.

## Capture classique

Une capture classique est effectuee quand une piece arrive sur une case occupee par une piece adverse. Le moteur refuse toujours la capture d'une piece alliee.
