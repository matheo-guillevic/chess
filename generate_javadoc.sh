#!/bin/bash

cd "$(dirname "$0")"

mkdir -p doc/javadoc

echo "Generation de la Javadoc..."
javadoc -quiet -d doc/javadoc $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Javadoc generee dans doc/javadoc/index.html"
else
    echo "Erreur pendant la generation de la Javadoc."
    exit 1
fi
