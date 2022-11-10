package clp.edit.util;

import java.io.IOException;
import java.io.InputStream;

public class ContentReader {
  private int count;
  private StringBuilder content;

  public ContentReader(InputStream in) throws IOException {
    content = readAll(in);
  }


  //
  public String readLine() {
    int index  = content.indexOf("\n", count);
    if (index > 0) {
      String line = content.substring(count, index);
      count = index+1;
      return line;
    }
    return null;
  }

  //
  private StringBuilder readAll(InputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      sb.append(new String(buf).substring(0, len));
    }
    return sb;
  }


  /**
   * @return the content
   */
  public synchronized StringBuilder getContent() {
    return content;
  }
}
