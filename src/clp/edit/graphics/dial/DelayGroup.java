package clp.edit.graphics.dial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import clp.edit.GeneralContext;
import clp.run.res.Unit;

public class DelayGroup implements Serializable {

  private static final long serialVersionUID = -3202203803284873232L;

  private String timeIdentifier;
  private JTextField stepfield;
  private JTextField delayfield;
  transient private JComboBox<Unit> unitfield;
  private Unit selectedUnit;

  public DelayGroup(int delayNo) {
    timeIdentifier = "t" + delayNo;
    stepfield = new JTextField(5);
    stepfield.setText(GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getCurrentContainer().getStringActionNo());
    delayfield = new JTextField(5);
    delayfield.setText("1");
    setUnit(Unit.SECONDS);
  }

  public void parse(String t) {
    if (t != null && !t.isEmpty()) {
      if (t.charAt(0) == 't') {
        String[] sp = t.split("/");
        try {
          if (sp.length != 3) {
            System.err.println("ERROR found on parsing delay term "+t);
          }
          else {
            String s = sp[1].trim().toUpperCase();
            if (s.equals("C")) {
              stepfield.setText(s);
            }
            else {
              int stepnumber = Integer.parseInt(s);
              stepfield.setText(""+stepnumber);
            }
            String[] ssp = sp[2].trim().split(" ");
            int delay = Integer.parseInt(ssp[0].trim());
            String uni = ssp[1].trim();
            for (Unit u : Unit.values()) {
              if (u.getVal().equalsIgnoreCase(uni)) {
                selectedUnit = u;
                getUnitfield().setSelectedItem(u);
                break;
              }
            }
            delayfield.setText(""+delay);
          }
        }
        catch (NumberFormatException e) {
          System.err.println("ERROR found on parsing delay number "+t);
        }
      }
    }
  }

  /**
   * @return the stepfield
   */
  public JTextField getStepfield() {
    return stepfield;
  }

  /**
   * @param stepfield the stepfield to set
   */
  public void setStepfield(JTextField stepfield) {
    this.stepfield = stepfield;
  }

  /**
   * @return the delayfield
   */
  public JTextField getDelayfield() {
    return delayfield;
  }

  /**
   * @param delayfield the delayfield to set
   */
  public void setDelayfield(JTextField delayfield) {
    this.delayfield = delayfield;
  }

  /**
   * @return the unitfield
   */
  public JComboBox<Unit> getUnitfield() {
    if (unitfield == null) {
      unitfield = new JComboBox<Unit>(Unit.values());
      unitfield.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          selectedUnit = (Unit) unitfield.getSelectedItem();
        }
      });
      unitfield.setSelectedItem(selectedUnit);
    }
    return unitfield;
  }

  @Override
  public String toString() {
    return timeIdentifier + " / " + stepfield.getText() + " / " + delayfield.getText() + " " + getUnit().getVal();
  }

  public String getStep(char prefix) {
    String s = stepfield.getText().trim().toUpperCase();
    if (s.equals("C")) {
      return "C";
    }
    return prefix + s;
  }

  public int getDelay() {
    try {
      return Integer.parseInt(delayfield.getText().trim());
    }
    catch (NumberFormatException e) {
      System.err.println("ERROR found on parsing delay number "+delayfield.getText());
    }
    return 0;
  }

  /**
   * @return the timeIdentifier
   */
  public String getTimeIdentifier() {
    return timeIdentifier;
  }

  public Unit getUnit() {
    if (selectedUnit == null) {
      if (unitfield == null) {
        setUnit(Unit.SECONDS);
      }
      else {
        selectedUnit = (Unit) unitfield.getSelectedItem();
      }
    }
    return selectedUnit;
  }

  public void setUnit(Unit selUnit) {
    unitfield = new JComboBox<>(Unit.values());
    unitfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedUnit = (Unit) unitfield.getSelectedItem();
      }
    });
    unitfield.setSelectedItem(selUnit);
    selectedUnit = selUnit;
  }
}
