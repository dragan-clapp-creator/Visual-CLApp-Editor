package clp.edit.graphics.dial;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.pn.PetriNetsShape;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;


public class PNTransitionDialog extends TransitionDialog {

  private static final long serialVersionUID = -6433146820679639497L;

  private static final Insets insets = new Insets(3, 3, 3, 3);

  protected enum DownWeightingType {
    MONO, BI, TRI;
  }

  public enum TransitionPosition {
    INITIAL, MIDDLE, FINAL;
  }

  private boolean isNotFirstCall;

  private PNDialogHelper uphelper;
  private PNDialogHelper uplefthelper;
  private PNDialogHelper uprighthelper;

  private PNDialogHelper downhelper;
  private PNDialogHelper downlefthelper;
  private PNDialogHelper downrighthelper;

  private TransitionNodeShape transitionNodeShape;

  private DownWeightingType downtype;


  /**
   * CONSTRUCTOR
   * 
   * @param frame
   * @param pnShape
   * @param transitionNodeShape 
   * @param tt: transition type 
   * @param tp: transition position 
   * @param height 
   */
  public PNTransitionDialog(Frame frame, PetriNetsShape pnShape, TransitionNodeShape transitionNodeShape, TransitionType tt, TransitionPosition tp, int height) {
    super(frame, transitionNodeShape, null, height);
    this.transitionNodeShape = transitionNodeShape;
    pnsetup(pnShape);
  }

  //
  public void pnsetup(PetriNetsShape pnShape) {
    if (pnShape != null) {
      if (transitionNodeShape.getTrPos() != TransitionPosition.INITIAL) {
        uphelper = new PNDialogHelper();
        countTokens(uphelper, pnShape.getWeightOrColor());
      }
      if (transitionNodeShape.getTrPos() != TransitionPosition.FINAL) {
        downhelper = new PNDialogHelper();
        countTokens(downhelper, pnShape.getWeightOrColor());
        downtype = DownWeightingType.MONO;
      }
    }
    else {
      if (transitionNodeShape.getTrPos() != TransitionPosition.INITIAL) {
        ABindingShape b = transitionNodeShape.getParent();
        if (b instanceof TriBinding) {
          TriBinding tri = (TriBinding) b;
          if (tri.getLeft() != null) {
            if (uplefthelper == null) {
              uplefthelper = new PNDialogHelper();
            }
            countTokens(uplefthelper, tri.getLeft().getText());
          }
          else {
            uplefthelper = null;
          }
          if (tri.getMiddle() != null) {
            if (uphelper == null) {
              uphelper = new PNDialogHelper();
            }
            countTokens(uphelper, tri.getMiddle().getText());
          }
          else {
            uphelper = null;
          }
          if (tri.getRight() != null) {
            if (uprighthelper == null) {
              uprighthelper = new PNDialogHelper();
            }
            countTokens(uprighthelper, tri.getRight().getText());
          }
          else {
            uprighthelper = null;
          }
        }
        else {
          countTokens(uphelper, transitionNodeShape.getParent().getText());
        }
      }
      if (transitionNodeShape.getTrPos() != TransitionPosition.FINAL) {
        ABindingShape b = transitionNodeShape.getChild();
        if (b instanceof TriBinding) {
          downtype = DownWeightingType.TRI;
          TriBinding tri = (TriBinding) b;
          if (downlefthelper == null) {
            downlefthelper = new PNDialogHelper();
          }
          if (downrighthelper == null) {
            downrighthelper = new PNDialogHelper();
          }
          if (tri.getLeft() != null) {
            countTokens(downlefthelper, tri.getLeft().getText());
          }
          else {
            downtype = DownWeightingType.BI;
          }
          if (tri.getMiddle() != null) {
            countTokens(downhelper, tri.getMiddle().getText());
          }
          if (tri.getRight() != null) {
            countTokens(downrighthelper, tri.getRight().getText());
          }
          else {
            downtype = DownWeightingType.BI;
          }
        }
        else if (b != null) {
          countTokens(downhelper, b.getText());
          downtype = DownWeightingType.MONO;
        }
      }
    }
  }

  @Override
  public void edit(String t, String d) {
    pnsetup(null);
    defineOwnContent();
    super.edit(t, d);
  }

  @Override
  public String getTransitionText() {
    if (transitionNodeShape.getTrPos() != TransitionPosition.INITIAL) {
      ABindingShape b = transitionNodeShape.getParent();
      if (b instanceof TriBinding) {
        TriBinding tri = (TriBinding) b;
        if (tri.getLeft() != null) {
          tri.getLeft().setText(uplefthelper.toString());
        }
        if (tri.getMiddle() != null) {
          tri.getMiddle().setText(uphelper.toString());
        }
        if (tri.getRight() != null) {
          tri.getRight().setText(uprighthelper.toString());
        }
      }
      else {
        if (transitionNodeShape.getParent() != null) {
          transitionNodeShape.getParent().setText(uphelper.toString());
        }
      }
    }
    if (transitionNodeShape.getTrPos() != TransitionPosition.FINAL) {
      if (transitionNodeShape.getChild() != null) {
        ABindingShape b = transitionNodeShape.getChild();
        if (b instanceof TriBinding) {
          ((TriBinding) b).getLeft().setText(downlefthelper.toString());
          ((TriBinding) b).getMiddle().setText(downhelper.toString());
          if (((TriBinding) b).getRight() != null) {
            ((TriBinding) b).getRight().setText(downrighthelper.toString());
          }
        }
        else {
          b.setText(downhelper.toString());
        }
      }
    }
    return super.getTransitionText();
  }

  //
  private void countTokens(PNDialogHelper helper, String text) {
    if (text == null || text.isEmpty()) {
      helper.setNbBlack(1);
    }
    else {
      String[] sp = text.split("\\.");
      for (int i=0; i<sp.length; i++) {
        String x = sp[i];
        if (x.length() == 1) {
          helper.setNb(x.charAt(0), 1);
        }
        else {
          int n = Integer.parseInt(""+x.charAt(0));
          helper.setNb(x.charAt(1), n);
        }
      }
    }
  }

  @Override
  public void defineContent(GridBagConstraints gc, JButton okButton) {
    if (isNotFirstCall) {
      defineOwnContent();
    }
    else {
      isNotFirstCall = true;
    }
  }

  //
  protected void addComponent(Component component, int gridx, int gridy, int gridwidth, GridBagConstraints c) {
    c.gridx = gridx;
    c.gridy = gridy;
    c.gridwidth = gridwidth;
    getContentPane().add(component, c);
  }

  //
  private void defineOwnContent() {
    getContentPane().removeAll();
    GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0);

    addComponent(new JLabel("Transition Type"), 0, 0,  1, c);
    JPanel jp1 = createRadioButtonsForType();
    addComponent(jp1,                           1, 0, 10, c);

    if (transitionNodeShape.getTrPos() != TransitionPosition.INITIAL) {
      createUpWeightingPanels(c);
    }

    if (transitionNodeShape.getTrPos() != TransitionPosition.FINAL) 
      createDownWeightingPanels(c);

    addComponent(new JLabel("Crossing"),        0, 5,  1, c);
    boolean isDescriptionRequested = createCrossingFromType(c);

    if (isDescriptionRequested) {
      addComponent(new JLabel("Description"),                       0, 6, 1, c);
      addComponent(getClassicInfo().getEventDescriptionField(),     1, 6, 2, c);
      addComponent(getClassicInfo().getConditionDescriptionField(), 3, 6, 5, c);
      addComponent(Box.createVerticalStrut(9),                      8, 6, 3, c);
    }

    c.fill = GridBagConstraints.BOTH;
    addComponent(getOkButton(),                 0, 7, 2, c);
  }
  
  //
  protected boolean createCrossingFromType(GridBagConstraints c) {
    switch (transitionNodeShape.getTrType()) {
      case TAUTOLOGY:
        addComponent(getTautology(),             1, 5, 1, c);
        addComponent(Box.createVerticalStrut(9), 2, 5, 9, c);
        break;
      case CLASSICAL:
        addComponent(getClassicInfo().getArrowfield(),     1, 5, 1, c);
        addComponent(getClassicInfo().getEventfield(),     2, 5, 1, c);
        addComponent(getClassicInfo().getConditionfield(), 3, 5, 5, c);
        addComponent(Box.createVerticalStrut(9),           8, 5, 3, c);
        return true;
      case DELAY:
        addComponent(new JLabel("Step N°"),          1, 5, 1, c);
        addComponent(getDelayInfo().getStepfield(),  2, 5, 2, c);
        addComponent(new JLabel("Delay"),            4, 5, 1, c);
        addComponent(getDelayInfo().getDelayfield(), 5, 5, 2, c);
        addComponent(getDelayInfo().getUnitfield(),  7, 5, 2, c);
        addComponent(Box.createVerticalStrut(9),     9, 5, 2, c);
        break;

      default:
        break;
    }
    return false;
  }

  //
  protected  void createUpWeightingPanels(GridBagConstraints c) {
    if (uplefthelper == null && uprighthelper == null) {
      addComponent(createWeightingPanel(uphelper, "Up"), 1, 1, 3, c);
      addComponent(Box.createHorizontalGlue(),           4, 1, 7, c);
    }
    else if (uplefthelper != null && uprighthelper != null) {
      addComponent(createWeightingPanel(uplefthelper, "Up Left"),   1, 1, 3, c);
      addComponent(createWeightingPanel(uphelper, "Up"),            4, 1, 3, c);
      addComponent(createWeightingPanel(uprighthelper, "Up Right"), 7, 1, 4, c);
    }
    else if (uplefthelper != null) {
      addComponent(createWeightingPanel(uplefthelper, "Up Left"), 1, 1, 3, c);
      addComponent(createWeightingPanel(uphelper, "Up"),          4, 1, 3, c);
      addComponent(Box.createHorizontalGlue(),                    7, 1, 4, c);
    }
    else {
      addComponent(createWeightingPanel(uphelper, "Up"),            1, 1, 3, c);
      addComponent(createWeightingPanel(uprighthelper, "Up Right"), 4, 1, 3, c);
      addComponent(Box.createHorizontalGlue(),                      7, 1, 4, c);
    }
  }

  //
  protected void createDownWeightingPanels(GridBagConstraints c) {
    switch (downtype) {
      case MONO:
        addComponent(createWeightingPanel(downhelper, "Down"), 1, 3, 3, c);
        addComponent(Box.createHorizontalGlue(),               4, 3, 7, c);
        break;
      case BI:
        addComponent(createWeightingPanel(downlefthelper, "Down Left"),   1, 3, 3, c);
        addComponent(createWeightingPanel(downhelper, "Down Right"),      4, 3, 3, c);
        addComponent(Box.createHorizontalGlue(),                          7, 3, 4, c);
        break;
      case TRI:
        addComponent(createWeightingPanel(downlefthelper, "Down Left"),   1, 3, 3, c);
        addComponent(createWeightingPanel(downhelper, "Down"),            4, 3, 3, c);
        addComponent(createWeightingPanel(downrighthelper, "Down Right"), 7, 3, 4, c);
        break;

      default:
        break;
    }
  }

  //
  protected JPanel createWeightingPanel(PNDialogHelper helper, String string) {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            string + " Weighting"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();

    gc.gridy = 0;
    gc.gridx = 0;
    jp.add(new JLabel("Nb Tokens"), gc);
    
    gc.gridy = 1;
    jp.add(helper.createField("Black", ""+helper.getNbBlack()), gc);

    return jp;
  }

  public String getCheckMarks() {
    if (uphelper == null) {
      return null;
    }
    return "[ " + uphelper.getMarks() + " ]";
  }

  public String getSetMarks() {
    if (downhelper == null) {
      return null;
    }
    return "[ " + downhelper.getMarks() + " ]";
  }

  /**
   * @return the uphelper
   */
  public PNDialogHelper getUphelper() {
    return uphelper;
  }

  /**
   * @return the uplefthelper
   */
  public PNDialogHelper getUplefthelper() {
    return uplefthelper;
  }

  /**
   * @return the uprighthelper
   */
  public PNDialogHelper getUprighthelper() {
    return uprighthelper;
  }

  /**
   * @return the downhelper
   */
  public PNDialogHelper getDownhelper() {
    return downhelper;
  }

  /**
   * @return the downlefthelper
   */
  public PNDialogHelper getDownlefthelper() {
    return downlefthelper;
  }

  /**
   * @return the downrighthelper
   */
  public PNDialogHelper getDownrighthelper() {
    return downrighthelper;
  }

  /**
   * @return the downtype
   */
  public DownWeightingType getDowntype() {
    return downtype;
  }

  public void reinitializeHelpers() {
    if (uphelper != null) {
      uphelper.setNbRed(0);
    }
    if (uplefthelper != null) {
      uplefthelper.setNbRed(0);
    }
    if (uprighthelper != null) {
      uprighthelper.setNbRed(0);
    }
    if (downhelper != null) {
      downhelper.setNbRed(0);
    }
    if (downlefthelper != null) {
      downlefthelper.setNbRed(0);
    }
    if (downrighthelper != null) {
      downrighthelper.setNbRed(0);
    }
  }
}
