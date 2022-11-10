package clp.edit.graphics.code.prt;

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
import clp.edit.graphics.dial.GenericActionListener;
import clp.edit.graphics.panel.SimulationHelper;
import clp.edit.panel.GraphicsPanel;

public class ConsHandlerDialog extends JDialog {

  private static final long serialVersionUID = -1866923096226788245L;

  private Hashtable<String, CSLDialog> dialogs;

  private GenericActionListener gal;

  private JComboBox<String> combo;

  private List<String> cslList;

  private CslContext cslContext;

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
   * @param webList
   * @param cc cslContext
   */
  public ConsHandlerDialog(Frame owner, List<String> webList, CslContext cc) {
    super(owner, "Handling Console Variables", true);
    initialize(owner, webList, cc);
    setup();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
    setVisible(true);
  }

  //
  private void initialize(Frame owner, List<String> list, CslContext cc) {
    this.dialogs = new Hashtable<>();
    this.cslList = list;
    this.cslContext = cc;
    this.gal = new GenericActionListener(this);
    changeButton = new JButton("change");
    changeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String csl = (String) combo.getSelectedItem();
        CSLDialog dial = dialogs.get(csl);
        if (dial == null) {
          dial = new CSLDialog(GeneralContext.getInstance().getFrame(), cslContext.getCslInfo(csl));
        }
        dial.jumpToCreation();
        if (dial.isOk()) {
          GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
          if (gp != null) {
            SimulationHelper sh = gp.getControlsContainer().getSimulationHelper();
            sh.setDirty(true);
          }
          else {
            GeneralContext.getInstance().setDirty();
          }
          if (dial.setupInstruction()) {
            cslContext.addCslInfo(dial.getInstructionName(), dial.getOutvar());
          }
          refresh();
        }
     }
    });
    removeButton = new JButton("remove");
    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String csl = (String) combo.getSelectedItem();
        cslContext.remove(csl);
        cslList.remove(csl);
        refresh();
     }
    });
    addButton = new JButton("add");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CSLDialog dial = new CSLDialog(GeneralContext.getInstance().getFrame());
        if (dial.isOk() && dial.setupInstruction()) {
          cslList.add(dial.getInstructionName());
          dialogs.put(dial.getInstructionName(), dial);
          GraphicsPanel gp = GeneralContext.getInstance().getGraphicsPanel();
          if (gp != null) {
            SimulationHelper sh = gp.getControlsContainer().getSimulationHelper();
            sh.setDirty(true);
          }
          else {
            GeneralContext.getInstance().setDirty();
          }
          if (dial.setupInstruction()) {
            cslContext.addCslInfo(dial.getInstructionName(), dial.getOutvar());
          }
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
    for (String bci : cslList) {
      combo.addItem(bci);
    }
    getContentPane().add(combo, c);
    if (!cslList.isEmpty()) {
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
   * @return the webList
   */
  public List<String> getConsolesList() {
    return cslList;
  }
}
