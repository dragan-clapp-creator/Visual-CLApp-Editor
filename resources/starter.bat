@echo off

set TITLE="<TITLE>"
set CRYPTER=<CRYPTER>

set LAUNCH_BIN="<BIN>"
set CLAPP_JAR="<CLIB>"

java -cp "$LAUNCH_BIN;$CLAPP_JAR" clapp.start.Launcher "$TITLE" $CRYPTER
