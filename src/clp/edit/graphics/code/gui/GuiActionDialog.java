package clp.edit.graphics.code.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.code.InstructionDialog;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.shapes.ActionShape;

public class GuiActionDialog extends JDialog implements InstructionDialog {

  private static final long serialVersionUID = -5964204057616529763L;

  private GenericActionListener gal;

  private String uiName;

  private JComboBox<String> combo;
  private JCheckBox keepbox;

  private ActionShape caller;

  private ClappInstruction instruction;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param uilist
   * @param c caller 
   */
  public GuiActionDialog(Frame parent, Set<String> uilist, ActionShape c) {
    this(parent, uilist, c, null);
    setVisible(true);
  }

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param uilist
   * @param c caller 
   * @param inst
   */
  public GuiActionDialog(Frame parent, Set<String> uilist, ActionShape c, ClappInstruction inst) {
    super(parent, "Adding a UI call", true);
    instruction = inst;
    setup(parent, uilist, c);
  }

  //
  private void setup(Frame parent, Set<String> uilist, ActionShape c) {
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    Dimension dim = new Dimension(580, 200);
    getContentPane().setPreferredSize(dim);
    keepbox = new JCheckBox("Keep Alive");
    if (instruction != null) {
      keepbox.setSelected(instruction.isIskeepalive());
    }
    caller = c;
    fillContent(uilist);
  }

  //
  private void fillContent(Set<String> uilist) {
    setLayout(new GridBagLayout());
    gal = new GenericActionListener(this);

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 1;
    c.gridy = 0;

    c.gridx = 0;
    combo = new JComboBox<>();
    for (String ui : uilist) {
      combo.addItem(ui);
    }
    combo.setSelectedItem(caller.getUiName());
    getContentPane().add(combo, c);

    c.gridx = 1;
    getContentPane().add(keepbox, c);

    c.gridy = 1;
    c.gridx = 0;
    JButton cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);
    getContentPane().add(cancelButton, c);
    c.gridx++;
    JButton okButton = new JButton("ok");
    okButton.addActionListener(gal);
    getContentPane().add(okButton, c);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  @Override
  public String getRefName() {
    return "UI";
  }

  @Override
  public String getInstructionContent() {
    if (instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  @Override
  public String getInstructionName() {
    return uiName;
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }

  @Override
  public void reset() {
    instruction.reset();
  }

  @Override
  public ClappInstruction getInstruction() {
    return instruction;
  }

  @Override
  public void setInstruction(ClappInstruction inst) {
    setupInstruction();
  }

  @Override
  public boolean setupInstruction() {
    if (instruction == null) {
      instruction = new ClappInstruction();
    }
    instruction.setColor(Color.orange);
    uiName = (String) combo.getSelectedItem();
    if (uiName == null) {
      System.err.println("ERROR: please enter an identifier (UI name)");
      return false;
    }
    if (keepbox.isSelected()) {
      instruction.setIskeepalive(true);
      instruction.setStatement("show keeping "+uiName+";");
    }
    else {
      instruction.setStatement("show "+uiName+";");
    }
    instruction.setIntructionType(IntructionType.UI.name());
    caller.setUiName(uiName);
    return true;
  }
}
