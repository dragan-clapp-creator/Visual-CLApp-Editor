package clp.edit.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clapp.run.Supervisor;
import clapp.run.sim.api.ISimulator;
import clapp.run.token.MarkHandler;
import clapp.run.util.CellChainLink;
import clapp.run.util.CellQueueHandler;
import clp.edit.graphics.dial.Token;
import clp.edit.graphics.panel.cntrl.AInputButton;
import clp.edit.graphics.panel.cntrl.DummyButton;
import clp.edit.graphics.panel.cntrl.DummyConditionButton;
import clp.edit.graphics.panel.cntrl.DummyEventButton;
import clp.edit.graphics.panel.cntrl.DummyPanel;
import clp.edit.graphics.panel.cntrl.IOutput;
import clp.edit.graphics.panel.cntrl.OutputPanel;
import clp.edit.graphics.panel.cntrl.StartButton;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.handler.CLAppSourceHandler;
import clp.edit.panel.GraphicsPanel;
import clp.run.cel.Cell;
import clp.run.cel.Weightings;
import clp.run.res.VarType;
import clp.run.res.WebVariable;
import clp.run.res.Weighting;
import clp.run.res.ui.UiVar;
import clp.run.res.weave.WeaveVar;

public class ControlsContainer extends JPanel  implements ISimulator {

  private static final long serialVersionUID = 2187222681113479252L;

  private static final Insets insets = new Insets(3, 3, 3, 3);

  private StartButton startButton;

  private Hashtable<String, CellQueueHandler> activeQueues;
  private Hashtable<String, ArrayList<CellChainLink>> previouslyActiveQueues;

  private boolean isSimulationRunning;

  private CLAppSourceHandler sourceHandler;
  private SimulationHelper helper;

  private GraphicsPanel graphicsPanel;
  private GeneralShapesContainer genericShapesContainer;

  private JPanel inputjp;
  private GridBagConstraints gc;

  private JPanel outputjp;

  private Hashtable<String, Boolean> activities;


  /**
   * CONSTRUCTOR
   * 
   * @param graphicsPanel 
   * @param shapesContainer 
   */
  public ControlsContainer(GraphicsPanel graphicsPanel, GeneralShapesContainer shapesContainer) {
    setLayout(new BorderLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        "Control");
    setBorder(border);

    this.graphicsPanel = graphicsPanel;
    this.genericShapesContainer = shapesContainer;

    initializeFields();

    JPanel jp1 = new JPanel();
    jp1.setLayout(new BorderLayout());
    add(jp1, BorderLayout.PAGE_START);

    JPanel jp2 = new JPanel();
    jp2.setLayout(new GridLayout(2,1));
    add(jp2, BorderLayout.CENTER);

    initializeStartArea(jp1);

    initializeInputArea(jp2);

    initializeOutputArea(jp2);
  }

  //
  private void initializeFields() {
    activeQueues = new Hashtable<>();
    previouslyActiveQueues = new Hashtable<>();
    sourceHandler = new CLAppSourceHandler();
    helper = new SimulationHelper(this, sourceHandler);
    activities = new Hashtable<>();

    setPreferredSize(new Dimension(250, 0));
  }

  //
  private void initializeStartArea(JPanel jp1) {
    startButton = new StartButton();
    setupStartActionListener();
    jp1.add(startButton, BorderLayout.CENTER);
  }

  //
  public void setupStartActionListener() {
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (genericShapesContainer.getCurrentContainer() == null) {
          return;
        }
        if (!isSimulationRunning) {
          //=====================
          // GENERATE SOURCE CODE
          if (!helper.isGenerated()) {
            helper.setDirty(true);
          }
          helper.generateCode();
          if (!helper.isGenerated() && helper.isDirty()) {
            System.err.println("ERROR: problem found in code generation");
          }
          else {
            isSimulationRunning = true;
            for (ControlInfo ci : helper.getControls()) {
              if (ci.getComponent() instanceof AbstractButton) {
                ((AbstractButton)ci.getComponent()).setEnabled(true);
              }
              else if (ci.getComponent() instanceof OutputPanel) {
                ((OutputPanel)ci.getComponent()).disableIt();
              }
            }
            //===================================
            // GET SOURCE & TRIGGER SIMULATE
            graphicsPanel.getButtonsContainer().setButtonsEnabled(false);
            genericShapesContainer.setEnabled(false);
            genericShapesContainer.getCurrentContainer().getAutomaton().enabling(false);
            sourceHandler.startSimulation(ControlsContainer.this);
            //===================================
          }
        }
        else {
          if (Supervisor.getInstance() != null) {
            Supervisor.getInstance().stopAll(ControlsContainer.this, null);    // will call #onFinish()
          }
        }
      }
    });
  }

  //
  public void setupControlActionListeners() {
    for (ControlInfo ci : helper.getControls()) {
      ci.setupActionListener();
    }
  }

  //
  private void initializeInputArea(JPanel jp2) {
    gc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, insets, 0, 0);
    inputjp = new JPanel();
    inputjp.setLayout(new GridBagLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Inputs");
    inputjp.setBorder(border);

    for (int i=0; i<40; i++) {
      gc.gridx = i%5;
      gc.gridy = i/5;
      DummyConditionButton dummy = new DummyConditionButton();
      dummy.setCol(gc.gridx);
      dummy.setLine(gc.gridy);
      inputjp.add(dummy, gc);
    }
    jp2.add(inputjp);
  }

  //
  private void initializeOutputArea(JPanel jp2) {
    outputjp = new JPanel();
    outputjp.setLayout(new FlowLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Outputs");
    outputjp.setBorder(border);
    for (int i=0; i<16; i++) {
      gc.gridy = i/2;
      if (i%2 == 0) {
        gc.gridx = 0;
        DummyEventButton dummy = new DummyEventButton();
        dummy.setCol(0);
        dummy.setLine(gc.gridy);
        outputjp.add(dummy, gc);
      }
      else {
        gc.gridx = 1;
        gc.gridwidth = 4;
        gc.gridheight = 2;
        DummyConditionButton dummy = new DummyConditionButton();
        dummy.setCol(1);
        dummy.setLine(gc.gridy);
        outputjp.add(dummy, gc);
      }
    }
    gc.gridwidth = 1;
    gc.gridheight = 1;
    jp2.add(outputjp);
  }

  /**
   * add variable to output area as control button (if boolean, then toggle button, otherwise it's a field)
   * 
   * @param name
   * @param varType
   * @return 
   */
  public boolean addOutputVariable(String name, VarType varType) {
    Boolean isResult = sourceHandler.addVariableToRes(name, varType);
    if (isResult == null) {
      helper.addToOutput(name, varType);
    }
    return isResult != Boolean.FALSE;
  }

  /**
   * just add given UI variable to clapp resources, if not already created, replace otherwise
   * 
   * @param name
   * @param uiVar
   */
  public void addVariable(String name, UiVar uiVar) {
    sourceHandler.addUIVariableToRes(name, uiVar);
  }

  /**
   * just add given WeaveVar variable to clapp resources, if not already created, replace otherwise
   * 
   * @param name
   * @param wvar
   */
  public void addVariable(String name, WeaveVar wvar) {
    sourceHandler.addWeaveVariableToRes(name, wvar);
  }

  /**
   * just add given WebVariable variable to clapp resources, if not already created, replace otherwise
   * 
   * @param name
   * @param wvar
   */
  public void addVariable(String name, WebVariable wvar) {
    sourceHandler.addWebVariableToRes(name, wvar);
  }

  /**
   * checks whether given variable name is declared in clapp resources
   * 
   * @param name
   * @return
   */
  public boolean existsVariable(String name) {
    return sourceHandler.existsVariableInRes(name);
  }

  public String gatherSimpleVariables() {
    StringBuffer sb = new StringBuffer();
    for (ControlInfo ci : helper.getControls()) {
      if (ci.getType() != VarType.TUI) {
        sb.append(ci.toString());
        sb.append("\r\n");
      }
    }
    return sb.toString();
  }

  public String gatherUiVariables() {
    return gatherVariablesFromControllers(VarType.TUI);
  }

  //
  private String gatherVariablesFromControllers(VarType type) {
    StringBuffer sb = new StringBuffer();
    for (ControlInfo ci : helper.getControls()) {
      if (ci.getType() == type) {
        sb.append(ci.toString());
        sb.append("\r\n");
      }
    }
    return sb.toString();
  }

  @Override
  public void onExecution(String key, CellQueueHandler activeQueue) {
    if (activeQueue == null) {
      if (key != null && activeQueues.get(key) != null) {
        updateControls();
        ArrayList<CellChainLink> activeCells = populateActiveCells(activeQueues.get(key));
        ArrayList<CellChainLink> previouslyActiveCells = previouslyActiveQueues.get(key);
        if (hasActivityChanged(activeCells, previouslyActiveCells)) {
          updateUI(key, activeCells, previouslyActiveCells);
        }
      }
    }
    else if (!activeQueues.containsKey(key)) {
      // register active queue
      activeQueues.put(key, activeQueue);
      ArrayList<CellChainLink> previouslyActiveCells = new ArrayList<>();
      previouslyActiveQueues.put(key, previouslyActiveCells);
    }
  }

  //
  private void updateUI(String key, ArrayList<CellChainLink> activeCells, ArrayList<CellChainLink> previouslyActiveCells) {
    sourceHandler.resetBackgroundColor(key);
    for (CellChainLink ccl : activeCells) {
      sourceHandler.setBackgroundColor(key, ccl.getName());
    }
    previouslyActiveCells.clear();
    previouslyActiveCells.addAll(activeCells);
    Hashtable<String, Cell> mc = MarkHandler.getInstance().getMarkedCells();
    for (String name : mc.keySet()) {
      sourceHandler.updateCellMarks(key, name, mc.get(name));
    }
    refresh();
  }

  //
  private ArrayList<CellChainLink> populateActiveCells(CellQueueHandler queue) {
    ArrayList<CellChainLink> activeCells = new ArrayList<>();
    CellChainLink ccl = queue.getFirstCell();
    while (ccl != null) {
      if (accepted(ccl.getCell())) {
        activeCells.add(ccl);
      }
      ccl = ccl.getNext();
    }
    return activeCells;
  }

  //
  private boolean accepted(Cell cell) {
    if (!cell.getName().startsWith("P")) {
      return true;
    }
    Weightings weightings = cell.getWeightings();
    Weighting w = weightings.getWeighting();
    if (w != null && w.getWeight() > 0) {
      return true;
    }
    for (Weighting aw : weightings.getWeightings()) {
      if (aw.getWeight() > 0) {
        return true;
      }
    }
    return false;
  }

  //
  private void updateControls() {
    for (ControlInfo ci : helper.getControls()) {
      ci.updateOutput();
    }
  }

  //
  private boolean hasActivityChanged(ArrayList<CellChainLink> activeCells, ArrayList<CellChainLink> previouslyActiveCells) {
    for (CellChainLink ccl : previouslyActiveCells) {
      if (!activeCells.contains(ccl)) {
        return true;
      }
    }
    for (CellChainLink ccl : activeCells) {
      if (!previouslyActiveCells.contains(ccl)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onFinish() {
    startButton.setBackground(Color.lightGray);
    startButton.setForeground(Color.black);
    startButton.setText("Simulate");
    startButton.setSelected(false);
    for (ControlInfo ci : helper.getControls()) {
      if (ci.getComponent() instanceof AbstractButton) {
        ((AbstractButton)ci.getComponent()).setSelected(false);
        ((AbstractButton)ci.getComponent()).setEnabled(false);
      }
      else if (ci.getComponent() instanceof OutputPanel) {
        ((OutputPanel)ci.getComponent()).enableIt();
      }
    }
    isSimulationRunning = false;
    sourceHandler.resetBackgroundColorForAll();
    sourceHandler.resetAllCellMarks();
    previouslyActiveQueues.clear();
    activeQueues.clear();
    graphicsPanel.getButtonsContainer().setButtonsEnabled(true);
    genericShapesContainer.setEnabled(true);
    genericShapesContainer.getCurrentContainer().getAutomaton().enabling(true);
    refresh();
  }

  public void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        genericShapesContainer.repaint();
      }
    });
  }

  //
  private void drawInputArea() {
    inputjp.removeAll();
    for (int i=0; i<40; i++) {
      int col = i%5;
      int line = i/5;
      gc.gridx = col;
      gc.gridy = line;
      AInputButton dummy;
      if (col == 0 && line < helper.getEvents().size()) {
        dummy = helper.getEvents().get(line);
      }
      else if (col > 0 && col-1 + line*4 < helper.getConditions().size()) {
        dummy = helper.getConditions().get(col-1 + line*4);
      }
      else {
        dummy = new DummyConditionButton();
        dummy.setCol(gc.gridx);
        dummy.setLine(gc.gridy);
      }
      inputjp.add(dummy, gc);
    }
    validate();
    repaint();
  }

  //
  private void drawOutputArea() {
    outputjp.removeAll();
    for (int i=0; i<16; i++) {
      int col = i%2;
      int line = i/2;
      gc.gridx = col;
      gc.gridy = line;
      IOutput dummy;
      if (col == 0) {
        gc.gridwidth = 1;
        gc.gridheight = 1;
        if (line < helper.getOutButtons().size()) {
          dummy = helper.getOutButtons().get(line);
        }
        else {
          dummy = new DummyButton();
          dummy.setCol(col);
          dummy.setLine(line);
        }
      }
      else {
        gc.gridwidth = 4;
        gc.gridheight = 2;
        if (line < helper.getOutPanels().size()) {
          dummy = helper.getOutPanels().get(line);
        }
        else {
          dummy = new DummyPanel();
          dummy.setCol(col);
          dummy.setLine(line);
        }
      }
      outputjp.add((JComponent) dummy, gc);
    }
    gc.gridwidth = 1;
    gc.gridheight = 1;
    validate();
    repaint();
  }

  public void addShapeAsCell(ActionShape actionShape, String name) {
    String key = genericShapesContainer.getCurrentContainer().getKey();
    sourceHandler.addShapeAsCell(key, actionShape, name);
  }

  public void addTokensToRes(Token[] tokens, String name) {
    sourceHandler.addTokensToRes(tokens, name);
  }

  @Override
  public boolean getActivity(String name) {
    if (activities.containsKey(name)) {
      return activities.get(name);
    }
    return true;
  }

  @Override
  public void setActivity(String name, boolean b) {
    activities.put(name, b);
  }

  public void drawControls() {
    drawInputArea();
    drawOutputArea();
  }

  public void removeRes() {
    sourceHandler.removeResources();
  }

  public SimulationHelper getSimulationHelper() {
    return helper;
  }

  /**
   * @return the sourceHandler
   */
  public CLAppSourceHandler getSourceHandler() {
    return sourceHandler;
  }

  public void updateTreeResources() {
    sourceHandler.updateTreeResources();
  }
}
