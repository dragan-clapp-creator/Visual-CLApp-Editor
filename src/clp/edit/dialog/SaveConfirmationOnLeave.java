package clp.edit.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

public class SaveConfirmationOnLeave extends JDialog implements ActionListener {

  private static final long serialVersionUID = -7741688597161023383L;

  private boolean isOk;

  public SaveConfirmationOnLeave(Frame parent) {
    super(parent, "Leave Current Work", true);
    isOk = false;
    Point p = parent.getLocation(); 
    setLocation(p.x + 350, p.y + 200);

    createOwnContent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  public void createOwnContent() {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;

    JButton okButton = new JButton("Yes");
    okButton.addActionListener(this);

    JButton cancelButton = new JButton("No");
    cancelButton.addActionListener(this);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 3;
    getContentPane().add(new JTextArea("Do you really want to leave this project\n"
                                      + " without saving your changes"), c);
    c.gridwidth = 1;
    c.gridy = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridy = 2;
    getContentPane().add(cancelButton, c);
    c.gridx = 1;
    getContentPane().add(Box.createHorizontalStrut(70), c);
    c.gridx = 2;
    c.gridy = 2;
    getContentPane().add(okButton, c);
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    String name = ((JButton)e.getSource()).getText();
    if ("Yes".equals(name)) {
      isOk = true;
    }
    setVisible(false);
  }

  public boolean isOk() {
    return isOk;
  }
}
