#!/bin/bash
# build and run the vibematch desktop app
# usage: ./run.sh

# compile everything under desktop/ into build/
mkdir -p build
echo "compiling..."
javac -cp "desktop/lib/*" -d build $(find desktop -name "*.java")
if [ $? -ne 0 ]; then
    echo "compile failed, fix the errors above"
    exit 1
fi

echo "starting vibematch..."
java -cp "build:desktop/lib/*" app.Main
