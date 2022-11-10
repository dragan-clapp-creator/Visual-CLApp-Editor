package clp.edit.graphics.dial;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.act.ActivityShape;

public class ActivityDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = 4380597140182866078L;

  private JTextField field;
  private String name;
  private JTextArea area;

  private GenericActionListener gal;

  private ActivityShape activityShape;

  private JTextField wfield;
  private JTextField hfield;

  /**
   * CONSTRUCTOR
   * 
   * @param t
   * @param activityShape
   */
  public ActivityDialog(String t, ActivityShape activityShape) {
    super(GeneralContext.getInstance().getFrame(), "Defining an Activity", true);
    this.activityShape = activityShape;
    this.name = t;
    Frame parent = GeneralContext.getInstance().getFrame();
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    setPreferredSize(new Dimension(800, 350));
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;

    gal = new GenericActionListener(this);

    JButton okButton = new JButton("ok");
    okButton.addActionListener(gal);

    c.gridx = 0;
    c.gridy = 0;
    getContentPane().add(new JLabel("Activity"), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    field = new JTextField();
    field.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String n = getCurrentName();
        if (!n.equals(field.getText()) && !updateName(n, field.getText())) {
          field.setBackground(Color.red);
        }
        else {
          field.setBackground(Color.white);
        }
      }
    });
    field.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        String n = getCurrentName();
        if (!n.equals(field.getText()) && !updateName(n, field.getText())) {
          field.setBackground(Color.red);
        }
        else {
          field.setBackground(Color.white);
        }
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    field.setText(t);
    getContentPane().add(field, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel("Width"), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    wfield = new JTextField();
    wfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateSize(wfield.getText(), true);
      }
    });
    wfield.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        updateSize(wfield.getText(), true);
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    wfield.setText(""+activityShape.getWidth());
    getContentPane().add(wfield, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel("Height"), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    hfield = new JTextField();
    hfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateSize(hfield.getText(), false);
      }
    });
    hfield.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        updateSize(hfield.getText(), false);
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    hfield.setText(""+activityShape.getHeight());
    getContentPane().add(hfield, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel("Activity description"), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    area = new JTextArea(3, 40);
    getContentPane().add(area, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(Box.createVerticalStrut(5), c);

    c.gridx = 1;
    c.gridy++;
    getContentPane().add(okButton, c);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  private String getCurrentName() {
    return name;
  }

  public void updateFields() {
    hfield.setText(""+activityShape.getHeight());
    wfield.setText(""+activityShape.getWidth());
    area.setText(activityShape.getDesc());
  }

  //
  private boolean updateName(String oldName, String newName) {
    if (activityShape.rename(oldName.replace(" ", "_"), newName.replace(" ", "_"))) {
      name = newName;
      activityShape.setSimpleName(newName);
      return true;
    }
    return false;
  }

  //
  private void updateSize(String text, boolean isWidth) {
    int x = 0;
    try {
      x = Integer.parseInt(text);
      if (isWidth) {
        activityShape.setWidth(x);
      }
      else {
        activityShape.setHeight(x);
      }
      activityShape.refresh();
    }
    catch(NumberFormatException e) {
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }

  public void edit(String t, String d) {
    field.setText(t);
    updateFields();
    setVisible(true);
  }

  public String getTransitionText() {
    if (gal.isOk() || gal.isInitial()) {
      return field.getText();
    }
    return null;
  }

  public String getDescription() {
    if (gal.isOk() || gal.isInitial()) {
      return area.getText();
    }
    return null;
  }
}
