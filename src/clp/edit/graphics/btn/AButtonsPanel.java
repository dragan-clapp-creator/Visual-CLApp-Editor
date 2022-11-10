package clp.edit.graphics.btn;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import clp.edit.graphics.shapes.AShape;

public abstract class AButtonsPanel extends JPanel {

  private static final long serialVersionUID = 6757543331782861251L;

  abstract public void placeCorrespondingShape(AShape shape);

  abstract public void bindToCorrespondingShape(AShape currentShape);


  public void setButtonsEnabled(boolean b) {
    for (Component c : getComponents()) {
      if (c instanceof AControlButton || c instanceof JToggleButton) {
        c.setEnabled(b);
      }
    }
  }
}
