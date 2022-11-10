package clp.edit.graphics.code.java;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import clp.edit.graphics.code.ClappInstruction;

public class ClappJavaInstruction extends ClappInstruction {

  private static final long serialVersionUID = 2541682730890618440L;

  private File selectedFile;

  transient private JComboBox<String> classCombo;     // select class
  private String selectedClass;

  transient private JComboBox<String> constCombo;     // select constructor
  private String selectedCons;
  private ConstructorArgsInfo constArgs;    // constructor arguments
  private JTextField instanceField;         // instance name
  private String selectedInstance;

  transient private JComboBox<String> methodCombo;    // select method
  private String selectedMeth;
  private MethodArgsInfo methArgs;          // method arguments
  private MethodArgsInfo methReturn;        // method return code

  private String selectedType;

  public ClappJavaInstruction() {
    selectedType = "NEW";
  }

  /**
   * @return the classCombo
   */
  public JComboBox<String> getClassCombo() {
    return classCombo;
  }

  /**
   * @param classCombo the classCombo to set
   */
  public void setClassCombo(JComboBox<String> classCombo) {
    this.classCombo = classCombo;
  }

  /**
   * @return the constCombo
   */
  public JComboBox<String> getConstCombo() {
    return constCombo;
  }

  /**
   * @param constCombo the constCombo to set
   */
  public void setConstCombo(JComboBox<String> constCombo) {
    this.constCombo = constCombo;
  }

  /**
   * @return the constArgs
   */
  public ConstructorArgsInfo getConstArgs() {
    return constArgs;
  }

  /**
   * @param constArgs the constArgs to set
   */
  public void setConstArgs(ConstructorArgsInfo constArgs) {
    this.constArgs = constArgs;
  }

  /**
   * @return the instanceField
   */
  public JTextField getInstanceField() {
    return instanceField;
  }

  /**
   * @param instanceField the instanceField to set
   */
  public void setInstanceField(JTextField instanceField) {
    this.instanceField = instanceField;
    instanceField.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        setSelectedInstance(instanceField.getText());
      }
      
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
  }

  /**
   * @return the methodCombo
   */
  public JComboBox<String> getMethodCombo() {
    return methodCombo;
  }

  /**
   * @param methodCombo the methodCombo to set
   */
  public void setMethodCombo(JComboBox<String> methodCombo) {
    this.methodCombo = methodCombo;
  }

  /**
   * @return the methArgs
   */
  public MethodArgsInfo getMethArgs() {
    return methArgs;
  }

  /**
   * @param methArgs the methArgs to set
   */
  public void setMethArgs(MethodArgsInfo methArgs) {
    this.methArgs = methArgs;
  }

  /**
   * @return the methReturn
   */
  public MethodArgsInfo getMethReturn() {
    return methReturn;
  }

  /**
   * @param methReturn the methReturn to set
   */
  public void setMethReturn(MethodArgsInfo methReturn) {
    this.methReturn = methReturn;
  }

  /**
   * @return the selectedClass
   */
  public String getSelectedClass() {
    return selectedClass;
  }

  /**
   * @param selectedClass the selectedClass to set
   */
  public void setSelectedClass(String selectedClass) {
    this.selectedClass = selectedClass;
    this.classCombo.setSelectedItem(selectedClass);
  }

  /**
   * @return the selectedCons
   */
  public String getSelectedCons() {
    return selectedCons;
  }

  /**
   * @param selectedCons the selectedCons to set
   */
  public void setSelectedCons(String selectedCons) {
    this.selectedCons = selectedCons;
    this.constCombo.setSelectedItem(selectedCons);
  }

  /**
   * @return the selectedMeth
   */
  public String getSelectedMeth() {
    return selectedMeth;
  }

  /**
   * @param selectedMeth the selectedMeth to set
   */
  public void setSelectedMeth(String selectedMeth) {
    if (selectedMeth != null) {
      this.selectedMeth = selectedMeth;
    }
    this.methodCombo.setSelectedItem(selectedMeth);
  }

  /**
   * @return the selectedFile
   */
  public File getSelectedFile() {
    return selectedFile;
  }

  /**
   * @param selectedFile the selectedFile to set
   */
  public void setSelectedFile(File selectedFile) {
    this.selectedFile = selectedFile;
  }

  /**
   * @return the selectedInstance
   */
  public String getSelectedInstance() {
    return selectedInstance;
  }

  /**
   * @param selectedInstance the selectedInstance to set
   */
  public void setSelectedInstance(String selectedInstance) {
    this.selectedInstance = selectedInstance;
  }

  /**
   * @return the selectedType
   */
  public String getSelectedType() {
    return selectedType;
  }

  /**
   * @param selectedType the selectedType to set
   */
  public void setSelectedType(String selectedType) {
    this.selectedType = selectedType;
  }
}
