package clp.edit;

import java.awt.Color;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import clapp.cmp.ClappMain;
import clapp.run.Supervisor;
import clapp.run.sim.api.ISimulator;
import clapp.run.ui.util.ConsoleProvider;
import clapp.run.ui.util.DefaultConsoleHandler;
import clapp.run.util.CellQueueHandler;
import clp.edit.handler.TreeMouseListener;
import clp.edit.panel.ClappPanel;
import clp.edit.panel.ControlPanel;
import clp.edit.panel.GraphicsPanel;
import clp.edit.panel.PropertiesPanel;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.util.ColorSet;
import clp.edit.util.ConsoleHandler;
import clp.edit.util.CustomOutputStream;
import clp.edit.util.TreeCellRenderer;
import clp.run.msc.MscOutput;
import clp.run.msc.Port;

public class GeneralContext implements ISimulator, Serializable {

  private static final long serialVersionUID = -7461597408613568306L;

  private static final GeneralContext instance = new GeneralContext();

  static public GeneralContext getInstance() {
    return instance;
  }

  // used for runtime call
  private Port port;
  private JScrollPane outPanel;
  private MetaScenarioTreeNode mscNode;
  private PrintStream out;
  private PrintStream err;
  private MscOutput mscOutput;
  private JTree mainTree;
  private PropertiesPanel propsPanel;
  private ClappPanel clappPanel;
  private CLAppEditor clappEditor;
  private boolean isCodeDirty;
  private Hashtable<String, Boolean> activities;

  // set at start
  private Frame frame;

  private GraphicsPanel graphicsPanel;

  private JToggleButton startButton;

  private boolean isDesignTime;

  private boolean isStartEnabled;

  /**
   * PRIVATE CONSTRUCTOR
   */
  private GeneralContext() {
    activities = new Hashtable<>();
    isDesignTime = true;
  }

  /**
   * in runtime mode, start clapp with reference file associated to msc
   * 
   * @param mscNode 
   */
  public void startClappApplication(MetaScenarioTreeNode mscNode) {
    this.mscNode = mscNode;
    if (mscNode != null) {
      ClappMain clapp = mscNode.getClappMain();
      redirectOutput();
      if (isCodeDirty) {
        String source = ((FileTreeNode)mscNode.getParent()).getUnassignedSource();
        InputStream is = new ByteArrayInputStream(source.getBytes(), 0, source.length());
        clapp = new ClappMain();
        String str = clapp.silentParse(is);
        if (str != null) {
          ConsoleProvider.getInstance().eprint(str);
          retreiveStandardOutput();
        }
        else {
          isCodeDirty = false;
          mscNode.setClappMain(clapp);
        }
        clappEditor.setDirty(true); // code is now generated but not saved yet
      }
      if (!isCodeDirty) {
        port = clapp.getMetaScenario().getMetaScenarioBody().getPort();
        initializeConsole(clapp);
        Supervisor.startRunning(this, clapp);
      }
    }
    else {
      ConsoleProvider.getInstance().eprint("No meta-scenario defined");
      retreiveStandardOutput();
    }
  }

  //
  private void redirectOutput() {
    this.out = System.out;
    this.err = System.err;
    PrintStream printStream = new PrintStream(new CustomOutputStream(outPanel));
    System.setOut(printStream);
    System.setErr(printStream);
  }

  //
  private void retreiveStandardOutput() {
    System.setOut(out);
    System.setErr(err);
  }

  //
  private void initializeConsole(ClappMain clapp) {
    mscOutput = clapp.getMetaScenario().getMetaScenarioBody().getMscOutput();
    if (mscOutput != null) {
      JTabbedPane tabbedPane = new JTabbedPane();
      outPanel.getViewport().removeAll();
      outPanel.getViewport().add(tabbedPane);
      ConsoleHandler ch = new ConsoleHandler();
      ConsoleProvider.getInstance().register(ch);
      ch.setInitialized(false);
      ch.initialize(mscOutput, tabbedPane);   
    }
    else {
      ConsoleProvider.getInstance().register(new DefaultConsoleHandler());
    }
  }

  @Override
  public void onExecution(String scnName, CellQueueHandler activeQueue) {
  }

  @Override
  public void onFinish() {
    SwingUtilities.invokeLater( new Runnable() { 
      public void run() {
        updateClappPanel();
      } 
    } );
  }

  public boolean isRuntimeTriggered() {
    return !isDesignTime;
  }

  public JTree createMainTree(ProjectTreeNode root) {
    mainTree = new JTree(new DefaultTreeModel(root));
    ToolTipManager.sharedInstance().registerComponent(mainTree);
    TreeCellRenderer renderer = new TreeCellRenderer();
    renderer.setBackgroundSelectionColor(ColorSet.selectedBackground.getLight());
    renderer.setTextSelectionColor(Color.white);
    mainTree.setCellRenderer(renderer);
    TreeMouseListener treeMouseListener = new TreeMouseListener(mainTree);
    mainTree.addTreeSelectionListener(treeMouseListener);
    mainTree.addMouseListener(treeMouseListener);
    return mainTree;
  }

  /**
   * @return the mainTree
   */
  public JTree getMainTree() {
    return mainTree;
  }

  public PropertiesPanel createPropertiesPanel() {
    propsPanel = new PropertiesPanel();
    return propsPanel;
  }

  /**
   * @return the propsPanel
   */
  public PropertiesPanel getPropertiesPanel() {
    return propsPanel;
  }

  public JScrollPane createOutPanel() {
    outPanel = new JScrollPane();
    outPanel.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Output Area"));
    outPanel.getViewport().add(new JTextArea());
    return outPanel;
  }

  /**
   * @return the msc
   */
  public MetaScenarioTreeNode getMscNode() {
    return mscNode;
  }

  public boolean isDesignTime() {
    return isDesignTime;
  }

  public boolean isGraphics() {
    return graphicsPanel != null;
  }

  public void addSendToControlPanel(ATreeNode node) {
    if (port != null) {
      clappPanel.getCtrlPanel().addSendTo(node, port, clappPanel);
      clappPanel.refresh();
    }
  }

  public void recreateMainTree() {
    mainTree = clappPanel.recreateMainTree();
    clappPanel.setup(isDesignTime(), isStartEnabled);
  }

  public void updateClappPanel() {
    clappPanel.validate();
  }

  public ClappPanel createClappPanel(ProjectTreeNode root, boolean isEnabled) {
    clappPanel = new ClappPanel(root, isEnabled);
    isStartEnabled = isEnabled;
    return clappPanel;
  }

  public void addProperties(ATreeNode node) {
    if (isDesignTime) {
      propsPanel.setNodesProperties(node);
      clappPanel.updateUI();
    }
  }

  public void register(CLAppEditor clappEditor) {
    port = null;
    this.clappEditor = clappEditor;
  }

  /**
   * @return the clappEditor
   */
  public CLAppEditor getClappEditor() {
    return clappEditor;
  }

  public void setDesignRuntime(boolean b) {
    isDesignTime = b;
    if (isDesignTime) {
      startButton.setText("Start");
    }
    else {
      startButton.setText("Stop");
    }
  }

  public ClappPanel getClappPanel() {
    return clappPanel;
  }

  public ControlPanel getControlPanel() {
    return clappPanel.getCtrlPanel();
  }

  public void setDirty() {
    clappEditor.setDirty(true);
  }

  public void setSelection(ATreeNode node) {
    TreeMouseListener tml = (TreeMouseListener) mainTree.getTreeSelectionListeners()[0];
    TreePath paths = tml.getPath((TreeNode)node);
    mainTree.setSelectionPath(paths);
    clappPanel.redrawTreePanel();
    clappEditor.setDirty(true);
  }

  public Frame getFrame() {
    return frame;
  }

  /**
   * @param frame the frame to set
   */
  public void setFrame(Frame frame) {
    this.frame = frame;
  }

  public GraphicsPanel getGraphicsPanel() {
    return graphicsPanel;
  }

  /**
   * @param graphicsPanel the graphicsPanel to set
   */
  public void setGraphicsPanel(GraphicsPanel graphicsPanel) {
    this.graphicsPanel = graphicsPanel;
  }

  public ProjectTreeNode getRootNode() {
    return clappEditor.getRootNode();
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

  /**
   * @return the isCodeDirty
   */
  public boolean isCodeDirty() {
    return isCodeDirty;
  }

  /**
   * @param isCodeDirty the isCodeDirty to set
   */
  public void setCodeDirty(boolean isCodeDirty) {
    this.isCodeDirty = isCodeDirty;
    if (isCodeDirty) {
      setDirty();
    }
  }

  public void registerStartButton(JToggleButton tb) {
    startButton = tb;
  }

  public void setupOutput() {
    clappPanel.setup(isDesignTime(), isStartEnabled);
  }
}
