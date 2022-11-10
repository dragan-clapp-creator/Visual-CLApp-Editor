package clp.edit.graphics.shapes.pn;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class PNBindingShape extends BindingShape {

  private static final long serialVersionUID = 3724081949514024832L;

  private Color color;

  public PNBindingShape(BindingType b, String t) {
    super(b, t);
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
        drawArrowDown(g, x, cy);
        drawText(g, x+2, (py+cy)/2);
        break;
      case DOWN_LEFT:
      case DOWN_RIGHT:
        int px = parent.getPX() + offset;
        int cx = getX2() + offset;
        drawBindings(parent, g, px, cx, py, cy);
        break;
      case DOWN_MIDDLE:
        px = getX1() + offset;
        cx = getX2() + offset;
        int xdelta = cx - px;
        if (xdelta > 0 && xdelta < 4 || xdelta < 0 && xdelta > -4) {
          setBindingType(BindingType.DOWN);
        }
        else {
          drawBindings(parent, g, px, cx, py, cy);
        }
        break;
      case UP_LEFT:
      case UP_RIGHT:
        px = parent.getPX() + offset;
        cx = getX2() + offset;
        drawBindings(parent, g, px, cx, py, cy);
        break;
      default:
        break;
    }
    g.setColor(c);
    if (getChild() != null && getBindingType() != BindingType.UP_LEFT && getBindingType() != BindingType.UP_RIGHT) {
      getChild().paintShape(g, offset);
    }
  }

  //
  private void drawBindings(AShape parent, Graphics g, int px, int cx, int py, int cy) {
    int xdelta = cx - px;
    if (xdelta > 0 && xdelta < 4 || xdelta < 0 && xdelta > -4) {
      g.drawLine(px, py, px, cy);
      drawArrowDown(g, px, cy);
      drawText(g, px+2, (py+cy)/2);
      getChild().addToX(-xdelta);
    }
    else {
      int littleShift = 0;
      if (parent instanceof PlaceNodeShape) {
        if (getChild().getParent() != this) {
          littleShift = xdelta > 0 ? -9 : +9;
        }
        drawFromPlaceToTransition(parent, g, px, cx+littleShift, parent.getPY(), cy);
      }
      else {
        if (parent.getChild() != this) {
          littleShift = xdelta < 0 ? -9 : +9;
        }
        drawFromTransitionToPlace(parent, g, px+littleShift, cx, parent.getPY(), cy);
      }
    }
  }

  //
  private void drawFromPlaceToTransition(AShape parent, Graphics g, int px, int cx, int py, int cy) {
    int pw = parent.getWidth();
    int ph = parent.getHeight();
    int ch = getChild().getHeight();
    int pw_2 = pw/2;
    if (cy < py) {  // Transition over Place
      if (cx < px) {    // Transition left from Place
        int dx = px - pw_2 - cx;
        int dy = py + ph/2 - cy - ch/2;
        g.drawArc(cx, cy+ch-dy, dx*2, dy*2, 180, 90);
        drawText(g, cx+4, (py+cy)/2+2);
      }
      else {            // Transition right from Place
        int dx = cx - px - pw_2;
        int dy = py + ph/2 - cy - ch/2;
        g.drawArc(px+pw_2-dx, cy+ch-dy, dx*2, dy*2, 0, -90);
        drawText(g, cx+2, (py+cy)/2+2);
      }
      drawArrowUp(g, cx, cy+ch);
    }
    else {          // Transition under Place
      if (cx < px) {    // Transition left from Place
        int dx = px - pw_2 - cx;
        int dy = cy - py - ph/2;
        g.drawArc(cx, py+ph/2, dx*2, dy*2, 90, 90);
        drawText(g, cx+4, (py+cy)/2+8);
      }
      else {            // Transition right from Place
        int dx = cx - px - pw_2;
        int dy = cy - py - ph/2;
        g.drawArc(px+pw_2-dx, py+ph/2, dx*2, dy*2, 0, 90);
        drawText(g, cx-8, (py+cy)/2+8);
      }
      drawArrowDown(g, cx, cy);
    }
  }

  //
  private void drawFromTransitionToPlace(AShape parent, Graphics g, int px, int cx, int py, int cy) {
    int cw = getChild().getWidth();
    int ch = getChild().getHeight();
    int ph = parent.getHeight();
    int cw_2 = cw/2;
    int ch_2 = ch/2;
    if (cy < py) {  // Place over Transition
      if (cx < px) {    // Place left from Transition
        int ci = ((ActionShape)getChild()).getInstructionSize() * 6;
        int dx = px - cx - cw_2;
        int dy = py - cy - ch_2;
        g.drawArc(cx+cw_2-dx+ci, cy+ch_2, dx*2-ci, dy*2, 0, 90);
        drawArrowLeft(g, cx+cw_2+ci, cy+ch_2);
        drawText(g, px-dx/2, (py+cy)/2);
      }
      else {            // Place right from Transition
        int dx = cx - cw_2 - px;
        int dy = py - cy - ch_2;
        g.drawArc(px, cy+ch_2, dx*2, dy*2, 90, 90);
        drawArrowRight(g, cx-cw_2, cy+ch_2);
        drawText(g, px+dx/2, (py+cy)/2);
      }
    }
    else {          // Place under Transition
      if (cx < px) {    // Place left from Transition
        int dx = px - cx - cw_2;
        int dy = cy + ch/2 - py - ph;
        g.drawArc(cx+cw_2-dx, py+ph-dy, dx*2, dy*2, 0, -90);
        drawArrowLeft(g, cx+cw_2, cy+ch_2);
        drawText(g, px-6, (py+cy)/2);
      }
      else {            // Place right from Transition
        int dx = cx - cw_2 - px;
        int dy = cy + ch_2 - py - ph;
        g.drawArc(px, py+ph-dy, dx*2, dy*2, 180, 90);
        drawArrowRight(g, cx-cw_2, cy+ch_2);
        drawText(g, px+4, (py+cy)/2);
      }
    }
  }

  //
  private void drawText(Graphics g, int x, int y) {
    String t = getText();
    if (t != null && t.length() > 0 && !"1".equals(t)) {
      Font currentFont = g.getFont();
      Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.7F);
      g.setFont(newFont);
      g.drawString(t, x, y);
      g.setFont(currentFont);
    }
  }

  //
  private void drawArrowUp(Graphics g, int x, int y) {
    int[] ptx = {x-4,   x, x+4};
    int[] pty = {y+7, y, y+7};
    g.fillPolygon(ptx, pty, 3);
  }

  //
  private void drawArrowDown(Graphics g, int x, int y) {
    int[] ptx = {x-4,   x, x+4};
    int[] pty = {y-7, y, y-7};
    g.fillPolygon(ptx, pty, 3);
  }

  //
  private void drawArrowLeft(Graphics g, int x, int y) {
    int[] ptx = {x, x+7, x+7};
    int[] pty = {y, y+4, y-4};
    g.fillPolygon(ptx, pty, 3);
  }

  //
  private void drawArrowRight(Graphics g, int x, int y) {
    int[] ptx = {x, x-7, x-7};
    int[] pty = {y, y-4, y+4};
    g.fillPolygon(ptx, pty, 3);
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

  public void setChild(PlaceNodeShape place, String name) {
    super.setChild(place);
  }
}
