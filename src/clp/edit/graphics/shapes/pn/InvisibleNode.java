package clp.edit.graphics.shapes.pn;

import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.AInitialShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;

public class InvisibleNode extends AInitialShape {

  private static final long serialVersionUID = -3655592197201104712L;

  public InvisibleNode() {
    super(0, -30, "");
    setXoffset(200);
  }

  public void paintShape(Graphics g, int offset) {
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
    PNBindingShape b = new PNBindingShape(bindingType, null);
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
  public void cacheFromTransients() {
  }
}
