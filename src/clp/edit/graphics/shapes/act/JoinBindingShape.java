package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AJoinShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class JoinBindingShape extends AJoinShape {

  private static final long serialVersionUID = 7173833530898590776L;

  private ABindingShape lmb;    // leftmost binding shape

  private BindingType btype;

  private int py;

  /**
   * CONSTRUCTOR
   */
  public JoinBindingShape() {
    super(0, 0, "", null);
  }

  public void paintShape(Graphics g, int offset) {
    int ox = getPX() + offset;
    int oy = getPY()-3;
    Color c = g.getColor();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    if (getParents().size() < 2) {
      g.fillRect(ox-30, oy, 60, 3);
    }
    else {
      int w = getRightmost() - getLeftmost();
      g.fillRect(offset+getLeftmost()-10, oy, w+20, 3);
    }
    g.setColor(c);
    if (getChild() != null) {
      getChild().paintShape(g, offset);
    }
  }

  //
  private void defineLimits() {
    Integer lmost = null;
    Integer rmost = null;
    for (ABindingShape b : getParents()) {
      int x = b.getParent().getPX();
      if (lmost == null || x < lmost) {
        lmost = x;
        lmb = b;
      }
      if (rmost == null || x > rmost) {
        rmost = x;
      }
    }
    setLeftmost(lmost);
    setRightmost(rmost);
  }

  @Override
  public int getPX() {
    defineLimits();
    int w = 20;
    if (getLeftmost() == null || getRightmost() == null) {
      setLeftmost(getParent().getX1() - 10);
    }
    else {
      w = getWidth();
    }
    return getLeftmost() + w/2;
  }

  @Override
  public int getPY() {
    if (py == 0) {
      py = super.getPY();
      setYoffset(0);
    }
    return py + getYoffset();
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    int ref_x = getPX();
    int ref_y = getPY();
    if (getLeftmost() != null && getRightmost() != null) {
      int w_2 = getWidth()/2 + 10;
      if (x > ref_x-w_2 && x < ref_x+w_2
       && y > ref_y-3 && y < ref_y+3) {

        return this;
      }
      if (getChild() != null) {
        return getChild().getSelectedShape(x, y);
      }
    }
    return null;
  }

  @Override
  public void addToX(int delta) {
    setXoffset(getXoffset()+delta);
  }

  @Override
  public void setChild(ABindingShape shape) {
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    if (lmb == null) {
      lmb = getParent();
      getParents().add(lmb);
    }
    BindingShape b = new BindingShape(bindingType, "");
    if (shape.getParent() != null) {
      shape.setChild(b);
      b.setOnlyChild(this);
      getParents().add(b);
    }
    else {
      super.setChild(b);
      b.setChild(shape);
    }
  }

  public BindingType getBindingType() {
    return btype;
  }

  public void setBindingType(BindingType t) {
    btype = t;
  }

  public EventNodeShape addEventAbove() {
    if (lmb == null) {
      lmb = getParent();
      getParents().add(lmb);
    }
    ActionNodeShape shape = new ActionNodeShape(GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForActigram());
    EventNodeShape event = new EventNodeShape();
    BindingShape be = new BindingShape(BindingType.DOWN, "");
    event.setChild(be);
    be.setChild(shape);
    BindingShape bs = new BindingShape(BindingType.DOWN, "");
    shape.setChild(bs);
    bs.setOnlyChild(this);
    defineLimits();
    int y = getPY() - 160;
    int x;
    if (btype == BindingType.UP_LEFT) {
      x = getLeftmost() - 160;
    }
    else {
      x = getRightmost() + 160;
    }
    event.setXoffset(x);
    event.setYoffset(y);
    shape.setYoffset(event.getHeight()+30);
    getParents().add(bs);
    return event;
  }
}
