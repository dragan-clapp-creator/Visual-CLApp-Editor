package clp.edit.graphics.code;

public interface InstructionDialog {

  public String getRefName();

  public String getInstructionName();

  public String getInstructionContent();

  public boolean isOk();

  public void reset();

  public ClappInstruction getInstruction();

  public void setInstruction(ClappInstruction inst);

  public boolean setupInstruction();
}