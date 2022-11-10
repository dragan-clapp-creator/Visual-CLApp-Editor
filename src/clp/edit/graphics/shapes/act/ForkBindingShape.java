package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AForkShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;

public class ForkBindingShape extends AForkShape {

  private static final long serialVersionUID = -5403718943662231267L;

  public ForkBindingShape() {
    super(0, 0, "", null);
  }

  public void paintShape(Graphics g, int offset) {
    int ox = getPX()+offset;
    int oy = getPY();
    if (!getDestinations().isEmpty()) {
      int w = getDestinations().size() < 2 ? 160 : getWidth();
      int dx = ox - w/2 - 10;
      int dy = oy-3;
      Color c = g.getColor();
      if (isSelected()) {
        g.setColor(Color.lightGray);
      }
      g.fillRect(dx, dy, w+20, 3);
      g.setColor(c);
      dx += 10;
      int step = getDestinations().size() < 2 ? w / 2 : w / (getDestinations().size()-1);
      for (ABindingShape b : getDestinations()) {
        setBindingType(b, dx, offset);
        drawBinding(g, b, dx, offset);
        dx += step;
      }
    }
    else {
      g.fillRect(ox-30, oy, 60, 3);
    }
  }

  //
  private void setBindingType(ABindingShape b, int dx, int offset) {
    int delta = b.getChild().getPX() + offset - dx;
    if (delta > 0 && delta < 4 || delta < 0 && delta > -4) {
      b.setConditionnallyBindingType(BindingType.DOWN);
      b.setXshift(0);
    }
    else {
      b.setConditionnallyBindingType(BindingType.DOWN_MIDDLE);
      b.setXshift(delta);
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
        break;
      case UP_RIGHT:
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
        && y > ref_y-3 && y < ref_y+3) {

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
      bs.getChild().gatherChildrenShapes(list);
    }
  }

  @Override
  public void setChild(ABindingShape shape) {
  }
}
