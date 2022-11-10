package clp.edit.graphics.code.asn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.code.InstructionDialog;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;
import clp.run.res.VarType;

public class AssignDialog extends ADialog implements InstructionDialog {

  private static final long serialVersionUID = -6228802168918418761L;

  private static final String[] types = { "BOOL", "INT", "FLOAT", "STRING", "DATE", "TIME", "TOKEN" };
  private static final String[] tlbl = { "R", "B", "G", "Y", "O", "C" };

  private JComboBox<String> varTypeCombo;

  private JTextField namefield;
  private JTextField iffield;
  private JCheckBox keepbox;
  private JTextField valuefield;
  private JComboBox<String> tcombo;
  private JLabel lbl;

  private ArrayList<TokenInfo> tokenList;

  private boolean isError;

  private GenericActionListener gal;

  private ActionShape caller;

  private ClappInstruction instruction;


  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param caller
   */
  public AssignDialog(Frame parent, ActionShape caller) {
    super(parent, "Adding an assignment statement", true);
    this.caller = caller;
    setup(parent, null);
    setVisible(true);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param caller
   * @param inst
   */
  public AssignDialog(Frame parent, ActionShape caller, ClappInstruction inst) {
    super(parent, "Adding an assignment statement", true);
    this.caller = caller;
    setup(parent, inst);
  }

  //
  private void setup(Frame owner, ClappInstruction inst) {
    prepareContent();
    if (inst != null) {
      instruction = inst;
      varTypeCombo.setSelectedIndex(instruction.getIndex());
      String stype = (String) varTypeCombo.getSelectedItem();
      if (stype.equals("TOKEN")) {
        String txt = instruction.getValue();
        reassemble(txt.substring(1, txt.length()-1), instruction.getName());
      }
      else {
        namefield.setText(instruction.getName());
        iffield.setText(instruction.getIfname());
        keepbox.setText("Keep Alive");
        keepbox.setSelected(inst.isIskeepalive());
        valuefield.setText(instruction.getValue());
      }
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
  private void gatherTokenInfo() {
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    List<AContainer> containers = shapesContainer.getContainers();
    for (AContainer container : containers) {
      if (container.isPetri()) {
        if (tokenList == null) {
          tokenList = new ArrayList<>();
        }
        AShape shape = getFirstShape(container.getContainerShape().getRoot());
        if (shape != null) {
          ArrayList<String> names = new ArrayList<>();
          ArrayList<AShape> shapes = new ArrayList<>();
          shape.gatherChildrenShapes(shapes);
          for (AShape sh : shapes) {
            if (sh instanceof TransitionNodeShape) {
              if (sh.getParent() instanceof TriBinding) {
                TriBinding tri = (TriBinding) sh.getParent();
                if (tri.getLeft() != null) {
                  String name = tri.getLeft().getParent().getName();
                  if (!names.contains(name)) {
                    tokenList.add(new TokenInfo(name, container.isColored()));
                    names.add(name);
                  }
                }
                if (tri.getMiddle() != null) {
                  String name = tri.getMiddle().getParent().getName();
                  if (!names.contains(name)) {
                    tokenList.add(new TokenInfo(name, container.isColored()));
                    names.add(name);
                  }
                }
                if (tri.getRight() != null) {
                  String name = tri.getRight().getParent().getName();
                  if (!names.contains(name)) {
                    tokenList.add(new TokenInfo(name, container.isColored()));
                    names.add(name);
                  }
                }
              }
            }
            else {
              tokenList.add(new TokenInfo(sh.getName(), container.isColored()));
              names.add(sh.getName());
            }
          }
        }
     }
    }
  }

  //
  private AShape getFirstShape(AShape root) {
    if (root.getChild() != null) {
      return root.getChild().getChild();
    }
    return null;
  }

  @Override
  public String getRefName() {
    return "Assignment";
  }

  @Override
  public String getInstructionName() {
    return "Assignment";
  }

  @Override
  public String getInstructionContent() {
    if (isError) {
      return null;
    }
    if (instruction == null || instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  //
  private VarType valueOf(String selectedItem) {
    for (VarType t : VarType.values()) {
      if (t.getVal().equals(selectedItem)) {
        return t;
      }
    }
    return null;
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
    if (isError) {
      return false;
    }
    if (instruction == null) {
      instruction = new ClappInstruction();
    }
    instruction.setColor(Color.white);
    String stype = (String) varTypeCombo.getSelectedItem();
    if (stype.equals("TOKEN")) {
      int index = tcombo.getSelectedIndex();
      if (index >= 0) {
        TokenInfo ti = tokenList.get(index);
        String txt = assemble(ti);
        if (txt == null) {
          return false;
        }
        instruction.setStatement("loadMarks (" + ti.cellName + " , " + txt + ");");
        instruction.setName(ti.cellName);
        instruction.setValue(txt);
        instruction.setIndex(varTypeCombo.getSelectedIndex());
        instruction.setIntructionType(IntructionType.ASSIGN.name());
        return true;
      }
    }
    else {
      String name = namefield.getText().trim();
      if (!name.isEmpty()) {
        VarType vtype = valueOf(stype);
        caller.addOutputVariable(name, vtype);
        if (vtype == VarType.TBOOL) {
          String txt = "bset ";
          if (keepbox.isSelected()) {
            txt += "keeping ";
          }
          txt += name;
          if (iffield != null && !iffield.getText().trim().isEmpty()) {
            txt += " if " + iffield.getText();
            String ifname = iffield.getText().trim();
            caller.addInputVariable(ifname, ifname, VarType.TBOOL, false);
          }
          txt += ";";
          instruction.setStatement(txt);
        }
        else {
          String txt = getConstant(valuefield.getText().trim(), vtype == VarType.TINT || vtype == VarType.TFLOAT);
          instruction.setStatement("set " + name + " = " + txt + ";");
        }
        instruction.setValue(valuefield.getText());
      }
      instruction.setName(name);
      instruction.setIfname(iffield.getText());
      instruction.setIskeepalive(keepbox.isSelected());
      instruction.setIndex(varTypeCombo.getSelectedIndex());
      instruction.setIntructionType(IntructionType.ASSIGN.name());
      return true;
    }
    return false;
  }

  //
  private String assemble(TokenInfo ti) {
    if (ti.tfs.length == 1) {
      String txt = ti.tfs[0].getText();
      if (txt != null && !txt.isBlank()) {
        try {
          Integer.parseInt(txt);
          return "\"N:" + txt + "\"";
        }
        catch(NumberFormatException e) {
          return "\"N:$" + txt + "\"";
        }
      }
    }
    else {
      String ret = "";
      for (int i=0; i<6; i++) {
        String txt = ti.tfs[i].getText();
        if (txt != null && !txt.isBlank()) {
          if (!ret.isEmpty()) {
            ret += ",";
          }
          try {
            Integer.parseInt(txt);
            ret += tlbl[i] + ":" + txt;
          }
          catch(NumberFormatException e) {
            ret += tlbl[i] + ":$" + txt;
          }
        }
      }
      return "\"" + ret + "\"";
    }
    return null;
  }

  //
  private void reassemble(String marks, String name) {
    TokenInfo ti = findTokenInfo(name);
    if (ti != null) {
      String[] splist = marks.split(",");
      for (int i=0; i<splist.length; i++) {
        String str = splist[i];
        String[] sp = str.split(":");
        if (sp.length == 2 && sp[0].length() == 1) {
          String val;
          if (sp[1].charAt(0) == '$') {
            val = sp[1].substring(1);
          }
          else {
            val = sp[1];
          }
          char c = sp[0].charAt(0);
          if (c != 'N') {
            for (int j=0; j<tlbl.length; j++) {
              if (c == tlbl[j].charAt(0)) {
                ti.tfs[j].setText(val);
                break;
              }
            }
          }
          else {
            ti.tfs[0].setText(val);
          }
        }
      }
      tcombo.setSelectedItem(name);
    }
  }

  //
  private TokenInfo findTokenInfo(String name) {
    for (TokenInfo ti : tokenList) {
      if (ti.cellName.equals(name)) {
        return ti;
      }
    }
    return null;
  }

  //
  private String getConstant(String txt, boolean isNumeric) {
    if (isNumeric) {
      return txt;
    }
    if (txt.isEmpty()) {
      return "\"\"";
    }
    if (!txt.matches("^[a-zA-Z0-9]*$")) {
      char c0 = txt.charAt(0);
      char cn = txt.charAt(txt.length()-1);
      if (c0 != '\"') {
        txt = "\"" + txt;
      }
      if (cn != '\"') {
        txt += "\"";
      }
    }
    return txt;
  }

  //
  private void prepareContent() {
    gatherTokenInfo();
    varTypeCombo = new JComboBox<>(types);
    varTypeCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refresh();
      }
    });
    namefield = new JTextField(10);
    lbl = new JLabel("IF ");
    valuefield = new JTextField(10);
    iffield = new JTextField(10);
    keepbox = new JCheckBox("Keep Alive");
    varTypeCombo.setSelectedItem(0);
    tcombo = new JComboBox<>();
    tcombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refresh();
      }
    });
    fillTokenComboFromList();
  }

  //
  private void fillContent() {
    setLayout(new SpringLayout());

    if (varTypeCombo.getSelectedItem().equals("TOKEN")) {
      int index = tcombo.getSelectedIndex();
      if (index < 0) {
        getContentPane().add(new JLabel("No Token to Load!"));
      }
      else {
        getContentPane().add(createTokenPanel(index));
      }
    }
    else if (varTypeCombo.getSelectedItem().equals(VarType.TBOOL.getVal())) {
      getContentPane().add(createBooleanPanel());
    }
    else {
      getContentPane().add(createAssignmentPanel());
    }
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(), 2, 1, 6, 6, 6, 6);
  }

  public void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        getContentPane().removeAll();
        fillContent();
        getContentPane().repaint();
        getContentPane().validate();
      }
    });
  }

  //
  private Component createAssignmentPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Assignment");
    p.setBorder(border);

    p.add(varTypeCombo);
    p.add(namefield);
    lbl.setText(" = ");
    p.add(lbl);
    if (varTypeCombo.getSelectedItem().equals(VarType.TDATE.getVal())) {
      valuefield.setToolTipText("syntax: 'DD/MM/YYYY'");
    }
    else if (varTypeCombo.getSelectedItem().equals(VarType.TTIME.getVal())) {
      valuefield.setToolTipText("syntax: 'HH/mm/ss'");
    }
    else {
      valuefield.setToolTipText(null);
    }
    valuefield.setColumns(10);
    p.add(valuefield);

    makeCompactGrid(p, 1, 4, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  //
  private Component createTokenPanel(int index) {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Token Assignment");
    p.setBorder(border);

    int nbl = 1;
    int nbc = 3;
    TokenInfo ti = tokenList.get(index);
    lbl.setText(" <- ");
    if (ti.isColored) {
      nbl++;
      p.add(Box.createVerticalStrut(5));
      p.add(Box.createVerticalStrut(5));
      p.add(Box.createVerticalStrut(5));
      nbc += 6;
      for (int i=0; i<6; i++) {
        p.add(new JLabel(tlbl[i]));
      }
      p.add(varTypeCombo);
      p.add(tcombo);
      p.add(lbl);
      for (int i=0; i<6; i++) {
        p.add(ti.tfs[i]);
      }
    }
    else {
      p.add(varTypeCombo);
      p.add(tcombo);
      p.add(lbl);
      nbc++;
      p.add(ti.tfs[0]);
    }

    makeCompactGrid(p, nbl, nbc, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  //
  private Component createBooleanPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Boolean Assignment");
    p.setBorder(border);

    p.add(varTypeCombo);
    p.add(Box.createVerticalStrut(5));
    p.add(namefield);
    p.add(Box.createVerticalStrut(5));
    lbl.setText("IF ");
    lbl.setLabelFor(iffield);
    p.add(lbl);
    p.add(iffield);
    p.add(keepbox);

    makeCompactGrid(p, 1, 7, 6, 6, 6, 6);

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

  //
  private void fillTokenComboFromList() {
    if (tokenList != null && !tokenList.isEmpty()) {
      for (TokenInfo ti : tokenList) {
        tcombo.addItem(ti.cellName);
      }
    }
  }

  private class TokenInfo {
    private String cellName;
    private boolean isColored;
    private JTextField[] tfs;
    private TokenInfo(String n, boolean b) {
      cellName = n;
      isColored = b;
      if (isColored) {
        tfs = new JTextField[6];
        for (int i=0; i<6; i++) {
          tfs[i] = new JTextField(3);
        }
      }
      else {
        tfs = new JTextField[1];
        tfs[0] = new JTextField(3);
      }
    }
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

  @Override
  public boolean isOk() {
    return gal.isOk();
  }
}
