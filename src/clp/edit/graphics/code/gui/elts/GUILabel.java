package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import clp.run.res.ui.CstOrVarIdentifier;

public class GUILabel extends AguiLeaf {

  private static final long serialVersionUID = -773091730522411713L;

  private JPanel jp;

  private JLabel label;

  private CstOrVarInfo cov;

  private GridBagConstraints c;

  public GUILabel() {
    super("Label");
  }

  public GUILabel(CstOrVarIdentifier value) {
    this();
    cov = new CstOrVarInfo(this, 1, (GUITextArea)null, value);
  }

  @Override
  public AguiLeaf copy() {
    GUILabel n = new GUILabel();
    JPanel njp = n.createPropertiesPanel();
    n.cov.copy(cov);
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Label "+n.cov.getIdentText()));
    return n;
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "Label"));
      jp.setLayout(new GridBagLayout());
      c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      c.gridy++;
      c.gridx = 0;
      if (cov == null) {
        cov = new CstOrVarInfo(this, 1, null);
      }
      cov.createCstOrVar(jp, c);
    }
    return jp;
  }

  @Override
  public void createListeners() {
    cov.createListeners(jp, c);
  }

  @Override
  public Component createUIElement() {
    label = new JLabel("label");
    String txt = cov.getIdentText();
    if (txt != null && !txt.isEmpty()) {
      label.setText(txt);
    }
    return label;
  }

  @Override
  public String getContent() {
    String c = "        ";
    String txt = cov.getIdentText();
    c += "LABEL l_" + txt.replace(" ", "_").replace(":", "") + " < ";
    if (cov.isVar()) {
      c += cov.getVarText();
      if (cov.isIndex()) {
        c += "[" + cov.getIndex();
        if (cov.isIndex2()) {
          c += ", " + cov.getIndex2();
        }
        c += "]";
      }
    }
    else {
      c += "\"" + txt + "\"";
    }
    return c;
  }

  @Override
  public String getVariable() {
    String c = "";
    if (cov.isVar()) {
      c += cov.getVarText();
      String str = cov.getIndex();
      if (str != null && !str.isEmpty()) {
        c += ":" + str;
      }
      c += "/STRING/";
    }
    return c;
  }
}
