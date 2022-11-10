package clp.edit.graphics.code.java;

import java.awt.Component;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import clp.edit.graphics.code.java.bci.ActionInfo;
import clp.run.res.weave.WeaveVar;

public class JavaContext implements Serializable {

  private static final long serialVersionUID = -5386608744571328698L;

  private static File selectedDirectory = new File(".");

  /**
   * use file chooser to select class file or jar
   * 
   * @param parent
   * @return
   */
  public static File getSelectedFile(Component parent) {
    JFileChooser fc = new JFileChooser(selectedDirectory);
    fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fc.addChoosableFileFilter(new FileFilter() {
      @Override
      public String getDescription() {
        return "choose a java '.class' or '.jar' file or a 'bin' directory";
      }
      @Override
      public boolean accept(File f) {
        return f.isDirectory() && f.getName().equals("bin") ||
               f.isFile() && (f.getName().endsWith(".class") || f.getName().endsWith(".jar"));
      }
    });
    int result = fc.showOpenDialog(parent);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fc.getSelectedFile();
      selectedDirectory = selectedFile.getParentFile();
      return selectedFile;
    }
    return null;
  }

  private Hashtable<String, BciInfo> bciInfo;

  public JavaContext() {
    bciInfo = new Hashtable<>();
  }

  public BciInfo getBciInfo(String name) {
    if (name == null) {
      return null;
    }
    return bciInfo.get(name);
  }

  public Set<String> getBciList() {
    return bciInfo.keySet();
  }

  public boolean addBciInfo(String bciName, WeaveVar bciVar, ArrayList<String> variables, File selectedFile, String selectedClass, String method, ArrayList<ArrayList<ActionInfo>> list, String statement) {
    bciInfo.put(bciName, new BciInfo(bciVar, variables, selectedFile, selectedClass, method, list, statement));
    return true;
  }

  public void remove(String bci) {
    bciInfo.remove(bci);
  }

  //=================================================================

  public static class BciInfo implements Serializable {

    private static final long serialVersionUID = 5143341664590407815L;

    private WeaveVar bciVar;
    private ArrayList<String> variables;
    private File selectedFile;
    private String selectedClass;
    private String method;
    private ArrayList<ArrayList<ActionInfo>> list;
    private String statement;

    public BciInfo(WeaveVar bciVar, ArrayList<String> variables, File selectedFile, String selectedClass, String method, ArrayList<ArrayList<ActionInfo>> list, String statement) {
      this.bciVar = bciVar;
      this.variables = variables;
      this.selectedFile = selectedFile;
      this.selectedClass = selectedClass;
      this.method = method;
      this.list = list;
      this.statement = statement;
    }

    /**
     * @return the bciVar
     */
    public WeaveVar getBciVar() {
      return bciVar;
    }

    /**
     * @param bciVar the bciVar to set
     */
    public void setBciVar(WeaveVar bciVar) {
      this.bciVar = bciVar;
    }

    /**
     * @return the variables
     */
    public ArrayList<String> getVariables() {
      return variables;
    }

    /**
     * @param variables the variables to set
     */
    public void setVariables(ArrayList<String> variables) {
      this.variables = variables;
    }

    /**
     * @return the selectedFile
     */
    public File getSelectedFile() {
      return selectedFile;
    }

    /**
     * @param selectedFile the selectedFile to set
     */
    public void setSelectedFile(File selectedFile) {
      this.selectedFile = selectedFile;
    }

    /**
     * @return the selectedClass
     */
    public String getSelectedClass() {
      return selectedClass;
    }

    /**
     * @param selectedClass the selectedClass to set
     */
    public void setSelectedClass(String selectedClass) {
      this.selectedClass = selectedClass;
    }

    /**
     * @return the method
     */
    public String getMethod() {
      return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
      this.method = method;
    }

    /**
     * @return the list
     */
    public ArrayList<ArrayList<ActionInfo>> getList() {
      return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<ArrayList<ActionInfo>> list) {
      this.list = list;
    }

    /**
     * @return the statement
     */
    public String getStatement() {
      return statement;
    }

    /**
     * @param statement the statement to set
     */
    public void setStatement(String statement) {
      this.statement = statement;
    }
  }
}
