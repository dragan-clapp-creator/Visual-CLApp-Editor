package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AInitialShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;

public class InitialNodeShape extends AInitialShape {

  private static final long serialVersionUID = -1951022843974693067L;

  public InitialNodeShape() {
    super(20, 20, "Initial node");
    setXoffset(200);
  }

  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    if (isSelected() || getChild() == null) {
      g.setColor(Color.lightGray);
    }
    g.fillOval(getPX()+offset-10, getPY(), 20, 20);
    g.setColor(c);
    if (getChild() != null) {
      super.checkDownType();
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
    getChild().setChild(shape);
  }

  @Override
  public int getPX() {
    return getXoffset();
  }

  @Override
  public int getPY() {
    return 50 + getYoffset();
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
