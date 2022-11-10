package clp.edit.graphics.panel.cntrl;

import java.awt.Graphics;

public class DummyConditionButton extends ConditionButton implements IDummy {

  private static final long serialVersionUID = -347153608952633583L;

  public DummyConditionButton() {
    super("dummy", "");
  }

  @Override
  public void paint(Graphics g) {
  }
}
