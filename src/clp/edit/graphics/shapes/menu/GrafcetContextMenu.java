package clp.edit.graphics.shapes.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.btn.gc.GrafcetAutomaton;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AFinalShape;
import clp.edit.graphics.shapes.AJoinShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.MultiBinding;
import clp.edit.graphics.shapes.gc.EventNodeShape;
import clp.edit.graphics.shapes.gc.ForkBindingShape;
import clp.edit.graphics.shapes.gc.GrafcetContainer;
import clp.edit.graphics.shapes.gc.JoinBindingShape;
import clp.edit.graphics.shapes.gc.TransitionNodeShape;

public class GrafcetContextMenu extends JPopupMenu {

  private static final long serialVersionUID = -390768966646277306L;

  private GeneralShapesContainer shapesContainer;
  private GrafcetContainer container;
  private GrafcetAutomaton automaton;

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
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener addToJoinListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.addToJoin((AJoinShape) shape);
      container.setDirty(true);
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

  private ActionListener insertActionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertActionToShape(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener insertTransitionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertAlternateTransitionToShape(shape);
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
  public GrafcetContextMenu(AShape shape, GeneralShapesContainer shapesContainer) {
    this.shape = shape;
    this.shapesContainer = shapesContainer;
    this.container = (GrafcetContainer)shapesContainer.getCurrentContainer();
    this.automaton = container.getAutomaton();
    setup(shape);
  }

  //
  private void setup(AShape shape) {
    JMenuItem item;
    item = new JMenuItem("select all below");
    item.addActionListener(selectListener);
    item.setEnabled(shape.getChild() != null || shape instanceof ForkBindingShape);
    add(item);
    if (shape instanceof ActionShape && isTransitionSelected()) {
      item = new JMenuItem("bind from selection");
      item.addActionListener(bindListener);
      add(item);
    }
    if (shape instanceof ActionShape || shape instanceof TransitionNodeShape || shape instanceof EventNodeShape) {
      if (hasEnoughChild(shape)) {
        add(new Separator());
        JMenu submenu = new JMenu("insert...");
        if (shape instanceof ActionShape) {
            item = new JMenuItem("alternative transition node");
            item.addActionListener(insertTransitionListener);
            submenu.add(item);
        }
        add(submenu);
          item = new JMenuItem("step node");
          item.addActionListener(insertActionListener);
          submenu.add(item);
      }
    }
    if (shape instanceof JoinBindingShape && isChildlessActionSelected()) {
      add(new Separator());
      item = new JMenuItem("add selection");
      item.addActionListener(addToJoinListener);
      add(item);
    }
    if (container.isDeleteAllowed(shape)) {
      add(new Separator());
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

  private boolean isChildlessActionSelected() {
    if (container.getRedShape().isAttached()) {
      AShape s = container.getRedShape().getAttach();
      return s instanceof ActionShape && s.getChild() == null;
    }
    return false;
  }

  //
  private boolean isTransitionSelected() {
    if (container.getRedShape().isAttached()) {
      return container.getRedShape().getAttach() instanceof TransitionNodeShape;
    }
    return false;
  }

  //
  private boolean hasEnoughChild(AShape shape) {
    ABindingShape b = shape.getChild();
    return b instanceof MultiBinding ||
        b != null && b.getChild() != null && b.getChild().getChild() != null;
  }
}
