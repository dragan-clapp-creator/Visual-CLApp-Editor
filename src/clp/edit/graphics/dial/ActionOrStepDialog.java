package clp.edit.graphics.dial;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.code.InstructionDialog;
import clp.edit.graphics.code.asn.AssignDialog;
import clp.edit.graphics.code.exit.ExitDialog;
import clp.edit.graphics.code.gui.GuiActionDialog;
import clp.edit.graphics.code.java.ClappJavaInstruction;
import clp.edit.graphics.code.java.JavaCallDialog;
import clp.edit.graphics.code.java.JavaContext;
import clp.edit.graphics.code.prt.PrintDialog;
import clp.edit.graphics.code.ref.ReflectDialog;
import clp.edit.graphics.code.web.WEBcallDialog;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ActionShape;


public class ActionOrStepDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = 4917878048633714108L;

  static public enum IntructionType {
    NONE(""),
    ASSIGN("Assignment"),   // conditional assignment (A if b) or normal one
    PRINT("Print"),         // print to the console
    REFLECT("Reflect"),     // reflect internal structure
    STOP("Stop"),           // exit the simulation
    UI("UI call"),          // allows building of UI
    JAVA("Java call"),      // java startall
    WEB("WEB sender call"); // send, request

    private String value;
    private IntructionType(String s) {
      value = s;
    }
    static IntructionType getName(String selectedItem) {
      IntructionType[] names = values();
      for (IntructionType t : names) {
        if (t.value.equalsIgnoreCase(selectedItem)) {
          return t;
        }
      }
      return null;
    }
    public String getValue() {
      return value;
    }
  }

  private JTextField namefield;
  private JTextField descriptionfield;
  private List<InstructionDialog> instructionDialogs;

  private GenericActionListener gal;

  private JButton okButton;

  private ActionShape caller;
  private List<ClappInstruction> instructions;

  private JCheckBox jcheck;
  transient private JComboBox<String> jcombo;


  /**
   * CONSTRUCTOR
   * 
   * @param parent frame
   * @param cal ActionShape caller
   */
  public ActionOrStepDialog(Frame parent, ActionShape cal) {
    this(parent, cal, null);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param parent frame
   * @param cal ActionShape caller
   * @param instList instructions list
   */
  public ActionOrStepDialog(Frame parent, ActionShape cal, List<ClappInstruction> instList) {
    super(parent, "Defining an Action Item", true);
    Dimension parentSize = parent.getSize(); 
    Point p = parent.getLocation(); 
    setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    caller = cal;
    setLayout(new SpringLayout());

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);
    namefield = new JTextField(15);
    namefield.setEnabled(false);
    descriptionfield = new JTextField(25);
    if (instList != null) {
      setInstructions(instList);
    }
    else {
      instructionDialogs = new ArrayList<>();
      instructions = new ArrayList<>();
    }

    defineContent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  private void setupBciContent(JavaContext jc) {
    if (jcombo == null) {
      jcombo = new JComboBox<>();
      jcombo.setEnabled(false);
      jcombo.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (jcombo.isEnabled()) {
            String name = (String) jcombo.getSelectedItem();
            caller.setBciName(name);
            refresh();
          }
        }
      });
    }
    if (jcombo.getItemCount() != jc.getBciList().size()+1) {
      jcombo.removeAllItems();
      jcombo.addItem(null);
      for (String key : jc.getBciList()) {
        jcombo.addItem(key);
      }
      if (jcheck == null && jcombo.getItemCount() > 0) {
        jcheck = new JCheckBox("Use Byte-Code Injection:");
        jcheck.addChangeListener(new ChangeListener() {
          @Override
          public void stateChanged(ChangeEvent e) {
            if (jcheck.isSelected()) {
              jcombo.setEnabled(true);
              jcombo.setSelectedItem(caller.getBciName());
            }
            else {
              jcombo.setEnabled(false);
              caller.setBciName(null);
            }
          }
        });
        if (caller.getBciName() != null && !caller.getBciName().isBlank()) {
          jcheck.setSelected(true);
        }
      }
    }
  }

  //
  private void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        defineContent();
        pack(); 
      }
    });
  }

  //
  public void defineContent() {
    getContentPane().removeAll();

    getContentPane().add(createDescriptionPanel());
    getContentPane().add(createInstructionsPanel());
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(), 3, 1, 6, 6, 6, 6);
  }

  public JPanel createDescriptionPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Actions Description");
    p.setBorder(border);

    JLabel l = new JLabel("Action name:", JLabel.TRAILING);
    p.add(l);
    l.setLabelFor(namefield);
    p.add(namefield);

    l = new JLabel("Action description:", JLabel.TRAILING);
    p.add(l);
    l.setLabelFor(descriptionfield);
    p.add(descriptionfield);

    makeCompactGrid(p, 2, 2, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  public JPanel createInstructionsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Instructions");
    p.setBorder(border);

    boolean isJavaCall = false;
    int rows = 0;
    for (; rows<instructionDialogs.size(); rows++) {
      InstructionDialog instDial = instructionDialogs.get(rows);
      if (instDial instanceof JavaCallDialog) {
        isJavaCall = true;
      }
      JLabel l = new JLabel("Instruction "+rows+":", JLabel.TRAILING);
      p.add(l);
      JPanel jp = createInfoFrom(instDial);
      l.setLabelFor(jp);
      p.add(jp);
      JButton cbutton = new JButton("change");
      cbutton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          instDial.reset();
          ((JDialog) instDial).setVisible(true);
          if (instDial.isOk()) {
            instDial.setupInstruction();
            refresh();
          }
          else {
            instDial.getInstruction().getOldStatement();
          }
        }
      });
      p.add(cbutton);
      JButton rbutton = new JButton("remove");
      rbutton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          instructionDialogs.remove(instDial);
          refresh();
        }
      });
      p.add(rbutton);
    }
    if (isJavaCall) {
      rows++;
      proposeBCIOption(p);
    }
    JLabel l = new JLabel("New Instruction:", JLabel.TRAILING);
    p.add(l);
    JComboBox<String> itypefield = new JComboBox<>();
    GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    for (IntructionType itype : IntructionType.values()) {
      if (itype == IntructionType.UI && !sc.getUiList().isEmpty() ||
          itype == IntructionType.WEB && !sc.getWebInfoNames().isEmpty() ||
          itype != IntructionType.UI && itype != IntructionType.WEB) {

        itypefield.addItem(itype.getValue());
      }
    }
    setActionListenerToITypeField(itypefield);
    l.setLabelFor(itypefield);
    p.add(itypefield);
    p.add(new JLabel());
    p.add(new JLabel());

    makeCompactGrid(p, rows+1, 4, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  public JPanel createControlsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Control");
    p.setBorder(border);

    p.add(new JLabel());
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

  //
  private void proposeBCIOption(JPanel p) {
    setupBciContent(GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getJavaContext());

    p.add(jcheck);
    p.add(jcombo);
    p.add(new JLabel());
    p.add(new JLabel());
  }

  //
  private JPanel createInfoFrom(InstructionDialog insDial) {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Instruction Details"));
    jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
    jp.add(Box.createHorizontalStrut(100));
    String str = insDial.getRefName() + " -> ";
    if (insDial.isOk()) {
      str += insDial.getInstructionContent();
    }
    else {
      str += insDial.getInstruction().getOldStatement();
    }
    if (str.length() > 32) {
      str = str.substring(0, 32) + "...";
    }
    jp.add(new JLabel(str));
    return jp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    setVisible(false); 
  }

  public String getTransitionText() {
    return namefield.getText();
  }

  public void edit(String t, String d) {
    namefield.setText(t);
    descriptionfield.setText(d);
    defineContent();
    setVisible(true);
  }

  //
  public void setActionListenerToITypeField(JComboBox<String> itypefield) {
    Frame parent = (Frame) getOwner();
    itypefield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        IntructionType t = IntructionType.getName((String)itypefield.getSelectedItem());
        InstructionDialog insDial = null;
        if (t == null) {
          return;
        }
        GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
        switch (t) {
          case ASSIGN:
            insDial = new AssignDialog(parent, caller);
            break;
          case PRINT:
            insDial = new PrintDialog(parent, caller);
            break;
          case REFLECT:
            insDial = new ReflectDialog(parent);
            break;
          case STOP:
            insDial = new ExitDialog(parent);
            break;
          case UI:
            insDial = new GuiActionDialog(parent, sc.getUiList(), caller);
            break;
          case JAVA:
            insDial = new JavaCallDialog(parent, caller);
            break;
          case WEB:
            insDial = new WEBcallDialog(parent, sc.getWebInfoNames(), caller);
            break;
          default:
            return;
        }
        if (insDial.isOk()) {
          instructionDialogs.add(insDial);
          if (insDial.setupInstruction()) {
            caller.addToInstructions(insDial.getInstruction());
          }
          refresh();
        }
      }
    });
  }

  /**
   * @return the instructions
   */
  public List<ClappInstruction> getInstructions() {
    if (instructions == null) {
      instructions = new ArrayList<>();
    }
    else {
      instructions.clear();
    }
    for (InstructionDialog id : instructionDialogs) {
      instructions.add(id.getInstruction());
    }
    return instructions;
  }

  /**
   * @param instructions the instructions to set
   */
  public void setInstructions(List<ClappInstruction> instructions) {
    this.instructions = instructions;
    if (instructions != null) {
      instructionDialogs = new ArrayList<>();
      for (ClappInstruction inst : instructions) {
        String tp = inst.getInstructionType();
        if (tp != null) {
          IntructionType t = IntructionType.valueOf(tp);
          InstructionDialog insDial = null;
          if (t != null) {
            GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
            switch (t) {
              case ASSIGN:
                insDial = new AssignDialog((Frame) getOwner(), caller, inst);
                break;
              case PRINT:
                insDial = new PrintDialog((Frame) getOwner(), caller, inst);
                break;
              case REFLECT:
                insDial = new ReflectDialog((Frame) getOwner(), inst);
                break;
              case STOP:
                insDial = new ExitDialog((Frame) getOwner(), inst);
                break;
              case UI:
                insDial = new GuiActionDialog((Frame) getOwner(), sc.getUiList(), caller, inst);
                break;
              case JAVA:
                insDial = new JavaCallDialog((Frame) getOwner(), caller, (ClappJavaInstruction) inst);
                break;
              case WEB:
                insDial = new WEBcallDialog((Frame) getOwner(), inst.getSendInfoList(), sc.getWebInfoNames(), inst, caller);
                break;
              default:
                return;
            }
            instructionDialogs.add(insDial);
            insDial.setInstruction(inst);
          }
        }
      }
    }
  }

  /**
   * @return the namefield
   */
  public JTextField getNamefield() {
    return namefield;
  }

  /**
   * @return the okButton
   */
  public JButton getOkButton() {
    return okButton;
  }

  public boolean isOk() {
    return gal.isOk();
  }

  /**
   * @return the descriptionfield
   */
  public JTextField getDescriptionfield() {
    return descriptionfield;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return descriptionfield.getText();
  }

  /**
   * @return the instructionDialogs
   */
  public List<InstructionDialog> getInstructionDialogs() {
    return instructionDialogs;
  }

  /**
   * @return the caller
   */
  public synchronized ActionShape getCaller() {
    return caller;
  }
}
