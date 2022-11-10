package clp.edit.graphics.code.web;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import clp.run.res.WebVariable;

public class WebContext implements Serializable {

  private static final long serialVersionUID = 6265319575546173952L;

  public static final String varTemplate = " <TYPE> <VAR> = <VALUE>;";

  private Hashtable<String, WebInfo> webInfo;

  public WebContext() {
    webInfo = new Hashtable<>();
  }

  /**
   * @return the uiInfo
   */
  public Hashtable<String, WebInfo> getWebInfos() {
    return webInfo;
  }

  public void remove(String web) {
    webInfo.remove(web);
  }

  public WebInfo getWebInfo(String name) {
    if (name == null) {
      return null;
    }
    return webInfo.get(name);
  }

  public Set<String> getWebList() {
    return webInfo.keySet();
  }

  public boolean addWebInfo(String webName, WebVariable webVar, String declaration) {
    webInfo.put(webName, new WebInfo(webVar, declaration));
    return true;
  }

  //=================================================================

  public static class WebInfo implements Serializable {
    private static final long serialVersionUID = 2684465153988607067L;
    private WebVariable webVar;
    private String declaration;

    public WebInfo(WebVariable webVar, String declaration) {
      this.webVar = webVar;
      this.declaration = declaration;
    }

    /**
     * @return the webVar
     */
    public WebVariable getWebVar() {
      return webVar;
    }

    /**
     * @return the declaration
     */
    public String getDeclaration() {
      return declaration;
    }
  }
}
