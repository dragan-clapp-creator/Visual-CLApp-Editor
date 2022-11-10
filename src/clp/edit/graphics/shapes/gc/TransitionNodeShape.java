package clp.edit.graphics.shapes.gc;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.TransitionDialog;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ATransitionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;

public class TransitionNodeShape extends ATransitionShape {

  private static final long serialVersionUID = 79988378747243534L;

  transient private TransitionDialog dialog;

  /**
   * CONSTRUCTOR
   * 
   * @param grafcetShape 
   */
  public TransitionNodeShape(GrafcetShape grafcetShape) {
    super(grafcetShape.getDefaultTransitionType());
    dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    setName(dialog.getTransitionText());
  }

  /**
   * CONSTRUCTOR
   * @param containerShape
   * @param dial
   */
  public TransitionNodeShape(GrafcetShape grafcetShape, TransitionDialog dial) {
    super(grafcetShape.getDefaultTransitionType());
    dialog = new TransitionDialog(GeneralContext.getInstance().getFrame(), this, null, 200);
    setName(dial.getTransitionText());
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    BindingShape b = new BindingShape(bindingType, "");
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
  public String getActivationCondition(ABindingShape bs) {
    return getCustDialog().getActivationCondition(null, 'X', getParent().getParent());
  }

  @Override
  public String getDeactivationCondition() {
    if (getChild() != null) {
      AShape chld = getChild().getChild();
      addInputVariable(chld.getName(), chld.getDesc(), null);
      return " activated(" + chld.getName() + ") ";
    }
    return null;
  }
}
