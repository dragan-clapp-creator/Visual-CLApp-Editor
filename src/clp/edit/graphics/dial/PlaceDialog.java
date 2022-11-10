package clp.edit.graphics.dial;

import java.awt.Frame;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.pn.PlaceNodeShape;
import clp.edit.graphics.shapes.pn.PlaceNodeShape.ColorInfo;


public class PlaceDialog extends ActionOrStepDialog {

  private static final long serialVersionUID = 3157746775633064158L;

  private PNDialogHelper helper;

  private boolean isColored;

  private boolean isPlaceSetup;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param isColored 
   * @param caller 
   * @param list 
   */
  public PlaceDialog(Frame parent, boolean isColored, ActionShape caller, List<ClappInstruction> list) {
    super(parent, caller, list);
    helper = new PNDialogHelper();
    this.isColored = isColored;
    defineOwnContent();
    isPlaceSetup = true;
  }

  //
  public void count(ColorInfo[] tokens) {
    for (ColorInfo ci : tokens) {
      switch (ci.getC()) {
        case 'R':
          helper.incNbRed();
          break;
        case 'G':
          helper.incNbGreen();
          break;
        case 'B':
          helper.incNbBlue();
          break;
        case 'Y':
          helper.incNbYellow();
          break;
        case 'O':
          helper.incNbOrange();
          break;
        case 'C':
          helper.incNbCyan();
          break;

        default:
          helper.incNbBlack();
          break;
      }
    }
  }

  public void recount(ColorInfo[] tokens) {
    helper.resetAll();
    if (tokens != null) {
      count(tokens);
    }
    defineOwnContent();
  }

  @Override
  public void defineContent() {
    if (isPlaceSetup) {
      defineOwnContent();
      pack(); 
    }
  }

  @Override
  public void edit(String t, String d) {
    defineOwnContent();
    super.edit(t, d);
  }

  public void defineOwnContent() {
    getContentPane().removeAll();

    getContentPane().add(createDescriptionPanel());
    getContentPane().add(createWeightingPanel());
    getContentPane().add(createInstructionsPanel());
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(), 4, 1, 6, 6, 6, 6);
  }

  @Override
  public JPanel createDescriptionPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Place Description");
    p.setBorder(border);

    JLabel l = new JLabel("Place name:", JLabel.TRAILING);
    p.add(l);
    l.setLabelFor(getNamefield());
    p.add(getNamefield());

    l = new JLabel("Place description:", JLabel.TRAILING);
    p.add(l);
    l.setLabelFor(getDescriptionfield());
    p.add(getDescriptionfield());

    makeCompactGrid(p, 2, 2, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private JPanel createWeightingPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Weighting");
    p.setBorder(border);

    if (isColored) {
      p.add(new JLabel("Red"));
      p.add(new JLabel("Green"));
      p.add(new JLabel("Blue"));
      p.add(new JLabel("Yellow"));
      p.add(new JLabel("Orange"));
      p.add(new JLabel("Cyan"));

      p.add(helper.createField("Red", ""+helper.getNbRed()));
      p.add(helper.createField("Green", ""+helper.getNbGreen()));
      p.add(helper.createField("Blue", ""+helper.getNbBlue()));
      p.add(helper.createField("Yellow", ""+helper.getNbYellow()));
      p.add(helper.createField("Orange", ""+helper.getNbOrange()));
      p.add(helper.createField("Cyan", ""+helper.getNbCyan()));

      makeCompactGrid(p, 2, 6, 6, 6, 6, 6);
    }
    else {
      p.add(new JLabel("Nb Tokens"));
      p.add(helper.createField("Black", ""+helper.getNbBlack()));
      makeCompactGrid(p, 2, 1, 6, 6, 6, 6);
    }

    p.setOpaque(true);
    return p;
  }

  public ColorInfo[] createTokens(PlaceNodeShape placeNodeShape) {
    if (!isColored) {
      ColorInfo[] tokens = new ColorInfo[helper.getNbBlack()];
      create(tokens, placeNodeShape, 0, helper.getNbBlack(), 'N');
      return tokens;
    }
    ColorInfo[] tokens = new ColorInfo[helper.getAll()];
    int index = create(tokens, placeNodeShape, 0,     helper.getNbRed(),    'R');
        index = create(tokens, placeNodeShape, index, helper.getNbGreen(),  'G');
        index = create(tokens, placeNodeShape, index, helper.getNbBlue(),   'B');
        index = create(tokens, placeNodeShape, index, helper.getNbYellow(), 'Y');
        index = create(tokens, placeNodeShape, index, helper.getNbOrange(), 'O');
                create(tokens, placeNodeShape, index, helper.getNbCyan(),   'C');
    return tokens;
  }

  //
  private int create(ColorInfo[] tokens, PlaceNodeShape placeNodeShape, int index, int nb, char c) {
    for (int i=index; i<index+nb; i++) {
      ColorInfo ci = placeNodeShape.new ColorInfo();
      ci.setC(c);
      tokens[i] = ci;
    }
    return index+nb;
  }

  public Token[] getTokens() {
    return Token.getTokens(helper);
  }

  public String getCheckMarks() {
    return "[ " + helper.getMarks() + " ]";
  }
}
