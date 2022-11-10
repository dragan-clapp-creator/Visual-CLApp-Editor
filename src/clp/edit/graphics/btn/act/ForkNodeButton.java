package clp.edit.graphics.btn.act;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ForkNodeButton extends AnActigramButton {

  private static final long serialVersionUID = -7408676299200664913L;

  /**
   * constructor
   * 
   * @param panel
   * @param tooltip
   * @param b
   */
  public ForkNodeButton(ActigramButtonsPanel panel, String tooltip, ShapeButton b) {
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
    g.fillRect(5, 8, 20, 4);
    g.drawLine(15, 2, 15, 8);
    g.drawLine(10, 12, 10, 17);
    g.drawLine(20, 12, 20, 17);
  }

  @Override
  public void setInitialEnabling() {
    setEnabled(false);
  }

  @Override
  public void setupItemLisener(ActigramButtonsPanel panel) {
    addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        panel.getCurrentContainer().getAutomaton().performAction(ForkNodeButton.this, null);
     }
    });
  }
}
