package clp.edit.util;

import java.awt.Color;

public enum ColorSet {

  ProjectProperties     (0xffcce5, 0xcc00cc, "Project Name"),
  SetterProperties      (0xeed2ee, 0x8b668b, "Settings Block"),
  ResourcesProperties   (0xeed2ee, 0x8b668b, "Resources Block"),
  MetaScenarioProperties(0xffe4e1, 0x8b3626, "Meta-Scenario"),
  ScenarioProperties    (0xe6fae6, 0x9a32cd, "Scenario"),
  ActorProperties       (0xcaffff, 0x778899, "Actor"),
  HeapProperties        (0xc1ffc1, 0x698b69, "Heap"),
  CellProperties        (0xffffe0, 0xcdcdb4, "Cell"),

  notEditableProperty   (0xffffd0, 0, "Not Editable Property Field"),
  notEditableArea       (0xccffff, 0, "Not Editable Text Area"),

  activityBackground    (0xffffdc, 0, "Background of Activity Diagram"),
  grafcetBackground     (0xfcedcf, 0, "Background of Grafcet Diagram"),
  cpnBackground         (0xdffead, 0, "Background of Colored Petri Nets"),
  wpnBackground         (0xcffced, 0, "Background of Weighted Petri Nets"),

  selectedBackground    (0xe14169, 0, "Background of Selected Item");


  private Color light;
  private Color dark;
  private String nodeLabel;

  private ColorSet(int rgb1, int rgb2, String lbl) {
    light = new Color(rgb1);
    dark = new Color(rgb2);
    nodeLabel = lbl;
  }

  /**
   * @return the light
   */
  public Color getLight() {
    return light;
  }

  /**
   * @param light the light to set
   */
  public void setLight(Color light) {
    this.light = light;
  }

  /**
   * @return the dark
   */
  public Color getDark() {
    return dark;
  }

  /**
   * @param dark the dark to set
   */
  public void setDark(Color dark) {
    this.dark = dark;
  }

  /**
   * @return the nodeLabel
   */
  public String getNodeLabel() {
    return nodeLabel;
  }

  /**
   * @param nodeLabel the nodeLabel to set
   */
  public void setNodeLabel(String nodeLabel) {
    this.nodeLabel = nodeLabel;
  }
}
