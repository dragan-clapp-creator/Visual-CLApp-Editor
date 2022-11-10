package clp.edit.exp;

import java.util.ArrayList;

public class ExportScenrio {

  private String name;
  private String properties;
  private ArrayList<ExportActor> actors;

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
   * @return the properties
   */
  public String getProperties() {
    return properties;
  }
  /**
   * @param properties the properties to set
   */
  public void setProperties(String properties) {
    this.properties = properties;
  }
  /**
   * @return the actors
   */
  public ArrayList<ExportActor> getActors() {
    return actors;
  }
  /**
   * @param actors the actors to set
   */
  public void setActors(ArrayList<ExportActor> actors) {
    this.actors = actors;
  }

  public String getSource() {
    return "scenario " + getName() + " {\n" + getProperties() + "\n" + getChildrenSource() + "\n}\n";
  }

  //
  private String getChildrenSource() {
    String src = "";
    for (ExportActor act : actors) {
      src  += act.getSource();
    }
    return src;
  }
}
