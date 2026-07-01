#!/bin/bash
# build and run the vibematch SERVER
# this is the one you start first, and the one that would live on the cloud
# machine. it holds the shared database everybody talks to.

mkdir -p build
echo "compiling..."
javac -cp "desktop/lib/*" -d build $(find desktop -name "*.java")
if [ $? -ne 0 ]; then
    echo "compile failed, fix the errors above"
    exit 1
fi

echo "starting vibematch server..."
java -cp "build:desktop/lib/*" server.ServerMain
