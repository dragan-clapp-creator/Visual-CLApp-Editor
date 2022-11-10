package clp.edit.graphics.shapes.pn;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

import clp.edit.GeneralContext;
import clp.edit.graphics.dial.ActionOrStepDialog;
import clp.edit.graphics.dial.PNTransitionDialog;
import clp.edit.graphics.dial.PlaceDialog;
import clp.edit.graphics.dial.Token;
import clp.edit.graphics.panel.ControlsContainer;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.util.CellInfo;
import clp.edit.handler.CLAppSourceHandler;
import clp.run.cel.Weightings;
import clp.run.res.Weighting;

public class PlaceNodeShape extends ActionShape {

  private static final long serialVersionUID = -2747985167995681728L;

  public class ColorInfo implements Serializable {
    private static final long serialVersionUID = 1973489769875258389L;
    private char c; // color ref
    private int dx; // relative x position
    private int dy; // relative y position

    public char getC() {
      return c;
    }
    public void setC(char c) {
      this.c = c;
    }
    public String toString() {
      return ""+c;
    }
  }

  private int placeNumber;

  private ColorInfo[] tokens;

  private PetriNetsShape petriNetsShape;

  transient private PlaceDialog dialog;

  private ColorInfo[] initialTokens;

  /**
   * CONSTRUCTOR
   * 
   * @param index
   * @param pnShape
   */
  public PlaceNodeShape(int index, PetriNetsShape pnShape) {
    super(40, 40, "P"+index, "Place ", 1);
    placeNumber = index;
    petriNetsShape = pnShape;
    dialog = (PlaceDialog) super.getDialog();
  }

  @Override
  public void setParent(ABindingShape parent) {
    super.setParent(parent);
    if (parent.getParent() instanceof InvisibleNode) {
      defineTokens();
      ((PlaceDialog)getDialog()).count(tokens);
      initialTokens = tokens;
    }
  }

  public void resetTokens(CLAppSourceHandler clAppSourceHandler) {
    tokens = initialTokens;
    ((PlaceDialog)getDialog()).recount(tokens);
    clAppSourceHandler.addTokensToRes(((PlaceDialog)getDialog()).getTokens(), getName());
  }

  //
  private void defineTokens() {
    if (petriNetsShape.isColored()) {
      String s = petriNetsShape.getWeightOrColor();
      String[] sp = s.split("\\.");
      tokens = new ColorInfo[sp.length];
      for (int i=0; i<sp.length; i++) {
        String x = sp[i];
        if (x.length() == 1) {
          ColorInfo ci = new ColorInfo();
          ci.c = x.charAt(0);
          tokens[i] = ci;
        }
        else {
          int n = Integer.parseInt(""+x.charAt(0));
          char c_ref = x.charAt(1);
          for (int j=0; j<n; j++) {
            ColorInfo ci = new ColorInfo();
            ci.c = c_ref;
            tokens[i] = ci;
          }
        }
      }
      defineRelativePositions();
    }
    else {
      String s = petriNetsShape.getWeightOrColor();
      int nb = Integer.parseInt(s);
      tokens = new ColorInfo[nb];
      for (int i=0; i<nb; i++) {
        ColorInfo ci = new ColorInfo();
        ci.c = 'N';   // select black color
        tokens[i] = ci;
      }
      defineRelativePositions();
    }
  }

  /**
   * entries are bindings from transition nodes from below
   * @param b
   */
  @Override
  public void addEntryPoint(ABindingShape b) {
    if (!getEntries().contains(b)) {
      getEntries().add(b);
    }
    setTransportationDomainFromEntry(b);
  }

  //
  protected void updateCellInitialWeighting() {
    Token[] tokens = ((PlaceDialog)getDialog()).getTokens();
    if (tokens.length > 0) {
      ControlsContainer cc = GeneralContext.getInstance().getGraphicsPanel().getControlsContainer();
      cc.addTokensToRes(tokens, getName());
    }
  }

  //
  private void defineRelativePositions() {
    if (tokens == null) {
      return;
    }
    ColorInfo ci;
    int coloredTokenNumber = tokens.length;
    switch (coloredTokenNumber) {
      case 1:
        ci = tokens[0];
        ci.dx = -5;
        ci.dy = 16;
        break;
      case 2:
        ci = tokens[0];
        ci.dx = -15;
        ci.dy = 14;
        ci = tokens[1];
        ci.dx = 8;
        ci.dy = 14;
        break;
      case 3:
        ci = tokens[0];
        ci.dx = -15;
        ci.dy = 12;
        ci = tokens[1];
        ci.dx = 8;
        ci.dy = 12;
        ci = tokens[2];
        ci.dx = -5;
        ci.dy = 20;
        break;
      case 4:
        ci = tokens[0];
        ci.dx = -16;
        ci.dy = 13;
        ci = tokens[1];
        ci.dx = 7;
        ci.dy = 13;
        ci = tokens[2];
        ci.dx = -10;
        ci.dy = 25;
        ci = tokens[3];
        ci.dx = 3;
        ci.dy = 25;
        break;
      case 5:
        ci = tokens[0];
        ci.dx = -16;
        ci.dy = 13;
        ci = tokens[1];
        ci.dx = 7;
        ci.dy = 13;
        ci = tokens[2];
        ci.dx = -10;
        ci.dy = 25;
        ci = tokens[3];
        ci.dx = 3;
        ci.dy = 25;
        ci = tokens[4];
        ci.dx = -5;
        ci.dy = 3;
        break;
      case 6:
        ci = tokens[0];
        ci.dx = -16;
        ci.dy = 13;
        ci = tokens[1];
        ci.dx = 7;
        ci.dy = 13;
        ci = tokens[2];
        ci.dx = -10;
        ci.dy = 25;
        ci = tokens[3];
        ci.dx = 3;
        ci.dy = 25;
        ci = tokens[4];
        ci.dx = -10;
        ci.dy = 3;
        ci = tokens[5];
        ci.dx = 3;
        ci.dy = 3;
        break;

      default:
        break;
    }
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    int x = getPX()+offset;
    int y = getPY();
    Color c = g.getColor();
    if (getBgcolor() == null) {
      if (isSelected()) {
        g.setColor(Color.lightGray);
      }
      else {
        g.setColor(tokens == null || tokens.length < 7 ? Color.white : Color.red);
        g.fillOval(x-20, y, 40, 40);
        g.setColor(Color.black);
      }
    }
    else {
      g.setColor(getBgcolor());
      g.fillOval(x-24, y-4, 48, 48);
      g.setColor(Color.white);
      g.fillOval(x-20, y, 40, 40);
      g.setColor(Color.black);
    }
    if (tokens == null || tokens.length < 7) {
      g.drawOval(x-20, y, 40, 40);
      drawTokens(g, x, y);
    }
    else {
      g.setColor(Color.red);
      g.fillOval(x-20, y, 40, 40);
    }

    g.setColor(c);

    paintInstructionsFromShape(g, offset);

    g.drawString(""+placeNumber, x-5, getPY()+15);
    if (getChild() != null) {
      super.checkDownType();
      getChild().paintShape(g, offset);
    }
  }

  //
  private void drawTokens(Graphics g, int x, int y) {
    if (tokens != null) {
      Color c = g.getColor();
      for (ColorInfo ci : tokens) {
        g.setColor(getColor(ci.c));
        g.fillOval(x+ci.dx, y+ci.dy, 10, 10);
        
      }
      g.setColor(c);
    }
  }

  //
  private Color getColor(char c) {
    switch (c) {
      case 'R':
        return Color.red;
      case 'G':
        return Color.green;
      case 'B':
        return Color.blue;
      case 'Y':
        return Color.yellow;
      case 'O':
        return Color.orange;
      case 'C':
        return Color.cyan;

      default:
        break;
    }
    return Color.black;
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    String t = petriNetsShape.getWeightOrColor();
    PNBindingShape b = new PNBindingShape(bindingType, t);
    setChild(b);
    b.setChild(shape);
  }

  @Override
  public void setupInstructions() {
    super.setupInstructions();
    tokens = ((PlaceDialog)getDialog()).createTokens(this);
    initialTokens = tokens;
    defineRelativePositions();
    TransitionNodeShape tr = getTransitionAbowe();
    if (tr != null) {
      updateCellInitialWeighting();
    }
  }

  //
  private TransitionNodeShape getTransitionAbowe() {
    ABindingShape b = getParent();
    if (b != null) {
      AShape s = b.getParent();
      if (s instanceof TransitionNodeShape) {
        return (TransitionNodeShape) b.getParent();
      }
    }
    return null;
  }

  public void setTransportationDomainFromEntry(ABindingShape b) {
    setTransportationDomainForEntries();
  }

  //
  public void setTransportationDomainForEntries() {
    ArrayList<ABindingShape> list = getEntries();
    if (!getEntries().contains(getParent())) {
      list.add(0, getParent());
    }
  }

  public void setTokens(Weightings weightings) {
    ArrayList<Weighting> list = new ArrayList<>();
    Weighting w = weightings.getWeighting();
    if (w != null) {
      list.add(w);
    }
    list.addAll(weightings.getWeightings());
    ArrayList<ColorInfo> cis = new ArrayList<>();
    for (Weighting lw : list) {
      for (int i=0; i<lw.getWeight(); i++) {
        ColorInfo ci = new ColorInfo();
        ci.c = lw.getMark();
        cis.add(ci);
      }
    }
    tokens = new ColorInfo[cis.size()];
    for (int i=0; i<cis.size(); i++) {
      tokens[i] = cis.get(i);
    }
    defineRelativePositions();
  }

  public String getCheckMarks() {
    AShape t = getParent().getParent();
    if (t instanceof TransitionNodeShape) {
      return getName() + " [ " + ((PNTransitionDialog)t.getDialog()).getDownhelper().getMarks() + " ]";
    }
    return  getName() + " " + ((PlaceDialog)getDialog()).getCheckMarks();
  }

  @Override
  public ActionOrStepDialog getActionOrStepDialog() {
    if (dialog == null) {
      dialog = new PlaceDialog(GeneralContext.getInstance().getFrame(), petriNetsShape.isColored(), this, getInstructions());
    }
    return dialog;
  }

  @Override
  public String getActivationDomain(AContainer container) {
    setTokensForCell(((PlaceDialog)getDialog()).getTokens(), getName());
    return super.getActivationDomain(container);
  }

  @Override
  public boolean generateCode(AContainer container) {
    if (!container.isRegistered(getName())) {
      CellInfo info = new CellInfo(getName());
      fillContent(container, info);
      container.register(getName(), info, initialTokens != null && initialTokens.length > 0);
      if (getChild() != null) {
        if (!getChild().generateCode(container)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void cacheFromTransients() {
  }
}
