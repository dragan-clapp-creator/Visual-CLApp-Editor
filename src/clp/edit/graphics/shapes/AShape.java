package clp.edit.graphics.shapes;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.util.CellInfo;

public abstract class AShape extends JPanel {

  private static final long serialVersionUID = 9186985784901447564L;

  private int xoffset;
  private int yoffset;
  private int width;
  private int height;
  private boolean isSelected;
  private boolean isPublished;
  private boolean isReferencingSelectedCondition;

  private ABindingShape parent;
  private ABindingShape child;

  private String name;
  private String desc;
  private String adesc;

  /**
   * CONSTRUCTOR
   * @param width
   * @param height
   * @param name
   * @param prefix
   */
  public AShape(int width, int height, String name, String prefix) {
    this.width = width;
    this.height = height;
    this.name = name;
    this.adesc = "";
    setDesc(prefix + name);
  }

  abstract public void setChild(AShape shape, BindingType bindingType);
  abstract public ADialog getDialog();

  public void cacheFromTransients() {
  }


  public void paintShape(Graphics g, int offset) {
  }

  public void recreateListeners() {
  }

  public void checkDownType() {
    ABindingShape b = getChild();
    if (b instanceof TriBinding || b instanceof MultiBinding || b == null) {
      return;
    }
    if (b.getBindingType() == BindingType.DOWN || b.getBindingType() == BindingType.DOWN_MIDDLE) {
      if (b.getChild() != null && !(b.getChild() instanceof AJoinShape)) {
        int delta = b.getX2() - b.getX1();
        if (delta >= 0 && delta < 4 || delta < 0 && delta > -4) {
          b.setConditionnallyBindingType(BindingType.DOWN);
          b.setXshift(0);
        }
        else {
          b.setConditionnallyBindingType(BindingType.DOWN_MIDDLE);
          b.setOldXShift(); // restore 1st non-0 value
        }
      }
    }
  }

  public void gatherLibsDeclaration(ArrayList<String> jars) {
  }

  /**
   * this shape is selected if coordinates point within it
   * if not, look up for the children
   * 
   * @param x
   * @param y
   * @return
   */
  public AShape getSelectedShape(int x, int y) {
    int ref_x = getPX();
    if (x > ref_x-getWidth()/2 && x < ref_x+getWidth()/2
     && y > getPY() && y < getPY()+getHeight()) {

      return this;
    }
    if (getChild() != null) {
      return getChild().getSelectedShape(x, y);
    }
    return null;
  }

  /**
   * this shape is selected if in the region given by coordinates and
   * the same is proposed for children
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @param list
   */
  public void gatherSelectedShapes(int x1, int y1, int x2, int y2, List<AShape> list) {
    int ref_x = getPX() - getWidth()/2;
    int ref_y = getPY() + getHeight();
    if (ref_x > x1 && ref_x < x2 && ref_y > y1 && ref_y < y2) {
      list.add(this);
    }
    if (getChild() != null) {
      getChild().getChild().gatherSelectedShapes(x1, y1, x2, y2, list);
    }
  }

  /**
   * this shape is selected and the same is proposed to children if exist
   * 
   * @param list
   */
  public void gatherChildrenShapes(List<AShape> list) {
    list.add(this);
    if (getChild() != null) {
      getChild().gatherChildrenShapes(list);
    }
  }

  /**
   * go through the children shapes and add binding of this, if child is a join
   * 
   * @param list
   */
  public void gatherLinksToJoin(List<ABindingShape> list) {
    ABindingShape b = getChild();
    if (b != null) {
      AShape s = b.getChild();
      if (s instanceof AJoinShape) {
        list.add(b);
      }
      else if (!upBinding(b.getBindingType())) {
        s.gatherLinksToJoin(list);
      }
    }
  }

  //
  private boolean upBinding(BindingType bindingType) {
    return bindingType == BindingType.UP_LEFT || bindingType == BindingType.UP_LEFT_MIDDLE ||
           bindingType == BindingType.UP_RIGHT || bindingType == BindingType.UP_RIGHT_MIDDLE;
  }

  /**
   * @return the x
   */
  public int getPX() {
    if (parent != null) {
      return xoffset + parent.getX1();
    }
    return xoffset;
  }

  /**
   * @return the py
   */
  public int getPY() {
    if (parent != null) {
      return yoffset + parent.getPY();
    }
    return yoffset;
  }

  public void addToX(int delta) {
    xoffset += delta;
    if (getParent() != null && !(delta > 0 && delta < 4 || delta < 0 && delta > -4)) {
      getParent().updateDownBinding();
    }
  }

  public void addToY(int delta) {
    yoffset += delta;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return the isSelected
   */
  public boolean isSelected() {
    return isSelected;
  }

  /**
   * @param isSelected the isSelected to set
   */
  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @param height the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * @return the parent
   */
  public ABindingShape getParent() {
    return parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(ABindingShape parent) {
    this.parent = parent;
  }

  /**
   * @return the child
   */
  public ABindingShape getChild() {
    return child;
  }

  /**
   * @param child the child to set
   */
  public void setChild(ABindingShape child) {
    this.child = child;
    if (child != null) {
      child.setParent(this);
    }
  }

  /**
   * @return the xoffset
   */
  public int getXoffset() {
    return xoffset;
  }

  /**
   * @return the yoffset
   */
  public int getYoffset() {
    return yoffset;
  }

  public void setXoffset(int delta) {
    xoffset = delta;
  }

  public void setYoffset(int delta) {
    yoffset = delta;
  }

  /**
   * @return the isReferencingSelectedCondition
   */
  public boolean isReferencingSelectedCondition() {
    return isReferencingSelectedCondition;
  }

  /**
   * @param isReferencingSelectedCondition the isReferencingSelectedCondition to set
   */
  public void setReferencingSelectedCondition(boolean isReferencingSelectedCondition) {
    this.isReferencingSelectedCondition = isReferencingSelectedCondition;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the isPublished
   */
  public boolean isPublished() {
    return isPublished;
  }

  /**
   * @param isPublished the isPublished to set
   */
  public void setPublished(boolean isPublished) {
    this.isPublished = isPublished;
  }

  /**
   * @return the desc
   */
  public String getDesc() {
    return desc;
  }

  /**
   * @param desc the desc to set
   */
  public void setDesc(String desc) {
    if (desc != null) {
      this.desc = desc;
      setToolTipText("<html><b>" + this.name + " (" + this.desc + ")</b>" + this.adesc + "</html>");
    }
  }

  /**
   * @param desc the desc to set
   */
  public void addToDesc(String desc) {
    this.adesc = "<p>" + desc.replaceAll("\n", "<p>");
    setToolTipText("<html><b>" + this.name + " (" + this.desc + ")</b>" + this.adesc + "</html>");
  }

  public boolean generateCode(AContainer container) {
    if (!container.isRegistered(name)) {
      CellInfo info = new CellInfo(name);
      fillContent(container, info);
      container.register(name, info, false);
      if (child != null) {
        if (!child.generateCode(container)) {
          return false;
        }
      }
    }
    return true;
  }

  public void declareResources(boolean isEndNode) {
    if (getChild() != null) {
      getChild().declareResources();
    }
  }

  public void clearResources() {
    if (getChild() != null) {
      getChild().clearResources();
    }
  }

  //
  protected void fillContent(AContainer container, CellInfo info) {
    String code = getActivationDomain(container);
    if (code != null) {
      info.setAd("      AD { " + code + "; }\n");
    }
    code = getDeactivationDomain(container);
    if (code != null) {
      info.setDd("      DD { " + code + "; }\n");
    }
    ArrayList<String> instList = getExecutionDomain(container);
    if (instList != null && !instList.isEmpty()) {
      info.setXd(instList);
    }
  }

  public String getActivationDomain(AContainer container) {
    return null;
  }

  public String getDeactivationDomain(AContainer container) {
    return null;
  }

  public ArrayList<String> getExecutionDomain(AContainer container) {
    return null;
  }

  public boolean generateActiveCode(AContainer container) {
    if (!container.isRegistered(name)) {
      CellInfo info = new CellInfo(name);
      fillContent(container, info);
      container.register(name, info, true);
      if (child != null) {
        if (!child.generateCode(container)) {
          return false;
        }
      }
    }
    return true;
  }

  public String getActivationCondition(ABindingShape bs) {
    return " activated(" + getName() + ") ";
  }

  public String getDeactivationCondition() {
    if (this instanceof ActionShape) {
      ((ActionShape)this).addInputVariable(getName(), getDesc(), null, false);
    }
    return " activated(" + getName() + ") ";
  }

  public void upateDialog() {
  }
}
