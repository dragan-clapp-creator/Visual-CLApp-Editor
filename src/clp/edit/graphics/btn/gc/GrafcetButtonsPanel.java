package clp.edit.graphics.btn.gc;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AButtonsPanel;
import clp.edit.graphics.btn.AControlButton.ShapeButton;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.gc.GrafcetContainer;

public class GrafcetButtonsPanel extends AButtonsPanel {

  private static final long serialVersionUID = 7699633215720534192L;

  private boolean isInitialized;

  private GeneralShapesContainer shapesContainer;

  /**
   * constructor
   * @param shapesContainer 
   */
  public GrafcetButtonsPanel(GeneralShapesContainer shapesContainer) {
    super();
    this.shapesContainer = shapesContainer;
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Grafcet Tools");
    setBorder(border);
    if (!isInitialized) {
      setup();
      isInitialized = true;
    }
}

  //
  private void setup() {
    JToggleButton grafcetButton = createGrafcetButton();
    add(grafcetButton);
    add(Box.createHorizontalStrut(10));

    add(new InitialNodeButton(this, "Initial Step", ShapeButton.INIT));
    add(Box.createHorizontalStrut(5));
    add(new EventNodeButton(this, "Event", ShapeButton.EVENT));
    add(Box.createHorizontalStrut(5));
    add(new StepNodeButton(this, "Step", ShapeButton.STEP));
    add(Box.createHorizontalStrut(5));
    add(new TransitionNodeButton(this, "Transition", ShapeButton.TRANSITION));
    add(Box.createHorizontalStrut(5));
    add(new ForkNodeButton(this, "Fork", ShapeButton.FORK));
    add(Box.createHorizontalStrut(5));
    add(new JoinNodeButton(this, "Join", ShapeButton.JOIN));
    add(Box.createHorizontalStrut(5));
    add(new FinalNodeButton(this, "Final Node", ShapeButton.FINAL));
  }

  //
  private JToggleButton createGrafcetButton() {
    JToggleButton grafcetButton = new JToggleButton("Grafcet");
    addItemListenerTo(grafcetButton);
    return grafcetButton;
  }

  //
  private void addItemListenerTo(JToggleButton grafcetButton) {
    grafcetButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        if (ev.getStateChange() != 1) {
          return;
        }
        shapesContainer.addGrafcetContainer();
        grafcetButton.setSelected(false);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            shapesContainer.repaint();
          }
        });
      }
    });
  }

  public void handleToggleState(ItemEvent ev, ShapeButton a) {
    
  }

  @Override
  public void placeCorrespondingShape(AShape shape) {
    GrafcetContainer currentContainer = getCurrentContainer();
    currentContainer.addShape(shape);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  @Override
  public void bindToCorrespondingShape(AShape shape) {
    GrafcetContainer currentContainer = getCurrentContainer();
    currentContainer.bindShape(shape);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  public GrafcetContainer getCurrentContainer() {
    return (GrafcetContainer) shapesContainer.getCurrentContainer();
  }

  public void createListeners() {
    for (int i=0; i<getComponentCount(); i++) {
      Component comp = getComponent(i);
      if (comp instanceof JToggleButton) {
        if ("Grafcet".equals(((JToggleButton) comp).getText())) {
          JToggleButton grafcetButton = (JToggleButton) comp;
          addItemListenerTo(grafcetButton);
        }
        else if (comp instanceof AGrafcetButton) {
          ((AGrafcetButton)comp).setupItemLisener(this);
        }
      }
    }
  }
}
