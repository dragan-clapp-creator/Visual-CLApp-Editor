package clp.edit.graphics.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import clp.edit.GeneralContext;
import clp.edit.graphics.dial.Token;
import clp.edit.graphics.panel.cntrl.AInputButton;
import clp.edit.graphics.panel.cntrl.ConditionButton;
import clp.edit.graphics.panel.cntrl.DelayButton;
import clp.edit.graphics.panel.cntrl.EventButton;
import clp.edit.graphics.panel.cntrl.OutputButton;
import clp.edit.graphics.panel.cntrl.OutputPanel;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.handler.CLAppSourceHandler;
import clp.run.res.Unit;
import clp.run.res.VarType;

public class SimulationHelper implements Serializable {

  private static final long serialVersionUID = -3745794325213012504L;

  private ArrayList<ControlInfo> controls;
  private CLAppSourceHandler sourceHandler;

  private DelayButton delayButton;                  // used to display as tool tip text all declared delays
  private ArrayList<EventButton> events;            // list of all declared events
  private ArrayList<ConditionButton> conditions;    // list of all declared conditions (input variables)
  private ArrayList<OutputButton> outButtons;       // list of all declared output buttons (boolean variables)
  private ArrayList<OutputPanel> outPanels;         // list of all declared output panels (containing other variables)

  private ControlsContainer controlsContainer;

  private boolean isDirty;

  private boolean isGenerated;

  /**
   *  CONSTRUCTOR
   * @param cc controlsContainer 
   * @param sh sourceHandler 
   */
  public SimulationHelper(ControlsContainer cc, CLAppSourceHandler sh) {
    controls = new ArrayList<>();
    sourceHandler = sh;
    controlsContainer = cc;
    events = new ArrayList<>();
    conditions = new ArrayList<>();
    outButtons = new ArrayList<>();
    outPanels = new ArrayList<>();
    delayButton = new DelayButton();
    isDirty = true;
  }

  /**
   * @return the controls
   */
  public ArrayList<ControlInfo> getControls() {
    return controls;
  }

  /**
   * @param controls the controls to set
   */
  public void setControls(ArrayList<ControlInfo> controls) {
    this.controls = controls;
  }

  /**
   * generate CLApp code for all created container shapes, if dirty
   * and additionally extracts input and output variables with associated controls
   * @return
   */
  public boolean generateCode() {
    isGenerated = false;
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    List<AContainer> containers = shapesContainer.getContainers();
    isDirty |= isAnyContainerDirty(containers);
    if (isDirty) {
      cleanAll();
      for (AContainer container : containers) {
        if (container.isActive()) {
          //----------- GENERATE CODE FOR A CONTAINER ----------------------
          if (!container.generateCode()) {
            return false;
          }
        }
      }
      resetContainersDirtyFlag(containers);
      controlsContainer.drawControls();
      controlsContainer.updateTreeResources();
      isDirty = false;
    }
    isGenerated = true;
    return true;
  }

  public boolean isAnyContainerDirty(List<AContainer> containers) {
    for (AContainer container : containers) {
      if (container.isDirty()) {
        return true;
      }
    }
    return false;
  }

  public void resetContainersDirtyFlag(List<AContainer> containers) {
    for (AContainer container : containers) {
      if (container.isDirty()) {
        container.setDirty(false);
      }
    }
    // as long as project isn't saved, following flag should be true
    GeneralContext.getInstance().getClappEditor().setDirty(true);
  }

  private void cleanAll() {
    events.clear();
    conditions.clear();
    controls.clear();
    controlsContainer.removeRes();
    delayButton.getDelays().clear();
    outButtons.clear();
    outPanels.clear();
    sourceHandler.cleanMetaScenario();
  }

  /**
   * set tokens of a particular cell to the resources block
   * 
   * @param tokens
   * @param name 
   */
  public void addTokensToRes(Token[] tokens, String name) {
    sourceHandler.addTokensToRes(tokens, name);
  }

  /**
   * add cell event to the resources block
   * 
   * @param cellName
   */
  public void addCellEventToRes(String cellName) {
    sourceHandler.addCellEventToRes(cellName);
  }

  /**
   * just add given variables to clapp resources, if not already created, replace otherwise
   * 
   * @param uiName
   * @param uiVar
   * @return 
   */
  public boolean addVariables(ArrayList<String> variables) {
    for (String text : variables) {
      if (text.isBlank()) {
        continue;
      }
      String[] sp = text.split("/");
      if (sp[0].isBlank()) {
        return false;
      }
      VarType tp = VarType.valueOf("T"+sp[1]);
      Boolean isResult = sourceHandler.addVariableToRes(sp[0], tp);
      if (isResult == null) {
        if (sp.length > 2 && sp[2].equals("D")) {
          addToOutput(sp[0], tp);
        }
      }
      else if (isResult == Boolean.FALSE) {
        System.err.printf("Variable %s already exists but is not declared as %s variable\n", sp[0], tp.getVal());
        return false;
      }
    }
    return true;
  }

  public void addToOutput(String name, VarType varType) {
    ControlInfo ci = new ControlInfo(name, name, varType, false);
    controls.add(ci);
    JComponent comp = ci.getComponent();
    if (comp != null) {
      int x;
      int y;
      if (varType == VarType.TBOOL) {
        x = 0;
        y = outButtons.size();
        OutputButton outb = (OutputButton) comp;
        outButtons.add(outb);
        outb.setCol(x);
        outb.setLine(y);
      }
      else {
        x = 1;
        y = outPanels.size();
        OutputPanel outp = (OutputPanel) comp;
        outPanels.add(outp);
        outp.setCol(x);
        outp.setLine(y);
      }
    }
  }

  public void addVariableToRes(String name, VarType varType) {
    sourceHandler.addVariableToRes(name, varType);
  }

  public void addInputVariableToRes(String name, String description, VarType varType) {
    sourceHandler.addVariableToRes(name, varType);
    ControlInfo oldci = getExistingControlInfo(name);
    if (oldci != null) {
      return;
    }
    ControlInfo ci = new ControlInfo(name, description, varType, true);
    AInputButton ib = (AInputButton) ci.getComponent();
    if (ib instanceof EventButton) {
      addToEvents((EventButton) ib);
    }
    else if (ib instanceof ConditionButton) {
      addToConditions((ConditionButton) ib);
    }
    controls.add(ci);
  }

  public void addToLibs(String usedLib, boolean isJar) {
    sourceHandler.addLibToRes(usedLib, isJar);
  }

  //
  private void addToEvents(EventButton ev) {
    int x = 0;
    int y = events.size();
    ev.setCol(x);
    ev.setLine(y);
    events.add(ev);
  }

  //
  private void addToConditions(ConditionButton cnd) {
    int size = conditions.size();
    int x = (size % 4) + 1;
    int y = size / 4;
    cnd.setCol(x);
    cnd.setLine(y);
    conditions.add(cnd);
  }

  //
  private ControlInfo getExistingControlInfo(String name) {
    for (ControlInfo ci : controls) {
      if (ci.getName().equals(name)) {
        return ci;
      }
    }
    return null;
  }

  public void addDelay(String cellName, String timeIdentifier, int delay, Unit unit, boolean cycle) {
    sourceHandler.declareDelay(cellName, timeIdentifier, delay, unit, cycle);
    if (delayButton.isEmpty()) {
      addToEvents(delayButton);
    }
    delayButton.addToToolTip(timeIdentifier);
  }

  /**
   * @param delayButton the delayButton to set
   */
  public void setDelayButton(DelayButton delayButton) {
    this.delayButton = delayButton;
    if(this.delayButton == null) {
      this.delayButton = new DelayButton();
    }
    else if (!delayButton.isEmpty()) {
      addToEvents(delayButton);
    }
  }

  /**
   * @return the events
   */
  public ArrayList<EventButton> getEvents() {
    return events;
  }

  /**
   * @return the conditions
   */
  public ArrayList<ConditionButton> getConditions() {
    return conditions;
  }

  /**
   * @return the outButtons
   */
  public ArrayList<OutputButton> getOutButtons() {
    return outButtons;
  }

  /**
   * @return the delayButton
   */
  public DelayButton getDelayButton() {
    return delayButton;
  }

  /**
   * @return the outPanels
   */
  public ArrayList<OutputPanel> getOutPanels() {
    return outPanels;
  }

  /**
   * @return the isDirty
   */
  public boolean isDirty() {
    return isDirty;
  }
  public boolean isGloballyDirty() {
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    List<AContainer> containers = shapesContainer.getContainers();
    return isDirty || isAnyContainerDirty(containers);
  }

  /**
   * @return the isGenerated
   */
  public boolean isGenerated() {
    return isGenerated;
  }

  /**
   * @param isDirty the isDirty to set
   */
  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  public boolean isControlsEmpty() {
    return isControlsEmpty(events) && conditions.isEmpty();
  }

  //
  private boolean isControlsEmpty(ArrayList<EventButton> events) {
    if (events.size() == 1) {
      return events.get(0) instanceof DelayButton;
    }
    return events.isEmpty();
  }
}
