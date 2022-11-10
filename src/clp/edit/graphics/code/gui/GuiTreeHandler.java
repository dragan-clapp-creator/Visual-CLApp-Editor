package clp.edit.graphics.code.gui;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import clp.edit.graphics.code.gui.elts.GUIRoot;
import clp.edit.graphics.code.gui.elts.GuiDialog;
import clp.run.res.ui.UiBundle;
import clp.run.res.ui.UiVar;

public class GuiTreeHandler implements Serializable {

  private static final long serialVersionUID = -4524718390501767884L;

  private JTree tree;
  private GUIRoot root;

  /**
   * CONSTRUCTOR
   */
  public GuiTreeHandler() {
    setup(null);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param ui root
   */
  public GuiTreeHandler(UiVar uiVar) {
    setup(uiVar);
  }

  /**
   * add mouse listener to the tree
   * @param dialog
   */
  public void addUITreeMouseListener(GuiDialog dialog) {
    GuiTreeMouseListener listener = new GuiTreeMouseListener(tree, dialog);
    tree.addTreeSelectionListener(listener);
    tree.addMouseListener(listener);
  }

  //
  private void setup(UiVar uiVar) {
    root = new GUIRoot();
    if (uiVar == null) {
      root.addNewGroup();
    }
    else {
      createNodes(uiVar);
//      root.addListeners();
    }
    tree = new JTree(root);
    MyRenderer renderer = new MyRenderer();
    renderer.setBorderSelectionColor(Color.magenta);
    renderer.setBackgroundSelectionColor(Color.orange);
    tree.setCellRenderer(renderer);
    refresh();
  }

  //
  private void createNodes(UiVar uiVar) {
    root.setName(uiVar.getName());
    if (uiVar.isTitle()) {
      root.setTitle(uiVar.getTitle());
    }
    ArrayList<UiBundle> bundles = new ArrayList<>();
    bundles.add(uiVar.getUiBundle());
    bundles.addAll(uiVar.getUiBundles());
    for (UiBundle bundle : bundles) {
      bundle.accept(new GuiBundleVisitor(root));
    }
  }

  //
  private void refresh() {
    DefaultTreeModel data = (DefaultTreeModel) tree.getModel();
    data.reload();
    DefaultMutableTreeNode node = getLastDeepestChild(root);
    TreePath path = new TreePath(data.getPathToRoot(node));
    tree.scrollPathToVisible(path);
  }

  public JTree getTree() {
    return tree;
  }

  private DefaultMutableTreeNode getLastDeepestChild(DefaultMutableTreeNode node) {
    for (int i=node.getChildCount()-1; i>=0; i--) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
      if (child.isLeaf()) {
        continue;
      }
      return getLastDeepestChild(child);
    }
    return node.getFirstLeaf();
  }

  //=========================================================================

  static public class MyRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -8241294887485801033L;

    public Component getTreeCellRendererComponent(JTree tree,
        Object value, boolean sel, boolean expanded, boolean leaf,
        int row, boolean hasFocus) {
      
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      setVisible(true);

      return this;
    }
  }

  /**
   * @return the root
   */
  public GUIRoot getRoot() {
    return root;
  }
}
