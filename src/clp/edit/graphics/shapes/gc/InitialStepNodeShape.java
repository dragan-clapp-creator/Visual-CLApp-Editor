package clp.edit.graphics.shapes.gc;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.graphics.dial.ActionOrStepDialog;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class InitialStepNodeShape extends ActionShape {

  private static final long serialVersionUID = -2425875260318670114L;

  private int number;

  transient private ActionOrStepDialog dialog;

  public InitialStepNodeShape(int i) {
    super(40, 26, "X"+i, "Initial Step ", 2);
    number = i;
    setYoffset(30);
    dialog = new ActionOrStepDialog(GeneralContext.getInstance().getFrame(), this);
  }

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
    g.drawRect(x-18, getPY()+2, 36, 22);
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

  @Override
  public boolean generateCode(AContainer container) {
    if (getChild() != null) {
      return getChild().generateActiveCode(container);
    }
    return true;
  }
}
