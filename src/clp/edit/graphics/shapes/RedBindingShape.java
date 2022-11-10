package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.graphics.shapes.act.DecisionNodeShape;
import clp.edit.graphics.shapes.pn.FinalNodeShape;

public class RedBindingShape extends BindingShape {

  private static final long serialVersionUID = -6553720501310905527L;

  private Color color;

  private AShape attach;

  private boolean isReady;

  public RedBindingShape(BindingType b) {
    super(b, null);
    color = Color.red;
    isReady = true;
  }

  /**
   * @return the color
   */
  public Color getColor() {
    return color;
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    if (getBindingType() != BindingType.UP_LEFT && getBindingType() != BindingType.UP_RIGHT) {
      super.paintShape(attach, g, offset);
    }
  }

  public BindingShape pinIt() {
    return new BindingShape(getBindingType(), getText());
  }

  public boolean reverseBindingType(AShape curr, AShape shape) {
    if (getBindingType() == BindingType.DOWN_LEFT) {
      setConditionnallyBindingType(BindingType.UP_LEFT);
      return true;
    }
    else if (getBindingType() == BindingType.DOWN_RIGHT) {
      setConditionnallyBindingType(BindingType.UP_RIGHT);
      return true;
    }
    if (curr instanceof AForkShape) {
      if (curr.getPX() > shape.getPX()) {
        setConditionnallyBindingType(BindingType.UP_LEFT);
      }
      else {
        setConditionnallyBindingType(BindingType.UP_RIGHT);
      }
      return true;
    }
    return false;
  }

  public boolean isAttached() {
    return attach != null;
  }

  /**
   * @return the attach
   */
  public AShape getAttach() {
    return attach;
  }

  /**
   * @param shape the attach to set
   */
  public void setAttachTo(AShape shape) {
    this.attach = shape;
    if (shape instanceof AFinalShape || shape instanceof FinalNodeShape) {
      isReady = false;
    }
  }

  /**
   * update X,Y boundaries
   */
  public void updateXYBoundaries() {
    if (attach != null && !(attach instanceof DecisionNodeShape)) {
      setConditionnallyBindingType(BindingType.DOWN);
    }
  }


  /**
   * @param shape the {@link DecisionNodeShape} attach to set
   */
  public void setAttachToDecisionShape(DecisionNodeShape shape, int x) {
    this.attach = shape;
    setConditionnallyBindingType(BindingType.DOWN_LEFT);
  }

  /**
   * @param attach to right part of {@link DecisionNodeShape}
   */
  public void updateRightPartDecisionShape() {
    setConditionnallyBindingType(BindingType.DOWN_RIGHT);
  }

  /**
   * @param attach to right part of {@link DecisionNodeShape}
   */
  public void forceRightPartDecisionShape() {
    setBindingType(BindingType.DOWN_RIGHT);
  }

  /**
   * @return the isReady
   */
  public boolean isReady() {
    return isReady;
  }

  /**
   * @param isReady the isReady to set
   */
  public void setReady(boolean isReady) {
    if (attach instanceof AFinalShape || attach instanceof FinalNodeShape) {
      this.isReady = false;
      return;
    }
    this.isReady = isReady;
    if (attach == null) {
      return;
    }
    if (attach instanceof ActionShape) {
      setBindingType(BindingType.DOWN);
    }
  }

  /**
   * X position of 1st point (binding the parent) = parent's X position
   * it depends on the binding type
   */
  public int getX1() {
    if (attach instanceof AForkShape || attach instanceof AJoinShape) {
      return attach.getPX();
    }
    switch (getBindingType()) {
      case DOWN:
      case DOWN_MIDDLE:
        return attach.getPX();
      case DOWN_LEFT:
      case UP_LEFT:
        return attach.getPX() - attach.getWidth()/2;
      case DOWN_RIGHT:
      case UP_RIGHT:
        return attach.getPX() + attach.getWidth()/2;

      default:
        break;
    }
    return 0;
  }

  /**
   * X position of 2nd point (binding the child) = child's X position
   * it depends on the binding type
   */
  public int getX2() {
    if (attach instanceof AForkShape || attach instanceof AJoinShape) {
      return attach.getPX()+30;
    }
    switch (getBindingType()) {
      case DOWN:
      case DOWN_MIDDLE:
        return attach.getPX()+30;
      case DOWN_LEFT:
      case UP_LEFT:
        return attach.getPX() - attach.getWidth()/2 - 90;
      case DOWN_RIGHT:
      case UP_RIGHT:
        return attach.getPX() - attach.getWidth()/2 + 90;

      default:
        break;
    }
    return 0;
  }

  /**
   * X position of middle point (middle position)
   * it depends on the xshift from parent
   */
  public int getXmiddle() {
    return attach.getPX() + getXshift();
  }

  @Override
  public int getChildY() {
    return getParentY() + 30;
  }

  @Override
  public int getParentY() {
    return attach == null ? 0 : attach.getPY()+attach.getHeight();
  }
}
