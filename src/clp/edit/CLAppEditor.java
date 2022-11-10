package clp.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeNode;

import clapp.cmp.ClappMain;
import clapp.graph.DynamicCompiler;
import clp.edit.dialog.ExportDialog;
import clp.edit.exp.ExportActor;
import clp.edit.exp.ExportMetaScenrio;
import clp.edit.exp.ExportScenrio;
import clp.edit.graphics.code.prt.CslContext.CslInfo;
import clp.edit.graphics.code.web.WebContext.WebInfo;
import clp.edit.graphics.panel.SimulationHelper;
import clp.edit.graphics.panel.cntrl.ConditionButton;
import clp.edit.graphics.panel.cntrl.DelayButton;
import clp.edit.graphics.panel.cntrl.EventButton;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.RedBindingShape;
import clp.edit.handler.MenuHandler;
import clp.edit.panel.ClappPanel;
import clp.edit.panel.GraphicsPanel;
import clp.edit.panel.SourcePanel;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.ActorTreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.FlowTreeNode;
import clp.edit.tree.node.HeapTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ResourcesTreeNode;
import clp.edit.tree.node.ScenarioTreeNode;
import clp.edit.tree.node.util.SaveInfo;
import clp.edit.util.ContentReader;
import clp.edit.util.FilesResolver;
import clp.edit.util.IPlaceHolderResolver;
import clp.edit.util.ButtonsResolver;
import clp.edit.util.ProjectInfo;
import clp.run.act.Actor;
import clp.run.cel.Heap;
import clp.run.msc.Output;
import clp.run.scn.Scenario;

public class CLAppEditor extends ComponentAdapter implements ActionListener {

  private File selectedDirectory = new File(".");
  private File selectedFile;
  private File clappLib;
  private File bcelLib;

  private MenuHandler menuhandler;

  private JPanel mainPanel;

  private JFrame application;
  private boolean isDirty;
  transient private ProjectInfo projectInfo;
  private ProjectTreeNode root;
  private GraphicsPanel graphicsPanel;
  private SimulationHelper simulator;

  private ClappPanel clappPanel;
  private JTabbedPane tabbedPane;
  private String cpseparator;

  public CLAppEditor() {
    initialize();
  }

  /**
   * MAIN
   * @param args
   */
  public static void main(String[] args) {
    new CLAppEditor();
  }

  private void initialize() {
    application = new JFrame("Visual CLApp Editor");
    application.addComponentListener(this);
    application.setSize(1250, 800);
    application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    menuhandler = new MenuHandler();
    menuhandler.initialize(this);

    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setPreferredSize(new Dimension(1250, 800));

    tabbedPane = new JTabbedPane();
    tabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        int index = tabbedPane.getSelectedIndex();
        if (index >= 0) {
          if (tabbedPane.getTitleAt(index).startsWith("CLApp")) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                if (simulator != null && simulator.isGloballyDirty()) {
                  simulator.generateCode();
                  GeneralContext.getInstance().recreateMainTree();
                }
              }
            });
          }
          else {
            projectInfo.updateContents(root);
            if (tabbedPane.getTitleAt(index).startsWith("Source")) {
              ((SourcePanel)tabbedPane.getComponentAt(index)).setup();
            }
          }
        }
      }
    });

    mainPanel.add(tabbedPane, BorderLayout.CENTER);
    application.add(menuhandler, BorderLayout.NORTH);
    application.add(mainPanel, BorderLayout.CENTER);

    GraphicsConfiguration conf = application.getGraphicsConfiguration();
    Rectangle screenRect = conf.getBounds();
    Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(conf);
    Dimension size = application.getSize();
    int centerWidth = screenRect.width < size.width ?
        screenRect.x : screenRect.x + screenRect.width / 2 - size.width / 2;
    int centerHeight = screenRect.height < size.height ?
        screenRect.y : screenRect.y + screenRect.height / 2 - size.height / 2;
    centerHeight = centerHeight < screenInsets.top ?
        screenInsets.top : centerHeight;
    application.setLocation(centerWidth, centerHeight);

    application.setVisible(true);
    GeneralContext.getInstance().setFrame(application);
  }

  public void actionPerformed(ActionEvent object) {
    try {
      menuhandler.perform(object.getSource());
    }
    catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }

  public File chooseFile(boolean isSave, boolean isSaveAs) {
    JFileChooser chooser = new JFileChooser(selectedDirectory);
    int result;
    if (isSave) {
      selectedFile = new File(projectInfo.getName());
      chooser.setFileFilter(new FileNameExtensionFilter("save whole project (with '.prj' as extension)", "prj"));
      if (!isSaveAs && !projectInfo.getName().startsWith("<")) {
        chooser.setSelectedFile(selectedFile);
      }
      result = chooser.showSaveDialog(mainPanel);
    }
    else {
      chooser.setFileFilter(new FileNameExtensionFilter("Choose a .prj file", "prj"));
      result = chooser.showOpenDialog(mainPanel);
    }
    if (result == JFileChooser.APPROVE_OPTION) {
      selectedFile = checkName(chooser.getSelectedFile());
      selectedDirectory = selectedFile.getParentFile();
      return selectedFile;
    }
    return null;
  }

  //
  private File checkName(File file) {
    String name = file.getAbsolutePath();
    if (!name.endsWith(".prj")) {
      if (name.endsWith(".flw")) {
        name = name.replace(".flw", "");
      }
      return new File(name+".prj");
    }
    return file;
  }

  /**
   * @param selectedFile the selectedFile to set
   */
  public void setSelectedFile(File selectedFile) {
    this.selectedFile = selectedFile;
    this.selectedDirectory = selectedFile.getParentFile();
  }

  public void saveContent(boolean isSaveAs) throws IOException {
    boolean isInitialized = projectInfo.isInitialized();
    if (!isSaveAs) {
      isSaveAs |= !isInitialized;
    }
    if (isSaveAs || isAnyDirty()) {
      File file = isSaveAs ? chooseFile(true, isSaveAs) : definedFile();
      if (file != null) {
        saveProject(file, isSaveAs);
        isDirty = false;
        projectInfo.setDirty(false);
        GraphicsPanel gpanel = GeneralContext.getInstance().getGraphicsPanel();
        if (gpanel != null) {
          List<AContainer> containers = gpanel.getShapesContainer().getContainers();
          simulator.resetContainersDirtyFlag(containers);
        }
        System.out.printf("clapp file %s saved\n", file.getName());
      }
    }
  }

  //
  private File definedFile() throws IOException {
    if (!".".equals(projectInfo.getPath()) &&
        !"/.".equals(projectInfo.getPath()) &&
        !selectedDirectory.getCanonicalPath().equals(projectInfo.getPath())) {
      File file = new File(projectInfo.getPath()+File.separator+root.getName()+".prj");
      setSelectedFile(file);
      return file;
    }
    return new File(selectedDirectory.getCanonicalPath()+File.separator+root.getName()+".prj");
  }

  /**
   * looks for all possible dirty places
   * @return
   */
  public boolean isAnyDirty() {
    GraphicsPanel gpanel = GeneralContext.getInstance().getGraphicsPanel();
    if (gpanel == null) {
      return isDirty();
    }
    List<AContainer> containers = gpanel.getShapesContainer().getContainers();
    return projectInfo.isDirty() || isDirty() ||
           !containers.isEmpty() && gpanel.getControlsContainer().getSimulationHelper().isAnyContainerDirty(containers);
  }

  //
  private void saveProject(File file, boolean isSaveAs) throws IOException {
    root.rename(file.getName().replace(".prj", ""));
    SaveInfo sinfo = new SaveInfo();
    sinfo.setRoot(root);
    if (graphicsPanel != null) {
      projectInfo.updateGraphicsFile((FlowTreeNode) root.getFileTreeNodes().get(0));
      sinfo.setGraphicsPanel(graphicsPanel);
      sinfo.setCellInfoList(graphicsPanel.getControlsContainer().getSourceHandler().getCellInfoList());
    }
    projectInfo.setPath(selectedDirectory.getAbsolutePath());
    FileOutputStream fos = new FileOutputStream(file);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
    out.write(projectInfo.getSourceInfo());
    out.close();
    fos.close();
    for (FileTreeNode fnode : root.getFileTreeNodes()) {
      fnode.save(sinfo);
    }
  }

  public JFrame getFrame() {
    return application;
  }

  /**
   * @return the isDirty
   */
  public boolean isDirty() {
    return isDirty;
  }

  /**
   * @param isDirty the isDirty to set
   */
  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  public void setup(File selectedFile, Boolean isFlowChart) {
    GeneralContext.getInstance().register(this);
    projectInfo = new ProjectInfo(selectedFile, isFlowChart);
    root = new ProjectTreeNode(projectInfo);
    if (isFlowChart == null) {
      isFlowChart = projectInfo.isGraphics();
    }
    if (isFlowChart) {
      setupFlowChart(selectedFile == null);
    }
    else {
      setupClapp();
    }
  }

  //
  private void setupFlowChart(boolean isNew) {
    tabbedPane.removeAll();
    
    if (isNew) {
      graphicsPanel = new GraphicsPanel();
      simulator = graphicsPanel.getSimulationHelper();
    }
    else {
      SaveInfo sinfo = projectInfo.retrievGraphics();
      root = sinfo.getRoot();
      projectInfo.setGraphic((FlowTreeNode) root.getChildAt(0));
      graphicsPanel = sinfo.getGraphicsPanel();
      GeneralContext.getInstance().setGraphicsPanel(graphicsPanel);
      graphicsPanel.recreateControlsContainerAndSetCellInfoList(sinfo.getCellInfoList());
      simulator = graphicsPanel.getSimulationHelper();
      graphicsPanel.getControlsContainer().getSourceHandler().resetAllCellMarks();
      graphicsPanel.recreateAll();
      List<AContainer> containers = graphicsPanel.getShapesContainer().getContainers();
      if (!containers.isEmpty()) {
        menuhandler.enableExport();
        for (AContainer container : containers) {
          RedBindingShape redShape = container.getRedShape();
          redShape.setAttachTo(null);
          redShape.setReady(false);
        }
      }
    }
    tabbedPane.add("Flow Charts", graphicsPanel);

    clappPanel = GeneralContext.getInstance().createClappPanel(root, false);
    tabbedPane.add("CLApp Structure", clappPanel);
  }

  //
  private void setupClapp() {
    tabbedPane.removeAll();
    
    GeneralContext.getInstance().setGraphicsPanel(null);
    simulator = null;

    clappPanel = GeneralContext.getInstance().createClappPanel(root, true);
    tabbedPane.add("CLApp Structure", clappPanel);

    SourcePanel sourcePanel = new SourcePanel(tabbedPane.getBounds(), projectInfo);
    tabbedPane.add("Source", sourcePanel);
    sourcePanel.setup();
 }

  public ProjectTreeNode getRootNode() {
    return root;
  }

  public void exportProject() throws IOException {
    if (clappLib == null) {
      initializeFromClassPath();
      if (clappLib == null || bcelLib == null) {
        System.err.println("SORRY: export cannot be performed because of uninitialized used libraries");
        return;
      }
    }
    if (!chooseForExport()) {
      return;
    }
    boolean isMacOs = System.getProperty("os.name").startsWith("Mac");
    cpseparator = isMacOs ? ":" : ";" ;
    if (simulator != null) {
      simulator.generateCode();
      ExportParams params = processExport(isMacOs,
                                          projectInfo.getGraphic(),
                                          simulator.isControlsEmpty(),
                                          getControlButtons(),
                                          graphicsPanel.getShapesContainer().getWebInfos());
      if (params != null) {
        ButtonsResolver.Builder builder = resolveFlowChartPlaceHolders(params.elist, params.controls);
        exportAll(isMacOs, params.elist, params.controls, builder);
      }
    }
    else {
      try {
        ArrayList<ResourcesTreeNode> resList = root.findAllResources();
        ExportParams params = processExport(isMacOs,
                                            (FileTreeNode)root.findMetaScenario().getParent(),
                                            isControlsEmpty(resList),
                                            getControlButtons(resList),
                                            getWebInfos(resList));
        if (params != null) {
          if (params.elist != null) {
            ButtonsResolver.Builder builder = resolveButtonsPlaceHolders(params.elist, params.controls);
            exportAll(isMacOs, params.elist, params.controls, builder);
          }
          else {
            FilesResolver.Builder builder = resolveFileNamesPlaceHolders(params.emsc, params.controls);
            exportAllWithSender(isMacOs, params.emsc, builder);
          }
        }
      }
      catch (NoSuchElementException e) {
        System.err.println("SORRY: export cannot be performed because no root node was found");
      }
    }
  }

  //
  private boolean isControlsEmpty(ArrayList<ResourcesTreeNode> resList) {
    if (!resList.isEmpty()) {
      for (ResourcesTreeNode res : resList) {
        if (res.containsControlVariables()) {
          return false;
        }
      }
    }
    return true;
  }

  //
  private Collection<WebInfo> getWebInfos(ArrayList<ResourcesTreeNode> resList) {
    ArrayList<WebInfo> webCollection = new ArrayList<>();
    if (!resList.isEmpty()) {
      for (ResourcesTreeNode res : resList) {
        webCollection.addAll(res.getWebInfos());
      }
    }
    return webCollection;
  }

  //
  private ButtonsResolver.Builder resolveFlowChartPlaceHolders(ArrayList<ExportMetaScenrio> elist, ArrayList<String> selectedButtons) {
    ButtonsResolver.Builder builder = new ButtonsResolver.Builder();
    List<AContainer> containers = graphicsPanel.getShapesContainer().getContainers();
    for (AContainer container : containers) {
      String text = container.getContainerShape().getDesc();
      if (text != null && !text.isBlank()) {
        builder.addMessage(text);
      }
      else {
        builder.addMessage("no content information");
      }
    }
    for (int i=containers.size(); i<elist.size(); i++) {
      builder.addMessage(elist.get(i).getName());
    }
    if (selectedButtons != null) {
      for (String name : selectedButtons) {
        builder.addButton(name);
      }
    }
    return builder;
  }

  //
  private ButtonsResolver.Builder resolveButtonsPlaceHolders(ArrayList<ExportMetaScenrio> elist, ArrayList<String> selectedButtons) {
    ButtonsResolver.Builder builder = new ButtonsResolver.Builder();
    ArrayList<ScenarioTreeNode> sncList = root.findAllScenarios();
    for (ScenarioTreeNode scn : sncList) {
      builder.addMessage(scn.getName());
    }
    for (int i=sncList.size(); i<elist.size(); i++) {
      builder.addMessage(elist.get(i).getName());
    }
    if (selectedButtons != null) {
      for (String name : selectedButtons) {
        builder.addButton(name);
      }
    }
    return builder;
  }

  //
  private FilesResolver.Builder resolveFileNamesPlaceHolders(ExportMetaScenrio emsc, ArrayList<String> names) {
    FilesResolver.Builder builder = new FilesResolver.Builder();
    builder.setMessage(emsc.getDescription());
    if (names != null) {
      for (String name : names) {
        builder.addFileName(name);
      }
    }
    return builder;
  }

  //
  private ExportParams processExport(boolean isMacOs, FileTreeNode fnode, boolean isControlsEmpty, ArrayList<String> controlButtons, Collection<WebInfo> webCollection) throws IOException {
    ExportMetaScenrio emsc = createExportNode(fnode);
    MetaScenarioTreeNode mnode = findMeta(fnode);
    if (isControlsEmpty && root.getChildCount() == 1) {
      if (simulator != null) {
        retreiveResources(emsc, mnode);
      }
      ArrayList<ExportMetaScenrio> elist = new ArrayList<>();
      elist.add(emsc);
      return new ExportParams(elist, null);
    }
    else {
      ArrayList<String> names = new ArrayList<>();
      ArrayList<ExportScenrio> eslist = new ArrayList<>();
      ArrayList<ExportActor> ealist = new ArrayList<>();
      for (int i=0; i<root.getChildCount(); i++) {
        ATreeNode n1 = (ATreeNode) root.getChildAt(i);
        if (n1 != fnode && n1 instanceof FileTreeNode) {
          names.add(n1.getName());
          for (int j=0; j<n1.getChildCount(); j++) {
            TreeNode n2 = n1.getChildAt(j);
            fillExportInfo(n2, emsc, eslist, ealist);
          }
        }
      }
      if (!names.isEmpty()) {
        return new ExportParams(emsc, names);
      }
      emsc.getScenarios().addAll(eslist);
    }
    ExportDialog dial = new ExportDialog(getFrame(), this, controlButtons, emsc, webCollection);
    dial.setVisible(true);
    if (dial.isOk()) {
      ArrayList<ExportMetaScenrio> elist = getExportsWithFullResources( dial.getExports(), mnode );
      ArrayList<String> buttons = dial.getSelectedButtons();
      return new ExportParams(elist, buttons);
    }
    return null;
  }

  /*
    if (!isFlow) {
    }
   */

  //
  private ArrayList<ExportMetaScenrio> getExportsWithFullResources(ArrayList<ExportMetaScenrio> exports, MetaScenarioTreeNode mnode) {
    for (ExportMetaScenrio emsc : exports) {
      retreiveResources(emsc, mnode);
    }
    return exports;
  }

  //
  private void retreiveResources(ExportMetaScenrio emsc, MetaScenarioTreeNode mnode) {
    for (int i=0; i<mnode.getChildCount(); i++) {
      TreeNode n = mnode.getChildAt(i);
      if (n instanceof ResourcesTreeNode) {
        emsc.setResources(((ResourcesTreeNode)n).getSource());
      }
    }
  }

  public boolean chooseForExport() {
    JFileChooser chooser = new JFileChooser(selectedDirectory.getParentFile());
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle("Select an export folder ...");
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setApproveButtonText("export");
    int result = chooser.showOpenDialog(mainPanel);
    if (result == JFileChooser.APPROVE_OPTION) {
      selectedDirectory = chooser.getSelectedFile();
      return true;
    }
    return false;
  }

  //
  private ExportMetaScenrio createExportNode(FileTreeNode fnode) {
    ExportMetaScenrio emsc = new ExportMetaScenrio();
    MetaScenarioTreeNode mnode = findMeta(fnode);
    emsc.setName(mnode.getName());
    if (fnode instanceof FlowTreeNode) {
      emsc.setParentName(fnode.getName().replace(".flw", ""));
      addDeclaredConsoles(emsc);
    }
    else {
      emsc.setParentName(fnode.getName().replace(".clp", ""));
    }
    emsc.setPort(mnode.getPort());
    emsc.setProperties(mnode.getMetaProperties());

    ArrayList<ExportScenrio> eslist = new ArrayList<>();
    ArrayList<ExportActor> ealist = new ArrayList<>();
    for (int i=0; i<fnode.getChildCount(); i++) {
      ATreeNode node = (ATreeNode) fnode.getChildAt(i);
      if (node == mnode) {
        fillLists(emsc, mnode, eslist, ealist);
      }
      else {
        Object obj = node.getAssociatedObject();
        if ((obj instanceof Scenario) && mnode.getName().equals(((Scenario)obj).getMsc())) {
          fillExportInfo(node, emsc, eslist, ealist);
        }
        else if ((obj instanceof Actor) && foundScenarioName(fnode, ((Actor)obj).getScn())) {
          fillExportInfo(node, emsc, eslist, ealist);
        }
        else if ((obj instanceof Heap) && foundActorName(fnode, ((Heap)obj).getAct())) {
          fillExportInfo(node, emsc, eslist, ealist);
        }
      }      
    }
    emsc.setScenarios(eslist);
    return emsc;
  }

  //
  private boolean foundScenarioName(FileTreeNode fnode, String scn) {
    for (int i=0; i<fnode.getChildCount(); i++) {
      TreeNode node = fnode.getChildAt(i);
      if (node instanceof ScenarioTreeNode) {
        return ((ScenarioTreeNode) node).getName().equals(scn);
      }
    }
    return false;
  }

  //
  private boolean foundActorName(FileTreeNode fnode, String act) {
    for (int i=0; i<fnode.getChildCount(); i++) {
      TreeNode node = fnode.getChildAt(i);
      if (node instanceof ActorTreeNode) {
        return ((ActorTreeNode) node).getName().equals(act);
      }
    }
    return false;
  }

  //
  private void fillLists(ExportMetaScenrio emsc, MetaScenarioTreeNode mnode, ArrayList<ExportScenrio> eslist, ArrayList<ExportActor> ealist) {
    int nbScn = 0;
    for (int i=0; i<mnode.getChildCount(); i++) {
      TreeNode n = mnode.getChildAt(i);
      if (n instanceof ScenarioTreeNode) {
        nbScn++;
      }
    }
    if (nbScn > 1) {
      for (int i=0; i<mnode.getChildCount(); i++) {
        TreeNode n = mnode.getChildAt(i);
        fillExportInfoAsIs(n, emsc, eslist);
      }
    }
    else {
      for (int i=0; i<mnode.getChildCount(); i++) {
        TreeNode n = mnode.getChildAt(i);
        fillExportInfo(n, emsc, eslist, ealist);
      }
    }
  }

  //
  private MetaScenarioTreeNode findMeta(FileTreeNode fnode) {
    for (int i=0; i<fnode.getChildCount(); i++) {
      TreeNode node = fnode.getChildAt(i);
      if (node instanceof MetaScenarioTreeNode) {
        return (MetaScenarioTreeNode) node;
      }
    }
    throw new NoSuchElementException();
  }

  //
  private void fillExportInfo(TreeNode n, ExportMetaScenrio emsc, ArrayList<ExportScenrio> eslist, ArrayList<ExportActor> ealist) {
    if (n instanceof ResourcesTreeNode) {
      emsc.setResources(((ResourcesTreeNode)n).getExportSource());
    }
    else if (n instanceof ScenarioTreeNode) {
      ScenarioTreeNode snode = (ScenarioTreeNode) n;
      ExportScenrio escn = new ExportScenrio();
      escn.setName(snode.getName());
      escn.setProperties(snode.getScenarioProperties());
       for (int j=0; j<snode.getChildCount(); j++) {
        n = snode.getChildAt(j);
        if (n instanceof ActorTreeNode) {
          ActorTreeNode anode = (ActorTreeNode) n;
          ExportActor eact = new ExportActor();
          eact.setName(anode.getName());
          eact.setSource(anode.getSource());
          ealist.add(eact);
        }
      }
      escn.setActors(ealist);
      eslist.add(escn);
    }
    else if (n instanceof ActorTreeNode) {
      ActorTreeNode anode = (ActorTreeNode) n;
      ExportActor eact = new ExportActor();
      eact.setName(anode.getName());
      eact.setSource(anode.getSource());
      ealist.add(eact);
    }
    else if (n instanceof HeapTreeNode) {
      HeapTreeNode hnode = (HeapTreeNode) n;
      ExportActor eact = findFromList(ealist, ((Heap)hnode.getAssociatedObject()).getAct());
      if (eact != null) {
        eact.setSource(hnode.getSource());
      }
    }
  }

  //
  private void fillExportInfoAsIs(TreeNode n, ExportMetaScenrio emsc, ArrayList<ExportScenrio> eslist) {
    ArrayList<ExportActor> ealist = new ArrayList<>();
    if (n instanceof ResourcesTreeNode) {
      emsc.setResources(((ResourcesTreeNode)n).getExportSource());
    }
    else if (n instanceof ScenarioTreeNode) {
      ScenarioTreeNode snode = (ScenarioTreeNode) n;
      ExportScenrio escn = new ExportScenrio();
      escn.setName(snode.getName());
      escn.setProperties(snode.getScenarioProperties());
       for (int j=0; j<snode.getChildCount(); j++) {
        n = snode.getChildAt(j);
        if (n instanceof ActorTreeNode) {
          ActorTreeNode anode = (ActorTreeNode) n;
          ExportActor eact = new ExportActor();
          eact.setName(anode.getName());
          eact.setSource(anode.getSource());
          ealist.add(eact);
        }
      }
      escn.setActors(ealist);
      eslist.add(escn);
    }
    else if (n instanceof ActorTreeNode) {
      ActorTreeNode anode = (ActorTreeNode) n;
      ExportActor eact = new ExportActor();
      eact.setName(anode.getName());
      eact.setSource(anode.getSource());
      ealist.add(eact);
    }
    else if (n instanceof HeapTreeNode) {
      HeapTreeNode hnode = (HeapTreeNode) n;
      ExportActor eact = findFromList(ealist, ((Heap)hnode.getAssociatedObject()).getAct());
      if (eact != null) {
        eact.setSource(hnode.getSource());
      }
    }
  }

  //
  private ExportActor findFromList(ArrayList<ExportActor> ealist, String act) {
    for (ExportActor ea : ealist) {
      if (act.equals(ea.getName())) {
        return ea;
      }
    }
    return null;
  }

  //
  private void addDeclaredConsoles(ExportMetaScenrio emsc) {
    ArrayList<Output> outputs = new ArrayList<Output>();
    Collection<CslInfo> infos = graphicsPanel.getShapesContainer().getCslInfos();
    for (CslInfo info : infos) {
      outputs.add(info.getOut());
    }
    emsc.setOutputs(outputs);
  }

  //
  private void initializeFromClassPath() throws IOException {
    String line = System.getProperty("java.class.path");
    String sep = System.getProperty("path.separator");
    setupFiles(line.split(sep));
  }

  //
  private void setupFiles(String[] sp) {
    for (String str : sp) {
      if (clappLib == null && str.toLowerCase().contains("clapp.jar")) {
        clappLib = new File(str);
      }
      if (bcelLib == null && str.toLowerCase().contains("bcel")) {
        bcelLib = new File(str);
      }
    }
  }

  //
  private void exportAll(boolean isMacOs, ArrayList<ExportMetaScenrio> elist, ArrayList<String> buttons, ButtonsResolver.Builder builder) throws IOException {
    File jstarter = null;

    // 0. initialize template names
    String cname;
    String cmd;
    String jname;
    if (isMacOs) {
      cname = "cstarter.sh";
      cmd = "bash;-c;\\\""+selectedDirectory.getAbsolutePath()+File.separator+"\\\"";
      jname = "starter.sh";
    }
    else {
      cname = "cstarter.bat";
      cmd = "cmd;/c;\\\""+selectedDirectory.getAbsolutePath()+File.separator+"\\\"";
      jname = "starter.bat";
    }
    String fullCrypter = root.getFullCrypter();
    String crypter = fullCrypter.isEmpty() ? "" : fullCrypter.substring(fullCrypter.lastIndexOf("/")+1);

    // 1. create java simple launcher with or without sender
    for (ExportMetaScenrio e : elist) {
      String oname = projectInfo.getName() + e.getName() + cname.substring(cname.indexOf('.'));
      String cmdl = cmd + oname;
      builder = builder.addStarter(cmdl);
      builder.addTitle(e.getName()+"/"+(e.getHost() == null ? "" : e.getHost())+"/"+e.getPort()+"/"+crypter);

      // 2. save CLApp sources
      File clpFile = new File(selectedDirectory.getAbsolutePath()+File.separator+projectInfo.getName()+e.getName()+".clp");
      FileOutputStream fos = new FileOutputStream(clpFile);
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
      String src = new ClappMain().render( e.getSource() ).toString();
      out.write(src);
      out.close();
      fos.close();
      System.out.printf("clapp file %s exported\n", clpFile.getName());

      // 3. create and save BASH or BAT to start java launcher, if needed
      if (jstarter == null && buttons != null && !buttons.isEmpty()) {
        jstarter = exportBatch(jname, jname, e.getParentName(), fullCrypter, false);
      }

      // 4. create and save BASH or BAT to start CLApp with generated source
      File cstarter = exportBatch(cname, oname, clpFile.getCanonicalPath(), "", true);

      if (isMacOs) {
        Runtime.getRuntime().exec("chmod u+x "+cstarter.getCanonicalPath());
      }
    }

    // 5. save java launcher if needed
    if (jstarter != null) {
      if (isMacOs) {
        Runtime.getRuntime().exec("chmod u+x "+jstarter.getCanonicalPath());
      }
      IPlaceHolderResolver pr = builder.build();
      deleteFiles(selectedDirectory.getAbsolutePath()+"/bin");
      if (createLauncher(pr)) {
        deleteFiles(selectedDirectory.getAbsolutePath()+"/src");
      }
    }
  }

  //
  private void exportAllWithSender(boolean isMacOs, ExportMetaScenrio e, FilesResolver.Builder builder) throws IOException {
    File jstarter = null;

    // 0. initialize template names
    String cname;
    String cmd;
    String jname;
    if (isMacOs) {
      cname = "cstarter.sh";
      cmd = "bash;-c;\\\""+selectedDirectory.getAbsolutePath()+File.separator+"\\\"";
      jname = "fstarter.sh";
    }
    else {
      cname = "cstarter.bat";
      cmd = "cmd;/c;\\\""+selectedDirectory.getAbsolutePath()+File.separator+"\\\"";
      jname = "fstarter.bat";
    }
    String fullCrypter = root.getFullCrypter();
    String crypter = fullCrypter.isBlank() ? " " : fullCrypter.substring(fullCrypter.lastIndexOf("/")+1);

    // 1. create java simple launcher with sender
    String oname = projectInfo.getName() + e.getName() + cname.substring(cname.indexOf('.'));
    String cmdl = cmd + oname;
    builder = builder.addStarter(cmdl);
    builder.setTitle(e.getName()+"/"+(e.getHost() == null ? "" : e.getHost())+"/"+e.getPort()+"/"+crypter);

    // 2. save CLApp sources
    File clpFile = new File(selectedDirectory.getAbsolutePath()+File.separator+projectInfo.getName()+e.getName()+".clp");
    FileOutputStream fos = new FileOutputStream(clpFile);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
    String src = new ClappMain().render( e.getSource() ).toString();
    out.write(src);
    out.close();
    fos.close();
    System.out.printf("clapp file %s exported\n", clpFile.getName());

    // 3. create and save BASH or BAT to start java launcher (files sender)
    jstarter = exportBatch(jname, jname, e.getParentName(), fullCrypter, false);

    // 4. create and save BASH or BAT to start CLApp with generated source
    File cstarter = exportBatch(cname, oname, clpFile.getCanonicalPath(), "", true);

    if (isMacOs) {
      Runtime.getRuntime().exec("chmod u+x "+cstarter.getCanonicalPath());
    }

    // 5. save java launcher (files sender)
    if (isMacOs) {
      Runtime.getRuntime().exec("chmod u+x "+jstarter.getCanonicalPath());
    }
    IPlaceHolderResolver pr = builder.build();
    deleteFiles(selectedDirectory.getAbsolutePath()+"/bin");
    if (createFileSender(pr)) {
      deleteFiles(selectedDirectory.getAbsolutePath()+"/src");
    }
  }

  //
  private void deleteFiles(String dirName) {
    File dir = new File(dirName);
    if (dir.exists()) {
      for (File f : dir.listFiles()) {
        if (f.isDirectory()) {
          deleteFiles(f.getAbsolutePath());
        }
        f.delete();
      }
      dir.delete();
    }
  }

  //
  private boolean createLauncher(IPlaceHolderResolver pr) throws IOException {
    String dir = selectedDirectory.getAbsolutePath();
    String src = dir+"/src/clapp/start";
    new File(src).mkdirs();
    createSource(src, "Launcher", pr);
    String dest = dir+"/bin";
    DynamicCompiler cmp = new DynamicCompiler();
    try {
      if (cmp.compileDynamically(src, dest, true)) {
        System.out.printf("file Launcher.class saved under %s\n", dest);
        return true;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  //
  private boolean createFileSender(IPlaceHolderResolver pr) throws IOException {
    String dir = selectedDirectory.getAbsolutePath();
    String src = dir+"/src/clapp/start";
    new File(src).mkdirs();
    createSource(src, "FileSender", pr);
    String dest = dir+"/bin";
    DynamicCompiler cmp = new DynamicCompiler();
    try {
      if (cmp.compileDynamically(src, dest, true)) {
        System.out.printf("file FileSender.class saved under %s\n", dest);
        return true;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  //
  private void createSource(String dir, String source, IPlaceHolderResolver pr) throws IOException {
    InputStream in = CLAppEditor.class.getClassLoader().getResourceAsStream(source+".txt");
    String content = new ContentReader(in).getContent().toString();
    if (pr != null) {
      content = pr.resolve( content );
    }
    File launcher = new File(dir+File.separator+source+".java");
    FileOutputStream fos = new FileOutputStream(launcher);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
    out.write(content);
    out.close();
    fos.write(in.readAllBytes());
    fos.close();
    System.out.printf("java file %s.java saved\n", source);
  }

  //
  private File exportBatch(String iname, String oname, String text, String crypter, boolean isForClapp) throws IOException {
    InputStream in = CLAppEditor.class.getClassLoader().getResourceAsStream(iname);
    File starter = new File(selectedDirectory.getAbsolutePath()+File.separator+oname);
    String content = adaptContent(in, text, crypter, isForClapp);
    FileOutputStream fos = new FileOutputStream(starter);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
    out.write(content);
    out.close();
    fos.write(in.readAllBytes());
    fos.close();
    System.out.printf("batch file %s exported\n", oname);
    return starter;
  }

  //
  private String adaptContent(InputStream in, String text, String crypter, boolean isForClapp) throws IOException {
    String content = new ContentReader(in).getContent().toString();
    if (isForClapp) {
      content = content.replace("<SRC>", text);
      content = content.replace("<CLIB>", clappLib.getCanonicalPath());
      content = content.replace("<BLIB>", bcelLib.getCanonicalPath());
    }
    else {
      content = content.replace("<TITLE>", text);
      content = content.replace("<BIN>", selectedDirectory.getCanonicalPath()+"/bin");
      if (crypter.isBlank()) {
        content = content.replace("<CRYPTER>", crypter);
        content = content.replace("<CLIB>", clappLib.getCanonicalPath());
      }
      else {
        int i = crypter.lastIndexOf("/");
        content = content.replace("<CRYPTER>", crypter.substring(i+1));
        content = content.replace("<CLIB>", clappLib.getCanonicalPath()+cpseparator+crypter.substring(0, i));
      }
    }
    return content;
  }

  public void enableExport() {
    menuhandler.enableExport();
  }

  public String getselectedDir() {
    return selectedDirectory.getAbsolutePath();
  }

  //
  private ArrayList<String> getControlButtons() {
    ArrayList<String> buttons = new ArrayList<>();
    for (EventButton eb : simulator.getEvents()) {
      if (!(eb instanceof DelayButton)) {
        buttons.add(eb.getText());
      }
    }
    for (ConditionButton cb : simulator.getConditions()) {
      buttons.add(cb.getText());
    }
    return buttons;
  }

  //
  private ArrayList<String> getControlButtons(ArrayList<ResourcesTreeNode> resList) {
    ArrayList<String> buttons = new ArrayList<>();
    for (ResourcesTreeNode res : resList) {
      buttons.addAll(res.getControlVariables());
    }
    return buttons;
  }

  //===========================================================================

  private class ExportParams {
    private ArrayList<ExportMetaScenrio> elist;
    private ArrayList<String> controls;
    private ExportMetaScenrio emsc;

    public ExportParams(ArrayList<ExportMetaScenrio> elist, ArrayList<String> buttons) {
      this.elist = elist;
      this.controls = buttons;
    }

    public ExportParams(ExportMetaScenrio emsc, ArrayList<String> names) {
      this.emsc = emsc;
      this.controls = names;
    }
    
  }
}