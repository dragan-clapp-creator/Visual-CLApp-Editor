package clp.edit.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.swing.tree.TreeNode;

import clp.edit.GeneralContext;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.ContainerHelper;
import clp.edit.graphics.shapes.util.CellInfo;
import clp.edit.tree.node.ActorTreeNode;
import clp.edit.tree.node.DisplayCellTreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.HeapTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ScenarioTreeNode;
import clp.parse.CLAppParser;
import clp.parse.act.UActParser;
import clp.run.act.Actor;
import clp.run.cel.Cell;
import clp.run.cel.Heap;
import clp.run.scn.Scenario;

public class TreeNodeInfo {
  
  private HeapTreeNode heapNode1;
  private HeapTreeNode heapNode0;
  private ActorTreeNode anode;
  private ScenarioTreeNode snode;

  private String aname;
  private AContainerShape containerShape;

  /**
   * CONSTRUCTOR
   * 
   * @param containerShape
   */
  public TreeNodeInfo(AContainerShape containerShape) {
    this.containerShape = containerShape;
    this.aname = containerShape.getActorName();
    ProjectTreeNode rootNode = GeneralContext.getInstance().getRootNode();
    retrieveContext(rootNode);
    heapNode0.removeAllChildren();
    heapNode1.removeAllChildren();
  }

  public ActorTreeNode getActor() {
    return anode;
  }

  public void setActor(ActorTreeNode anode) {
    this.anode = anode;
  }

  private void retrieveContext(ProjectTreeNode rootNode) {
    for (int i=0; i< rootNode.getChildCount(); i++) {
      FileTreeNode fnode = (FileTreeNode) rootNode.getChildAt(i);
      for (int j=0; j<fnode.getChildCount(); j++) {
        TreeNode mn = fnode.getChildAt(j);
        if (mn instanceof MetaScenarioTreeNode) {
          for (int k=0; k<mn.getChildCount(); k++) {
            TreeNode sn = mn.getChildAt(k);
            if (sn instanceof ScenarioTreeNode) {
              for (int l=0; l<sn.getChildCount(); l++) {
                ActorTreeNode an = (ActorTreeNode)sn.getChildAt(l);
                if (an.getName().equals(aname)) {
                  anode = an;
                  heapNode1 = (HeapTreeNode) anode.getChildAt(0);
                  heapNode0 = (HeapTreeNode) anode.getChildAt(1);
                  snode = (ScenarioTreeNode) sn;
                  return;
                }
              }
            } 
          }
        }
      }
    }
  }

  public String getActorName() {
    return aname;
  }

  public void generateCode(Hashtable<String, CellInfo> acells, Hashtable<String, CellInfo> icells) {
    StringBuilder buffer = new StringBuilder("actor " + aname
        + " assignTo " + containerShape.getScenarioName() + " {\n");

    buffer.append("  heap "+ containerShape.getHeapNamePrefix() + "_1 usedResources "
        + ContainerHelper.resName + " loadOn qa [" + containerShape.getHighLevel() + "] {\n");
    fillBuffer(buffer, acells.values());
    buffer.append("  }\n");

    buffer.append("  heap " + containerShape.getHeapNamePrefix() + "_0 usedResources "
        + ContainerHelper.resName + " loadOn qi {\n");
    fillBuffer(buffer, icells.values());
    buffer.append("  }\n");

    buffer.append("}\n");

    InputStream is = new ByteArrayInputStream(buffer.toString().getBytes(), 0, buffer.length());
    CLAppParser parser = new CLAppParser(new BufferedReader(new InputStreamReader(is)));
    try {
      UActParser pact = new UActParser();
      pact.parse(parser, false);

      Actor act = pact.getActor();
      ArrayList<Heap> heaps = act.getHeaps();
      for (Heap heap : heaps) {
        if (((Heap)heapNode1.getAssociatedObject()).getName().equals(heap.getName())) {
          addCells(heapNode1, heap.getCells(), acells);
        }
        else if (((Heap)heapNode0.getAssociatedObject()).getName().equals(heap.getName())) {
          addCells(heapNode0, heap.getCells(), icells);
        }
      }
      ArrayList<Actor> actors = ((Scenario)snode.getAssociatedObject()).getScenarioBody().getActors();
      for (Actor a : actors) {
        if (a.getName().equals(act.getName())) {
          actors.remove(a);
          break;
        }
      }
      actors.add(act);
   }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  //
  private void fillBuffer(StringBuilder buffer, Collection<CellInfo> collection) {
    for (CellInfo info : collection) {
      buffer.append("    cell " + info.getName() + " {\n");
      if (info.getAd() != null) {
        buffer.append(info.getAd());
      }
      if (info.getDd() != null) {
        buffer.append(info.getDd());
      }
      List<String> codes = info.getXd();
      if (codes != null) {
        buffer.append("      XD {\n");
        for (String c : codes) {
          if (!c.startsWith("1: send")) {
            buffer.append("         " + c + "\n");
          }
        }
        buffer.append("      }\n");
      }
      buffer.append("    }\n");
    }
  }

  //
  private void addCells(HeapTreeNode heapNode, ArrayList<Cell> cells, Hashtable<String, CellInfo> cellInfo) {
    for (Cell c : cells) {
      DisplayCellTreeNode cnode = new DisplayCellTreeNode(c.getName(), c);
      CellInfo info = cellInfo.get(c.getName());
      cnode.setAcondition(info.getAd());
      cnode.setDcondition(info.getDd());
      cnode.setStatements(info.getXd());
      heapNode.add(cnode);
    }
  }
}
