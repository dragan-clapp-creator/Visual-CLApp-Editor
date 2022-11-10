package clp.edit.graphics.btn.act;

import clp.edit.graphics.btn.AControlButton;

public abstract class AnActigramButton extends AControlButton {

  private static final long serialVersionUID = 2720546853652339626L;

  public AnActigramButton(ShapeButton b) {
    super(b);
  }

  abstract public void setupItemLisener(ActigramButtonsPanel panel);
}
