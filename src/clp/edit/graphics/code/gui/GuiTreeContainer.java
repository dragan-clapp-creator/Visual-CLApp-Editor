package clp.edit.graphics.code.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;

import clp.edit.graphics.code.gui.elts.GUIRoot;
import clp.edit.graphics.code.gui.elts.GuiDialog;
import clp.run.res.ui.UiVar;

public class GuiTreeContainer extends JPanel {

  private static final long serialVersionUID = -7602844821189025233L;

  private GuiTreeHandler treeHandler;

  /**
   * CONSTRUCTOR
   * 
   * @param dialog
   */
  public GuiTreeContainer(GuiDialog dialog) {
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "UI Tree"));

    treeHandler = new GuiTreeHandler();
    treeHandler.addUITreeMouseListener(dialog);
    add(treeHandler.getTree(), BorderLayout.PAGE_START);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param dialog
   * @param uiVar
   */
  public GuiTreeContainer(GuiDialog dialog, UiVar uiVar) {
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "UI Tree"));

    treeHandler = new GuiTreeHandler(uiVar);
    treeHandler.addUITreeMouseListener(dialog);
    add(treeHandler.getTree(), BorderLayout.PAGE_START);
  }

  public JTree getTree() {
    return treeHandler.getTree();
  }

  public GUIRoot getRoot() {
    return treeHandler.getRoot();
  }
}
