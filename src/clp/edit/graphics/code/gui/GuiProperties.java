package clp.edit.graphics.code.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import clp.edit.graphics.code.gui.elts.GUIButton;
import clp.edit.graphics.code.gui.elts.GUIField;
import clp.edit.graphics.code.gui.elts.GUIGroup;
import clp.edit.graphics.code.gui.elts.GUILabel;
import clp.edit.graphics.code.gui.elts.GUILine;
import clp.edit.graphics.code.gui.elts.GUIRoot;
import clp.edit.graphics.code.gui.elts.GUITable;
import clp.edit.graphics.code.gui.elts.GUITextArea;

public class GuiProperties extends JPanel {

  private static final long serialVersionUID = 286004410552636555L;

  public GuiProperties() {
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Properties"));
  }

  public void display(GUIGroup obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUIButton obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUIField obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUILabel obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUILine obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUIRoot obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUITable obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  public void display(GUITextArea obj) {
    removeAll();
    add( obj.createPropertiesPanel() );
    invokeUpdate();
  }

  //
  private void invokeUpdate() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        validate();
        repaint();
      }
    });
  }
}
