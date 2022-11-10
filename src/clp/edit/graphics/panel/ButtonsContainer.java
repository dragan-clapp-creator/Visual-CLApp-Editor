package clp.edit.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.act.ActigramButtonsPanel;
import clp.edit.graphics.btn.gc.GrafcetButtonsPanel;
import clp.edit.graphics.btn.pn.PetriNetsButtonsPanel;
import clp.edit.graphics.code.gui.GuiHandlerDialog;
import clp.edit.graphics.code.java.bci.JavaBCIHandlerDialog;
import clp.edit.graphics.code.prt.ConsHandlerDialog;
import clp.edit.graphics.code.web.WebHandlerDialog;

public class ButtonsContainer extends JPanel {

  private static final long serialVersionUID = 5137435211589749278L;

  public static enum ButtonType {
    ACT, SFC, WPN, CPN;
  }

  private JRadioButton agm;
  private JRadioButton gfc;
  private JRadioButton cpn;
  private JRadioButton wpn;

  transient private ItemListener radioListener;
  private JRadioButton selection;

  private ButtonGroup group;

  private ActigramButtonsPanel actigramButtonsPanel;
  private GrafcetButtonsPanel grafcetButtonsPanel;
  private PetriNetsButtonsPanel cpnButtonsPanel;
  private PetriNetsButtonsPanel wpnButtonsPanel;

  private GeneralShapesContainer shapesContainer;

  private ArrayList<String> uiList;
  private ArrayList<String> bciList;
  private ArrayList<String> webList;
  private ArrayList<String> cslList;

  transient private JButton uiButton;
  transient private JButton bciButton;
  transient private JButton webButton;
  transient private JButton cslButton;

  /**
   * CONSTRUCTOR
   * 
   * @param shapesContainer
   */
  public ButtonsContainer(GeneralShapesContainer shapesContainer) {
    setLayout(new BorderLayout());
    this.shapesContainer = shapesContainer;
    uiList = new ArrayList<>();
    bciList = new ArrayList<>();
    webList = new ArrayList<>();
    cslList = new ArrayList<>();
    setup();
  }

  //
  private void setup() {
    Rectangle rect = shapesContainer.getBounds();
    setPreferredSize(new Dimension(rect.width, 120));
    setSize(new Dimension(rect.width, 120));
    setupRadioListener();

    add(createFlowChartButtons(), BorderLayout.WEST);
 
    add(createGlobalButtons(), BorderLayout.EAST);

    addButtonsFromSelected();
    createListeners();
  }

  //
  private JPanel createFlowChartButtons() {
    group = new ButtonGroup();
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        "Choose Flow Chart Type");
    jp.setBorder(border);

    agm = new JRadioButton("Activity Diagram", true);
    jp.add(agm);
    agm.setName(ButtonType.ACT.name());
    selection = agm;
    group.add(agm);

    gfc = new JRadioButton("Grafcet (SFC)");
    jp.add(gfc);
    gfc.setName(ButtonType.SFC.name());
    group.add(gfc);

    wpn = new JRadioButton("Weighted Petri Nets");
    jp.add(wpn);
    wpn.setName(ButtonType.WPN.name());
    group.add(wpn);

    cpn = new JRadioButton("Colored Petri Nets");
    jp.add(cpn);
    cpn.setName(ButtonType.CPN.name());
    group.add(cpn);
    return jp;
  }

  //
  private JPanel createGlobalButtons() {
    JPanel jp = new JPanel();
    jp.setLayout(new FlowLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Global Items");
    jp.setBorder(border);
    uiButton = new JButton("GUI");
    uiButton.setToolTipText("Define GUI");
    jp.add(uiButton);
    bciButton = new JButton("BCI");
    bciButton.setToolTipText("Define Byte-Code Injection");
    jp.add(bciButton);
    webButton = new JButton("WEB");
    webButton.setToolTipText("Define Web Sender");
    jp.add(webButton);
    cslButton = new JButton("CSL");
    cslButton.setToolTipText("<html>Define Output Console<br><b>will only be active on exported project</b></html>");
    jp.add(cslButton);
    return jp;
  }

  //
  private void createListeners() {
    setupRadioListener();
    agm.addItemListener(radioListener);
    gfc.addItemListener(radioListener);
    wpn.addItemListener(radioListener);
    cpn.addItemListener(radioListener);
    if (actigramButtonsPanel != null ) {
      actigramButtonsPanel.createListeners();
    }
    if (grafcetButtonsPanel != null) {
      grafcetButtonsPanel.createListeners();
    }
    if (cpnButtonsPanel != null) {
      cpnButtonsPanel.createListeners();
    }
    if (wpnButtonsPanel != null) {
      wpnButtonsPanel.createListeners();
    }
    uiButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GuiHandlerDialog dial = new GuiHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(uiList), shapesContainer.getGuiContext());
        if (dial.isOk()) {
          uiList = (ArrayList<String>) dial.getUiList();
        }
      }
    });
    bciButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JavaBCIHandlerDialog dial = new JavaBCIHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(bciList), shapesContainer.getJavaContext());
        if (dial.isOk()) {
          bciList = (ArrayList<String>) dial.getBciList();
        }
      }
    });
    webButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        WebHandlerDialog dial = new WebHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(webList), shapesContainer.getWebContext());
        if (dial.isOk()) {
          webList = (ArrayList<String>) dial.getWebList();
        }
      }
    });
    cslButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConsHandlerDialog dial = new ConsHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(cslList), shapesContainer.getConsoleContext());
        if (dial.isOk()) {
          cslList = (ArrayList<String>) dial.getConsolesList();
        }
      }
    });
  }

  public void recoverGlobalButtons() {
    JPanel jp = (JPanel) getComponent(1);
    uiButton = (JButton) jp.getComponent(0);
    bciButton = (JButton) jp.getComponent(1);
    webButton = (JButton) jp.getComponent(2);
    cslButton = (JButton) jp.getComponent(3);
    createListeners();
  }

  //
  private void setupRadioListener() {
    radioListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          selection = rb;
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              addButtonsFromSelected();
              revalidate();
              updateUI();
            }
          });
        }
      }
    };
  }

  //
  private void addButtonsFromSelected() {
    if (getComponentCount() > 2) {
      remove(2);
    }
    if (selection == agm) {
      if (actigramButtonsPanel == null ) {
        actigramButtonsPanel = new ActigramButtonsPanel(shapesContainer);
      }
      add(actigramButtonsPanel, BorderLayout.SOUTH);
    }
    else if (selection == gfc) {
      if (grafcetButtonsPanel == null) {
        grafcetButtonsPanel = new GrafcetButtonsPanel(shapesContainer);
      }
      add(grafcetButtonsPanel, BorderLayout.SOUTH);
    }
    else if (selection == cpn) {
      if (cpnButtonsPanel == null) {
        cpnButtonsPanel = new PetriNetsButtonsPanel(true);
      }
      add(cpnButtonsPanel, BorderLayout.SOUTH);
    }
    else if (selection == wpn) {
      if (wpnButtonsPanel == null) {
        wpnButtonsPanel = new PetriNetsButtonsPanel(false);
      }
      add(wpnButtonsPanel, BorderLayout.SOUTH);
    }
  }

  public void updateRadioButtons(ButtonType type) {
    for (Enumeration<AbstractButton> btns = group.getElements(); btns.hasMoreElements();) {
      AbstractButton btn = btns.nextElement();
      ButtonType btype = ButtonType.valueOf( btn.getName() );
      if (btype == type) {
        selection.setSelected(false);
        selection = (JRadioButton) btn;
        selection.setSelected(true);
        break;
      }
    }
  }

  public ActigramButtonsPanel getActigramButtonsPanel() {
    return actigramButtonsPanel;
  }

  /**
   * @return the grafcetButtonsPanel
   */
  public GrafcetButtonsPanel getGrafcetButtonsPanel() {
    return grafcetButtonsPanel;
  }

  /**
   * @param isColored 
   * @return the pnButtonsPanel
   */
  public PetriNetsButtonsPanel getPNButtonsPanel(boolean isColored) {
    if (isColored) {
      return cpnButtonsPanel;
    }
    return wpnButtonsPanel;
  }

  public void setButtonsEnabled(boolean b) {
    agm.setEnabled(b);
    gfc.setEnabled(b);
    cpn.setEnabled(b);
    wpn.setEnabled(b);
  }
}
