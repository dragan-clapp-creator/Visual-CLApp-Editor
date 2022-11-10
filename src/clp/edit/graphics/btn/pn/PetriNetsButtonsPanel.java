package clp.edit.graphics.btn.pn;

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
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.pn.PetriNetsContainer;

public class PetriNetsButtonsPanel extends AButtonsPanel {

  private static final long serialVersionUID = 7986203556946928808L;

  private boolean isInitialized;

  private boolean isColored;

  private String title;

  /**
   * constructor
   * 
   * @param isColored 
   */
  public PetriNetsButtonsPanel(boolean isColored) {
    super();
    this.isColored = isColored;
    this.title = isColored ? "Colored Petri Nets" : "Weighted Petri Nets";
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        title + " Tools");
    setBorder(border);
    if (!isInitialized) {
      setup();
      isInitialized = true;
    }
  }

  //
  private void setup() {
    JToggleButton pnButton = createPnButton();
    add(pnButton);
    add(Box.createHorizontalStrut(10));

    add(new InitialNodeButton(this, "Initial Node", ShapeButton.EVENT));
    add(Box.createHorizontalStrut(5));
    add(new PlaceNodeButton(this, "Place", ShapeButton.PLACE));
    add(Box.createHorizontalStrut(5));
    add(new TransitionNodeButton(this, "Transition", ShapeButton.TRANSITION));
    add(Box.createHorizontalStrut(5));
    add(new FinalNodeButton(this, "Final Node", ShapeButton.FINAL));
  }

  //
  private JToggleButton createPnButton() {
    JToggleButton pnButton = new JToggleButton(title);
    addItemListenerTo(pnButton);
    return pnButton;
  }

  //
  private void addItemListenerTo(JToggleButton pnButton) {
    pnButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        if (ev.getStateChange() != 1) {
          return;
        }
        GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().addPNContainer(title, isColored);
        pnButton.setSelected(false);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().validate();
            GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().repaint();
          }
        });
      }
    });
  }

  public void handleToggleState(ItemEvent ev, ShapeButton a) {
    
  }

  @Override
  public void placeCorrespondingShape(AShape shape) {
    PetriNetsContainer currentContainer = getCurrentContainer();
    currentContainer.addShape(shape);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  @Override
  public void bindToCorrespondingShape(AShape shape) {
    PetriNetsContainer currentContainer = getCurrentContainer();
    currentContainer.bindShape(shape);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  public PetriNetsContainer getCurrentContainer() {
    return (PetriNetsContainer) GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getCurrentContainer();
  }

  public void createListeners() {
    for (int i=0; i<getComponentCount(); i++) {
      Component comp = getComponent(i);
      if (comp instanceof JToggleButton) {
        if (((JToggleButton) comp).getText().endsWith("Petri Nets")) {
          JToggleButton pnButton = (JToggleButton) comp;
          addItemListenerTo(pnButton);
        }
        else if (comp instanceof APetriNetsButton) {
          ((APetriNetsButton)comp).setupItemLisener(this);
        }
      }
    }
  }
}
