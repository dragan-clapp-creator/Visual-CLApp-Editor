package clp.edit.graphics.shapes;

import clp.edit.graphics.dial.TransitionType;

abstract public class AEventShape extends ATransitionRoot {

  private static final long serialVersionUID = 7332626605015319700L;

  public AEventShape(int width, int height, TransitionType tt) {
    super(width, height, tt);
  }

  @Override
  public boolean generateCode(AContainer container) {
    if (getChild() != null) {
      return getChild().generateCode(container);
    }
    return true;
  }
}
