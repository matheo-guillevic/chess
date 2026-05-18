#!/bin/bash

# Se placer dans le répertoire contenant le script (racine du projet)
cd "$(dirname "$0")"

# Créer le dossier "out" s'il n'existe pas
mkdir -p out
mkdir -p out/resources

# Compiler tous les fichiers Java de "src" vers "out"
echo "⚙️ Compilation en cours..."
javac -cp "lib/*" -d out $(find src -name "*.java")

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    if [ -d resources ]; then
        cp -R resources/. out/resources/
    fi
    echo "✅ Compilation terminée avec succès."
    echo "🚀 Lancement du jeu..."
    echo "=========================================="
    # Exécuter la classe Main avec "out" et "lib/*" comme classpath
    java -cp "out:out/resources:lib/*" Main --cli "$@"
else
    echo "❌ Erreur de compilation."
fi
