package clp.edit.graphics.dial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;

import javax.swing.JTextField;

public class PNDialogHelper implements Serializable {

  private static final long serialVersionUID = -2200338022612806887L;

  private int nbRed;
  private int nbGreen;
  private int nbBlue;
  private int nbYellow;
  private int nbOrange;
  private int nbCyan;
  private int nbBlack;
  /**
   * @return the nbRed
   */
  public int getNbRed() {
    return nbRed;
  }
  /**
   * increment nbRed
   */
  public void incNbRed() {
    this.nbRed ++;
  }
  /**
   * @param nbRed the nbRed to set
   */
  public void setNbRed(int nbRed) {
    this.nbRed = nbRed;
  }
  /**
   * @return the nbGreen
   */
  public int getNbGreen() {
    return nbGreen;
  }
  /**
   * increment nbGreen
   */
  public void incNbGreen() {
    this.nbGreen ++;
  }
  /**
   * @param nbGreen the nbGreen to set
   */
  public void setNbGreen(int nbGreen) {
    this.nbGreen = nbGreen;
  }
  /**
   * @return the nbBlue
   */
  public int getNbBlue() {
    return nbBlue;
  }
  /**
   * increment nbBlue
   */
  public void incNbBlue() {
    this.nbBlue ++;
  }
  /**
   * @param nbBlue the nbBlue to set
   */
  public void setNbBlue(int nbBlue) {
    this.nbBlue = nbBlue;
  }
  /**
   * @return the nbYellow
   */
  public int getNbYellow() {
    return nbYellow;
  }
  /**
   * increment nbYellow
   */
  public void incNbYellow() {
    this.nbYellow ++;
  }
  /**
   * @param nbYellow the nbYellow to set
   */
  public void setNbYellow(int nbYellow) {
    this.nbYellow = nbYellow;
  }
  /**
   * @return the nbOrange
   */
  public int getNbOrange() {
    return nbOrange;
  }
  /**
   * increment nbOrange
   */
  public void incNbOrange() {
    this.nbOrange ++;
  }
  /**
   * @param nbOrange the nbOrange to set
   */
  public void setNbOrange(int nbOrange) {
    this.nbOrange = nbOrange;
  }
  /**
   * @return the nbCyan
   */
  public int getNbCyan() {
    return nbCyan;
  }
  /**
   * increment nbCyan
   */
  public void incNbCyan() {
    this.nbCyan ++;
  }
  /**
   * @param nbCyan the nbCyan to set
   */
  public void setNbCyan(int nbCyan) {
    this.nbCyan = nbCyan;
  }
  /**
   * @return the nbBlack
   */
  public int getNbBlack() {
    return nbBlack;
  }
  /**
   * increment nbBlack
   */
  public void incNbBlack() {
    this.nbBlack ++;
  }
  /**
   * @param nbBlack the nbBlack to set
   */
  public void setNbBlack(int nbBlack) {
    this.nbBlack = nbBlack;
  }
  public int getAll() {
    return nbRed+nbGreen+nbBlue+nbYellow+nbOrange+nbCyan;
  }


  //
  public JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(5);
    field.setText(text);
    field.setName(lbl);
    field.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateNumber((JTextField)e.getSource());
      }
    });
    field.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        updateNumber((JTextField)e.getSource());
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    return field;
  }

  //
  private void updateNumber(JTextField source) {
    try {
      int nb = Integer.parseInt(source.getText());
      switch (source.getName()) {
        case "Black":
          setNbBlack(nb);
          break;
        case "Red":
          setNbRed(nb);
          break;
        case "Green":
          setNbGreen(nb);
          break;
        case "Blue":
          setNbBlue(nb);
          break;
        case "Yellow":
          setNbYellow(nb);
          break;
        case "Orange":
          setNbOrange(nb);
          break;
        case "Cyan":
          setNbCyan(nb);
          break;

        default:
          break;
      }
    }
    catch (NumberFormatException e) {
      
    }
  }

  public void setNb(char c, int nb) {
    switch (c) {
      case 'R':
        setNbRed(nb);
        break;
      case 'G':
        setNbGreen(nb);
        break;
      case 'B':
        setNbBlue(nb);
        break;
      case 'Y':
        setNbYellow(nb);
        break;
      case 'O':
        setNbOrange(nb);
        break;
      case 'C':
        setNbCyan(nb);
        break;

      default:
        int n = Integer.parseInt(""+c);
        setNbBlack(n);
        break;
    }
  }

  @Override
  public String toString() {
    if (nbBlack > 0) {
      return nbBlack > 1 ? ""+nbBlack : "1";
    }
    String text = "";
    text += getFromColor(true, nbRed, "R");
    text += getFromColor(text.isEmpty(), nbGreen, "G");
    text += getFromColor(text.isEmpty(), nbBlue, "B");
    text += getFromColor(text.isEmpty(), nbYellow, "Y");
    text += getFromColor(text.isEmpty(), nbOrange, "O");
    text += getFromColor(text.isEmpty(), nbCyan, "C");
    return text;
  }

  //
  private String getFromColor(boolean b, int color, String ch) {
    if (color > 0) {
      if (color > 1) {
        return (b ? "" : ".") + color + ch;
      }
      return (b ? "" : ".") + ch;
    }
    return "";
  }

  public String getMarks() {
    if (nbBlack > 0) {
      return "'N':" + nbBlack;
    }
    String text = "";
    text += getMarkFromColor(true, nbRed, "R");
    text += getMarkFromColor(text.isEmpty(), nbGreen, "G");
    text += getMarkFromColor(text.isEmpty(), nbBlue, "B");
    text += getMarkFromColor(text.isEmpty(), nbYellow, "Y");
    text += getMarkFromColor(text.isEmpty(), nbOrange, "O");
    text += getMarkFromColor(text.isEmpty(), nbCyan, "C");

    return text;
  }

  private String getMarkFromColor(boolean b, int color, String ch) {
    if (color > 0) {
      return (b ? "'" : ", '") + ch+"':"+color;
    }
    return "";
  }

  public int getSize() {
    int size = 0;
    if (nbBlack > 0) {
      size++;
    }
    else {
      if (nbRed > 0) {
        size++;
      }
      if (nbGreen > 0) {
        size++;
      }
      if (nbBlue > 0) {
        size++;
      }
      if (nbYellow > 0) {
        size++;
      }
      if (nbOrange > 0) {
        size++;
      }
      if (nbCyan > 0) {
        size++;
      }
    }
    return size;
  }

  public void resetAll() {
    nbBlack = 0;
    nbRed = 0;
    nbGreen = 0;
    nbBlue = 0;
    nbYellow = 0;
    nbOrange = 0;
    nbCyan = 0;
  }

  public Token[] getTokens() {
    return Token.getTokens(this);
  }
}
