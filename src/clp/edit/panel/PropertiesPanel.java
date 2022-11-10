package clp.edit.panel;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.edit.tree.node.ATreeNode;

public class PropertiesPanel extends JPanel {

  private static final long serialVersionUID = -4989884486164939052L;

  public PropertiesPanel() {
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Properties Area"));
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
  }

  /**
   * set node's properties
   * 
   * @param node
   */
  public void setNodesProperties(ATreeNode node) {
    removeAll();
    Dimension dim = getSize();
    dim.width = 0;
    setPreferredSize(dim);
    updateUI();
    SwingUtilities.invokeLater( new Runnable() { 
      public void run() { 
        node.setProperties(PropertiesPanel.this);
        add(Box.createVerticalStrut(1000));
        GeneralContext.getInstance().updateClappPanel();
     } 
    } );
  }
}
