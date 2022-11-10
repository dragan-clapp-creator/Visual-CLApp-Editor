package clp.edit.graphics.code.java;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.code.InstructionDialog;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.shapes.ActionShape;
import clp.run.res.VarType;

public class JavaCallDialog extends JDialog implements InstructionDialog {

  private static final long serialVersionUID = 1323135393833941518L;

  private static final String[] types = { "NEW", "START", "CALL", "STATIC" };

  private Hashtable<MethodInfo, MethodArgsDialog> method_hash;
  private Hashtable<MethodInfo, MethodArgsDialog> return_hash;
  private Hashtable<ConstructorInfo, ConstructorArgsDialog> cons_hash;

  private JButton okButton;
  private GenericActionListener gal;

  private JButton cancelButton;

  private JComboBox<String> callCombo;      // select call type see #types

  private Hashtable<String, ClassInfo> class_hash;

  private GridBagConstraints c;

  private String consSel;
  private String oldConsSel;
  private String classSel;
  private String oldClassSel;
  private boolean hasSelectionChanged;

  private ClappJavaInstruction instruction;

  private ActionShape caller;

  private String savedStatement;

  private boolean isRecover;

  /**
   * CONSTRUCTOR
   * 
   * used for adding new instruction
   * 
   * @param parent
   * @param caller
   */
  public JavaCallDialog(Frame parent, ActionShape caller) {
    super(parent, "Adding a Java instruction", true);
    this.caller = caller;
    setup(parent, null);
    setVisible(true);
  }

  /**
   * CONSTRUCTOR
   * 
   * used for changing existing instruction
   * 
   * @param parent
   * @param caller
   * @param inst
   */
  public JavaCallDialog(Frame parent, ActionShape caller, ClappJavaInstruction inst) {
    super(parent, "Adding a Java instruction", true);
    this.caller = caller;
    setup(parent, inst);
  }

  //
  private void setup(Frame parent, ClappJavaInstruction inst) {
    Dimension parentSize = parent.getSize(); 
    Point p = parent.getLocation(); 
    setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    setPreferredSize(new Dimension(800, 200));
    setLayout(new GridBagLayout());

    method_hash = new Hashtable<>();
    return_hash = new Hashtable<>();
    cons_hash = new Hashtable<>();

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);

    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    if (inst == null) {
      instruction = new ClappJavaInstruction();
      instruction.setColor(Color.magenta);
      fillInitialContent();
    }
    else {
      instruction = inst;
      isRecover = true;
      retreiveContext();
    }

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  //
  private void retreiveContext() {
    File selectedFile = instruction.getSelectedFile();
    if (selectedFile != null) {
      try {
        String selClass = instruction.getSelectedClass();
        classSel = selClass;
        String selCons = instruction.getSelectedCons();
        consSel = selCons;
        String selMeth = instruction.getSelectedMeth();
        Inspector inspector = new Inspector(selectedFile, false);
        ArrayList<ClassInfo> cls = inspector.getClasses();
        prepareFollowingContent();
        JTextField instanceField = instruction.getInstanceField();
        instanceField .setText(instruction.getSelectedInstance());
        fillClasses(cls);
        callCombo.setSelectedItem(instruction.getSelectedType());
        inspector.addUsedLib(caller);
        updateCombos(instruction.getSelectedType(), selCons, selMeth);
      }
      catch (IOException ex) {
      }
    }

    getContentPane().removeAll();

    c.gridy = 0;
    c.gridx = 0;
    getContentPane().add(cancelButton, c);

    c.gridy = 2;
    c.gridwidth = 1;
    getContentPane().add(okButton, c);
  }

  //
  private void updateCombos(String type, String selCons, String selMeth) {
    switch (type) {
      case "NEW":
        instruction.getConstCombo().setSelectedItem(selCons);
        break;
      case "START":
        break;
      case "CALL":
      case "STATIC":
        instruction.getMethodCombo().setSelectedItem(selMeth);
        break;

      default:
        break;
    }
  }

  //
  private void fillInitialContent() {
    JButton button = new JButton("select file or directory...");
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        File selectedFile = JavaContext.getSelectedFile(JavaCallDialog.this);
        if (selectedFile != null) {
          try {
            instruction.setSelectedFile(selectedFile);
            Inspector inspector = new Inspector(selectedFile, false);
            ArrayList<ClassInfo> cls = inspector.getClasses();
            prepareFollowingContent();
            fillClasses(cls);
            inspector.addUsedLib(caller);
          }
          catch (IOException ex) {
          }
        }
        redrawLater();
      }
    });

    getContentPane().removeAll();

    c.gridy = 0;
    c.gridx = 0;
    getContentPane().add(button, c);

    c.gridy = 2;
    c.gridwidth = 1;
    getContentPane().add(okButton, c);
  }

  //
  private void fillClasses(ArrayList<ClassInfo> cls) {
    class_hash = new Hashtable<String, ClassInfo>();
    String[] list = new String[cls.size()];
    for (int i=0; i<cls.size(); i++) {
      ClassInfo ci = cls.get(i);
      list[i] = ci.getName();
      class_hash.put(list[i], ci);
    }
    JComboBox<String> iclassCombo = new JComboBox<>(list);
    iclassCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        oldClassSel = classSel;
        classSel = (String) iclassCombo.getSelectedItem();
        if (classSel != null) {
          instruction.setSelectedClass(classSel);
          fillMethodsInCombos(class_hash.get(classSel));
          redrawLater();
        }
      }
    });
    instruction.setClassCombo(iclassCombo);
    if (classSel == null) {
      classSel = cls.get(0).getName();
    }
    instruction.setSelectedClass(classSel);
    if (!isRecover) {
      fillMethodsInCombos(class_hash.get(classSel));
    }
  }

  //
  private void fillMethodsInCombos(ClassInfo ci) {
    switch ((String)callCombo.getSelectedItem()) {
      case "NEW":
        instruction.getConstCombo().removeAllItems();
        HashMap<String, ConstructorInfo> consts = ci.getConstructors();
        for (String key : consts.keySet()) {
          instruction.getConstCombo().addItem(key);
        }
        instruction.getConstCombo().setSelectedItem(instruction.getSelectedCons());
        break;
      case "START":
        break;
      case "CALL":
        instruction.getMethodCombo().removeAllItems();
        HashMap<String, MethodInfo> methods = ci.getMethods();
        for (String key : methods.keySet()) {
          MethodInfo mi = methods.get(key);
          if (!mi.isStatic() && !key.startsWith("<init>")) {
            instruction.getMethodCombo().addItem(key);
          }
        }
        instruction.getMethodCombo().setSelectedItem(instruction.getSelectedMeth());
        break;
      case "STATIC":
        instruction.getMethodCombo().removeAllItems();
        methods = ci.getMethods();
        for (String key : methods.keySet()) {
          MethodInfo mi = methods.get(key);
          if (mi.isStatic()) {
            instruction.getMethodCombo().addItem(key);
          }
        }
        instruction.getMethodCombo().setSelectedItem(instruction.getSelectedMeth());
        break;

      default:
        break;
    }
  }

  //
  private void prepareFollowingContent() {
    callCombo = new JComboBox<>(types);
    callCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        hasSelectionChanged = true;
        instruction.setSelectedType((String) callCombo.getSelectedItem());
        fillMethodsInCombos(class_hash.get(instruction.getSelectedClass()));
        redrawLater();
      }
    });

    JComboBox<String> methodCombo = new JComboBox<>();
    methodCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        instruction.setSelectedMeth((String) methodCombo.getSelectedItem());
        redrawLater();
      }
    });
    instruction.setMethodCombo(methodCombo);

    JTextField instanceField = new JTextField(10);
    instruction.setInstanceField(instanceField);

    JComboBox<String> constCombo = new JComboBox<>();
    constCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        consSel = (String) constCombo.getSelectedItem();
        instruction.setSelectedCons(consSel);
        if (consSel != null && !consSel.equals(oldConsSel)) {
          hasSelectionChanged = true;
          oldConsSel = consSel;
          redrawLater();
        }
      }
    });
    instruction.setConstCombo(constCombo);
  }

  //
  private void redrawLater() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        fillFollowingContent();
        repaint();
        validate();
      }
    });
  }

  //
  private void fillFollowingContent() {
    getContentPane().removeAll();

    c.gridy = 0;
    c.gridx = 0;
    getContentPane().add(callCombo, c);
    c.gridx++;
    getContentPane().add(instruction.getClassCombo(), c);

    c.gridx = 0;
    c.gridwidth = 3;
    setLine(callCombo.getSelectedIndex());

    c.gridy++;
    c.gridwidth = 1;
    getContentPane().add(okButton, c);
  }

  //
  private void setLine(int selectedIndex) {
    c.gridy = 1;
    okButton.setEnabled(true);
    switch (selectedIndex) {
      case 0:   // NEW
        getContentPane().add(createNewPanel(), c);
        break;
      case 1:   // START
        getContentPane().add(Box.createVerticalStrut(5), c);
        ClassInfo ci = class_hash.get((String) instruction.getClassCombo().getSelectedItem());
        if (!ci.isRunnable()) {
          okButton.setEnabled(false);
        }
        break;
      case 2:   // CALL
        ci = class_hash.get((String) instruction.getClassCombo().getSelectedItem());
        getContentPane().add(createCallPanel(ci), c);
        break;
      case 3:   // STATIC CALL
        getContentPane().add(createStaticCallPanel(), c);
        break;
      default:
        break;
    }
  }

  //
  private JPanel createNewPanel() {
    JPanel jp = new JPanel();
    jp.add(new JLabel("instance:"));
    jp.add(instruction.getInstanceField());
    String key = instruction.getSelectedClass();
    ClassInfo ci = class_hash.get(key);
    addConstructors(ci.getConstructors().keySet());
    jp.add(instruction.getConstCombo());
    if (ci.hasConstructorArguments((String)instruction.getConstCombo().getSelectedItem())) {
      JButton btn = new JButton("args");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ConstructorArgsDialog ai = handleArguments(ci.getConstructors().get((String)instruction.getConstCombo().getSelectedItem()));
          if (ai != null) {
            instruction.setConstArgs(ai.getInfo());
          }
          redrawLater();
        }
      });
      jp.add(btn);
    }
    else {
      instruction.setConstArgs(null);
    }
    okButton.setEnabled(!instruction.getInstanceField().getText().isBlank() &&
        (instruction.getConstArgs() == null || instruction.getConstArgs().isComplete()));
    return jp;
  }

  //
  private void addConstructors(Set<String> set) {
    if (hasSelectionChanged) {
      hasSelectionChanged = false;
    }
    else if (oldClassSel != null && oldClassSel != classSel) {
      String scons = instruction.getSelectedCons();
      instruction.getConstCombo().removeAllItems();
      if (!set.isEmpty()) {
        for (String key : set) {
          instruction.getConstCombo().addItem(key);
        }
        instruction.getConstCombo().setSelectedItem(scons);
      }
    }
  }

  //
  private JPanel createCallPanel(ClassInfo ci) {
    JPanel jp = new JPanel();
    jp.add(new JLabel("instance:"));
    jp.add(instruction.getInstanceField());
    addConstructors(ci.getConstructors().keySet());
    jp.add(new JLabel("method:"));
    jp.add(instruction.getMethodCombo());
    MethodInfo mi = ci.getMethod((String)instruction.getMethodCombo().getSelectedItem());
    if (mi != null && mi.hasArguments()) {
      JButton btn2 = new JButton("args");
      btn2.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MethodArgsDialog ai = handleArguments(ci.getMethods().get((String)instruction.getMethodCombo().getSelectedItem()), false);
          if (ai != null) {
            instruction.setMethArgs(ai.getInfo());
          }
          redrawLater();
        }
      });
      jp.add(btn2);
      if (instruction.getMethArgs() == null || !instruction.getMethArgs().isComplete()) {
        okButton.setEnabled(false);
      }
    }
    if (mi != null && mi.hasReturn()) {
      JButton btn = new JButton("return");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MethodArgsDialog ai = handleArguments(ci.getMethods().get((String)instruction.getMethodCombo().getSelectedItem()), true);
          if (ai != null) {
            instruction.setMethReturn(ai.getInfo());
          }
          redrawLater();
        }
      });
      jp.add(btn);
      if (instruction.getMethReturn() == null || !instruction.getMethReturn().isComplete()) {
        okButton.setEnabled(false);
      }
    }
    return jp;
  }

  //
  private JPanel createStaticCallPanel() {
    String key = (String) instruction.getClassCombo().getSelectedItem();
    ClassInfo ci = class_hash.get(key);
    JPanel jp = new JPanel();
    jp.add(new JLabel("method:"));
    jp.add(instruction.getMethodCombo());
    MethodInfo mi = ci.getMethod((String)instruction.getMethodCombo().getSelectedItem());
    if (mi != null && mi.hasArguments()) {
      JButton btn = new JButton("args");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MethodArgsDialog ai = handleArguments(ci.getMethods().get((String)instruction.getMethodCombo().getSelectedItem()), false);
          if (ai != null) {
            instruction.setMethArgs(ai.getInfo());
          }
          redrawLater();
        }
      });
      jp.add(btn);
      if (instruction.getMethArgs() == null || !instruction.getMethArgs().isComplete()) {
        okButton.setEnabled(false);
      }
    }
    if (mi != null && mi.hasReturn()) {
      JButton btn = new JButton("return");
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MethodArgsDialog ai = handleArguments(ci.getMethods().get((String)instruction.getMethodCombo().getSelectedItem()), true);
          if (ai != null) {
            instruction.setMethReturn(ai.getInfo());
          }
          redrawLater();
        }
      });
      jp.add(btn);
      if (instruction.getMethReturn() == null || !instruction.getMethReturn().isComplete()) {
        okButton.setEnabled(false);
      }
    }
    return jp;
  }

  //
  private ConstructorArgsDialog handleArguments(ConstructorInfo consInfo) {
    ConstructorArgsDialog ai = cons_hash.get(consInfo);
    if (ai == null) {
      ai = new ConstructorArgsDialog((Frame)getOwner(), consInfo);
      cons_hash.put(consInfo, ai);
    }
    ai.showPopup();
    return ai.isOk() ? ai : null;
  }

  //
  private MethodArgsDialog handleArguments(MethodInfo methodInfo, boolean isReturn) {
    MethodArgsDialog ai = getMethodArgsDialog(methodInfo, isReturn);
    if (ai == null) {
      ai = new MethodArgsDialog((Frame)getOwner(), methodInfo, isReturn);
      putMethodArgsDialog(methodInfo, ai, isReturn);
    }
    ai.showPopup();
    return ai.isOk() ? ai : null;
  }

  public MethodArgsDialog getMethodArgsDialog(MethodInfo methodInfo, boolean isReturn) {
    if (isRecover) {
      if (isReturn) {
        return new MethodArgsDialog((Frame)getOwner(), methodInfo, true);
      }
      return new MethodArgsDialog((Frame)getOwner(), instruction.getMethArgs());
    }
    if (isReturn) {
      return return_hash.get(methodInfo);
    }
    return method_hash.get(methodInfo);
  }

  public void putMethodArgsDialog(MethodInfo methodInfo, MethodArgsDialog ai, boolean isReturn) {
    if (isReturn) {
      return_hash.put(methodInfo, ai);
    }
    else {
      method_hash.put(methodInfo, ai);
    }
  }

  @Override
  public String getRefName() {
    return "JAVA";
  }

  @Override
  public String getInstructionName() {
    return getRefName();
  }

  @Override
  public String getInstructionContent() {
    if (instruction == null || instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  private ArrayList<String> getVariables() {
    ArrayList<String> list = new ArrayList<>();
    String c = instruction.getInstanceField().getText().trim();
    if (!c.isEmpty()) {
      c += "/" + VarType.TREF.getVal() + "/";    // will NOT be displayed on output area
      list.add(c);
    }
    if (instruction.getConstArgs() != null) {
      list.addAll(instruction.getConstArgs().getVariables());
    }
    if (instruction.getMethArgs() != null) {
      list.addAll(instruction.getMethArgs().getVariables());
    }
    if (instruction.getMethReturn() != null) {
      list.add(instruction.getMethReturn().getRetVariable());
    }
    return list;
  }

  @Override
  public void reset() {
    savedStatement = instruction.getStatement();
    instruction.reset();
  }

  @Override
  public ClappInstruction getInstruction() {
    if(instruction.getStatement() == null) {
      instruction.setStatement(savedStatement);
    }
    return instruction;
  }

  @Override
  public boolean setupInstruction() {
    String statement = "java";
    switch (callCombo.getSelectedIndex()) {
      case 0: // NEW
        statement += " new \"" + instruction.getClassCombo().getSelectedItem() + "\"";
        if (instruction.getConstArgs() != null) {
          statement += " pass " + instruction.getConstArgs().getNamesList();
        }
        if (!instruction.getInstanceField().getText().isBlank()) {
          statement += " get " + instruction.getInstanceField().getText().trim();
        }
        break;
      case 1: // START
        statement += " start \"" + instruction.getClassCombo().getSelectedItem() + "\"";
        break;
      case 2: // CALL
        statement += " " + instruction.getInstanceField().getText().trim();
        statement += ".\"" + ((String)instruction.getMethodCombo().getSelectedItem()).split("/")[0] + "\"";
        if (instruction.getMethArgs() != null) {
          statement += " pass " + instruction.getMethArgs().getNamesList();
        }
        if (instruction.getMethReturn() != null) {
          statement += " get " + instruction.getMethReturn().getRetName();
        }
        break;
      case 3: // STATIC CALL
        statement += " \"" + instruction.getClassCombo().getSelectedItem() + "\"";
        if (instruction.getMethArgs() != null) {
          statement += " pass " + instruction.getMethArgs().getNamesList();
        }
        if (instruction.getMethReturn() != null) {
          statement += " get " + instruction.getMethReturn().getRetName();
        }
        break;

      default:
        return false;
    }
    instruction.setStatement(statement+";");
    instruction.setIntructionType(IntructionType.JAVA.name());
    caller.addVariables(getVariables());
    return true;
  }

  @Override
  public void setInstruction(ClappInstruction inst) {
    instruction = (ClappJavaInstruction) inst;
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }
}
