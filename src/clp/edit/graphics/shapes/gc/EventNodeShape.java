package clp.edit.graphics.shapes.gc;

import java.awt.Color;
import java.awt.Graphics;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.TransitionRootDialog;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AEventShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.util.CellInfo;

public class EventNodeShape extends AEventShape {

  private static final long serialVersionUID = 8453477157312970032L;

  transient private TransitionRootDialog dialog;

  /**
   * CONSTRUCTOR
   * @param grafcetShape 
   */
  public EventNodeShape(GrafcetShape grafcetShape, TransitionType tt) {
    super(20, 20, tt);
    dialog = new TransitionRootDialog(GeneralContext.getInstance().getFrame(), this);
    super.setName(dialog.getTransitionText());
  }

  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    int sx = getPX()+offset;
    int sy = getPY();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    g.fillRect(sx-20, sy, 40, 4);
    g.drawLine(sx, sy, sx, sy+20);
    g.setColor(c);
    if (getName() != null) {
      g.drawString(getName(), sx+23, sy+5);
    }
    if (getChild() != null) {
      super.checkDownType();
      getChild().paintShape(g, offset);
    }
    if (getSibling() != null) {
      getSibling().paintShape(g, offset);
    }
  }

  @Override
  public int getPY() {
    return 50 + getYoffset();
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
      dialog = new TransitionRootDialog(GeneralContext.getInstance().getFrame(), this);
    }
    return dialog;
  }

  public TransitionRootDialog getCustDialog() {
    return (TransitionRootDialog) getDialog();
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    AShape s = super.getSelectedShape(x, y);
    if (s == null && getSibling() != null) {
      return getSibling().getSelectedShape(x, y);
    }
    return s;
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    String init_name = "INIT_"+getChild().getChild().getName();
    return getCustDialog().getActivationCondition(init_name, 'X', getChild().getChild());
  }

  @Override
  public boolean generateActiveCode(AContainer container) {
    if (getChild() != null) {
      String name = getChild().getChild().getName();
      String init_name = "INIT_"+name;
      if (!container.isRegistered(init_name)) {
        CellInfo info = new CellInfo(init_name);
        String code = getDeactivationCondition(name);
        if (code != null) {
          info.setDd("      DD { " + code + "; }\n");
        }
        container.register(init_name, info, true);
      }
      return getChild().generateCode(container);
    }
    return true;
  }

  //
  private String getDeactivationCondition(String name) {
    return getCustDialog().getDeactivationCondition(name, 'X', null);
  }

  @Override
  public boolean generateCode(AContainer container) {
    if (getChild() != null) {
      String name = getChild().getChild().getName();
      String init_name = "INIT_"+name;
      if (!container.isRegistered(init_name)) {
        CellInfo info = new CellInfo(init_name);
        String code = getDeactivationCondition(name);
        if (code != null) {
          info.setDd("      DD { " + code + "; }\n");
        }
        container.register(init_name, info, true);
      }
      return getChild().generateCode(container);
    }
    return true;
  }
}
