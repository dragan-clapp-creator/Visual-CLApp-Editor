package clp.edit.graphics.shapes.gc;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AButtonsPanel;
import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.btn.gc.GrafcetAutomaton;
import clp.edit.graphics.dial.TransitionDialog;
import clp.edit.graphics.panel.ButtonsContainer.ButtonType;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.AEventShape;
import clp.edit.graphics.shapes.AForkShape;
import clp.edit.graphics.shapes.AJoinShape;
import clp.edit.graphics.shapes.ARootShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.MultiBinding;
import clp.edit.graphics.shapes.RedBindingShape;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.menu.GrafcetContextMenu;
import clp.edit.panel.GraphicsPanel;

public class GrafcetContainer extends AContainer {

  private static final long serialVersionUID = 2304688058232161487L;

  private GrafcetShape containerShape;
  private GrafcetAutomaton automaton;

  private GraphicsPanel graphicsPanel;

  /**
   * CONSTRUCTOR
   * @param graphicsPanel 
   */
  public GrafcetContainer(GraphicsPanel graphicsPanel) {
    super();
    this.graphicsPanel = graphicsPanel;
    automaton = new GrafcetAutomaton(this, graphicsPanel.getButtonsContainer().getGrafcetButtonsPanel());
  }

  @Override
  public Dimension paint(Graphics g, int offset, boolean isSelected) {
    Dimension dim = super.paint(g, offset, isSelected, containerShape);
    RedBindingShape rs = getRedShape();
    if (rs.isAttached() && rs.isReady()) {
      rs.paintShape(g, offset);
    }
    return dim;
  }

  @Override
  public AContainerShape getContainerShape() {
    return containerShape;
  }

  @Override
  public void setContainerShape(AContainerShape as) {
    containerShape =  (GrafcetShape) as;
  }

  @Override
  public boolean isSelected(int px, int py) {
    return super.isSelected(px, py, containerShape);
  }

  @Override
  public ButtonType getType() {
    return ButtonType.SFC;
  }

  @Override
  public void addShape(AShape shape) {
    boolean isUpdateAllowed = !(getCurrent() instanceof ForkBindingShape);
    BindingType bindingType = getRedShape().getBindingType();
    attachRedBinding(shape, isUpdateAllowed);
    addShape(shape, bindingType);
    if (isUpdateAllowed) {
      getRedShape().updateXYBoundaries();
//    if (shape instanceof FinalNodeShape) {
//      ((FinalNodeShape)shape).setDeactivationForParent();
//    }
//    else {
      setCurrent(shape);
//    }
      setDirty(true);
    }
  }

  @Override
  public void bindShape(AShape shape) {
    if (!(shape instanceof InvisibleNode) &&
        getRedShape().getAttach() instanceof TransitionNodeShape && shape.getChild() != null) {
      automaton.bindShape(shape);
      getRedShape().setAttachTo(null);
    }
    else {
      super.bindShape(shape);
    }
  }

  //
  private void attachRedBinding(AShape shape, boolean isUpdateAllowed) {
    if (isUpdateAllowed) {
      getRedShape().setAttachTo(shape);
    }
  }

  @Override
  public GrafcetAutomaton getAutomaton() {
    return automaton;
  }

  public void disableRedShape() {
    getRedShape().setReady(false);
    getRedShape().setAttachTo(null);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  @Override
  public ABindingShape[] getChildren(AShape s, boolean isUp) {
    if (s instanceof ActionShape || s instanceof TransitionNodeShape ||
        s instanceof EventNodeShape || s instanceof JoinBindingShape) {
      if (s.getChild() instanceof TriBinding) {
        TriBinding t = (TriBinding)s.getChild();
        int size = t.getLeft() == null || t.getRight() == null ? 2 : 3;
        ABindingShape[] cs = new ABindingShape[size];
        int i = 0;
        if (t.getLeft() != null) {
          cs[i++] = t.getLeft();
        }
        cs[i++] = t.getMiddle();
        if (t.getRight() != null) {
          cs[i] = t.getRight();
        }
        return cs;
      }
      else if (s.getChild() instanceof MultiBinding) {
        MultiBinding multi = (MultiBinding) s.getChild();
        ArrayList<ABindingShape> children = multi.getChildren();
        ABindingShape[] cs = new ABindingShape[children.size()];
        for (int i=0; i<children.size(); i++) {
          cs[i] = children.get(i);
        }
      }
      return new ABindingShape[] {s.getChild()};
    }
    if (s instanceof ForkBindingShape) {
      ForkBindingShape f = (ForkBindingShape) s;
      List<ABindingShape> bs = f.getDestinations();
      ABindingShape[] cs = new ABindingShape[bs.size()];
      for (int i=0; i<bs.size(); i++) {
        cs[i] = bs.get(i);
      }
      return cs;
    }
    return null;
  }

  @Override
  public void handleAction(AShape sel, int px, int py, boolean isMultiSelect) {
    if ((automaton.getMode() == ActionMode.NONE || isMultiSelect) && sel != null) {
      automaton.performAction(ActionMode.SELECT, null, sel, isMultiSelect);
    }
    else {
      if (sel == null && automaton.getMode() == ActionMode.JOIN) {
        automaton.checkForJoinType(px, py);
      }
      automaton.performAction(null, sel);
    }
  }

  @Override
  public void showContextMenu(GeneralShapesContainer genericContainer, AShape shape, MouseEvent e) {
    GrafcetContextMenu cmenu = new GrafcetContextMenu(shape, genericContainer);
    cmenu.show(e.getComponent(), e.getX()+10, e.getY());
  }

  @Override
  public void replaceJoin(AJoinShape join) {
    AShape child = join.getChild().getChild();
    ABindingShape pbind = join.getParents().get(0);
    TransitionNodeShape trans = new TransitionNodeShape((GrafcetShape) getContainerShape(), (TransitionDialog) join.getDialog());
    trans.addToY(child.getHeight()+30);
    pbind.setChild(trans);
    ABindingShape nb = new BindingShape(BindingType.DOWN, null);
    trans.setChild(nb);
    nb.setChild(child);
  }

  public void insertActionToShape(AShape shape) {
    StepNodeShape step = new StepNodeShape(getIncrementingActionNo());
    TransitionNodeShape trans = new TransitionNodeShape((GrafcetShape) getContainerShape());
    ABindingShape cb = shape.getChild();
    ABindingShape nb1 = new BindingShape(BindingType.DOWN, null);
    ABindingShape nb2 = new BindingShape(BindingType.DOWN, null);
    shape.setChild(nb1);
    if (shape instanceof ActionShape) {
      trans.addToY(shape.getHeight()+30);
      step.addToY(trans.getHeight()+30);
      nb1.setChild(trans);
      trans.setChild(nb2);
      step.setChild(cb);
      nb2.setChild(step);
    }
    else {
      step.addToY(shape.getHeight()+30);
      trans.addToY(step.getHeight()+30);
      trans.setChild(cb);
      nb2.setChild(trans);
      step.setChild(nb2);
      nb1.setChild(step);
    }
    ARootShape root = (ARootShape) getContainerShape().getRoot();
    if (root.getSibling() != null) {
      int delta = shape.getHeight()+30;
      root.updateOtherBranches(shape, delta);
    }
    getRedShape().setReady(false);
  }

  public void insertAlternateTransitionToShape(AShape shape) {
    TransitionNodeShape trans = new TransitionNodeShape((GrafcetShape) getContainerShape());
    StepNodeShape step = new StepNodeShape(getIncrementingActionNo());
    BindingShape nb1 = new BindingShape(BindingType.DOWN, null);
    BindingShape nb2 = new BindingShape(BindingType.DOWN, null);
    nb1.setParent(shape);
    nb1.setChild(trans);
    trans.addToY(shape.getHeight()+30);
    trans.setChild(nb2);
    nb2.setChild(step);
    step.addToY(30);
    BindingShape cb = (BindingShape) shape.getChild();
    if (cb instanceof MultiBinding) {
      MultiBinding multi = (MultiBinding) cb;
      multi.addChild(nb1);
      setRightmost(multi.getRightmost());
      updateLimits(containerShape, shape);
    }
    else {
      TriBinding tri;
      if (cb instanceof TriBinding) {
        tri = (TriBinding) cb;
        if (tri.isFull()) {
          MultiBinding multi = new MultiBinding();
          multi.addChildren(tri);
          multi.addChild(nb1);
          shape.setChild(multi);
          setRightmost(multi.getRightmost());
          updateLimits(containerShape, shape);
        }
        else {
          tri.addChild(nb1);
          tri.addToX(80, shape);
          tri.getLeft().setXshift(0);
        }
      }
      else {
        tri = new TriBinding();
        tri.addChild(cb);
        tri.addChild(nb1);
        shape.setChild(tri);
        trans.addToX(80);
        cb.getChild().addToX(-80);
      }
    }
    getRedShape().setReady(false);
  }

  public void deleteShape(AShape shape) {
    if (shape instanceof AEventShape) {
      ARootShape prev = (ARootShape) getContainerShape().getRoot();
      ARootShape next = prev.getSibling();
      while (next != null && next != shape) {
        prev = next;
        next = next.getSibling();
      }
      if (next != null) {
        prev.setSibling(next.getSibling());
      }
      else {
        automaton.refreshButtons();
      }
    }
    else if (shape.getChild() == null) {
      AShape p = shape.getParent().getParent();
      p.setChild(null);
    }
    else {
      AShape p = shape.getParent().getParent();
      AShape next = shape.getChild().getChild();
      if (next.getChild() != null) {
        p.setChild(next.getChild());
      }
      ARootShape root = (ARootShape) getContainerShape().getRoot();
      if (root.getSibling() != null) {
        shape.addToY(shape.getHeight()+30);
        int delta = shape.getHeight()+30;
        root.updateOtherBranches(shape, -delta);
      }
    }
    getRedShape().setReady(false);
  }

  @Override
  public boolean isDeleteAllowed(AShape shape) {
    if (shape instanceof AEventShape || shape.getChild() == null) {
      return true;
    }
    if (shape.getParent().getParent().getParent() == null ||
        shape.getChild().getChild() == null) {
      return false;
    }
    if (shape instanceof AForkShape ||
        shape.getParent().getParent() instanceof AForkShape || 
        shape.getChild().getChild() instanceof AJoinShape) {
      return false;
    }
    for (ABindingShape b : getBindings()) {
      if (b.getChild().equals(shape)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isInMainBranch(ABindingShape b)  {
    AShape p = b.getParent();
    while (p != null && p.getParent() != null && !(p instanceof InvisibleNode)) {
      p = p.getParent().getParent();
    }
    return p != getContainerShape().getRoot(); 
  }

  public void addToJoin(AJoinShape shape) {
    ActionShape s = (ActionShape) getRedShape().getAttach();
    getRedShape().setAttachTo(shape);
    setCurrent(shape);
    bindShape(s);
  }

  @Override
  public String getStringActionNo() {
    return "" + GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getActionNoForGrafcet();
  }

  //
  private int getIncrementingActionNo() {
    return GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForGrafcet();
  }

  @Override
  public AButtonsPanel getButtonsPanel() {
    return graphicsPanel.getButtonsContainer().getGrafcetButtonsPanel();
  }
}
