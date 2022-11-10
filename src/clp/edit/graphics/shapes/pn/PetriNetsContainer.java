package clp.edit.graphics.shapes.pn;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AButtonsPanel;
import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.btn.pn.PetriNetsAutomaton;
import clp.edit.graphics.btn.pn.PetriNetsButtonsPanel;
import clp.edit.graphics.dial.PNTransitionDialog.TransitionPosition;
import clp.edit.graphics.panel.ButtonsContainer.ButtonType;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.AEventShape;
import clp.edit.graphics.shapes.ARootShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.RedBindingShape;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.menu.PetriNetsContextMenu;
import clp.edit.panel.GraphicsPanel;

public class PetriNetsContainer extends AContainer {

  private static final long serialVersionUID = 3292146692884839701L;

  private AContainerShape containerShape;
  private PetriNetsAutomaton automaton;

  private boolean isColored;

  private PetriNetsButtonsPanel pnButtonsPanel;

  /**
   * CONSTRUCTOR
   * @param graphicsPanel 
   * 
   * @param isColored 
   */
  public PetriNetsContainer(GraphicsPanel graphicsPanel, boolean isColored) {
    super();
    this.pnButtonsPanel = graphicsPanel.getButtonsContainer().getPNButtonsPanel(isColored);
    this.isColored = isColored;
    automaton = new PetriNetsAutomaton(this, pnButtonsPanel);
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
    containerShape = as;
  }

  @Override
  public boolean isSelected(int px, int py) {
    return super.isSelected(px, py, containerShape);
  }

  @Override
  public ButtonType getType() {
    return isColored ? ButtonType.CPN : ButtonType.WPN;
  }

  @Override
  public void addShape(AShape shape) {
    BindingType bindingType = getRedShape().getBindingType();
    getRedShape().setAttachTo(shape);
    addShape(shape, bindingType);
    getRedShape().updateXYBoundaries();
    setCurrent(shape);
    setDirty(true);
  }

  @Override
  public void bindShape(AShape shape) {
    if (!(shape instanceof InvisibleNode)) {
      String t = ((PetriNetsShape)getContainerShape()).getWeightOrColor();
      AShape sel = getRedShape().getAttach();
      if (sel == null) {
        sel = getSelected();
      }
      if (sel == shape) {
        return;
      }
      if (sel instanceof TransitionNodeShape) {
        TransitionNodeShape tr = (TransitionNodeShape) sel;
        PNBindingShape pin;
        if (tr.getPX() < shape.getPX()) {
          pin = new PNBindingShape(BindingType.UP_LEFT, t);
        }
        else {
          pin = new PNBindingShape(BindingType.UP_RIGHT, t);
        }
        pin.setOnlyChild(shape);
        tr.setChild(pin);
        getRedShape().setAttachTo(null);
        ((ActionShape)shape).addEntryPoint(pin);
      }
      else if (sel instanceof PlaceNodeShape) {
        PlaceNodeShape pl = (PlaceNodeShape) sel;
        PNBindingShape pin;
        TransitionNodeShape tr = (TransitionNodeShape)shape;
        if (pl.getPX() < shape.getPX()) {
          if (pl.getPY() > shape.getPY()) {
            pin = new PNBindingShape(BindingType.UP_LEFT, t);
            pin.setXshift(-9);
            tr.setUpleft(pin);
          }
          else {
            pin = new PNBindingShape(BindingType.DOWN_RIGHT, t);
            pin.setParent(pl);
            BindingShape cb = (BindingShape) pl.getChild();
            TriBinding tri;
            if (cb instanceof TriBinding) {
              tri = (TriBinding) cb;
              tri.addChild(pin);
              tri.addToX(-80, shape);
              tri.getLeft().setXshift(0);
            }
            else if (cb != null) {
              tri = new TriBinding();
              tri.addChild(pin);
              tri.addChild(cb);
              pl.setChild(tri);
              tr.addToX(-80);
            }
            else {
              pl.setChild(pin);
            }
            tr.setUpleft(pin);
          }
        }
        else {
          if (pl.getPY() > shape.getPY()) {
            pin = new PNBindingShape(BindingType.UP_RIGHT, t);
            pin.setXshift(+9);
            tr.setUpright(pin);
            pl.setChild(pin);
          }
          else {
            pin = new PNBindingShape(BindingType.DOWN_LEFT, t);
            BindingShape cb = (BindingShape) pl.getChild();
            TriBinding tri;
            if (cb instanceof TriBinding) {
              tri = (TriBinding) cb;
              tri.addChild(pin);
              tri.addToX(80, shape);
              tri.getLeft().setXshift(0);
            }
            else if (cb != null) {
              tri = new TriBinding();
              tri.addChild(pin);
              tri.addChild(cb);
              pl.setChild(tri);
              tr.addToX(80);
            }
            else {
              pl.setChild(pin);
            }
            tr.setUpright(pin);
          }
        }
        pin.setOnlyChild(shape);
        getRedShape().setAttachTo(null);
      }
    }
    else {
      super.bindShape(shape);
    }
  }

  @Override
  public PetriNetsAutomaton getAutomaton() {
    return automaton;
  }

  public void disableRedShape() {
    getRedShape().setReady(false);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  @Override
  public ABindingShape[] getChildren(AShape s, boolean isUp) {
    if (s instanceof PlaceNodeShape || s instanceof TransitionNodeShape) {
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
      return new ABindingShape[] {s.getChild()};
    }
    return null;
  }

  @Override
  public void handleAction(AShape sel, int px, int py, boolean isMultiSelect) {
    if ((automaton.getMode() == ActionMode.NONE || isMultiSelect) && sel != null) {
      automaton.performAction(ActionMode.SELECT, null, sel, isMultiSelect);
    }
    else {
      automaton.performAction(null, sel);
    }
  }

  @Override
  public void showContextMenu(GeneralShapesContainer genericContainer, AShape shape, MouseEvent e) {
    PetriNetsContextMenu cmenu = new PetriNetsContextMenu(shape, genericContainer);
    cmenu.show(e.getComponent(), e.getX()+10, e.getY());
  }

  public void insertPlaceToShape(AShape shape) {
    PetriNetsShape pnShape = (PetriNetsShape) getContainerShape();
    PlaceNodeShape place = new PlaceNodeShape(getIncrementingActionNo(), pnShape);
    TransitionNodeShape trans = new TransitionNodeShape(pnShape, pnShape.getDefaultTransitionType(), TransitionPosition.MIDDLE);
    ABindingShape cb = shape.getChild();
    String t = pnShape.getWeightOrColor();
    ABindingShape nb1 = new PNBindingShape(BindingType.DOWN, t);
    ABindingShape nb2 = new PNBindingShape(BindingType.DOWN, t);
    shape.setChild(nb1);
    if (shape instanceof PlaceNodeShape) {
      trans.addToY(shape.getHeight()+30);
      place.addToY(trans.getHeight()+30);
      nb1.setChild(trans);
      trans.setChild(nb2);
      place.setChild(cb);
      nb2.setChild(place);
    }
    else {
      place.addToY(shape.getHeight()+30);
      trans.addToY(place.getHeight()+30);
      trans.setChild(cb);
      nb2.setChild(trans);
      place.setChild(nb2);
      nb1.setChild(place);
    }
    ARootShape root = (ARootShape) getContainerShape().getRoot();
    if (root.getSibling() != null) {
      int delta = shape.getHeight()+30;
      root.updateOtherBranches(shape, delta);
    }
    getRedShape().setReady(false);
  }

  public void insertAlternateTransitionToPlace(AShape shape) {
    BindingShape cb = (BindingShape) shape.getChild();
    PetriNetsShape pnShape = (PetriNetsShape) getContainerShape();
    TransitionNodeShape trans = new TransitionNodeShape(pnShape, pnShape.getDefaultTransitionType(), TransitionPosition.MIDDLE);
    PlaceNodeShape place = new PlaceNodeShape(getIncrementingActionNo(), pnShape);
    String t = pnShape.getWeightOrColor();
    PNBindingShape nb1 = new PNBindingShape(BindingType.DOWN, t);
    PNBindingShape nb2 = new PNBindingShape(BindingType.DOWN, t);
    nb1.setParent(shape);
    nb1.setChild(trans);
    trans.addToY(shape.getHeight()+30);
    trans.setChild(nb2);
    place.addToY(30);
    nb2.setChild(place);
    TriBinding tri;
    if (cb instanceof TriBinding) {
      tri = (TriBinding) cb;
      tri.addChild(nb1);
      tri.addToX(80, shape);
      tri.getLeft().setXshift(0);
    }
    else {
      tri = new TriBinding();
      tri.addChild(cb);
      tri.addChild(nb1);
      shape.setChild(tri);
      trans.addToX(80);
      cb.getChild().addToX(-80);
    }
    updateLimits(containerShape, place);
    updateLimits(containerShape, cb.getChild());
    getRedShape().setReady(false);
  }

  public void insertPlaceUpLeftFromTransition(AShape shape) {
    InvisibleNode init = new InvisibleNode();
    init.setYoffset(shape.getPY()-90);
    init.setXoffset(shape.getPX()-80);
    PNBindingShape b = new PNBindingShape(BindingType.NONE, null);
    init.setChild(b);
    PlaceNodeShape place = new PlaceNodeShape(getIncrementingActionNo(), (PetriNetsShape) getContainerShape());
    b.setChild(place);
    String t = ((PetriNetsShape)getContainerShape()).getWeightOrColor();
    PNBindingShape nb = new PNBindingShape(BindingType.DOWN, t);
    place.setChild(nb);
    nb.setOnlyChild(shape);
    nb.setXshift(-9);
    ((TransitionNodeShape)shape).setUpleft(nb);
    bindShape(init);
  }

  public void insertPlaceUpRightFromTransition(AShape shape) {
    InvisibleNode init = new InvisibleNode();
    init.setYoffset(shape.getPY()-90);
    init.setXoffset(shape.getPX()+80);
    PNBindingShape b = new PNBindingShape(BindingType.NONE, null);
    init.setChild(b);
    PlaceNodeShape place = new PlaceNodeShape(getIncrementingActionNo(), (PetriNetsShape) getContainerShape());
    b.setChild(place);
    String t = ((PetriNetsShape)getContainerShape()).getWeightOrColor();
    PNBindingShape nb = new PNBindingShape(BindingType.DOWN, t);
    place.setChild(nb);
    nb.setOnlyChild(shape);
    nb.setXshift(+9);
    ((TransitionNodeShape)shape).setUpright(nb);
    bindShape(init);
  }

  public void insertPlaceBelowTransition(TransitionNodeShape trans) {
    PlaceNodeShape place = new PlaceNodeShape(getIncrementingActionNo(), (PetriNetsShape) getContainerShape());
    ABindingShape b = trans.getChild();
    if (b instanceof PNBindingShape) {
      createTwinBinding(place, trans, (PNBindingShape)b);
    }
    else {
      createTriBinding(place, trans, (TriBinding)b);
    }
  }

  //
  private int getIncrementingActionNo() {
    return GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForPetriNets();
  }

  //
  private void createTwinBinding(PlaceNodeShape place, TransitionNodeShape trans, PNBindingShape cb) {
    TriBinding twin = new TriBinding();
    String t = ((PetriNetsShape)getContainerShape()).getWeightOrColor();
    PNBindingShape nb = new PNBindingShape(BindingType.DOWN, t);
    nb.setParent(trans);
    nb.setChild(place, trans.getParent().getParent().getName());
    place.addToX(80);
    place.addToY(trans.getHeight()+30);
    twin.addChild(cb);
    twin.addChild(nb);
    trans.setChild(twin);
    trans.setupDialogHelpers();
    AShape c = cb.getChild();
    c.addToX(-80);
    getRedShape().setReady(false);
  }

  //
  private void createTriBinding(PlaceNodeShape place, TransitionNodeShape trans, TriBinding twin) {
    TriBinding tri = new TriBinding();
    PNBindingShape lb = (PNBindingShape) twin.getLeft();
    PNBindingShape rb = (PNBindingShape) twin.getMiddle();
    String t = ((PetriNetsShape)getContainerShape()).getWeightOrColor();
    PNBindingShape nb = new PNBindingShape(BindingType.DOWN, t);
    nb.setParent(trans);
    nb.setChild(place);
    place.addToY(trans.getHeight()+30);
    tri.addChild(lb);
    tri.addChild(nb);
    tri.addChild(rb);
    trans.setChild(tri);
    trans.setupDialogHelpers();
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
    else {
      AShape p = shape.getParent().getParent();
      if (p.getChild() instanceof TriBinding) {
        TriBinding tri = (TriBinding)p.getChild();
        tri.delete(shape.getParent(), shape, false);
        if (tri.getChild() == null) {
          setCurrent(p);
          getRedShape().setAttachTo(p);
          return;
        }
      }
      else if (shape.getChild() == null) {
        p.setChild(null);
      }
      else {
        AShape next = shape.getChild().getChild().getChild().getChild();
        p.setChild(next.getParent());
        ARootShape root = (ARootShape) getContainerShape().getRoot();
        if (root.getSibling() != null) {
          shape.addToY(shape.getHeight()+30);
          int delta = shape.getHeight()+30;
          root.updateOtherBranches(shape, -delta);
        }
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
        shape.getChild().getChild().getChild() == null) {
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
    while (p != null && !(p instanceof InvisibleNode)) {
      p = p.getParent().getParent();
    }
    return p != getContainerShape().getRoot(); 
  }

  @Override
  public String getStringActionNo() {
    return "" + GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getActionNoForPetriNets();
  }

  @Override
  public AButtonsPanel getButtonsPanel() {
    return pnButtonsPanel;
  }

  @Override
  public boolean isPetri() {
    return true;
  }

  @Override
  public boolean isColored() {
    return isColored;
  }
}
