package clp.edit.graphics.code;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.swing.JButton;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.GenericActionListener;

public abstract class AInstructionDialog extends ADialog implements InstructionDialog {

  private static final long serialVersionUID = -8717304798471278558L;

  private JButton okButton;
  private GenericActionListener gal;

  private JButton cancelButton;

  abstract public void fillContent();

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param title
   * @param isVisible
   */
  public AInstructionDialog(Frame parent, String title, boolean isVisible) {
    super(parent, title, true);
    setup(parent);
    setVisible(isVisible);
  }

  //
  private void setup(Frame parent) {
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    setPreferredSize(new Dimension(800, 200));
    setLayout(new GridBagLayout());

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);

    fillContent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  public boolean isOk() {
    return gal.isOk();
  }

  /**
   * @return the okButton
   */
  public JButton getOkButton() {
    return okButton;
  }

  /**
   * @return the cancelButton
   */
  public JButton getCancelButton() {
    return cancelButton;
  }

  @Override
  public void edit(String text, String desc) {
  }

  @Override
  public String getTransitionText() {
    return null;
  }

  @Override
  public String getDescription() {
    return null;
  }
}
