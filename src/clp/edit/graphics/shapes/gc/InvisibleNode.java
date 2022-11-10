package clp.edit.graphics.shapes.gc;

import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AInitialShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class InvisibleNode extends AInitialShape {

  private static final long serialVersionUID = -8465444795125064982L;

  public InvisibleNode() {
    super(60, -30, "");
    setXoffset(200);
  }

  public void paintShape(Graphics g, int offset) {
    super.checkDownType();
    if (getChild() != null) {
      getChild().paintShape(g, offset);
    }
    if (getSibling() != null) {
      getSibling().paintShape(g, offset);
    }
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    AShape s = super.getSelectedShape(x, y);
    if (s == null && getSibling() != null) {
      return getSibling().getSelectedShape(x, y);
    }
    return s;
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    BindingShape b = new BindingShape(bindingType, null);
    setChild(b);
    b.setChild(shape);
  }

  @Override
  public int getPX() {
    return getXoffset();
  }

  @Override
  public int getPY() {
    return 20 + getYoffset();
  }

  @Override
  public ADialog getDialog() {
    return null;
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    return null;
  }
}
