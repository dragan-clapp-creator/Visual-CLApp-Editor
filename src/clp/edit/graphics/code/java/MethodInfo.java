package clp.edit.graphics.code.java;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;

public class MethodInfo implements Serializable {

  private static final long serialVersionUID = -8561112196383767435L;

  private String name;
  private Signature signature;
  private boolean isStatic;

  private LocalVariableGen[] variables;
  transient private Method reflectMethod;
  transient private ConstantPoolGen cpg;
  transient private JavaClass jc;
  private String fullClassName;

  /**
   * CONSTRUCTOR for java call
   * 
   * @param m
   */
  public MethodInfo(Method m) {
    reflectMethod = m;
    name = m.getName();
    isStatic = Modifier.isStatic(m.getModifiers());
    signature = new Signature(m);
  }

  /**
   * CONSTRUCTOR for BCI
   * 
   * @param m
   * @param lib 
   */
  public MethodInfo(Method m, String lib) {
    this(m);
    fullClassName = m.getDeclaringClass().getName();
    setupForLocalVariables(lib);
  }

  //
  private void setupForLocalVariables(String lib) {
    jc = findAndParse(lib, fullClassName.replace('.', '/') + ".class");
    if (jc != null) {
      cpg = new ConstantPoolGen(jc.getConstantPool());
      ClassGen clg = new InstructionFactory(cpg).getClassGen();
      if (clg == null) {
        clg = new ClassGen(jc);
      }
    }
  }

  /**
   * gather all relevant information for all local variable of this method
   * 
   * @return
   */
  public LocalVariableGen[] gatherLocalVariables() {
    if (variables == null && jc != null) {
      org.apache.bcel.classfile.Method method = jc.getMethod(reflectMethod);
      MethodGen mg = new MethodGen(method, fullClassName, cpg);

      variables = mg.getLocalVariables();
    }
    return variables;
  }

  //
  private JavaClass findAndParse(String dir, String clname) {
    JavaClass java_class = null;
    try {
      String fullName = dir;
      if (dir.endsWith(".jar")) {
        java_class = new ClassParser(dir, clname).parse();
      }
      else {
        if (dir.endsWith("/")) {
          fullName += clname;
        }
        else {
          fullName += "/" + clname;
        }
        java_class = new ClassParser(fullName).parse();
      }
    }
    catch (ClassFormatException | NullPointerException | IOException e) {
      System.err.println(e.getClass().getName()+" for "+clname);
    }
    return java_class;
  }

  public boolean isStatic() {
    return isStatic;
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

  public boolean hasReturn() {
    return !"V".equals(signature.getReturnCode());
  }

  public String getReturnType() {
    return signature.getRetType().getName();
  }

  public Parameter[] getParameters() {
    return signature.getParameters();
  }

  public String[] getArguments(int size) {
    int index = 0;
    String[] args = new String[size];
    String str = signature.getArgs();
    int i=0;
    while (i<str.length()) {
      char c = str.charAt(i);
      if (c == 'L') {
        int j = str.indexOf(';', i);
        args[index++] = str.substring(i+1, j).replace("/", ".");
        i = j;
     }
      else {
        args[index++] = convertType(c);
      }
      i++;
    }
    return args;
  }

  //
  private String convertType(char c) {
    switch(c) {
      case 'B':
        return "byte";
      case 'C':
        return "char";
      case 'I':
        return "int";
      case 'J':
        return "long";
      case 'F':
        return "float";
      case 'Z':
        return "boolean";
    }
    return null;
  }
}
