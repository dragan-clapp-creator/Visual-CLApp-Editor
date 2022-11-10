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
import clp.edit.graphics.shapes.pn.PetriNetsShape;

public class PetriNetsDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = 4407745619847274599L;

  private static final String COLORS = "R.G.B.Y.O.C";

  private JTextField nfield;    // name field
  private String name;
  private JTextField wfield;    // width field
  private JTextField hfield;    // height field
  private JTextField nbfield;   // default either height or colors field (depends on argument
  private JTextArea area;       // description area

  private GenericActionListener gal;

  private PetriNetsShape pnShape;

  private int defaultNumber;

  private boolean isColored;


  /**
   * CONSTRUCTOR
   * 
   * @param name
   * @param pnShape
   * @param isColored
   */
  public PetriNetsDialog(String name, PetriNetsShape pnShape, boolean isColored) {
    super(GeneralContext.getInstance().getFrame(), "Defining a Petri Nets flow chart", true);
    this.pnShape = pnShape;
    this.name = name;
    this.isColored = isColored;
    this.defaultNumber = 1;
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
    nfield = new JTextField();
    nfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String n = getCurrentName();
        if (!n.equals(nfield.getText()) && !updateName(n, nfield.getText())) {
          nfield.setBackground(Color.red);
        }
        else {
          nfield.setBackground(Color.white);
        }
      }
    });
    nfield.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        String n = getCurrentName();
        if (!n.equals(nfield.getText()) && !updateName(n, nfield.getText())) {
          nfield.setBackground(Color.red);
        }
        else {
          nfield.setBackground(Color.white);
        }
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    nfield.setText(name);
    getContentPane().add(nfield, c);

    wfield = createTextField(c, "Width", true);
    getContentPane().add(wfield, c);

    hfield = createTextField(c, "Height", false);
    getContentPane().add(hfield, c);

    nbfield = createIntegerField(c, isColored ? "Default colors number" : "Default weight");
    getContentPane().add(nbfield, c);

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
    if (pnShape.rename(oldName.replace(" ", "_"), newName.replace(" ", "_"))) {
      name = newName;
      pnShape.setSimpleName(newName);
      return true;
    }
    return false;
  }

  //
  private JTextField createTextField(GridBagConstraints c, String text, boolean isWidth) {
    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel(text), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    JTextField fld = new JTextField();
    fld.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateSize(fld.getText(), isWidth);
      }
    });
    fld.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        updateSize(fld.getText(), isWidth);
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    fld.setText(""+(isWidth ? pnShape.getWidth() : pnShape.getHeight()));
    return fld;
  }

  //
  private JTextField createIntegerField(GridBagConstraints c, String text) {
    c.gridx = 0;
    c.gridy++;
    getContentPane().add(new JLabel(text), c);
    c.gridx = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx = 2;
    JTextField fld = new JTextField();
    fld.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateDefaultNumber(fld.getText());
      }
    });
    fld.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        updateDefaultNumber(fld.getText());
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    fld.setText(""+1);
    return fld;
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
          pnShape.setDefaultTransitionType( TransitionType.getName(rb.getText()) );
        }
      }
    };
    ButtonGroup group = new ButtonGroup();
    TransitionType deftype = pnShape.getDefaultTransitionType();
    if (deftype == null) {
      deftype = TransitionType.TAUTOLOGY;
      pnShape.setDefaultTransitionType(deftype);
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
    hfield.setText(""+pnShape.getHeight());
    wfield.setText(""+pnShape.getWidth());
  }

  private void updateSize(String text, boolean isWidth) {
    int x = 0;
    try {
      x = Integer.parseInt(text);
      if (isWidth) {
        pnShape.setWidth(x);
      }
      else {
        pnShape.setHeight(x);
      }
      pnShape.refresh();
    }
    catch(NumberFormatException e) {
    }
  }

  private void updateDefaultNumber(String text) {
    try {
      int x = Integer.parseInt(text);
      if (x < 7 && x > 0) {
        defaultNumber = x;
      }
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
    nfield.setText(t);
    updateFields();
    setVisible(true);
  }

  public String getTransitionText() {
    if (gal.isOk() || gal.isInitial()) {
      return nfield.getText();
    }
    return null;
  }

  public String getDescription() {
    if (gal.isOk() || gal.isInitial()) {
      return area.getText();
    }
    return null;
  }


  public String getDefaultWeightOrColor() {
    if (isColored) {
      return COLORS.substring(0, defaultNumber*2-1);
    }
    return ""+defaultNumber;
  }
}
