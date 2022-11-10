package clp.edit.graphics.code.java;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Signature implements Serializable {

  private static final long serialVersionUID = 8198455668684426474L;

  private String returnCode;
  private String args;
  transient private Class<?> retType;
  transient private Parameter[] parameters;

  public Signature(Method m) {
    retType = m.getReturnType();
    parameters = m.getParameters();
    returnCode = code(retType);
    args = appendedCode(parameters);
  }

  public Signature(Constructor<?> c) {
    parameters = c.getParameters();
    args = appendedCode(parameters);
  }

  //
  private String appendedCode(Parameter[] parameters) {
    String c = "";
    for (Parameter param : parameters) {
      c += code(param.getType());
    }
    return c;
  }

  //
  private String code(Class<?> type) {
    String tsring = type.getName();
    switch (tsring) {
      case "void":
        return "V";
      case "boolean":
        return "Z";
      case "int":
        return "I";
      case "long":
        return "L";
      case "float":
      case "double":
        return "F";

      default:
        break;
    }
    if ((tsring.startsWith("L") || tsring.startsWith("[L")) && tsring.endsWith(";")) {
      return tsring;
    }
    return "L"+tsring+";";
  }

  /**
   * @return the returnCode
   */
  public String getReturnCode() {
    return returnCode;
  }

  /**
   * @return the args
   */
  public String getArgs() {
    return args;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (returnCode == null) {
      return "/(" + args + ")";
    }
    return "/" + returnCode + "/(" + args + ")";
  }

  /**
   * @return the retType
   */
  public Class<?> getRetType() {
    return retType;
  }

  /**
   * @return the parameters
   */
  public Parameter[] getParameters() {
    return parameters;
  }
}
