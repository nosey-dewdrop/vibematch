#!/bin/bash
# build and run the vibematch desktop CLIENT
# start the server first (./run-server.sh) in another terminal, then run this.
#
# to connect to a server on another computer, pass its address:
#   ./run.sh 192.168.1.20

mkdir -p build
echo "compiling..."
javac -cp "desktop/lib/*" -d build $(find desktop -name "*.java")
if [ $? -ne 0 ]; then
    echo "compile failed, fix the errors above"
    exit 1
fi

echo "starting vibematch..."
java -cp "build:desktop/lib/*" app.Main "$@"
