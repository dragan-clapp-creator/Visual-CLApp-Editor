package clp.edit.tree.node.util;

import java.util.Hashtable;

import clp.edit.handler.CLAppSourceHandler.CellsInfoList;
import clp.edit.panel.GraphicsPanel;
import clp.edit.tree.node.ProjectTreeNode;

public class SaveInfo {
  private ProjectTreeNode root;
  private GraphicsPanel graphicsPanel;
  private Hashtable<String, CellsInfoList> cellInfoList;

  /**
   * @return the root
   */
  public ProjectTreeNode getRoot() {
    return root;
  }
  /**
   * @param root the root to set
   */
  public void setRoot(ProjectTreeNode root) {
    this.root = root;
  }
  /**
   * @return the cellInfoList
   */
  public Hashtable<String, CellsInfoList> getCellInfoList() {
    return cellInfoList;
  }
  /**
   * @param cellInfoList the cellInfoList to set
   */
  public void setCellInfoList(Hashtable<String, CellsInfoList> cellInfoList) {
    this.cellInfoList = cellInfoList;
  }
  /**
   * @return the graphicsPanel
   */
  public synchronized GraphicsPanel getGraphicsPanel() {
    return graphicsPanel;
  }
  /**
   * @param graphicsPanel the graphicsPanel to set
   */
  public synchronized void setGraphicsPanel(GraphicsPanel graphicsPanel) {
    this.graphicsPanel = graphicsPanel;
  }
  public String getPath() {
    return root.getProjectInfo().getPath();
  }
}
