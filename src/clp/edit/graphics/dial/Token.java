package clp.edit.graphics.dial;

import java.io.Serializable;

public class Token implements Serializable {

  private static final long serialVersionUID = 7068920528962128740L;

  private char c; // color ref
  private int nb; // number of tokens of that color

  /**
   * @return the c
   */
  public char getC() {
    return c;
  }

  /**
   * @return the nb
   */
  public int getNb() {
    return nb;
  }


  public static Token[] getTokens(PNDialogHelper helper) {
    Token[] tks = new Token[helper.getSize()];
    if (helper.getNbBlack() > 0) {
      fillTokens(tks, 0, helper.getNbBlack(), 'N');
    }
    else {
      int i = 0;
      i = fillTokens(tks, i, helper.getNbRed(),    'R');
      i = fillTokens(tks, i, helper.getNbGreen(),  'G');
      i = fillTokens(tks, i, helper.getNbBlue(),   'B');
      i = fillTokens(tks, i, helper.getNbYellow(), 'Y');
      i = fillTokens(tks, i, helper.getNbOrange(), 'O');
      i = fillTokens(tks, i, helper.getNbCyan(),   'C');
    }
    return tks;
  }

  private static int fillTokens(Token[] tks, int i, int nb, char c) {
    if (nb > 0) {
      Token token = new Token();
      tks[i++] = token;
      token.nb = nb;
      token.c = c;
    }
    return i;
  }
}
