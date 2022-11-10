package clp.edit.graphics.btn.pn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class PlaceNodeButton extends APetriNetsButton {

  private static final long serialVersionUID = -6836419577809936120L;

  public PlaceNodeButton(PetriNetsButtonsPanel panel, String tooltip, ShapeButton b) {
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
    g.drawOval(10, 3, 15, 15);
  }

  @Override
  public void setInitialEnabling() {
    setEnabled(true);
  }

  @Override
  public void setupItemLisener(PetriNetsButtonsPanel panel) {
    addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        if (ev.getStateChange() == ItemEvent.SELECTED) {
          panel.getCurrentContainer().getAutomaton().performAction(PlaceNodeButton.this, null);
        }
     }
    });
  }
}
