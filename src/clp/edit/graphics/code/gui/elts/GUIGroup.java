package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.graphics.code.gui.GuiBundleVisitor;
import clp.run.res.ui.UiBundle;
import clp.run.res.ui.UiGroup;
import clp.run.res.ui.UiLine;

public class GUIGroup extends AguiNode {

  private static final long serialVersionUID = 407438776567153760L;

  private int line;

  private JTextField titleField;
  private String title;

  private JPanel jp;

  public GUIGroup() {
    super("Group");
    setAllowsChildren(true);
  }

  public GUIGroup(UiGroup x) {
    this();
    title = x.getTitle();
    ArrayList<UiBundle> bundles = new ArrayList<>();
    bundles.add(x.getUiBundle());
    bundles.addAll(x.getUiBundles());
    for (UiBundle bundle : bundles) {
      bundle.accept(new GuiBundleVisitor(this));
    }
  }

  public void addNewLine() {
    GUILine l = new GUILine();
    add(l);
    l.setParent(this);
  }

  public int getLineNumber() {
    return line;
  }

  @Override
  public AguiNode copy() {
    GUIGroup n = new GUIGroup();
    JPanel njp = n.createPropertiesPanel();
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Group "+titleField.getText()));
    n.titleField.setText(titleField.getText());
    if (!isLeaf()) {
      copyLines(n, (GUILine) getFirstChild());
    }
    return n;
  }

  //
  private void copyLines(GUIGroup n, GUILine ln) {
    if (ln != null) {
      AguiLeaf newln = ln.copy();
      n.insert(newln, n.getChildCount());
      copyLines(n, (GUILine) ln.getNextSibling());
    }
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "Group"));
      jp.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      c.gridx = 0;
      jp.add(new JLabel("group title"), c);
      c.gridx = 1;
      titleField = new JTextField(10);
      titleField.setText(title);
      jp.add(titleField, c);
      titleField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          jp.setBorder(
              BorderFactory.createTitledBorder(
                  BorderFactory.createBevelBorder(EtchedBorder.RAISED),
                  "Group "+titleField.getText()));
        }
      });
    }
    return jp;
  }

  public String getName() {
    if (titleField == null || titleField.getText() == null || titleField.getText().isEmpty()) {
      return title == null ? "" : title;
    }
    return titleField.getText();
  }

  @Override
  public Component createUIElement() {
    return null;
  }

  public String getContent() {
    GUILine n = (GUILine) getFirstChild();
    if (n == null || n.getContent().isEmpty()) {
      return "";
    }
    String c = "    GROUP \"" + getGroupName() + "\" {\r\n";
    int i = 1;
    while (n != null) {
      c += "      LINE " + i + " {\r\n";
      c += n.getContent();
      c += "      }\r\n";
      i++;
      n = (GUILine) n.getNextSibling();
    }
    c += "    }\r\n";
    return c;
  }

  //
  private String getGroupName() {
    if (titleField != null && !titleField.getText().isBlank()) {
      return titleField.getText().trim();
    }
    return "";
  }

  public ArrayList<String> getVariables() {
    ArrayList<String> list = new ArrayList<>();
    GUILine n = (GUILine) getFirstChild();
    while (n != null) {
      list.addAll( n.getVariables() );
      n = (GUILine) n. getNextSibling();
    }
    return list;
  }

  @Override
  public String getVariable() {
    return null;
  }

  @Override
  public void addGroup(UiGroup x) {
    GUIGroup grp = new GUIGroup(x);
    add(grp);
    grp.setParent(this);
  }

  @Override
  public void addLine(UiLine x) {
    GUILine l = new GUILine(x);
    add(l);
    l.setParent(this);
  }
}
