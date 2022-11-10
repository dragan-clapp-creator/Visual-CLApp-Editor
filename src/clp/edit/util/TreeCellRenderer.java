package clp.edit.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.FileTreeNode;

public class TreeCellRenderer extends DefaultTreeCellRenderer {

  private static final long serialVersionUID = 8989263415982632421L;

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
      boolean expanded, boolean leaf, int row, boolean hasFocus) {
    ATreeNode obj = (ATreeNode)value;
    setFont(obj.getFont());
    setToolTipText(obj.getToolTipText());
    if (obj instanceof FileTreeNode) {
      setBackgroundNonSelectionColor(((FileTreeNode) obj).getColor());
    }
    else {
      setBackgroundNonSelectionColor(obj.getBackground());
    }

    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    setVisible(true);

    setClosedIcon(null);
    setOpenIcon(null);
    setLeafIcon(null);
    setBackgroundSelectionColor(ColorSet.selectedBackground.getLight());
    setTextSelectionColor(Color.white);
    
    return this;
  }
}
