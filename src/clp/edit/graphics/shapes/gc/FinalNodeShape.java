package clp.edit.graphics.shapes.gc;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.TransitionDialog;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AFinalShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.util.CellInfo;

public class FinalNodeShape extends AFinalShape {

  private static final long serialVersionUID = 7864381852046391226L;

  transient private TransitionDialog dialog;

  /**
   * CONSTRUCTOR
   * @param grafcetShape 
   */
  public FinalNodeShape(GrafcetShape grafcetShape, TransitionType tt) {
    super(20, 20, "", tt);
    dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    setName(dialog.getTransitionText());
  }

  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    int sx = getPX()+offset;
    int sy = getPY();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    g.fillRect(sx-20, sy, 40, 4);
    g.drawLine(sx, sy-20, sx, sy);
    g.setColor(c);
    if (getName() != null) {
      g.drawString(getName(), sx+23, sy+5);
    }
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    BindingShape b = new BindingShape(bindingType, "e");
    setChild(b);
    b.setChild(shape);
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    }
    return dialog;
  }

  public TransitionDialog getCustDialog() {
    return (TransitionDialog) getDialog();
  }

  @Override
  public void setName(String name) {
    super.setName(name);
  }

  @Override
  public boolean generateCode(AContainer container) {
    AShape p = getParent().getParent();
    String name = p.getName();
    String final_name = "FINAL_"+name;
    if (!container.isRegistered(final_name)) {
      CellInfo info = new CellInfo(final_name);
      String code = getCustDialog().getActivationCondition(name, 'X', getParent().getParent());
      if (code != null) {
        info.setAd("      AD { " + code + "; }\n");
      }
      container.register(final_name, info, false);
      container.addDeactivationConditionFromFinal(name, final_name);
      info.setDd("      DD { TRUE; }\n");
    }
    return true;
  }

  @Override
  public String getDeactivationCondition() {
    AShape p = getParent().getParent();
    return getCustDialog().getDeactivationCondition("FINAL_"+p.getName(), 'F', p);
  }
}
