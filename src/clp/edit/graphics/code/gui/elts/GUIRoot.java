package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.run.res.ui.UiGroup;
import clp.run.res.ui.UiLine;
import clp.run.res.weave.CstOrVar;

public class GUIRoot extends AguiNode {

  private static final long serialVersionUID = 3012019039941282947L;

  private int index;

  private JTextField uiNameField;
  private String uiName;

  private JTextField uiTitleField;
  private String uiTitle;
  private boolean isTitleVar;

  private JPanel jp;



  public GUIRoot() {
    super("UI Root");
    setAllowsChildren(true);
  }

  public void addNewGroup() {
    GUIGroup grp = new GUIGroup();
    add(grp);
    grp.setParent(this);
    grp.addNewLine();
  }

  public int getNumber() {
    return index;
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "General UI parameters"));
      jp.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      c.gridx = 0;
      jp.add(new JLabel("name"), c);
      uiNameField = new JTextField(10);
      uiNameField.setText(uiName);
      c.gridx = 1;
      jp.add(uiNameField, c);
      c.gridy++;
      c.gridx = 0;
      jp.add(new JLabel("title"), c);
      uiTitleField = new JTextField(10);
      uiTitleField.setText(uiTitle);
      c.gridx = 1;
      jp.add(uiTitleField, c);
      c.gridx = 2;
      JCheckBox cb = new JCheckBox();
      cb.setSelected(isTitleVar);
    }
    return jp;
  }

  @Override
  public Component createUIElement() {
    return null;
  }

  public String getTitle() {
    if (uiTitleField == null || uiTitleField.getText().isBlank()) {
      return null;
    }
    return uiTitleField.getText();
  }

  @Override
  public String getContent() {
    GUIGroup n = (GUIGroup) getFirstChild();
    if (n.getContent().isEmpty()) {
      return null;
    }
    String c = "";
    String txt = getTitle();
    if (txt != null && !txt.isEmpty()) {
      c += " title \"" + txt + "\"";
    }
    c += " {\r\n";
    while (n != null) {
      c += n.getContent();
      n = (GUIGroup) n.getNextSibling();
    }
    c += "  }\r\n";
    return c;
  }

  public String getName() {
    if (uiNameField == null || uiNameField.getText().isBlank()) {
      return "NoName";
    }
    return uiNameField.getText();
  }

  public ArrayList<String> getVariables() {
    ArrayList<String> list = new ArrayList<>();
    GUIGroup n = (GUIGroup) getFirstChild();
    while (n != null) {
      list.addAll( n.getVariables() );
      n = (GUIGroup) n.getNextSibling();
    }
    return list;
  }

  @Override
  public String getVariable() {
    return null;
  }

  @Override
  public AguiLeaf copy() {
    return null;
  }

  public void setTitle(CstOrVar title) {
    if (title.getId() != null) {
      uiTitle = title.getId();
      isTitleVar = true;    
    }
    else {
      uiTitle = title.getCst();
    }
  }

  public void setName(String name) {
    uiName = name;
  }

  @Override
  public void addGroup(UiGroup x) {
    GUIGroup grp = new GUIGroup(x);
    add(grp);
    grp.setParent(this);
  }

  @Override
  public void addLine(UiLine x) {
  }
}
