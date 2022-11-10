package clp.edit.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;

import clp.edit.handler.ColorsHandler;
import clp.edit.util.ColorSet;

public class ColorsDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = -5463000147177706824L;
 
  private JButton okButton;
  private JButton cancelButton;
  private JButton resetButton;

  private ColorsHandler chandler;

  public ColorsDialog(Frame parent, ColorsHandler chandler) {
    super(parent, "Customize Color Preferences", true);
    this.chandler = chandler;
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

    Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
    attributes.put(TextAttribute.FAMILY, Font.MONOSPACED);
    Font font2 = new JLabel().getFont().deriveFont(attributes);
    c.gridy = 0;
    for (ColorSet cs : ColorSet.values()) {
      c.gridx = 0;
      getContentPane().add(Box.createVerticalStrut(5), c);
      c.gridx = 1;
      JButton button1 = new JButton();
      JLabel label1 = new JLabel(cs.getNodeLabel()+" light");
      label1.setBackground(cs.getLight());
      label1.setFont(font2);
      label1.setOpaque(true);
      button1.add(label1);
      getContentPane().add(button1, c);
      button1.addActionListener(this);
      c.gridx = 2;
      JButton button2 = new JButton();
      if (!cs.getDark().equals(Color.black)) {
        JLabel label2 = new JLabel(cs.getNodeLabel()+" dark");
        label2.setBackground(cs.getDark());
        label2.setFont(font2);
        label2.setOpaque(true);
        button2.add(label2);
      }
      getContentPane().add(button2, c);
      button2.addActionListener(this);
      c.gridy++;
    }
    c.gridwidth = 1;
    c.gridx = 0;
    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(this);
    getContentPane().add(cancelButton, c);
    c.gridx = 1;
    okButton = new JButton("ok");
    okButton.addActionListener(this);
    getContentPane().add(okButton, c);
    c.gridx = 3;
    resetButton = new JButton("reset");
    resetButton.addActionListener(this);
    getContentPane().add(resetButton, c);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == resetButton) {
      chandler.removePreferredColors();
      dispose();
    }
    else if (e.getSource() == cancelButton) {
      dispose();
    }
    else if (e.getSource() == okButton) {
      chandler.savePreferredColors();
      dispose();
    }
    else {
      performAction(e);
    }
  }

  //
  private void performAction(ActionEvent e) {
    JButton btn = (JButton)e.getSource();
    JLabel label = (JLabel) btn.getComponent(0);
    for (ColorSet cs : ColorSet.values()) {
      if (label.getText().equals(cs.getNodeLabel()+" light")) {
        Color color = JColorChooser.showDialog(this, "choose a color", cs.getLight());
        if (color != null) {
          cs.setLight(color);
          label.setBackground(color);
        }
        break;
      }
      if (label.getText().equals(cs.getNodeLabel()+" dark")) {
        Color color = JColorChooser.showDialog(this, "choose a color", cs.getDark());
        if (color != null) {
          cs.setLight(color);
          label.setBackground(color);
        }
        break;
      }
    }
  }
}
