package clp.edit.graphics.btn;

import java.awt.Point;
import java.io.Serializable;

import clp.edit.graphics.shapes.AShape;

public interface IAutomaton extends Serializable {

  public enum ActionMode {
    NONE, INIT, STEP, DECISION, TRANSITION, FORK, JOIN, SELECT, MOVE;
  }

  public void performAction(AControlButton currentButton, AShape shape);
  public void performAction(ActionMode mode, Point delta, AShape shape, boolean isMultiSelect);
  public void updateEnabling();
  public void refreshButtons();
  public void setMode(ActionMode none);
  public ActionMode getMode();
  public void enabling(boolean b);
}
