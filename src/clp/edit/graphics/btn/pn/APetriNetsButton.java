package clp.edit.graphics.btn.pn;

import clp.edit.graphics.btn.AControlButton;

abstract public class APetriNetsButton extends AControlButton {

  private static final long serialVersionUID = -2513821743495263407L;

  public APetriNetsButton(ShapeButton b) {
    super(b);
  }

  abstract public void setupItemLisener(PetriNetsButtonsPanel panel);
}
