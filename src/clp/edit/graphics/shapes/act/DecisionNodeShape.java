package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.DecisionDialog;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.ADecisionShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class DecisionNodeShape extends ADecisionShape {

  private static final long serialVersionUID = -1158045307775943118L;

  transient private DecisionDialog dialog;

  /**
   * CONSTRUCTOR
   * 
   */
  public DecisionNodeShape(GeneralShapesContainer shapesContainer) {
    super(20, 20, "c"+shapesContainer.getIncrentingCounterForActigramDecisions());
    dialog = new DecisionDialog(GeneralContext.getInstance().getFrame(), getName());
    dialog.setupInfo(getInfo());
  }

  @Override
  public void setChild(ABindingShape shape) {
    if (shape == null) {
      super.resetChildren();
    }
    else {
      switch (((BindingShape) shape).getBindingType()) {
        case DOWN_LEFT:
          setLeft((BindingShape) shape);
          super.setChild(getLeft());
          break;
        case DOWN_RIGHT:
          setRight((BindingShape) shape);
          super.setChild(getRight());
          break;
        case UP_LEFT:
          setLeftup((BindingShape) shape);
          shape.setParent(this);
          setDecisionUp(true);
          break;
        case UP_RIGHT:
          setRightup((BindingShape) shape);
          shape.setParent(this);
          setDecisionUp(true);
          break;

        default:
          break;
      }
    }
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    switch (bindingType) {
      case DOWN_LEFT:
        getLeft().setText(getInfo().getLeftPartName());
        getLeft().setChild(shape);
        break;
      case DOWN_RIGHT:
        getRight().setText(getInfo().getRightPartName());
        getRight().setChild(shape);
        break;
      case UP_LEFT:
        getLeftup().setText(getInfo().getLeftPartName());
        getLeftup().setOnlyChild(shape);
        ((ActionShape)shape).addEntryPoint(getLeftup());
        break;
      case UP_RIGHT:
        getRightup().setText(getInfo().getRightPartName());
        getRightup().setOnlyChild(shape);
        ((ActionShape)shape).addEntryPoint(getRightup());
        break;

      default:
        break;
    }
  }

  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    int sx = getPX()+offset;
    int sy = getPY();
    int[] px = {sx, sx-10, sx,    sx+10};
    int[] py = {sy, sy+10, sy+20, sy+10};
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    g.fillPolygon(px, py, 4);
    g.setColor(c);
    if (getLeft() != null) {
      getLeft().paintShape(g, offset);
    }
    if (getLeftup() != null) {
      getLeftup().paintShape(g, offset);
    }
    if (getRight() != null) {
      getRight().paintShape(g, offset);
    }
    if (getRightup() != null) {
      getRightup().paintShape(g, offset);
    }
  }

  @Override
  public void setName(String text) {
    getCustDialog().setupInfo(getInfo());
    if (getLeft() != null) {
      getLeft().setText(getInfo().getLeftPartName());
    }
    else if (getLeftup() != null) {
      getLeftup().setText(getInfo().getLeftPartName());
    }
    if (getRight() != null) {
      getRight().setText(getInfo().getRightPartName());
    }
    else if (getRightup() != null) {
      getRightup().setText(getInfo().getRightPartName());
    }
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    if (x > getPX()-getWidth() && x < getPX()+getWidth()/2
     && y > getPY() && y < getPY()+getHeight()) {

      return this;
    }
    if (getLeft() != null) {
      AShape s = getLeft().getSelectedShape(x, y);
      if (s != null) {
        return s;
      }
    }
    if (getRight() != null) {
      return getRight().getSelectedShape(x, y);
    }
    return null;
  }

  @Override
  public void gatherSelectedShapes(int x1, int y1, int x2, int y2, List<AShape> list) {
    int ref_x = getPX() - getWidth()/2;
    int ref_y = getPY() + getHeight();
    if (ref_x > x1 && ref_x < x2 && ref_y > y1 && ref_y < y2) {
      list.add(this);
    }
    if (getLeft() != null) {
      getLeft().getChild().gatherSelectedShapes(x1, y1, x2, y2, list);
    }
    if (getRight() != null) {
      getRight().getChild().gatherSelectedShapes(x1, y1, x2, y2, list);
    }
  }

  @Override
  public void gatherChildrenShapes(List<AShape> list) {
    list.add(this);
    if (getLeft() != null) {
      getLeft().getChild().gatherChildrenShapes(list);
    }
    if (getRight() != null) {
      getRight().getChild().gatherChildrenShapes(list);
    }
  }

  @Override
  public void gatherLinksToJoin(List<ABindingShape> list) {
    gatherOneLinkToJoin(list, getLeft());
    gatherOneLinkToJoin(list, getRight());
  }

  //
  private void gatherOneLinkToJoin(List<ABindingShape> list, ABindingShape b) {
    if (b != null) {
      AShape s = b.getChild();
      s.gatherLinksToJoin(list);
    }
  }

  public void shiftLeft(int delta) {
    getLeft().getChild().addToX(-delta);;
  }

  public void shiftRight(int delta) {
    getRight().getChild().addToX(delta);;
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new DecisionDialog(GeneralContext.getInstance().getFrame(), getName());
    }
    return dialog;
  }

  public DecisionDialog getCustDialog() {
    return (DecisionDialog) getDialog();
  }

  @Override
  public boolean generateCode(AContainer container) {
    boolean b = true;
    if (getLeft() != null) {
      b &= getLeft().getChild().generateCode(container);
    }
    if (b && getRight() != null) {
      return getRight().getChild().generateCode(container);
    }
    return b;
  }
}
