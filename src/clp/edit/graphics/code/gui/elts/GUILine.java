package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import clp.edit.graphics.code.gui.GuiDefTypeVisitor;
import clp.run.res.ui.UiButton;
import clp.run.res.ui.UiDefType;
import clp.run.res.ui.UiGroup;
import clp.run.res.ui.UiInputField;
import clp.run.res.ui.UiLabel;
import clp.run.res.ui.UiLine;
import clp.run.res.ui.UiTable;
import clp.run.res.ui.UiTextArea;

public class GUILine extends AguiNode {

  private static final long serialVersionUID = 7511796635043824378L;

  public GUILine() {
    super("Line");
    setAllowsChildren(true);
  }

  public GUILine(UiLine x) {
    this();
    ArrayList<UiDefType> guiRows = new ArrayList<>();
    guiRows.add(x.getUiDefType());
    guiRows.addAll(x.getUiDefTypes());
    for (UiDefType row : guiRows) {
      row.accept(new GuiDefTypeVisitor(this));
    }
  }

  @Override
  public AguiLeaf copy() {
    GUILine n = new GUILine();
    JPanel njp = n.createPropertiesPanel();
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Line"));
    if (!isLeaf()) {
      copyElements(n, (AguiLeaf) getFirstChild());
    }
    return n;
  }

  //
  private void copyElements(GUILine n, AguiLeaf child) {
    if (child != null) {
      AguiLeaf newchild = child.copy();
      n.insert(newchild, n.getChildCount());
      copyElements(n, (AguiLeaf) child.getNextSibling());
    }
  }

  @Override
  public Component createUIElement() {
    return null;
  }

  public String getContent() {
    String c = "";
    if (!isLeaf()) {
      AguiLeaf n = (AguiLeaf) getFirstChild();
      while (n != null) {
        c += n.getContent();
        if (n != getLastChild()) {
          c += ",";
          n = (AguiLeaf) n.getNextNode();
        }
        else {
          n = null;
        }
        c += "\r\n";
      }
    }
    return c;
  }

  public ArrayList<String> getVariables() {
    ArrayList<String> list = new ArrayList<String>();
    if (!isLeaf()) {
      AguiLeaf n = (AguiLeaf) getFirstChild();
      while (n != null) {
        if (n instanceof AguiNode) {
          list.addAll(((AguiNode)n).getVariables() );
        }
        else {
          list.add(n.getVariable());
        }
        if (n != getLastChild()) {
          n = (AguiLeaf) n.getNextNode();
        }
        else {
          n = null;
        }
      }
    }
    return list;
  }

  @Override
  public JPanel createPropertiesPanel() {
    return new JPanel();
  }

  @Override
  public String getVariable() {
    return null;
  }

  @Override
  public void addGroup(UiGroup x) {
  }

  @Override
  public void addLine(UiLine x) {
  }

  public void addLabel(UiLabel x) {
    GUILabel lbl = new GUILabel(x.getValue());
    add(lbl);
    lbl.setParent(this);
  }

  public void addInputField(UiInputField x) {
    GUIField in = new GUIField(x);
    add(in);
    in.setParent(this);
  }

  public void addTextArea(UiTextArea x) {
    GUITextArea ta = new GUITextArea(x);
    add(ta);
    ta.setParent(this);
  }

  public void addTable(UiTable x) {
    GUITable tab = new GUITable(x);
    add(tab);
    tab.setParent(this);
  }

  public void addButton(UiButton x) {
    GUIButton tab = new GUIButton(x);
    add(tab);
    tab.setParent(this);
  }
}
