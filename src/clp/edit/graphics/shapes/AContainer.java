package clp.edit.graphics.shapes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AButtonsPanel;
import clp.edit.graphics.btn.IAutomaton;
import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.panel.ButtonsContainer.ButtonType;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.act.ActivityShape;
import clp.edit.graphics.shapes.util.CellInfo;
import clp.edit.util.TreeNodeInfo;

abstract public class AContainer implements Serializable {

  private static final long serialVersionUID = 3958439770618221531L;

  private int offset;

  private boolean isDirty;
  private boolean isActive;
  private Hashtable<String, CellInfo> acells; // initially active cells
  private Hashtable<String, CellInfo> icells; // initially inactive cells

  private AShape current;

  private int leftmost;
  private int rightmost;

  private RedBindingShape pushedRedShape;
  private RedBindingShape redShape;
  private List<ABindingShape> bindings;

  private AShape selected;
  private List<AShape> slist;

  private JCheckBoxMenuItem containerEnabling;

  abstract public Dimension         paint(Graphics g, int width, boolean isSelected);
  abstract public AContainerShape   getContainerShape();
  abstract public AButtonsPanel     getButtonsPanel();
  abstract public IAutomaton        getAutomaton();
  abstract public void              setContainerShape(AContainerShape as);
  abstract public boolean           isSelected(int px, int py);
  abstract public ButtonType        getType();
  abstract public void              addShape(AShape shape);
  abstract public void              handleAction(AShape sel, int px, int py, boolean isMultiSelect);
  abstract public ABindingShape[]   getChildren(AShape s, boolean isUp);
  abstract public void              showContextMenu(GeneralShapesContainer genericContainer, AShape shape, MouseEvent e);
  abstract public boolean           isInMainBranch(ABindingShape b);
  abstract public String            getStringActionNo();

  /**
   * CONSTRUTOR
   */
  public AContainer() {
    setLeftmost(200);
    setRightmost(200);
    redShape = new RedBindingShape(BindingType.DOWN);
    bindings = new ArrayList<>();
    slist = new ArrayList<>();
    isActive = true;
    containerEnabling = new JCheckBoxMenuItem("deactivate", true);
    createLisener();
  }

  /**
   * add given shape to container shape (c_shape) and update relative positions
   * 
   * @param shape
   * @param bindingType 
   */
  public void addShape(AShape shape, BindingType bindingType) {
    if (getContainerShape().getRoot() == null) {
      getContainerShape().setRoot(shape);
    }
    else if (bindingType == null) {
      int i = getContainerShape().bindToRoot((ARootShape) shape);
      shape.addToX(i*100);
      updateLimits(getContainerShape(), shape);
    }
    else {  
      getCurrent().setChild(shape, bindingType);
      if (shape.getParent() == null) {
        return; // it didn't work
      }
      shape.addToX(shape.getParent().getXshift());
      shape.setYoffset(getCurrent().getHeight()+30);
      storeUpBindings(bindingType);
      if (getCurrent() instanceof AJoinShape) {
        return;
      }
      if (shape instanceof ActionShape || getCurrent() instanceof AForkShape) {
        if (getCurrent() instanceof AForkShape) {
          checkResizing(getContainerShape(), ((AForkShape)getCurrent()).getLeftmostChild());
        }
        checkResizing(getContainerShape(), shape);
      }
      updateLimits(getContainerShape(), shape);
    }
    setDirty(true);
  }

  /**
   * bind to existing shape
   * @param shape
   */
  public void bindShape(AShape shape) {
    if (shape instanceof ARootShape) {
      getContainerShape().bindToRoot((ARootShape) shape);
      checkResizing(getContainerShape(), shape.getChild().getChild());
    }
    else if (getRedShape().isAttached()) {
      pinUpdating(shape);
    }
    setDirty(true);
  }

  //
  private void pinUpdating(AShape shape) {
    getRedShape().reverseBindingType(getCurrent(), shape);
    BindingShape pin = getRedShape().pinIt();
    int delta = getCurrent().getPX() - shape.getPX();
    if (pin.getBindingType() == BindingType.UP_LEFT) {
      int x = shape.getPX()-shape.getWidth()/2 - 10;
      if (delta < 0) {
        pin.setXshift(delta);
      }
      if (x < getLeftmost()) {
        setLeftmost(x-10);
      }
      getCurrent().setChild(pin);   // attach binding to left branch
      getCurrent().setChild(shape, pin.getBindingType());   // attach shape to binding
      getRedShape().forceRightPartDecisionShape();
    }
    else {
      int x = shape.getPX()+shape.getWidth()/2 + 10;
      if (delta > 0) {
        pin.setXshift(delta);
      }
      if (x > getRightmost()) {
        setRightmost(x+10);
      }
      getCurrent().setChild(pin);   // attach binding to right (or join/fork) branch
      getCurrent().setChild(shape, pin.getBindingType());   // attach shape to binding
      if (getCurrent() instanceof ADecisionShape) {
        getRedShape().setAttachTo(null);
      }
    }
  }

  //
  private void storeUpBindings(BindingType bindingType) {
    if (getCurrent() instanceof ADecisionShape) {
      ADecisionShape d = (ADecisionShape) getCurrent();
      if (bindingType == BindingType.UP_LEFT) {
        getBindings().add(d.getLeftup());
        d.getLeftup().setXshift(getLeftmost());
      }
      if (bindingType == BindingType.UP_RIGHT) {
        getBindings().add(d.getRightup());
        d.getRightup().setXshift(getRightmost());
      }
    }
  }

  public boolean isDeleteAllowed(AShape shape) {
    if (!(shape instanceof ARootShape) && shape.getParent().getParent() instanceof ADecisionShape) {
      return false;
    }
    for (ABindingShape b : bindings) {
      if (b.getChild().equals(shape)) {
        return false;
      }
    }
    return shape.getChild() == null;
  }

  public void multiselect(AShape shape) {
    if (selected != null && !slist.contains(selected)) {
      slist.add(selected);
    }
    if (shape.isSelected()) {
      shape.setSelected(false);
      slist.remove(shape);
    }
    else {
      shape.setSelected(true);
      slist.add(shape);
    }
    getContainerShape().refresh();
  }

  public void select(AShape shape) {
    if (!slist.isEmpty()) {
      for (AShape s : slist) {
        s.setSelected(false);
      }
      slist.clear();
    }
    if (shape != null) {
      if (shape.isSelected()) {
        selected = null;
        shape.setSelected(false);
      }
      else {
        boolean wasSelected = false;
        if (selected != null) {
          selected.setSelected(false);
          wasSelected = true;
        }
        shape.setSelected(true);
        selected = shape;
        if (wasSelected && shape.getChild() == null && !(shape instanceof AFinalShape)) {
          getRedShape().setReady(true);
          getRedShape().setAttachTo(shape);
          getRedShape().updateXYBoundaries();
        }
        else {
          boolean isReady = getRedShape().isReady();
          if (!isReady && shape.getChild() == null) {
            getRedShape().setReady(true);
            getRedShape().setAttachTo(shape);
          }
          else {
            getRedShape().setReady(!isReady);
          }
        }
        setCurrent(shape);
      }
      getContainerShape().refresh();
    }
  }

  public void provideSelection(Point op, Point fp) {
    slist = getContainerShape().getSelectedShapes(op.x, op.y, fp.x, fp.y, getOffset());
    for (AShape shape : slist) {
      shape.setSelected(true);
    }
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  /**
   * check if a container shape is selected
   * 
   * @param px
   * @param py
   * @param c_shape
   * @return
   */
  public boolean isSelected(int px, int py, AShape c_shape) {
    int x = px - offset;
    return (x > c_shape.getPX() && x < c_shape.getPX()+c_shape.getWidth()
         && py > c_shape.getPY() && py < c_shape.getPY()+c_shape.getHeight());
  }

  public void updateRedShape(ADecisionShape d2, int delta) {
    if (getRedShape().getAttach() == d2) {
      int x1 = getRedShape().getX1()+delta;
      getRedShape().setXshift(x1+80);
    }
  }

  public void selectFromRedShape() {
    if (getRedShape().getAttach() != null) {
      select(getRedShape().getAttach());
    }
  }

  public void selectAllFromShape(AShape shape) {
    slist.clear();
    shape.gatherChildrenShapes(slist);
    for (AShape s : slist) {
      s.setSelected(true);
    }
  }

  public void unselectAll() {
    for (AShape shape : slist) {
      shape.setSelected(false);
    }
    slist.clear();
    if (selected != null) {
      selected.setSelected(false);
      selected = null;
      boolean isReady = getRedShape().isReady();
      if (!isReady) {
        getAutomaton().performAction(ActionMode.INIT, null, null, false);
      }
    }
  }

  public boolean hasSelection() {
    return selected != null || !slist.isEmpty();
  }

  public boolean checkForBindingUpdate(int x) {
    getRedShape().setReady(false);
    ADecisionShape d = ((ADecisionShape)selected);
    if (d.isDecisionUp()) {
      x -= getOffset();
      BindingShape b;
      if (d.getPX() < x) {
        b = d.getRightup();
        if (b != null) {
          b.setXshift(x-b.getX2()-b.getChild().getWidth()/2-10);
          return true;
        }
      }
      else {
        b = d.getLeftup();
        if (b != null) {
          b.setXshift(x-b.getX2()+b.getChild().getWidth()/2+10);
          return true;
        }
      }
    }
    return false;
  }

  public boolean isReadyToAddShape() {
    return getRedShape().isReady();
  }

  public void setReadyToAddShape() {
    getRedShape().setAttachTo(null);
  }

  public void moveSelection(Point delta) {
    if (selected != null && !slist.contains(selected)) {
      slist.add(selected);
    }
    if (!slist.isEmpty()) {
      getRedShape().setReady(false);
      List<AShape> entries = getEntries(slist);
      for (AShape e : entries) {
        if (!(e instanceof AJoinShape)) {
          e.addToX(delta.x);
        }
        e.addToY(delta.y);
      }
      List<AShape> outputs = getOutputs(slist);
      for (AShape o : outputs) {
        if (!(o instanceof AJoinShape)) {
          o.addToX(-delta.x);
        }
        if (!(o instanceof ARootShape)) {
          o.addToY(-delta.y);
        }
      }
      for (AShape shape : slist) {
        if (shape instanceof ActionShape) {
          checkResizing(getContainerShape(), shape);
        }
        else if (shape instanceof ADecisionShape) {
          ((ADecisionShape)shape).updateDeltaX(delta.x);
        }
        else if (shape instanceof ATransitionShape) {
          updateBindingType((ATransitionShape) shape);
        }
        updateLimits(getContainerShape(), shape);
      }
    }
  }

  //
  private void updateBindingType(ATransitionShape shape) {
    ABindingShape c = shape.getChild();
    if (c != null) {
      if (c.getBindingType() == BindingType.UP_LEFT && c.getX1() > c.getX2()) {
        c.setBindingType(BindingType.UP_RIGHT);
      }
      else if (c.getBindingType() == BindingType.UP_RIGHT && c.getX1() < c.getX2()) {
        c.setBindingType(BindingType.UP_LEFT);
      }
    }
  }
  //
  private void checkResizing(AContainerShape c_shape, AShape shape) {
    int xroot = c_shape.getRoot().getPX();
    int x = shape.getPX();
    int halfShape = shape.getWidth()/2;
    int delta = 0;
    if (x < xroot) {
      delta = halfShape + 10 - x;
      if (delta > 0) {
        c_shape.setWidth(c_shape.getWidth() + delta);
        c_shape.getRoot().addToX(delta);
        if (getCurrent() instanceof ADecisionShape) {
          getRedShape().updateRightPartDecisionShape();
        }
      }
      else {
        checkForOverlap(shape, c_shape);
      }
    }
    else {
      delta = halfShape + x + 10  - c_shape.getWidth();
      if (delta > 0) {
        c_shape.setWidth(c_shape.getWidth() + delta);
      }
      else {
        checkForOverlap(shape, c_shape);
      }
    }
  }

  //
  private void checkForOverlap(AShape shape, AContainerShape c_shape) {
    BindingType type = shape.getParent().getBindingType();
    switch (type) {
      case DOWN:
        checkForLeftOverlaps(shape, c_shape);
        checkForRightOverlaps(shape, c_shape);
        break;
      case DOWN_MIDDLE:
        if (shape.getParent().getX1() < 0) {
          checkForLeftOverlaps(shape, c_shape);
        }
        else {
          checkForRightOverlaps(shape, c_shape);
        }
        break;
      case DOWN_LEFT:
        checkForLeftOverlaps(shape, c_shape);
        break;
      case DOWN_RIGHT:
        checkForRightOverlaps(shape, c_shape);
        break;

      default:
        break;
    }
  }

  //
  private void checkForRightOverlaps(AShape shape, AContainerShape c_shape) {
    if (c_shape instanceof ActivityShape) {
      int refx = shape.getPX() + shape.getWidth()/2;
      int refy = shape.getPY();
      AShape s = c_shape.getRoot();
      ADecisionShape dnode = findDecisionNode(shape, s, refx, refy, false);
      if (dnode != null) {
        int delta = shape.getWidth() + 10;
        dnode.getLeft().getChild().addToX(-delta);
        s.addToX(delta);
        AShape rshape = getRightmostShape(dnode);
        checkResizing(c_shape, rshape);
      }
    }
//    JoinBindingShape jnode = findJoinNode(shape, c_shape.getRoot());
//    if (jnode != null) {
//      int delta = shape.getWidth() + 10;
//      List<ABindingShape> parents = jnode.getParents();
//      for (ABindingShape pb : parents) {
//        pb.getParent().addToX(-delta);
//      }
//      AShape rshape = parents.get(parents.size()-1).getParent();
//      checkResizing(c_shape, rshape);
//    }
  }

  //
  private void checkForLeftOverlaps(AShape shape, AContainerShape c_shape) {
    if (c_shape instanceof ActivityShape) {
      int refx = shape.getPX() - shape.getWidth()/2;
      int refy = shape.getPY();
      ADecisionShape dnode = findDecisionNode(shape, c_shape.getRoot(), refx, refy, true);
      if (dnode != null) {
        int delta = shape.getWidth() + 10;
        dnode.getRight().getChild().addToX(delta);
        AShape rshape = getRightmostShape(dnode);
        checkResizing(c_shape, rshape);
        getRedShape().updateRightPartDecisionShape();
      }
    }
//    JoinBindingShape jnode = findJoinNode(shape, c_shape.getRoot());
//    if (jnode != null) {
//      int delta = shape.getWidth() + 10;
//      List<ABindingShape> parents = jnode.getParents();
//      for (ABindingShape pb : parents) {
//        pb.getParent().addToX(delta);
//      }
//      AShape rshape = parents.get(parents.size()-1).getParent();
//      checkResizing(c_shape, rshape);
//    }
  }

  //
  private AShape getRightmostShape(ADecisionShape node) {
    BindingShape n = node.getRight();
    if (n == null) {
      return null;
    }
    AShape c = n.getChild();
    if (c.getChild() == null) {
      return c;
    }
    AShape d = c;
    while (d != null && !(d instanceof ADecisionShape)) {
      ABindingShape b = d.getChild();
      if (b == null) {
        d = null;
      }
      else {
        d = b.getChild();
      }
    }
    if (d == null) {
      return c;
    }
    AShape ret = getRightmostShape((ADecisionShape) d);
    return ret != null ? ret : c;
  }

  //
  private ADecisionShape findDecisionNode(AShape shape, AShape s, int refx, int refy, boolean isLeft) {
    ABindingShape b = null;
    ABindingShape[] bs = null;
    ADecisionShape d = null;
    do {
      bs = getChildren(s, false);
      if (bs != null) {
        for (int i=0; i<bs.length; i++) {
          b = getAllowedBinding(bs[i]);
          if (b != null) {
            s = b.getChild();
            if (s != shape) {
              if (s.getPY() > refy) {
                break;
              }
              int x = s.getPX();
              if (x-s.getWidth()/2 <= refx && x+s.getWidth()/2 >= refx
               && s.getPY()-s.getHeight()/2 <= refy && s.getPY()+s.getHeight()/2 >= refy) {
                d = retrieveDecisionNode(s, !isLeft);
                if (d != null) {
                  return d;
                }
              }
              d = findDecisionNode(shape, s, refx, refy, isLeft);
              if (d != null) {
                return d;
              }
            }
          }
        }
      }
      else {
        b = s.getChild();
        if (b != null) {
          s = b.getChild();
        }
      }
    }  while (b != null && s != null && s != shape);

    return null;
  }

  //
  private ABindingShape getAllowedBinding(ABindingShape b) {
    if (b != null && isBindingTypeAllowed(b.getBindingType())) {
      return b;
    }
    return null;
  }

  //
  private boolean isBindingTypeAllowed(BindingType t) {
    return t == BindingType.DOWN || t == BindingType.DOWN_LEFT ||
           t == BindingType.DOWN_MIDDLE || t == BindingType.DOWN_RIGHT;
  }

  //
  private ADecisionShape retrieveDecisionNode(AShape s, boolean isSearchForLeft) {
    ABindingShape b = null;
    ADecisionShape d = null;
    boolean isParent = false;
    do {
      b = s.getParent();
      if (b != null) {
        s = b.getParent();
        if (s instanceof ADecisionShape) {
          if (!isParent) {
            if (isSearchForLeft && b.getBindingType() == BindingType.DOWN_LEFT ||
               !isSearchForLeft && b.getBindingType() == BindingType.DOWN_RIGHT) {
              isParent = true;
            }
          }
          else {
            if (isSearchForLeft && b.getBindingType() == BindingType.DOWN_RIGHT ||
               !isSearchForLeft && b.getBindingType() == BindingType.DOWN_LEFT) {
              d = (ADecisionShape) s;
            }
          }
        }
      }
    } while (s != null && b != null && d == null);

    return d;
  }

  /**
   * PAINT a container shape
   * 
   * @param g
   * @param offset
   * @param isSelected
   * @param c_shape
   * @return
   */
  public Dimension paint(Graphics g, int offset, boolean isSelected, AContainerShape c_shape) {
    int width = 1;
    int height = 1;
    c_shape.paintShape(g, offset, isSelected, containerEnabling.getState());
    int w = c_shape.getWidth();
    if (width < w) {
      width = w;
    }
    int h = c_shape.getHeight();
    if (height < h) {
      height = h;
    }
    this.offset = offset;

    return new Dimension(width+2, height);
  }

  public void deleteAllFromShape(AShape shape) {
    ABindingShape cb = shape.getChild();
    AShape cshape = cb.getChild();
    if (cb != null) {
      if (cshape instanceof AJoinShape) {
        AJoinShape join = (AJoinShape) cb.getChild();
        for (int i=0; i<join.getParents().size(); i++) {
          ABindingShape b = join.getParents().get(i);
          if (b == cb) {
            join.getParents().remove(i);
            break;
          }
        }
        if (join.getParents().size() == 1) {
          replaceJoin(join);
        }
      }
      else {
        ArrayList<ABindingShape> list = new ArrayList<>();
        cshape.gatherLinksToJoin(list);
        if (!list.isEmpty()) {
          AJoinShape join = (AJoinShape) list.get(0).getChild();
          int nblinks = join.getParents().size();
          if (nblinks == list.size()) {
            deleteAllFromShape(join);
          }
          else {
            join.deleteChildNodes(list);
          }
        }
      }
    }
    ABindingShape pb = cshape.getParent();
    if (pb instanceof TriBinding) {
      ((TriBinding)pb).delete(cb, cshape, true);
    }
    shape.setChild(null);
    cb.clearChildrenEntries();
    setCurrent(shape);
    getRedShape().setAttachTo(shape);
  }

  public void replaceJoin(AJoinShape join) {
    AShape child = join.getChild().getChild();
    join.getParents().get(0).setChild(child);
    child.addToY(30);
  }
  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * @return the current
   */
  public AShape getCurrent() {
    return current;
  }

  /**
   * @param current the current to set
   */
  public void setCurrent(AShape current) {
    this.current = current;
  }

  //
  public List<AShape> getEntries(List<AShape> list) {
    List<AShape> entries = new ArrayList<>();
    for (AShape s : list) {
      if (s.getParent() == null) {
        entries.add(s);
      }
      else {
        AShape p = s.getParent().getParent();
        if (!list.contains(p)) {
          entries.add(s);
        }
      }
    }
    return entries;
  }

  //
  public List<AShape> getOutputs(List<AShape> list) {
    List<AShape> outputs = new ArrayList<>();
    for (AShape s : list) {
      if (s instanceof AJoinShape) {
        continue;
      }
      ABindingShape[] bs = getChildren(s, true);
      if (bs != null) {
        for (ABindingShape b : bs) {
//          if (b != null && isBindingTypeAllowed(b.getBindingType()) &&
//              b.getChild() != null && !list.contains(b.getChild()) && isInMainBranch(b)) {
//            outputs.add(b.getChild());
//          }
          if (b != null && isBindingTypeAllowed(b.getBindingType()) &&
              b.getChild() != null && !list.contains(b.getChild())) {
            outputs.add(b.getChild());
          }
          if (s instanceof ARootShape) {
            ARootShape sibling = ((ARootShape) s).getSibling();
            while (sibling != null) {
              outputs.add(sibling);
              sibling = sibling.getSibling();
            }
          }
        }
      }
    }
    return outputs;
  }

  //
  public void updateLimits(int left, int right) {
    AContainerShape c_shape = getContainerShape();
    if (leftmost > left) {
      leftmost = left;
    }
    if (rightmost < right) {
      rightmost = right;
    }
    if (rightmost > c_shape.getWidth()) {
      c_shape.setWidth(rightmost + 20);
    }
  }

  //
  public void updateLimits(AContainerShape c_shape, AShape shape) {
    int x = shape.getPX();
    int left = x - shape.getWidth()/2;
    int right = x + shape.getWidth()/2;
    if (leftmost > left) {
      leftmost = left;
    }
    if (rightmost < right) {
      rightmost = right;
    }
    int delta;
    if (leftmost < 0) {
      delta = 10 - leftmost;
      leftmost += delta;
      rightmost += delta;
      c_shape.getRoot().addToX(delta);
    }
    else {
      delta = rightmost - c_shape.getWidth();
    }
    if (rightmost > c_shape.getWidth()) {
      c_shape.setWidth(c_shape.getWidth()+delta);
    }
    int y_location = shape.getPY();
    if (y_location > c_shape.getHeight()-30) {
      c_shape.setHeight(y_location+100);
    }
  }

  /**
   * @return the leftmost
   */
  public int getLeftmost() {
    return leftmost;
  }

  /**
   * @param leftmost the leftmost to set
   */
  public void setLeftmost(int leftmost) {
    this.leftmost = leftmost;
  }

  /**
   * @return the rightmost
   */
  public int getRightmost() {
    return rightmost;
  }

  /**
   * @param rightmost the rightmost to set
   */
  public void setRightmost(int rightmost) {
    this.rightmost = rightmost;
  }

  /**
   * @return the redShape
   */
  public RedBindingShape getRedShape() {
    return redShape;
  }
  /**
   * @return the bindings
   */
  public List<ABindingShape> getBindings() {
    return bindings;
  }
  /**
   * @return the selected
   */
  public AShape getSelected() {
    return selected;
  }
  public void pushRedShape() {
    pushedRedShape = redShape;
    redShape = new RedBindingShape(BindingType.DOWN);
  }
  public void popRedShape() {
    redShape = pushedRedShape;
    pushedRedShape = null;
  }
  /**
   * @return the isDirty
   */
  public boolean isDirty() {
    return isDirty;
  }
  /**
   * @param isDirty the isDirty to set
   */
  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  public boolean generateCode() {
    acells = new Hashtable<>();
    icells = new Hashtable<>();
    ARootShape shape = (ARootShape) getContainerShape().getRoot();
    while (shape != null) {
      shape.clearResources();
      shape = shape.getSibling();
    }
    shape = (ARootShape) getContainerShape().getRoot();
    while (shape != null) {
      if (!((AShape)shape).generateCode(this)) {
        return false;
      }
      shape = shape.getSibling();
    }
    if (!acells.isEmpty() || !icells.isEmpty()) {
      TreeNodeInfo nodeInfo = new TreeNodeInfo( getContainerShape());
      nodeInfo.generateCode(acells, icells);
    }
    shape = (ARootShape) getContainerShape().getRoot();
    while (shape != null) {
      shape.declareResources(false);
      shape = shape.getSibling();
    }
    return true;
  }

  public void register(String name, CellInfo info, boolean isInitiallyActive) {
    if (isInitiallyActive) {
      acells.put(name, info);
    }
    else {
      icells.put(name, info);
    }
  }

  public boolean isRegistered(String name) {
    return acells.containsKey(name) || icells.containsKey(name);
  }

  public boolean removeDeactivationForInit(String name) {
    CellInfo cinfo = acells.get(name);
    if (cinfo != null) {
      cinfo.setDd(null);
      return true;
    }
    return false;
  }

  public boolean addDeactivationConditionFromFinal(String name, String final_name) {
    CellInfo cinfo = acells.get(name);
    if (cinfo == null) {
      cinfo = icells.get(name);
    }
    if (cinfo != null && cinfo.getDd() != null) {
      String cnd = cinfo.getDd();
      int i = cnd.indexOf("{");
      if (i > 0) {
        cinfo.setDd("      DD { activated(" + final_name + "); }\n");
        return true;
      }
    }
    return false;
  }

  public String getKey() {
    return getContainerShape().getScenarioName() + getContainerShape().getActorName();
  }

  /**
   * @return the isActive
   */
  public boolean isActive() {
    return isActive;
  }
  /**
   * @param isActive the isActive to set
   */
  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public JCheckBoxMenuItem getCheckBox() {
    return containerEnabling;
  }

  public void createLisener() {
    containerEnabling.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
        if (cb.getState()) {
          cb.setText("deactivate");
          setDirty(true);
          setActive(true);
        }
        else {
          cb.setText("reactivate");
          setDirty(true);
          setActive(false);
        }
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().repaint();
          }
        });
      }
    });
  }

  public boolean isPetri() {
    return false;
  }
  public boolean isColored() {
    return false;
  }
}
