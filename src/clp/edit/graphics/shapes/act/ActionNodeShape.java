package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.graphics.dial.ActionOrStepDialog;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingType;

public class ActionNodeShape extends ActionShape {

  private static final long serialVersionUID = 543334981363673112L;

  enum Action {
    PR, AS, ODATA, UI, JAVA;
  }

  private Action action;

  transient private ActionOrStepDialog dialog;

  public ActionNodeShape(int index) {
    super(50, 30, "A"+index, "Action ", 1);
    dialog = new ActionOrStepDialog(GeneralContext.getInstance().getFrame(), this);
 }

  @Override
  public void paintShape(Graphics g, int offset) {
    int ref_x = getPX()+offset;
    Color c = g.getColor();
    if (getBgcolor() == null) {
      if (isSelected()) {
        g.setColor(Color.lightGray);
        g.fillRoundRect(ref_x-getWidth()/2, getPY(), 50, 30, 30, 30);
      }
      else {
        g.setColor(Color.white);
        g.fillRoundRect(ref_x-getWidth()/2, getPY(), 50, 30, 30, 30);
        g.setColor(Color.black);
        g.drawRoundRect(ref_x-getWidth()/2, getPY(), 50, 30, 30, 30);
      }
    }
    else {
      g.setColor(getBgcolor());
      g.fillRoundRect(ref_x-getWidth()/2, getPY(), 50, 30, 30, 30);
    }
    if (action != null) {
      g.setColor(Color.orange);
      int x = ref_x-getWidth()/2+5;
      if (action.name().length() < 4) {
        g.fillRoundRect(x, getPY()+5, 30, 20, 20, 20);
        g.setColor(Color.yellow);
        g.drawString(action.name(), x+8, getPY()+20);
      }
      else {
        g.fillRoundRect(x, getPY()+5, 35, 20, 20, 20);
        g.setColor(Color.yellow);
        g.drawString(action.name(), x+5, getPY()+20);
      }
    }
    g.setColor(c);
    g.drawString(getName(), ref_x-8, getPY()+20);

    paintInstructionsFromShape(g, offset);

    if (getChild() != null) {
      super.checkDownType();
      getChild().paintShape(g, offset);
    }
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    if (getChild() != null) {
      getChild().setChild(shape);
    }
  }

  @Override
  public ActionOrStepDialog getActionOrStepDialog() {
    if (dialog == null) {
      dialog = new ActionOrStepDialog(GeneralContext.getInstance().getFrame(), this, getInstructions());
    }
    return dialog;
  }
}
