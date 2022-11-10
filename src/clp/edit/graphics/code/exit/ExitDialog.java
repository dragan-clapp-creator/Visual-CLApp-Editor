package clp.edit.graphics.code.exit;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import clp.edit.graphics.code.AInstructionDialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;

public class ExitDialog extends AInstructionDialog {

  private static final long serialVersionUID = -7529011755820446755L;

  private ClappInstruction instruction;

  public ExitDialog(Frame owner) {
    super(owner, "Adding a stop statement", true);
  }

  public ExitDialog(Frame owner, ClappInstruction inst) {
    super(owner, "Adding a stop statement", false);
    instruction = inst;
  }

  @Override
  public String getRefName() {
    return "Stop";
  }

  @Override
  public String getInstructionName() {
    return "exit";
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
    instruction = new ClappInstruction();
    instruction.setColor(Color.yellow);
    String str = "exit ;";
    instruction.setStatement(str);
    instruction.setIntructionType(IntructionType.STOP.name());
    return true;
  }

  @Override
  public void fillContent() {
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.VERTICAL;
    c.gridy = 0;
    c.gridx = 1;
    getContentPane().add(new JTextField(" EXIT "), c);
    c.gridy++;
    c.gridx = 1;
    getContentPane().add(getCancelButton(), c);
    c.gridx = 3;
    getContentPane().add(getOkButton(), c);
  }
}
