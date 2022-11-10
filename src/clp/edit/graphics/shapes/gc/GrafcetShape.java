package clp.edit.graphics.shapes.gc;

import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.GrafcetDialog;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.ContainerHelper;
import clp.edit.util.ColorSet;

public class GrafcetShape extends AContainerShape {

  private static final long serialVersionUID = -7465353380950117778L;

  transient private GrafcetDialog dialog;

  private TransitionType defaultTransitionType;

  private ContainerHelper chelper;

  private String hname;

  /**
   * CONSTRUCTOR
   * 
   * @param width
   * @param height
   * @param text
   * @param count 
   */
  public GrafcetShape(int width, int height, String text, int count) {
    super(width, height, text+count);
    super.setBackground(ColorSet.grafcetBackground.getLight());
    hname = "G_HEAP"+count;
    dialog = new GrafcetDialog(text+count, this);
    chelper = new ContainerHelper(count, getHighLevel());
    chelper.createTreeNodes(this, getScenarioName(), hname);
  }

  @Override
  public void paintShape(Graphics g, int offset, boolean b1, boolean b2) {
    super.paintShape(g, offset, b1, b2);
    if (getRoot() != null) {
      getRoot().paintShape(g, offset);
    }
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new GrafcetDialog(getName(), this);
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
    sb.append("        deactivation = MANUAL;  // cell deactivation happens manually (use of DEACTIVATOR)\n");
    sb.append("        level = 3;   // the are 3 (0, 1, 2) activity levels for a cell\n");
    sb.append("        type = +;    // an activation is done as soon the activation condition is true (activity level goes from 0 to 2)\n");
    sb.append("                     // each execution step lowers the activity level by 1 (2->1->0) (at 0, nothing can be executed anymore)\n");
    sb.append("      }\n");
    sb.append("      queues {\n");
    sb.append("        qa income=Qe;\n");
    sb.append("        qi income=Qe;\n");
    sb.append("      }\n");
    sb.append("      tasks {\n");
    sb.append("        ACTIVATOR operatingOn qi passingTo qa;\n");
    sb.append("        EXECUTOR operatingOn qa;\n");
    sb.append("        DEACTIVATOR operatingOn qa passingTo qi;\n");
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

  @Override
  public void removeActor() {
    chelper.removeActor("GC_SCN", getName());
  }

  public boolean rename(String oldName, String newName) {
    return chelper.rename(oldName, newName);
  }

  @Override
  public String getScenarioName() {
    return "GC_SCN";
  }

  @Override
  public String getHeapNamePrefix() {
    return hname;
  }

  @Override
  public int getHighLevel() {
    return 2;
  }
}
