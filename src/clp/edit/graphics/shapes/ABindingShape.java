package clp.edit.graphics.shapes;

import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

public abstract class ABindingShape extends JComponent {

  private static final long serialVersionUID = -2273189660395210964L;


  abstract public void gatherChildrenShapes(List<AShape> list);
  abstract public void clearChildrenEntries();

  private AShape parent;
  private AShape child;

  private String text;

  private int xshift;

  private BindingType bindingType;

  private int oldxshift;


  public ABindingShape(String t, BindingType b) {
    this.bindingType = b;
    this.text = t;
    setupXshiftings();
  }

  //
  private void setupXshiftings() {
    switch (bindingType) {
      case DOWN_LEFT:
        xshift = -90;
        break;
      case DOWN_RIGHT:
        xshift = +90;
        break;
      default:
        break;
    }
  }

  public void paintShape(Graphics g, int offset) {
  }

  public void paintShape(AShape parent, Graphics g, int offset) {
  }

  public int getChildY() {
    if (getChild() == null) {
      return 0;
    }
    return getChild().getPY();
  }

  public int getParentY() {
    return getParent().getPY()+getParent().getHeight();
  }

  /**
   * @return the bindingType
   */
  public BindingType getBindingType() {
    return bindingType;
  }

  /**
   * @param bindingType the bindingType to set if not already set to UP
   * @return *
   */
  public boolean setConditionnallyBindingType(BindingType bt) {
    if (bindingType != BindingType.UP_LEFT && bindingType != BindingType.UP_RIGHT) {
      bindingType = bt;
      return true;
    }
    return false;
  }

  /**
   * @param bindingType the bindingType to set
   */
  public void setBindingType(BindingType bt) {
    bindingType = bt;
  }

  /**
   * @return the parent
   */
  public AShape getParent() {
    return parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(AShape parent) {
    this.parent = parent;
  }

  /**
   * @return the child
   */
  public AShape getChild() {
    return child;
  }

  /**
   * @param child the child to set
   */
  public void setChild(AShape child) {
    this.child = child;
    child.setParent(this);
  }

  public void setOnlyChild(AShape child) {
    this.child = child;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }

  public AShape getSelectedShape(int x, int y) {
    if (getChild() != null) {
      return getChild().getSelectedShape(x, y);
    }
    return null;
  }

  /**
   * X position of 1st point (binding the parent) = parent's X position
   * it depends on the binding type
   */
  public int getX1() {
    switch (bindingType) {
      case DOWN_LEFT:
      case UP_LEFT:
        return parent.getPX() - parent.getWidth()/2;
      case DOWN_RIGHT:
      case UP_RIGHT:
        return parent.getPX() + parent.getWidth()/2;

      default:
        return parent.getPX();
    }
  }

  /**
   * X position of 2nd point (binding the child) = child's X position
   */
  public int getX2() {
    return child.getPX();
  }

  /**
   * X position of middle point (middle position)
   */
  public int getXmiddle() {
    return xshift;
  }

  /**
   * @return xshift
   */
  public int getXshift() {
    return xshift;
  }

  /**
   * @param xshift the xshift to set
   */
  public void setXshift(int xshift) {
    this.xshift = xshift;
    if (oldxshift == 0) {   // save 1st non-0 value
      oldxshift = xshift;
    }
  }

  public void updateDownBinding() {
    if (bindingType == BindingType.DOWN) {
      bindingType = BindingType.DOWN_MIDDLE;
    }
  }

  public void shiftX(int delta) {
    xshift += delta;
  }

  public int getPY() {
    return parent.getPY();
  }

  public void setOldXShift() {
    xshift = oldxshift;
  }

  public boolean generateCode(AContainer container) {
    if (bindingType == BindingType.UP_LEFT || bindingType == BindingType.UP_LEFT_MIDDLE ||
        bindingType == BindingType.UP_RIGHT || bindingType == BindingType.UP_RIGHT_MIDDLE) {
      return true;
    }
    return getChild().generateCode(container);
  }

  public boolean generateActiveCode(AContainer container) {
    return getChild().generateActiveCode(container);
  }

  public String getDeactivationCondition() {
    return getChild().getDeactivationCondition();
  }

  public void declareResources() {
    boolean isEndNode = (bindingType == BindingType.UP_LEFT || bindingType == BindingType.UP_LEFT_MIDDLE ||
                         bindingType == BindingType.UP_RIGHT || bindingType == BindingType.UP_RIGHT_MIDDLE);
    getChild().declareResources(isEndNode);
  }

  public void clearResources() {
    if (bindingType != BindingType.UP_LEFT && bindingType != BindingType.UP_RIGHT &&
        bindingType != BindingType.UP_LEFT_MIDDLE && bindingType != BindingType.UP_RIGHT_MIDDLE) {
      getChild().clearResources();
    }
  }
}
