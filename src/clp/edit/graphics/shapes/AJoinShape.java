package clp.edit.graphics.shapes;

import java.util.ArrayList;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.JoinDialog;
import clp.edit.graphics.dial.TransitionType;

public abstract class AJoinShape extends ATransitionShape {

  private static final long serialVersionUID = 1146116008362444218L;

  private List<ABindingShape> parents;

  private Integer leftmost;
  private Integer rightmost;

  transient private JoinDialog dialog;

  public AJoinShape(int width, int height, String name, TransitionType tt) {
    super(width, height, name, "", tt);
    parents = new ArrayList<>();
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new JoinDialog(this);
    }
    return dialog;
  }

  /**
   * @return the parents
   */
  public List<ABindingShape> getParents() {
    return parents;
  }

  public void deleteChildNodes(ArrayList<ABindingShape> list) {
    for (ABindingShape b : list) {
      parents.remove(b);
    }
    if (parents.size() == 1) {
      ABindingShape b = getParents().get(0);
      AShape chld = getChild().getChild();
      chld.addToY(30);
      AShape par = b.getParent();
      if (par instanceof ActionShape && chld instanceof ActionShape ||
          par instanceof ATransitionShape && chld instanceof ATransitionShape) {
        par.setChild(null);
      }
      else {
        b.setChild(chld);
      }
    }
  }

  public void exchangeChild(ABindingShape b, ABindingShape parent) {
    int i = parents.indexOf(b);
    parents.set(i, parent);
  }

  /**
   * @return the leftmost
   */
  public Integer getLeftmost() {
    return leftmost;
  }

  /**
   * @param leftmost the leftmost to set
   */
  public void setLeftmost(Integer leftmost) {
    this.leftmost = leftmost;
  }

  /**
   * @return the rightmost
   */
  public Integer getRightmost() {
    return rightmost;
  }

  /**
   * @param rightmost the rightmost to set
   */
  public void setRightmost(Integer rightmost) {
    this.rightmost = rightmost;
  }

  @Override
  public int getWidth() {
    if (leftmost == null || rightmost == null) {
      return 0;
    }
    return rightmost - leftmost;
  }

  @Override
  public void setWidth(int width) {
    ActionShape p = null;
    for (ABindingShape b : parents) {
      if (p == null || b.getParent().getPX() > p.getPX()) {
        p = (ActionShape) b.getParent();
      }
    }
    p.addToX(width - rightmost + leftmost);
    GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getCurrentContainer()
      .updateLimits(leftmost, leftmost+width+10);
  }

  @Override
  public boolean generateCode(AContainer container) {
    if (getChild() != null) {
      return getChild().generateCode(container);
    }
    return true;
  }

  @Override
  public String getActivationCondition(ABindingShape bsr) {
    if (parents.size() > 1) {
      String s = " activated(" + parents.get(0).getParent().getName() + ") ";
      for (int i=1; i<parents.size(); i++) {
        s += "AND activated(" + parents.get(i).getParent().getName() + ") ";
      }
      return s;
    }
    return null;
  }

  @Override
  public String getDeactivationCondition() {
    AShape chld = getChild().getChild();
    addInputVariable(chld.getName(), chld.getDesc(), null);
    return " activated(" + chld.getName() + ") ";
  }

  public void replaceParent(ABindingShape cb, ABindingShape nb) {
    for (int i=0; i<parents.size(); i++) {
      ABindingShape b = parents.get(i);
      if (b == cb) {
        parents.set(i, nb);
        break;
      }
    }
  }
}
