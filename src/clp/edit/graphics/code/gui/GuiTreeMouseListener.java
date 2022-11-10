package clp.edit.graphics.code.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import clp.edit.graphics.code.gui.elts.GUIButton;
import clp.edit.graphics.code.gui.elts.GUIField;
import clp.edit.graphics.code.gui.elts.GUIGroup;
import clp.edit.graphics.code.gui.elts.GUILabel;
import clp.edit.graphics.code.gui.elts.GUILine;
import clp.edit.graphics.code.gui.elts.GUIRoot;
import clp.edit.graphics.code.gui.elts.GUITable;
import clp.edit.graphics.code.gui.elts.GUITextArea;
import clp.edit.graphics.code.gui.elts.GuiDialog;

public class GuiTreeMouseListener extends MouseAdapter implements TreeSelectionListener {

  private GuiDialog dialog;
  private JTree tree;

  public GuiTreeMouseListener(JTree tree, GuiDialog dialog) {
    this.tree = tree;
    this.dialog = dialog;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.getNewLeadSelectionPath() == null) {
      return;
    }
    Object obj = e.getNewLeadSelectionPath().getLastPathComponent();
    if (obj instanceof GUIButton) {
      dialog.getProperties().display((GUIButton)obj);
    }
    else if (obj instanceof GUIField) {
      dialog.getProperties().display((GUIField)obj);
    }
    else if (obj instanceof GUIGroup) {
      dialog.getProperties().display((GUIGroup)obj);
    }
    else if (obj instanceof GUILabel) {
      dialog.getProperties().display((GUILabel)obj);
    }
    else if (obj instanceof GUILine) {
      dialog.getProperties().display((GUILine)obj);
    }
    else if (obj instanceof GUIRoot) {
      dialog.getProperties().display((GUIRoot)obj);
    }
    else if (obj instanceof GUITable) {
      dialog.getProperties().display((GUITable)obj);
    }
    else if (obj instanceof GUITextArea) {
      dialog.getProperties().display((GUITextArea)obj);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      tree.setSelectionPath(selPath); 
      if (selRow > -1) {
         tree.setSelectionRow(selRow); 
         Object obj = tree.getLastSelectedPathComponent();
         if (obj instanceof GUIButton) {
           dialog.displayButtonContext(tree, e.getX(), e.getY(), (GUIButton)obj);
         }
         else if (obj instanceof GUIField) {
           dialog.displayFieldContext(tree, e.getX(), e.getY(), (GUIField)obj);
         }
         else if (obj instanceof GUIGroup) {
           dialog.displayGroupContext(tree, e.getX(), e.getY(), (GUIGroup)obj);
         }
         else if (obj instanceof GUILabel) {
           dialog.displayLabelContext(tree, e.getX(), e.getY(), (GUILabel)obj);
         }
         else if (obj instanceof GUILine) {
           dialog.displayLineContext(tree, e.getX(), e.getY(), (GUILine)obj);
         }
         else if (obj instanceof GUIRoot) {
           dialog.displayRootContext(tree, e.getX(), e.getY(), (GUIRoot)obj);
         }
         else if (obj instanceof GUITable) {
           dialog.displayTableContext(tree, e.getX(), e.getY(), (GUITable)obj);
         }
         else if (obj instanceof GUITextArea) {
           dialog.displayTextAreaContext(tree, e.getX(), e.getY(), (GUITextArea)obj);
         }
      }
    }
  }
}
