#!/bin/bash

export CLAPP_SRC="<SRC>"

export CLAPP_JAR="<CLIB>"
export BCEL_JAR="<BLIB>"

java -cp "$CLAPP_JAR:$BCEL_JAR" clapp.run.Supervisor "$CLAPP_SRC"
