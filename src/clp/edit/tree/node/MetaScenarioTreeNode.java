package clp.edit.tree.node;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import clapp.cmp.ClappMain;
import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.graphics.code.prt.ConsHandlerDialog;
import clp.edit.graphics.code.prt.CslContext;
import clp.edit.graphics.code.prt.CslContext.CslInfo;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.MetaScenarioContent;
import clp.edit.util.ColorSet;
import clp.run.msc.MetaScenario;
import clp.run.msc.MetaScenarioBody;
import clp.run.msc.MscTaskName;
import clp.run.msc.MscTasks;
import clp.run.msc.Out;
import clp.run.msc.Output;
import clp.run.msc.OutputTarget;
import clp.run.msc.Port;
import clp.run.res.Binpath;
import clp.run.res.Jarpath;
import clp.run.res.Resources;
import clp.run.res.UsedJava;
import clp.run.res.UsedJavaVisitor;

public class MetaScenarioTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = 7360155651230176296L;

  private MetaScenario msc;

  private ColorSet info;  // this object's context
  private MetaScenarioContent content;

  private JTextField portField;
  private JButton decrypt;

  transient private ClappMain clapp;

  private CslContext ccontext;
  private ArrayList<String> cslList;
  private JPanel container;

  private boolean isCreated;

  /**
   * constructor
   * 
   * @param name
   * @param msc
   * @param ref
   * @param source
   */
  public MetaScenarioTreeNode(String name, MetaScenario msc, File ref, String source) {
    super(name, source, ref);
    this.msc = msc;
    this.msc.setName(name);
    info = ColorSet.MetaScenarioProperties;
    initializeContext(msc);
  }

  //
  private void initializeContext(MetaScenario msc) {
    content = new MetaScenarioContent();
    cslList = new ArrayList<>();
    ccontext = new CslContext();
    if (msc == null) {
      ArrayList<MscTaskName> list = new ArrayList<>();
      list.add(MscTaskName.SCHEDULER);
      content.setTasks(list); // default
    }
    else {
      MetaScenarioBody body = msc.getMetaScenarioBody();
      if (body == null) {
        body = new MetaScenarioBody();
        body.setMscTasks(new MscTasks());
      }
      Port p = body.getPort();
      if (p != null) {
        content.setPort(p.getNum());
        if (p.isDecryption()) {
          content.setDecryption(this, p.getDecryption());
        }
        content.setIsChecked(true);
      }
      ArrayList<MscTaskName> tasks = body.getMscTasks().getMscTaskNames();
      content.setTasks(tasks);
      if (body.isMscOutput()) {
        Output out = body.getMscOutput().getOutput();
        ArrayList<Output> outputs = new ArrayList<>();
        outputs.add(out);
        outputs.addAll(body.getMscOutput().getOutputs());
        for (Output o : outputs) {
          String n = getOutName(o.getOutputTarget());
          ccontext.addCslInfo(n, o);
          cslList.add(n);
        }
      }
    }
  }

  //
  private String getOutName(OutputTarget outputTarget) {
    if (outputTarget.isStringCONSOLE()) {
      return outputTarget.getStringCONSOLE();
    }
    return outputTarget.getName();
  }

  @Override
  public Object getAssociatedObject() {
    return msc;
  }

  @Override
  public void setProperties(JPanel panel) {
    if (!isCreated || panel.getComponentCount() == 0) {
      JPanel jp1 = new JPanel();
      jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
      jp1.add(new JLabel("Name: "));
      jp1.add( createField("name", getName()) );
      jp1.add(Box.createHorizontalStrut(500));
      panel.add(jp1);

      JPanel jp2 = new JPanel();
      jp2.setLayout(new BoxLayout(jp2, BoxLayout.X_AXIS));
      jp2.add(createTasksPanel());
      jp2.add(createPortPanel());
      jp2.add(createOutputPanel());
      panel.add(jp2);

      container = new JPanel();
      container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
      panel.add(container);

      isCreated = true;
    }
    if (!ccontext.getCslList().isEmpty()) {
      container.removeAll();
      JPanel jp3 = new JPanel();
      jp3.setLayout(new BoxLayout(jp3, BoxLayout.X_AXIS));
      jp3.add(createOutputFillingPanel());
      container.add(jp3);
    }
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
  private JPanel createTasksPanel() {
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
 
    tf = new JTextField("deletion");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 1;
    jp.add(tf, c);

    JButton tadd = new JButton("add");
    tadd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        GeneralContext.getInstance().setDirty();
        content.addDefaultTask();
        refresh(MetaScenarioTreeNode.this);
      }
    });
    c.gridx = 2;
    jp.add(tadd, c);
  }

  //
  protected void fillTasks(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    for (MscTaskName t : content.getTasks()) {
      JComboBox<String> cmb = new JComboBox<>();
      for (MscTaskName tn : MscTaskName.values()) {
        cmb.addItem(tn.name());
      }
      cmb.setSelectedItem(t.name());
      cmb.setName(""+c.gridy);
      cmb.setBackground(info.getLight());
      cmb.addActionListener(new ActionListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
          JComboBox<String> box = (JComboBox<String>) e.getSource();
          int index = Integer.parseInt(box.getName())-1;
          content.setTaskNameAt(index, MscTaskName.valueOf((String)box.getSelectedItem()));
        }
      });
      c.gridx = 0;
      jp.add(cmb, c);

      JButton btn = new JButton("remove");
      btn.setName(""+c.gridy);
      btn.setBackground(info.getLight());
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JButton b = (JButton) e.getSource();
          int index = Integer.parseInt(b.getName())-1;
          content.removeTask(index);
          GeneralContext.getInstance().setDirty();
          refresh(MetaScenarioTreeNode.this);
        }
      });
      c.gridx = 1;
      jp.add(btn, c);

      c.gridy++;
    }
  }

  //
  private JPanel createPortPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Port"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    JCheckBox port = new JCheckBox("port");
    port.setSelected(content.getIsChecked());
    port.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        if (((JCheckBox)a.getSource()).isSelected()) {
          portField.setEditable(true);
          decrypt.setEnabled(true);
          content.setIsChecked(true);
        }
        else {
          portField.setEditable(false);
          decrypt.setEnabled(false);
          portField.setText("");
          updateField(portField);
          content.setIsChecked(false);
        }
      }
    });
    jp.add(port, c);

    c.gridx = 1;
    portField = new JTextField(8);
    portField.setName("port");
    portField.setEditable(content.getIsChecked());
    portField.setText(content.getPort());
    portField.setBackground(info.getLight());
    portField.addFocusListener(this);
    portField.addActionListener(this);
    jp.add(portField, c);

    c.gridx = 2;
    decrypt = new JButton();
    decrypt.setEnabled(content.getIsChecked());
    decrypt.setText(content.getDclass() == null ? "Decryption File..." : content.getDclass());
    decrypt.setBackground(info.getLight());
    decrypt.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Choose a .class file", "class"));
        int result = chooser.showOpenDialog(GeneralContext.getInstance().getClappPanel());
        if (result == JFileChooser.APPROVE_OPTION) {
          String file = chooser.getSelectedFile().getAbsolutePath();
          content.setDecrypt(file);
          decrypt.setText(content.getDclass());
        }
      }
    });
    jp.add(decrypt, c);
    return jp;
  }

  //
  private JPanel createOutputPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Output"));

    JButton cslButton = new JButton("CSL");
    cslButton.setToolTipText("Define Output Console");
    jp.add(cslButton);
    cslButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConsHandlerDialog dial = new ConsHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(cslList), ccontext);
        if (dial.isOk()) {
          cslList = (ArrayList<String>) dial.getConsolesList();
          GeneralContext.getInstance().setCodeDirty(true);
          refresh();
        }
      }
    });
    jp.add(cslButton);

    return jp;
  }

  //
  private JPanel createOutputFillingPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Output"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillOutputHeader(jp, c);

    fillOutputs(jp, c);

    return jp;
  }

  //
  private void fillOutputHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JLabel lbl = new JLabel(" console name ");
    lbl.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(lbl, c);

    lbl = new JLabel(" foreground color ");
    lbl.setForeground(info.getDark());
    c.gridx++;
    jp.add(lbl, c);

    lbl = new JLabel(" background color ");
    lbl.setForeground(info.getDark());
    c.gridx++;
    jp.add(lbl, c);

    lbl = new JLabel(" output kind");
    lbl.setForeground(info.getDark());
    c.gridx++;
    jp.add(lbl, c);
  }

  //
  private void fillOutputs(JPanel jp, GridBagConstraints c) {
    c.gridy = 1;
    for (CslInfo cinfo : getCcontext().getCslInfos().values()) {
      OutputTarget target = cinfo.getOut().getOutputTarget();
      JTextField tf = new JTextField(target.isStringCONSOLE() ?
          target.getStringCONSOLE() : target.getName());
      tf.setBackground(Color.white);
      tf.setEnabled(false);
      c.gridx = 0;
      jp.add(tf, c);

      tf = new JTextField(cinfo.getOut().getColor());
      tf.setBackground(Color.white);
      tf.setEnabled(false);
      c.gridx++;
      jp.add(tf, c);

      tf = new JTextField(cinfo.getOut().getBackground());
      tf.setBackground(Color.white);
      tf.setEnabled(false);
      c.gridx++;
      jp.add(tf, c);

      if (cinfo.getOut().isOut()) {
        tf = new JTextField(cinfo.getOut().getOut().getVal());
        tf.setBackground(Color.white);
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);
      }

      c.gridy++;
    }
  }

  @Override
  public String getIcon() {
    if (GeneralContext.getInstance().isRuntimeTriggered()) {
      if (getFileReference() == null) {
        // error msg
      }
      else {
        return "red";
      }
    }
    return getFileReference() == null ? null : "black";
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Meta-Scenario";
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public JMenu addChildContextMenu() {
    JMenu menu = new JMenu("add");
    menu.add(PopupContext.getInstance().createSubItem(this, "a SCENARIO", Action.INSERT, Argument.SCN));
    menu.add(PopupContext.getInstance().createSubItem(this, "a RESOURCES block", Action.INSERT, Argument.RES));
    menu.add(PopupContext.getInstance().createSubItem(this, "a Grep", Action.INSERT, Argument.GREP));
    return menu;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    return null;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return candidate instanceof ScenarioTreeNode;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
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
        ((MetaScenario)getAssociatedObject()).setName(getName());
        GeneralContext.getInstance().setCodeDirty(true);
      }
    }
    else if ("port".equals(field.getName())) {
      int nport;
      try {
        nport = Integer.parseInt( portField.getText() );
      }
      catch (NumberFormatException ne) {
        nport = 0;
        portField.setText("");
      }
      if (!portField.getText().equals(content.getPort())) {
        content.setPort(nport);
        GeneralContext.getInstance().setCodeDirty(true);
      }
    }
    super.updateNode(isRename, MetaScenarioTreeNode.this);
  }

  @Override
  public String getSource() {
    return "metaScenario " + getName() + "{" + getMetaProperties() + getChildrenSource() + "}";
  }

  @Override
  public String getUnassignedSource() {
    return getSource();
  }

  public String getMetaProperties() {
    return
        " properties {" +
        "   tasks {" + getTasks() +
        "   }" +
        (content.getPort() == null ? "" : "port " + content.getPort() + (content.getDclass() == null ? "" : content.getDecrypt()) + ";\n") +
        declareOutputs(ccontext.getCslInfos()) +
        "}";
  }

  //
  private String declareOutputs(Hashtable<String, CslInfo> hashtable) {
    if (hashtable.isEmpty()) {
      return "";
    }
    String s = "output : \n";
    int i = 0;
    for (String name : hashtable.keySet()) {
      CslInfo cinfo = hashtable.get(name);
      Out out = cinfo.getOut().getOut();
      s += "     " + name + " " + (out == null ? "" : out.getVal())
          + " \"" + cinfo.getOut().getColor()+"\"/\"" + cinfo.getOut().getBackground() + "\"";
      if (i < hashtable.size()-1) {
        s += ",\n";
      }
      i++;
    }
    return s + ";\n";
  }

  //
  private String getTasks() {
    String src = "";
    for (MscTaskName t : content.getTasks()) {
      src += " " + t.getVal() + ";\n";
    }
    return src;
  }

  //
  private String getChildrenSource() {
    if (getChildCount() == 0) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<getChildCount(); i++) {
      sb.append(((ATreeNode) getChildAt(i)).getSource());
      sb.append("\n");
    }
    return sb.toString();
  }

  public ClappMain getClappMain() {
    return this.clapp;
  }

  public void setClappMain(ClappMain clapp) {
    this.clapp = clapp;
  }

  public String getPort() {
    return content.getPort();
  }

  /**
   * @return the info
   */
  public synchronized ColorSet getInfo() {
    return info;
  }

  /**
   * @return the ccontext
   */
  public CslContext getCcontext() {
    return ccontext;
  }

  public String findPath(String dpack, String dclass) {
    String fileName = dclass + ".class";
    String dpath = dpack.replace(".", "/") + "/";
    for (Resources res : msc.getMetaScenarioBody().getResourcess()) {
      ArrayList<UsedJava> list = new ArrayList<>();
      list.add(res.getUsedLib().getUsedJava());
      list.addAll(res.getUsedLib().getUsedJavas());
      for (UsedJava java : list) {
        MyUsedJavaVisitor vis = new MyUsedJavaVisitor(dpath, fileName);
        java.accept(vis);
        if (vis.getDir() != null) {
          return vis.getDir();
        }
      }
    }
    return null;
  }

  static class MyUsedJavaVisitor implements UsedJavaVisitor {
    private String dir;
    private String fullName;
    public MyUsedJavaVisitor(String path, String clss) {
      fullName = path+clss;
    }
    @Override
    public void visitBinpath(Binpath arg0) {
      String str = arg0.getDir();
      File f = new File(str+"/"+fullName);
      if (f.exists()) {
        dir = str;
      }
    }
    @Override
    public void visitJarpath(Jarpath arg0) {
      // TODO: search whether file is in arg0.getJar();
    }
    private String getDir() {
      return dir;
    }
  }
}
