package clp.edit.graphics.code.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import clp.run.res.VarType;

public class MethodArgsInfo implements Serializable {

  private static final long serialVersionUID = -6065265652718407219L;

  private boolean isReturn;

  private String retName;
  private VarType retType;
  private ArrayList<String> names;
  private Hashtable<String, VarType> hash;

  private MethodInfo methodInfo;

  public MethodArgsInfo(MethodInfo methodInfo, boolean isReturn) {
    this.methodInfo = methodInfo;
    this.isReturn = isReturn;
    if (isReturn) {
      this.retName = methodInfo.getName();
    }
    this.names = new ArrayList<>();
    this.hash = new Hashtable<>(); 
  }

  /**
   * @return the retName
   */
  public String getRetName() {
    return retName;
  }

  /**
   * @param retName the retName to set
   */
  public void setRetName(String retName) {
    this.retName = retName;
  }

  /**
   * @return the retType
   */
  public VarType getRetType() {
    return retType;
  }

  /**
   * @param retType the retType to set
   */
  public void setRetType(VarType retType) {
    this.retType = retType;
  }

  /**
   * @return the names
   */
  public ArrayList<String> getNames() {
    return names;
  }

  /**
   * @return the hash
   */
  public Hashtable<String, VarType> getHash() {
    return hash;
  }

  /**
   * @return the methodInfo
   */
  public MethodInfo getMethodInfo() {
    return methodInfo;
  }

  /**
   * @return the isReturn
   */
  public boolean isReturn() {
    return isReturn;
  }

  public boolean isComplete() {
    if (isReturn) {
      return getRetName() != null && !getRetName().isBlank();
    }
    for (String nm : getNames()) {
      if (nm.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public Collection<? extends String> getVariables() {
    ArrayList<String> list = new ArrayList<>();
    for (String key : getHash().keySet()) {
      String c = key;
      c += "/" + getHash().get(key).getVal() + "/";    // will NOT be displayed on output area
      list.add(c);
    }
    return list;
  }

  public String getRetVariable() {
    String c = getRetName();
    c += "/" + getRetType().getVal() + "/D";    // will be displayed on output area
    return c;
  }

  public String getNamesList() {
    return getNames().toString().replace("[", "").replace("]", "");
  }
}
