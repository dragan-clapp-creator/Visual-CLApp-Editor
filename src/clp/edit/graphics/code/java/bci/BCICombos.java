package clp.edit.graphics.code.java.bci;

import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class BCICombos implements Serializable {

  private static final long serialVersionUID = -5092692230018157790L;

  private JTextField weaverField;           // weaver identifier

  private JComboBox<String> classCombo;     // select class

  private JComboBox<String> methodCombo;    // select method

  private JComboBox<String> fieldCombo;     // select field (global variable)

  private JComboBox<String> localCombo;     // select local variable

  /**
   * CONSTRUCTOR
   */
  public BCICombos() {
  }

  /**
   * @return the weaverField
   */
  public JTextField getWeaverField() {
    return weaverField;
  }

  /**
   * @return the classCombo
   */
  public JComboBox<String> getClassCombo() {
    return classCombo;
  }

  /**
   * @return the methodCombo
   */
  public JComboBox<String> getMethodCombo() {
    return methodCombo;
  }

  /**
   * @return the fieldCombo
   */
  public JComboBox<String> getFieldCombo() {
    return fieldCombo;
  }

  /**
   * @return the localCombo
   */
  public JComboBox<String> getLocalCombo() {
    return localCombo;
  }

  /**
   * @param weaverField the weaverField to set
   */
  public void setWeaverField(JTextField weaverField) {
    this.weaverField = weaverField;
  }

  /**
   * @param classCombo the classCombo to set
   */
  public void setClassCombo(JComboBox<String> classCombo) {
    this.classCombo = classCombo;
  }

  /**
   * @param methodCombo the methodCombo to set
   */
  public void setMethodCombo(JComboBox<String> methodCombo) {
    this.methodCombo = methodCombo;
  }

  /**
   * @param fieldCombo the fieldCombo to set
   */
  public void setFieldCombo(JComboBox<String> fieldCombo) {
    this.fieldCombo = fieldCombo;
  }

  /**
   * @param localCombo the localCombo to set
   */
  public void setLocalCombo(JComboBox<String> localCombo) {
    this.localCombo = localCombo;
  }

  public String getMethodName() {
    return ((String) methodCombo.getSelectedItem()).split("/")[0];
  }

  public int getMethodArgumentsCount() {
    return ((String) methodCombo.getSelectedItem()).split("/")[2].length()-2;
  }
}
