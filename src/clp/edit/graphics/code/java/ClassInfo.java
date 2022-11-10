package clp.edit.graphics.code.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class ClassInfo {

  private String name;
  private HashMap<String, ConstructorInfo> constructors;
  private HashMap<String, MethodInfo> methods;
  private boolean isRunnable;
  private Field[] fields;

  /**
   * CONSTRUCTOR
   * 
   * @param cl
   * @param isBCI
   * @param lib 
   */
  public ClassInfo(Class<?> cl, boolean isBCI, String lib) {
    name = cl.getName();
    constructors = new HashMap<>();
    Constructor<?>[] cts = cl.getDeclaredConstructors();
    for (Constructor<?> c : cts) {
      if (isAccepted(c.getModifiers())) {
        ConstructorInfo csi = new ConstructorInfo(c);
        String key = csi.getFullName();
        constructors.put(key, csi);
      }
    }
    Method[] mtds = cl.getDeclaredMethods();
    methods = new HashMap<>();
    for (Method m : mtds) {
      if (isAccepted(m.getModifiers())) {
        MethodInfo mi = isBCI ? new MethodInfo(m, lib) : new MethodInfo(m);
        String key = mi.getFullName();
        methods.put(key, mi);
      }
    }
    if (isBCI) {
      fields = cl.getDeclaredFields();
    }
    isRunnable = checkRunnable(cl);
  }

  //
  private boolean checkRunnable(Class<?> cl) {
    if (cl != null) {
      if (cl.getSimpleName().equals(Thread.class.getSimpleName())) {
        return true;
      }
      Class<?>[] inters = cl.getInterfaces();
      for (Class<?> inter : inters) {
        if (inter.getSimpleName().equals(Runnable.class.getSimpleName())) {
          return true;
        }
        return checkRunnable(inter.getSuperclass());
      }
      return checkRunnable(cl.getSuperclass());
    }
    return false;
  }

  //
  private boolean isAccepted(int modifiers) {
    return Modifier.isPublic(modifiers);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the methods
   */
  public HashMap<String, MethodInfo> getMethods() {
    return methods;
  }

  public MethodInfo getMethod(String selectedItem) {
    return methods.get(selectedItem);
  }

  public boolean hasConstructorArguments(String selectedItem) {
    if (!constructors.isEmpty()) {
      ConstructorInfo consInfo = constructors.get(selectedItem);
      if (consInfo != null) {
        return consInfo.hasArguments();
      }
    }
    return false;
  }

  public boolean isRunnable() {
    return isRunnable;
  }

  /**
   * @return the constructors
   */
  public HashMap<String, ConstructorInfo> getConstructors() {
    return constructors;
  }

  /**
   * @return the fields
   */
  public Field[] getFields() {
    return fields;
  }
}
