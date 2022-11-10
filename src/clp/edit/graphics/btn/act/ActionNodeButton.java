package clp.edit.graphics.btn.act;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class ActionNodeButton extends AnActigramButton {

  private static final long serialVersionUID = -8445173404641404536L;

  /**
   * constructor
   * 
   * @param panel
   * @param tooltip
   * @param b
   */
  public ActionNodeButton(ActigramButtonsPanel panel, String tooltip, ShapeButton b) {
    super(b);
    setToolTipText(tooltip);
    setSize(new Dimension(30, 20));
    setPreferredSize(new Dimension(30, 20));
    setEnabled(false);
  }

  @Override
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
    g.drawRoundRect(3, 3, 24, 14, 10, 10);
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
          panel.getCurrentContainer().getAutomaton().performAction(ActionNodeButton.this, null);
        }
      }
    });
  }
}
