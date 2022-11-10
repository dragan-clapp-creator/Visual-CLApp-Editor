package clp.edit.graphics.dial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JDialog;

public class GenericActionListener implements ActionListener, Serializable {

  private static final long serialVersionUID = -2161874054986636126L;

  private JDialog dialog;

  private boolean isOk;

  private boolean isCancel;

  private boolean isInitial;

  private boolean isListenerAlone;

  public GenericActionListener(JDialog d) {
    dialog = d;
    isOk = false;
    isCancel = false;
    isInitial = true;
    isListenerAlone = true;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (((JButton)e.getSource()).getText().equals("ok")) {
      isOk = true;
    }
    else {
      isOk = false;
      isCancel = true;
    }
    isInitial = false;
    if (isListenerAlone) {
      dialog.dispose();
    }
  }

  public boolean isOk() {
    return isOk;
  }

  public boolean isInitial() {
    return isInitial;
  }

  /**
   * @return the isCancel
   */
  public boolean isCancel() {
    return isCancel;
  }

  /**
   * @param isListenerAlone the isListenerAlone to set
   */
  public void setListenerAlone(boolean isListenerAlone) {
    this.isListenerAlone = isListenerAlone;
  }
}
