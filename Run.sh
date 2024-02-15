#!/bin/sh

CLASSPATH="./lib/json-smart-2.5.0.jar:./lib/jsonrpc2-client-2.1.1.jar:./lib/jsonrpc2-base-2.1.1.jar:./lib/asm-1.0.2.jar"

java -cp $CLASSPATH:./build i2pm $@
