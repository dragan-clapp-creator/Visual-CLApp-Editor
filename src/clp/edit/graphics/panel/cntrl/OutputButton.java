package clp.edit.graphics.panel.cntrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JToggleButton;

public class OutputButton extends JToggleButton implements IOutput {

  private static final long serialVersionUID = 3872977572020038321L;

  private Color off = new Color(0xa74800);
  private Color on = new Color(0xe08700);

  private int line;
  private int col;
  
  public OutputButton(String name) {
    super(name);
    setToolTipText(name);
    setEnabled(false);
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

  /**
   * @return the line
   */
  public int getLine() {
    return line;
  }

  /**
   * @param line the line to set
   */
  public void setLine(int line) {
    this.line = line;
  }

  /**
   * @return the col
   */
  public int getCol() {
    return col;
  }

  /**
   * @param col the col to set
   */
  public void setCol(int col) {
    this.col = col;
  }
}
