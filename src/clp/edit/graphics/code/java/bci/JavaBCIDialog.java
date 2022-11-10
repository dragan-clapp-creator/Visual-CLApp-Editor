package clp.edit.graphics.code.java.bci;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import clp.edit.graphics.code.java.ClassInfo;
import clp.edit.graphics.code.java.ConstructorInfo;
import clp.edit.graphics.code.java.Inspector;
import clp.edit.graphics.code.java.JavaContext;
import clp.edit.graphics.code.java.JavaContext.BciInfo;
import clp.edit.graphics.code.java.MethodInfo;
import clp.edit.graphics.dial.GenericActionListener;
import clp.parse.CLAppParser;
import clp.parse.res.weave.WeaveVar;
import clp.run.res.VarType;

public class JavaBCIDialog extends JDialog {

  private static final long serialVersionUID = -154671401817470602L;

  private Hashtable<String, ClassInfo> class_hash;

  private GridBagConstraints c;

  private JButton okButton;
  private JButton cancelButton;
  private GenericActionListener gal;

  private BCICombos bciCombos;
  private String selectedClass;
  private File selectedFile;

  private List<ActionsDialog> adials;

  private clp.run.res.weave.WeaveVar wvar;

  private BciInfo bciInfo;

  private Set<String> bciList;

  private String statement;

  private JavaContext javaContext;

  /**
   * CONSTRUCTOR
   * 
   * used for adding new instruction
   * 
   * @param parent
   * @param javaContext 
   */
  public JavaBCIDialog(Frame parent, JavaContext javaContext) {
    this(parent, null, javaContext);
  }
  /**
   * CONSTRUCTOR
   * 
   * used for changing existing instruction
   * 
   * @param parent
   * @param bciInfo 
   * @param jc JavaContext javaContext 
   */
  public JavaBCIDialog(Frame parent, BciInfo binfo, JavaContext jc) {
    super(parent, "Adding a Java Byte-Code Injection", true);
    bciInfo = binfo;
    bciList = jc.getBciList();
    javaContext = jc;
    bciCombos = new BCICombos();
    adials = new ArrayList<>();
    setup(parent, binfo);
  }

  public void showDialog() {
    setVisible(true);
  }

  public void jumpToInjection() {
    redrawLater();
    setVisible(true);
  }

  //
  private void setup(Frame parent, BciInfo binfo) {
    Dimension parentSize = parent.getSize(); 
    Point p = parent.getLocation(); 
    setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    setPreferredSize(new Dimension(800, 200));
    setLayout(new GridBagLayout());

    c = new GridBagConstraints();
    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);

    if (binfo == null) {
      fillContent();
    }
    else {
      try {
        retreiveContext(binfo);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  //
  private void retreiveContext(BciInfo binfo) throws IOException {
    selectedFile = binfo.getSelectedFile();
    selectedClass = binfo.getSelectedClass();
    Inspector inspector = new Inspector(selectedFile, true);
    ClassInfo ci = inspector.createClassInfo(selectedClass, true);
    MethodInfo mi = ci.getMethods().get(binfo.getMethod());

    JComboBox<String> classCombo = new JComboBox<>();
    bciCombos.setClassCombo(classCombo);
    classCombo.addItem(selectedClass);

    JComboBox<String> methodCombo = new JComboBox<>();
    bciCombos.setMethodCombo(methodCombo);
    methodCombo.addItem(binfo.getMethod());

    bciCombos.setWeaverField(new JTextField(binfo.getBciVar().getName()));

    for (ArrayList<ActionInfo> alist : binfo.getList()) {
      ActionsDialog adial = new ActionsDialog((Frame) getOwner(),
          "Add Byte-Code Injection",
          mi.gatherLocalVariables(),
          ci.getFields());
      adials.add(adial);
      adial.setInfos(alist, mi.gatherLocalVariables(), ci.getFields());
    }
  }

  //
  private void fillContent() {
    JButton button = new JButton("select file or directory...");
    button.addActionListener(new ActionListener() {
 
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedFile = JavaContext.getSelectedFile(JavaBCIDialog.this);
        if (selectedFile != null) {
          try {
            Inspector inspector = new Inspector(selectedFile, true);
//            inspector.addUsedLib(caller);
            ArrayList<ClassInfo> cls = inspector.getClasses();
            selectedClass = cls.get(0).getName();
            prepareFollowingContent();
            fillClasses(cls);
            redrawLater();
          }
          catch (IOException ex) {
          }
        }
      }
    });

    getContentPane().removeAll();

    c.gridy = 0;
    c.gridx = 0;
    c.gridwidth = 2;
    getContentPane().add(button, c);

    c.gridy = 2;
    c.gridx = 0;
    c.gridwidth = 1;
    getContentPane().add(cancelButton, c);

    c.gridy = 2;
    c.gridx = 1;
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
        selectedClass = (String) iclassCombo.getSelectedItem();
        ClassInfo ci = class_hash.get((String) iclassCombo.getSelectedItem());
        fillMethodsInCombos(ci);
        redrawLater();
      }
    });
    bciCombos.setClassCombo(iclassCombo);
    if (selectedClass == null) {
      selectedClass = list[0];
    }
    fillMethodsInCombos(class_hash.get(selectedClass));
  }

  //
  private void fillMethodsInCombos(ClassInfo ci) {
    bciCombos.getMethodCombo().removeAllItems();
    HashMap<String, MethodInfo> methods = ci.getMethods();
    for (String key : methods.keySet()) {
      MethodInfo mi = methods.get(key);
      if (mi.isStatic()) {
        bciCombos.getMethodCombo().addItem(key);
      }
    }
    HashMap<String, ConstructorInfo> consts = ci.getConstructors();
    for (String key : consts.keySet()) {
      bciCombos.getMethodCombo().addItem(key);
    }
    for (String key : methods.keySet()) {
      MethodInfo mi = methods.get(key);
      if (!mi.isStatic() && !key.startsWith("<init>")) {
        bciCombos.getMethodCombo().addItem(key);
      }
    }
  }

  //
  private void prepareFollowingContent() {
    JComboBox<String> methodCombo = new JComboBox<>();
//    methodCombo.addItemListener(new ItemListener() {
//      @Override
//      public void itemStateChanged(ItemEvent e) {
//        ClassInfo ci = class_hash.get(selectedClass);
//        MethodInfo mi = ci.getMethods().get(methodCombo.getSelectedItem());
//        if (mi != null) {
//          System.out.println("methods number: "+methodCombo.getItemCount());
//          System.out.println("fields number: "+ci.getFields().length);
//          LocalVariableGen[] vars = mi.gatherLocalVariables();
//          if (vars != null) {
//            System.out.println("vars number: "+vars.length);
//          }
//        }
//      }
//    });
    bciCombos.setMethodCombo(methodCombo);

    JTextField wf = new JTextField(10);
    wf.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        redrawLater();
      }
    });
    bciCombos.setWeaverField(wf);
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
    c.gridwidth = 1;
    JLabel lbl = new JLabel("BCI Identifier: ", JLabel.TRAILING);
    lbl.setLabelFor(bciCombos.getWeaverField());
    getContentPane().add(lbl, c);
    c.gridx = 1;
    getContentPane().add(bciCombos.getWeaverField(), c);

    c.gridy++;
    c.gridx = 0;
    lbl = new JLabel("Class: ", JLabel.TRAILING);
    lbl.setLabelFor(bciCombos.getClassCombo());
    getContentPane().add(lbl, c);
    c.gridx = 1;
    c.gridwidth = 3;
    getContentPane().add(bciCombos.getClassCombo(), c);

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = 1;
    lbl = new JLabel("Method: ", JLabel.TRAILING);
    lbl.setLabelFor(bciCombos.getMethodCombo());
    getContentPane().add(lbl, c);
    c.gridx = 1;
    c.gridwidth = 2;
    getContentPane().add(bciCombos.getMethodCombo(), c);

    c.gridy++;
    c.gridwidth = 1;
    for (int i=0; i<adials.size(); i++) {
      JButton actions = new JButton("Show");
      ActionsDialog adial = adials.get(i);
      actions.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          adial.setVisible(true);
        }
      });
      lbl = new JLabel("Injection "+(i+1)+":", JLabel.TRAILING);
      lbl.setLabelFor(actions);
      c.gridx = 1;
      getContentPane().add(lbl, c);
      c.gridx = 2;
      getContentPane().add(actions, c);
      c.gridy++;
    }
    JButton actions = new JButton("Create");
    actions.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          ClassInfo ci;
          MethodInfo mi;
          if (class_hash == null) {
            Inspector inspector;
              inspector = new Inspector(selectedFile, true);
              ci = inspector.createClassInfo(bciInfo.getSelectedClass(), true);
              mi = ci.getMethods().get(bciCombos.getMethodCombo().getSelectedItem());
          }
          else {
            ci = class_hash.get(selectedClass);
            mi = ci.getMethods().get(bciCombos.getMethodCombo().getSelectedItem());
          }
          ActionsDialog adial = new ActionsDialog((Frame) getOwner(),
                        "Add Byte-Code Injection",
                        mi.gatherLocalVariables(),
                        ci.getFields());
          adial.showDialog();
          if (adial.isOk()) {
          adials.add(adial);
          redrawLater();
          }
        }
        catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    lbl = new JLabel("New Injection:", JLabel.TRAILING);
    lbl.setLabelFor(actions);
    c.gridx = 1;
    getContentPane().add(lbl, c);
    c.gridx = 2;
    getContentPane().add(actions, c);

    c.gridy++;
    c.gridx = 1;
    c.gridwidth = 2;
    getContentPane().add(okButton, c);
    okButton.setEnabled(check());
  }

  //
  private boolean check() {
    String ident = bciCombos.getWeaverField().getText().trim();
    if (adials.isEmpty() || ident.isBlank() ||
        bciInfo == null && bciList != null && bciList.contains(ident)) {
      return false;
    }
    for (ActionsDialog adial : adials) {
      for (ActionInfo info : adial.getInfos()) {
        if (!info.getNamefield().getText().isBlank()) {
          return true;
        }
      }
    }
    return false;
  }

  // variables will NOT be displayed on output area
  public ArrayList<String> getVariables() {
    ArrayList<String> list = new ArrayList<>();
    String c = bciCombos.getWeaverField().getText().trim();
    if (!c.isEmpty()) {
      c += "/" + VarType.TWEAVER.getVal() + "/";
      list.add(c);
    }
    for (ActionsDialog adial : adials) {
      adial.gatherVariables(list);
    }
    return list;
  }

  public boolean parseStatement() {
    statement = buildupStatement(selectedClass, bciCombos);
    InputStream is = new ByteArrayInputStream(statement.getBytes(), 0, statement.length());
    CLAppParser parser = new CLAppParser(new BufferedReader(new InputStreamReader(is)));
    WeaveVar wparse = new WeaveVar();
    try {
      wparse.parse(parser, false);
      if (parser.getError() == null) {
        wvar = wparse.getWeaveVar();
        if (wvar != null) {
          return true;
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  //
  private String buildupStatement(String selectedClass, BCICombos bciCombos) {
    int i = selectedClass.lastIndexOf(".");
    String path;
    String clname;
    if (i < 0) {
      path = "";
      clname = selectedClass;
    }
    else {
      path = selectedClass.substring(0, i);
      clname = selectedClass.substring(i+1);
    }
    String statement = "WEAVER " + bciCombos.getWeaverField().getText() + " onClass \"" + path + "\" : \"" + clname + "\" {\n";
    for (ActionsDialog adial : adials) {
      statement += adial.buildupStatement(bciCombos);
    }
    statement += "}\n";
    return statement;
  }

  public boolean setupInstruction() {
    if ( parseStatement() ) {
      ArrayList<ArrayList<ActionInfo>> list = fillActionsInfo(adials);
      if (wvar != null) {
        if (bciInfo != null) {
          bciInfo.setBciVar(wvar);
          bciInfo.setVariables(getVariables());
          bciInfo.setSelectedFile(selectedFile);
          bciInfo.setMethod((String) bciCombos.getMethodCombo().getSelectedItem());
          bciInfo.setSelectedClass(selectedClass);
          bciInfo.setList(list);
          bciInfo.setStatement(statement);
          return true;
        }
        return javaContext.addBciInfo(wvar.getName(), wvar, getVariables(), selectedFile, selectedClass, (String) bciCombos.getMethodCombo().getSelectedItem(), list, statement);
      }
    }
    return false;
  }

  //
  private ArrayList<ArrayList<ActionInfo>> fillActionsInfo(List<ActionsDialog> adials) {
    ArrayList<ArrayList<ActionInfo>> list = new ArrayList<>();
    for (ActionsDialog ad : adials) {
      ad.saveInfo(ad.getTopInfo());
      list.add(ad.getInfos());
    }
    return list;
  }

  public boolean isOk() {
    return gal.isOk();
  }

  public String getInstructionName() {
    return wvar.getName();
  }
}
