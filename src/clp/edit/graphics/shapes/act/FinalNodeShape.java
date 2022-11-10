package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.AFinalShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;

public class FinalNodeShape extends AFinalShape {

  private static final long serialVersionUID = -1310105256433370050L;

  public FinalNodeShape() {
    super(26, 26, "", null);
  }

  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    int x = getPX()+offset;
    int y = getPY();
    g.fillOval(x-10, y,   20, 20);
    g.drawOval(x-13, y-3, 26, 26);
    g.setColor(c);
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
  }

  @Override
  public ADialog getDialog() {
    return null;
  }

  @Override
  public String getDeactivationCondition() {
    return "TRUE";
  }
}
