package clp.edit.exp;

public class ExportActor {

  private String name;
  private String source;

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
   * @return the source
   */
  public String getSource() {
    return source;
  }
  /**
   * @param source the source to set
   */
  public void setSource(String src) {
    if (source == null) {
      source = src;
    }
    else {
      int i = source.lastIndexOf("}");
      source = source.substring(0, i) + src + "}";
    }
  }
}
