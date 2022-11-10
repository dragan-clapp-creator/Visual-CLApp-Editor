package clp.edit.graphics.code.prt;

import java.io.Serializable;

import javax.swing.JTextField;

public class PrintElementInfo implements Serializable {

  private static final long serialVersionUID = 973872126660709810L;

  private JTextField namefield;

  private int line;

  public PrintElementInfo(int line) {
    this.line = line;
    namefield = new JTextField(10);
  }

  public String getText() {
    String txt = namefield.getText().trim();
    if (txt.isEmpty()) {
      return "";
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

  /**
   * @return the namefield
   */
  public JTextField getNamefield() {
    return namefield;
  }

  /**
   * @return the line
   */
  public int getLine() {
    return line;
  }
}
