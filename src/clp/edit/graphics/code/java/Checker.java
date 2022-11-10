package clp.edit.graphics.code.java;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class Checker {

  private boolean isJar;
  private boolean isBin;
  private File entry;
  private URLClassLoader loader;
  private String className;
  private String urlName;

  public Checker(File file) throws IOException {
    String path = file.getCanonicalPath();
    if (file.isFile()) {
      if (path.endsWith(".jar")) {
        isJar = true;
      }
      entry = file;
    }
    else if (path.endsWith("bin")) {
      isBin = true;
      entry = file;
    }
    else {
      throw new IOException("not allowed");
    }
    String[] sp = path.split("/");
    urlName = "";
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
      else {
        urlName += s + "/";
      }
      if ("bin".equals(s)) {
        isPackage = true;
      }
    }
    if (!clName.isEmpty()) {
      className = clName.substring(0,clName.length()-6);
    }
    URL classUrl = new URL("file://"+urlName);
    URL[] classUrls = { classUrl };
    loader = new URLClassLoader(classUrls);
  }

  /**
   * @return the isJar
   */
  public boolean isJar() {
    return isJar;
  }

  /**
   * @return the isBin
   */
  public boolean isBin() {
    return isBin;
  }

  /**
   * @return the entry
   */
  public File getEntry() {
    return entry;
  }

  /**
   * @return the loader
   */
  public URLClassLoader getLoader() {
    return loader;
  }

  /**
   * @return the className
   */
  public String getClassName() {
    return className;
  }

  public String getUsedLib() {
    if (isJar) {
      try {
        return entry.getCanonicalPath();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return urlName;
  }
}
