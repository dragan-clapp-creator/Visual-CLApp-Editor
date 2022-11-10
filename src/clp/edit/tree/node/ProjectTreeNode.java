package clp.edit.tree.node;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;

import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.MetaScenarioContent;
import clp.edit.util.ColorSet;
import clp.edit.util.FileInfo;
import clp.edit.util.ProjectInfo;
import clp.run.act.Actor;
import clp.run.cel.Cell;
import clp.run.cel.Heap;
import clp.run.msc.MetaScenario;
import clp.run.msc.MetaScenarioBody;
import clp.run.res.Resources;
import clp.run.scn.Scenario;
import clp.run.scn.ScenarioBody;

public class ProjectTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = 117554760421775239L;

  private static ProjectInfo projectInfo;

  private ColorSet dinfo;


  public ProjectTreeNode(ProjectInfo info) {
    super(info.getName(), null, null);
    projectInfo = info;
    ATreeNode fnode = null;
    for (ATreeNode node : info.getList()) {
      if (node instanceof FileTreeNode) {
        add(node);
        node.setParent(this);
        fnode = node;
      }
      else {
        fnode.add(node);
        node.setParent(fnode);
      }
      createChildren(node, projectInfo.isGraphics());
    }
    fnode = info.getGraphic();
    if (fnode != null) {
      add(fnode);
      fnode.setParent(this);
      createChildren(fnode, true);
    }
    dinfo = ColorSet.ProjectProperties;
  }

  //
  private void createChildren(ATreeNode node, boolean isGraphics) {
    Object obj = node.getAssociatedObject();
    if (obj instanceof MetaScenario) {
      MetaScenario msc = (MetaScenario) obj;
      MetaScenarioBody body = msc.getMetaScenarioBody();
      for (Resources res : body.getResourcess()) {
        res.setMetaScenario(msc);
        ResourcesTreeNode rnode = isGraphics ?
              new DisplayResourcesTreeNode(res.getName(), res, null, node.getSource()) :
              new ResourcesTreeNode(res.getName(), res, null, node.getSource());
        node.add(rnode);
        rnode.setParent(node);
      }
      for (Scenario scn : body.getScenarios()) {
        scn.setMetaScenario(msc);
        ScenarioTreeNode snode = isGraphics ?
              new DisplayScenarioTreeNode(scn.getName(), scn, null, node.getSource()) :
              new ScenarioTreeNode(scn.getName(), scn, null, node.getSource());
        node.add(snode);
        snode.setParent(node);
        createChildren(snode, isGraphics);
      }
    }
    else if (obj instanceof Scenario) {
      Scenario scn = (Scenario) obj;
      ScenarioBody body = scn.getScenarioBody();
      for (Actor act : body.getActors()) {
        act.setScenario(scn);
        ActorTreeNode anode = isGraphics ?
              new DisplayActorTreeNode(act.getName(), act, null, node.getSource()) :
              new ActorTreeNode(act.getName(), act, null, node.getSource());
        node.add(anode);
        anode.setParent(node);
        createChildren(anode, isGraphics);
      }
    }
    else if (obj instanceof Actor) {
      Actor act = (Actor) obj;
      for (Heap heap : act.getHeaps()) {
        heap.setActor(act);
        HeapTreeNode hnode = isGraphics ?
              new DisplayHeapTreeNode(heap.getName(), heap, null, node.getSource()) :
              new HeapTreeNode(heap.getName(), heap, null, node.getSource());
        node.add(hnode);
        hnode.setParent(node);
        createChildren(hnode, isGraphics);
      }
    }
    else if (obj instanceof Heap) {
      for (Cell cell : ((Heap) obj).getCells()) {
        CellTreeNode cnode = isGraphics ?
              new DisplayCellTreeNode(cell.getName(), cell) :
              new CellTreeNode(cell.getName(), cell);
        node.add(cnode);
        cnode.setParent(node);
      }
    }
  }

  /**
   * @return list of resources names blocks
   */
  public ArrayList<String> createResourceNamesList() {
    ArrayList<String> list = new ArrayList<>();
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode fnode = (FileTreeNode) getChildAt(i);
      for (int j=0; j<fnode.getChildCount(); j++) {
        ATreeNode node = (ATreeNode) fnode.getChildAt(j);
        if (node instanceof ResourcesTreeNode) {
          if (!list.contains(node.getName())) {
            list.add(node.getName());
          }
        }
        else if (node instanceof MetaScenarioTreeNode) {
          for (int k=0; k<node.getChildCount(); k++) {
            ATreeNode n = (ATreeNode) node.getChildAt(k);
            if (n instanceof ResourcesTreeNode) {
              if (!list.contains(n.getName())) {
                list.add(n.getName());
              }
            }
          }
        }
      }
    }
    return list;
  }

  /**
   * gather all scenarios under graphical {@link FileTreeNode}
   * 
   * @return
   */
  public ArrayList<Scenario> getGraphicalScenarios() {
    ArrayList<Scenario> lscn = new ArrayList<>();
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      if (node.isGraphics()) {
        for (int j=0; j<node.getChildCount(); j++) {
          TreeNode snode = node.getChildAt(j);
          if (snode instanceof ScenarioTreeNode) {
            lscn.add((Scenario) ((ScenarioTreeNode)snode).getAssociatedObject());
          }
        }
        break;
      }
    }
    return lscn;
  }

  /**
   * find scenario {@link ScenarioTreeNode} associated to the given scenario name
   * 
   * @param scn
   * @return
   */
  public ScenarioTreeNode findScenario(String scn) {
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      ScenarioTreeNode snode = findScenario(node, scn);
      if (snode != null) {
        return snode;
      }
    }
    return null;
  }

  public ArrayList<ScenarioTreeNode> findAllScenarios() {
    ArrayList<ScenarioTreeNode> scnList = new ArrayList<>();
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      for (int j=0; j<node.getChildCount(); j++) {
        ATreeNode n = (ATreeNode) node.getChildAt(j);
        if (n instanceof ScenarioTreeNode) {
          scnList.add((ScenarioTreeNode) n);
        }
        else if (n instanceof MetaScenarioTreeNode) {
          for (int k=0; k<n.getChildCount(); k++) {
            ATreeNode rnode = (ATreeNode) n.getChildAt(k);
            if (rnode instanceof ScenarioTreeNode) {
              scnList.add((ScenarioTreeNode) rnode);
            }
          }
        }
      }
    }
    return scnList;
  }

  //
  private ScenarioTreeNode findScenario(FileTreeNode node, String scn) {
    for (int i=0; i<node.getChildCount(); i++) {
      ATreeNode n = (ATreeNode) node.getChildAt(i);
      if (n instanceof ScenarioTreeNode
          && scn.equals(((Scenario)n.getAssociatedObject()).getName())) {
        return (ScenarioTreeNode) n;
      }
      else if (n instanceof MetaScenarioTreeNode) {
        for (int j=0; j<n.getChildCount(); j++) {
          ATreeNode snode = (ATreeNode) n.getChildAt(j);
          if (snode instanceof ScenarioTreeNode
              && scn.equals(((Scenario)snode.getAssociatedObject()).getName())) {
            return (ScenarioTreeNode) snode;
          }
        }
      }
    }
    return null;
  }

  /**
   * find meta-scenario {@link MetaScenarioTreeNode} as clapp root
   * 
   * @return
   */
  public MetaScenarioTreeNode findMetaScenario() {
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      for (int j=0; j<node.getChildCount(); j++) {
        ATreeNode n = (ATreeNode) node.getChildAt(j);
        if (n instanceof MetaScenarioTreeNode) {
          return (MetaScenarioTreeNode) n;
        }
      }
    }
    return null;
  }

  /**
   * find resources {@link ResourcesTreeNode} associated to the given resources name
   * 
   * @param res
   * @return
   */
  public ResourcesTreeNode findResources(String res) {
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      ResourcesTreeNode rnode = findResources(node, res);
      if (rnode != null) {
        return rnode;
      }
    }
    return null;
  }

  public ArrayList<ResourcesTreeNode> findAllResources() {
    ArrayList<ResourcesTreeNode> resList = new ArrayList<>();
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      for (int j=0; j<node.getChildCount(); j++) {
        ATreeNode n = (ATreeNode) node.getChildAt(j);
        if (n instanceof ResourcesTreeNode) {
          resList.add((ResourcesTreeNode) n);
        }
        else if (n instanceof MetaScenarioTreeNode) {
          for (int k=0; k<n.getChildCount(); k++) {
            ATreeNode rnode = (ATreeNode) n.getChildAt(k);
            if (rnode instanceof ResourcesTreeNode) {
              resList.add((ResourcesTreeNode) rnode);
            }
          }
        }
      }
    }
    return resList;
  }

  //
  private ResourcesTreeNode findResources(FileTreeNode node, String res) {
    for (int i=0; i<node.getChildCount(); i++) {
      ATreeNode n = (ATreeNode) node.getChildAt(i);
      if (n instanceof ResourcesTreeNode
          && res.equals(((Resources)n.getAssociatedObject()).getName())) {
        return (ResourcesTreeNode) n;
      }
      else if (n instanceof MetaScenarioTreeNode) {
        for (int j=0; j<n.getChildCount(); j++) {
          ATreeNode rnode = (ATreeNode) n.getChildAt(j);
          if (rnode instanceof ResourcesTreeNode
              && res.equals(((Resources)rnode.getAssociatedObject()).getName())) {
            return (ResourcesTreeNode) rnode;
          }
        }
      }
    }
    return null;
  }

  /**
   * find scenario {@link ScenarioTreeNode} as parent of the given actor node
   * 
   * @param anode
   * @return
   */
  public ScenarioTreeNode findScenario(ActorTreeNode anode) {
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      ScenarioTreeNode snode = findScenario(node, anode);
      if (snode != null) {
        return snode;
      }
    }
    return null;
  }

  //
  private ScenarioTreeNode findScenario(FileTreeNode node, ActorTreeNode anode) {
    for (int i=0; i<node.getChildCount(); i++) {
      ATreeNode n = (ATreeNode) node.getChildAt(i);
      if (n instanceof ScenarioTreeNode) {
        if (n.isNodeChild(anode)) {
          return (ScenarioTreeNode) n;
        }
      }
      else if (n instanceof MetaScenarioTreeNode) {
        for (int j=0; j<n.getChildCount(); j++) {
          ATreeNode snode = (ATreeNode) n.getChildAt(j);
          if (snode instanceof ScenarioTreeNode) {
            if (node.isNodeChild(anode)) {
              return (ScenarioTreeNode) snode;
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * find actor {@link ActorTreeNode} associated to the given actor name
   * 
   * @param act
   * @return
   */
  public ActorTreeNode findActor(String act) {
    for (int i=0; i<getChildCount(); i++) {
      FileTreeNode node = (FileTreeNode) getChildAt(i);
      ActorTreeNode anode = findActor(node, act);
      if (anode != null) {
        return anode;
      }
    }
    return null;
  }

  //
  private ActorTreeNode findActor(FileTreeNode node, String act) {
    for (int i=0; i<node.getChildCount(); i++) {
      ATreeNode n = (ATreeNode) node.getChildAt(i);
      if (n instanceof ActorTreeNode
          && act.equals(((Actor)n.getAssociatedObject()).getName())) {
        return (ActorTreeNode) n;
      }
      else if (n instanceof MetaScenarioTreeNode) {
        for (int j=0; j<n.getChildCount(); j++) {
          ATreeNode snode = (ATreeNode) n.getChildAt(j);
          if (snode instanceof ScenarioTreeNode) {
            ActorTreeNode anode = findInScenario(snode, act);
            if (anode != null ) {
              return anode;
            }
          }
        }
      }
      else if (n instanceof ScenarioTreeNode) {
        ActorTreeNode anode = findInScenario(n, act);
        if (anode != null ) {
          return anode;
        }
      }
    }
    return null;
  }

  //
  private ActorTreeNode findInScenario(ATreeNode snode, String act) {
    for (int k=0; k<snode.getChildCount(); k++) {
      ATreeNode anode = (ATreeNode) snode.getChildAt(k);
      if (anode instanceof ActorTreeNode
          && act.equals(((Actor)anode.getAssociatedObject()).getName())) {
        return (ActorTreeNode) anode;
      }
    }
    return null;
  }

  @Override
  public Object getAssociatedObject() {
    return projectInfo;
  }

  @Override
  public void setProperties(JPanel panel) {
    JPanel jp = new JPanel();
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 0;
    c.gridx = 0;
    jp.add(new JLabel("Project Name: "), c);
    c.gridx = 1;
    jp.add( createField("name", projectInfo.getName()), c );
    c.gridx = 2;
    jp.add(Box.createHorizontalStrut(200), c);

    c.gridy++;
    c.gridx = 0;
    jp.add(new JLabel("Path: "), c);
    c.gridx = 1;
    jp.add( createField("path", projectInfo.getPath()), c );
    c.gridx = 2;
    jp.add(Box.createHorizontalStrut(200), c);

    if (projectInfo.getGraphic() != null) {
      c.gridy++;
      c.gridx = 0;
      jp.add(new JLabel("Graphic: "), c);
      c.gridx = 1;
      jp.add( createField("graphic", projectInfo.getGraphic().getName()), c );
      c.gridx = 2;
      jp.add(Box.createHorizontalStrut(200), c);
    }
    panel.add(jp);
  }

  //
  private String getFileName(int i) {
    FileTreeNode child = (FileTreeNode) getChildAt(i);
    return child.getName();
  }

  //
  private JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(50);
    field.setBackground(dinfo.getLight());
    field.setText(text);
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  @Override
  public String getIcon() {
    return null;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Project "+getName();
  }

  @Override
  public IContent getContent() {
    return null;
  }

  @Override
  public JMenu addChildContextMenu() {
    JMenu menu = new JMenu("add");
    menu.add(PopupContext.getInstance().createSubItem(this, "a CLApp file", Action.INSERT, Argument.FILE));
    return menu;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    return null;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return candidate instanceof FileTreeNode;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
    if (e.getSource() instanceof JTextArea) {
      updateArea((JTextArea) e.getSource());
    }
    else {
      updateField((JTextField) e.getSource());
    }
  }

  //
  private void updateArea(JTextArea area) {
    ArrayList<String> files = new ArrayList<>();
    for (int i=0; i<getChildCount(); i++) {
      files.add(getFileName(i));
    }
    String[] text = area.getText().split("\n");
    for (String line : text) {
      if (line.contains(".")) {
        if (!line.contains(".clp")) {
          System.err.println("wrong file extention at " + line + "; please change to '.clp'");
        }
      }
      else {
        line += ".clp";
      }
      if (!files.remove(line)) {
        FileTreeNode node = new FileTreeNode(new File(projectInfo.getPath()+line), null);
        projectInfo.addToList(node);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ProjectTreeNode.this.add(node);
            node.setParent(ProjectTreeNode.this);
            GeneralContext.getInstance().recreateMainTree();
          }
        });
      }
    }
    if (!files.isEmpty()) {
      System.out.println("WARNING: are you sure you want to delete the following?");
      for (String file : files) {
        System.out.println(file);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateField((JTextField) e.getSource());
  }

  //
  private void updateField(JTextField field) {
    switch (field.getName()) {
      case "name":
        rename(field.getText());
        GeneralContext.getInstance().getPropertiesPanel().setNodesProperties(this);
        GeneralContext.getInstance().getClappPanel().refresh();
        break;
      case "path":
        projectInfo.setPath(field.getText());
        break;
      case "graphic":
        projectInfo.renameGraphic(field.getText());
        break;

      default:
        break;
    }
  }

  public void rename(String newname) {
    projectInfo.setName(newname);
    setName(newname);
  }

  public void renameFile(FileInfo fileInfo, String newname) {
    if (!newname.equals(fileInfo.getName())) {
      newname = getClappName(newname);
      File newfile = new File(fileInfo.getFile().getParentFile().getAbsolutePath()+File.separator+newname);
      if (fileInfo.isGraphic() && !projectInfo.isInitialized()) {
        projectInfo.renameGraphic(newfile.getName());
      }
      else {
        Hashtable<String, String> content = projectInfo.getContent();
        String source = content.remove(fileInfo.getName());
        if (source == null) {
          projectInfo.putContent(newname, "");
        }
        else {
          projectInfo.putContent(newname, source);
        }
      }
      for (int i=0; i<getChildCount(); i++) {
        FileTreeNode node = (FileTreeNode) getChildAt(i);
        if (node.getName().equals(fileInfo.getName())) {
          node.setName(newname);
          node.setFileReference(newfile);
          projectInfo.setDirty(true);
          break;
        }
      }
      fileInfo.setFile(newfile);
      fileInfo.setName(newname);
    }
  }

  //
  private String getClappName(String newname) {
    if (newname.contains(".")) {
      return newname;
    }
    return newname + ".clp";
  }

  public void insertNodeInfo(ATreeNode node, Argument arg) {
    if (arg == Argument.FILE) {
      projectInfo.putContent(node.getName(), "");
    }
    else {
      projectInfo.setGraphic((FlowTreeNode) node);
    }
  }

  @Override
  public String getSource() {
    return projectInfo.getSourceInfo();
  }

  @Override
  public String getUnassignedSource() {
    return null;
  }

  public List<FileTreeNode> getFileTreeNodes() {
    List<FileTreeNode> nodes = new ArrayList<>();
    for (int i=0; i<getChildCount(); i++) {
      nodes.add((FileTreeNode) getChildAt(i));
    }
    return nodes;
  }

  public ProjectInfo getProjectInfo() {
    return projectInfo;
  }

  public String getFullCrypter() {
    MetaScenarioTreeNode msc = findMetaScenario();
    if (msc != null) {
      MetaScenarioContent content = (MetaScenarioContent)msc.getContent();
      String dpack = content.getDpack();
      if (dpack != null) {
        return content.getDpath() + "/" + dpack + "." + content.getDclass();
      }
    }
    return " ";
  }
}
