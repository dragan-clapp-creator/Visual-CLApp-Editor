package clp.edit.graphics.panel.cntrl;

import javax.swing.JToggleButton;

abstract public class AInputButton extends JToggleButton {

  private static final long serialVersionUID = 7993011728489662807L;

  private int line;
  private int col;
  
  public AInputButton(String name) {
    super(name);
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
