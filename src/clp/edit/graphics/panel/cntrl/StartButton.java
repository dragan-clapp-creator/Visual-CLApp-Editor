package clp.edit.graphics.panel.cntrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JToggleButton;;

public class StartButton extends JToggleButton {

  private static final long serialVersionUID = 9016965581885122262L;

  private Color off = new Color(0x870000);
  private Color on = new Color(0xd70000);
  
  public StartButton() {
    super("");
    setSize(150, 30);
    setPreferredSize(new Dimension(150, 30));
  }

  @Override
  public void paint(Graphics g) {
    String text;
    if (isSelected()) {
      g.setColor(on);
      text = "Simulation is Running";
    }
    else {
      g.setColor(off);
      text = "Simulate";
    }
    g.fillRoundRect(30, 0, 170, 30, 5, 5);

    g.setColor(Color.white);
    g.drawString(text, 40, 22);
  }
}
