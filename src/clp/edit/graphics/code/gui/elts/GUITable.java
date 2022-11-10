package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.run.res.ui.UiTable;


public class GUITable extends AguiLeaf {

  private static final long serialVersionUID = -3619766733797955816L;

  private static final String[] selTypes = new String[] {"", "LINE", "COLUMN", "CELL"};

  private JPanel jp;

  private JTextField tn;

  private JTextField varname;

  transient private JComboBox<String> sel;
  private String selectedSel;

  public GUITable() {
    super("Table");
    selectedSel = "";
  }

  public GUITable(UiTable x) {
    this();
    // TODO
  }

  @Override
  public AguiLeaf copy() {
    GUITable n = new GUITable();
    JPanel njp = n.createPropertiesPanel();
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Table "+tn.getText()));
    n.tn.setText(tn.getText());
    n.varname.setText(varname.getText());
    
    n.sel.setSelectedItem(selectedSel);
    return n;
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "Table"));
      jp.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      c.gridx = 0;
      jp.add(new JLabel("name"), c);
      c.gridx = 1;
      tn = new JTextField(10);
      jp.add(tn, c);
      c.gridy++;
      c.gridx = 0;
      jp.add(new JLabel("attached to"), c);
      c.gridx = 1;
      varname = new JTextField(10);
      jp.add(varname, c);
      c.gridy++;
      c.gridx = 0;
      jp.add(new JLabel("SEL"), c);
      c.gridx = 1;
      sel = new JComboBox<String>(selTypes);
      jp.add(sel, c);
    }
    return jp;
  }

  @Override
  public Component createUIElement() {
    return new JTable();
  }

  @Override
  public void createListeners() {
    if (sel == null) {
      sel = new JComboBox<String>(selTypes);
      sel.setSelectedItem(selectedSel);
    }
  }

  @Override
  public String getContent() {
    String c = "        ";
    String str = varname.getText();
    if (tn.getText().isEmpty()) {
      c += "TABLE " + str + " = " + str;
    }
    else {
      c += "TABLE " + tn.getText() + " = " + str;
    }
    String txt = selectedSel;
    if (!txt.isEmpty()) {
      c += " SEL " + txt;
    }
    return c;
  }

  @Override
  public String getVariable() {
    String c = varname.getText() + "/TABLE/D";
    return c;
  }
}
