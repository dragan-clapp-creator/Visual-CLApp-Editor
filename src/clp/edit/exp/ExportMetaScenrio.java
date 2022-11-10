package clp.edit.exp;

import java.util.ArrayList;

import clp.run.msc.Output;

public class ExportMetaScenrio {

  private String parentName;
  private String name;
  private String description;
  private String properties;
  private String host;
  private String port;
  private String resources;
  private ArrayList<ExportScenrio> scenarios;
  private ArrayList<Output> outputs;

  public ExportMetaScenrio() {
    scenarios = new ArrayList<>();
  }

  /**
   * @return the parentName
   */
  public String getParentName() {
    return parentName;
  }

  /**
   * @param parentName the parentName to set
   */
  public void setParentName(String parentName) {
    this.parentName = parentName;
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
   * @return the properties
   */
  public String getProperties() {
    if ((port == null || port.isBlank()) && outputs == null) {
      return properties;
    }
    int i = properties.lastIndexOf("}");
    String ret = properties.substring(0, i);
    if (port != null && !port.isBlank() && !ret.contains("port ")) {
      ret += "port " + port + ";\n";
    }
    if (outputs != null && !outputs.isEmpty()) {
      ret += declareOutputs(outputs);
    }
   return ret + "\n}";
  }

  //
  private String declareOutputs(ArrayList<Output> outputs) {
    String s = "output : ";
    for (int i=0; i< outputs.size(); i++) {
      Output o = outputs.get(i);
      if (i > 0) {
        s += "         ";
      }
      if (o.getOutputTarget().isStringCONSOLE()) {
        s += o.getOutputTarget().getStringCONSOLE();
      }
      else {
        s += o.getOutputTarget().getName();
      }
      if (o.isOut()) {
        s += " " + o.getOut().getVal();
      }
      s += " \""+o.getColor()+"\"/\""+o.getBackground()+"\"";
      s += i < outputs.size()-1 ? ",\n" : ";\n";
    }
    return s;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(String properties) {
    this.properties = properties;
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the port
   */
  public String getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(String port) {
    this.port = port;
  }

  /**
   * @return the resources
   */
  public String getResources() {
    if (resources == null) {
      return "";
    }
    return resources;
  }

  /**
   * @param resources the resources to set
   */
  public void setResources(String resources) {
    this.resources = resources;
  }

  /**
   * @return the scenarios
   */
  public ArrayList<ExportScenrio> getScenarios() {
    return scenarios;
  }

  /**
   * @param scenarios the scenarios to set
   */
  public void setScenarios(ArrayList<ExportScenrio> scenarios) {
    this.scenarios = scenarios;
  }

  public String getSource() {
    return "metaScenario " + getName() + " {\n" + getProperties() + getResources() + getChildrenSource() + "}\n";
  }

  private String getChildrenSource() {
    String src = "";
    for (ExportScenrio escn : scenarios) {
      src += escn.getSource();
    }
    return src;
  }

  public void setOutputs(ArrayList<Output> outputs) {
    this.outputs = outputs;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
