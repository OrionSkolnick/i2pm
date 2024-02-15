#!/bin/sh

CLASSPATH="./lib/json-smart-2.5.0.jar:./lib/jsonrpc2-client-2.1.1.jar:./lib/jsonrpc2-base-2.1.1.jar:./lib/asm-1.0.2.jar"

rm ./build
printf "Building..\n\n"
mkdir build

printf "Classpath: $CLASSPATH\n\n"
printf "Compiling..\n\n"

javac -cp $CLASSPATH -d ./build/ ./src/*.java

printf "Running..\n\n"
sh Run.sh $@
