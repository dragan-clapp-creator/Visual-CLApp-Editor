package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import clp.run.res.ui.UiButton;

public class GUIButton extends AguiLeaf {

  private static final long serialVersionUID = 5609770147387978519L;

  private JPanel jp;

  private JCheckBox uiRollback;
  private boolean isRollback;

  private JTextField bn;
  private String bnText;

  private JTextField cn;
  private String cnText;

  public GUIButton() {
    super("Button");
  }

  public GUIButton(UiButton x) {
    this();
    isRollback = x.isIsRollback();
    bnText = x.getName();
    cnText = x.getTitle();
  }

  @Override
  public AguiLeaf copy() {
    GUIButton n = new GUIButton();
    JPanel njp = n.createPropertiesPanel();
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Button "+bn.getText()));
    n.bn.setText(bn.getText());
    n.cn.setText(cn.getText());
    n.uiRollback.setSelected(uiRollback.isSelected());
    return n;
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "Button"));
      jp.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      c.gridx = 0;
      jp.add(new JLabel("name"), c);
      c.gridx = 1;
      bn = new JTextField(10);
      bn.setText(bnText);
      jp.add(bn, c);
      c.gridy++;
      c.gridx = 0;
      jp.add(new JLabel("caption"), c);
      c.gridx = 1;
      cn = new JTextField(10);
      cn.setText(cnText);
      jp.add(cn, c);
      c.gridy++;
      c.gridx = 0;
      uiRollback = new JCheckBox("rollback");
      uiRollback.setSelected(isRollback);
      jp.add(uiRollback, c);
      createListeners();
    }
    return jp;
  }

  @Override
  public void createListeners() {
    bn.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        bnText = bn.getText().trim();
        invokeUpdate(jp);
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    uiRollback.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        isRollback = uiRollback.isSelected();
      }
    });
  }

  //
  private void invokeUpdate(JPanel jp) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Container p = jp.getParent();
        while (!(p instanceof GuiDialog) && p != null) {
          p = p.getParent();
        }
        if (p != null) {
          ((GuiDialog)p).refresh(GUIButton.this);
        }
      }
    });
  }

  @Override
  public Component createUIElement() {
    String text = bn == null ? "" : bn.getText();
    if (text.isEmpty()) {
      text = bnText == null ? "button" : bnText;
    }
    return new JButton(text);
  }

  @Override
  public String getContent() {
    String c = "        ";
    String bname = bn == null ? "" : bn.getText();
    if (bname.isEmpty()) {
      bname = bnText == null ? "button" : bnText;
    }
    String str = bname;
    if (str.isEmpty()) {
      str = "btn";
    }
    c += "BUTTON " + str;
    if (isRollback) {
      c += " ROLLBACK";
    }
    String txt = cn == null ? "" : cn.getText();
    if (txt.isEmpty()) {
      txt = cnText == null ? "" : cnText;
    }
    if (!txt.isEmpty()) {
      c += " caption \"" + txt + "\"";
    }
    if (!bname.isEmpty()) {
      c += " > " + str;
    }
    return c;
  }

  @Override
  public String getVariable() {
    String c = getButtonName() + "/BOOL/";
    return c;
  }

  //
  private String getButtonName() {
    return bnText;
  }
}
