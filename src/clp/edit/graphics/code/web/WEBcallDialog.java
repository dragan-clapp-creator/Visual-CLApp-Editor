package clp.edit.graphics.code.web;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.code.InstructionDialog;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.shapes.ActionShape;

public class WEBcallDialog extends ADialog implements InstructionDialog {

  private static final long serialVersionUID = -5899622018941856509L;

  static enum ItemType { BOOL, INT, FLOAT, STRING, DATE, TIME, EVENT };

  private static class SendComponents {
    private JComboBox<String> typeCombo;
    private JTextField itemField;
    private JTextField valueField;
  }

  public static class SendInfo implements Serializable {
    private static final long serialVersionUID = 5428714112792856134L;
    private String type;
    private String item;
    private String value;
  }

  private GenericActionListener gal;

  private ArrayList<SendInfo> infoList;
  private ArrayList<SendComponents> componentsList;

  private JComboBox<String> refcombo;
  private JCheckBox respCheck;
  private JTextArea respArea;
  private String respValue;

  private Set<String> webNames;

  private ActionShape caller;

  private ClappInstruction instruction;


  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param webNames 
   * @param c caller 
   */
  public WEBcallDialog(Frame parent, Set<String> webNames, ActionShape c) {
    super(parent, "Adding a Send instruction", true);
    this.webNames = webNames;
    this.caller = c;
    setup(parent);
    setVisible(true);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param sendInfos
   * @param webNames 
   * @param inst 
   * @param c caller 
   */
  public WEBcallDialog(Frame parent, ArrayList<SendInfo> sendInfos, Set<String> webNames, ClappInstruction inst, ActionShape c) {
    super(parent, "Adding a Send instruction", true);
    this.infoList = sendInfos;
    this.webNames = webNames;
    this.instruction = inst;
    this.caller = c;
    setup(parent);
  }

  //
  private void setup(Frame owner) {
    if (componentsList == null) {
      componentsList = new ArrayList<>();
    }
    if (infoList != null) {
      for (SendInfo si : infoList) {
        SendComponents sc = new SendComponents();
        sc.itemField = new JTextField(10);
        sc.itemField.setText(si.item);
        sc.valueField = new JTextField(10);
        sc.valueField.setText(retrieveValue(si.value));
        sc.typeCombo = new JComboBox<>(getItemNames());
        sc.typeCombo.setSelectedItem(si.type);
        componentsList.add(sc);
      }
      respValue = caller.getWebRespValue();
      respArea = new JTextArea();
      respArea.setText(respValue);
    }
    getContentPane().removeAll();
    Dimension parentSize = owner.getSize(); 
    Point p = owner.getLocation(); 
    setLocation(p.x + parentSize.width * 2 / 5, p.y + parentSize.height / 4);

    fillContent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  private String retrieveValue(String value) {
    if (value.startsWith("#")) {
      return "";
    }
    if (value.startsWith("\"")) {
      return value.substring(1, value.length()-1);
    }
    return value;
  }

  //
  private void fillContent() {
    setLayout(new SpringLayout());

    getContentPane().add(createItemsHandlingPanel());
    getContentPane().add(createItemsPanel());
    getContentPane().add(createReferencePanel());
    getContentPane().add(createResponsePanel());
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(), 5, 1, 6, 6, 6, 6);
  }

  //
  private JPanel createItemsHandlingPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Items Handling");
    p.setBorder(border);

    int rows = 1;
    if (!componentsList.isEmpty()) {
      JLabel l = new JLabel("Index:", JLabel.TRAILING);
      p.add(l);
      JComboBox<Integer> cb = new JComboBox<>(getItemIndexes());
      cb.setSelectedIndex(0);
      l.setLabelFor(cb);
      p.add(cb);
      JButton btn = new JButton("remove");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          componentsList.remove(cb.getSelectedIndex());
          refresh();
        }
      });
      p.add(btn);
      rows = 2;
    }
    JButton btn = new JButton("add");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        componentsList.add(new SendComponents());
        refresh();
      }
    });
    p.add(btn);
    p.add(new JLabel());
    p.add(new JLabel());

    makeCompactGrid(p, rows, 3, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private Integer[] getItemIndexes() {
    Integer[] list = new Integer[componentsList.size()];
    for (int i=0; i<list.length; i++) {
      list[i] = i;
    }
    return list;
  }

  //
  private JPanel createItemsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Items to send");
    p.setBorder(border);

    for (int i=0; i<componentsList.size(); i++) {
      SendComponents sc = componentsList.get(i);
      JLabel l = new JLabel("Item:", JLabel.TRAILING);
      p.add(l);
      JComboBox<String> cb = sc.typeCombo;
      if (cb == null) {
        cb = new JComboBox<>(getItemNames());
        sc.typeCombo = cb;
      }
      l.setLabelFor(cb);
      p.add(cb);
      addItemField(p, cb, sc);
      addValueField(p, cb, sc);
    }

    makeCompactGrid(p, componentsList.size(), 4, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private void addItemField(JPanel p, JComboBox<String> cb, SendComponents sc) {
    JTextField tf = sc.itemField;
    if (tf == null) {
      tf = new JTextField(10);
      sc.itemField = tf;
    }
    p.add(tf);
  }

  //
  private void addValueField(JPanel p, JComboBox<String> cb, SendComponents sc) {
    JTextField tf = sc.valueField;
    if (tf == null) {
      tf = new JTextField(10);
      sc.valueField = tf;
    }
    p.add(tf);
  }

  //
  private String[] getItemNames() {
    String[] types = new String[ItemType.values().length];
    int i=0;
    for (ItemType it : ItemType.values()) {
      types[i++] = it.name();
    }
    return types;
  }

  //
  private void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        getContentPane().removeAll();
        fillContent();
        pack(); 
        validate();
      }
    });
  }

  //
  private JPanel createReferencePanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Reference to Receiver");
    p.setBorder(border);

    JLabel l = new JLabel("use Web Reference:", JLabel.TRAILING);
    p.add(l);
    refcombo = new JComboBox<String>();
    for (String n : webNames) {
      refcombo.addItem(n);
    }
    l.setLabelFor(refcombo);
    p.add(refcombo);
    if (instruction != null) {
      refcombo.setSelectedItem(instruction.getRefvar());
    }

    makeCompactGrid(p, 1, 2, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private JPanel createResponsePanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Receiver's Response");
    p.setBorder(border);

    respCheck = new JCheckBox("await response");
    respCheck.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        respArea.setVisible(respCheck.isSelected());
        respArea.setText(respValue);
      }
    });
    p.add(respCheck);
    if (respArea == null) {
      respArea = new JTextArea();
      respArea.setVisible(false);
    }
    p.add(respArea);

    makeCompactGrid(p, 1, 2, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private JPanel createControlsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Control");
    p.setBorder(border);
    p.add(new JLabel());
    gal = new GenericActionListener(this);
    JButton cbtn = new JButton("cancel");
    cbtn.setForeground(Color.blue);
    cbtn.addActionListener(gal);
    p.add(cbtn);
    JButton okbtn = new JButton("ok");
    okbtn.setForeground(Color.blue);
    okbtn.addActionListener(gal);
    p.add(okbtn);

    makeCompactGrid(p, 1, 3, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  @Override
  public String getRefName() {
    return "Send";
  }

  @Override
  public String getInstructionName() {
    return "send";
  }

  @Override
  public String getInstructionContent() {
    if (instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  @Override
  public void reset() {
    instruction.reset();
  }

  @Override
  public ClappInstruction getInstruction() {
    return instruction;
  }

  @Override
  public void setInstruction(ClappInstruction inst) {
    instruction = inst;
  }

  @Override
  public boolean setupInstruction() {
    if (instruction == null) {
      instruction = new ClappInstruction();
    }
    String str = buildupStatement();
    if (str != null) {
      instruction.setColor(Color.blue);
      instruction.setStatement(str);
      instruction.setSendInfoList(infoList);
      instruction.setIntructionType(IntructionType.WEB.name());
      return true;
    }
    return false;
  }

  //
  private String buildupStatement() {
    if (componentsList.isEmpty()) {
      return null;
    }
    infoList = new ArrayList<>();
    boolean isEmpty = true;
    String s = "send \"set FLOW_RES {";
    for (SendComponents sc : componentsList) {
      if (!sc.itemField.getText().isBlank()) {
        s += toBeSent(sc);
        isEmpty = false;
      }
    }
    if (isEmpty) {
      return null;
    }
    String refvar = (String) refcombo.getSelectedItem();
    instruction.setRefvar(refvar);
    s += "}\" using " + refvar + 
        (respCheck.isSelected() ? (" receiving \"" + respArea.getText().replace("\n", ",") + "\" ;") : ";");
    respValue = respArea.getText();
    caller.setWebName(refvar);
    caller.setWebRespValue(respValue);
    return s;
  }

  //
  private String toBeSent(SendComponents sc) {
    SendInfo si = new SendInfo();
    si.type = (String) sc.typeCombo.getSelectedItem();
    si.item = sc.itemField.getText();
    si.value = getValue( si.type, si.item, sc.valueField.getText());
    infoList.add(si);
    String s = WebContext.varTemplate;
    s = s.replace("<TYPE>", si.type);
    s = s.replace("<VAR>", si.item);
    s = s.replace("<VALUE>", si.value);
    return s;
  }

  //
  private String getValue(String type, String name, String text) {
    if (text.isBlank() || text.equals(name)) {
      return "#" + name;
    }
    if (type.equals("STRING")) {
      return "\\\"" + text + "\\\"";
    }
    return text;
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }

  @Override
  public void edit(String text, String desc) {
  }

  @Override
  public String getTransitionText() {
    return null;
  }

  @Override
  public String getDescription() {
    return null;
  }
}
