package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class MultiBinding extends BindingShape {

  private static final long serialVersionUID = 5458352114345480351L;

  private ArrayList<ABindingShape> children;

  public MultiBinding() {
    super(BindingType.DOWN_MIDDLE, null);
    children = new ArrayList<>();
  }

  public void addChildren(TriBinding tri) {
    addChild(tri.getLeft());
    addChild(tri.getMiddle());
    addChild(tri.getRight());
  }

  public void addChild(ABindingShape nb) {
    children.add(nb);
    int n = children.size();
    for (int i=1; i<=n; i++) {
      AShape ch = children.get(i-1).getChild();
      ch.setXoffset((n-i)*80);
      ch.setYoffset(i*80);
    }
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    for (ABindingShape b : children) {
      int py = b.getParentY();
      int cy = b.getChildY();
      int x1 = b.getX1() + offset;
      int x2 = b.getX2() + offset;
      int middle = (cy-py)/2;
      g.setColor(getColor());
      g.drawLine(x1, py, x1, py+middle);
      g.drawLine(x1, py+middle, x2, py+middle);
      g.drawLine(x2, py+middle, x2, cy);
      g.setColor(c);
      b.getChild().paintShape(g, offset);
    }
 }

  /**
   * @return the children
   */
  public ArrayList<ABindingShape> getChildren() {
    return children;
  }

  public int getRightmost() {
    return children.get(0).getX2() + 50;
  }

  @Override
  public void gatherChildrenShapes(List<AShape> list) {
    for (ABindingShape b : children) {
      b.gatherChildrenShapes(list);
    }
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    for (ABindingShape b : children) {
      AShape s = b.getChild().getSelectedShape(x, y);
      if (s != null) {
        return s;
      }
    }
    return null;
  }

  @Override
  public boolean generateCode(AContainer container) {
    boolean b = true;
    for (int i=0; b && i<children.size(); i++) {
      ABindingShape bs = children.get(i);
      b &= bs.generateCode(container);
    }
    return b;
  }

  @Override
  public boolean generateActiveCode(AContainer container) {
    boolean b = true;
    for (int i=0; b && i<children.size(); i++) {
      ABindingShape bs = children.get(i);
      b &= bs.generateActiveCode(container);
    }
    return b;
  }

  @Override
  public String getDeactivationCondition() {
    String s = children.get(0).getDeactivationCondition();
    for (int i=1;  i<children.size(); i++) {
      ABindingShape bs = children.get(i);
      s += " OR " + bs.getDeactivationCondition();
    }
    return s;
  }

  @Override
  public void clearChildrenEntries() {
    for (ABindingShape b : children) {
      clearEntries(b.getChild());
    }
  }

  @Override
  public void declareResources() {
    for (ABindingShape bs : children) {
      bs.declareResources();
    }
  }

  @Override
  public void clearResources() {
    for (ABindingShape bs : children) {
      bs.clearResources();
    }
  }
}
