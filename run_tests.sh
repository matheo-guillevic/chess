#!/bin/bash

cd "$(dirname "$0")"

mkdir -p out/test
mkdir -p out/test-resources

echo "Compilation du projet et des tests..."
javac -cp "lib/*" -d out/test $(find src test -name "*.java")

if [ $? -ne 0 ]; then
    echo "Erreur de compilation des tests."
    exit 1
fi

if [ -d resources ]; then
    cp -R resources/. out/test-resources/
fi

echo "Lancement des tests..."
java -cp "out/test:out/test-resources:lib/*" ChessEngineTest
