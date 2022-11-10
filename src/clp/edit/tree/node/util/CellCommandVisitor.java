package clp.edit.tree.node.util;

import clp.run.cel.asm.AssignStatement;
import clp.run.cel.asm.BoolAssignment;
import clp.run.cel.asm.VarAssignment;
import clp.run.cel.dom.ClappStatement;
import clp.run.cel.dom.CommandVisitor;
import clp.run.cel.dom.CutStatement;
import clp.run.cel.dom.ExecStatement;
import clp.run.cel.dom.SysStatement;
import clp.run.cel.exp.ArrayFactor;
import clp.run.cel.exp.Expression;
import clp.run.cel.exp.Factor;
import clp.run.cel.exp.FactorVisitor;
import clp.run.cel.exp.S_constant;
import clp.run.cel.exp.S_dval;
import clp.run.cel.exp.S_exp;
import clp.run.cel.exp.S_ival;
import clp.run.cel.exp.S_tval;
import clp.run.cel.exp.S_var;
import clp.run.cel.exp.Sfactor;
import clp.run.cel.exp.SfactorVisitor;
import clp.run.cel.exp.SimpleFactor;
import clp.run.cel.exp.Term;
import clp.run.cel.graph.GParser;
import clp.run.cel.graph.eval.GEvaluator;
import clp.run.cel.graph.init.GInit;
import clp.run.cel.graph.map.GAssistent;
import clp.run.cel.graph.map.GMapper;
import clp.run.cel.graph.rnd.GRenderer;
import clp.run.cel.java.JavaStatement;
import clp.run.cel.prt.P_constant;
import clp.run.cel.prt.P_exp;
import clp.run.cel.prt.P_var;
import clp.run.cel.prt.PrintElt;
import clp.run.cel.prt.PrintEltVisitor;
import clp.run.cel.prt.PrintStatement;
import clp.run.cel.ref.ReflectStatement;
import clp.run.cel.web.WebStatement;
import clp.run.msc.OutputTarget;
import clp.run.ui.VisualizeStatement;

public class CellCommandVisitor implements CommandVisitor {

  private String command;
  private String value = null;

  @Override
  public void visitPrintStatement(PrintStatement x) {
    command = "print ";
    if (x.getPrintElt() != null) {
      command += getString(x.getPrintElt());
      for (PrintElt elt : x.getPrintElts()) {
        command += ", " + getString(elt);
      }
    }
    if (x.isTo()) {
      command += " to " + getConsole(x.getOutputTarget());
    }
  }

  //
  private String getConsole(OutputTarget outputTarget) {
    if (outputTarget.isStringCONSOLE()) {
      return "CONSOLE";
    }
    if (outputTarget.getName() != null) {
      return outputTarget.getName();
    }
    return "FILE" + outputTarget.getSendFile().getFileName();
  }

  //
  private String getString(PrintElt printElt) {
    PrintEltVisitor vis = new PrintEltVisitor() {
      @Override
      public void visitP_var(P_var x) {
        value = x.getVar();
      }
      @Override
      public void visitP_exp(P_exp x) {
        value = "(" + x.getExpression() + ")";
      }
      @Override
      public void visitP_constant(P_constant x) {
        value = "\"" + x.getConstant() + "\"";
      }
    };
    printElt.accept(vis);
    return value;
  }

  @Override
  public void visitReflectStatement(ReflectStatement x) {
    command = "reflect " + x.getReflectObject().name();
    if (x.getOutputTarget() != null) {
      command += " to " + getConsole(x.getOutputTarget());
    }
  }

  @Override
  public void visitCutStatement(CutStatement x) {
    command = "cut";
  }

  @Override
  public void visitExecStatement(ExecStatement x) {
    command = "exec";
  }

  @Override
  public void visitJavaStatement(JavaStatement x) {
    command = "java";
  }

  @Override
  public void visitWebStatement(WebStatement x) {
    command = "send";
  }

  @Override
  public void visitClappStatement(ClappStatement x) {
    command = "WEBREFLECT";
  }

  @Override
  public void visitSysStatement(SysStatement x) {
    if (x.getSysCommand() != null) {
      switch (x.getSysCommand()) {
        case EXIT:
          command = "exit";
          break;
        case PROCESS_INBOUND:
          command = "processInbound";
          break;
      }
    }
    else if (x.getSysExp() != null) {
      switch(x.getSysExp()) {
        case ACTIVE:
          command = "isActive";
          break;
        case INACTIVE:
          command = "isInactive";
          break;
      }
    }
    else if (x.getLoadMarks() != null) {
      command = "loadMarks (" + x.getLoadMarks().getName() + ", \"" + x.getLoadMarks().getMarks() + "\")";
    }
  }

  @Override
  public void visitAssignStatement(AssignStatement x) {
    command = "";
    if (x.getBoolAssignment() != null) {
      BoolAssignment b = x.getBoolAssignment();
      if (b.isModifier()) {
        switch (b.getModifier()) {
          case UP:
            command = "^";
            break;
          case NOT:
            command = "!";
            break;
          case DOWN:
            command = "v";
            break;
        }
      }
      command += b.getVar();
      if (b.isIfclause()) {
        command += " if " + b.getIfclause().getVar();
      }
    }
    else {
      VarAssignment v = x.getVarAssignment();
      command = "set " + v.getVar() + " = ";
      Expression exp = v.getExpression();
      if (exp != null) {
        command += extract(exp);
      }
    }
  }

  //
  private String extract(Expression exp) {
    String val = extract(exp.getTerm());
    if (exp.getOp() != null) {
      val += exp.getOp().getVal();
      val += extract(exp.getExpression());
    }
    return val;
  }

  //
  private String extract(Term term) {
    String val = extract(term.getFactor());
    if (term.getOp() != null) {
      val += term.getOp().getVal();
      val += extract(term.getTerm());
    }
    return val;
  }

  private String extract(Factor factor) {
    value = "";
    factor.accept(new FactorVisitor() {
      @Override
      public void visitSimpleFactor(SimpleFactor x) {
        Sfactor sf = x.getSfactor();
        sf.accept(new SfactorVisitor() {
          @Override
          public void visitS_var(S_var x) {
            value += x.getVar();
          }
          @Override
          public void visitS_ival(S_ival x) {
            if (x.isTermOperator()) {
              value += x.getTermOperator().getVal();
            }
            value += x.getIval();
          }
          @Override
          public void visitS_exp(S_exp x) {
          }
          @Override
          public void visitS_constant(S_constant x) {
            value += x.getConstant();
          }
          @Override
          public void visitS_dval(S_dval x) {
            value += x.getDay() + "/" + x.getMonth() + "/" + x.getYear();
          }
          @Override
          public void visitS_tval(S_tval x) {
            value += x.getHour() + ":" + x.getMin() + ":" + x.getSec();
          }
        });
      }
      @Override
      public void visitArrayFactor(ArrayFactor x) {
      }
    });
    return value;
  }

  @Override
  public void visitGraphParseStatement(GParser x) {
    command = "parse // TODO";
  }

  @Override
  public void visitGraphAssistStatement(GAssistent x) {
    command = "assist " + x.getGname1() + " with " + x.getGname2();
  }

  @Override
  public void visitGraphMapStatement(GMapper x) {
    command = "map " + x.getGname() + " // TODO";
  }

  @Override
  public void visitGraphRenderStatement(GRenderer x) {
    command = "render " + x.getGraphRef().getGname() + " to ";
    OutputTarget target = x.getOutputTarget();
    command += target.isStringCONSOLE() ? target.getStringCONSOLE() : target.getName();
  }

  @Override
  public void visitGraphEvaluateStatement(GEvaluator x) {
    command = "evaluate " + x.getGraphRef().getGname() + " // TODO";
  }

  @Override
  public void visitGraphReinitStatement(GInit x) {
    command = "reinitialize " + x.getGname();
  }

  @Override
  public void visitVisualizeStatement(VisualizeStatement x) {
    command = "show " + (x.isKeeping() ? "keeping " : "") + x.getUiname();
  }

  public String getCommand() {
    return command;
  }
}
