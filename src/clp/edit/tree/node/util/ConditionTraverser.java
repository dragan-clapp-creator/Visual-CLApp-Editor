package clp.edit.tree.node.util;

import clp.run.cel.log.LogicalExp;
import clp.run.cel.log.LogicalFactor;
import clp.run.cel.log.LogicalTerm;
import clp.run.cel.log.Operand;

public class ConditionTraverser {

  public String getCondition(LogicalExp logicalExp) {
    String condition = getString(logicalExp.getLogicalTerm());
    if (logicalExp.hasLogicalTerms()) {
      for (LogicalTerm term : logicalExp.getLogicalTerms()) {
        condition += " OR " + getString(term);
      }
    }
    return condition;
  }

  //
  protected String getString(LogicalTerm logicalTerm) {
    String condition = getString(logicalTerm.getLogicalFactor());
    if (logicalTerm.hasLogicalFactors()) {
      for (LogicalFactor fact : logicalTerm.getLogicalFactors()) {
        condition += " AND " + getString(fact);
      }
    }
    return condition;
  }

  //
  protected String getString(LogicalFactor logicalFactor) {
    String condition = "";
    if (logicalFactor.isNOT()) {
      condition = "NOT ";
    }
    if (logicalFactor.getBvariable() != null) {
      condition += logicalFactor.getBvariable().getVar();
    }
    if (logicalFactor.getCellFunction() != null) {
      condition += logicalFactor.getCellFunction().getFunction().getVal();
      condition += "(" + logicalFactor.getCellFunction().getCellName() + ")";
    }
    if (logicalFactor.getComparison() != null) {
      ExpressionTraverser et = new ExpressionTraverser();
      condition += et.getExpressionAsString(logicalFactor.getComparison().getExp1());
      condition += " " + logicalFactor.getComparison().getOpComp().getVal() + " ";
      condition += et.getExpressionAsString(logicalFactor.getComparison().getExp2());
    }
    if (logicalFactor.getLexpression() != null) {
      condition += "(" + getCondition(logicalFactor.getLexpression().getLogicalExp()) + ")";
    }
    if (logicalFactor.getSysFunction() != null) {
      condition += logicalFactor.getSysFunction().getFunction().getVal();
      condition += "(" + logicalFactor.getSysFunction().getName() + ", "
                       + getString(logicalFactor.getSysFunction().getOperand()) + ")";
    }
    if (logicalFactor.getTautology() != null) {
      condition += "TRUE";
    }
    return condition;
  }

  //
  private String getString(Operand operand) {
    if (operand.getCst() != null) {
      return "\"" + operand.getCst() + "\"";
    }
    if (operand.getLevel() != null) {
      return operand.getLevel().toString();
    }
    return operand.getName();
  }
}
