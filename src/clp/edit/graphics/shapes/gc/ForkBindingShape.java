package clp.edit.graphics.shapes.gc;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.TransitionDialog;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AForkShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.RedBindingShape;

public class ForkBindingShape extends AForkShape {

  private static final long serialVersionUID = -3728721663025397833L;

  transient private TransitionDialog dialog;

  public ForkBindingShape(GrafcetShape grafcetShape, TransitionType tt) {
    super(0, 6, "", tt);
    dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    setName(dialog.getTransitionText());
  }

  public void paintShape(Graphics g, int offset) {
    int ox = getPX()+offset;
    int oy = getPY();
    if (!getDestinations().isEmpty()) {
      int nb = getDestinations().size() < 2 ? 2 : getDestinations().size();
      int w = 160*(nb-1);
      int dx = ox - w/2 - 10;
      int dy = oy-3;
      Color c = g.getColor();
      if (isSelected()) {
        g.setColor(Color.lightGray);
      }
      g.fillRect(ox-20, dy-4, 40, 4);
      dy += 5;
      g.drawLine(dx, dy, dx+w+20, dy);
      dy += 3;
      g.drawLine(dx, dy, dx+w+20, dy);
      g.setColor(c);
      if (getName() != null) {
        g.drawString(getName(), ox+23, dy-6);
      }
      dx += 10;
      for (ABindingShape b : getDestinations()) {
        setBindingType(b, dx, offset);
        drawBinding(g, b, dx, offset);
        dx += 160;
      }
    }
    else {
      g.fillRect(ox-30, oy, 60, 3);
    }
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

  //
  private void setBindingType(ABindingShape b, int dx, int offset) {
    int delta = b.getChild().getPX() + offset - dx;
    if (delta > 0 && delta < 4 || delta < 0 && delta > -4) {
      b.setConditionnallyBindingType(BindingType.DOWN);
      b.setXshift(0);
    }
    else {
      if (b.setConditionnallyBindingType(BindingType.DOWN_MIDDLE)) {
        b.setXshift(delta);
      }
    }
  }

  //
  private void drawBinding(Graphics g, ABindingShape b, int dx, int offset) {
    int py = b.getParentY();
    int cy = b.getChildY();
    switch (b.getBindingType()) {
      case DOWN:
        g.drawLine(dx, py, dx, cy);
        b.getChild().paintShape(g, offset);
        break;
      case DOWN_MIDDLE:
        int x1 = dx;
        int x2 = dx + b.getXshift();
        int middle = (cy-py)/2;
        g.drawLine(x1, py, x1, py+middle);
        g.drawLine(x1, py+middle, x2, py+middle);
        g.drawLine(x2, py+middle, x2, cy);
        b.getChild().paintShape(g, offset);
        break;
      case UP_LEFT:
      case UP_RIGHT:
        int xf = b.getX2() + b.getChild().getWidth()/2 + offset;
        int yf = cy + b.getChild().getHeight()/2;
        int xi = b.getX1() + b.getXshift() + offset;
        int yi = py - getParent().getHeight()/2;
        int xr = xi + 30;
        g.drawLine(xi, yi, xi, yi+30);
        g.drawLine(xi, yi+30, xr, yi+30);
        g.drawLine(xr, yi+30, xr, yf);
        g.drawLine(xr, yf, xf, yf);
        g.drawLine(xf+5, yf+5, xf, yf);
        g.drawLine(xf+5, yf-5, xf, yf);
        if (b.getText() != null) {
          g.drawString(b.getText(), xi+2, yi-3);
        }
        break;

      default:
        break;
    }
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    int ref_x = getPX();
    int ref_y = getPY();
    int nb = getDestinations().size() < 2 ? 2 : getDestinations().size();
    int w_2 = 80*(nb-1)+10;
    if (x > ref_x-w_2 && x < ref_x+w_2
        && y > ref_y-10 && y < ref_y+10) {

      return this;
    }
    for (ABindingShape bs : getDestinations()) {
      AShape s = bs.getSelectedShape(x, y);
      if (s != null) {
        return s;
      }
    }
    return null;
  }

  @Override
  public void gatherChildrenShapes(List<AShape> list) {
    list.add(this);
    for (ABindingShape bs : getDestinations()) {
      if (bs.getBindingType() != BindingType.UP_LEFT && bs.getBindingType() != BindingType.UP_RIGHT) {
        bs.getChild().gatherChildrenShapes(list);
      }
    }
  }

  @Override
  public void setChild(ABindingShape shape) {
  }

  public void reverseBindingType(RedBindingShape redShape) {
    if (getDestinations().isEmpty()) {
      redShape.setConditionnallyBindingType(BindingType.UP_LEFT);
    }
    else {
      redShape.setConditionnallyBindingType(BindingType.UP_RIGHT);
    }
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    return getCustDialog().getActivationCondition(getParent().getParent().getName(), 'X', getParent().getParent());
  }
}
