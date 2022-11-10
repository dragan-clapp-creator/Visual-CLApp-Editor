package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import clp.edit.graphics.shapes.gc.JoinBindingShape;
import clp.edit.graphics.shapes.gc.TransitionNodeShape;

public class BindingShape extends ABindingShape {

  private static final long serialVersionUID = 1436254108715557862L;

  private Color color;

  public BindingShape(BindingType b, String t) {
    super(t, b);
    color = Color.black;
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    paintShape(getParent(), g, offset);
  }

  @Override
  public void paintShape(AShape parent, Graphics g, int offset) {
    Color c = g.getColor();
    g.setColor(getColor());
    int py = parent.getPY()+parent.getHeight();
    int cy = getChildY();
    switch (getBindingType()) {
      case DOWN:
        int x = getX1() + offset;
        g.drawLine(x, py, x, cy);
        break;
      case DOWN_MIDDLE:
        int x1 = getX1() + offset;
        int x2 = getX2() + offset;
        int delta = x2 - x1;
        if (delta > 0 && delta < 4 || delta < 0 && delta > -4) {
          setConditionnallyBindingType(BindingType.DOWN);
          g.drawLine(x1, py, x1, cy);
          getChild().addToX(-delta);
        }
        else {
          int middle = (cy-py)/2;
          g.drawLine(x1, py, x1, py+middle);
          g.drawLine(x1, py+middle, x2, py+middle);
          g.drawLine(x2, py+middle, x2, cy);
        }
        break;
      case DOWN_LEFT:
        x1 = getX1() + offset;
        if (getChild() instanceof JoinBindingShape) {
          x2 = ((JoinBindingShape)getChild()).getRightMost() - 10 + offset;
        }
        else {
          x2 = getX2() + offset;
        }
        int y = py-10;
        g.drawLine(x1, y, x2, y);
        g.drawLine(x2, y, x2, cy);
        if (getText() != null) {
          g.drawString(getText(), x2+5, y-3);
        }
        break;
      case DOWN_RIGHT:
        x1 = getX1() + offset;
        if (getChild() instanceof JoinBindingShape) {
          x2 = ((JoinBindingShape)getChild()).getLeftMost() + 10 + offset;
        }
        else {
          x2 = getX2() + offset;
        }
        y = py-10;
        g.drawLine(x1, y, x2, y);
        g.drawLine(x2, y, x2, cy);
        if (getText() != null) {
          g.drawString(getText(), x1+5, y-3);
        }
        break;
      case UP_LEFT:
        int xi = getX1() + offset;
        int yi = py - parent.getHeight()/2;
        int xl;
        int xf = getX2() - getChild().getWidth()/2 + offset;
        int yf = cy + getChild().getHeight()/2;
        if (parent instanceof TransitionNodeShape) {
          xl = parent.getPX() + offset;
        }
        else {
          xl = xf + getXmiddle() - 10;
        }
        g.drawLine(xi, yi, xl, yi);
        g.drawLine(xl, yi, xl, yf);
        g.drawLine(xl, yf, xf, yf);
        g.drawLine(xf-5, yf-5, xf, yf);
        g.drawLine(xf-5, yf+5, xf, yf);
        if (getText() != null) {
          g.drawString(getText(), xl+2, yi-3);
        }
        break;
      case UP_LEFT_RIGHT:
        if (getChild() instanceof ActionShape) {
          xf = ((ActionShape)getChild()).getRightPart() + offset;
        }
        else {
          xf = getX2() + getChild().getWidth()/2 + offset;
        }
        yf = cy + getChild().getHeight()/2;
        xi = getX1() + offset;
        yi = py - parent.getHeight()/2;
        if (parent instanceof TransitionNodeShape) {
          xi -= parent.getWidth()/2;
          g.drawLine(xi, yi, xi, yf);
          g.drawLine(xi, yf, xf, yf);
        }
        else {
          int xr = xf + getXmiddle() + 10;
          g.drawLine(xi, yi, xr, yi);
          g.drawLine(xr, yi, xr, yf);
          g.drawLine(xr, yf, xf, yf);
        }
        g.drawLine(xf+5, yf+5, xf, yf);
        g.drawLine(xf+5, yf-5, xf, yf);
        if (getText() != null) {
          g.drawString(getText(), xf+2, yi-3);
        }
        break;
      case UP_LEFT_MIDDLE:
        x1 = getX1() + offset;
        x2 = getX2() + offset;
        int y1 = py;
        cy = getChildY();
        int y2 = cy > y1 ? cy+15 : y1+15;
        g.drawLine(x1, y1, x1, y2);
        g.drawLine(x1, y2, x2, y2);
        g.drawLine(x2, y2, x2, cy);
        if (getText() != null) {
          g.drawString(getText(), x2+5, y2-3);
        }
        break;
      case UP_RIGHT:
        if (getChild() instanceof ActionShape) {
          xf = ((ActionShape)getChild()).getRightPart() + offset;
        }
        else {
          xf = getX2() + getChild().getWidth()/2 + offset;
        }
        yf = cy + getChild().getHeight()/2;
        xi = getX1() + offset;
        yi = py - parent.getHeight()/2;
        if (parent instanceof TransitionNodeShape) {
          xi -= parent.getWidth()/2;
          g.drawLine(xi, yi, xi, yf);
          g.drawLine(xi, yf, xf, yf);
        }
        else {
          int xr = xf + getXmiddle() + 10;
          g.drawLine(xi, yi, xr, yi);
          g.drawLine(xr, yi, xr, yf);
          g.drawLine(xr, yf, xf, yf);
        }
        g.drawLine(xf+5, yf+5, xf, yf);
        g.drawLine(xf+5, yf-5, xf, yf);
        if (getText() != null) {
          g.drawString(getText(), xi+2, yi-3);
        }
        break;
      case UP_RIGHT_LEFT:
        xi = getX1() + offset;
        yi = py - parent.getHeight()/2;
        xf = getX2() - getChild().getWidth()/2 + offset;
        yf = cy + getChild().getHeight()/2;
        if (parent instanceof TransitionNodeShape) {
          xl = parent.getPX() + offset;
        }
        else {
          xl = xf + getXmiddle() - 10;
        }
        g.drawLine(xi, yi, xl, yi);
        g.drawLine(xl, yi, xl, yf);
        g.drawLine(xl, yf, xf, yf);
        g.drawLine(xf-5, yf-5, xf, yf);
        g.drawLine(xf-5, yf+5, xf, yf);
        if (getText() != null) {
          g.drawString(getText(), xi+2, yi-3);
        }
        break;
      default:
        break;
    }
    g.setColor(c);
    if (getChild() != null && getBindingType() != BindingType.UP_LEFT && getBindingType() != BindingType.UP_RIGHT
                           && getBindingType() != BindingType.UP_LEFT_RIGHT && getBindingType() != BindingType.UP_RIGHT_LEFT) {
      getChild().paintShape(g, offset);
    }
  }

  public BindingType getDirection() {
    return getBindingType();
  }

  /**
   * @return the color
   */
  public Color getColor() {
    return color;
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    if (getChild() != null && getBindingType() != BindingType.UP_LEFT && getBindingType() != BindingType.UP_RIGHT) {
      return getChild().getSelectedShape(x, y);
    }
    return null;
  }

  @Override
  public void gatherChildrenShapes(List<AShape> list) {
    if (getChild() != null && isBindingTypeAllowed(getBindingType())) {
      if (getChild().getChild() != null) {
        getChild().gatherChildrenShapes(list);
      }
      else {
        list.add(getChild());
      }
    }
  }

  //
  private boolean isBindingTypeAllowed(BindingType t) {
    return t == BindingType.DOWN || t == BindingType.DOWN_LEFT ||
           t == BindingType.DOWN_MIDDLE || t == BindingType.DOWN_RIGHT;
  }

  @Override
  public void clearChildrenEntries() {
    clearEntries(getChild());
  }

  public void clearEntries(AShape child) {
    if (child instanceof ATransitionShape) {
      if (child.getChild() != null) {
        clearEntries(child.getChild().getChild());
      }
    }
    else if (child instanceof ActionShape) {
      ((ActionShape)child).getEntries().clear();
    }
  }
}
