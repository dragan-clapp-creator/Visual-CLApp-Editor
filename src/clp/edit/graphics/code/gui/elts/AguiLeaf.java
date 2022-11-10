package clp.edit.graphics.code.gui.elts;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class AguiLeaf extends DefaultMutableTreeNode {

  private static final long serialVersionUID = -278205558240704720L;

  /**
   * CONSTRUCTOR
   * 
   * @param s
   */
  public AguiLeaf(String s) {
    super(s);
  }

  abstract public JPanel createPropertiesPanel();

  abstract public String getContent();

  abstract public Component createUIElement();

  abstract public AguiLeaf copy();

  abstract public String getVariable();

  public void createListeners() {
  }
}
