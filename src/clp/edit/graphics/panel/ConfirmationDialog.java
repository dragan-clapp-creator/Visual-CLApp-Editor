package clp.edit.graphics.panel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import clp.edit.graphics.dial.GenericActionListener;

public class ConfirmationDialog extends JDialog {

  private static final long serialVersionUID = 6487459610119222090L;

  private GridBagConstraints c;

  private JButton okButton;
  private JButton cancelButton;
  private GenericActionListener gal;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   */
  public ConfirmationDialog(Frame parent) {
    super(parent, "Confirmation Dialog", true);
    setup(parent);
    setVisible(true);
  }

  //
  private void setup(Frame parent) {
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    setPreferredSize(new Dimension(600, 200));
    setLayout(new GridBagLayout());

    c = new GridBagConstraints();
    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);

    fillContent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  //
  private void fillContent() {
    c.gridy = 0;
    c.gridx = 0;
    c.gridwidth = 2;
    getContentPane().add(new JLabel("Do you really want to delete this container?"), c);

    c.gridy = 2;
    c.gridx = 0;
    c.gridwidth = 1;
    getContentPane().add(cancelButton, c);

    c.gridx = 1;
    getContentPane().add(okButton, c);
  }

  public boolean isOk() {
    return gal.isOk();
  }
}
