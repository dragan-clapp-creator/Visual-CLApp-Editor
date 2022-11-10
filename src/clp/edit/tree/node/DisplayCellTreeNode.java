package clp.edit.tree.node;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.util.ColorSet;
import clp.run.cel.Cell;
import clp.run.cel.dom.ActivationDomain;
import clp.run.cel.dom.Adomain;
import clp.run.cel.dom.Ddomain;
import clp.run.cel.log.TransposingLine;

public class DisplayCellTreeNode extends CellTreeNode {

  private static final long serialVersionUID = 869110004188770602L;

  private List<String> aconditions;
  private List<String> dconditions;
  private List<String> statements;

  public DisplayCellTreeNode(String name, Cell c) {
    super(name, c);
    aconditions = new ArrayList<>();
    dconditions = new ArrayList<>();
    statements = new ArrayList<>();
  }


  @Override
  public void setProperties(JPanel panel) {
    JPanel jp1 = new JPanel();
    jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
    jp1.add(new JLabel("Name: "));
    jp1.add( createField("name", getName(), 50) );
    jp1.add(Box.createHorizontalStrut(500));

    JPanel jp2 = new JPanel();
    jp2.setLayout(new BoxLayout(jp2, BoxLayout.Y_AXIS));
    jp2.add(createActivationPanel());
    jp2.add(createDectivationPanel());
    jp2.add(createExecutionPanel());

    panel.add(jp1);
    panel.add(jp2);

    disablePanelContents(panel);
  }

  //
  private JTextField createField(String lbl, String text, int spaces) {
    JTextField field = new JTextField(spaces);
    field.setBackground(ColorSet.CellProperties.getLight());
    field.setText(text);
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  //
  private JPanel createActivationPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Activation Domain"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillConditionHeader(jp, c, DomainType.ACTIVATION);

    for (String acondition : aconditions) {
      c.gridy++;
      jp.add( createField(DomainType.ACTIVATION.name()+"_stat_"+c.gridy, acondition, 40), c );
    }

    return jp;
  }

  //
  private void fillConditionHeader(JPanel jp, GridBagConstraints c, DomainType type) {
    c.gridy = 0;
    c.gridx = 0;
 
    JTextField tf = new JTextField("condition");
    tf.setForeground(ColorSet.CellProperties.getLight());
    tf.setBackground(ColorSet.CellProperties.getDark());
    jp.add(tf, c);
  }

  //
  private JPanel createDectivationPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Deactivation Domain"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillConditionHeader(jp, c, DomainType.DEACTIVATION);

    for (String dcondition : dconditions) {
      c.gridy++;
      jp.add( createField(DomainType.DEACTIVATION.name()+"_stat_"+c.gridy, dcondition, 40), c );
    }

    return jp;
  }

  private JPanel createExecutionPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Execution Domain"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillInstructionHeader(jp, c);

    fillStatements(jp, c, statements, DomainType.EXECUTION);

    return jp;
  }

  //
  private void fillInstructionHeader(JPanel jp, GridBagConstraints c) {
    c.gridx = 0;
    c.gridy = 0;
    JTextField tf = new JTextField("statement", 20);
    tf.setForeground(ColorSet.CellProperties.getLight());
    tf.setBackground(ColorSet.CellProperties.getDark());
    jp.add(tf, c);
   }

  //
  private void fillStatements(JPanel jp, GridBagConstraints c, List<String> stmts, DomainType type) {
    c.gridy = 1;
    if (stmts != null) {
      for (String stat : stmts) {
        jp.add( createField(type.name()+"_stat_"+c.gridy, stat, 75), c );
        c.gridy++;
      }
    }
  }

  @Override
  public void focusLost(FocusEvent e) {
  }

  @Override
  public String getSource() {
    return "cell " + getName() + " {\n" + getAdSource() + getDdSource() + getXdSource() + "    }";
  }

  //
  private String getAdSource() {
    if (aconditions.isEmpty()) {
      return "";
    }
    String src = "      AD {\n";
    for (String stmt : aconditions) {
      src += "        " + stmt + ";\n";
    }
    return src + "      }\n";
  }

  //
  private String getDdSource() {
    if (dconditions.isEmpty()) {
      return "";
    }
    String src = "      DD {\n";
    for (String stmt : dconditions) {
      src += "        " + stmt + ";\n";
    }
    return src + "      }\n";
  }

  //
  private String getXdSource() {
    if (statements == null || statements.isEmpty()) {
      return "";
    }
    String src = "      XD {\n";
    for (String stmt : statements) {
      src += "        " + stmt + "\n";
    }
    return src + "      }\n";
  }


  /**
   * @param acondition the acondition to add
   * @param ad 
   */
  public void addAcondition(String acondition, Adomain ad) {
    String cnd = extract(acondition);
    if (!aconditions.contains(cnd)) {
      aconditions.add( cnd );
      addActivationDomain(ad);
    }
  }


  /**
   * @param acondition the acondition to set
   */
  public void setAcondition(String acondition) {
    aconditions.clear();
    if (acondition != null) {
      String cnd = extract(acondition);
      aconditions.add( cnd );
    }
  }

  public void resetAcondition() {
    aconditions.clear();
    updateActivationDomain(null);
  }

  public Adomain getADomain() {
    return getCell().getAdomain();
  }


  public void setADomain(Adomain sad, List<String> list, String pname) {
    Adomain adom = getCell().getAdomain();
    ActivationDomain ad = null;
    if (adom != null) {
      ad = adom.getAd();
      ad.getTransposingLines().clear();
    }
    if (ad == null) {
      adom = new Adomain();
      ad = new ActivationDomain();
      adom.setAd(ad);
      getCell().setIsAdomain(true);
      getCell().setAdomain(adom);
    }
    aconditions.clear();
    ArrayList<TransposingLine> lines = sad.getAd().getTransposingLines();
    for (int i=0; i<list.size(); i++) {
      String x = list.get(i);
      if (x.contains(pname)) {
        aconditions.add(x);
        ad.addTransposingLine(lines.get(i));
      }
    }
  }

  //
  private String extract(String text) {
    int i0 = text.indexOf("{");
    int i1 = text.lastIndexOf(";");
    return text.substring(i0+1, i1).trim();
  }


  /**
   * @return the dconditions
   */
  public List<String> getDconditions() {
    return dconditions;
  }


  /**
   * @param dcondition the dcondition to set
   */
  public void setDcondition(String dcondition) {
    dconditions.clear();
    if (dcondition != null) {
      String cnd = extract(dcondition);
      dconditions.add( cnd );
    }
  }

  /**
   * @param dcondition the dcondition to set
   * @param dd 
   */
  public void addDcondition(String dcondition, Ddomain dd) {
    String cnd = extract(dcondition);
    if (!dconditions.contains(cnd)) {
      dconditions.add( cnd );
      addDeactivationDomain(dd);
    }
  }

  /**
   * @return the statements
   */
  public List<String> getStatements() {
    return statements;
  }

  /**
   * @param statements the statements to set
   */
  public void setStatements(List<String> xd) {
    statements = xd;
  }

  /**
   * @return the aconditions
   */
  public List<String> getAconditions() {
    return aconditions;
  }
}
