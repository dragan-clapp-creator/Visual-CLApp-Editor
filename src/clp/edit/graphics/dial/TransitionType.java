package clp.edit.graphics.dial;

public enum TransitionType {

  TAUTOLOGY("Tautology (= 1)"), CLASSICAL("Events & Conditions"), DELAY("Temporary Delay");

  private String value;

  private TransitionType(String s) {
    value = s;
  }

  static TransitionType getName(String selectedItem) {
    TransitionType[] names = values();
    for (TransitionType t : names) {
      if (t.value.equalsIgnoreCase(selectedItem)) {
        return t;
      }
    }
    return null;
  }

  public String getVal() {
    return value;
  }
}
