package clp.edit.graphics.panel.cntrl;

public interface IOutput {

  public int getLine();
  public int getCol();
  public String getText();
  public void setCol(int col);
  public void setLine(int line);
}
