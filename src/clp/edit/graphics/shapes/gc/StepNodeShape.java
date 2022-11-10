package clp.edit.graphics.shapes.gc;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.graphics.dial.ActionOrStepDialog;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class StepNodeShape extends ActionShape {

  private static final long serialVersionUID = -6544230476879566172L;

  enum Action {
    PR, AS, ODATA, UI, JAVA;
  }

  private Action action;

  private int number;

  transient private ActionOrStepDialog dialog;

  public StepNodeShape(int i) {
    super(40, 26, "X"+i, "Step ", 2);
    number = i;
    dialog = new ActionOrStepDialog(GeneralContext.getInstance().getFrame(), this);
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    int x = getPX()+offset;
    Color c = g.getColor();
    if (getBgcolor() == null) {
      if (isSelected()) {
        g.setColor(Color.lightGray);
      }
      else {
        if (isPublished()) {
          g.setColor(Color.pink);
          g.fillRect(x-20, getPY(), 40, 26);
          g.setColor(Color.white);
          g.fillRect(x-15, getPY()+5, 30, 16);
        }
        else {
          g.setColor(Color.white);
          g.fillRect(x-20, getPY(), 40, 26);
        }
        g.setColor(Color.black);
      }
    }
    else {
      g.setColor(Color.white);
      g.fillRect(x-20, getPY(), 40, 26);
      g.setColor(getBgcolor());
      g.fillOval(x-5, getPY()+10, 10, 10);
    }
    g.drawRect(x-20, getPY(), 40, 26);

    if (action != null) {
      g.setColor(Color.orange);
      if (action.name().length() < 4) {
        g.fillRect(x-20, getPY(), 40, 26);
        g.setColor(Color.yellow);
        g.drawString(action.name(), x+8, getPY()+20);
      }
      else {
        g.fillRect(x-20, getPY(), 40, 26);
        g.setColor(Color.yellow);
        g.drawString(action.name(), x+5, getPY()+20);
      }
    }
    g.setColor(c);
    g.drawString(""+number, x-5, getPY()+18);

    paintInstructionsFromShape(g, offset);

    if (getChild() != null) {
      super.checkDownType();
      getChild().paintShape(g, offset);
    }
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    BindingShape b = new BindingShape(bindingType, "");
    setChild(b);
    b.setChild(shape);
  }

  @Override
  public ActionOrStepDialog getActionOrStepDialog() {
    if (dialog == null) {
      dialog = new ActionOrStepDialog(GeneralContext.getInstance().getFrame(), this, getInstructions());
    }
    return dialog;
  }
}
