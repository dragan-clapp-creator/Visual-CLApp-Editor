package clp.edit.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RecentHandler {

  private ArrayList<File> files = new ArrayList<>();

  public boolean add(File selectedFile) {
    if (!files.contains(selectedFile)) {
      files.add(selectedFile);
      saveFiles();
      return true;
    }
    return false;
  }

  /**
   * @return the files
   */
  public ArrayList<File> getFiles() {
    return files;
  }

  /**
   * @param files the files to set
   */
  public void setFiles(ArrayList<File> files) {
    this.files = files;
    saveFiles();
  }

  @SuppressWarnings("unchecked")
  public void populateRecentFiles() {
    Object obj = null;
    try {
      File rtf = new File("./recent");
      if (rtf.exists()) {
        FileInputStream fis = new FileInputStream(rtf);
        ObjectInputStream in = new ObjectInputStream(fis);
        obj = in.readObject();
        in.close();
        files.addAll( (ArrayList<File>) obj );
      }
    }
    catch(IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void saveFiles() {
    if (files.isEmpty()) {
      removeRecentlyOpened();
    }
    else {
      File f = new File("./recent");
      try {
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(files);
        out.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
  }

  public File getFile(String text) throws IOException {
    int i = text.indexOf('(');
    int j = text.indexOf(')');
    String fname = text.substring(i+1, j) + "/" + text.substring(0, i-1).trim();
    for (File f : files) {
      if (f.getCanonicalPath().equals(fname)) {
        return f;
      }
    }
    return null;
  }

  private void removeRecentlyOpened() {
    File f = new File("./recent");
    File name = f.getAbsoluteFile();
    if (f.exists() && f.delete()) {
      System.out.println(name + " successfully deleted");
    }
  }

}
