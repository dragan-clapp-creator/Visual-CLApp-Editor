package clp.edit.graphics.code.java.bci;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import clp.edit.GeneralContext;
import clp.edit.graphics.code.java.JavaContext;
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.panel.SimulationHelper;
import clp.edit.panel.GraphicsPanel;

public class JavaBCIHandlerDialog extends JDialog {

  private static final long serialVersionUID = -156992243794384255L;

  private Hashtable<String, JavaBCIDialog> dialogs;

  private GenericActionListener gal;

  private JComboBox<String> combo;

  private List<String> bciList;

  private JavaContext javaContext;

  private JButton addButton;
  private JButton changeButton;
  private JButton removeButton;
  private JButton cancelButton;
  private JButton okButton;

  private GridBagConstraints c;

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param bciList
   * @param jc javaContext
   */
  public JavaBCIHandlerDialog(Frame owner, List<String> bciList, JavaContext jc) {
    super(owner, "Handling BCI variables", true);
    initialize(owner, bciList, jc);
    setup();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
    setVisible(true);
  }

  //
  private void initialize(Frame owner, List<String> list, JavaContext jc) {
    this.dialogs = new Hashtable<>();
    this.bciList = list;
    this.javaContext = jc;
    this.gal = new GenericActionListener(this);
    changeButton = new JButton("change");
    changeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String bci = (String) combo.getSelectedItem();
        JavaBCIDialog dial = dialogs.get(bci);
        if (dial == null) {
          dial = new JavaBCIDialog(GeneralContext.getInstance().getFrame(), javaContext.getBciInfo(bci), javaContext);
        }
        dial.jumpToInjection();
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
    removeButton = new JButton("remove");
    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
        if (gp != null) {
          SimulationHelper sh = gp.getControlsContainer().getSimulationHelper();
          sh.setDirty(true);
        }
        else {
          GeneralContext.getInstance().setDirty();
        }
        String bci = (String) combo.getSelectedItem();
        bciList.remove(bci);
        refresh();
     }
    });
    addButton = new JButton("add");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JavaBCIDialog dial = new JavaBCIDialog(GeneralContext.getInstance().getFrame(), javaContext);
        dial.showDialog();
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
          bciList.add(dial.getInstructionName());
          dialogs.put(dial.getInstructionName(), dial);
          refresh();
        }
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
    for (String bci : bciList) {
      combo.addItem(bci);
    }
    getContentPane().add(combo, c);
    if (!bciList.isEmpty()) {
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
   * @return the bciList
   */
  public List<String> getBciList() {
    return bciList;
  }
}
