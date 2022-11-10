package clp.edit.graphics.shapes.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.btn.act.ActigramAutomaton;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.act.ActigramContainer;
import clp.edit.graphics.shapes.act.ActionNodeShape;
import clp.edit.graphics.shapes.act.DecisionNodeShape;
import clp.edit.graphics.shapes.act.EventNodeShape;
import clp.edit.graphics.shapes.act.ForkBindingShape;
import clp.edit.graphics.shapes.act.InitialNodeShape;
import clp.edit.graphics.shapes.act.JoinBindingShape;

public class ActigramContextMenu extends JPopupMenu {

  private static final long serialVersionUID = -9177502870181776571L;

  private GeneralShapesContainer shapesContainer;
  private ActigramContainer container;
  private ActigramAutomaton automaton;

  private AShape shape;

  private ActionListener swapListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (shape instanceof DecisionNodeShape) {
        swapDecisionNode((DecisionNodeShape) shape);
      }
      else {
        swapActionNode((ActionNodeShape) shape);
      }
      container.selectFromRedShape();
      shapesContainer.refresh();
    }
  };

  private ActionListener selectListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.selectAllFromShape(shape);
      automaton.setMode(ActionMode.SELECT);
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

  private ActionListener insertDecisionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.insertDecisionToShape(shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener addJoinListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.addJoin((JoinBindingShape) shape);
      container.setDirty(true);
      shapesContainer.refresh();
    }
  };

  private ActionListener addForkListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      container.addFork((ForkBindingShape) shape);
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
  public ActigramContextMenu(AShape shape, GeneralShapesContainer shapesContainer) {
    this.shape = shape;
    this.shapesContainer = shapesContainer;
    this.container = (ActigramContainer)shapesContainer.getCurrentContainer();
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
    if (!(shape instanceof InitialNodeShape)) {
      add(new Separator());
      item = new JMenuItem("edit");
      item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().edit(shape);
          container.setDirty(true);
        }
      });
      add(item);
      add(new Separator());
      if (shape instanceof DecisionNodeShape) {
        item = new JMenuItem("swap");
        item.addActionListener(swapListener);
        add(item);
      }
      if (shape instanceof ActionNodeShape) {
        if (!((ActionNodeShape)shape).getEntries().isEmpty()) {
          item = new JMenuItem("swap");
          item.addActionListener(swapListener);
          add(item);
        }
      }
      if (shape instanceof EventNodeShape || shape instanceof ActionNodeShape) {
        JMenu submenu = new JMenu("insert...");
        submenu.setEnabled(shape.getChild() != null);
        add(submenu);
          item = new JMenuItem("action node");
          item.addActionListener(insertActionListener);
          submenu.add(item);
          if (isAddDecisionAllowed(shape)) {
            item = new JMenuItem("decision node");
            item.addActionListener(insertDecisionListener);
            submenu.add(item);
          }
      }
      else if (shape instanceof JoinBindingShape) {
        item = new JMenuItem("add join element");
        item.addActionListener(addJoinListener);
        add(item);
      }
      else if (shape instanceof ForkBindingShape) {
        item = new JMenuItem("add fork element");
        item.addActionListener(addForkListener);
        add(item);
      }
    }
    if (container.isDeleteAllowed(shape)) {
      item = new JMenuItem("delete node");
      item.addActionListener(deleteListener);
      add(item);
    }
    if (shape instanceof InitialNodeShape || shape instanceof EventNodeShape || shape instanceof ActionNodeShape) {
      item = new JMenuItem("delete all below");
      item.addActionListener(deleteAllListener);
      item.setEnabled(shape.getChild() != null);
      add(item);
    }
    pack();
  }

  //
  private boolean isAddDecisionAllowed(AShape shape) {
    if (shape instanceof ActionNodeShape) {
      ABindingShape b = shape.getChild();
      if (b != null && b.getChild() instanceof ActionNodeShape) {
        return true;
      }
    }
    return false;
  }

  //
  private void swapDecisionNode(DecisionNodeShape shape) {
    shape.swapInfo();
    BindingShape left = shape.getLeft();
    if (left != null) {
      BindingShape right = shape.getRight();
      if (right != null) {
        swapCoordinates(left.getChild(), right.getChild());
        shape.setRight(shape.getLeft());
        shape.setLeft(right);
      }
      else if (shape.getRightup() != null) {
        left.getChild().setXoffset(-left.getChild().getXoffset());
        shape.setRight(left);
        shape.setLeft(null);
        BindingShape rightup = shape.getRightup();
        rightup.setXshift(shape.getPX() - rightup.getChild().getPX());
        rightup.setBindingType(BindingType.UP_LEFT);
        shape.setLeftup(rightup);
        shape.setRightup(null);
      }
    }
    else if (shape.getLeftup() != null) {
      BindingShape leftup = shape.getLeftup();
      BindingShape rightup = shape.getRightup();
      if (rightup != null) {
        swapCoordinates(leftup.getChild(), rightup.getChild());
        rightup.setBindingType(BindingType.UP_LEFT);
        leftup.setBindingType(BindingType.UP_RIGHT);
        shape.setRightup(leftup);
        shape.setLeftup(rightup);
      }
      else if (shape.getRight() != null) {
        leftup.setXshift(shape.getPX() - leftup.getChild().getPX());
        leftup.setBindingType(BindingType.UP_RIGHT);
        shape.setRightup(leftup);
        shape.setLeftup(null);
        BindingShape right = shape.getRight();
        right.getChild().setXoffset(-right.getChild().getXoffset());
        shape.setLeft(right);
        shape.setRight(null);
      }
    }
  }

  //
  private void swapCoordinates(AShape lshape, AShape rshape) {
    int rshift = rshape.getXoffset();
    rshape.setXoffset(lshape.getXoffset());
    lshape.setXoffset(rshift);
  }

  //
  private void swapActionNode(ActionNodeShape shape) {
    for (ABindingShape b : shape.getEntries()) {
      switch (b.getBindingType()) {
        case UP_LEFT:
          b.setBindingType(BindingType.UP_LEFT_RIGHT);
          break;
        case UP_LEFT_RIGHT:
          b.setBindingType(BindingType.UP_LEFT);
          break;
        case UP_RIGHT:
          b.setBindingType(BindingType.UP_RIGHT_LEFT);
          break;
        case UP_RIGHT_LEFT:
          b.setBindingType(BindingType.UP_RIGHT);
          break;

        default:
          break;
      }
    }
  }
}
