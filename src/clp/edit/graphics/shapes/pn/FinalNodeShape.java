package clp.edit.graphics.shapes.pn;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.graphics.dial.PNTransitionDialog;
import clp.edit.graphics.dial.PNTransitionDialog.TransitionPosition;
import clp.edit.graphics.dial.TransitionDialog;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.util.CellInfo;

public class FinalNodeShape extends TransitionNodeShape {

  private static final long serialVersionUID = 7725165550829176902L;

  /**
   * CONSTRUCTOR
   * @param pnShape 
   */
  public FinalNodeShape(PetriNetsShape pnShape) {
    super(pnShape, pnShape.getDefaultTransitionType(), TransitionPosition.FINAL);
  }

  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    int sx = getPX()+offset;
    int sy = getPY();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    g.fillRect(sx-20, sy, 40, 4);
    g.setColor(c);
    if (getName() != null) {
      g.drawString(getName(), sx+23, sy+5);
    }
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    PNBindingShape b = new PNBindingShape(bindingType, "e");
    setChild(b);
    b.setChild(shape);
  }

  @Override
  public boolean generateCode(AContainer container) {
    AShape p = getParent().getParent();
    String name = p.getName();
    String final_name = "FINAL_"+name;
    if (!container.isRegistered(final_name)) {
      CellInfo info = new CellInfo(final_name);
      String code = getActivationCondition(name);
      if (code != null) {
        info.setAd("      AD { " + code + "; }\n");
      }
      container.register(final_name, info, false);
      container.addDeactivationConditionFromFinal(name, final_name);
      if (p instanceof TransitionNodeShape) {
        code = ((TransitionDialog)p.getDialog()).getDeactivationCondition(name, 'P', null);
      }
      else {
        code = "TRUE";
      }
      if (code != null) {
        info.setDd("      DD { " + code + "; }\n");
      }
    }
    return true;
  }

  //
  private String getActivationCondition(String name) {
    String s = "";
    PNTransitionDialog dial = (PNTransitionDialog)getDialog();
    ABindingShape b = getParent();
    if (b instanceof TriBinding) {
      s = gatherUpTokensFromDialog((TriBinding) b);
    }
    else {
      AShape p = b.getParent();
      String marks = dial.getCheckMarks();
      if (marks != null) {
        String cnd = dial.getActivationCondition(null, 'P', getParent().getParent());
        s = getTrName() + " { " + p.getName() + " " + marks + " } " + (cnd == null ? "" : "AND " + cnd);
      }
      else {
        s = dial.getActivationCondition(name, 'P', getParent().getParent());
      }
    }
    return s;
  }
}
