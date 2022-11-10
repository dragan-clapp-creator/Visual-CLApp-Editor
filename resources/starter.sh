#!/bin/bash

export TITLE="<TITLE>"
export CRYPTER=<CRYPTER>

export LAUNCH_BIN="<BIN>"
export CLAPP_JAR="<CLIB>"

java -cp "$LAUNCH_BIN:$CLAPP_JAR" clapp.start.Launcher "$TITLE" $CRYPTER
