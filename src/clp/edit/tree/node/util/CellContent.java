package clp.edit.tree.node.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import clp.edit.GeneralContext;
import clp.run.cel.dom.Adomain;
import clp.run.cel.dom.CommandLine;
import clp.run.cel.dom.Ddomain;
import clp.run.cel.dom.Xdomain;
import clp.run.cel.log.InitialATokens;
import clp.run.cel.log.LogicalExpression;
import clp.run.cel.log.LogicalFactor;
import clp.run.cel.log.LogicalTerm;
import clp.run.cel.log.LogicalTerms;
import clp.run.cel.log.TokenAMarking;
import clp.run.cel.log.TokenAMarkingVisitor;
import clp.run.cel.log.TransposingLine;
import clp.run.res.CellMarkCheck;
import clp.run.res.CellMarkSet;
import clp.run.res.CellMarks;
import clp.run.res.Weighting;

public class CellContent implements IContent, Serializable {

  private static final long serialVersionUID = 7624629425984689672L;

  private Adomain adomain;
  private Ddomain ddomain;
  private Xdomain xdomain;
  private ArrayList<Statement> aconditions;
  private ArrayList<Statement> dconditions;
  private ArrayList<Statement> statements;

  public CellContent() {
    aconditions = new ArrayList<>();
    dconditions = new ArrayList<>();
    statements = new ArrayList<>();
  }

  /**
   * @return the adomain
   */
  public Adomain getAdomain() {
    return adomain;
  }

  /**
   * @param adomain the adomain to set
   */
  public void setAdomain(Adomain adomain) {
    this.adomain = adomain;
    if (adomain != null && adomain.getAd() != null) {
      extractTranspositions(adomain.getAd().getTransposingLines(), aconditions);
    }
  }

  //
  private void extractTranspositions(ArrayList<TransposingLine> tlines, ArrayList<Statement> cnds) {
    for (TransposingLine tl : tlines) {
      Statement cnd = new Statement();
      aconditions.add(cnd);
      if (tl.isLevel()) {
        cnd.setLevel(""+tl.getLevel());
      }
      if (tl.isNext()) {
        cnd.setNext(""+tl.getNext());
      }
      extractTMarks(tl.getTokenAMarking(), tl.getOutputs(), cnd);
    }
  }

  //
  private void extractDConditions(ArrayList<LogicalExpression> expList) {
    for (LogicalExpression exp : expList) {
      Statement stat = new Statement();
      dconditions.add(stat);
      if (exp.isLevel()) {
        stat.setLevel(""+exp.getLevel());
      }
      if (exp.isNext()) {
        stat.setNext(""+exp.getNext());
      }
      fillLogicalTerms(exp.getLogicalTerms(), stat);
    }
  }

  //
  private void extractTMarks(TokenAMarking t, CellMarkCheck outputs, Statement cnd) {
    t.accept(new TokenAMarkingVisitor() {
      @Override
      public void visitLogicalTerms(LogicalTerms x) {
        fillLogicalTerms(x, cnd);
      }
      @Override
      public void visitInitialATokens(InitialATokens x) {
        fillInitialMarks(x, cnd, outputs);
      }
    });
  }

  //
  private void fillLogicalTerms(LogicalTerms x, Statement cnd) {
    ArrayList<LogicalTerm> logicalTerms = new ArrayList<>();
    LogicalTerm lfact = x.getLogicalTerm();
    if (lfact != null) {
      logicalTerms.add(lfact);
    }
    if (x.getLogicalTerms() != null) {
      logicalTerms.addAll(x.getLogicalTerms());
    }
    extractConditions(logicalTerms, cnd);
  }

  //
  private void fillInitialMarks(InitialATokens x, Statement cnd, CellMarkCheck outputs) {
    CellMarkSet inputs = x.getEntries();
    List<CellMarks> list = new ArrayList<>();
    CellMarks cm = inputs.getCellMarks();
    if (cm != null) {
      list.add(cm);
    }
    list.addAll(inputs.getCellMarkss());
    String line = "";
    for (int i=0; i<list.size(); i++) {
      CellMarks lcm = list.get(i);
      if (i > 0) {
        line += ", ";
      }
      line += lcm.getCellName() + " " +extractMarks(lcm.getCellMarkCheck());
    }
    cnd.setInputMarks(line);
    extractTConditions(x.getLogicalFactors(), cnd);
    cnd.setOuputMarks(extractMarks(outputs));
  }

  //
  private String extractMarks(CellMarkCheck cellMarkCheck) {
    if (cellMarkCheck != null) {
      List<Weighting> list = new ArrayList<>();
      Weighting w = cellMarkCheck.getWeighting();
      if (w != null) {
        list.add(w);
      }
      list.addAll(cellMarkCheck.getWeightings());
      return list.toString();
    }
    return "";
  }

  //
  private void extractConditions(ArrayList<LogicalTerm> factList, Statement stmt) {
    for (LogicalTerm fact : factList) {
      ConditionTraverser cct = new ConditionTraverser();
      stmt.setStatement(cct.getString(fact));
    }
  }

  //
  private void extractTConditions(ArrayList<LogicalFactor> factList, Statement stmt) {
    for (LogicalFactor fact : factList) {
      ConditionTraverser cct = new ConditionTraverser();
      stmt.setStatement(cct.getString(fact));
    }
  }

  /**
   * @return the ddomain
   */
  public Ddomain getDdomain() {
    return ddomain;
  }

  /**
   * @param ddomain the ddomain to set
   */
  public void setDdomain(Ddomain ddomain) {
    this.ddomain = ddomain;
    if (ddomain != null && ddomain.getDd() != null) {
      extractDConditions(ddomain.getDd().getLogicalExpressions());
    }
  }

  /**
   * @return the xdomain
   */
  public Xdomain getXdomain() {
    return xdomain;
  }

  /**
   * @param xdomain the xdomain to set
   */
  public void setXdomain(Xdomain xdomain) {
    this.xdomain = xdomain;
    if (xdomain != null && xdomain.getXd() != null) {
      extractInstructions();
      if (!statements.isEmpty()) {
        GeneralContext.getInstance().getClappEditor().enableExport();
      }
    }
  }

  //
  private void extractInstructions() {
    ArrayList<CommandLine> cmds = xdomain.getXd().getCommandLines();
    for (CommandLine cl : cmds) {
      Statement stat = new Statement();
      if (cl.isLevel()) {
        stat.setLevel(""+cl.getLevel());
      }
      if (cl.isNext()) {
        stat.setNext(""+cl.getNext());
      }
      CellCommandVisitor vis = new CellCommandVisitor();
      cl.getCommand().accept(vis);
      stat.setStatement(vis.getCommand());
      statements.add(stat);
    }
  }

  public void addEmptyActivationCondition() {
    aconditions.add(new Statement());
  }

  public void addEmptyDeactivationCondition() {
    dconditions.add(new Statement());
  }

  public void addEmptyExecutionStatement() {
    statements.add(new Statement());
  }

  public void removeActivationExpression(int index) {
    aconditions.remove(index);
    if (adomain != null) {
      adomain.getAd().getTransposingLines().remove(index);
    }
  }

  public void removeDeactivationExpression(int index) {
    dconditions.remove(index);
    if (ddomain != null) {
      ddomain.getDd().getLogicalExpressions().remove(index);
    }
  }

  public void removeExecutionCommand(int index) {
    statements.remove(index);
    if (xdomain != null) {
      xdomain.getXd().getCommandLines().remove(index);
    }
  }

  //=================================================================

  public static class Statement implements Serializable {
    private static final long serialVersionUID = -7947476637563435505L;
    private String level;
    private String next;
    private String statement;
    private String ouputMarks;
    private String inputMarks;
    public Statement() {
      level = "";
      next = "";
    }
    /**
     * @return the level
     */
    public String getLevel() {
      return level;
    }
    /**
     * @param level the level to set
     */
    public void setLevel(String level) {
      this.level = level;
    }
    /**
     * @return the next
     */
    public String getNext() {
      return next;
    }
    /**
     * @param next the next to set
     */
    public void setNext(String next) {
      this.next = next;
    }
    /**
     * @return the statement
     */
    public String getStatement() {
      return statement;
    }
    /**
     * @param statement the statement to set
     */
    public void setStatement(String statement) {
      this.statement = statement.replace("\n", "\\n");
    }
    /**
     * @return the ouputMarks
     */
    public String getOuputMarks() {
      return ouputMarks;
    }
    /**
     * @param ouputMarks the ouputMarks to set
     */
    public void setOuputMarks(String ouputMarks) {
      this.ouputMarks = ouputMarks;
    }
    /**
     * @return the inputMarks
     */
    public String getInputMarks() {
      return inputMarks;
    }
    /**
     * @param inputMarks the inputMarks to set
     */
    public void setInputMarks(String inputMarks) {
      this.inputMarks = inputMarks;
    }
  }

  /**
   * @return the statements
   */
  public ArrayList<Statement> getStatements() {
    return statements;
  }

  /**
   * @return the aconditions
   */
  public ArrayList<Statement> getAconditions() {
    return aconditions;
  }

  /**
   * @return the dconditions
   */
  public ArrayList<Statement> getDconditions() {
    return dconditions;
  }

  @Override
  public boolean isAssigned() {
    return true;
  }
}
