package clp.edit.util;

import java.awt.Color;
import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {

  private static final long serialVersionUID = 9042198543305684702L;

  private File file;
  private Color color;

  private String name;
  private boolean isDirty;
  private boolean isGraphic;

  private String error;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
    isDirty = true;
  }
  /**
   * @return the isDirty
   */
  public boolean isDirty() {
    return isDirty;
  }
  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }
  /**
   * @param file the file to set
   */
  public void setFile(File file) {
    this.file = file;
    this.name = file.getName();
  }
  /**
   * @return the color
   */
  public Color getColor() {
    return color;
  }
  /**
   * @param color the color to set
   */
  public void setColor(Color color) {
    this.color = color;
  }
  /**
   * @return the error
   */
  public String getError() {
    return error;
  }
  /**
   * @param error the error to set
   */
  public void setError(String error) {
    this.error = error;
  }
  /**
   * @return the isGraphic
   */
  public boolean isGraphic() {
    return isGraphic;
  }
  /**
   * @param isGraphic the isGraphic to set
   */
  public void setGraphic(boolean isGraphic) {
    this.isGraphic = isGraphic;
  }
}
