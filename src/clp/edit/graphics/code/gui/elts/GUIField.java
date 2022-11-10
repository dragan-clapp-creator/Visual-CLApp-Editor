package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.run.res.ui.UiInputField;

public class GUIField extends AguiLeaf {

  private static final long serialVersionUID = -7532617226938214352L;

  private static final String[] types = { "BOOL", "INT", "FLOAT", "STRING", "DATE", "TIME" };

  private JPanel jp;

  transient private JComboBox<String> vartype;
  private String selectedVartype;

  private JTextField varNameField;
  private String varName;

  private JCheckBox uiRequired;
  private boolean isRequired;

  private JCheckBox uiEnabled;
  private boolean isEnabled;

  private JCheckBox uiPasswrd;
  private boolean isPasswrd;

  public GUIField() {
    super("Field");
  }

  public GUIField(UiInputField x) {
    this();
    isEnabled = x.isIsEnabled();
    isRequired = x.isIsRequired();
    isPasswrd = x.isIsPassword();
    if (x.getVarIdentifier() != null) {
      varName = x.getVarIdentifier().getVar();
      selectedVartype = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getVariableTypeForUI(varName);
    }
  }

  @Override
  public AguiLeaf copy() {
    GUIField n = new GUIField();
    JPanel njp = n.createPropertiesPanel();
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Input Field "+varNameField.getText()));
    n.vartype.setSelectedItem(selectedVartype);
    n.varNameField.setText(varNameField.getText());
    n.uiRequired.setSelected(uiRequired.isSelected());
    n.uiEnabled.setSelected(uiEnabled.isSelected());
    n.uiPasswrd.setSelected(uiPasswrd.isSelected());
    return n;
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "Input Field"));
      jp.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      createListeners();
      jp.add(vartype, c);
      c.gridx = 1;
      varNameField = new JTextField(10);
      varNameField.setText(varName);
      jp.add(varNameField, c);
      c.gridx = 0;
      c.gridy++;
      uiEnabled = new JCheckBox("enabled");
      uiEnabled.setSelected(isEnabled);
      jp.add(uiEnabled, c);
      c.gridy++;
      uiRequired = new JCheckBox("required");
      uiRequired.setSelected(isRequired);
      jp.add(uiRequired, c);
      c.gridy++;
      uiPasswrd = new JCheckBox("password");
      uiPasswrd.setSelected(isPasswrd);
      jp.add(uiPasswrd, c);
    }
    return jp;
  }

  @Override
  public Component createUIElement() {
    return new JTextField(8);
  }

  @Override
  public String getContent() {
    String c = "        ";
    String vname = varNameField == null ? varName : varNameField.getText();
    c += "FIELD f_" + vname;
    boolean isreq = uiRequired == null ? isRequired : uiRequired.isSelected();
    if (isreq) {
      c += " REQUIRED";
    }
    boolean isenb = uiEnabled == null ? isEnabled : uiEnabled.isSelected();
    if (isenb) {
      c += " ENABLED";
    }
    boolean ispsw = uiPasswrd == null ? isPasswrd : uiPasswrd.isSelected();
    if (ispsw) {
      c += " PASS";
    }
    if (!vname.isEmpty()) {
      c += " = " + vname;
    }
    return c;
  }

  @Override
  public void createListeners() {
    if (vartype == null) {
      vartype = new JComboBox<>(types);
      vartype.setSelectedItem(selectedVartype);
    }
    vartype.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedVartype = (String) vartype.getSelectedItem();
      }
    });
  }

  @Override
  public String getVariable() {
    if (varNameField == null) {
      return "/f_/";
    }
    String c = varNameField.getText();
    c += "/" + selectedVartype + "/D";    // will be displayed on output area
    return c;
  }
}
