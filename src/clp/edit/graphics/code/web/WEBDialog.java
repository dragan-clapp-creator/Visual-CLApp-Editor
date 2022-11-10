package clp.edit.graphics.code.web;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.code.web.WebContext.WebInfo;
import clp.edit.graphics.dial.GenericActionListener;
import clp.parse.CLAppParser;
import clp.run.res.WebVariable;

public class WEBDialog extends ADialog {

  private static final long serialVersionUID = -6069237536711378288L;

  private WebVariable webvar;
  private WebInfo webInfo;

  private GenericActionListener gal;

  private String ident;
  private String pack;
  private String clazz;
  private String addr;
  private String port;

  private JTextField nfield;
  private JTextField pfield;
  private JTextField cfield;
  private JTextField afield;
  private JTextField field;

  private WebContext webContext;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param webContext 
   */
  public WEBDialog(Frame parent, WebContext webContext) {
    this(parent, null, webContext);
    setVisible(true);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param info
   * @param wc webContext 
   */
  public WEBDialog(Frame parent, WebInfo info, WebContext wc) {
    super(parent, "Adding a WEB SENDER", true);
    webContext = wc;
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
  private void setup(WebInfo info) {
    if (info != null) {
      this.webInfo = info;
      WebVariable wvar = info.getWebVar();
      ident = wvar.getName();
      if (wvar.isEncryption()) {
        pack = wvar.getEncryption().getClazz().getPack();
        clazz = wvar.getEncryption().getClazz().getClazz();
      }
      if (wvar.isAddress()) {
        addr = wvar.getAddress().getAddr();
      }
      port = ""+wvar.getPort().getNum();
    }
  }

  public void jumpToCreation() {
    nfield.setText(ident);
    pfield.setText(pack);
    cfield.setText(clazz);
    afield.setText(addr);
    field.setText(port);
    
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

  private void fillContent() {
    getContentPane().add(createIdentifierPanel());
    getContentPane().add(createEncryptionPanel());
    getContentPane().add(createWebAddressPanel());
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(),
        4, 1, //rows, cols
        6, 6, //initX, initY
        6, 6);//xPad, yPad
  }

  //
  private Component createIdentifierPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Identification");
    p.setBorder(border);

    JLabel l = new JLabel("Identifier:", JLabel.TRAILING);
    p.add(l);
    if (nfield == null) {
      nfield = new JTextField(10);
      nfield.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          ident = nfield.getText();
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
    }
    l.setLabelFor(nfield);
    p.add(nfield);

    //Lay out the panel.
    makeCompactGrid(p,
                    1, 2, //rows, cols
                    6, 6, //initX, initY
                    6, 6);//xPad, yPad

    //Set up the content pane.
    p.setOpaque(true);  //content panes must be opaque
    return p;
  }

  //
  private Component createEncryptionPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Select Encryption File");
    p.setBorder(border);

    JLabel l = new JLabel("Package:", JLabel.TRAILING);
    p.add(l);
    if (pfield == null) {
      pfield = new JTextField(10);
      pfield.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          pack = pfield.getText();
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
    }
    l.setLabelFor(pfield);
    p.add(pfield);

    l = new JLabel("Class:", JLabel.TRAILING);
    p.add(l);
    if (cfield == null) {
      cfield = new JTextField(10);
      cfield.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          clazz = cfield.getText();
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
    }
    l.setLabelFor(cfield);
    p.add(cfield);

    makeCompactGrid(p,
                    1, 4, //rows, cols
                    6, 6, //initX, initY
                    6, 6);//xPad, yPad

    p.setOpaque(true);
    return p;
  }

  //
  private Component createWebAddressPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Declare Web Address");
    p.setBorder(border);

    JLabel l = new JLabel("IP Adress:", JLabel.TRAILING);
    p.add(l);
    if (afield == null) {
      afield = new JTextField(10);
      afield.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          addr = afield.getText();
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
    }
    l.setLabelFor(afield);
    p.add(afield);

    l = new JLabel("Port:", JLabel.TRAILING);
    p.add(l);
    if (field == null) {
      field = new JTextField(10);
      field.addFocusListener(new FocusListener() {
        @Override
        public void focusLost(FocusEvent e) {
          port = field.getText();
        }
        @Override
        public void focusGained(FocusEvent e) {
        }
      });
    }
    l.setLabelFor(field);
    p.add(field);

    makeCompactGrid(p,
                    1, 4, //rows, cols
                    6, 6, //initX, initY
                    6, 6);//xPad, yPad

    p.setOpaque(true);
    return p;
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

    makeCompactGrid(p,
                    1, 3, //rows, cols
                    6, 6, //initX, initY
                    6, 6);//xPad, yPad

    p.setOpaque(true);
    return p;
  }

  public boolean setupInstruction() {
    String statement = parseStatement();
    if ( statement != null ) {
      if (webvar != null) {
        if (webInfo != null) {
          return true;
        }
        return webContext.addWebInfo(webvar.getName(), webvar, statement);
      }
    }
    return false;
  }

  private String parseStatement() {
    String statement = buildupStatement();
    if (statement != null) {
      InputStream is = new ByteArrayInputStream(statement.getBytes(), 0, statement.length());
      CLAppParser parser = new CLAppParser(new BufferedReader(new InputStreamReader(is)));
      clp.parse.res.WebVariable wparse = new clp.parse.res.WebVariable();
      try {
        wparse.parse(parser, false);
        if (parser.getError() == null) {
          webvar = wparse.getWebVariable();
          if (webvar != null) {
            return statement;
          }
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private String buildupStatement() {
    if (ident == null || ident.isBlank() || !isInteger(port)) {
      return null;
    }
    String s = "WEB " + ident + " {\n";
    if (pack != null && !pack.isBlank() || clazz != null && !clazz.isBlank()) {
      if (clazz == null || clazz.isBlank()) {
        return null;
      }
      s += "encryptFile ";
      if (pack != null && !pack.isBlank()) {
        s += "\"" + pack + "\" : ";
      }
      s += "\"" + clazz + "\"";
    }
    if (addr != null && !addr.isBlank()) {
      s += " address \"" + addr + "\"";
    }
    s += " port " + port + "\n}\n";
    return s;
  }

  //
  private boolean isInteger(String p) {
    if (p == null || p.isBlank()) {
      return false;
    }
    try {
      Integer.parseInt(p);
      return true;
    }
    catch (NumberFormatException e) {
      return false;
    }
  }

  public boolean isOk() {
    return gal.isOk();
  }

  public String getInstructionName() {
    if (webvar == null) {
      return null;
    }
    return webvar.getName();
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
