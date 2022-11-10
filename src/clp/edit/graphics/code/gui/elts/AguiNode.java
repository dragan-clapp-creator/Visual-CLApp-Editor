package clp.edit.graphics.code.gui.elts;

import java.util.ArrayList;

import clp.run.res.ui.UiGroup;
import clp.run.res.ui.UiLine;

abstract public class AguiNode extends AguiLeaf {

  private static final long serialVersionUID = -3198594131762377335L;

  /**
   * CONSCTRUCTOR
   * 
   * @param s
   */
  public AguiNode(String s) {
    super(s);
  }

  abstract public ArrayList<String> getVariables();
  abstract public void addGroup(UiGroup x);
  abstract public void addLine(UiLine x);

//  public void addListeners() {
//    for (int i=0; i<getChildCount(); i++) {
//      AguiLeaf c = (AguiLeaf) getChildAt(i);
//      if (c instanceof AguiNode) {
//        ((AguiNode) c).addListeners();
//      }
//      else {
//        c.createListeners();
//      }
//    }
//  }
}
