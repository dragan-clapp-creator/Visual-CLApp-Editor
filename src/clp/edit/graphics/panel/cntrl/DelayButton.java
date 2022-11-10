package clp.edit.graphics.panel.cntrl;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class DelayButton extends EventButton {

  private static final long serialVersionUID = 1050357635174606713L;

  private Color off = new Color(0x6400c3);

  private ArrayList<String> delays;

  public DelayButton() {
    super("Delay", "NO Delays");
    delays = new ArrayList<>();
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(off);
    g.fillRoundRect(0, 0, 40, 30, 5, 5);

    g.setColor(Color.white);
    g.drawString(getText(), 2, 22);
  }

  public void addToToolTip(String timeIdentifier) {
    if (!delays.contains(timeIdentifier)) {
      delays.add(timeIdentifier);
    }
    setToolTipText(delays.toString());
  }

  public boolean isEmpty() {
    return delays.isEmpty();
  }

  public ArrayList<String> getDelays() {
    return delays;
  }

  public void removeDelay(String delay) {
    delays.remove(delay);
    setToolTipText(delays.toString());
  }
}
