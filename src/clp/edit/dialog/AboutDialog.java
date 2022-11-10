package clp.edit.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import clp.edit.CLAppEditor;

public class AboutDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = -1275080048139803148L;

  public AboutDialog(Frame parent) {
    super(parent, "About Visual CLApp Editor", true);
    setAlwaysOnTop(true);
    Point p = parent.getLocation(); 
    setLocation(p.x + 350, p.y + 200);
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;

    JButton okButton = new JButton("ok");
    okButton.addActionListener(this);

    ImageIcon icon = createImage("clapp.png", "logo");
    getContentPane().add(new JLabel(icon), c);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  private ImageIcon createImage(String path, String description) {
    URL imgURL = CLAppEditor.class.getClassLoader().getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    }
    System.err.println("Couldn't find file: " + path);
    return null;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    
  }

}
