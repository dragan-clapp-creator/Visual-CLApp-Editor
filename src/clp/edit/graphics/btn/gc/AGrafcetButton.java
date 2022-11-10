package clp.edit.graphics.btn.gc;

import clp.edit.graphics.btn.AControlButton;

public abstract class AGrafcetButton extends AControlButton {

  private static final long serialVersionUID = -3149241447082738174L;

  public AGrafcetButton(ShapeButton b) {
    super(b);
  }

  abstract public void setupItemLisener(GrafcetButtonsPanel panel);
}
