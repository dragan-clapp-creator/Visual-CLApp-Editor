package clp.edit.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

abstract public class ADialog extends JDialog {

  private static final long serialVersionUID = -3917298078158700791L;

  abstract public void edit(String text, String desc);

  abstract public String getTransitionText();

  abstract public String getDescription();

  abstract public boolean isOk();

  public ADialog(Frame owner, String title, boolean isModal) {
    super(owner, title, isModal);
  }

  public void makeCompactGrid(Container parent,
      int rows, int cols,
      int initialX, int initialY,
      int xPad, int yPad) {

    SpringLayout layout;
    try {
        layout = (SpringLayout)parent.getLayout();
    } catch (ClassCastException exc) {
        System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
        return;
    }

    //Align all cells in each column and make them the same width.
    Spring x = Spring.constant(initialX);
    //Align all cells in each column and make them the same width.
    for (int c = 0; c < cols; c++) {
        Spring width = Spring.constant(0);
        for (int r = 0; r < rows; r++) {
            width = Spring.max(width,
                               getConstraintsForCell(r, c, parent, cols).getWidth());
        }
        for (int r = 0; r < rows; r++) {
            SpringLayout.Constraints constraints =
                    getConstraintsForCell(r, c, parent, cols);
            constraints.setX(x);
            constraints.setWidth(width);
        }
        x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }

    //Align all cells in each row and make them the same height.
    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
        Spring height = Spring.constant(0);
        for (int c = 0; c < cols; c++) {
            height = Spring.max(height,
                                getConstraintsForCell(r, c, parent, cols).
                                    getHeight());
        }
        for (int c = 0; c < cols; c++) {
            SpringLayout.Constraints constraints =
                    getConstraintsForCell(r, c, parent, cols);
            constraints.setY(y);
            constraints.setHeight(height);
        }
        y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }

    //Set the parent's size.
    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint(SpringLayout.SOUTH, y);
    pCons.setConstraint(SpringLayout.EAST, x);
  }

  //
  private Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
    SpringLayout layout = (SpringLayout) parent.getLayout();
    Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }
}
