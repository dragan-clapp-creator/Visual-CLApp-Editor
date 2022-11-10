package clp.edit.handler;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import clp.edit.util.ColorSet;

public class ColorsHandler {

  public void retreivePreferredColors() {
    try {
      File rtf = new File("./colors");
      if (rtf.exists()) {
        FileInputStream fis = new FileInputStream(rtf);
        ObjectInputStream in = new ObjectInputStream(fis);
        for (ColorSet cs : ColorSet.values()) {
          Color color = (Color) in.readObject();
          cs.setLight(color);
          color = (Color) in.readObject();
          cs.setDark(color);
        }
        in.close();
      }
    }
    catch(IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void removePreferredColors() {
    File f = new File("./colors");
    File name = f.getAbsoluteFile();
    if (f.exists() && f.delete()) {
      System.out.println(name + " successfully deleted");
    }
  }

  public void savePreferredColors() {
    File f = new File("./colors");
    try {
      FileOutputStream fos = new FileOutputStream(f);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      for (ColorSet cs : ColorSet.values()) {
        out.writeObject(cs.getLight());
        out.writeObject(cs.getDark());
      }
      out.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
}
