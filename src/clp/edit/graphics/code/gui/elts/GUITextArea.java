package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import clp.run.res.ui.UiTextArea;


public class GUITextArea extends AguiLeaf {

  private static final long serialVersionUID = -7246169744072579057L;

  private ArrayList<CstOrVarInfo> cstorvars;

  private JPanel jp;

  private GridBagConstraints c;

  private JTextArea area;

  private JTextField tn;

  private int index;

  public GUITextArea() {
    super("Text Area");
    cstorvars = new ArrayList<>();
  }

  public GUITextArea(UiTextArea x) {
    this();
    cstorvars.add( new CstOrVarInfo((GUILabel)null, cstorvars.size()+1, this, x.getValue()) );
  }

  @Override
  public AguiLeaf copy() {
    GUITextArea n = new GUITextArea();
    JPanel njp = n.createPropertiesPanel();
    njp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Text Area"));
    CstOrVarInfo cov = cstorvars.get(0);
    CstOrVarInfo ncov = n.cstorvars.get(0);
    ncov.copy(cov);
    for (int i=1; i<cstorvars.size(); i++) {
      cov = cstorvars.get(i);
      ncov = new CstOrVarInfo(null, n.cstorvars.size(), this);
      n.c.gridx = 0;
      ncov.createCstOrVar(njp, n.c);
      ncov.copy(cov);
      n.cstorvars.add(ncov);
    }
    invokeUpdate();
    return n;
  }

  public JPanel createPropertiesPanel() {
    if (jp == null) {
      jp = new JPanel();
      jp.setBorder(
          BorderFactory.createTitledBorder(
              BorderFactory.createBevelBorder(EtchedBorder.RAISED),
              "Text Area"));
      jp.setLayout(new GridBagLayout());
      c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.gridy = 0;
      c.gridx = 0;
      jp.add(new JLabel("name"), c);
      c.gridx = 1;
      tn = new JTextField(10);
      jp.add(tn, c);
      c.gridy++;
      c.gridx = 0;
      JButton button = new JButton("+");
      jp.add(button, c);
      CstOrVarInfo cov = new CstOrVarInfo(null, cstorvars.size()+1, this);
      cov.createCstOrVar(jp, c);
      cstorvars.add(cov);
      index = c.gridy;
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          CstOrVarInfo cov = new CstOrVarInfo(null, cstorvars.size()+index, GUITextArea.this);
          c.gridx = 0;
          cov.createCstOrVar(jp, c);
          cstorvars.add(cov);
          invokeUpdate();
       }
      });
    }
    return jp;
  }

  //
  public void updateArea() {
    String text = "";
    for (CstOrVarInfo cov : cstorvars) {
      if (cov.isVar()) {
        text += "<" + cov.getVarText();
        if (cov.isIndex()) {
          text += ":" + cov.getIndex();
        }
        text += ">\n";
      }
      else {
        text += cov.getIdentText() + "\n";
      }
    }
    if (area != null) {
      area.setText(text);
    }
  }

  //
  private void invokeUpdate() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Container p = jp.getParent();
        if (p != null) {
          p.validate();
          p.repaint();
        }
        else {
          jp.validate();
          jp.repaint();
        }
      }
    });
  }

  @Override
  public Component createUIElement() {
    area = new JTextArea(2, 5);
    return area;
  }

  @Override
  public String getContent() {
    String c = "        ";
    if (tn.getText().isEmpty()) {
      for (int i=0; i<getParent().getChildCount(); i++) {
        if (getParent().getChildAt(i) == this) {
          c += "TEXT ta_" + i + " < \"";
          break;
        }
      }
    }
    else {
      c += "TEXT " + tn.getText() + " < \"";
    }
    for (CstOrVarInfo cov : cstorvars) {
      if (cov.isVar()) {
        String str = cov.getIndex();
        if (str != null && !str.isEmpty()) {
          c += "<" + cov.getVarText() + ":" + str + ">";
        }
        else {
          c += "<" + cov.getVarText() + ">";
        }
      }
      else {
        c += cov.getIdentText();
      }
    }
    c += "\"";
    return c;
  }

  @Override
  public String getVariable() {
    String c = "";
    String str;
    for (CstOrVarInfo cov : cstorvars) {
      if (cov.isVar()) {
        c += cov.getName();
        str = cov.getIndex();
        if (str != null && !str.isEmpty()) {
          c += ":" + str;
        }
        c += ",";
      }
    }
    c += "/STRING/D";
    return c;
  }
}
