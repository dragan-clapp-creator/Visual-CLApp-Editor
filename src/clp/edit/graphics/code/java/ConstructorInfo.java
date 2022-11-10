package clp.edit.graphics.code.java;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ConstructorInfo implements Serializable {

  private static final long serialVersionUID = -8240559103797296139L;

  private String name;
  private String refname; // variable where the instance will be stored
  private Signature signature;

  public ConstructorInfo(Constructor<?> c) {
    name = c.getName();
    int i = name.lastIndexOf(".");
    if (i > 0) {
      name = name.substring(i+1);
    }
    signature = new Signature(c);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the name
   */
  public String getFullName() {
    return name + signature.toString();
  }

  public boolean hasArguments() {
    return !signature.getArgs().isEmpty();
  }

  public Parameter[] getParameters() {
    return signature.getParameters();
  }

  /**
   * @return the refname
   */
  public String getRefname() {
    return refname;
  }

  /**
   * @param refname the refname to set
   */
  public void setRefname(String refname) {
    this.refname = refname;
  }
}
