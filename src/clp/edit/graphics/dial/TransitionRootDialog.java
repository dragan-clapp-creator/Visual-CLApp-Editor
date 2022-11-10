package clp.edit.graphics.dial;

import java.awt.Frame;

import clp.edit.graphics.shapes.ATransitionRoot;


public class TransitionRootDialog extends TransitionDialog {

  private static final long serialVersionUID = 3254365094632125403L;


  /**
   * CONSTRUCTOR
   * 
   * @param frame
   * @param ts transitionNodeShape
   */
  public TransitionRootDialog(Frame frame, ATransitionRoot ts) {
    super(frame, null, ts, 200);
  }
}
