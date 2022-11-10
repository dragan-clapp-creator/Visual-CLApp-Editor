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

public class ConstructorArgsDialog extends JDialog {

  private static final long serialVersionUID = 376518377402799130L;

  private GridBagConstraints c;

  private JButton okButton;

  private GenericActionListener gal;

  private ConstructorArgsInfo info;

  /**
   * CONSTRUCTOR
   * 
   * @param frame
   * @param consInfo
   */
  public ConstructorArgsDialog(Frame frame, ConstructorInfo consInfo) {
    super(frame, "parameters", true);
    this.info = new ConstructorArgsInfo();
    info.setConsInfo(consInfo);

    Dimension frameSize = frame.getSize(); 
    Point p = frame.getLocation(); 
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

  //
  private void setup() {
    getContentPane().removeAll();
    c.gridy = 0;
    c.gridx = 0;
    getContentPane().add(new JLabel("Parameter(s):"), c);
    Parameter[] params = info.getConsInfo().getParameters();
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
      case "int":
      case "java.lang.Integer":
        return VarType.TINT;
      case "long":
      case "java.lang.Long":
        return VarType.TLONG;
      case "java.lang.String":
        return VarType.TSTRING;

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
          int index = Integer.parseInt(nf.getName()) - 1;
          info.getNames().set(index, txt);
          if (ptype != null && isVariable(txt)) {
            info.getHash().put(txt, convertType(ptype));
          }
        }
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
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

  public String getRetVariable() {
    String c = info.getRetName();
    c += "/" + info.getRetType().getVal() + "/";    // will NOT be displayed on output area
    return c;
  }

  /**
   * @return the consInfo
   */
  public ConstructorInfo getConsInfo() {
    return info.getConsInfo();
  }

  /**
   * @return the info
   */
  public ConstructorArgsInfo getInfo() {
    return info;
  }

  /**
   * @param info the info to set
   */
  public void setInfo(ConstructorArgsInfo info) {
    this.info = info;
  }
}
