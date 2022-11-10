package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.dial.ActionOrStepDialog;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.dial.Token;
import clp.edit.graphics.panel.ControlsContainer;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.util.ResourceHelper;
import clp.edit.graphics.shapes.util.ResourceHelper.InputResourcesInfo;
import clp.edit.graphics.shapes.util.ResourceHelper.OutputResourcesInfo;
import clp.run.res.VarType;

abstract public class ActionShape extends AShape {

  private static final long serialVersionUID = 8795899175363804863L;

  private Color bgcolor;

  abstract public ActionOrStepDialog getActionOrStepDialog();

  private ArrayList<ABindingShape> entries;

  private List<ClappInstruction> instructions;
  private List<LibInfo> libs;

  private Hashtable<String, InputResourcesInfo> inputResources;
  private Hashtable<String, OutputResourcesInfo> outputResources;

  private String uiName;
  private String bciName;
  private String webName;

  private String respValue;

  private int level;

  /**
   * CONSTRUCTOR
   * 
   * @param width
   * @param height
   * @param name
   * @param prefix
   * @param level
   */
  public ActionShape(int width, int height, String name , String prefix, int level) {
    super(width, height, name, prefix);
    this.level = level;
    entries = new ArrayList<>();
    ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
    cc.addShapeAsCell(this, getName());
    inputResources = new Hashtable<>();
    outputResources = new Hashtable<>();
    libs = new ArrayList<>();
  }

  /**
   * paint instructions
   * 
   * @param g
   * @param offset
   */
  public void paintInstructionsFromShape(Graphics g, int offset) {
    if (instructions != null && !instructions.isEmpty()) {
      int x1 = getPX() + getWidth()/2 + offset;
      int x2 = x1 + 5;
      int y0 = getPY();
      int y1 = y0 + getHeight()/2;
      g.drawLine(x1, y1, x2, y1);
      int width = instructions.size()*6 + 2;
      int height = getHeight();
      Color clr = g.getColor();
      g.setColor(Color.black);
      g.fillRect(x2, y0, width, height);
      int x0 = x2 + 2;
      height -= 4;
      y0 += 2;
      for (ClappInstruction inst : instructions) {
        g.setColor(inst.getColor());
        g.fillRect(x0, y0, 4, height);
        x0 += 6;
      }
      g.setColor(clr);
    }
  }

  @Override
  public ADialog getDialog() {
    return getActionOrStepDialog();
  }

  @Override
  public void gatherLibsDeclaration(ArrayList<String> jars) {
    System.err.println("NOT IMPLEMENTED YET");
//    ArrayList<String> j = dialog.getLibsDeclaration();
//    if (j != null) {
//      jars.addAll( j );
//    }
  }

  /**
   * @return the bgcolor
   */
  public Color getBgcolor() {
    return bgcolor;
  }

  /**
   * @param bgcolor the bgcolor to set
   */
  public void setBgcolor(Color bgcolor) {
    this.bgcolor = bgcolor;
  }

  /**
   * entries are bindings from decision, fork or join nodes from below
   * @param b
   */
  public void addEntryPoint(ABindingShape b) {
    if (!entries.contains(b)) {
      entries.add(b);
    }
    if (!entries.contains(getParent())) {
      entries.add(getParent());
    }
  }

  /**
   * @return the cell source
   */
  public StringBuilder getSource() {
    // TODO
    return null;
  }

  //
  private StringBuilder getInstructions(Collection<String> c) {
    StringBuilder sb = new StringBuilder();
    for (String inst : c) {
      sb.append(inst);
      sb.append("<br>");
    }
    return sb;
  }

  @Override
  public String toString() {
    return getSource().toString();
  }

  /**
   * @return the entries
   */
  public ArrayList<ABindingShape> getEntries() {
    return entries;
  }

  public void setupInstructions() {
    instructions = getActionOrStepDialog().getInstructions();
    if (instructions != null) {
      ArrayList<String> slist = new ArrayList<>();
      for (ClappInstruction inst : instructions) {
        if (inst.getStatement() != null) {
          slist.add(inst.getStatement());
        }
      }
      String instructions = getInstructions(slist).toString();
      addToDesc(instructions);
    }
  }

  /**
   * @return the instructions
   */
  public List<ClappInstruction> getInstructions() {
    return instructions;
  }

  /**
   * @param instruction to be added to the instructions set
   */
  public void addToInstructions(ClappInstruction instruction) {
    if (instructions == null) {
      instructions = new ArrayList<>();
    }
    instructions.add(instruction);
  }

  public int getRightPart() {
    if (instructions == null || instructions.isEmpty()) {
      return getPX() + getWidth()/2;
    }
    return getPX() + getWidth()/2 + instructions.size()*6 + 6;
  }

  public void setAsInitial() {
//    getTreeNodeInfo().moveToActiveHeap(this.getName());
  }

  public void setAsEventRoot(ActionShape shape) {
//    getTreeNodeInfo().updateActivationDomain(this.getName(), shape.getName());
  }

  @Override
  public String getActivationDomain(AContainer container) {
    if (entries.isEmpty()) {
      return getParent().getParent().getActivationCondition(getParent());
    }
    ArrayList<String> list = new ArrayList<>();
    String s;
    for (ABindingShape b : entries) {
      s = b.getParent().getActivationCondition(b);
      if (s != null) {
        list.add(s);
      }
    }
    if (!list.isEmpty()) {
      s = list.get(0);
      if (list.size() > 1) {
        for (int i=1; i<list.size(); i++) {
          s += " ; " + list.get(i);
        }
      }
      return s;
    }
    return null;
  }

  @Override
  public String getDeactivationDomain(AContainer container) {
    if (getChild() != null && !containsExitInstruction()) {
      return getChild().getDeactivationCondition();
    }
    return null;
  }

  //
  private boolean containsExitInstruction() {
    if (instructions != null) {
      for (ClappInstruction inst : instructions) {
        if (inst.getInstructionType().equals(IntructionType.STOP.name())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public ArrayList<String> getExecutionDomain(AContainer container) {
    if (instructions != null && !instructions.isEmpty()) {
      ArrayList<String> instList = new ArrayList<>();
      for (ClappInstruction inst : instructions) {
        if (inst.getInstructionType().equals(IntructionType.ASSIGN.name())) {
          instList.add("1: " + inst.getStatement());
        }
        else {
          instList.add(level+": " + inst.getStatement());
        }
      }
      return instList;
    }
    return null;
  }

  public void addInputVariable(String cnd, String desc, VarType type, boolean isToken) {
    if (cnd.charAt(0) == '!') {
      cnd = cnd.substring(1);
    }
    ResourceHelper.getInstance().addInputVariable(cnd, desc, type, isToken, inputResources);
  }

  public void setTokensForCell(Token[] tokens, String name) {
    ResourceHelper.getInstance().setTokensForCell(tokens, name, inputResources);
  }

  @Override
  public void declareResources(boolean isEndNode) {
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    ResourceHelper.getInstance().declareUI(shapesContainer.getGuiContext().getUiInfo(uiName), uiName);
    ResourceHelper.getInstance().declareBCI(shapesContainer.getJavaContext().getBciInfo(bciName), bciName);
    ResourceHelper.getInstance().declareWEB(shapesContainer.getWebContext().getWebInfo(webName), webName);
    ResourceHelper.getInstance().declareLibs(libs);
    ResourceHelper.getInstance().declareIn(inputResources);
    ResourceHelper.getInstance().declareOut(outputResources);
    if (!isEndNode && getChild() != null) {
      getChild().declareResources();
    }
  }

  public void addOutputVariable(String name, VarType vtype) {
    if (name.charAt(0) == '!') {
      name = name.substring(1);
    }
    ResourceHelper.getInstance().addOutputVariable(name, vtype, outputResources);
  }

  public void addVariables(ArrayList<String> variables) {
    ResourceHelper.getInstance().addVariables(variables, inputResources, outputResources);
  }

  public void addToLibs(String usedLib, boolean isjar) {
    if (!contains(libs, usedLib)) {
      LibInfo li = new LibInfo(usedLib, isjar);
      libs.add(li);
    }
  }

  //
  private boolean contains(List<LibInfo> libs, String usedLib) {
    for (LibInfo li : libs) {
      if (usedLib.equals(li.getUsedLib())) {
        return true;
      }
    }
    return false;
  }

  public static class LibInfo implements Serializable {
    private static final long serialVersionUID = -5011254741874900541L;

    private String usedLib;
    private boolean isJar;

    public LibInfo(String usedLib, boolean isjar) {
      this.usedLib = usedLib;
      this.isJar = isjar;
    }

    /**
     * @return the usedLib
     */
    public String getUsedLib() {
      return usedLib;
    }

    /**
     * @return the isJar
     */
    public boolean isJar() {
      return isJar;
    }
  }

  public int getInstructionSize() {
    if (instructions != null && !instructions.isEmpty()) {
      return instructions.size() + 1;
    }
    return 0;
  }

  /**
   * @return the uiName
   */
  public String getUiName() {
    return uiName;
  }

  /**
   * @return the bciName
   */
  public String getBciName() {
    return bciName;
  }

  /**
   * @param uiName the uiName to set
   */
  public void setUiName(String uiName) {
    this.uiName = uiName;
  }

  /**
   * @param bciName the bciName to set
   */
  public void setBciName(String bciName) {
    this.bciName = bciName;
  }

  /**
   * @return the webName
   */
  public String getWebName() {
    return webName;
  }

  /**
   * @param webName the webName to set
   */
  public void setWebName(String webName) {
    this.webName = webName;
  }

  public void setWebRespValue(String respValue) {
    this.respValue = respValue;
  }

  public String getWebRespValue() {
    return respValue;
  }
}
