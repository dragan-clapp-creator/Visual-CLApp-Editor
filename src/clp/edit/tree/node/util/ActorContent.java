package clp.edit.tree.node.util;

import java.io.Serializable;

public class ActorContent implements IContent, Serializable {

  private static final long serialVersionUID = 4559041300941673450L;


  private boolean isAssigned;

  /**
   * constructor
   * 
   * @param b
   */
  public ActorContent(boolean b) {
    isAssigned = b;
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
}
