package clp.edit.graphics.shapes;

import java.awt.Graphics;
import java.util.List;

import clp.edit.graphics.dial.PNTransitionDialog;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;

public class TriBinding extends BindingShape {

  private static final long serialVersionUID = -3685729487706661431L;

  private ABindingShape left;
  private ABindingShape middle;
  private ABindingShape right;

  public TriBinding() {
    super(BindingType.DOWN, null);
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    if (left != null) {
      left.paintShape(getParent(), g, offset);
    }
    if (middle != null) {
      middle.paintShape(getParent(), g, offset);
    }
    if (right != null) {
      right.paintShape(getParent(), g, offset);
    }
  }

  public void addChild(BindingShape b) {
    if (left == null) {
      left = b;
      b.setXshift(-9);
    }
    else if (middle == null) {
      middle = b;
      b.setXshift(0);
    }
    else if (right == null) {
      right = b;
      b.setXshift(+9);
    }
  }

  @Override
  public boolean setConditionnallyBindingType(BindingType bindingType) {
    super.setConditionnallyBindingType(bindingType);
    if (left != null) {
      left.setConditionnallyBindingType(bindingType);
    }
    else if (middle != null) {
      middle.setConditionnallyBindingType(bindingType);
    }
    return false;
  }

  @Override
  public void setOldXShift() {
  }

  @Override
  public AShape getChild() {
    if (left != null) {
      return left.getChild();
    }
    else if (middle != null) {
      return middle.getChild();
    }
    else if (right != null) {
      return right.getChild();
    }
    return null;
  }

  @Override
  public int getX2() {
    return getChild().getPX();
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    if (left != null) {
      AShape s = left.getChild().getSelectedShape(x, y);
      if (s != null) {
        return s;
      }
    }
    if (middle != null) {
      AShape s = middle.getChild().getSelectedShape(x, y);
      if (s != null) {
        return s;
      }
    }
    if (right != null) {
      AShape s = right.getChild().getSelectedShape(x, y);
      if (s != null) {
        return s;
      }
    }
    return null;
  }

  @Override
  public void gatherChildrenShapes(List<AShape> list) {
    if (left != null) {
      left.gatherChildrenShapes(list);
    }
    if (middle != null) {
      middle.gatherChildrenShapes(list);
    }
    if (right != null) {
      right.gatherChildrenShapes(list);
    }
  }

  @Override
  public void setText(String text) {
    super.setText(text);
    if (left != null) {
      left.setText(text);
    }
    if (middle != null) {
      middle.setText(text);
    }
    if (right != null) {
      right.setText(text);
    }
  }

  /**
   * @return the left
   */
  public ABindingShape getLeft() {
    return left;
  }

  /**
   * @param left the left to set
   */
  public void setLeft(ABindingShape left) {
    this.left = left;
  }

  /**
   * @return the middle
   */
  public ABindingShape getMiddle() {
    return middle;
  }

  /**
   * @param middle the middle to set
   */
  public void setMiddle(ABindingShape middle) {
    this.middle = middle;
  }

  /**
   * @return the right
   */
  public ABindingShape getRight() {
    return right;
  }

  /**
   * @param right the right to set
   */
  public void setRight(ABindingShape right) {
    this.right = right;
  }

  public void addToX(int delta, AShape shape) {
    if (left != null && left.getParent() != shape) {
      left.getParent().addToX(-delta);
    }
    if (right != null && right.getParent() != shape) {
      right.getParent().addToX(0);
    }
    if (middle != null && middle.getParent() != shape) {
      middle.getParent().addToX(delta);
    }
  }

  public boolean isFull() {
    return left != null && right != null && middle != null;
  }

  @Override
  public boolean generateCode(AContainer container) {
    boolean b = true;
    if (left != null) {
      b &= left.generateCode(container);
    }
    if (b && middle != null) {
      b &= middle.generateCode(container);
    }
    if (b && right != null) {
      b &= right.generateCode(container);
    }
    return b;
  }

  @Override
  public boolean generateActiveCode(AContainer container) {
    boolean b = true;
    if (left != null) {
      b &= left.generateActiveCode(container);
    }
    if (b && middle != null) {
      b &= middle.generateActiveCode(container);
    }
    if (b && right != null) {
      b &= right.generateActiveCode(container);
    }
    return b;
  }

  @Override
  public String getDeactivationCondition() {
    String s = null;
    if (left != null) {
      s = left.getDeactivationCondition();
    }
    if (middle != null) {
      String m = middle.getDeactivationCondition();
      if (s == null) {
        s = m;
      }
      else {
        s += " OR " + m;
      }
    }
    if (right != null) {
      String r = right.getDeactivationCondition();
      if (s == null) {
        s = r;
      }
      else {
        s += " OR " + r;
      }
    }
    return s;
  }

  public String getDeactivationCondition(TransitionNodeShape trShape, PNTransitionDialog trDialog) {
    String s = null;
    if (left != null) {
      AShape chld = left.getChild();
      trShape.addInputVariable(chld.getName(), chld.getDesc(), null);
      s =  trDialog.getDeactivationCondition(chld.getName(), 'P', chld);
    }
    if (middle != null) {
      AShape chld = middle.getChild();
      trShape.addInputVariable(chld.getName(), chld.getDesc(), null);
      String m = trDialog.getDeactivationCondition(chld.getName(), 'P', chld);
      if (s == null) {
        s = m;
      }
      else {
        s += " AND " + m;
      }
    }
    if (right != null) {
      AShape chld = right.getChild();
      trShape.addInputVariable(chld.getName(), chld.getDesc(), null);
      String r = trDialog.getDeactivationCondition(chld.getName(), 'P', chld);
      if (s == null) {
        s = r;
      }
      else {
        s += " AND " + r;
      }
    }
    return s;
  }

  @Override
  public void clearChildrenEntries() {
    if (left != null) {
      clearEntries(left.getChild());
    }
    if (middle != null) {
      clearEntries(middle.getChild());
    }
    if (right != null) {
      clearEntries(right.getChild());
    }
  }

  @Override
  public void declareResources() {
    if (left != null) {
      left.declareResources();
    }
    if (middle != null) {
      middle.declareResources();
    }
    if (right != null) {
      right.declareResources();
    }
  }

  @Override
  public void clearResources() {
    if (left != null) {
      left.clearResources();
    }
    if (middle != null) {
      middle.clearResources();
    }
    if (right != null) {
      right.clearResources();
    }
  }

  public void delete(ABindingShape cb, AShape cshape, boolean isParentChange) {
    if (left == cb) {
      left = null;
    }
    if (middle == cb) {
      middle = null;
    }
    if (right == cb) {
      right = null;
    }
    if (isParentChange) {
      if (left == null) {
        if (middle == null) {
          right.setBindingType(BindingType.DOWN);
          cshape.setParent(right);
        }
        else if (right == null) {
          middle.setBindingType(BindingType.DOWN);
          cshape.setParent(middle);
        }
      }
      else if (middle == null) {
        if (right == null) {
          left.setBindingType(BindingType.DOWN);
          cshape.setParent(left);
        }
      }
    }
  }
}
