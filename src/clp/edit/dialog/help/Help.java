package clp.edit.dialog.help;

public enum Help {

  INIT,
  START,
  DIAGRAM,
  GRAFCET,
  WPETRI,
  CPETRI,
  INSTRUCTION,
  GLOBAL,
  EXPORT,
  GUI,
  BCI,
  WEB,
  CSL,
  BEHAVIOR,
  ELEMENTS;

  private Help() {
  }

  /**
   * @return the content template
   */
  public StringBuilder getContent() {
    return HelpReader.getInstance().getContent(name());
  }

  @Override
  public String toString() {
    return getContent().toString();
  }
}
