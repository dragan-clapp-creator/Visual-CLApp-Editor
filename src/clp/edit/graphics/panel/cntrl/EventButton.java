package clp.edit.graphics.panel.cntrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class EventButton extends AInputButton {

  private static final long serialVersionUID = -6249930675846964865L;

  private Color off = new Color(0x000087);
  private Color on = new Color(0x0000d7);
  
  public EventButton(String name, String text) {
    super(name);
    setToolTipText(text);
    setSize(40, 30);
    setPreferredSize(new Dimension(40, 30));
  }

  @Override
  public void paint(Graphics g) {
    if (isSelected()) {
      g.setColor(on);
    }
    else {
      g.setColor(off);
    }
    g.fillRoundRect(0, 0, 40, 30, 5, 5);

    g.setColor(Color.white);
    g.drawString(getText(), 5, 22);
  }
}
