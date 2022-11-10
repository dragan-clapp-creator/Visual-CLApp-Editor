package clp.edit.graphics.dial;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.AJoinShape;

public class JoinDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = -2557493284237047301L;

  private AJoinShape shape;

  private JTextField wfield;

  public JoinDialog(AJoinShape shape) {
    super(GeneralContext.getInstance().getFrame(), "Defining Width", true);
    Frame parent = GeneralContext.getInstance().getFrame();
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    this.shape = shape;
    setPreferredSize(new Dimension(400, 150));
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;

    JButton okButton = new JButton("ok");
    okButton.addActionListener(this);

    c.gridx = 0;
    c.gridy = 0;
    getContentPane().add(new JLabel("Width"), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    wfield = new JTextField();
    wfield.setText(""+shape.getWidth());
    getContentPane().add(wfield, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(Box.createVerticalStrut(5), c);

    c.gridx = 1;
    c.gridy++;
    getContentPane().add(okButton, c);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  public void updateFields() {
    wfield.setText(""+shape.getWidth());
  }

  private void updateSize(String text) {
    int x = 0;
    try {
      x = Integer.parseInt(text);
      shape.setWidth(x);
    }
    catch(NumberFormatException e) {
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateSize(wfield.getText());
    setVisible(false);
  }

  @Override
  public boolean isOk() {
    return true;
  }

  public void edit(String t, String d) {
    updateFields();
    setVisible(true);
  }

  public String getTransitionText() {
    return null;
  }

  public String getDescription() {
    return null;
  }
}
