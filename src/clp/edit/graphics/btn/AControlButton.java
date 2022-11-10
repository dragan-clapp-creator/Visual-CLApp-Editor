package clp.edit.graphics.btn;

import javax.swing.JToggleButton;

abstract public class AControlButton extends JToggleButton {

  private static final long serialVersionUID = -1887455568873267284L;

  public enum ShapeButton {
    NONE, INIT, ACTION, STEP, PLACE, TRANSITION, DECISION, FORK, JOIN, BIND, UNBIND, FINAL, EVENT;
  }

  private ShapeButton shapeButton;

  public AControlButton(ShapeButton b) {
    super();
    shapeButton = b;
    
  }

  abstract public void setInitialEnabling();

  /**
   * @return the shapeButton
   */
  public ShapeButton getShapeButton() {
    return shapeButton;
  }
}
