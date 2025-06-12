# Puissance X - Tests Unitaires

Ce document explique comment configurer et lancer les tests unitaires pour le projet Puissance X.

## Prérequis

- Java JDK 17 ou supérieur
- Maven 3.6 ou supérieur
- IntelliJ IDEA (recommandé)

## Structure du Projet

```
graphique/
├── src/
│   └── main/
│       └── java/
│           ├── model/
│           ├── view/
│           └── control/
└── test/
    └── java/
        ├── model/
        ├── view/
        └── control/
```

## Configuration

1. Assurez-vous que le JDK est correctement configuré dans votre IDE
2. Importez le projet comme un projet Maven
3. Maven téléchargera automatiquement les dépendances nécessaires :
   - JUnit 5.8.2
   - Mockito 4.5.1
   - JavaFX 17.0.1

## Lancer les Tests

### Avec Maven (en ligne de commande)

```bash
mvn test
```

### Avec IntelliJ IDEA

1. Ouvrez le projet dans IntelliJ IDEA
2. Faites un clic droit sur le dossier `test`
3. Sélectionnez "Run 'All Tests'"

Ou pour lancer un test spécifique :
1. Ouvrez le fichier de test
2. Cliquez sur l'icône verte "Run" à côté de la classe ou de la méthode de test

## Tests Disponibles

### Model
- `PuissanceXBoardTest` : Tests du plateau de jeu
- `PuissanceXStageModelTest` : Tests du modèle de jeu
- `PuissanceXPawnPotTest` : Tests des pions

### Control
- `GameControllerTest` : Tests du contrôleur de jeu

### View
- `GameViewTest` : Tests de la vue principale
- `UIComponentsTest` : Tests des composants d'interface

## Couverture des Tests

Les tests couvrent les aspects suivants :
- Initialisation du jeu
- Placement des pions
- Détection des victoires (horizontale, verticale, diagonale)
- Gestion des tours de jeu
- Validation des coups
- Match nul
- Interface utilisateur

## Dépannage

Si vous rencontrez des erreurs :

1. Vérifiez que Maven a bien téléchargé toutes les dépendances :
   ```bash
   mvn clean install
   ```

2. Assurez-vous que le JDK est correctement configuré :
   ```bash
   java -version
   mvn -version
   ```

3. Invalidez les caches d'IntelliJ et redémarrez :
   - File > Invalidate Caches > Invalidate and Restart 