package clp.edit.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import clp.edit.GeneralContext;
import clp.edit.graphics.panel.ButtonsContainer;
import clp.edit.graphics.panel.ControlsContainer;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.panel.SimulationHelper;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.handler.CLAppSourceHandler.CellsInfoList;

public class GraphicsPanel extends JPanel {

  private static final long serialVersionUID = -5627583060254956711L;

  private GeneralShapesContainer shapesContainer;

  transient private ControlsContainer controlsContainer;

  private ButtonsContainer buttonsContainer;


  public GraphicsPanel() {
    setLayout(new BorderLayout());
    setup();
  }

  private void setup() {
    GeneralContext.getInstance().setGraphicsPanel(this);
    TooltipPopupProvider popupProvider = new TooltipPopupProvider();
    shapesContainer = new GeneralShapesContainer(this, popupProvider);
    controlsContainer = new ControlsContainer(this, shapesContainer);
    buttonsContainer = new ButtonsContainer(shapesContainer);
    add(createSplitPane(), BorderLayout.CENTER);
  }

  public void recreateAll() {
    removeAll();
    shapesContainer.createListeners();
    for (AContainer container : shapesContainer.getContainers()) {
      container.createLisener();
    }
    buttonsContainer.recoverGlobalButtons();
    JSplitPane pane = createSplitPane();
    add(pane, BorderLayout.CENTER);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
          pane.setDividerLocation(0.2f);
      }
  });
  }


  //
  private JSplitPane createSplitPane() {
    JScrollPane scroll = new JScrollPane();
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scroll.getViewport().add(shapesContainer);
    scroll.getViewport().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            updateUI();
          }
        });
      }
    });

    JSplitPane rightPart = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonsContainer, scroll);
    return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlsContainer, rightPart);
  }

  /**
   * @return the shapesContainer
   */
  public GeneralShapesContainer getShapesContainer() {
    return shapesContainer;
  }

  /**
   * @return the controlsContainer
   */
  public ControlsContainer getControlsContainer() {
    return controlsContainer;
  }

  /**
   * @param controlsContainer the controlsContainer to set
   */
  public void setControlsContainer(ControlsContainer controlsContainer) {
    this.controlsContainer = controlsContainer;
  }

  /**
   * @return the buttonsContainer
   */
  public ButtonsContainer getButtonsContainer() {
    return buttonsContainer;
  }

  public SimulationHelper getSimulationHelper() {
    return controlsContainer.getSimulationHelper();
  }

  public void setContainers(List<AContainer> containers) {
    if (containers != null) {
      shapesContainer.setContainers(containers);
    }
  }

  public List<AContainer> getContainers() {
    return shapesContainer.getContainers();
  }

  public AContainer getCurrentContainer() {
    return shapesContainer.getCurrentContainer();
  }

  public void setCurrentContainer(AContainer currentContainer) {
    shapesContainer.setCurrentContainer(currentContainer);
  }


  public void refreshUI() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        AContainer cc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getCurrentContainer();
        if (cc != null) {
          int ch = cc.getContainerShape().getHeight();
          Dimension size = GeneralContext.getInstance().getGraphicsPanel().getPreferredSize();
          if (size.height < ch) {
            size.height = ch;
            GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().setPreferredSize(size);
          }
          GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().repaint();
        }
      }
    });
  }

  public void recreateControlsContainerAndSetCellInfoList(Hashtable<String, CellsInfoList> cellInfoList) {
    controlsContainer = new ControlsContainer(this, shapesContainer);
    controlsContainer.getSourceHandler().setCellInfoList(cellInfoList);
  }
}
