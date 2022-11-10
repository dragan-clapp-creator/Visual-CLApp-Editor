package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import clp.run.res.ui.CstOrVarIdentifier;
import clp.run.res.ui.CstOrVarIdentifierVisitor;
import clp.run.res.ui.Literal;
import clp.run.res.ui.VarIdentifier;

public class CstOrVarInfo {

  private JComboBox<String> cstvar;
  private String selectedCstVar;

  private JTextField identNameField;
  private String identName;

  private JTextField varNameField;
  private String varName;

  private JTextField varIndexField;
  private String varIndexName;
  private String mark;
  private int line;
  private String mark_i;
  private int col;

  private GUITextArea ta;

  private JTextField varIndexField2;
  private String varIndexName2;

  private GUILabel label;

  public CstOrVarInfo(GUILabel label, int line, GUITextArea ta) {
    line++;
    this.label = label;
    this.mark= ""+line;
    this.line = line;
    this.ta = ta;
    mark_i = mark+"_"+line;
    selectedCstVar = "CST";
  }

  public CstOrVarInfo(GUILabel guiLabel, int i, GUITextArea ta, CstOrVarIdentifier value) {
    this(guiLabel, i, ta);
    value.accept(new CstOrVarIdentifierVisitor() {
      @Override
      public void visitVarIdentifier(VarIdentifier x) {
        selectedCstVar = "VAR";
        varName = x.getVar();
        if (x.isIndex()) {
          varIndexName = ""+x.getIndex();
          if (x.isIndex2()) {
            varIndexName2 = ""+x.getIndex2();
          }
        }
      }
      @Override
      public void visitLiteral(Literal x) {
        identName = x.getValue();
      }
    });
  }

  void copy(CstOrVarInfo cov) {
    selectedCstVar = (String) cov.cstvar.getSelectedItem();
    cstvar.setSelectedItem(selectedCstVar);
    identNameField.setText(cov.identNameField.getText());
    if (cov.varNameField != null) {
      varNameField.setText(cov.varNameField.getText());
      varIndexField.setText(cov.varIndexField.getText());
    }
  }

  //
  public void createCstOrVar(JPanel jp, GridBagConstraints c) {
    c.gridy = line;
    createComboForCstVar();
    jp.add(cstvar, c);
    c.gridx++;
    col = c.gridx;
    identNameField = new JTextField(10);
    identNameField.setName(mark);
    identNameField.setText(identName);
    jp.add(identNameField, c);
    createListeners(jp, c);
  }

  //
  private void createComboForCstVar() {
    cstvar = new JComboBox<String>(new String[]{"CST", "VAR"});
    cstvar.setName(mark);
    cstvar.setSelectedItem(selectedCstVar);
  }

  public void createListeners(JPanel jp, GridBagConstraints c) {
    identNameField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (ta != null) {
          ta.updateArea();
        }
        invokeUpdate(jp);
      }
    });
    if (cstvar == null) {
      createComboForCstVar();
    }
    cstvar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performAction(jp, c);
      }
    });
    performAction(jp, c);
  }

  //
  private void performAction(JPanel jp, GridBagConstraints c) {
    selectedCstVar = (String) cstvar.getSelectedItem();
    if (selectedCstVar.equals("VAR")) {
      c.gridx = col;
      c.gridy = line;
      createVariableArea(jp, c, mark_i);
    }
    else {
      for (int i=jp.getComponentCount()-1; i>0; i--) {
        Component comp = jp.getComponent(i);
        if (comp.getName() != null && comp.getName().equals(mark_i)) {
          jp.remove(i);
        }
      }
    }
    if (ta != null) {
      ta.updateArea();
    }
    invokeUpdate(jp);
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
          ((GuiDialog)p).refresh(label == null ? ta : label);
        }
      }
    });
  }

  //
  private void createVariableArea(JPanel jp, GridBagConstraints c, String mark) {
    varNameField = new JTextField(10);
    varNameField.setName(mark);
    varNameField.setText(varName);
    jp.add(varNameField, c);
    c.gridx++;
    JLabel cmp = new JLabel("[");
    cmp.setName(mark);
    jp.add(cmp, c);
    c.gridx++;
    varIndexField = new JTextField(2);
    varIndexField.setName(mark);
    varIndexField.setText(varIndexName);
    jp.add(varIndexField, c);
    c.gridx++;
    cmp = new JLabel("]");
    cmp.setName(mark);
    jp.add(cmp, c);
    c.gridx++;
    cmp = new JLabel("[");
    cmp.setName(mark);
    jp.add(cmp, c);
    c.gridx++;
    varIndexField2 = new JTextField(2);
    varIndexField2.setName(mark);
    varIndexField2.setText(varIndexName2);
    jp.add(varIndexField2, c);
    c.gridx++;
    cmp = new JLabel("]");
    cmp.setName(mark);
    jp.add(cmp, c);
  }

  public String getName() {
    String txt = identNameField.getText();
    if (txt == null || txt.isEmpty()) {
      txt = varNameField.getText();
    }
    return txt;
  }

  public boolean isVar() {
    if (cstvar == null) {
      createComboForCstVar();
    }
    return "VAR".equals(cstvar.getSelectedItem());
  }

  public String getIdentText() {
    if (identNameField == null) {
      return identName;
    }
    return identNameField.getText();
  }

  public String getVarText() {
    if (varNameField.getText().isEmpty()) {
      return identNameField.getText();
    }
    return varNameField.getText();
  }

  public boolean isIndex() {
    return !varIndexField.getText().isEmpty();
  }

  public String getIndex() {
    return varIndexField.getText();
  }

  public boolean isIndex2() {
    return !varIndexField2.getText().isEmpty();
  }

  public String getIndex2() {
    return varIndexField2.getText();
  }
}
