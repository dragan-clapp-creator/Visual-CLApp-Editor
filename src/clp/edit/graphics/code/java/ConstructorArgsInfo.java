package clp.edit.graphics.code.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import clp.run.res.VarType;

public class ConstructorArgsInfo implements Serializable {

  private static final long serialVersionUID = 8656848554005439269L;

  private String retName;
  private VarType retType;
  private ArrayList<String> names;
  private Hashtable<String, VarType> hash;

  private ConstructorInfo consInfo;

  public ConstructorArgsInfo() {
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
   * @return the retType
   */
  public VarType getRetType() {
    return retType;
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
   * @return the consInfo
   */
  public ConstructorInfo getConsInfo() {
    return consInfo;
  }

  /**
   * @param consInfo the consInfo to set
   */
  public void setConsInfo(ConstructorInfo consInfo) {
    this.consInfo = consInfo;
  }

  public boolean isComplete() {
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

  public String getNamesList() {
    return getNames().toString().replace("[", "").replace("]", "");
  }
}
