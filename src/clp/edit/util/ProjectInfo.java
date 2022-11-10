package clp.edit.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.tree.TreeNode;

import clapp.cmp.ClappMain;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.ActorTreeNode;
import clp.edit.tree.node.DisplayActorTreeNode;
import clp.edit.tree.node.DisplayHeapTreeNode;
import clp.edit.tree.node.DisplayMetaScenarioTreeNode;
import clp.edit.tree.node.DisplayResourcesTreeNode;
import clp.edit.tree.node.DisplayScenarioTreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.FlowTreeNode;
import clp.edit.tree.node.HeapTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ResourcesTreeNode;
import clp.edit.tree.node.ScenarioTreeNode;
import clp.edit.tree.node.SetterTreeNode;
import clp.edit.tree.node.util.SaveInfo;
import clp.run.CLApp;
import clp.run.act.Actor;
import clp.run.cel.Heap;
import clp.run.msc.MetaScenario;
import clp.run.res.Resources;
import clp.run.res.Setter;
import clp.run.scn.Scenario;

public class ProjectInfo implements Serializable {

  private static final long serialVersionUID = -7089452892790056311L;

  private static final String NAME = "name:";
  private static final String CONTENT = "content:";
  private static final String PATH = "path:";
  private static final String FLOW = "flow:";

  private String name;
  private String path;
  private String flow;
  private Hashtable<String, String> content;
  private ArrayList<ATreeNode> list;
  private FlowTreeNode graphic;
  private boolean isDirty;
  private String error;

  private boolean isInitialized;


  public ProjectInfo(File selectedFile, Boolean isFlowChart) {
    content = new Hashtable<>();
    list = new ArrayList<>();
    if (selectedFile == null) {
      // new project
      name = "<Project>";
      path = ".";
      if (isFlowChart) {
        flow = "NoName.flw";
        graphic = new FlowTreeNode(new File("./NoName.flw"), null);
      }
      else {
        list.add( new FileTreeNode(new File("./NoName.clp"), null) );
        content.put("NoName.clp", "");

      }
    }
    else {
      // existing project
      isInitialized = true;
      read(selectedFile);
    }
  }

  private void read(File selectedFile) {
    try {
      if (selectedFile.exists()) {
        String dir = selectedFile.getParentFile().getPath();
        FileInputStream fis = new FileInputStream(selectedFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        String line = in.readLine();
        while (line != null) {
          String[] st = line.split(NAME);
          if (st.length == 2) {
            name = st[1].trim();
          }
          st = line.split(PATH);
          if (st.length == 2) {
            path = st[1].trim();
          }
          st = line.split(FLOW);
          if (st.length == 2) {
            flow = st[1].trim();
            File reference = getFile(dir, flow);
            graphic = new FlowTreeNode(reference, null);
          }
          if (line.startsWith(CONTENT)) {
            line = in.readLine();
            while (line != null) {
              String ref = line.trim();
              if (!ref.isEmpty() && ref.endsWith(".clp")) {
                File reference = getFile(dir, ref);
                String source = readFile(reference);
                FileTreeNode sep = new FileTreeNode(reference, source);
                list.add(sep);
                if (!createNodes(source, reference, isGraphics())) {
                  sep.setColor(Color.red);
                  sep.setError(error);
                }
                content.put(ref, source);
              }
              line = in.readLine();
            }
          }
          else {
            line = in.readLine();
          }
        }
        in.close();
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  //
  private boolean createNodes(String source, File reference, boolean isGraphics) throws IOException {
    ClappMain clapp = new ClappMain();
    InputStream is = new ByteArrayInputStream(source.getBytes(), 0, source.length());
    String str = clapp.silentParse(is);
    if (str != null) {
      error = reference.getName()+" compilation ended: "+str;
      System.err.println(error);
      return false;
    }
    CLApp root = clapp.getResult();
    ATreeNode node;
    MetaScenario msc = root.getMetaScenario();
    if (msc != null) {
      node = isGraphics ?
            new DisplayMetaScenarioTreeNode(msc.getName(), msc, reference, source) :
            new MetaScenarioTreeNode(msc.getName(), msc, reference, source);
      list.add(node);
      ((MetaScenarioTreeNode)node).setClappMain( clapp );
    }
    ArrayList<Scenario> scnList = root.getScenarios();
    if (scnList != null) {
      for (Scenario scn : scnList) {
        node = isGraphics ?
            new DisplayScenarioTreeNode(scn.getName(), scn, reference, source) :
            new ScenarioTreeNode(scn.getName(), scn, reference, source);
        list.add(node);
      }
    }
    ArrayList<Resources> resList = root.getResourcess();
    if (resList != null) {
      for (Resources res : resList) {
        node = isGraphics ?
            new DisplayResourcesTreeNode(res.getName(), res, reference, source) :
            new ResourcesTreeNode(res.getName(), res, reference, source);
        list.add(node);
      }
    }
    ArrayList<Actor> actList = root.getActors();
    if (scnList != null) {
      for (Actor act : actList) {
        node = isGraphics ?
            new DisplayActorTreeNode(act.getName(), act, reference, source) :
            new ActorTreeNode(act.getName(), act, reference, source);
        list.add(node);
      }
    }
    ArrayList<Heap> heapList = root.getHeaps();
    if (scnList != null) {
      for (Heap heap : heapList) {
        node = isGraphics ?
            new DisplayHeapTreeNode(heap.getName(), heap, reference, source) :
            new HeapTreeNode(heap.getName(), heap, reference, source);
        list.add(node);
      }
    }
    ArrayList<Setter> setList = root.getSetters();
    if (scnList != null) {
      for (Setter setter : setList) {
        node = new SetterTreeNode(setter, setter.getRes(), reference, source);
        list.add(node);
      }
    }
    return true;
  }

  //
  private File getFile(String dir, String ref) {
    if (path == null) {
      path = "";
    }
    if (!path.endsWith("/")) {
      path += "/";
    }
    if (path.startsWith(".")) {
      path = path.replace(".", dir);
    }
    return new File(path + ref);
  }

  //
  private String readFile(File f) throws IOException {
    if (!f.exists()) {
      f.getParentFile().mkdirs();
      try {
        FileOutputStream out = new FileOutputStream(f);
        out.close();
        return "";
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
    StringBuilder sb = new StringBuilder();
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
    String line = br.readLine();
    while(line != null) {
      sb.append(line+"\n");
      line = br.readLine();
    }
    br.close();
    return sb.toString();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @return the graphic
   */
  public FileTreeNode getGraphic() {
    return graphic;
  }

  /**
   * @return the content
   */
  public Hashtable<String, String> getContent() {
    return content;
  }

  public void putContent(String name, String source) {
    content.put(name, source);
  }

  /**
   * @return the list
   */
  public ArrayList<ATreeNode> getList() {
    return list;
  }

  public void addToList(ATreeNode node) {
    list.add(node);
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
    isDirty = true;
    isInitialized = true;
    if (graphic != null) {
      renameGraphic(name+".flw");
    }
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
    isDirty = true;
  }

  /**
   * @param graphic the graphic to set
   */
  public void setGraphic(FlowTreeNode graphic) {
    this.graphic = graphic;
    isDirty = true;
  }

  public boolean isGraphics() {
    return graphic != null;
  }

  public SaveInfo retrievGraphics() {
    if (graphic != null) {
      return graphic.retrieveContainers(getPath());
    }
    return null;
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
  public synchronized void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

  public void renameGraphic(String text) {
    graphic.setName(text);
    flow = text;
  }

  public void checkProjectName(String name) {
    String n = name.substring(0, name.length()-4);
    if (!this.name.equals(n)) {
      setName(n);
    }
  }

  public String getSourceInfo() {
    StringBuffer sb = new StringBuffer();
    sb.append("name: ");
    sb.append(name + "\n");
    sb.append("path: ");
    sb.append(path + "\n");
    if (graphic != null) {
      sb.append("flow: ");
      sb.append(flow + "\n");
    }
    sb.append("content:\n");
    for (String key : content.keySet()) {
      sb.append("  " + key + "\n");
    }
    return sb.toString();
  }

  /**
   * @return the isInitialized
   */
  public synchronized boolean isInitialized() {
    return isInitialized;
  }

  public void updateGraphicsFile(FlowTreeNode flowTreeNode) {
    flow = flowTreeNode.getInfo().getName();
    graphic = flowTreeNode;
  }

  public void updateContents(ProjectTreeNode root) {
    for (int i=0; i<root.getChildCount(); i++) {
      TreeNode c = root.getChildAt(i);
      if (c instanceof FileTreeNode) {
        content.put(((FileTreeNode) c).getName(), getSource((FileTreeNode) c));
      }
    }
  }

  //
  private String getSource(FileTreeNode c) {
    String s = "";
    for (int i=0; i<c.getChildCount(); i++) {
      ATreeNode n = (ATreeNode) c.getChildAt(i);
      s += n.getUnassignedSource();
    }
    return new ClappMain().render( s ).toString();
  }
}
