package clp.edit.graphics.btn.gc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class EventNodeButton extends AGrafcetButton {

  private static final long serialVersionUID = 6631284861726411240L;

  /**
   * constructor
   * 
   * @param panel
   * @param tooltip
   * @param b
   */
  public EventNodeButton(GrafcetButtonsPanel panel, String tooltip, ShapeButton b) {
    super(b);
    setupItemLisener(panel);
    setToolTipText(tooltip);
    setSize(new Dimension(30, 20));
    setPreferredSize(new Dimension(30, 20));
    setEnabled(false);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.yellow);
    g.fill3DRect(0, 0, 30, 20, !isSelected());

    if (isEnabled()) {
      g.setColor(Color.black);
    }
    else {
      g.setColor(Color.magenta);
    }
    g.fillRect(5, 6, 20, 4);
    g.drawLine(15, 10, 15, 17);
  }

  @Override
  public void setInitialEnabling() {
    setEnabled(true);
  }

  @Override
  public void setupItemLisener(GrafcetButtonsPanel panel) {
    addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        if (ev.getStateChange() == ItemEvent.SELECTED) {
          panel.getCurrentContainer().getAutomaton().performAction(EventNodeButton.this, null);
        }
     }
    });
  }
}
