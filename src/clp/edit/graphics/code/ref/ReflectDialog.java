package clp.edit.graphics.code.ref;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import clp.edit.GeneralContext;
import clp.edit.graphics.code.AInstructionDialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.panel.GeneralShapesContainer;

public class ReflectDialog extends AInstructionDialog {

  private static final long serialVersionUID = -4728020839091718809L;

  private static final String[] types = { "DATA", "STRUCTURE", "ACTIVITY" };

  private JComboBox<String> combo;
  private JComboBox<String> console;

  private ClappInstruction instruction;

  public ReflectDialog(Frame owner) {
    super(owner, "Adding a reflect statement", true);
  }

  public ReflectDialog(Frame owner, ClappInstruction inst) {
    super(owner, "Adding a reflect statement", false);
    instruction = inst;
    combo.setSelectedIndex(instruction.getIndex());
  }

  @Override
  public String getRefName() {
    return "Reflect";
  }

  @Override
  public String getInstructionName() {
    return "reflect";
  }

  @Override
  public String getInstructionContent() {
    if (instruction == null || instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  @Override
  public void reset() {
    instruction.reset();
    console.removeAllItems();
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    console.addItem("");
    for (String n : shapesContainer.getCslInfoNames()) {
      console.addItem(n);
    }
  }

  @Override
  public ClappInstruction getInstruction() {
    return instruction;
  }

  @Override
  public void setInstruction(ClappInstruction inst) {
    instruction = inst;
  }

  @Override
  public boolean setupInstruction() {
    if (instruction == null) {
      instruction = new ClappInstruction();
    }
    instruction.setColor(Color.green);
    String str = "reflect " + combo.getSelectedItem();
    String cons = (String) console.getSelectedItem();
    if (cons == null || cons.isBlank()) {
      str += " to CONSOLE";
    }
    else {
      str += " to " + cons;
    }
    str += ";";
    instruction.setStatement(str);
    instruction.setIndex(combo.getSelectedIndex());
    instruction.setIntructionType(IntructionType.REFLECT.name());
    return true;
  }

  @Override
  public void fillContent() {
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.VERTICAL;
    c.gridy = 0;
    c.gridx = 1;
    combo = new JComboBox<>(types);
    getContentPane().add(combo, c);
    c.gridx = 2;
    getContentPane().add(new JLabel(" TO "), c);
    c.gridx = 3;
    console = new JComboBox<>();
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    for (String n : shapesContainer.getCslInfoNames()) {
      console.addItem(n);
    }
    getContentPane().add(console, c);
    c.gridy++;
    c.gridwidth = 2;
    c.gridx = 1;
    getContentPane().add(getCancelButton(), c);
    c.gridx = 3;
    getContentPane().add(getOkButton(), c);
  }
}
