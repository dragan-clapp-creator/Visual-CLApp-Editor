package clp.edit.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

public class CustomOutputStream extends OutputStream {

  private JScrollPane scrollpane;
  private String text = "";

  public CustomOutputStream(JScrollPane scrollpane) {
      this.scrollpane = scrollpane;
  }

  @Override
  public void write(int b) throws IOException {
    text += (char)b;
    if ((char)b == '\n') {
      JViewport vp = scrollpane.getViewport();
      if (vp.getComponent(0) instanceof JTextArea) {
        JTextArea ta = (JTextArea) vp.getComponent(0);
        ta.append(text);
      }
      text = "";
    }
  }
}
