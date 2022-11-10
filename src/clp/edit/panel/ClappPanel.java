package clp.edit.panel;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.edit.tree.node.ProjectTreeNode;

public class ClappPanel extends JPanel {

  private static final long serialVersionUID = -7763962730704114639L;

  private ProjectTreeNode root;

  private ControlPanel ctrlPanel;

  private JTree mainTree;

  private PropertiesPanel propsPanel;

  private JScrollPane outPanel;

  private GeneralContext context;

  public ClappPanel(ProjectTreeNode root, boolean isStartEnabled) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.root = root;
    context = GeneralContext.getInstance();
    mainTree = context.createMainTree(root);
    propsPanel = context.createPropertiesPanel();
    outPanel = context.createOutPanel();
    setup(true, isStartEnabled);
  }

  public JTree recreateMainTree() {
    mainTree = context.createMainTree(root);
    return mainTree;
  }

  public void redrawTreePanel() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        mainTree.updateUI();
      }
    });
  }

  public void setup(boolean isDesignTime, boolean isStartEnabled) {
    removeAll();
    JScrollPane treePanel = new JScrollPane();
    treePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    treePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    treePanel.getViewport().add(mainTree, BorderLayout.LINE_START);
    treePanel.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Project Structure"));
    if (ctrlPanel == null) {
      ctrlPanel = new ControlPanel(mainTree, root, propsPanel, outPanel, isStartEnabled);
    }
    else {
      ctrlPanel.setup(mainTree, root, outPanel, propsPanel, isStartEnabled);
    }
    add(ctrlPanel);
    JSplitPane sp2;
    if (isDesignTime) {
      sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, propsPanel);
    }
    else {
      sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, outPanel);
    }
    JSplitPane sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ctrlPanel, sp2);
    add(sp1);
    validate();
  }

  /**
   * @return the ctrlPanel
   */
  public ControlPanel getCtrlPanel() {
    return ctrlPanel;
  }

  public void refresh() {
    SwingUtilities.invokeLater( new Runnable() { 
      public void run() {
        propsPanel.updateUI();
      } 
    } );
    SwingUtilities.invokeLater( new Runnable() { 
      public void run() {
        updateUI();
      } 
    } );
  }
}
