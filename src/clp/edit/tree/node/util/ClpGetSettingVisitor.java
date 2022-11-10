package clp.edit.tree.node.util;

import java.util.ArrayList;

import clapp.run.util.SignUtil;
import clp.run.res.ArrayBVariable;
import clp.run.res.ArrayFVariable;
import clp.run.res.ArrayIVariable;
import clp.run.res.ArrayLVariable;
import clp.run.res.ArraySVariable;
import clp.run.res.BVariable;
import clp.run.res.BoolVariableVisitor;
import clp.run.res.DVariable;
import clp.run.res.EVariable;
import clp.run.res.Encryption;
import clp.run.res.FVariable;
import clp.run.res.FloatVariableVisitor;
import clp.run.res.HVariable;
import clp.run.res.IVariable;
import clp.run.res.IntVariableVisitor;
import clp.run.res.LVariable;
import clp.run.res.LongVariableVisitor;
import clp.run.res.PVariable;
import clp.run.res.SVariable;
import clp.run.res.SettingVisitor;
import clp.run.res.SimpleBVariable;
import clp.run.res.SimpleFVariable;
import clp.run.res.SimpleIVariable;
import clp.run.res.SimpleLVariable;
import clp.run.res.SimpleSVariable;
import clp.run.res.StringVariableVisitor;
import clp.run.res.TVariable;
import clp.run.res.VarType;
import clp.run.res.WebVariable;
import clp.run.res.weave.CstOrVar;
import clp.run.res.weave.MethodEnhancement;

public class ClpGetSettingVisitor implements SettingVisitor {

  private String name;
  private boolean hasArray;
  private Object initial;
  private Object value;
  private VarType varType;
  private int port;
  private Encryption encryption;
  private CstOrVar pack;
  private CstOrVar clazz;
  private ArrayList<MethodEnhancement> items;
  private String sentences;

  /**
   * @return the varType
   */
  public VarType getVarType() {
    return varType;
  }
  /**
   * @param varType the varType to set
   */
  public void setVarType(VarType varType) {
    this.varType = varType;
  }
  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }
  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  public String getSentences() {
    return sentences;
  }
  public CstOrVar getPack() {
    return pack;
  }
  public CstOrVar getClazz() {
    return clazz;
  }
  public ArrayList<MethodEnhancement> getItems() {
    return items;
  }
  public int getPort() {
    return port;
  }
  public Encryption getEncryption() {
    return encryption;
  }
  public Object getInitial() {
    return initial;
  }
  public boolean hasArray() {
    return hasArray;
  }
  @Override
  public void visitBVariable(BVariable x) {
    varType = x.getTBOOL();
    x.getBoolVariable().accept(new BoolVariableVisitor() {
      @Override
      public void visitSimpleBVariable(SimpleBVariable x) {
        name = x.getName();
        value = x.getInitial();
      }
      @Override
      public void visitArrayBVariable(ArrayBVariable x) {
      }
    });
  }
  @Override
  public void visitFVariable(FVariable x) {
    varType = x.getTFLOAT();
    x.getFloatVariable().accept(new FloatVariableVisitor() {
      @Override
      public void visitSimpleFVariable(SimpleFVariable x) {
        name = x.getName();
        initial = SignUtil.getFInitial(x.getFsigned());
      }
      @Override
      public void visitArrayFVariable(ArrayFVariable x) {
        name = x.getName();
        value = x.getArrayFInit();
        hasArray = true;
      }
    });
  }
  @Override
  public void visitIVariable(IVariable x) {
    varType = x.getTINT();
    x.getIntVariable().accept(new IntVariableVisitor() {
      @Override
      public void visitSimpleIVariable(SimpleIVariable x) {
        name = x.getName();
        initial = SignUtil.getIInitial(x.getIsigned());
      }
      @Override
      public void visitArrayIVariable(ArrayIVariable x) {
        name = x.getName();
        value = x.getArrayIInit();
        hasArray = true;
      }
    });
  }
  @Override
  public void visitLVariable(LVariable x) {
    varType = x.getTLONG();
    x.getLongVariable().accept(new LongVariableVisitor() {
      @Override
      public void visitSimpleLVariable(SimpleLVariable x) {
        name = x.getName();
        initial = SignUtil.getLInitial(x.getLsigned());
      }
      @Override
      public void visitArrayLVariable(ArrayLVariable x) {
        name = x.getName();
        value = x.getArrayLInit();
        hasArray = true;
      }
    });
  }
  @Override
  public void visitSVariable(SVariable x) {
    varType = x.getTSTRING();
    x.getStringVariable().accept(new StringVariableVisitor() {
      @Override
      public void visitSimpleSVariable(SimpleSVariable x) {
        name = x.getName();
        value = x.getInitial();
      }
      @Override
      public void visitArraySVariable(ArraySVariable x) {
        name = x.getName();
        value = x.getSInitList();
        hasArray = true;
      }
    });
  }
  @Override
  public void visitDVariable(DVariable x) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void visitTVariable(TVariable x) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void visitHVariable(HVariable x) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void visitPVariable(PVariable x) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void visitWebVariable(WebVariable x) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void visitEVariable(EVariable x) {
    // TODO Auto-generated method stub
    
  }
}
