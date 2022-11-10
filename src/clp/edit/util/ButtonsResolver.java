package clp.edit.util;

import java.util.ArrayList;

public class ButtonsResolver implements IPlaceHolderResolver {

  private ArrayList<String> messages;
  private ArrayList<String> titles;
  private ArrayList<String> buttons;
  private ArrayList<String> starters;

  private ButtonsResolver() {
    messages = new ArrayList<>();
    titles = new ArrayList<>();
    buttons = new ArrayList<>();
    starters = new ArrayList<>();
  }

  @Override
  public String resolve(String string) {
    String[] parts = split(string);
    String res = parts[0];
    if (!messages.isEmpty()) {
      String s = messages.toString();
      s = "\"" + s.substring(1, s.length()-1) + "\"";
      res += s.replace(", ", "\", \"");
    }
    res += parts[1];
    if (!titles.isEmpty()) {
      String s = titles.toString();
      s = "\"" + s.substring(1, s.length()-1) + "\"";
      res += s.replace(", ", "\", \"");
    }
    res += parts[2];
    if (!buttons.isEmpty()) {
      String s = buttons.toString();
      s = "\"" + s.substring(1, s.length()-1) + "\"";
      res += s.replace(", ", "\", \"");
    }
    res += parts[3];
    String s = starters.toString();
    s = "\"" + s.substring(1, s.length()-1) + "\"";
    res += s.replace(", ", "\", \"");
    res += parts[4];
    return res;
  }

  //
  private String[] split(String string) {
    String[] parts = new String[5];
    String[] sp = string.split("MESSAGE");
    parts[0] = sp[0];
    sp = sp[1].split("TITLE/HOST/PORT/CRYPTER");
    parts[1] = sp[0];
    sp = sp[1].split("BUTTON");
    parts[2] = sp[0];
    sp = sp[1].split("CSTARTER");
    parts[3] = sp[0];
    parts[4] = sp[1];
    return parts;
  }

  static public class Builder {
    private ButtonsResolver instance;
    public Builder() {
      instance = new ButtonsResolver();
    }
    public Builder addMessage(String s) {
      instance.messages.add(s);
      return this;
    }
    public Builder addTitle(String s) {
      instance.titles.add(s);
      return this;
    }
    public Builder addButton(String s) {
      instance.buttons.add(s);
      return this;
    }
    public Builder addStarter(String s) {
      instance.starters.add(s);
      return this;
    }
    public ButtonsResolver build() {
      if (instance.starters == null) {
        return null;
      }
      return instance;
    }
  }
}
