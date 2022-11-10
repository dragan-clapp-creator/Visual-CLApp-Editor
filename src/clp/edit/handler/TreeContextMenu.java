package clp.edit.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.CellTreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ResourcesTreeNode;
import clp.edit.tree.node.SetterTreeNode;

public class TreeContextMenu extends JPopupMenu {

  private static final long serialVersionUID = -3860271623951986961L;

  /**
   * constructor used for single selection
   * 
   * @param path
   * @param node
   */
  public TreeContextMenu(TreePath path, ATreeNode tnode) {
    setup(path, null, tnode, true);
  }

  /**
   * constructor used for multiple selection
   * 
   * @param paths
   */
  public TreeContextMenu(TreePath[] paths) {
    setup(paths[0], paths, (ATreeNode) paths[0].getLastPathComponent(), false);
  }

  //
  private void setup(TreePath path, TreePath[] paths, ATreeNode tnode, boolean isSingleSelection) {
    ATreeNode tparent = null;
    int n = path.getPathCount();
    if (n > 1) {
      tparent = (ATreeNode) path.getPathComponent(n - 2);
    }
    ArrayList<ATreeNode> selection = new ArrayList<>();
    if (isSingleSelection) {
      JMenu menu = tnode.addChildContextMenu();
      if (menu != null) {
        add(menu);
        if (!(tnode instanceof ProjectTreeNode)) {
          add(new Separator());
        }
      }
      menu = tnode.addWrapperContextMenu(tparent);
      if (menu != null) {
        add(menu);
        add(new Separator());
      }
      if (tnode instanceof ProjectTreeNode) {
        return;
      }
    }
    else {
      for (int i = 0; i < paths.length; i++) {
        path = paths[i];
        ATreeNode node = (ATreeNode) path.getLastPathComponent();
        selection.add(node);
      }
    }
    addMoveAndRemove(tnode, tparent, selection);
    add(new Separator());
    boolean isEmpty = PopupContext.getInstance().isListEmpty();
    createAndAddItemForCutPaste(tnode, "copy", Action.COPY, isEmpty);
    createAndAddItemForCutPaste(tnode, "cut", Action.CUT, isEmpty);
    createAndAddItemForCutPaste(tnode, "paste", Action.PASTE, !isEmpty);
  }

  //
  private void addMoveAndRemove(ATreeNode tnode, ATreeNode tparent, ArrayList<ATreeNode> selection) {
    PopupContext context = PopupContext.getInstance();
    context.setSelection(selection);
    int count = 0;
    ATreeNode previous = getPrevious(tnode);
    ATreeNode next = getNext(tnode);
    if (previous != null && previous.isWrapperForCandidate(tnode)) {
      count++;
      add(context.createAndAddItem(tnode, "move within previous node", Action.MOVE_UP_IN));
    }
    if (next != null && next.isWrapperForCandidate(tnode)) {
      count++;
      add(context.createAndAddItem(tnode, "move within next node", Action.MOVE_DOWN_IN));
    }
    if (previous != null && previous.hasWrapperForCandidate(tnode)) {
      count++;
      add(context.createAndAddItem(tnode, "move within previous node's child", Action.MOVE_UP_IN_OTHER));
    }
    if (next != null && next.hasWrapperForCandidate(tnode)) {
      count++;
      add(context.createAndAddItem(tnode, "move within next node's child", Action.MOVE_DOWN_IN_OTHER));
    }
    if (isWrapOutAllowed(tnode, tparent)) {
      count++;
      add(context.createAndAddItem(tnode, "move up out", Action.MOVE_UP_OUT));
    }
    if (isWrapOutAllowed(tnode, tparent)) {
      count++;
      add(context.createAndAddItem(tnode, "move down out", Action.MOVE_DOWN_OUT));
    }
    if (count > 0) {
      add(new Separator());
      count = 0;
    }
    if (hasParentPrevious(tnode)) {
      count++;
      add(context.createAndAddItem(tnode, "move up", Action.MOVE_UP));
    }
    if (hasParentNext(tnode, selection)) {
      count++;
      add(context.createAndAddItem(tnode, "move down", Action.MOVE_DOWN));
    }
    if (count > 0) {
      add(new Separator());
    }
    add(context.createAndAddItem(tnode, "delete node", Action.REMOVE));
  }

  //
  private boolean isWrapOutAllowed(ATreeNode candidate, ATreeNode parent) {
    return !(parent instanceof ProjectTreeNode)
        && !(parent instanceof FileTreeNode)
        && !(candidate instanceof CellTreeNode)
        && !(candidate instanceof SetterTreeNode)
        && !(candidate instanceof ResourcesTreeNode);
  }

  //
  private boolean hasParentPrevious(ATreeNode tparent) {
    TreeNode parent = tparent.getParent();
    if (parent != null) {
      return (parent.getIndex(tparent) > 0);
    }
    return false;
  }

  //
  private ATreeNode getPrevious(ATreeNode tparent) {
    TreeNode parent = tparent.getParent();
    if (parent != null) {
      int index = parent.getIndex(tparent);
      if (index > 0) {
        return (ATreeNode) parent.getChildAt(index - 1);
      }
    }
    return null;
  }

  //
  private boolean hasParentNext(ATreeNode tnode, ArrayList<ATreeNode> selection) {
    TreeNode parent = tnode.getParent();
    if (parent != null) {
      if (!selection.isEmpty()) {
        tnode = selection.get(selection.size()-1);
      }
      return (parent.getIndex(tnode) < parent.getChildCount() - 1);
    }
    return false;
  }

  //
  private ATreeNode getNext(ATreeNode tparent) {
    TreeNode parent = tparent.getParent();
    if (parent != null) {
      int index = parent.getIndex(tparent);
      if (index < parent.getChildCount() - 1) {
        return (ATreeNode) parent.getChildAt(index + 1);
      }
    }
    return null;
  }

  //
  private JComponent createAndAddItemForCutPaste(ATreeNode tnode, String name, Action act, boolean isEnabled) {
    JMenuItem item = new JMenuItem(name);
    item.setEnabled(isEnabled);
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (PopupContext.getInstance().performActionFromPopup(tnode, act)) {
          GeneralContext.getInstance().setDirty();
        }
      }
    });
    add(item);
    return item;
  }
}
