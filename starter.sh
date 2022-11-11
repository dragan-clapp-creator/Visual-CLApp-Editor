#!/bin/bash

export EDIT_JAR="<your_path>/editor/VisualCLAppEditor.jar"
export CLAPP_JAR="<your_path>/lib/CLApp.jar"
export BCEL_JAR="<your_path>/lib/bcel-5.2.jar"

java -cp "$EDIT_JAR:$CLAPP_JAR:$BCEL_JAR" clp.edit.CLAppEditor
