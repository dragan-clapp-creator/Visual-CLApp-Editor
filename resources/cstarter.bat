@echo off

SET CLAPP_SRC="<SRC>"

SET CLAPP_JAR="<CLIB>"
SET BCEL_JAR="<BLIB>"

java -cp "$CLAPP_JAR;$BCEL_JAR" clapp.run.Supervisor "$CLAPP_SRC"
