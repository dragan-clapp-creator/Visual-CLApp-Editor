package clp.edit.graphics.code.prt;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import clp.run.msc.Output;

public class CslContext implements Serializable {

  private static final long serialVersionUID = -5590253490809116978L;

  private Hashtable<String, CslInfo> cslInfoHash;

  public CslContext() {
    cslInfoHash = new Hashtable<>();
  }

  /**
   * @return the cslInfo
   */
  public Hashtable<String, CslInfo> getCslInfos() {
    return cslInfoHash;
  }

  public CslInfo getCslInfo(String name) {
    if (name == null) {
      return null;
    }
    return cslInfoHash.get(name);
  }

  public Set<String> getCslList() {
    return cslInfoHash.keySet();
  }

  public void addCslInfo(String cslName, Output out) {
    cslInfoHash.put(cslName, new CslInfo(out));
  }

  public void remove(String csl) {
    cslInfoHash.remove(csl);
  }

  //=================================================================

  public static class CslInfo implements Serializable {
    private static final long serialVersionUID = -9136504999384753171L;
    private Output out;

    public CslInfo(Output out) {
      this.out = out;
    }

    /**
     * @return the out
     */
    public Output getOut() {
      return out;
    }
  }
}
