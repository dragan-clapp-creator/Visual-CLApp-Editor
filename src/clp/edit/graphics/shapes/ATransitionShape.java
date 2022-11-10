package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Hashtable;

import javax.swing.JLabel;

import clp.edit.GeneralContext;
import clp.edit.graphics.dial.ClassicGroup;
import clp.edit.graphics.dial.DelayGroup;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.util.ResourceHelper;
import clp.edit.graphics.shapes.util.ResourceHelper.InputResourcesInfo;
import clp.run.res.Unit;
import clp.run.res.VarType;

public abstract class ATransitionShape extends AShape {

  private static final long serialVersionUID = -6805542307034576950L;

  private JLabel tautology;
  private ClassicGroup classicInfo;
  private DelayGroup delayInfo;

  private TransitionType trType;

  private Hashtable<String, InputResourcesInfo> inputResources;

  /**
   * CONSTRUCTOR
   * 
   * @param tt: transition type 
   */
  public ATransitionShape(TransitionType tt) {
    super(40, 5, "", "");
    setup(tt);
  }


  public ATransitionShape(int width, int height, String name, String string, TransitionType tt) {
    super(width, height, name, string);
    setup(tt);
  }

  //
  private void setup(TransitionType tt) {
    trType = tt;
    int delayNo = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingDelayNo();
    tautology = new JLabel("= 1");
    classicInfo = new ClassicGroup();
    delayInfo = new DelayGroup(delayNo);
    inputResources = new Hashtable<>();
  }


  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    int sx = getPX()+offset;
    int sy = getPY();
    if (isSelected()) {
      g.setColor(Color.lightGray);
    }
    g.fillRect(sx-20, sy, 40, 4);
    if (getChild() == null) {
      g.drawLine(sx, sy, sx, sy+20);
    }
    g.setColor(c);
    if (getName() != null) {
      g.drawString(getName(), sx+23, sy+5);
    }
    if (getChild() != null) {
      checkDownType();
      getChild().paintShape(g, offset);
    }
  }

  @Override
  public boolean generateCode(AContainer container) {
    if (getChild() != null) {
      return getChild().generateCode(container);
    }
    return true;
  }

  @Override
  public boolean generateActiveCode(AContainer container) {
    if (getChild() != null) {
      return getChild().generateActiveCode(container);
    }
    return true;
  }


  /**
   * @return the trType
   */
  public TransitionType getTrType() {
    return trType;
  }


  /**
   * @param trType the trType to set
   */
  public void setTrType(TransitionType trType) {
    this.trType = trType;
  }


  /**
   * @return the tautology
   */
  public JLabel getTautology() {
    return tautology;
  }


  /**
   * @return the classicInfo
   */
  public ClassicGroup getClassicInfo() {
    return classicInfo;
  }


  /**
   * @return the delayInfo
   */
  public DelayGroup getDelayInfo() {
    return delayInfo;
  }

  public void addInputVariable(String cnd, String desc, VarType type) {
    ResourceHelper.getInstance().addInputVariable(cnd, desc, type, false, inputResources);
  }

  public void addDelay(String step, String timeIdentifier, int delay, Unit unit, boolean isCyclic) {
    ResourceHelper.getInstance().addDelay(step, timeIdentifier, delay, unit, isCyclic, inputResources);
  }


  /**
   * @return the inputResources
   */
  public Hashtable<String, InputResourcesInfo> getInputResources() {
    return inputResources;
  }

  @Override
  public void declareResources(boolean isEndNode) {
    ResourceHelper.getInstance().declareIn(inputResources);
    if (getChild() != null) {
      getChild().declareResources();
    }
  }

  @Override
  public void clearResources() {
    inputResources.clear();
    if (getChild() != null) {
      getChild().clearResources();
    }
  }
}
