package clp.edit.util;

import java.util.ArrayList;

public class FilesResolver implements IPlaceHolderResolver {

  private String message;
  private String title;
  private ArrayList<String> fileNames;
  private ArrayList<String> starters;

  private FilesResolver() {
    fileNames = new ArrayList<>();
    starters = new ArrayList<>();
  }

  @Override
  public String resolve(String string) {
    String[] parts = split(string);
    String res = parts[0];
    if (message != null) {
      res += message;
    }
    res += parts[1];
    if (title != null) {
      res += title;
    }
    res += parts[2];
    if (!fileNames.isEmpty()) {
      String s = fileNames.toString();
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
    sp = sp[1].split("FILE");
    parts[2] = sp[0];
    sp = sp[1].split("CSTARTER");
    parts[3] = sp[0];
    parts[4] = sp[1];
    return parts;
  }

  static public class Builder {
    private FilesResolver instance;
    public Builder() {
      instance = new FilesResolver();
    }
    public Builder setMessage(String s) {
      instance.message = s;
      return this;
    }
    public Builder setTitle(String s) {
      instance.title = s;
      return this;
    }
    public Builder addFileName(String s) {
      instance.fileNames.add(s);
      return this;
    }
    public Builder addStarter(String s) {
      instance.starters.add(s);
      return this;
    }
    public FilesResolver build() {
      if (instance.starters == null) {
        return null;
      }
      return instance;
    }
  }
}
