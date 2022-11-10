package clp.edit.handler;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import clp.edit.GeneralContext;
import clp.edit.panel.ControlPanel;
import clp.edit.panel.PropertiesPanel;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;

public class TreeMouseListener extends MouseAdapter implements TreeSelectionListener {

  private static Hashtable<String, TreeNode> nodes = new Hashtable<>();

  private JTree jtree;

  private TreeContextMenu cmenu;

  transient private GeneralContext context;

  /**
   * CONSTRUCTOR
   * 
   * @param tree
   */
  public TreeMouseListener(JTree tree) {
    this.jtree = tree;
    this.context = GeneralContext.getInstance();
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.getNewLeadSelectionPath() == null) {
      return;
    }
    ATreeNode node = (ATreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
    context.addProperties(node);

    MetaScenarioTreeNode mscNode = context.getMscNode();
    if (mscNode != null) {
      if (node instanceof MetaScenarioTreeNode || node instanceof ProjectTreeNode) {
        return;
      }
      while (!(node instanceof ProjectTreeNode) && node.getFileReference() == null) {
        node = (ATreeNode) node.getParent();
      }
      if (!(node instanceof ProjectTreeNode)) {
        context.addSendToControlPanel(node);
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (context.isDesignTime()) {
      Point p = e.getPoint();
      TreePath path = jtree.getClosestPathForLocation(p.x, p.y);
      ATreeNode tnode = (ATreeNode) path.getLastPathComponent();
      if (jtree.getSelectionCount() == 1) {
        if (tnode != null && path.equals(jtree.getSelectionPath())) {
          PropertiesPanel propertiesPanel = context.getPropertiesPanel();
          ControlPanel controlPanel = context.getControlPanel();
          if (!context.isGraphics() && SwingUtilities.isRightMouseButton(e)) {
            cmenu = new TreeContextMenu(path, tnode);
            cmenu.show(e.getComponent(), e.getX()+30, e.getY());
         }
          else if (e.getClickCount() == 1) {
            tnode.populate(propertiesPanel, controlPanel);
          }
          else if (e.getClickCount() == 2 && tnode instanceof MetaScenarioTreeNode) {
            String name = tnode.getName();
            TreePath paths = getPath(getNodePath(name));
            jtree.setSelectionPath(paths);
            path = jtree.getSelectionPath();
            if (path != null) {
              tnode = (ATreeNode) path.getLastPathComponent();
              if (tnode != null) {
                tnode.populate(propertiesPanel, controlPanel);
              }
            }
          }
          context.updateClappPanel();
        }
      }
      else if (!context.isGraphics() && jtree.getSelectionCount() > 1 && SwingUtilities.isRightMouseButton(e)) {
        TreePath[] paths = jtree.getSelectionPaths();
        cmenu = new TreeContextMenu(paths);
        cmenu.show(e.getComponent(), e.getX()+30, e.getY());
      }
    }
  }

  public TreeNode getNodePath(String n) {
    return nodes.get(n);
  }
  
  public TreePath getPath(TreeNode treeNode) {
    List<Object> nodes = new ArrayList<Object>();
    if (treeNode != null) {
      nodes.add(treeNode);
      treeNode = treeNode.getParent();
      while (treeNode != null) {
        nodes.add(0, treeNode);
        treeNode = treeNode.getParent();
      }
    }

    return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
  }
}
