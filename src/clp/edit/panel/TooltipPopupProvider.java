package clp.edit.panel;

import java.awt.Point;
import java.io.Serializable;

import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import clp.edit.graphics.shapes.AShape;

public class TooltipPopupProvider implements Serializable {

  private static final long serialVersionUID = 5011230388563491571L;

  transient private Popup popup;

  /**
   * show popup for tooltip text of given component
   * 
   * @param sel
   * @param point 
   */
  public void show(AShape sel, Point point) {
    if (popup == null) {
      JToolTip tip = sel.createToolTip();
      tip.setTipText(sel.getToolTipText());
      popup = PopupFactory.getSharedInstance().getPopup(sel, tip, point.x+20, point.y+20);
      sleep(500);
    }
  }

  //
  private void sleep(int t0) {
    try {
      Thread.sleep(t0);
      if (popup != null) {
        popup.show();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * hide existing popup
   */
  public void hide() {
    if (popup != null) {
      popup.hide();
    }
    popup = null;
  }
}
