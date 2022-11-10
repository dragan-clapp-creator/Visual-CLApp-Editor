package clp.edit.graphics.code.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import clp.edit.graphics.code.gui.elts.AguiLeaf;
import clp.edit.graphics.code.gui.elts.AguiNode;
import clp.edit.graphics.code.gui.elts.GUIButton;
import clp.edit.graphics.code.gui.elts.GUIField;
import clp.edit.graphics.code.gui.elts.GUIGroup;
import clp.edit.graphics.code.gui.elts.GUILabel;
import clp.edit.graphics.code.gui.elts.GUILine;
import clp.edit.graphics.code.gui.elts.GUITextArea;
import clp.edit.graphics.code.gui.elts.GuiDialog;

public class GuiContextMenu extends JPopupMenu {

  private static final long serialVersionUID = -642913248276020477L;

  static public enum Element {
    GROUP("Add Group"),
    LINE("Add Line"),
    BUTTON("Add Button"),
    FIELD("Add Field"),
    LABEL("Add Label"),
    // TABLE("Add Table"),
    TEXT("Add Text Area")
    ;

    private String value;

    private Element(String s) {
      value = s;
    }
    String getValue() {
      return value;
    }
  }

  public GuiContextMenu(boolean isDuplicateAndDelete, ArrayList<Element> list, AguiLeaf node, GuiDialog uiDialog) {
    if (list != null) {
      for (Element elt : list) {
        JMenuItem add = new JMenuItem(elt.getValue());
        add.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            AguiLeaf n = null;
            switch(elt) {
              case BUTTON:
                n = new GUIButton();
                break;
              case FIELD:
                n = new GUIField();
                break;
              case LABEL:
                n = new GUILabel();
                break;
//              case TABLE:
//                n = new UITable();
//                break;
              case TEXT:
                n = new GUITextArea();
                break;
              case GROUP:
                n = new GUIGroup();
                break;
              case LINE:
                n = new GUILine();
                break;
            }
            insertTo(node, n);
            uiDialog.refresh(n);
          }
        });
        add(add);
      }
      add(new Separator());
    }
    if (isDuplicateAndDelete) {
      JMenuItem duplicate = new JMenuItem("duplicate");
      duplicate.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          AguiLeaf n = node.copy();
          insertTo((AguiLeaf) node.getParent(), n);
          uiDialog.refresh(n);
        }
      });
      add(duplicate);

      JMenuItem delete = new JMenuItem("delete");
      delete.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          AguiNode n = (AguiNode)node.getParent();
          n.remove(node);
          uiDialog.refresh(n);
        }
      });
      add(delete);
    }
  }

  //
  private void insertTo(AguiLeaf node, AguiLeaf n) {
    node.insert(n, node.getChildCount());
    n.setParent(node);
  }
}
