package clp.edit.graphics.shapes.pn;

import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.PetriNetsDialog;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.ContainerHelper;
import clp.edit.util.ColorSet;

public class PetriNetsShape extends AContainerShape {

  private static final long serialVersionUID = -3146217438324352949L;

  transient private PetriNetsDialog dialog;

  private TransitionType defaultTransitionType;

  private boolean isColored;

  private ContainerHelper chelper;

  private String hname;

  /**
   * CONSTRUCTOR
   * 
   * @param width
   * @param height
   * @param text
   * @param count 
   * @param isColored
   */
  public PetriNetsShape(int width, int height, String text, int count, boolean isColored) {
    super(width, height, text+count);
    ColorSet color = isColored ? ColorSet.cpnBackground : ColorSet.wpnBackground;
    hname = "G_HEAP"+count;
    this.isColored = isColored;
    super.setBackground(color.getLight());
    dialog = new PetriNetsDialog(text+count, this, isColored);
    chelper = new ContainerHelper(count, getHighLevel());
    chelper.createTreeNodes(this, "PN_SCN", hname);
  }

  @Override
  public void paintShape(Graphics g, int offset, boolean b1, boolean b2) {
    super.paintShape(g, offset, b1, b2);
    if (getRoot() != null) {
      getRoot().paintShape(g, offset);
    }
  }

  @Override
  public void setChild(ABindingShape shape) {
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new PetriNetsDialog(getName(), this, isColored);
    }
    return dialog;
  }

  public void setSimpleName(String name) {
    super.setName(name);
  }

  @Override
  public StringBuilder getSource(String mscName) {
    StringBuilder sb = new StringBuilder();
    sb.append("scenario " + getScenarioName() + " {\n");
    sb.append("  properties {\n");
    sb.append("      logic {\n");
    sb.append("        deactivation = MANUAL;  // cell deactivation happens manually\n");
    sb.append("        level = 2;   // the are 2 (0, 1) activity levels for a cell\n");
    sb.append("      }\n");
    sb.append("      queues {\n");
    sb.append("        qa income=Qe;\n");
    sb.append("        qi income=Qe;\n");
    sb.append("      }\n");
    sb.append("      tasks {\n");
    sb.append("        ACTIVATOR operatingOn qi passingTo qa;\n");
    sb.append("        EXECUTOR operatingOn qa;\n");
    sb.append("        DEACTIVATOR operatingOn qa passingTo qi;\n");
    sb.append("      }\n");
    sb.append("  }\n");
    sb.append("}\n\n");
    return sb;
  }

  /**
   * @return the defaultTransitionType
   */
  public TransitionType getDefaultTransitionType() {
    return defaultTransitionType;
  }

  /**
   * @param defaultTransitionType the defaultTransitionType to set
   */
  public void setDefaultTransitionType(TransitionType defaultTransitionType) {
    this.defaultTransitionType = defaultTransitionType;
  }

  public String getWeightOrColor() {
    return ((PetriNetsDialog)getDialog()).getDefaultWeightOrColor();
  }

  public boolean isColored() {
    return isColored;
  }

  @Override
  public void removeActor() {
    chelper.removeActor("PN_SCN", getName());
  }

  public boolean rename(String oldName, String newName) {
    return chelper.rename(oldName, newName);
  }

  @Override
  public String getScenarioName() {
    return "PN_SCN";
  }

  @Override
  public String getHeapNamePrefix() {
    return hname;
  }

  @Override
  public void cacheFromTransients() {
  }
}
