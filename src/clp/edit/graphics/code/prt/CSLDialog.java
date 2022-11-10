package clp.edit.graphics.code.prt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.code.prt.CslContext.CslInfo;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.panel.GraphicsPanel;
import clp.parse.CLAppParser;
import clp.run.msc.Out;
import clp.run.msc.Output;
import clp.run.msc.OutputTarget;

public class CSLDialog extends ADialog {

  private static final long serialVersionUID = 382225187672842829L;

  private static String[] targets = {
      "CONSOLE", "IDENTIFIER", "FILE"
  };
  private static String[] outs = {
      "", "LOG", "ON", "OFF"
  };

  private Output outvar;

  private GenericActionListener gal;

  private JComboBox<String> combo;
  private String selection;

  private JTextField tfield;
  private String identifier;

  private JComboBox<String> onoff;
  private String type;

  private JTextField ffield;
  private String fcolor;

  private JTextField bfield;
  private String bcolor;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   */
  public CSLDialog(Frame parent) {
    this(parent, null);
    setVisible(true);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param info
   */
  public CSLDialog(Frame parent, CslInfo info) {
    super(parent, "Adding a Console", true);
    setup(info);

    setLayout(new SpringLayout());

    Dimension parentSize = parent.getSize(); 
    Point p = parent.getLocation(); 
    setLocation(p.x + parentSize.width * 2 / 5, p.y + parentSize.height / 4);

    fillContent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  //
  private void setup(CslInfo info) {
    if (info != null) {
      OutputTarget target = info.getOut().getOutputTarget();
      if (target.isStringCONSOLE()) {
        identifier = target.getStringCONSOLE();
        selection = targets[0];
      }
      else if (target.getSendFile() == null) {
        identifier = target.getName();
        selection = targets[1];
      }
      else {
        identifier = target.getSendFile().getFileName();
        selection = targets[2];
      }
      Out out = info.getOut().getOut();
      if (out == null) {
        type = "";
      }
      else {
        type = out.getVal();
      }
      String c = info.getOut().getColor();
      if (c == null) {
        fcolor = "";
      }
      else {
        fcolor = c.substring(1);
      }
      String b = info.getOut().getBackground();
      if (b == null) {
        bcolor = "";
      }
      else {
        bcolor = b.substring(1);
      }
    }
    else {
      identifier = "CONSOLE";
      type = "";
      fcolor = "";
      bcolor = "";
    }
  }

  public void jumpToCreation() {
    redrawLater();
    setVisible(true);
  }

  //
  private void redrawLater() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        getContentPane().removeAll();
        fillContent();
        repaint();
        validate();
      }
    });
  }

  //
  private void fillContent() {
    getContentPane().add(createTargetPanel());
    getContentPane().add(createColorsPanel());
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(),
        3, 1, //rows, cols
        6, 6, //initX, initY
        6, 6);//xPad, yPad
  }

  //
  private Component createTargetPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Define Output Target");
    p.setBorder(border);

    if (combo == null) {
      combo = new JComboBox<>(targets);
      combo.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          selection = (String) combo.getSelectedItem();
          tfield.setEnabled(combo.getSelectedIndex() > 0);
          if (combo.getSelectedIndex() == 0) {
            identifier = selection;
            tfield.setText(identifier);
          }
        }
      });
    }
    p.add(combo);
    if (tfield == null) {
      tfield = new JTextField(10);
      tfield.setText(identifier);
      tfield.setEnabled(false);
    }
    p.add(tfield);
    if (onoff == null) {
      onoff = new JComboBox<>(outs);
      onoff.setSelectedItem(type);
    }
    p.add(onoff);
    combo.setSelectedItem(selection);

    makeCompactGrid(p, 1, 3, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  //
  private Component createColorsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Define Forground/Background Colors");
    p.setBorder(border);

    JLabel l = new JLabel("Forground:", JLabel.TRAILING);
    p.add(l);
    if (ffield == null) {
      ffield = new JTextField(10);
      ffield.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          fcolor = checkColor(ffield.getText());
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
      ffield.setText(fcolor);
    }
    l.setLabelFor(ffield);
    p.add(ffield);
    JButton btn = new JButton("choose");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Color c = chooseColor(getColorFromText(fcolor));
        if (c != null) {
          fcolor = getTextFromColor(c);
          ffield.setText(fcolor);
        }
     }
    });
    btn.setForeground(Color.blue);
    btn.addActionListener(gal);
    p.add(btn);

    l = new JLabel("Background:", JLabel.TRAILING);
    p.add(l);
    if (bfield == null) {
      bfield = new JTextField(10);
      bfield.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          bcolor = checkColor(bfield.getText());
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
      bfield.setText(bcolor);
    }
    l.setLabelFor(bfield);
    p.add(bfield);
    btn = new JButton("choose");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Color c = chooseColor(getColorFromText(bcolor));
        if (c != null) {
          bcolor = getTextFromColor(c);
          bfield.setText(bcolor);
        }
     }
    });
    btn.setForeground(Color.blue);
    btn.addActionListener(gal);
    p.add(btn);

    makeCompactGrid(p, 2, 3, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  //
  private String checkColor(String txt) {
    String s = txt.startsWith("#") ? txt.substring(1) : txt;
    try {
      Integer.parseInt(s);
      return s;
    }
    catch (NumberFormatException e) {}
    return null;
  }

  //
  private Color getColorFromText(String txt) {
    if (txt == null || txt.isBlank()) {
      return null;
    }
    return new Color(Integer.decode("0x"+txt));
  }

  //
  private String getTextFromColor(Color c) {
    String s = Integer.toHexString(c.getRGB()).toUpperCase();
    return s.substring(s.length()-6);
  }

  //
  private Color chooseColor(Color c) {
    return JColorChooser.showDialog(this, "choose a color", c);
  }

  //
  private Component createControlsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Control");
    p.setBorder(border);

    p.add(new JLabel("     "));
    gal = new GenericActionListener(this);
    JButton cbtn = new JButton("cancel");
    cbtn.setForeground(Color.blue);
    cbtn.addActionListener(gal);
    p.add(cbtn);
    JButton okbtn = new JButton("ok");
    okbtn.setForeground(Color.blue);
    okbtn.addActionListener(gal);
    p.add(okbtn);

    makeCompactGrid(p, 1, 3, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  public boolean setupInstruction() {
    if ( parseStatement() ) {
      if (outvar != null) {
        if (outvar != null) {
          return true;
        }
        GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
        if (gp != null) {
          GeneralShapesContainer shapesContainer = gp.getShapesContainer();
          gp.getControlsContainer().getSimulationHelper().setDirty(true);
          return shapesContainer.addCslInfo(getInstructionName(), outvar);
        }
        GeneralContext.getInstance().setDirty();
        return true;
      }
    }
    return false;
  }

  private boolean parseStatement() {
    String statement = buildupStatement();
    if (statement != null) {
      InputStream is = new ByteArrayInputStream(statement.getBytes(), 0, statement.length());
      CLAppParser parser = new CLAppParser(new BufferedReader(new InputStreamReader(is)));
      clp.parse.msc.Output oparse = new clp.parse.msc.Output();
      try {
        oparse.parse(parser, false);
        if (parser.getError() == null) {
          outvar = oparse.getOutput();
          if (outvar != null && outvar.getOutputTarget() != null) {
            return true;
          }
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private String buildupStatement() {
    String s = tfield.getText();
    if (onoff.getSelectedIndex() > 0) {
      s += " " + onoff.getSelectedItem();
    }
    if (fcolor == null) {
      fcolor = "FFFFFF";
    }
    if (bcolor == null) {
      bcolor = "000000";
    }
    s += " \"#" +fcolor + "\" / \"#" + bcolor + "\"";
    return s;
  }

  public boolean isOk() {
    return gal.isOk();
  }

  public String getInstructionName() {
    if (outvar.getOutputTarget().isStringCONSOLE()) {
      return outvar.getOutputTarget().getStringCONSOLE();
    }
    return outvar.getOutputTarget().getName();
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

  /**
   * @return the outvar
   */
  public Output getOutvar() {
    return outvar;
  }
}
