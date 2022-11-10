package clp.edit.graphics.shapes.act;

import java.awt.Graphics;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.ActivityDialog;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ContainerHelper;
import clp.edit.util.ColorSet;

public class ActivityShape extends AContainerShape {

  private static final long serialVersionUID = 5414309053600708426L;

  transient private ActivityDialog dialog;

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
  public ActivityShape(int width, int height, String text, int count) {
    super(width, height, text+count);
    super.setBackground(ColorSet.activityBackground.getLight());
    hname = "G_HEAP"+count;
    dialog = new ActivityDialog(text+count, this);
    chelper = new ContainerHelper(count, getHighLevel());
    chelper.createTreeNodes(this, getScenarioName(), hname);
  }

  public void addShape(AShape shape) {
    setRoot(shape);
  }

  @Override
  public void paintShape(Graphics g, int offset, boolean b1, boolean b2) {
    super.paintShape(g, offset, b1, b2);
    if (getRoot() != null) {
      getRoot().paintShape(g, offset);
    }
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new ActivityDialog(getName(), this);
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
    sb.append("        deactivation = MANUAL;  // means cell deactivation happens manually (use of DEACTIVATOR)\n");
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

  @Override
  public void removeActor() {
    chelper.removeActor("AD_SCN", getName());
  }

  public boolean rename(String oldName, String newName) {
    return chelper.rename(oldName, newName);
  }

  @Override
  public String getScenarioName() {
    return "AD_SCN";
  }

  @Override
  public String getHeapNamePrefix() {
    return hname;
  }
}