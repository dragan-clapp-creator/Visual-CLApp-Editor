
package clp.edit.tree.node;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.edit.tree.node.ResourcesTreeNode.ResourcesType;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.ResourcesContent.CommonVariable;
import clp.edit.tree.node.util.ResourcesContent.GraphVariable;
import clp.edit.tree.node.util.ResourcesContent.UiVariable;
import clp.edit.tree.node.util.ResourcesContent.WeaveVariable;
import clp.edit.tree.node.util.ResourcesContent.WebVariable;
import clp.edit.tree.node.util.SettingsContent;
import clp.edit.util.ColorSet;
import clp.run.res.Setter;

public class SetterTreeNode extends ATreeNode implements ActionListener,FocusListener {


  private static final long serialVersionUID = 1723192103320363202L;

  private Setter set;
  private String resName;

  private ColorSet info;  // this object's context

  private SettingsContent content;

  public SetterTreeNode(Setter s, String name, File ref, String source) {
    super(name, source, ref);
    this.set = s;
    this.set.setRes(name);
    this.resName = name;
    this.info = ColorSet.SetterProperties;
    initializeContext(set);
  }

  //
  private void initializeContext(Setter set) {
    content = new SettingsContent();
    content.setSettings(set.getSettings());
  }

  @Override
  public Object getAssociatedObject() {
    return set;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Setter for " + resName;
  }

  /**
   * @return the resName
   */
  public String getResName() {
    return resName;
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public void setProperties(JPanel panel) {
    JPanel jp1 = new JPanel();
    jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
    jp1.add(new JLabel("Setter for: "));
    jp1.add( createField("name", getName()) );
    jp1.add(Box.createHorizontalStrut(500));

    JPanel jp2 = new JPanel();
    jp2.setLayout(new BoxLayout(jp2, BoxLayout.Y_AXIS));
    jp2.add(createVariablesPanel());
    jp2.add(createWebVariablePanel());
    jp2.add(createWeaveVariablePanel());
    jp2.add(createAKDLGraphPanel());
    jp2.add(createUIVariablePanel());

    panel.add(jp1);
    panel.add(jp2);
  }

  //
  private JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(8);
    field.setText(text);
    field.setBackground(info.getLight());
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  //
  private JPanel createVariablesPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Variables"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillVariablesHeader(jp, c);

    fillVariables(jp, c);

    return jp;
  }

  //
  protected void fillVariablesHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("type");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("array");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("initial");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
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
        refresh(SetterTreeNode.this);
      }
    });

    jp.add(btn, c);
  }

  //
  protected void fillVariables(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    JTextField tf;
    for (CommonVariable var : content.getSimpleVariables()) {
      tf = new JTextField(var.getType().getVal());
      tf.setForeground(info.getDark());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
   
      JCheckBox cb = new JCheckBox();
      cb.setSelected(var.isArray());
      cb.setForeground(info.getDark());
      cb.setBackground(info.getLight());
      c.gridx = 1;
      jp.add(cb, c);

      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 2;
      jp.add(tf, c);
   
      tf = new JTextField(var.getInitial() == null ? "" :var.getInitial().toString());
      tf.setBackground(info.getLight());
      c.gridx = 3;
      jp.add(tf, c);

      c.gridx = 4;
      jp.add(createRemoveButton(ResourcesType.VARIABLES, ""+c.gridy), c);

      c.gridy++;
    }
  }

  //
  private JButton createRemoveButton(ResourcesType type, String id) {
    JButton btn = new JButton("remove");
    btn.setName(id);
    btn.setBackground(info.getLight());
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        int index = Integer.parseInt(b.getName())-1;
        content.removeVariable(index);
        GeneralContext.getInstance().setDirty();
        refresh(SetterTreeNode.this);
      }
    });
    return btn;
  }

  //
  private JPanel createWebVariablePanel() {
    final JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Web Variables"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillWebHeader(jp, c);

    fillWebVariablePanel(jp, c);

    return jp;
  }

  //
  protected void fillWebHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("Encryption");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("Address");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("Port");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
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
        refresh(SetterTreeNode.this);
      }
    });

    jp.add(btn, c);
  }

  //
  protected JPanel fillWebVariablePanel(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    JTextField tf;
    for (WebVariable var : content.getWebVariables()) {
      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
   
      tf = new JTextField(var.getEncryption());
      tf.setBackground(info.getLight());
      c.gridx = 1;
      jp.add(tf, c);

      tf = new JTextField(var.getAddress());
      tf.setBackground(info.getLight());
      c.gridx = 2;
      jp.add(tf, c);

      tf = new JTextField(""+var.getPort());
      tf.setBackground(info.getLight());
      c.gridx = 3;
      jp.add(tf, c);

      c.gridx = 4;
      jp.add(createRemoveButton(ResourcesType.WEB, ""+c.gridy), c);

      c.gridy++;
    }
    return jp;
  }

  //
  private JPanel createWeaveVariablePanel() {
    final JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Weavers (Bytecode Injection)"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillWeaverHeader(jp, c);

    fillWeaverVariablePanel(jp, c);

    return jp;
  }

  //
  protected void fillWeaverHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("on class");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("definition");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);

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
        refresh(SetterTreeNode.this);
      }
    });

    jp.add(btn, c);
  }

  //
  protected JPanel fillWeaverVariablePanel(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    JTextField tf;
    for (WeaveVariable var : content.getWeaveVariables()) {
      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
   
      tf = new JTextField(var.getPath());
      tf.setBackground(info.getLight());
      c.gridx = 1;
      jp.add(tf, c);

      JTextArea ta = new JTextArea(var.getSentences());
      ta.setBackground(info.getLight());
      c.gridx = 2;
      jp.add(ta, c);

      c.gridx = 3;
      jp.add(createRemoveButton(ResourcesType.WEAVING, ""+c.gridy), c);

      c.gridy++;
    }
    return jp;
  }

  //
  private JPanel createAKDLGraphPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "AKDL Graph"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillGraphHeader(jp, c);

    fillGraphVariablePanel(jp, c);

    return jp;
  }

  //
  protected void fillGraphHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("definition");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
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
        refresh(SetterTreeNode.this);
      }
    });

    jp.add(btn, c);
  }

  //
  protected JPanel fillGraphVariablePanel(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    for (GraphVariable var : content.getGraphVariables()) {
      JTextField tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
   
      JTextArea ta = new JTextArea(var.getSentences());
      ta.setBackground(info.getLight());
      c.gridx = 1;
      jp.add(ta, c);

      c.gridx = 4;
      jp.add(createRemoveButton(ResourcesType.GRAPH, ""+c.gridy), c);

      c.gridy++;
    }
    return jp;
  }

  //
  private JPanel createUIVariablePanel() {
    final JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "UI Variables"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillUIHeader(jp, c);

    fillUIVariablePanel(jp, c);

    return jp;
  }

  //
  protected void fillUIHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("title");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("definition");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);

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
        refresh(SetterTreeNode.this);
      }
    });

    jp.add(btn, c);
  }

  //
  protected JPanel fillUIVariablePanel(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    JTextField tf;
    for (UiVariable var : content.getUIVariables()) {
      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
   
      tf = new JTextField(var.getTitle());
      tf.setBackground(info.getLight());
      c.gridx++;
      jp.add(tf, c);

      JTextArea ta = new JTextArea(var.getSentences());
      ta.setBackground(info.getLight());
      c.gridx++;
      jp.add(ta, c);

      c.gridx++;
      jp.add(createRemoveButton(ResourcesType.UI, ""+c.gridy), c);

      c.gridy++;
    }
    return jp;
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
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
    set.setRes(parent.getName());
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
    updateField((JTextField) e.getSource());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateField((JTextField) e.getSource());
  }

  //
  private void updateField(JTextField field) {
    boolean isRename = false;
    if ("name".equals(field.getName())) {
      isRename = !getName().equals(field.getText());
      setName(field.getText());
      ((Setter)getAssociatedObject()).setRes(getName());
    }
    super.updateNode(isRename, SetterTreeNode.this);
  }

  @Override
  public String getSource() {
    String src = "set " + getName() + " {\n";
    for (GraphVariable var : content.getGraphVariables()) {
      src += "        GRAPH   " + var.getName() + " = " + var.getSentences() + ";\n";
    }
    for (CommonVariable var : content.getSimpleVariables()) {
      src += "        " + var.getType().getVal() + "   " + var.getName() + " = " + var.getInitial() + ";\n";
    }
    for (UiVariable var : content.getUIVariables()) {
      src += "        UI   " + var.getName() + " = " + var.getSentences() + ";\n";
    }
    for (WeaveVariable var : content.getWeaveVariables()) {
      src += "        WEAVER   " + var.getName() + " = " + var.getSentences() + ";\n";
    }
    for (WebVariable var : content.getWebVariables()) {
      src += "        WEB   " + var.getName() + " = " + var.getPort() + ";\n";
    }
    return src + "}";
  }

  @Override
  public String getUnassignedSource() {
    return getSource();
  }

  /**
   * @return the info
   */
  public synchronized ColorSet getInfo() {
    return info;
  }
}
