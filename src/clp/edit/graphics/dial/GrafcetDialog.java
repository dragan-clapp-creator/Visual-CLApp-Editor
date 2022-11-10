package clp.edit.graphics.dial;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.gc.GrafcetShape;

public class GrafcetDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = 5214139773538020781L;

  private JTextField field;
  private String name;
  private JTextArea area;

  private GenericActionListener gal;

  private GrafcetShape grafcetShape;

  private JTextField wfield;

  private JTextField hfield;

  public GrafcetDialog(String t, GrafcetShape grafcetShape) {
    super(GeneralContext.getInstance().getFrame(), "Defining a Grafcet (Sequencial Flow Chart)", true);
    this.grafcetShape = grafcetShape;
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
    getContentPane().add(new JLabel("Grafcet name"), c);
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
    wfield.setText(""+grafcetShape.getWidth());
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
    hfield.setText(""+grafcetShape.getHeight());
    getContentPane().add(hfield, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel("Default Transition Type"), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    JPanel jp1 = createRadioButtonsForType();
    getContentPane().add(jp1, c);

    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel("Grafcet description"), c);
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

  //
  private boolean updateName(String oldName, String newName) {
    if (grafcetShape.rename(oldName.replace(" ", "_"), newName.replace(" ", "_"))) {
      name = newName;
      grafcetShape.setSimpleName(newName);
      return true;
    }
    return false;
  }

  //
  private JPanel createRadioButtonsForType() {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        "Choose Transition Type");
    jp.setBorder(border);

    ItemListener radioListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          grafcetShape.setDefaultTransitionType( TransitionType.getName(rb.getText()) );
        }
      }
    };
    ButtonGroup group = new ButtonGroup();
    TransitionType deftype = grafcetShape.getDefaultTransitionType();
    if (deftype == null) {
      deftype = TransitionType.TAUTOLOGY;
      grafcetShape.setDefaultTransitionType(deftype);
    }
    for (TransitionType t : TransitionType.values()) {
      JRadioButton rb = new JRadioButton(t.getVal(), true);
      jp.add(rb); rb.addItemListener(radioListener);
      if (t == deftype) {
        rb.setSelected(true);
      }
      else {
        rb.setSelected(false);
      }
      group.add(rb);
    }
    return jp;
  }

  public void updateFields() {
    hfield.setText(""+grafcetShape.getHeight());
    wfield.setText(""+grafcetShape.getWidth());
  }

  private void updateSize(String text, boolean isWidth) {
    int x = 0;
    try {
      x = Integer.parseInt(text);
      if (isWidth) {
        grafcetShape.setWidth(x);
      }
      else {
        grafcetShape.setHeight(x);
      }
      grafcetShape.refresh();
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
