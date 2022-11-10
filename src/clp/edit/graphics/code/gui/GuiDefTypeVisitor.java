package clp.edit.graphics.code.gui;

import clp.edit.graphics.code.gui.elts.GUILine;
import clp.run.res.ui.UiButton;
import clp.run.res.ui.UiDefTypeVisitor;
import clp.run.res.ui.UiInputField;
import clp.run.res.ui.UiLabel;
import clp.run.res.ui.UiTable;
import clp.run.res.ui.UiTextArea;

public class GuiDefTypeVisitor implements UiDefTypeVisitor {

  private GUILine guiLine;

  public GuiDefTypeVisitor(GUILine guiLine) {
    this.guiLine = guiLine;
  }

  @Override
  public void visitUiLabel(UiLabel x) {
    guiLine.addLabel(x);
  }

  @Override
  public void visitUiInputField(UiInputField x) {
    guiLine.addInputField(x);
  }

  @Override
  public void visitUiTextArea(UiTextArea x) {
    guiLine.addTextArea(x);
  }

  @Override
  public void visitUiTable(UiTable x) {
    guiLine.addTable(x);
  }

  @Override
  public void visitUiButton(UiButton x) {
    guiLine.addButton(x);
  }

}
