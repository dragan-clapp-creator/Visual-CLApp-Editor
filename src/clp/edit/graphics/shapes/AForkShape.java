package clp.edit.graphics.shapes;

import java.util.ArrayList;
import java.util.List;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.ForkDialog;
import clp.edit.graphics.dial.TransitionType;

public abstract class AForkShape extends ATransitionShape {

  private static final long serialVersionUID = -1011308021246755546L;

  private List<ABindingShape> destinations;
  private int leftmost;
  private int rightmost;
  transient private ForkDialog dialog;

  public AForkShape(int width, int height, String name, TransitionType tt) {
    super(width, height, name, "", tt);
    destinations = new ArrayList<>();
  }

  /**
   * @return the destinations
   */
  public List<ABindingShape> getDestinations() {
    return destinations;
  }

  public AShape getLeftmostChild() {
    return destinations.get(0).getChild();
  }

  @Override
  public ABindingShape getChild() {
    if (destinations.size() > 1) {
      return destinations.get(0);
    }
    return null;
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    ABindingShape b = new BindingShape(bindingType, "");
    b.setParent(this);
    boolean isUp = false;
    if (bindingType == BindingType.UP_LEFT || bindingType == BindingType.UP_RIGHT) {
      isUp = true;
      b.setOnlyChild(shape);
      ((ActionShape)shape).addEntryPoint(b);
    }
    else {
      b.setChild(shape);
    }
    destinations.add(b);
    int nb = destinations.size() < 2 ? 2 : destinations.size();
    int w = 160*(nb-1);
    int pos = - w/2;
    ABindingShape b0 = destinations.get(0);
    b0.getChild().setXoffset(pos);
    leftmost = b0.getChild().getPX();
    for (int i=1; i<destinations.size(); i++) {
      ABindingShape db = destinations.get(i);
      pos += 160;
      if (db == b && isUp) {
        db.setXshift(pos);
      }
      else {
        db.getChild().setXoffset(pos);
      }
      int x = db.getChild().getPX();
      if (x > rightmost) {
        rightmost = x;
      }
    }
  }

  @Override
  public void gatherLinksToJoin(List<ABindingShape> list) {
    for (ABindingShape b : destinations) {
      b.getChild().gatherLinksToJoin(list);
    }
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new ForkDialog(this);
    }
    return dialog;
  }

  public void reverseBindingType(RedBindingShape redShape) {
    if (getDestinations().isEmpty()) {
      redShape.setConditionnallyBindingType(BindingType.UP_LEFT);
    }
    else {
      redShape.setConditionnallyBindingType(BindingType.UP_RIGHT);
    }
  }

  public void delete(ABindingShape b) {
    for (ABindingShape d : destinations) {
      if (b == d) {
        destinations.remove(b);
        break;
      }
    }
  }

  /**
   * @return the rightmost
   */
  public int getRightmost() {
    return rightmost;
  }

  /**
   * @return the leftmost
   */
  public int getLeftmost() {
    return leftmost;
  }

  @Override
  public int getWidth() {
    return rightmost - leftmost;
  }

  @Override
  public void setWidth(int width) {
    this.rightmost = leftmost + width;
  }

  @Override
  public boolean generateCode(AContainer container) {
    boolean b = true;
    for (ABindingShape d : destinations) {
      b &= d.generateCode(container);
      if (!b) {
        return false;
      }
    }
    return b;
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    return " activated(" + getParent().getParent().getName() + ") ";
  }

  @Override
  public String getDeactivationCondition() {
    AShape chld = destinations.get(0).getChild();
    addInputVariable(chld.getName(), chld.getDesc(), null);
    String s = " activated(" + chld.getName() + ") ";
    for (int i=1; i<destinations.size(); i++) {
      chld = destinations.get(i).getChild();
      addInputVariable(chld.getName(), chld.getDesc(), null);
      s += "AND activated(" + chld.getName() + ") ";
    }
    return s;
  }
}
