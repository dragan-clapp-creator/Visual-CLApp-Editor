package clp.edit.graphics.shapes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.tree.TreeNode;

import clp.edit.GeneralContext;
import clp.edit.graphics.panel.ControlsContainer;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.ActorTreeNode;
import clp.edit.tree.node.DisplayActorTreeNode;
import clp.edit.tree.node.DisplayHeapTreeNode;
import clp.edit.tree.node.DisplayMetaScenarioTreeNode;
import clp.edit.tree.node.DisplayResourcesTreeNode;
import clp.edit.tree.node.DisplayScenarioTreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.HeapTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ResourcesTreeNode;
import clp.edit.tree.node.ScenarioTreeNode;
import clp.parse.CLAppParser;
import clp.parse.scn.ScnParser;
import clp.run.act.Actor;
import clp.run.cel.Heap;
import clp.run.msc.MetaScenario;
import clp.run.msc.MetaScenarioBody;
import clp.run.msc.MscTaskName;
import clp.run.msc.MscTasks;
import clp.run.res.Resources;
import clp.run.scn.Scenario;

public class ContainerHelper implements Serializable {

  private static final long serialVersionUID = -695404666949388182L;

  public static final String resName = "FLOW_RES";

  private ATreeNode lastNode;

  private int activity;

  /**
   * CONSTRUCTOR
   * 
   * @param count
   * @param activity
   */
  public ContainerHelper(int count, int activity) {
    this.activity = activity;
  }

  /**
   * create all necessary tree nodes related to given container shape
   * 
   * @param c_shape
   * @param sname
   * @param hname
   */
  public void createTreeNodes(AContainerShape c_shape, String sname, String hname) {
    String aname = getActorName(c_shape.getName());
    ProjectTreeNode rootNode = GeneralContext.getInstance().getRootNode();

    MetaScenarioTreeNode mnode = getMetaScenario(rootNode);
    MetaScenario msc = (MetaScenario) mnode.getAssociatedObject();
    File ref = new File("");

    ResourcesTreeNode rnode = rootNode.findResources(resName);
    if (rnode == null) {
      ArrayList<Resources> rlist = msc.getMetaScenarioBody().getResourcess();
      if (rlist == null) {
        rlist = new ArrayList<>();
        msc.getMetaScenarioBody().setResourcess(rlist);
      }
      for (int i=0; i<rootNode.getChildCount(); i++) {
        FileTreeNode fnode = (FileTreeNode) rootNode.getChildAt(i);
        if (fnode.isGraphics()) {
          Resources r = new Resources();
          r.setName(resName);
          r.setMsc(mnode.getName());
          r.setMetaScenario(msc);
          rlist.add(r);
          rnode = new DisplayResourcesTreeNode(resName, r, ref, null);
          mnode.add(rnode);
        }
      }
    }

    Actor act = new Actor();
    act.setName(aname);
    Scenario scn = null;
    ScenarioTreeNode snode = rootNode.findScenario(sname);
    if (snode == null) {
      for (int i=0; i<rootNode.getChildCount(); i++) {
        FileTreeNode fnode = (FileTreeNode) rootNode.getChildAt(i);
        if (fnode.isGraphics()) {
          StringBuilder sb = c_shape.getSource(mnode.getName());
          InputStream is = new ByteArrayInputStream(sb.toString().getBytes(), 0, sb.length());
          CLAppParser parser = new CLAppParser(new BufferedReader(new InputStreamReader(is)));
          try {
            ScnParser pscn = new ScnParser();
            pscn.parse(parser, true);
            scn = pscn.getScenario();
            scn.setMetaScenario(msc);
            act.setScenario(scn);
            msc.getMetaScenarioBody().addScenario(scn);
            snode = new DisplayScenarioTreeNode(sname, scn, ref, null);
            mnode.add(snode);
         }
          catch (IOException e) {
            e.printStackTrace();
          }
          break;
        }
      }
    }
    else {
      scn = (Scenario) snode.getAssociatedObject();
      act.setScenario(scn);
    }
    scn.getScenarioBody().addActor(act);

    Heap h1 = new Heap();
    h1.setName(hname+"_1");
    h1.setRes(resName);
    h1.setActivity(activity);
    h1.setLoad("qa");
    h1.setActor(act);
    Heap h0 = new Heap();
    h0.setName(hname+"_0");
    h0.setRes(resName);
    h0.setLoad("qi");
    h0.setActor(act);
    act.addHeap(h0);
    act.addHeap(h1);
    
    ActorTreeNode anode = new DisplayActorTreeNode(aname, act, ref, null);
    snode.add(anode);
    HeapTreeNode heap1 = new DisplayHeapTreeNode(hname+"_1", h1, ref, null);
    HeapTreeNode heap0 = new DisplayHeapTreeNode(hname+"_0", h0, ref, null);
    anode.add(heap1);
    anode.add(heap0);
  }

  //
  private MetaScenarioTreeNode getMetaScenario(ProjectTreeNode root) {
    FileTreeNode fnode = null;
    for (int i=0; i<root.getChildCount(); i++) {
      fnode = (FileTreeNode) root.getChildAt(i);
      if (fnode.getChildCount() > 0) {
        TreeNode msc = fnode.getChildAt(0);
        if (msc instanceof MetaScenarioTreeNode) {
          return (MetaScenarioTreeNode) msc;
        }
      }
      else if (fnode.isGraphics()) {
        break;
      }
    }
    MetaScenario msc = new MetaScenario();
    msc.setName("MSC");
    MetaScenarioBody mb = new MetaScenarioBody();
    MscTasks mtasks = new MscTasks();
    mtasks.addMscTaskName(MscTaskName.SCHEDULER);
    mb.setMscTasks(mtasks);
    msc.setMetaScenarioBody(mb);
    MetaScenarioTreeNode mnode = new DisplayMetaScenarioTreeNode("MSC", msc, null, null);
    fnode.add(mnode);
    return mnode;
  }

  //
  private String getActorName(String text) {
    return text.replace(" ", "_");
  }

  /**
   * @return the lastNode
   */
  public ATreeNode getLastNode() {
    return lastNode;
  }

  /**
   * @param lastNode the lastNode to set
   */
  public void setLastNode(ATreeNode lastNode) {
    this.lastNode = lastNode;
  }

  /**
   * remove given actor and scenario if empty
   */
  public void removeActor(String scn_Name, String act_Name) {
    ProjectTreeNode rootNode = GeneralContext.getInstance().getRootNode();
    ScenarioTreeNode snode = rootNode.findScenario(scn_Name);
    if (snode != null) {
      String aname = getActorName(act_Name);
      for (int i=0; i<snode.getChildCount(); i++) {
        ATreeNode n = (ATreeNode) snode.getChildAt(i);
        if (n instanceof ActorTreeNode) {
          Actor act = (Actor)n.getAssociatedObject();
          if (aname.equals(act.getName())) {
            snode.remove(i);
            Scenario scn = (Scenario)snode.getAssociatedObject();
            scn.getScenarioBody().removeActor(act);
            break;
          }
        }
      }
      if (snode.isLeaf()) {
        for (int i=0; i<rootNode.getChildCount(); i++) {
          FileTreeNode node = (FileTreeNode) rootNode.getChildAt(i);
          for (int j=0; j<node.getChildCount(); j++) {
            ATreeNode n = (ATreeNode) node.getChildAt(j);
            if (n instanceof MetaScenarioTreeNode) {
              MetaScenarioTreeNode mnode = (MetaScenarioTreeNode)n;
              for (int k=0; k<mnode.getChildCount(); k++) {
                n = (ATreeNode) mnode.getChildAt(k);
                if (n == snode) {
                  mnode.remove(k);
                  break;
                }
              }
            }
          }
        }
      }
    }
  }

  public boolean rename(String oldName, String newName) {
    ProjectTreeNode rootNode = GeneralContext.getInstance().getRootNode();
    ActorTreeNode act1 = rootNode.findActor(newName);
    if (act1 == null) {
      ActorTreeNode act0 = rootNode.findActor(oldName);
      if (act0 != null) {
        act0.setName(newName);
        ((Actor)act0.getAssociatedObject()).setName(newName);
        ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
        String scnName = ((ScenarioTreeNode)act0.getParent()).getName();
        String oldKey = scnName + oldName;
        String newKey = scnName + newName;
        cc.getSourceHandler().reassignCellsInfoList(oldKey, newKey);
        return true;
      }
    }
    return false;
  }
}
