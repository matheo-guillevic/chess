#!/bin/bash

cd "$(dirname "$0")"

mkdir -p out/test

echo "Compilation du projet et des tests..."
javac -cp "lib/*" -d out/test $(find src test -name "*.java")

if [ $? -ne 0 ]; then
    echo "Erreur de compilation des tests."
    exit 1
fi

echo "Lancement des tests..."
java -cp "out/test:lib/*" ChessEngineTest
