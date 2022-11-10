package clp.edit.graphics.btn.act;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DecisionNodeButton extends AnActigramButton {

  private static final long serialVersionUID = -6183832104822656081L;

  /**
   * constructor
   * 
   * @param panel
   * @param tooltip
   * @param b
   */
  public DecisionNodeButton(ActigramButtonsPanel panel, String tooltip, ShapeButton b) {
    super(b);
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
    int[] px = {15, 10, 15, 20};
    int[] py = { 5, 10, 15, 10};
    g.drawPolygon(px, py, 4);
    g.drawLine(5, 10, 10, 10);
    g.drawLine(20, 10, 25, 10);
  }

  @Override
  public void setInitialEnabling() {
    setEnabled(false);
  }

  @Override
  public void setupItemLisener(ActigramButtonsPanel panel) {
    addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        if (ev.getStateChange() == ItemEvent.SELECTED) {
          panel.getCurrentContainer().getAutomaton().performAction(DecisionNodeButton.this, null);
        }
     }
    });
  }
}
