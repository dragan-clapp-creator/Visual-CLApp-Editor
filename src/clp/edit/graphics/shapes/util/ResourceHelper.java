package clp.edit.graphics.shapes.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.graphics.code.gui.elts.GuiInfo;
import clp.edit.graphics.code.java.JavaContext.BciInfo;
import clp.edit.graphics.code.web.WebContext.WebInfo;
import clp.edit.graphics.dial.Token;
import clp.edit.graphics.panel.ControlsContainer;
import clp.edit.graphics.panel.SimulationHelper;
import clp.edit.graphics.shapes.ActionShape.LibInfo;
import clp.run.res.Unit;
import clp.run.res.VarType;

public class ResourceHelper {

  private static final ResourceHelper instance = new ResourceHelper();

  public static ResourceHelper getInstance() {
    return instance;
  }

  private ResourceHelper() {}

  /**
   * extracts variables out of given condition string and add it to the list of input resources
   * 
   * @param cnd
   * @param desc
   * @param type
   * @param isToken
   * @param inputResources
   */
  public void addInputVariable(String cnd, String desc, VarType type, boolean isToken, Hashtable<String, InputResourcesInfo> inputResources) {
    String[] names = extractNames(cnd);
    for (String n : names) {
      if (!n.isBlank()) {
        InputResourcesInfo ri = null;
        switch (n.charAt(0)) {
          case 'A':
          case 'X':
          case 'P':
            if (!isNumerical(n.substring(1))) {
              ri = new InputResourcesInfo(desc, type);
            }
            else {
              if (isToken) {
                ri = new InputResourcesInfo(new Token[0]);
              }
              else {
                ri = new InputResourcesInfo();
              }
            }
            break;
          case '<':
          case '>':
            break;

          default:
            try {
              Integer.parseInt(n);
            }
            catch (NumberFormatException e) {
              ri = new InputResourcesInfo(desc, type);
            }
            break;
        }
        if (ri != null) {
          inputResources.put(n, ri);
        }
      }
    }
  }

  /**
   * add variables coming from a java call
   * 
   * @param variables
   * @param inputResources
   * @param outputResources 
   * @return
   */
  public void addVariables(ArrayList<String> variables, Hashtable<String, InputResourcesInfo> inputResources, Hashtable<String, OutputResourcesInfo> outputResources) {
    for (String text : variables) {
      if (text.isBlank()) {
        continue;
      }
      String[] sp = text.split("/");
      VarType tp = VarType.valueOf("T"+sp[1]);
      if (sp.length > 2 && sp[2].equals("D")) {
        OutputResourcesInfo ro = new OutputResourcesInfo(tp);
        outputResources.put(sp[0], ro);
      }
      else {
        InputResourcesInfo ri = new InputResourcesInfo(tp);
        inputResources.put(sp[0], ri);
      }
    }
  }

  /**
   * add output variable of given name string and add it to the list of output resources
   * 
   * @param name
   * @param varType
   * @param outputResources
   */
  public void addOutputVariable(String name, VarType varType, Hashtable<String, OutputResourcesInfo> outputResources) {
    OutputResourcesInfo ro = new OutputResourcesInfo(varType);
    outputResources.put(name, ro);
  }

  public void setTokensForCell(Token[] tokens, String name, Hashtable<String, InputResourcesInfo> inputResources) {
    InputResourcesInfo ri = new InputResourcesInfo(tokens);
    inputResources.put(name, ri);
  }

  public void addDelay(String step, String timeIdentifier, int delay, Unit unit, boolean isCyclic, Hashtable<String, InputResourcesInfo> inputResources) {
    InputResourcesInfo ri = new InputResourcesInfo(timeIdentifier, delay, unit, isCyclic);
    inputResources.put(step, ri);
  }

  //
  private boolean isNumerical(String string) {
    try {
      Integer.parseInt(string);
      return true;
    }
    catch (NumberFormatException e) {
      return false;
    }
  }

  //
  private String[] extractNames(String name) {
    name = name.replaceAll("AND", " ");
    name = name.replaceAll("OR", " ");
    name = name.replaceAll("NOT", " ");
    name = replace(name, '(');
    name = replace(name, ')');
    name = replace(name, ')');
    name = replace(name, '<');
    name = replace(name, '>');
    name = replace(name, ',');
    name = replace(name, '=');
    return name.split(" ");
  }

  //
  private String replace(String name, char c) {
    int i = name.indexOf(c);
    while (i >= 0) {
      name = name.substring(0, i) + " " + name.substring(i+1);
      i = name.indexOf(c);
    }
    return name;
  }

  public void declareLibs(List<LibInfo> libs) {
    if (!libs.isEmpty()) {
      SimulationHelper sh = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer().getSimulationHelper();
      for (LibInfo li : libs) {
        sh.addToLibs(li.getUsedLib(), li.isJar());
      }
    }
  }

  public void declareUI(GuiInfo ui, String name) {
    if (ui != null) {
      ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
      cc.addVariable(name, ui.getUiVar());
      cc.getSimulationHelper().addVariables(ui.getVariables());
    }
  }

  public void declareBCI(BciInfo bci, String name) {
    if (bci != null) {
      ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
      cc.addVariable(name, bci.getBciVar());
      cc.getSimulationHelper().addVariables(bci.getVariables());
    }
  }

  public void declareWEB(WebInfo web, String name) {
    if (web != null) {
      ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
      cc.addVariable(name, web.getWebVar());
    }
  }

  public void declareIn(Hashtable<String, InputResourcesInfo> inputResources) {
    if (!inputResources.isEmpty()) {
      SimulationHelper sh = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer().getSimulationHelper();
      for (String name : inputResources.keySet()) {
        InputResourcesInfo ri = inputResources.get(name);
        switch(ri.itype) {
          case VAR:
            sh.addInputVariableToRes(name, ri.desc, ri.type);
            break;
          case DUMMY:
            sh.addVariableToRes(name, ri.type);
            break;
          case EVENT:
            sh.addCellEventToRes(name);
            break;
          case TOKEN:
            sh.addTokensToRes(ri.tokens, name);
            break;
          case DELAY:
            sh.addDelay(name, ri.timeIdentifier, ri.delay, ri.unit, ri.isCyclic);
            break;
        }
      }
    }
  }

  public void declareOut(Hashtable<String, OutputResourcesInfo> outputResources) {
    if (!outputResources.isEmpty()) {
      ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
      for (String name : outputResources.keySet()) {
        OutputResourcesInfo ro = outputResources.get(name);
        if (!cc.addOutputVariable(name, ro.type)) {
          System.err.printf("variable %s already declared with another type\n", name);
        }
      }
    }
  }


  //=================================================================

  public static enum InfoType {
    VAR, EVENT, TOKEN, DELAY, DUMMY;
  }

  public static class InputResourcesInfo implements Serializable {
    private static final long serialVersionUID = 4735529696548584294L;

    private InfoType itype;

    private String desc;
    private VarType type;
    private Token[] tokens;

    private String timeIdentifier;
    private int delay;
    private Unit unit;
    private boolean isCyclic;

    public InputResourcesInfo(VarType t) {
      type = t;
      itype = InfoType.DUMMY;
    }

    public InputResourcesInfo(String d, VarType t) {
      desc = d;
      type = t;
      itype = InfoType.VAR;
    }

    public InputResourcesInfo(Token[] t) {
      tokens = t;
      itype = InfoType.TOKEN;
    }

    public InputResourcesInfo() {
      itype = InfoType.EVENT;
    }

    public InputResourcesInfo(String t, int d, Unit u, boolean b) {
      itype = InfoType.DELAY;
      timeIdentifier = t;
      delay = d;
      unit = u;
      isCyclic = b;
    }
  }

  public static class OutputResourcesInfo implements Serializable {
    private static final long serialVersionUID = -3836670690239533614L;

    private VarType type;

    public OutputResourcesInfo(VarType t) {
      type = t;
    }
  }
}
