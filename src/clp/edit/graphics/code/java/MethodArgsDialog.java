package clp.edit.graphics.code.java;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import clp.edit.graphics.dial.GenericActionListener;
import clp.run.res.VarType;

public class MethodArgsDialog extends JDialog {

  private static final long serialVersionUID = 9075293758799566436L;

  private GridBagConstraints c;

  private JButton okButton;

  private GenericActionListener gal;

  private MethodArgsInfo info;

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param methodInfo
   * @param isReturn
   */
  public MethodArgsDialog(Frame owner, MethodInfo methodInfo, boolean isReturn) {
    super(owner, isReturn ? "return code" : "parameters", true);
    this.info = new MethodArgsInfo(methodInfo, isReturn);
    initialize(owner);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param methArgs
   */
  public MethodArgsDialog(Frame owner, MethodArgsInfo methArgs) {
    super(owner, methArgs.isReturn() ? "return code" : "parameters", true);
    this.info = methArgs;
    initialize(owner);
  }

  //
  private void initialize(Frame owner) {
    Dimension frameSize = owner.getSize(); 
    Point p = owner.getLocation(); 
    setLocation(p.x + frameSize.width / 4, p.y + frameSize.height / 4);

    setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setPreferredSize(new Dimension(400, 200));

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    setup();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  private void setup() {
    getContentPane().removeAll();
    c.gridy = 0;
    c.gridx = 0;
    if (info.isReturn()) {
      getContentPane().add(new JLabel("Return Variable:"), c);
      c.gridx++;
      JTextField tf = new JTextField(info.getMethodInfo().getReturnType());
      info.setRetType( convertType(info.getMethodInfo().getReturnType()) );
      tf.setEnabled(false);
      getContentPane().add(tf, c);
      c.gridx++;
      JTextField nf = new JTextField(10);
      setFocusListener(nf, null);
      getContentPane().add(nf, c);
    }
    else {
      getContentPane().add(new JLabel("Parameter(s):"), c);
      Parameter[] params = info.getMethodInfo().getParameters();
      if (params == null) {
        ArrayList<String> names = info.getNames();
        String[] args = info.getMethodInfo().getArguments(names.size());
        for (int i=0; i<names.size(); i++) {
          String n = names.get(i);
          String a = args[i];
          c.gridy++;
          c.gridx = 0;
          JTextField tf = new JTextField(a);
          tf.setEnabled(false);
          getContentPane().add(tf, c);
          c.gridx++;
          JTextField nf = new JTextField(n);
          nf.setName(""+c.gridy);
          setFocusListener(nf, a);
          getContentPane().add(nf, c);
        }
      }
      else {
        for (Parameter param : params) {
          c.gridy++;
          c.gridx = 0;
          JTextField tf = new JTextField(param.getType().getName());
          tf.setEnabled(false);
          getContentPane().add(tf, c);
          c.gridx++;
          JTextField nf = new JTextField(param.getName());
          info.getNames().add("");
          nf.setName(""+c.gridy);
          setFocusListener(nf, param.getType().getName());
          getContentPane().add(nf, c);
        }
      }
    }
    c.gridy++;
    c.gridx = 1;
    c.gridwidth = 2;
    getContentPane().add(okButton, c);
  }

  public void showPopup() {
    setVisible(true);
  }

  //
  private VarType convertType(String stype) {
    switch (stype) {
      case "boolean":
      case "java.lang.Boolean":
        return VarType.TBOOL;
      case "int":
      case "java.lang.Integer":
        return VarType.TINT;
      case "float":
      case "java.lang.Float":
      case "double":
      case "java.lang.Double":
        return VarType.TFLOAT;
      case "long":
      case "java.lang.Long":
        return VarType.TLONG;
      case "java.lang.String":
        return VarType.TSTRING;
      case "java.util.Date":
        return VarType.TDATE;
      case "java.util.Time":
        return VarType.TTIME;

      default:
        return VarType.TREF;
    }
  }

  //
  private void setFocusListener(JTextField nf, String ptype) {
    nf.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        String txt = nf.getText().trim();
        if (!txt.isEmpty()) {
          if (info.isReturn()) {
            if (isVariable(txt)) {
              info.setRetName(txt);
            }
            else {
              nf.setText(null);
            }
          }
          else {
            int index = Integer.parseInt(nf.getName()) - 1;
            info.getNames().set(index, txt);
          }
          if (ptype != null && isVariable(txt)) {
            info.getHash().put(txt, convertType(ptype));
          }
        }
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    if (info.isReturn()) {
      nf.setText(info.getRetName());
    }
  }

  //
  private boolean isVariable(String txt) {
    if (Character.isLetter(txt.charAt(0))) {
      for (int i=1; i<txt.length(); i++) {
        if (!Character.isLetterOrDigit(txt.charAt(i)) || txt.charAt(i) == '_') {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public boolean isOk() {
    return gal.isOk();
  }

  /**
   * @return the retName
   */
  public String getRetName() {
    return info.getRetName();
  }

  /**
   * @return the names
   */
  public ArrayList<String> getNames() {
    return info.getNames();
  }

  /**
   * @return the methodInfo
   */
  public MethodInfo getMethodInfo() {
    return info.getMethodInfo();
  }

  /**
   * @return the info
   */
  public MethodArgsInfo getInfo() {
    return info;
  }

  /**
   * @param info the info to set
   */
  public void setInfo(MethodArgsInfo info) {
    this.info = info;
  }
}
