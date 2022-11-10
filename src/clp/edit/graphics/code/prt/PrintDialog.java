package clp.edit.graphics.code.prt;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import clp.edit.GeneralContext;
import clp.edit.graphics.code.AInstructionDialog;
import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.dial.ActionOrStepDialog.IntructionType;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ActionShape;
import clp.run.res.VarType;

public class PrintDialog extends AInstructionDialog {

  private static final long serialVersionUID = 2710056626872542272L;

  private ClappInstruction instruction;

  private ArrayList<PrintElementInfo> infos;
  private JComboBox<String> console;

  private GridBagConstraints c;

  private ActionShape caller;

  /**
   * 
   * @param parent
   * @param caller
   */
  public PrintDialog(Frame parent, ActionShape caller) {
    super(parent, "Adding a print statement", true);
    this.caller = caller;
  }

  /**
   * 
   * @param parent
   * @param caller
   * @param inst
   */
  public PrintDialog(Frame parent, ActionShape caller, ClappInstruction inst) {
    super(parent, "Adding a print statement", false);
    instruction = inst;
    infos = instruction.getPrintInfoList();
    this.caller = caller;
    getContentPane().removeAll();
    fillContent();
  }

  @Override
  public String getRefName() {
    return "Print";
  }

  @Override
  public String getInstructionName() {
    return "print";
  }

  @Override
  public String getInstructionContent() {
    if (instruction == null || instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  @Override
  public void reset() {
    if (instruction != null) {
      instruction.reset();
      console.removeAllItems();
      GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
      console.addItem("");
      for (String n : shapesContainer.getCslInfoNames()) {
        console.addItem(n);
      }
      console.setSelectedItem(instruction.getConsole());
    }
  }

  @Override
  public ClappInstruction getInstruction() {
    return instruction;
  }

  @Override
  public void setInstruction(ClappInstruction inst) {
    instruction = inst;
  }

  @Override
  public boolean setupInstruction() {
    if (!infos.isEmpty()) {
      if (instruction == null) {
        instruction = new ClappInstruction();
      }
      instruction.setColor(Color.lightGray);
      instruction.setInfos(infos);
      instruction.setIntructionType(IntructionType.PRINT.name());

      String str = "print " + infos.get(0).getText();
      for (int i=1; i<infos.size(); i++) {
        PrintElementInfo pei = infos.get(i);
        if (!pei.getText().isBlank()) {
          String text = pei.getText();
          str += ", " + text;
          if (!text.startsWith("\"")) {
            caller.addOutputVariable(text, VarType.TSTRING);
          }
        }
      }
      if (console.getSelectedIndex() > 0) {
        str += " to " + console.getSelectedItem();
        instruction.setConsole( (String) console.getSelectedItem() );
      }
      str += ";";
      instruction.setStatement(str);
      return true;
    }
    return false;
  }

  @Override
  public void fillContent() {
    if (infos == null) {
      infos = new ArrayList<>();
    }
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridx = 1;

    JButton button = new JButton("+");
    getContentPane().add(button, c);
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            getContentPane().removeAll();
            fillContent();
            validate();
          }
        });
     }
    });

    infos.add(new PrintElementInfo(infos.size()));
    for (PrintElementInfo pei : infos) {
      c.gridy = pei.getLine();
      c.gridx = 1;
      getContentPane().add(Box.createVerticalStrut(5), c);
      c.gridx = 2;
      JTextField namefield = pei.getNamefield();
      getContentPane().add(namefield, c);
    }

    c.gridy++;
    c.gridx = 1;
    getContentPane().add(new JLabel("To console:"), c);
    c.gridx = 2;
    console = new JComboBox<>();
    GeneralShapesContainer shapesContainer = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    console.addItem("");
    for (String n : shapesContainer.getCslInfoNames()) {
      console.addItem(n);
    }
    if (instruction != null) {
      console.setSelectedItem(instruction.getConsole());
    }
    getContentPane().add(console, c);

    c.gridy++;
    c.gridwidth = 2;
    c.gridx = 2;
    getContentPane().add(getCancelButton(), c);
    c.gridx = 5;
    getContentPane().add(getOkButton(), c);
  }
}
