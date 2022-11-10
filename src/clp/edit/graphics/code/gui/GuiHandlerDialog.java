package clp.edit.graphics.code.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import clp.edit.GeneralContext;
import clp.edit.graphics.code.gui.elts.GuiDialog;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.panel.SimulationHelper;
import clp.edit.panel.GraphicsPanel;

public class GuiHandlerDialog extends JDialog {

  private static final long serialVersionUID = 5461970856141855767L;

  private GenericActionListener gal;

  private JComboBox<String> combo;

  private List<String> uiList;

  private GuiContext guiContext;

  private JButton addButton;
  private JButton changeButton;
  private JButton cancelButton;
  private JButton okButton;

  private GridBagConstraints c;

  private JButton removeButton;

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param uiList
   * @param gc guiContext
   */
  public GuiHandlerDialog(Frame owner, List<String> uiList, GuiContext gc) {
    super(owner, "Handling GUI variables", true);
    initialize(owner, uiList, gc);
    setup();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
    setVisible(true);
  }

  //
  private void initialize(Frame owner, List<String> uiList, GuiContext gc) {
    this.uiList = uiList;
    this.guiContext = gc;
    this.gal = new GenericActionListener(this);
    changeButton = new JButton("change");
    changeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String ui = (String) combo.getSelectedItem();
        GuiDialog dial = new GuiDialog(GeneralContext.getInstance().getFrame(), guiContext.getUiInfo(ui), GuiHandlerDialog.this);
        if (dial.isOk()) {
          GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
          if (gp != null) {
            SimulationHelper sh = gp.getControlsContainer().getSimulationHelper();
            sh.setDirty(true);
          }
          else {
            GeneralContext.getInstance().setDirty();
          }
          dial.setupInstruction();
          refresh();
        }
     }
    });
    addButton = new JButton("add");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GuiDialog dial = new GuiDialog(GeneralContext.getInstance().getFrame(), GuiHandlerDialog.this);
        if (dial.isOk()) {
          GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
          if (gp != null) {
            SimulationHelper sh = gp.getControlsContainer().getSimulationHelper();
            sh.setDirty(true);
          }
          else {
            GeneralContext.getInstance().setDirty();
          }
          dial.setupInstruction();
          uiList.add(dial.getInstructionName());
          refresh();
        }
      }
    });
    removeButton = new JButton("remove");
    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String ui = (String) combo.getSelectedItem();
        uiList.remove(ui);
        guiContext.remove(ui);
        GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
        if (gp != null) {
          SimulationHelper sh = gp.getControlsContainer().getSimulationHelper();
          sh.setDirty(true);
        }
        else {
          GeneralContext.getInstance().setDirty();
        }
        refresh();
     }
    });
    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);
    okButton = new JButton("ok");
    okButton.addActionListener(gal);
    if (owner != null) {
      Dimension parentSize = owner.getSize(); 
      Point p = owner.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    setPreferredSize(new Dimension(800, 200));
    setLayout(new GridBagLayout());

    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 1;
  }

  //
  private void setup() {
    c.gridy = 0;

    c.gridx = 0;
    combo = new JComboBox<>();
    for (String ui : uiList) {
      combo.addItem(ui);
    }
    getContentPane().add(combo, c);
    if (!uiList.isEmpty()) {
      c.gridx++;
      getContentPane().add(changeButton, c);

      c.gridx++;
      getContentPane().add(removeButton, c);
    }

    c.gridx++;
    getContentPane().add(addButton, c);

    c.gridy = 1;

    c.gridx = 0;
    getContentPane().add(cancelButton, c);
    c.gridx++;
    getContentPane().add(okButton, c);
  }

  private void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        getContentPane().removeAll();
        setup();
        validate();
      }
    });
  }

  public boolean isOk() {
    return gal.isOk();
  }

  /**
   * @return the uiList
   */
  public List<String> getUiList() {
    return uiList;
  }

  public void reloadUiList() {
    uiList = new ArrayList<>(guiContext.getUiList());
  }

  /**
   * @return the guiContext
   */
  public GuiContext getGuiContext() {
    return guiContext;
  }
}
