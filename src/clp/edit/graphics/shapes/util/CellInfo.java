package clp.edit.graphics.shapes.util;

import java.io.Serializable;
import java.util.List;

public class CellInfo implements Serializable {

  private static final long serialVersionUID = 5107953197864289156L;

  private String name;
  private String ad;
  private String dd;
  private List<String> xd;

  public CellInfo(String string) {
    name = string;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the ad
   */
  public String getAd() {
    return ad;
  }
  /**
   * @param ad the ad to set
   */
  public void setAd(String ad) {
    this.ad = ad;
  }
  /**
   * @return the dd
   */
  public String getDd() {
    return dd;
  }
  /**
   * @param dd the dd to set
   */
  public void setDd(String dd) {
    this.dd = dd;
  }
  /**
   * @return the xd
   */
  public List<String> getXd() {
    return xd;
  }
  /**
   * @param xd the xd to set
   */
  public void setXd(List<String> xd) {
    this.xd = xd;
  }
}
