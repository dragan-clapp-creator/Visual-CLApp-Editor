package clp.edit.graphics.code.gui;

import clp.edit.graphics.code.gui.elts.AguiNode;
import clp.run.res.ui.UiBundleVisitor;
import clp.run.res.ui.UiGroup;
import clp.run.res.ui.UiLine;

public class GuiBundleVisitor implements UiBundleVisitor {

  private AguiNode node;

  public GuiBundleVisitor(AguiNode node) {
    this.node = node;
  }

  @Override
  public void visitUiGroup(UiGroup x) {
    node.addGroup(x);
  }

  @Override
  public void visitUiLine(UiLine x) {
    node.addLine(x);
  }

}
