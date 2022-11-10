package clp.edit.tree.node.util;

import java.io.Serializable;

public class HeapContent implements IContent, Serializable {

  private static final long serialVersionUID = -4321973271613235182L;

  private String res;

  private boolean isAssigned;

  private String queue;

  private int activity;

  /**
   * constructor
   * 
   * @param b
   */
  public HeapContent(boolean b) {
    isAssigned = b;
  }

  public void setResourcesName(String string) {
    res = string;
  }
  public String getResourcesName() {
    return res;
  }

  /**
   * @return the isAssigned
   */
  public boolean isAssigned() {
    return isAssigned;
  }

  /**
   * @param isAssigned the isAssigned to set
   */
  public void setAssigned(boolean isAssigned) {
    this.isAssigned = isAssigned;
  }

  /**
   * @return the queue
   */
  public String getQueue() {
    return queue;
  }

  /**
   * @param queue the queue to set
   */
  public void setQueue(String queue) {
    this.queue = queue;
  }

  /**
   * @return the activity
   */
  public int getActivity() {
    return activity;
  }

  /**
   * @param activity the activity to set
   */
  public void setActivity(int activity) {
    this.activity = activity;
  }
}
