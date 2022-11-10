package clp.edit.graphics.shapes;

import clp.edit.graphics.dial.TransitionType;

abstract public class AFinalShape extends ATransitionShape {

  private static final long serialVersionUID = 1047468899173820357L;

  public AFinalShape(int width, int height, String name, TransitionType tt) {
    super(width, height, name, "", tt);
  }

  @Override
  public boolean generateCode(AContainer container) {
    return true;
  }
}
