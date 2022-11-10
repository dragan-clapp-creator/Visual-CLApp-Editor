package clp.edit.graphics.panel.cntrl;

import java.awt.Graphics;

public class DummyEventButton extends EventButton implements IDummy {

  private static final long serialVersionUID = -5252010388177347482L;

  public DummyEventButton() {
    super("dummy", "");
  }

  @Override
  public void paint(Graphics g) {
  }
}
