package clp.edit.graphics.dial;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.graphics.shapes.pn.PetriNetsShape;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;


public class PNCTransitionDialog extends PNTransitionDialog {

  private static final long serialVersionUID = 1730615911156429761L;

  private enum UpWeightingType {
    LEFT("Up Left"), MIDDLE("Up Middle"), RIGHT("Up Right");
    private String value;
    private UpWeightingType(String s) {
      value = s;
    }
    static UpWeightingType getName(String selectedItem) {
      UpWeightingType[] names = values();
      for (UpWeightingType t : names) {
        if (t.value.equalsIgnoreCase(selectedItem)) {
          return t;
        }
      }
      return null;
    }
    public String getVal() {
      return value;
    }
  }
  private ArrayList<String> bititles;
  private ArrayList<String> trititles;

  private UpWeightingType selectedUpType;
  private int selectedDownIndex;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param pnShape
   * @param transitionNodeShape 
   * @param tt: transition type 
   * @param tp: transition position 
   * @param height 
   */
  public PNCTransitionDialog(Frame parent, PetriNetsShape pnShape, TransitionNodeShape transitionNodeShape, TransitionType tt, TransitionPosition tp, int height) {
    super(parent, pnShape, transitionNodeShape, tt, tp, height);
    selectedUpType = UpWeightingType.LEFT;
    selectedDownIndex = 0;
    bititles = new ArrayList<>();
    trititles = new ArrayList<>();
    bititles.add("Down Left");
    bititles.add("Down Right");
    trititles.add("Down Left");
    trititles.add("Down Middle");
    trititles.add("Down Right");
  }

  //
  private JPanel createRadioButtonsForUpWeighting(UpWeightingType exclude) {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        "Choose Up Branch");
    jp.setBorder(border);

    ItemListener radioListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          selectedUpType = UpWeightingType.getName(rb.getText());
          refresh();
        }
      }
    };
    ButtonGroup group = new ButtonGroup();
    for (UpWeightingType t : UpWeightingType.values()) {
      if (t != exclude) {
        JRadioButton rb = new JRadioButton(t.getVal(), true);
        jp.add(rb); rb.addItemListener(radioListener);
        if (t == selectedUpType) {
          if (!rb.isSelected()) {
            rb.setSelected(true);
          }
        }
        else if (rb.isSelected()) {
          rb.setSelected(false);
        }
        group.add(rb);
      }
    }
    return jp;
  }

  //
  private JPanel createRadioButtonsForDownWeighting(ArrayList<String> titles) {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        "Choose Down Branch");
    jp.setBorder(border);

    ItemListener radioListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          selectedDownIndex = titles.indexOf( rb.getText() );
          refresh();
        }
      }
    };
    ButtonGroup group = new ButtonGroup();
    for (String t : titles) {
      JRadioButton rb = new JRadioButton(t, true);
      jp.add(rb); rb.addItemListener(radioListener);
      if (t.equals(titles.get(selectedDownIndex))) {
        if (!rb.isSelected()) {
          rb.setSelected(true);
        }
      }
      else if (rb.isSelected()) {
        rb.setSelected(false);
      }
      group.add(rb);
    }
    return jp;
  }

  @Override
  protected  void createUpWeightingPanels(GridBagConstraints c) {
    if (getUplefthelper() == null && getUprighthelper() == null) {
      addComponent(createWeightingPanel(getUphelper(), "Up"), 1, 1, 10, c);
      return;
    }
    else if (getUplefthelper() != null && getUprighthelper() != null) {
      addComponent(createRadioButtonsForUpWeighting(null), 1, 1, 10, c);
    }
    else if (getUplefthelper() != null) {
      addComponent(createRadioButtonsForUpWeighting(UpWeightingType.RIGHT), 1, 1, 10, c);
    }
    else {
      addComponent(createRadioButtonsForUpWeighting(UpWeightingType.LEFT), 1, 1, 10, c);
    }
    switch (selectedUpType) {
      case LEFT:
        if (getUplefthelper() != null) {
          addComponent(createWeightingPanel(getUplefthelper(), "Up Left"),   1, 2, 10, c);
        }
        break;
      case MIDDLE:
        if (getUphelper() != null) {
          addComponent(createWeightingPanel(getUphelper(), "Up"),            1, 2, 10, c);
        }
        break;
      case RIGHT:
        if (getUprighthelper() != null) {
          addComponent(createWeightingPanel(getUprighthelper(), "Up Right"), 1, 2, 10, c);
        }
        break;

      default:
        break;
    }
  }

  @Override
  protected  void createDownWeightingPanels(GridBagConstraints c) {
    switch (getDowntype()) {
      case MONO:
        addComponent(createWeightingPanel(getDownhelper(), "Down"), 1, 3, 10, c);
        break;
      case BI:
        addComponent(createRadioButtonsForDownWeighting(bititles), 1, 3, 10, c);
        if (selectedDownIndex == 0) {
          addComponent(createWeightingPanel(getDownlefthelper(), bititles.get(0)),  1, 4, 10, c);
        }
        else {
          addComponent(createWeightingPanel(getDownhelper(), bititles.get(1)), 1, 4, 10, c);
        }
        break;
      case TRI:
        addComponent(createRadioButtonsForDownWeighting(trititles), 1, 3, 10, c);
        switch (selectedDownIndex) {
          case 0:
            addComponent(createWeightingPanel(getDownlefthelper(), trititles.get(0)),  1, 4, 10, c);
            break;
          case 1:
            addComponent(createWeightingPanel(getDownhelper(), trititles.get(1)),      1, 4, 10, c);
            break;
          case 2:
            addComponent(createWeightingPanel(getDownrighthelper(), trititles.get(2)), 1, 4, 10, c);
            break;

          default:
            break;
        }
        break;

      default:
        break;
    }
  }

  @Override
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
    jp.add(new JLabel("Red"), gc);
    gc.gridx++;
    jp.add(new JLabel("Green"), gc);
    gc.gridx++;
    jp.add(new JLabel("Blue"), gc);
    gc.gridx++;
    jp.add(new JLabel("Yellow"), gc);
    gc.gridx++;
    jp.add(new JLabel("Orange"), gc);
    gc.gridx++;
    jp.add(new JLabel("Cyan"), gc);

    gc.gridy = 1;
    gc.gridx = 0;
    jp.add(helper.createField("Red", ""+helper.getNbRed()), gc);
    gc.gridx++;
    jp.add(helper.createField("Green", ""+helper.getNbGreen()), gc);
    gc.gridx++;
    jp.add(helper.createField("Blue", ""+helper.getNbBlue()), gc);
    gc.gridx++;
    jp.add(helper.createField("Yellow", ""+helper.getNbYellow()), gc);
    gc.gridx++;
    jp.add(helper.createField("Orange", ""+helper.getNbOrange()), gc);
    gc.gridx++;
    jp.add(helper.createField("Cyan", ""+helper.getNbCyan()), gc);

    return jp;
  }
}
