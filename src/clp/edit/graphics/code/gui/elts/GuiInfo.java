package clp.edit.graphics.code.gui.elts;

import java.io.Serializable;
import java.util.ArrayList;

import clp.run.res.ui.UiVar;

public class GuiInfo implements Serializable {

  private static final long serialVersionUID = -5973279030810558849L;

  private UiVar uiVar;
  private ArrayList<String> variables;
  private String statement;

  public GuiInfo(UiVar uiVar, ArrayList<String> variables, String statement) {
    this.uiVar = uiVar;
    this.variables = variables;
    this.statement = statement;
  }

  /**
   * @return the uiVar
   */
  public UiVar getUiVar() {
    return uiVar;
  }

  /**
   * @param uiVar the uiVar to set
   */
  public void setUiVar(UiVar uiVar) {
    this.uiVar = uiVar;
  }

  /**
   * @return the variables
   */
  public ArrayList<String> getVariables() {
    return variables;
  }

  /**
   * @param variables the variables to set
   */
  public void setVariables(ArrayList<String> variables) {
    this.variables = variables;
  }

  /**
   * @return the statement
   */
  public String getDeclarationStatement() {
    return statement;
  }

  /**
   * @param statement the statement to set
   */
  public void setDeclarationStatement(String statement) {
    this.statement = statement;
  }
}
