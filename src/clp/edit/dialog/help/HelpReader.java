package clp.edit.dialog.help;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.MissingResourceException;

import clp.edit.util.ContentReader;

public class HelpReader {

  private static final HelpReader instance = new HelpReader();

  private static final String HELP_FILE = "help.txt";

  private Hashtable<String, StringBuilder> keys;

  private HelpReader() {
    keys = new Hashtable<>();
    gatherKeys();
  }

  public static HelpReader getInstance() {
    return instance;
  }

  //
  private void gatherKeys() {
    try {
      InputStream in = HelpReader.class.getClassLoader().getResourceAsStream(HELP_FILE);
      ContentReader cr = new ContentReader(in);
      String line = cr.readLine();
      while (line != null) {
        boolean isStart = false;
        boolean isEnd = false;
        String key = null;
        while (!isStart && line != null) {
          isStart = line.endsWith("=====");
          if (isStart) {
            int index = line.indexOf("=");
            key = line.substring(0, index);
          }
          line = cr.readLine();
        }
        if (line != null) {
          StringBuilder sb = new StringBuilder();
          do {
            sb.append(line+"\n");
            line = cr.readLine();
            isEnd = line.endsWith(key);
          }
          while (!isEnd);
          keys.put(key, sb);
        }
      }
      in.close();

    }
    catch (MissingResourceException | IOException e) {
    }
  }

  public StringBuilder getContent(String key) {
    return keys.get(key);
  }
}
