package clp.edit.tree.node.util;

import java.io.Serializable;
import java.util.ArrayList;

import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.run.msc.ClassReference;
import clp.run.msc.Decryption;
import clp.run.msc.MscTaskName;

public class MetaScenarioContent implements IContent, Serializable {

  private static final long serialVersionUID = -7570479800941177573L;

  private ArrayList<MscTaskName> tasks;
  private boolean isChecked;
  private int port;
  private String dpack;
  private String dclass;

  private String dpath;

  public boolean getIsChecked() {
    return isChecked;
  }
  public void setIsChecked(boolean isChecked) {
    this.isChecked = isChecked;
  }
  public String getPort() {
    if (port < 1) {
      return null;
    }
    return ""+port;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public void setDecryption(MetaScenarioTreeNode mnode, Decryption decryption) {
    ClassReference cref = decryption.getClazz();
    dpack = cref.getPack();
    dclass = cref.getClazz();
    dpath = mnode.findPath(dpack, dclass);
  }
  /**
   * @return the tasks
   */
  public ArrayList<MscTaskName> getTasks() {
    return tasks;
  }
  /**
   * @param tasks the tasks to set
   */
  public void setTasks(ArrayList<MscTaskName> tasks) {
    this.tasks = tasks;
  }

  public void setTaskNameAt(int i, MscTaskName name) {
    tasks.set(i, name);
  }
  public void removeTask(int index) {
    tasks.remove(index);
  }
  public void addDefaultTask() {
    tasks.add(MscTaskName.SCHEDULER);
  }

  @Override
  public boolean isAssigned() {
    return true;
  }
  /**
   * @return the decrypt
   */
  public String getDecrypt() {
    if (dpack == null) {
      return "";
    }
    return " decryptFile \"" + dpack + "\" : \"" + dclass + "\"";
  }
  /**
   * @param decrypt the decrypt to set
   */
  public void setDecrypt(String decrypt) {
    String s = decrypt.substring(0, decrypt.length()-6);
    int i = s.lastIndexOf("/bin");
    dpath = s.substring(0,i+4);
    String pack = s.substring(i+5).replace("/", ".");
    i = pack.lastIndexOf(".");
    this.dclass = pack.substring(i+1);
    this.dpack = pack.substring(0, i);
  }
  /**
   * @return the dclass
   */
  public String getDclass() {
    return dclass;
  }
  /**
   * @return the dpack
   */
  public String getDpack() {
    return dpack;
  }
  /**
   * @return the dpath
   */
  public String getDpath() {
    return dpath;
  }
}
