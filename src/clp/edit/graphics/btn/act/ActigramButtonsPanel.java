package clp.edit.graphics.btn.act;

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
import clp.edit.graphics.shapes.act.ActigramContainer;

public class ActigramButtonsPanel extends AButtonsPanel {

  private static final long serialVersionUID = -4572398429541151661L;

  private boolean isInitialized;

  private GeneralShapesContainer shapesContainer;

  /**
   * constructor
   * 
   * @param shapesContainer 
   */
  public ActigramButtonsPanel(GeneralShapesContainer shapesContainer) {
    super();
    this.shapesContainer = shapesContainer;
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Activity Diagram Tools");
    setBorder(border);
    if (!isInitialized) {
      setup();
      isInitialized = true;
    }
  }

  //
  private void setup() {
    JToggleButton activityButton = createActivityButton();
    add(activityButton);
    add(Box.createHorizontalStrut(10));

    add(new InitialNodeButton(this, "Initial Node", ShapeButton.INIT));
    add(Box.createHorizontalStrut(5));
    add(new EventNodeButton(this, "Event", ShapeButton.EVENT));
    add(Box.createHorizontalStrut(5));
    add(new ActionNodeButton(this, "Action", ShapeButton.ACTION));
    add(Box.createHorizontalStrut(5));
    add(new DecisionNodeButton(this, "Decision Node", ShapeButton.DECISION));
    add(Box.createHorizontalStrut(5));
    add(new ForkNodeButton(this, "Fork", ShapeButton.FORK));
    add(Box.createHorizontalStrut(5));
    add(new JoinNodeButton(this, "Join", ShapeButton.JOIN));
    add(Box.createHorizontalStrut(5));
    add(new FinalNodeButton(this, "Final Node", ShapeButton.FINAL));
  }

  //
  private JToggleButton createActivityButton() {
    JToggleButton activityButton = new JToggleButton("Activity");
    return activityButton;
  }

  //
  private void addItemListenerTo(JToggleButton activityButton) {
    activityButton.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ev) {
        if (ev.getStateChange() != 1) {
          return;
        }
        shapesContainer.addActivityContainer();

        activityButton.setSelected(false);

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            shapesContainer.repaint();
          }
        });
      }
    });
  }

  /**
   * add newly created shape as child to the currently selected one
   * 
   * @param shape
   */
  public void placeCorrespondingShape(AShape shape) {
    ActigramContainer currentContainer = getCurrentContainer();
    currentContainer.addShape(shape);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  /**
   * bind currently selected shape to the given one
   * 
   * @param destShape
   */
  public void bindToCorrespondingShape(AShape destShape) {
    ActigramContainer currentContainer = getCurrentContainer();
    currentContainer.bindShape(destShape);
    GeneralContext.getInstance().getGraphicsPanel().refreshUI();
  }

  public ActigramContainer getCurrentContainer() {
    return (ActigramContainer) shapesContainer.getCurrentContainer();
  }

  public void createListeners() {
    for (int i=0; i<getComponentCount(); i++) {
      Component comp = getComponent(i);
      if (comp instanceof JToggleButton) {
        if ("Activity".equals(((JToggleButton) comp).getText())) {
          JToggleButton activityButton = (JToggleButton) comp;
          addItemListenerTo(activityButton);
        }
        else if (comp instanceof AnActigramButton) {
          ((AnActigramButton)comp).setupItemLisener(this);
        }
      }
    }
  }
}
