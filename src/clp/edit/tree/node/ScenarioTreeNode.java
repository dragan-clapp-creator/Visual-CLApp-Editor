package clp.edit.tree.node;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.ScenarioContent;
import clp.edit.util.ColorSet;
import clp.run.msc.MetaScenario;
import clp.run.scn.DeactType;
import clp.run.scn.Ltype;
import clp.run.scn.Scenario;
import clp.run.scn.ScnLogBody;
import clp.run.scn.ScnPropBody;
import clp.run.scn.ScnQueue;
import clp.run.scn.ScnTask;
import clp.run.scn.ScnTaskName;
import clp.run.scn.SortOrder;

public class ScenarioTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = 5886882591717361631L;

  private Scenario scn;

  private ColorSet info;  // this object's context
  private ScenarioContent content;

  transient private JComboBox<String> locombo;


  public ScenarioTreeNode(String name, Scenario scn, File ref, String source) {
    super(name, source, ref);
    this.scn = scn;
    this.scn.setName(name);
    this.info = ColorSet.ScenarioProperties;
    initializeContext(scn);
  }

  //
  private void initializeContext(Scenario scn) {
    content = new ScenarioContent(scn != null && scn.getMetaScenario() != null);
    if (scn == null) {
      content.setLogicLevel(2);
      content.setDeactivation(DeactType.AUTO);
    }
    else {
      ScnPropBody body = scn.getScenarioBody().getScnPropBody();
      ScnLogBody logic = body.getScnLogic().getScnLogBody();
      content.setLogicLevel(logic.getScnLevel().getLevel());
      content.setDeactivation(logic.getScnDeact().getDeactType());
      content.setType(logic.isScnLtype() ? logic.getScnLtype().getLtype() : null);
      content.setQueues(body.getScnQueues().getScnQueues());
      content.setTasks(body.getScnTasks().getScnTasks());
    }
  }

  @Override
  public Object getAssociatedObject() {
    return scn;
  }

  @Override
  public void setProperties(JPanel panel) {
    JPanel jp1 = new JPanel();
    jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
    jp1.add(new JLabel("Name: "));
    jp1.add( createField("name", getName()) );
    if (!content.isAssigned()) {
      JLabel lbl = new JLabel("assigned to ");
      lbl.setBackground(info.getLight());
      jp1.add(lbl);
      jp1.add( createField("assign", scn.getMsc()) );
    }
    jp1.add(Box.createHorizontalStrut(500));

    JPanel jp2 = new JPanel();
    jp2.setLayout(new BoxLayout(jp2, BoxLayout.Y_AXIS));
    jp2.add(createQueuingPanel());
    jp2.add(createTasksPanel());
    jp2.add(createLogicPanel());

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
  private JPanel createQueuingPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Queuing"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillQueuesHeader(jp, c);

    fillQueues(jp, c);

    return jp;
  }

  //
  protected void fillQueuesHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("queue name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("income");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 1;
    jp.add(tf, c);
 
    tf = new JTextField("deletion");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 2;
    jp.add(tf, c);

    c.gridx = 3;
    JButton qadd = new JButton("add");
    qadd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        GeneralContext.getInstance().setDirty();
        content.addEmptyQueue();
        refresh(ScenarioTreeNode.this);
      }
    });

    jp.add(qadd, c);
  }

  //
  protected void fillQueues(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    for (ScnQueue q : content.getQueues()) {
      c.gridx = 0;
      jp.add(createQueueTextField(q), c);
   
      JComboBox<String> cmb = new JComboBox<>();
      for (SortOrder so : SortOrder.values()) {
        cmb.addItem(so.name());
      }
      cmb.setSelectedItem(q.getSortOrder().name());
      cmb.setBackground(info.getLight());
      cmb.addActionListener(new ActionListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
          JComboBox<String> box = (JComboBox<String>) e.getSource();
          q.setSortOrder(SortOrder.valueOf((String)box.getSelectedItem()));
        }
      });
      c.gridx = 1;
      jp.add(cmb, c);

      c.gridx = 2;
      jp.add(createRemoveButton(false, ""+c.gridy), c);

      c.gridy++;
    }
  }

  //
  protected JTextField createQueueTextField(ScnQueue q) {
    JTextField tf = new JTextField(4);
    tf.setText(q.getName());
    tf.setBackground(info.getLight());
    tf.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JTextField f = (JTextField) e.getSource();
        q.setName(f.getText());
      }
    });
    tf.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        JTextField f = (JTextField) e.getSource();
        q.setName(f.getText());
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    return tf;
  }

  //
  private Component createTasksPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Tasks"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillTasksHeader(jp, c);

    fillTasks(jp, c);

    return jp;
  }

  //
  protected void fillTasksHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("task name ");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("operating on ");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 1;
    jp.add(tf, c);
 
    tf = new JTextField("passing to ");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 2;
    jp.add(tf, c);
    
    tf = new JTextField("deletion");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 3;
    jp.add(tf, c);

    JButton tadd = new JButton("add");
    tadd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        GeneralContext.getInstance().setDirty();
        content.addEmptyTask();
        refresh(ScenarioTreeNode.this);
      }
    });
    c.gridx = 4;
    jp.add(tadd, c);
  }

  //
  protected void fillTasks(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    for (ScnTask t : content.getTasks()) {
      JComboBox<String> cmb = new JComboBox<>();
      for (ScnTaskName tn : ScnTaskName.values()) {
        cmb.addItem(tn.name());
      }
      cmb.setSelectedItem(t.getScnTaskName().name());
      cmb.setBackground(info.getLight());
      cmb.addActionListener(new ActionListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
          JComboBox<String> box = (JComboBox<String>) e.getSource();
          t.setScnTaskName(ScnTaskName.valueOf((String)box.getSelectedItem()));
        }
      });
      c.gridx = 0;
      jp.add(cmb, c);
   
      c.gridx = 1;
      jp.add(createTaskTextField(t, true), c);
      
      c.gridx = 2;
      jp.add(createTaskTextField(t, false), c);

      c.gridx = 3;
      jp.add(createRemoveButton(true, ""+c.gridy), c);

      c.gridy++;
    }
  }

  //
  private JButton createRemoveButton(boolean isTask, String id) {
    JButton btn = new JButton("remove");
    btn.setName(id);
    btn.setBackground(info.getLight());
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        int index = Integer.parseInt(b.getName())-1;
        if (isTask) {
          content.removeTask(index);
        }
        else {
          content.removeQueue(index);
        }
        GeneralContext.getInstance().setDirty();
        refresh(ScenarioTreeNode.this);
      }
    });
    return btn;
  }

  //
  protected Component createTaskTextField(ScnTask t, boolean isOperOn) {
    JTextField tf = new JTextField(4);
    tf.setText(isOperOn ? t.getOperOn() : t.getPassTo());
    tf.setBackground(info.getLight());
    tf.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JTextField f = (JTextField) e.getSource();
        if (isOperOn) {
          t.setOperOn(f.getText());
        }
        else {
          t.setIsPassingTo(true);
          t.setPassTo(f.getText());
        }
      }
    });
    tf.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        JTextField f = (JTextField) e.getSource();
        if (isOperOn) {
          t.setOperOn(f.getText());
        }
        else {
          t.setIsPassingTo(true);
          t.setPassTo(f.getText());
        }
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    return tf;
  }

  //
  private Component createLogicPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Logic"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    jp.add(createDeactType(), c);
    c.gridy = 1;
    jp.add(createLogic(), c);

    return jp;
  }

  //
  private void updateLogic(JTextField lf) {
    int logic;
    try {
      logic = Integer.parseInt( lf.getText() );
    }
    catch (NumberFormatException ne) {
      logic = 2;
      lf.setText("2");
    }
    content.setLogicLevel(logic);
    locombo.setVisible(logic > 2);
    GeneralContext.getInstance().setCodeDirty(true);
  }

  //
  protected JPanel createDeactType() {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout(1, 3));

    DeactType sel = content.getDeactivation();
    JLabel lbl = new JLabel(sel == DeactType.AUTO ? "automatic deactivation " : "deactivation through DEACTIVATOR ");
    jp.add(lbl);

    JComboBox<String> cmb = new JComboBox<>();
    for (DeactType dt : DeactType.values()) {
      cmb.addItem(dt.name());
    }
    cmb.setSelectedItem(sel.name());
    cmb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String name = (String) ((JComboBox<?>)e.getSource()).getSelectedItem();
        content.setDeactivation(DeactType.valueOf(name));
        refresh(ScenarioTreeNode.this);
      }
    });
    jp.add(cmb);

    return jp;
  }

  //
  private JPanel createLogic() {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout(1, 3));

    JLabel lbl = new JLabel("logic level ");
    jp.add(lbl);

    JTextField lfield = new JTextField(3);
    lfield.setText(""+content.getLogicLevel());
    lfield.setBackground(info.getLight());
    lfield.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        updateLogic((JTextField)e.getSource());
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    lfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateLogic((JTextField)e.getSource());
      }
    });
    jp.add(lfield);

    Ltype sel = content.getType();
    int logic = content.getLogicLevel();
    locombo = new JComboBox<>();
    for (Ltype lt : Ltype.values()) {
      locombo.addItem(lt.name());
    }
    if (sel != null) {
      locombo.setSelectedItem(sel.name());
    }
    locombo.setVisible(logic > 2);
    locombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String name = (String) ((JComboBox<?>)e.getSource()).getSelectedItem();
        content.setType(Ltype.valueOf(name));
      }
    });
    jp.add(locombo);

    return jp;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Scenario";
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public JMenu addChildContextMenu() {
    JMenu menu = new JMenu("add");
    menu.add(PopupContext.getInstance().createSubItem(this, "an ACTOR", Action.INSERT, Argument.ACT));
    menu.add(PopupContext.getInstance().createSubItem(this, "a Grep", Action.INSERT, Argument.GREP));
    return menu;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    if (tparent instanceof MetaScenarioTreeNode) {
      return null;
    }
    JMenu menu = new JMenu("wrap with");
    menu.add(PopupContext.getInstance().createSubItem(this, "a File", Action.WRAP, Argument.FILE));
    menu.add(PopupContext.getInstance().createSubItem(this, "a META-SCENARIO", Action.WRAP, Argument.MSC));
    return menu;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return candidate instanceof ActorTreeNode;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
    scn.setMetaScenario((MetaScenario) newParent.getAssociatedObject());
    scn.setMsc(null);
    content.setAssigned(true);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
    scn.setMsc(parent.getName());
    scn.setMetaScenario(null);
    content.setAssigned(false);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateField((JTextField) e.getSource());
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
    updateField((JTextField) e.getSource());
  }

  //
  private void updateField(JTextField field) {
    boolean isRename = false;
    if ("name".equals(field.getName())) {
      isRename = !getName().equals(field.getText());
      if (isRename) {
        setName(field.getText());
        ((Scenario)getAssociatedObject()).setName(getName());
        GeneralContext.getInstance().setCodeDirty(true);
      }
    }
    else if (!field.getText().equals(scn.getMsc())) {
      scn.setMsc(field.getText());
      GeneralContext.getInstance().setCodeDirty(true);
    }
    super.updateNode(isRename, ScenarioTreeNode.this);
  }

  @Override
  public String getSource() {
    return "scenario " + getName() + " {" + getScenarioProperties() + getChildrenSource() + " }";
  }

  @Override
  public String getUnassignedSource() {
    return "scenario " + getName() + " assignTo "+ scn.getMsc() + " {\n" + getScenarioProperties() + "\n" + getChildrenSource() + "\n}\n";
  }

  //
  public String getScenarioProperties() {
    return
        "properties {" +
        " logic {" +
        "   deactivation = " + content.getDeactivation().getVal() + ";\n" +
        "   level = " + content.getLogicLevel() + ";\n" +
        " }" +
        " queues { " + getQueues() +
        " }" +
        " tasks { " + getTasks() +
        " }" +
        "}";
  }

  //
  private String getQueues() {
    String src = "";
    for (ScnQueue q : content.getQueues()) {
      src += "          " + q.getName() + " income=" + q.getSortOrder().getVal() + ";\n";
    }
    return src;
  }

  //
  private String getTasks() {
    String src = "";
    for (ScnTask t : content.getTasks()) {
      src += " " + t.getScnTaskName().getVal() + " operatingOn " + t.getOperOn() + (t.isPassingTo() ? " passingTo "+t.getPassTo() : "") + ";\n";
    }
    return src;
  }

  //
  private String getChildrenSource() {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<getChildCount(); i++) {
      sb.append(((ATreeNode) getChildAt(i)).getSource());
    }
    return sb.toString();
  }

  /**
   * @return the info
   */
  public synchronized ColorSet getInfo() {
    return info;
  }
}
