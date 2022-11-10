package clp.edit.graphics.code;

import java.io.Serializable;

import clp.run.cel.dom.Command;

public interface InstructionInfo extends Serializable {

  public String getContent();
  public String getText();
  public Command getCommand();
  public void validate();
  public ClappInstruction getInstruction();
}
