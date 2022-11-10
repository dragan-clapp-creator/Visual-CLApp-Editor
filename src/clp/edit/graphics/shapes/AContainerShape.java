package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import clp.edit.GeneralContext;

public abstract class AContainerShape extends AShape {

  private static final long serialVersionUID = -4641680662938568513L;

  private AShape root;
  private Color background;

  /**
   * CONSTRUCTOR
   * 
   * @param width
   * @param height
   * @param name
   */
  public AContainerShape(int width, int height, String name) {
    super(width, height, name, "");
  }

  abstract public StringBuilder getSource(String mscName);
  abstract public void          removeActor();
  abstract public String        getScenarioName();
  abstract public String        getHeapNamePrefix();

  /**
   * PAINT a container shape
   * 
   * @param g
   * @param offset
   * @param isSelected
   * @param isActivated
   */
  public void paintShape(Graphics g, int offset, boolean isSelected, boolean isActivated) {
    if (isActivated) {
      if (isSelected) {
        g.setColor(new Color(0xFFC508));
      }
      else {
        g.setColor(new Color(0xF9F5D9));
      }
    }
    else {
      if (isSelected) {
        g.setColor(Color.darkGray);
      }
      else {
        g.setColor(Color.gray);
      }
    }
    int x = offset;
    g.fillRoundRect(x, 0, getWidth(), 40, 20, 20);
    g.setColor(getBackground());
    g.fillRoundRect(x, 40, getWidth(), getHeight(), 20, 20);
    g.setColor(Color.black);
    g.drawLine(x+5, 40, x+getWidth()-5, 40);
    g.drawString(getName(), x+(getWidth()-70)/2, 25);
  }

  public AShape getSelectedShape(int px, int py, int offset) {
    int x = px-offset;
    int y = py;
    return getRoot() == null ? null : getRoot().getSelectedShape(x, y);
  }

  public List<AShape> getSelectedShapes(int px1, int py1, int px2, int py2, int offset) {
    if (getRoot() == null) {
      return null;
    }
    int x1 = px1-offset;
    int y1 = py1;
    int x2 = px2-offset;
    int y2 = py2;
    List<AShape> list = new ArrayList<>();
    getRoot().gatherSelectedShapes(x1, y1, x2, y2, list);
    return list;
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    return null;
  }

  @Override
  public void gatherSelectedShapes(int x1, int y1, int x2, int y2, List<AShape> list) {
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
  }

  /**
   * @return the root
   */
  public AShape getRoot() {
    return root;
  }

  /**
   * @param root the root to set
   */
  public void setRoot(AShape root) {
    this.root = root;
  }

  public int bindToRoot(ARootShape event) {
    return ((ARootShape)getRoot()).setSibling(event);
  }

  /**
   * @return the background
   */
  public Color getBackground() {
    return background;
  }

  /**
   * @param background the background to set
   */
  public void setBackground(Color background) {
    this.background = background;
  }

  public void refresh() {
    GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().refresh();
  }

  @Override
  public String toString() {
    return getSource("<MSC>").toString();
  }

  public String getActorName() {
    return getName().replace(" ", "_");
  }

  public int getHighLevel() {
    return 1;
  }
}
