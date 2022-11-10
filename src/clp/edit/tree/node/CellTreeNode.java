package clp.edit.tree.node;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.edit.tree.node.util.CellContent;
import clp.edit.tree.node.util.CellContent.Statement;
import clp.edit.tree.node.util.IContent;
import clp.edit.util.ColorSet;
import clp.run.cel.Cell;
import clp.run.cel.Heap;
import clp.run.cel.dom.ActivationDomain;
import clp.run.cel.dom.Adomain;
import clp.run.cel.dom.Ddomain;
import clp.run.cel.dom.DeactivationDomain;
import clp.run.cel.dom.Xdomain;
import clp.run.cel.log.LogicalExpression;
import clp.run.cel.log.TransposingLine;

public class CellTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = -3359744491158072740L;

  enum DomainType {
    ACTIVATION, DEACTIVATION, EXECUTION;
  }
  private ColorSet info;  // this object's context
  private CellContent content;
  private boolean isFromGraphics;

  private Cell cell;


  public CellTreeNode(String name, Cell c) {
    super(name, null, null);
    this.cell = c;
    this.cell.setName(name);
    this.info = ColorSet.CellProperties;
    initializeContext(c);
  }

  //
  private void initializeContext(Cell c) {
    content = new CellContent();
    if (c.isAdomain()) {
      content.setAdomain(c.getAdomain());
    }
    if (c.isDdomain()) {
      content.setDdomain(c.getDdomain());
    }
    if (c.isXdomain()) {
      content.setXdomain(c.getXdomain());
    }
  }

  public void clearActivationDomain() {
    content.getAconditions().clear();
    if (cell.isAdomain()) {
      cell.setIsAdomain(false);
      cell.setAdomain(null);
    }
  }

  public void updateActivationDomain(Adomain adomain) {
    content.getAconditions().clear();
    if (content.getAdomain() == null) {
      content.setAdomain(adomain);
    }
    cell.setIsAdomain(adomain != null);
    cell.setAdomain(adomain);
  }


  public void addActivationDomain(Adomain adomain) {
    if (content.getAdomain() == null) {
      content.setAdomain(adomain);
      cell.setIsAdomain(true);
      cell.setAdomain(adomain);
    }
    else {
      ActivationDomain ad = cell.getAdomain().getAd();
      for (TransposingLine line : adomain.getAd().getTransposingLines()) {
        ad.addTransposingLine(line);
      }
    }
  }


  public void clearDeactivationDomain() {
    content.getDconditions().clear();
    if (cell.isDdomain()) {
      cell.setIsDdomain(false);
      cell.setDdomain(null);
    }
  }

  public void addDeactivationDomain(Ddomain ddomain) {
    if (content.getDdomain() == null) {
      content.setDdomain(ddomain);
      cell.setIsDdomain(true);
      cell.setDdomain(ddomain);
    }
    else {
      DeactivationDomain dd = cell.getDdomain().getDd();
      for (LogicalExpression line : ddomain.getDd().getLogicalExpressions()) {
        dd.addLogicalExpression(line);
      }
    }
  }

  public void updateDeactivationDomain(Ddomain ddomain) {
    content.getDconditions().clear();
    if (content.getDdomain() == null) {
      content.setDdomain(ddomain);
    }
    cell.setIsDdomain(true);
    cell.setDdomain(ddomain);
  }

  public void updateExecutionDomain(Xdomain xdomain) {
    content.getStatements().clear();
    content.setXdomain(xdomain);
    cell.setIsXdomain(true);
    cell.setXdomain(xdomain);
  }


  @Override
  public Object getAssociatedObject() {
    return cell;
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
  }

  //
  private JTextField createField(String lbl, String text, int spaces) {
    JTextField field = new JTextField(spaces);
    field.setBackground(info.getLight());
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

    fillStatements(jp, c, content.getAconditions(), DomainType.ACTIVATION);

    return jp;
  }

  //
  private void fillConditionHeader(JPanel jp, GridBagConstraints c, DomainType type) {
    c.gridy = 0;
    JTextField tf = new JTextField("level");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("condition");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("next level");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);

    if (!isFromGraphics) {
      tf = new JTextField("deletion");
      tf.setForeground(info.getLight());
      tf.setBackground(info.getDark());
      c.gridx++;
      jp.add(tf, c);

      c.gridx++;
      JButton btn = new JButton("add");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent a) {
          GeneralContext.getInstance().setDirty();
          if (type == DomainType.ACTIVATION) {
            content.addEmptyActivationCondition();
          }
          else {
            content.addEmptyDeactivationCondition();
          }
          GeneralContext.getInstance().setCodeDirty(true);
          refresh(CellTreeNode.this);
        }
      });

      jp.add(btn, c);
    }
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

    fillStatements(jp, c, content.getDconditions(), DomainType.DEACTIVATION);

    return jp;
  }

  //
  private void fillStatements(JPanel jp, GridBagConstraints c, ArrayList<Statement> stmts, DomainType type) {
    c.gridy = 1;
    for (Statement stat : stmts) {
      c.gridx = 0;
      jp.add( createField(type.name()+"_level_"+c.gridy, stat.getLevel(), 4), c );
   
      c.gridx = 1;
      jp.add( createField(type.name()+"_stat_"+c.gridy, stat.getStatement(), 15), c );
   
      c.gridx = 2;
      jp.add( createField(type.name()+"_next_"+c.gridy, stat.getNext(), 4), c );

      if (!isFromGraphics) {
        c.gridx = 3;
        jp.add(createRemoveButton(type, ""+c.gridy), c);
      }

      c.gridy++;
    }
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

    fillStatements(jp, c, content.getStatements(), DomainType.EXECUTION);

    return jp;
  }

  //
  private void fillInstructionHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("level");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("statement");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("next level");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    if (!isFromGraphics) {
      tf = new JTextField("deletion");
      tf.setForeground(info.getLight());
      tf.setBackground(info.getDark());
      c.gridx++;
      jp.add(tf, c);

      c.gridx++;
      JButton btn = new JButton("add");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent a) {
          GeneralContext.getInstance().setDirty();
          content.addEmptyExecutionStatement();
          GeneralContext.getInstance().setCodeDirty(true);
          refresh(CellTreeNode.this);
        }
      });

      jp.add(btn, c);
    }
  }

  //
  private Component createRemoveButton(DomainType type, String id) {
    JButton btn = new JButton("remove");
    btn.setName(id);
    btn.setBackground(info.getLight());
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        int index = Integer.parseInt(b.getName())-1;
        switch (type) {
          case ACTIVATION:
            content.removeActivationExpression(index);
            break;
          case DEACTIVATION:
            content.removeDeactivationExpression(index);
            break;
          case EXECUTION:
            content.removeExecutionCommand(index);
            break;
        }
        GeneralContext.getInstance().setCodeDirty(true);
        refresh(CellTreeNode.this);
      }
    });
    return btn;
  }

  @Override
  public String getIcon() {
    return null;
  }


  @Override
  public Color getBackground() {
    return null;
  }


  @Override
  public String getToolTipText() {
    return "Cell";
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public JMenu addChildContextMenu() {
    return null;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    return null;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return false;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
    ((Heap) parent.getAssociatedObject()).removeCell(cell);
    ((Heap) newParent.getAssociatedObject()).addCell(cell);
    cell.setBlock((Heap) newParent.getAssociatedObject());
    cell.setHeapName(null);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
    cell.setBlock(null);
    cell.setHeapName(parent.getName());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (updateField((JTextField) e.getSource())) {
      GeneralContext.getInstance().setCodeDirty(true);
      GeneralContext.getInstance().getClappEditor().enableExport();
    }
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
    if (updateField((JTextField) e.getSource())) {
      GeneralContext.getInstance().setCodeDirty(true);
      GeneralContext.getInstance().getClappEditor().enableExport();
    }
  }

  //
  private boolean updateField(JTextField field) {
    if ("name".equals(field.getName())) {
      boolean isRename = !getName().equals(field.getText());
      setName(field.getText());
      ((Cell)getAssociatedObject()).setName(getName());
      super.updateNode(isRename, CellTreeNode.this);
      return true;
    }
    String[] st = field.getName().split("_");
    DomainType type = DomainType.valueOf(st[0]);
    int nb = Integer.parseInt(st[2])-1;
    ArrayList<Statement> stmts = null;
    switch (type) {
      case ACTIVATION:
        stmts = content.getAconditions();
        break;
      case EXECUTION:
        stmts = content.getStatements();
        break;
      case DEACTIVATION:
        stmts = content.getDconditions();
        break;

      default:
        break;
    }
    boolean isdirty = false;
    if (stmts != null) {
      Statement element = stmts.get(nb);
      switch(st[1]) {
        case "level":
          try {
            Integer.parseInt(field.getText());
          }
          catch(NumberFormatException e) {
            field.setText("");
          }
          if (!element.getLevel().equals(field.getText())) {
            element.setLevel(field.getText());
            isdirty = true;
          }
          break;
        case "stat":
          if (element.getStatement() == null || !element.getStatement().equals(field.getText())) {
            element.setStatement(field.getText());
            isdirty = true;
          }
          break;
        case "next":
          try {
            Integer.parseInt(field.getText());
          }
          catch(NumberFormatException e) {
            field.setText("");
          }
          if (!element.getNext().equals(field.getText())) {
            element.setNext(field.getText());
            isdirty = true;
          }
          break;

        default:
          break;
      }
    }
    return isdirty;
  }

  /**
   * @return the isFromGraphics
   */
  public boolean isFromGraphics() {
    return isFromGraphics;
  }

  /**
   * @param isFromGraphics the isFromGraphics to set
   */
  public void setFromGraphics(boolean isFromGraphics) {
    this.isFromGraphics = isFromGraphics;
  }

  /**
   * @return the cell
   */
  public Cell getCell() {
    return cell;
  }

  @Override
  public String getSource() {
    return "cell " + getName() + " {" + getAdSource() + getDdSource() + getXdSource() + " }";
  }

  @Override
  public String getUnassignedSource() {
    return getSource();
  }

  //
  private String getAdSource() {
    if (content.getAconditions().isEmpty()) {
      return "";
    }
    String src = " AD {";
    for (Statement stmt : content.getAconditions()) {
      String text = stmt.getStatement();
      if (text != null && !text.isBlank()) {
        src += " " + (stmt.getLevel().isBlank() ? "" : stmt.getLevel()+":") + stmt.getStatement() +
            (stmt.getNext().isBlank() ? "" : ":"+stmt.getNext()) + ";";
      }
    }
    return src + " }";
  }

  //
  private String getDdSource() {
    if (content.getDconditions().isEmpty()) {
      return "";
    }
    String src = " DD {";
    for (Statement stmt : content.getDconditions()) {
      String text = stmt.getStatement();
      if (text != null && !text.isBlank()) {
        src += " " + (stmt.getLevel().isBlank() ? "" : stmt.getLevel()+":") + stmt.getStatement() +
            (stmt.getNext().isBlank() ? "" : ":"+stmt.getNext()) + ";";
      }
    }
    return src + " }";
  }

  //
  private String getXdSource() {
    if (content.getStatements().isEmpty()) {
      return "";
    }
    String src = " XD {";
    for (Statement stmt : content.getStatements()) {
      String text = stmt.getStatement();
      if (text != null && !text.isBlank()) {
        src += " " + (stmt.getLevel().isBlank() ? "" : stmt.getLevel()+":") + stmt.getStatement() +
            (stmt.getNext().isBlank() ? "" : ":"+stmt.getNext()) + ";";
      }
    }
    return src + " }";
  }
}
