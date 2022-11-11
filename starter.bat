@echo off

SET EDIT_JAR="<your_path>/editor/VisualCLAppEditor.jar"
SET CLAPP_JAR="<your_path>/lib/CLApp.jar"
SET BCEL_JAR="<your_path>/lib/bcel-5.2.jar"

java -cp "$EDIT_JAR;$CLAPP_JAR;$BCEL_JAR" clp.edit.CLAppEditor
