package clp.edit.graphics.shapes.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.btn.pn.PetriNetsAutomaton;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.AFinalShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.pn.FinalNodeShape;
import clp.edit.graphics.shapes.pn.PNBindingShape;
import clp.edit.graphics.shapes.pn.PetriNetsContainer;
import clp.edit.graphics.shapes.pn.PlaceNodeShape;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;

public class PetriNetsContextMenu extends JPopupMenu {

  private static final long serialVersionUID = 5283224176801864377L;

  private GeneralShapesContainer shapesContainer;
  private PetriNetsContainer container;
  private PetriNetsAutomaton automaton;

  private AShape shape;

  private ActionListener selectListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.selectAllFromShape(shape);
      automaton.setMode(ActionMode.SELECT);
      shapesContainer.refresh();
    }
  };

  private ActionListener bindListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.bindShape(shape);
      container.getRedShape().setAttachTo(null);
      shapesContainer.refresh();
    }
  };

  private ActionListener deleteListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.deleteShape(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener deleteAllListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.deleteAllFromShape(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener insertPlaceListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertPlaceToShape(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener insertTransitionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertAlternateTransitionToPlace(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener insertPlaceUpLeftListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertPlaceUpLeftFromTransition(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener insertPlaceUpRightListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertPlaceUpRightFromTransition(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener insertPlaceBelowListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertPlaceBelowTransition((TransitionNodeShape) shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  /**
   * CONSTRUCTOR
   * 
   * @param frame
   * @param shape
   * @param shapesContainer
   */
  public PetriNetsContextMenu(AShape shape, GeneralShapesContainer shapesContainer) {
    this.shape = shape;
    this.shapesContainer = shapesContainer;
    this.container = (PetriNetsContainer)shapesContainer.getCurrentContainer();
    this.automaton = container.getAutomaton();
    setup(shape);
  }

  //
  private void setup(AShape shape) {
    JMenuItem item;
    item = new JMenuItem("select all below");
    item.addActionListener(selectListener);
    item.setEnabled(shape.getChild() != null);
    add(item);
    if (shape instanceof ActionShape || shape instanceof TransitionNodeShape) {
      if (shape.getChild() != null) {
        add(new Separator());
        JMenu submenu = new JMenu("insert...");
        if (shape instanceof PlaceNodeShape) {
          if (isAuthorizedTransitionSelected()) {
            item = new JMenuItem("bind from selection");
            item.addActionListener(bindListener);
            add(item);
          }
          item = new JMenuItem("alternative transition");
          item.addActionListener(insertTransitionListener);
          item.setEnabled(!isTriBindingFull(shape));
          submenu.add(item);
        }
        else if (shape instanceof TransitionNodeShape) {
          if (isAuthorizedPlaceSelected()) {
            item = new JMenuItem("bind from selection");
            item.addActionListener(bindListener);
            add(item);
          }
          item = new JMenuItem("alternative place up left");
          item.addActionListener(insertPlaceUpLeftListener);
          item.setEnabled(((TransitionNodeShape)shape).getUpleft() == null);
          submenu.add(item);
          item = new JMenuItem("alternative place up right");
          item.addActionListener(insertPlaceUpRightListener);
          item.setEnabled(((TransitionNodeShape)shape).getUpright() == null);
          submenu.add(item);
          item = new JMenuItem("alternative place below");
          item.addActionListener(insertPlaceBelowListener);
          item.setEnabled(isEnoughRoomToAdd(shape));
          submenu.add(item);
        }
        add(submenu);
          item = new JMenuItem("new place");
          item.addActionListener(insertPlaceListener);
          item.setEnabled(shape.getChild() instanceof PNBindingShape);
          submenu.add(item);
      }
      else {
        if (shape instanceof TransitionNodeShape && isAuthorizedPlaceSelected() ||
            shape instanceof PlaceNodeShape && isAuthorizedTransitionSelected()) {
          item = new JMenuItem("bind from selection");
          item.addActionListener(bindListener);
          add(item);
        }
        if (shape instanceof FinalNodeShape) {  // final transition
          JMenu submenu = new JMenu("insert...");
          item = new JMenuItem("alternative place up left");
          item.addActionListener(insertPlaceUpLeftListener);
          item.setEnabled(((TransitionNodeShape)shape).getUpleft() == null);
          submenu.add(item);
          item = new JMenuItem("alternative place up right");
          item.addActionListener(insertPlaceUpRightListener);
          item.setEnabled(((TransitionNodeShape)shape).getUpright() == null);
          submenu.add(item);
        }
      }
    }
    add(new Separator());
    if (container.isDeleteAllowed(shape)) {
      item = new JMenuItem("delete node");
      item.addActionListener(deleteListener);
      add(item);
    }
    if (!(shape instanceof AFinalShape)) {
      item = new JMenuItem("delete all below");
      item.addActionListener(deleteAllListener);
      item.setEnabled(shape.getChild() != null);
      add(item);
    }
    pack();
  }

  //
  private boolean isTriBindingFull(AShape shape) {
    BindingShape cb = (BindingShape) shape.getChild();
    if (cb instanceof TriBinding) {
      return ((TriBinding)cb).isFull();
    }
    return false;
  }

  //
  private boolean isAuthorizedTransitionSelected() {
    if (container.getRedShape().isReady() && container.getRedShape().isAttached()) {
      return container.getRedShape().getAttach() instanceof TransitionNodeShape;
    }
    if (container.getSelected() instanceof TransitionNodeShape) {
      return isEnoughRoomToAdd(container.getSelected());
    }
    return false;
  }

  //
  private boolean isAuthorizedPlaceSelected() {
    if (container.getRedShape().isReady() && container.getRedShape().isAttached()) {
      return container.getRedShape().getAttach() instanceof PlaceNodeShape;
    }
    if (container.getSelected() instanceof PlaceNodeShape) {
      return isEnoughRoomToAdd(container.getSelected());
    }
    return false;
  }

  //
  private boolean isEnoughRoomToAdd(AShape selected) {
    if (selected.getChild() instanceof TriBinding) {
      return ((TriBinding)selected.getChild()).getRight() == null;
    }
    return true;
  }
}
