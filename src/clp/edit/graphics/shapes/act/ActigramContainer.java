package clp.edit.graphics.shapes.act;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AButtonsPanel;
import clp.edit.graphics.btn.AControlButton;
import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.btn.act.ActigramAutomaton;
import clp.edit.graphics.panel.ButtonsContainer.ButtonType;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.AInitialShape;
import clp.edit.graphics.shapes.AJoinShape;
import clp.edit.graphics.shapes.ARootShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.RedBindingShape;
import clp.edit.graphics.shapes.menu.ActigramContextMenu;
import clp.edit.panel.GraphicsPanel;

public class ActigramContainer extends AContainer {

  private static final long serialVersionUID = 8466214627103329382L;

  private ActivityShape activityShape;

  private ActigramAutomaton automaton;

  private GraphicsPanel graphicsPanel;

  /**
   * constructor
   * @param graphicsPanel 
   * 
   * @param buttonsPanel
   */
  public ActigramContainer(GraphicsPanel graphicsPanel) {
    super();
    this.graphicsPanel = graphicsPanel;
    automaton = new ActigramAutomaton(this, graphicsPanel);
  }

  @Override
  public Dimension paint(Graphics g, int offset, boolean isSelected) {
    Dimension dim = super.paint(g, offset, isSelected, activityShape);
    RedBindingShape rs = getRedShape();
    if (rs.isAttached() && rs.isReady()) {
      rs.paintShape(g, offset);
    }
    return dim;
  }

  @Override
  public ActivityShape getContainerShape() {
    return activityShape;
  }

  @Override
  public void setContainerShape(AContainerShape as) {
    activityShape = (ActivityShape) as;
  }

  @Override
  public boolean isSelected(int px, int py) {
    return super.isSelected(px, py, activityShape);
  }

  @Override
  public ButtonType getType() {
    return ButtonType.ACT;
  }

  /**
   * ADD a new shape
   */
  @Override
  public void addShape(AShape shape) {
    boolean isUpdateAllowed = !(getCurrent() instanceof DecisionNodeShape)
                           && !(getCurrent() instanceof ForkBindingShape);
    BindingType bindingType = (shape instanceof InitialNodeShape || shape instanceof EventNodeShape) ?
                                null : getRedShape().getBindingType();
    attachRedBinding(shape, isUpdateAllowed);
    addShape(shape, bindingType);
    if (isUpdateAllowed) {
      getRedShape().updateXYBoundaries();
      if (!(shape instanceof FinalNodeShape)) {
        setCurrent(shape);
      }
    }
    setDirty(true);
  }

  //
  private void attachRedBinding(AShape shape, boolean isUpdateAllowed) {
    if (getRedShape().isAttached()) {
      pinRedShape(shape);
      if (getSelected() != null) {
        getSelected().setSelected(false);
      }
    }
    if (shape instanceof InitialNodeShape ||
        shape instanceof EventNodeShape ||
        shape instanceof ActionNodeShape) {
      if (isUpdateAllowed) {
        getRedShape().setAttachTo(shape);
      }
    }
    else if (shape instanceof DecisionNodeShape) {
      getRedShape().setAttachToDecisionShape((DecisionNodeShape) shape, getCurrent().getPX());
    }
  }

  //
  private void pinRedShape(AShape shape) {
    BindingShape pin = getRedShape().pinIt();
    switch (pin.getBindingType()) {
      case DOWN_LEFT:
        getCurrent().setChild(pin);
        getRedShape().updateRightPartDecisionShape();
        break;
      case DOWN_RIGHT:
        super.setCurrent(getRedShape().getAttach());
        getCurrent().setChild(pin);
        getRedShape().setAttachTo(shape);
        break;
      default:
        getCurrent().setChild(pin);
        break;
    }
  }

  /**
   * @param currentButton the currentButton to set
   */
  public void setCurrentButton(AControlButton currentButton) {
    setCurrentButton(currentButton);
  }

  @Override
  public ActigramAutomaton getAutomaton() {
    return automaton;
  }

  public void deleteShape(AShape shape) {
    if (shape instanceof ARootShape) {
      ARootShape sibling = ((ARootShape)shape).getSibling();
      getContainerShape().setRoot(sibling);
      if (sibling == null) {
        automaton.resetButtons();
      }
    }
    else {
      ABindingShape b = shape.getParent();
      AShape p = b.getParent();
      if (p instanceof ForkBindingShape) {
        ((ForkBindingShape)p).delete(b);
      }
      else {
        p.setChild(shape.getChild());
      }
      if (shape.getChild() != null) {
        AShape c = shape.getChild().getChild();
        ARootShape root = (ARootShape) getContainerShape().getRoot();
        if (root.getSibling() != null) {
          int delta = shape.getHeight()+30;
          shape.addToY(delta);
          root.updateOtherBranches(shape, -delta);
          if (c instanceof JoinBindingShape) {
            c.addToY(-delta);
          }
        }
        if (p instanceof AInitialShape) {
          ((ActionShape)c).setAsInitial();
        }
        else if (p instanceof ARootShape) {
          ((ActionShape)c).setAsEventRoot((ActionShape) shape);
        }
      }
      getRedShape().setReady(false);
    }
  }

  @Override
  public void deleteAllFromShape(AShape shape) {
    super.deleteAllFromShape(shape);
    if (shape instanceof ActionNodeShape) {
      ((ActionNodeShape)shape).getEntries().clear();
    }
  }

  public void insertActionToShape(AShape shape) {
    ActionNodeShape act = new ActionNodeShape(getIncrementingActionNo());
    ABindingShape cb = shape.getChild();
    AShape c = cb.getChild();
    ABindingShape nb = new BindingShape(BindingType.DOWN, null);
    act.setChild(nb);
    nb.setChild(c);
    cb.setChild(act);
    int delta = shape.getHeight()+30;
    act.addToY(delta);
    if (c instanceof JoinBindingShape) {
      c.addToY(delta);
      ((AJoinShape)c).replaceParent(cb, nb);
    }
    ARootShape root = (ARootShape) getContainerShape().getRoot();
    if (root.getSibling() != null) {
      root.updateOtherBranches(shape, delta);
    }
    getRedShape().setReady(false);
  }

  public void insertDecisionToShape(AShape shape) {
    ABindingShape cb = shape.getChild();
    AShape c = cb.getChild();
    DecisionNodeShape dn = new DecisionNodeShape(graphicsPanel.getShapesContainer());      
    ActionNodeShape act = new ActionNodeShape(getIncrementingActionNo());
    BindingShape nbl = new BindingShape(BindingType.DOWN_LEFT, null);
    BindingShape nbr = new BindingShape(BindingType.DOWN_RIGHT, null);
    cb.setChild(dn);
    dn.setLeft(nbl);
    nbl.setChild(c);
    c.setXoffset(-80);
    dn.setRight(nbr);
    nbr.setChild(act);
    act.setXoffset(+80);
    int delta = shape.getHeight()+30;
    dn.addToY(delta);
    act.addToY(delta);
    getRedShape().setReady(false);
  }

  @Override
  public ABindingShape[] getChildren(AShape s, boolean isUp) {
    if (s instanceof DecisionNodeShape) {
      DecisionNodeShape d = (DecisionNodeShape) s;
      if (isUp) {
        return new ABindingShape[] {d.getLeft(), d.getRight(), d.getLeftup(), d.getRightup()};
      }
      return new ABindingShape[] {d.getLeft(), d.getRight()};
    }
    if (s instanceof ActionNodeShape || s instanceof InitialNodeShape ||
        s instanceof EventNodeShape || s instanceof JoinBindingShape) {
      return new ABindingShape[] {s.getChild()};
    }
    if (s instanceof ForkBindingShape) {
      ForkBindingShape f = (ForkBindingShape) s;
      List<ABindingShape> bs = f.getDestinations();
      ABindingShape[] cs = new ABindingShape[bs.size()];
      for (int i=0; i<bs.size(); i++) {
        ABindingShape b = bs.get(i);
        cs[i] = b;
      }
      return cs;
    }
    return null;
  }

  public void disableRedShape() {
    getRedShape().setReady(false);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
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
    ActigramContextMenu cmenu = new ActigramContextMenu(shape, genericContainer);
    cmenu.show(e.getComponent(), e.getX()+10, e.getY());
  }

  @Override
  public boolean isInMainBranch(ABindingShape b) {
    return true;
  }

  @Override
  public String getStringActionNo() {
    return "";
  }

  public void addJoin(JoinBindingShape shape) {
    ABindingShape child = shape.getChild();
    if (isRedShapeAbove(shape)) {
      // if attached element above given join shape -> attach it to join (evtl. create an action in between)
      AShape atShape = getRedShape().getAttach();
      if (atShape instanceof ARootShape) {
        ActionNodeShape act = new ActionNodeShape(getIncrementingActionNo());
        addShape(act);
        setCurrent(shape);
        graphicsPanel.getButtonsContainer().getActigramButtonsPanel().bindToCorrespondingShape(act);
        getRedShape().setAttachTo(child.getChild());
      }
      else if (atShape instanceof ActionShape) {
        setCurrent(shape);
        graphicsPanel.getButtonsContainer().getActigramButtonsPanel().bindToCorrespondingShape(atShape);
        getRedShape().setAttachTo(child.getChild());
      }
    }
    else {
      // create action+event above the given join shape
      EventNodeShape event = shape.addEventAbove();
      setCurrent(shape);
      graphicsPanel.getButtonsContainer().getActigramButtonsPanel().bindToCorrespondingShape(event);
      getRedShape().setAttachTo(child.getChild());
    }
  }

  //
  private boolean isRedShapeAbove(JoinBindingShape shape) {
    return getRedShape().isReady() && getRedShape().getAttach().getPY() < shape.getPY();
  }

  public void addFork(ForkBindingShape shape) {
    setCurrent(shape);
    ActionNodeShape act = new ActionNodeShape(getIncrementingActionNo());
    graphicsPanel.getButtonsContainer().getActigramButtonsPanel().placeCorrespondingShape(act);
  }

  //
  private int getIncrementingActionNo() {
    return GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForActigram();
  }

  @Override
  public AButtonsPanel getButtonsPanel() {
    return graphicsPanel.getButtonsContainer().getActigramButtonsPanel();
  }
}
