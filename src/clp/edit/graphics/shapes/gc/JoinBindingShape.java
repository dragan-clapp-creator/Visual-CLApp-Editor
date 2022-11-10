package clp.edit.graphics.shapes.gc;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.TransitionDialog;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AJoinShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.TriBinding;

public class JoinBindingShape extends AJoinShape {

  private static final long serialVersionUID = 3969967774023695547L;

  private ABindingShape lmb;    // leftmost binding shape
  private Integer leftmost;
  private Integer rightmost;

  private BindingType btype;

  transient private TransitionDialog dialog;

  private GrafcetShape grafcetShape;


  /**
   * CONSTRUCTOR
   * 
   * @param gs grafcetShape
   */
  public JoinBindingShape(GrafcetShape gs) {
    super(0, 6, "", gs.getDefaultTransitionType());
    this.grafcetShape = gs;
    dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    setName(dialog.getTransitionText());
  }

  public void paintShape(Graphics g, int offset) {
    int sx = getPX() + offset;
    int oy = getPY()-3;
    Color c = g.getColor();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    int ox;
    if (getParents().size() < 2) {
      g.fillRect(sx-30, oy, 60, 3);
    }
    else {
      int w = rightmost - leftmost;
      ox = offset+leftmost-10;
      oy += 4;
      g.drawLine(ox, oy, ox+w+20, oy);
      oy += 3;
      g.drawLine(ox, oy, ox+w+20, oy);
      g.fillRect(sx-20, oy+9, 40, 4);
    }
    g.setColor(c);
    if (getName() != null) {
      g.drawString(getName(), sx+23, oy+15);
    }
    if (getChild() != null) {
      getChild().paintShape(g, offset);
    }
  }

  //
  private void defineLimits() {
    leftmost = null;
    rightmost = null;
    for (ABindingShape b : getParents()) {
      int x = b.getParent().getPX();
      if (b.getParent().getChild() instanceof TriBinding) {
        TriBinding tri = (TriBinding) b.getParent().getChild();
        if (b == tri.getLeft()) {
          x -= 20;
        }
        else {
          x += 20;
        }
      }
      if (leftmost == null || x < leftmost) {
        leftmost = x;
        lmb = b;
      }
      if (rightmost == null || x > rightmost) {
        rightmost = x;
      }
    }
  }

  @Override
  public int getPX() {
    defineLimits();
    int w = 20;
    if (leftmost == null || rightmost == null) {
      leftmost = getParent().getX1() - 10;
    }
    else {
      w = rightmost - leftmost;
    }
    return leftmost + w/2;
  }

  public int getLeftMost() {
    defineLimits();
    return leftmost;
  }

  public int getRightMost() {
    defineLimits();
    return rightmost;
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    int ref_x = getPX();
    int ref_y = getPY();
    if (leftmost != null && rightmost != null) {
      int w_2 = (rightmost - leftmost)/2 + 10;
      if (x > ref_x-w_2 && x < ref_x+w_2
       && y > ref_y-10 && y < ref_y+10) {

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
    b.setOnlyChild(this);
    if (shape.getParent() != null) {
      BindingShape sch = (BindingShape) shape.getChild();
      if (sch == null) {
        shape.setChild(b);
      }
      else {
        b.setParent(shape);
        TriBinding tri;
        if (sch instanceof TriBinding) {
          tri = (TriBinding) sch;
          tri.addChild(b);
          b.setOnlyChild(this);
        }
        else {
          tri = new TriBinding();
          tri.addChild(sch);
          sch.setBindingType(BindingType.DOWN_LEFT);
          tri.addChild(b);
          shape.setChild(tri);
        }
        b.setBindingType(BindingType.DOWN_RIGHT);
      }
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
    StepNodeShape shape = new StepNodeShape(GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForGrafcet());
    EventNodeShape event = new EventNodeShape(grafcetShape, grafcetShape.getDefaultTransitionType());
    BindingShape be = new BindingShape(BindingType.DOWN, "");
    event.setChild(be);
    be.setChild(shape);
    BindingShape bs = new BindingShape(BindingType.DOWN, "");
    shape.setChild(bs);
    bs.setOnlyChild(this);
    defineLimits();
    int y = getPY() - 100;
    if (y > 20) {
      y -= 20;
    }
    else {
      addToY(20);
    }
    int x;
    if (btype == BindingType.UP_LEFT) {
      x = leftmost - 160;
    }
    else {
      x = rightmost + 160;
    }
    event.setXoffset(x);
    event.setYoffset(y);
    shape.setYoffset(event.getHeight()+5);
    getParents().add(bs);
    return event;
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    }
    return dialog;
  }

  public TransitionDialog getCustDialog() {
    return (TransitionDialog) getDialog();
  }

  @Override
 public String getActivationCondition(ABindingShape bs) {
    if (getParents().size() > 1) {
      AShape p = getParents().get(0).getParent();
      String s = getCustDialog().getActivationCondition(p.getName(), 'X', p);
      for (int i=1; i<getParents().size(); i++) {
        p = getParents().get(i).getParent();
        s += " AND " + getCustDialog().getActivationCondition(p.getName(), 'X', p);
      }
      return s;
    }
    return null;
  }
}
