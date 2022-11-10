package clp.edit.graphics.code.java;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import clp.edit.graphics.shapes.ActionShape;

public class Inspector {

  private ArrayList<ClassInfo> classes;
  private Checker checker;

  /**
   * gather all classes/methods information
   * 
   * @param file
   * @param isBCI
   * @throws IOException
   */
  public Inspector(File file, boolean isBCI) throws IOException {
    try {
      checker = new Checker(file);
      classes = new ArrayList<>();
      if (checker.isBin()) {
        createClassesFromBin(checker.getEntry(), isBCI);
      }
      else if (checker.isJar()) {
        createClassesFromJar(isBCI);
      }
      else {
        ClassInfo ci = createClassInfo(checker.getClassName(), isBCI);
        if (ci != null) {
          classes.add(ci);
        }
        else {
          System.err.printf("%s is not accepted. Please choose a concrete public class\n", file.getCanonicalPath());
        }
      }
      checker.getLoader().close();
    }
    catch (IOException e) {
      System.err.printf("Problem encountered by reading %s\nCause: %s\n", file.getCanonicalPath(), e.getMessage());
      throw new IOException("not allowed");
    }
  }

  //
  private void createClassesFromBin(File file, boolean isBCI) {
    if (file.isDirectory()) {
      for (String fn : file.list()) {
        createClassesFromBin(new File(file.getAbsolutePath() + File.separator + fn), isBCI);
      }
    }
    else {
      String name = file.getAbsolutePath();
      if (name.endsWith(".class")) {
        ClassInfo ci = createClassInfo(extractClassName(name), isBCI);
        if (ci != null) {
          classes.add(ci);
        }
      }
    }
  }

  //
  private String extractClassName(String path) {
    String[] sp = path.split("/");
    String clName = "";
    boolean isPackage = false;
    for (int i=0; i<sp.length; i++) {
      String s = sp[i];
      if (isPackage) {
        clName += s;
        if (i < sp.length-1) {
          clName += ".";
        }
      }
      if ("bin".equals(s)) {
        isPackage = true;
      }
    }
    if (clName.isEmpty()) {
      int i = path.lastIndexOf("/");
      int j = path.lastIndexOf(".");
      return path.substring(i+1, j);
    }
    return clName.substring(0,clName.length()-6);
  }

  //
  private void createClassesFromJar(boolean isBCI) throws IOException {
    File jar = checker.getEntry();
    JarFile jf = new JarFile(jar);
    for (Enumeration<JarEntry> files = jf.entries(); files.hasMoreElements();) {
      JarEntry je = files.nextElement();
      String fullname = je.getName();
      if (fullname.endsWith(".class")) {
        fullname = fullname.replace("/", ".").substring(0, fullname.length()-6);
        ClassInfo ci = createClassInfo(fullname, isBCI);
        if (ci != null) {
          classes.add(ci);
        }
      }
    }
    jf.close();
  }

  public ClassInfo createClassInfo(String clName, boolean isBCI) {
      try {
        Class<?> cl = checker.getLoader().loadClass(clName);
        if (isAccepted(cl.getModifiers())) {
          return new ClassInfo(cl, isBCI, checker.getUsedLib());
        }
      }
      catch (ClassNotFoundException e) {
        System.err.printf("Could not parse %s. Cause: %s\n", clName, e.getMessage());
      }
    return null;
  }

  //
  private boolean isAccepted(int modifiers) {
    return !Modifier.isAbstract(modifiers) &&
           !Modifier.isInterface(modifiers) &&
           Modifier.isPublic(modifiers);
  }

  /**
   * @return the classes
   */
  public ArrayList<ClassInfo> getClasses() {
    return classes;
  }

  public void addUsedLib(ActionShape caller) {
    caller.addToLibs(checker.getUsedLib(), checker.isJar());
  }
}
