package clp.edit.graphics.shapes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import clp.edit.graphics.dial.DecisionDialog;
import clp.edit.graphics.shapes.util.ResourceHelper;
import clp.edit.graphics.shapes.util.ResourceHelper.InputResourcesInfo;
import clp.run.res.VarType;

abstract public class ADecisionShape extends AShape {

  private static final long serialVersionUID = 6410312957242049081L;


  private Hashtable<String, InputResourcesInfo> inputResources;

  private BindingShape left;
  private BindingShape right;
  private BindingShape leftup;
  private BindingShape rightup;

  private boolean isDecisionUp;

  private DecisionInfo info;

  private int nbBranches;

  public ADecisionShape(int width, int height, String name) {
    super(width, height, name, "Decision ");
    nbBranches = 2;
    info = new DecisionInfo();
    inputResources = new Hashtable<>();
  }

  public void resetChildren() {
    left = null;
    right = null;
    leftup = null;
    rightup = null;
  }

  @Override
  public ABindingShape getChild() {
    if (left != null) {
      if (right != null || rightup != null) {
        return left;
      }
    }
    else if (leftup != null) {
      if (right != null || rightup != null) {
        return leftup;
      }
    }
    return null;
  }

  public boolean isComplete() {
    return nbBranches == 0;
  }

  private void decrement() {
    if (nbBranches > 0) {
      nbBranches--;
    }
  }

  /**
   * @return the left
   */
  public BindingShape getLeft() {
    return left;
  }

  /**
   * @param left the left to set
   */
  public void setLeft(BindingShape left) {
    this.left = left;
    if (left != null) {
      left.setText(info.getLeftPartName());
      this.left.setConditionnallyBindingType(BindingType.DOWN_LEFT);
      left.setParent(this);
      decrement();
    }
  }

  /**
   * @return the right
   */
  public BindingShape getRight() {
    return right;
  }

  /**
   * @param right the right to set
   */
  public void setRight(BindingShape right) {
    this.right = right;
    if (right != null) {
      right.setText(info.getRightPartName());
      this.right.setConditionnallyBindingType(BindingType.DOWN_RIGHT);
      right.setParent(this);
      decrement();
    }
  }

  /**
   * @return the leftup
   */
  public BindingShape getLeftup() {
    return leftup;
  }

  /**
   * @param leftup the leftup to set
   */
  public void setLeftup(BindingShape leftup) {
    this.leftup = leftup;
    if (leftup != null) {
      leftup.setText(info.getLeftPartName());
      this.leftup.setConditionnallyBindingType(BindingType.UP_LEFT);
      decrement();
    }
  }

  /**
   * @return the rightup
   */
  public BindingShape getRightup() {
    return rightup;
  }

  /**
   * @param rightup the rightup to set
   */
  public void setRightup(BindingShape rightup) {
    this.rightup = rightup;
    if (rightup != null) {
      rightup.setText(info.getRightPartName());
      this.rightup.setConditionnallyBindingType(BindingType.UP_RIGHT);
      decrement();
    }
  }

  public boolean isDecisionUp() {
    return isDecisionUp;
  }

  public void setDecisionUp(boolean isDecisionUp) {
    this.isDecisionUp = isDecisionUp;
  }

  public void swapInfo() {
    String n = info.getRightPartName();
    info.setRightPartName(info.getLeftPartName());
    info.setLeftPartName(n);
    n = info.getRightPartDescription();
    info.setRightPartDescription(info.getLeftPartDescription());
    info.setLeftPartDescription(n);
  }

  /**
   * @return the info
   */
  public DecisionInfo getInfo() {
    return info;
  }

  public void updateDeltaX(int x) {
    if (getLeftup() != null) {
      getLeftup().shiftX(x);
    }
    if (getRightup() != null) {
      getRightup().shiftX(x);
    }
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    String s;
    if (bs == left) {
      s = left.getText();
      ResourceHelper.getInstance().addInputVariable(s, info.getLeftPartDescription(), VarType.TBOOL, false, inputResources);
    }
    else if (bs == right) {
      s = right.getText();
      ResourceHelper.getInstance().addInputVariable(s, info.getRightPartDescription(), VarType.TBOOL, false, inputResources);
    }
    else if (bs == leftup) {
      s = leftup.getText();
      ResourceHelper.getInstance().addInputVariable(s, info.getLeftPartDescription(), VarType.TBOOL, false, inputResources);
    }
    else {
      s = rightup.getText();
      ResourceHelper.getInstance().addInputVariable(s, info.getRightPartDescription(), VarType.TBOOL, false, inputResources);
    }
    return " " + s + " AND activated(" + getParent().getParent().getName() + ") ";
  }

  @Override
  public String getDeactivationCondition() {
    ArrayList<String> list = new ArrayList<>();
    if (left != null) {
      fillListAndDeclareEvent(list, left.getChild());
    }
    if (right != null) {
      fillListAndDeclareEvent(list, right.getChild());
    }
    if (leftup != null) {
      fillListAndDeclareEvent(list, leftup.getChild());
    }
    if (rightup != null) {
      fillListAndDeclareEvent(list, rightup.getChild());
    }
    String s = list.toString();
    return s.substring(1, s.length()-2).replace(",", " OR");
  }

  //
  private void fillListAndDeclareEvent(ArrayList<String> list, AShape chld) {
    ResourceHelper.getInstance().addInputVariable(chld.getName(), chld.getDesc(), null, false, inputResources);
    list.add(" activated(" + chld.getName() + ") ");
  }

  @Override
  public void declareResources(boolean isEndNode) {
    ResourceHelper.getInstance().declareIn(inputResources);
    if (left != null) {
      left.declareResources();
    }
    if (right != null) {
      right.declareResources();
    }
  }

  @Override
  public void clearResources() {
    inputResources.clear();
    if (left != null) {
      left.clearResources();
    }
    if (right != null) {
      right.clearResources();
    }
  }

  @Override
  public void upateDialog() {
    ((DecisionDialog)getDialog()).updateFromInfo(info);
  }

  //===========================================================================

  public class DecisionInfo implements Serializable {
    private static final long serialVersionUID = 8765843389784929551L;
    private String leftPartName;
    private String rightPartName;
    private String leftPartDescription;
    private String rightPartDescription;
    /**
     * @return the leftPartName
     */
    public String getLeftPartName() {
      return leftPartName;
    }
    /**
     * @param leftPartName the leftPartName to set
     */
    public void setLeftPartName(String leftPartName) {
      this.leftPartName = leftPartName;
    }
    /**
     * @return the rightPartName
     */
    public String getRightPartName() {
      return rightPartName;
    }
    /**
     * @param rightPartName the rightPartName to set
     */
    public void setRightPartName(String rightPartName) {
      this.rightPartName = rightPartName;
    }
    /**
     * @return the leftPartDescription
     */
    public String getLeftPartDescription() {
      return leftPartDescription;
    }
    /**
     * @param leftPartDescription the leftPartDescription to set
     */
    public void setLeftPartDescription(String leftPartDescription) {
      this.leftPartDescription = leftPartDescription;
    }
    /**
     * @return the rightPartDescription
     */
    public String getRightPartDescription() {
      return rightPartDescription;
    }
    /**
     * @param rightPartDescription the rightPartDescription to set
     */
    public void setRightPartDescription(String rightPartDescription) {
      this.rightPartDescription = rightPartDescription;
    }
  }
}
