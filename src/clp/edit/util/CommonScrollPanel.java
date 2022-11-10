package clp.edit.util;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JScrollPane;

public class CommonScrollPanel extends JScrollPane {

  private static final long serialVersionUID = 1163378046873682877L;

  private int width;
  private int height;

  /**
   * constructor
   */
  public CommonScrollPanel(Rectangle rect) {
    super(null, 
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    width = rect.width;
    height = rect.height;
    setPreferredSize(new Dimension(width, height));
  }

  @Override
  public Dimension getMaximumSize(){
      return getCustomDimensions();
  }

  @Override
  public Dimension getMinimumSize(){
      return getCustomDimensions();
  }

  @Override
  public Dimension getPreferredSize(){
      return getCustomDimensions();
  }

  //
  private Dimension getCustomDimensions() {
    return new Dimension(width, height);
  }

  public void addHeight(int h) {
    height += h;
  }

  public void subHeight(int h) {
    height -= h;
  }
}
