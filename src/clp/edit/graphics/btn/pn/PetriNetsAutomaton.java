package clp.edit.graphics.btn.pn;

import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.JToggleButton;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AControlButton;
import clp.edit.graphics.btn.AControlButton.ShapeButton;
import clp.edit.graphics.btn.IAutomaton;
import clp.edit.graphics.dial.PNTransitionDialog.TransitionPosition;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.pn.FinalNodeShape;
import clp.edit.graphics.shapes.pn.InvisibleNode;
import clp.edit.graphics.shapes.pn.PNBindingShape;
import clp.edit.graphics.shapes.pn.PetriNetsContainer;
import clp.edit.graphics.shapes.pn.PetriNetsShape;
import clp.edit.graphics.shapes.pn.PlaceNodeShape;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;

public class PetriNetsAutomaton implements IAutomaton {

  private static final long serialVersionUID = 1954659657784952245L;

  private IState state;
  private EnablingState currentStateName;
  private ActionMode processingAction;
  private AShape modeShape;

  private PetriNetsContainer petriNetsContainer;
  private PetriNetsButtonsPanel petriNetsButtonsPanel;

  private boolean isInitialState;

  private enum EnablingState {
    NONE, PLACE, TRANSITION;
  }

  /**
   * constructor
   * 
   * @param container 
   * @param petriNetsButtonsPanel 
   */
 public PetriNetsAutomaton(PetriNetsContainer container, PetriNetsButtonsPanel buttonsPanel) {
   petriNetsContainer = container;
   petriNetsButtonsPanel = buttonsPanel;
   state = new SetupState().performAction(null, null);
   isInitialState = true;
  }

  @Override
  public void performAction(AControlButton currentButton, AShape shape) {
    AControlButton currentSelection = checkSelectedButton(currentButton);
    if (processingAction == ActionMode.SELECT) {
      if (currentSelection == null) {
        performAction(processingAction, null, shape, false);
      }
      else {
        isInitialState = false;
      }
      processingAction = ActionMode.NONE;
    }
    if (isInitialState ||
        (currentSelection != null || processingAction != ActionMode.NONE) && petriNetsContainer.isReadyToAddShape()) {
      state = state.performAction(currentSelection, shape);
    }
    else {
      petriNetsContainer.unselectAll();
      petriNetsContainer.getContainerShape().refresh();
      this.processingAction = ActionMode.NONE;
    }
  }

  /**
   * define next state according to given button clicked
   * 
   * @param mode 
   * @param delta 
   * @param shape 
   * @param isMultiSelect
   */
  @Override
  public void performAction(ActionMode mode, Point delta, AShape shape, boolean isMultiSelect) {
    checkSelectedButton(null);
    this.processingAction = mode;
    switch (mode) {
      case SELECT:
        if (isMultiSelect) {
          petriNetsContainer.multiselect(shape);
        }
        else if (shape != null) {
          if (petriNetsContainer.getSelected() != shape) {
            petriNetsContainer.select(shape);
            if (shape.getChild() == null) {
              petriNetsContainer.getRedShape().setAttachTo(shape);
              petriNetsContainer.getRedShape().setReady(true);
            }
            if (shape instanceof PlaceNodeShape) {
              currentStateName = EnablingState.PLACE;
            }
            else {
              currentStateName = EnablingState.TRANSITION;
            }
          }
          else {
            petriNetsContainer.getRedShape().setReady(false);
            petriNetsContainer.getRedShape().setAttachTo(null);
            petriNetsContainer.unselectAll();
            petriNetsContainer.getContainerShape().refresh();
            currentStateName = EnablingState.NONE;
            processingAction = ActionMode.NONE;
          }
          updateEnabling();
        }
        else {
          petriNetsContainer.unselectAll();
          petriNetsContainer.getContainerShape().refresh();
          processingAction = ActionMode.NONE;
        }
        break;
      case MOVE:
        petriNetsContainer.moveSelection(delta);
        processingAction = ActionMode.SELECT;
        break;
      case INIT:
        processingAction = ActionMode.NONE;
        state = new SetupState().performAction(null, null);
        isInitialState = true;
        break;

      default:
        processingAction = ActionMode.NONE;
        break;
    }
  }

  /**
   * @return the mode
   */
  public ActionMode getMode() {
    return processingAction;
  }

  /**
   * @param mode the mode to set
   */
  public void setMode(ActionMode mode) {
    this.processingAction = mode;
  }

  //
  private AControlButton checkSelectedButton(AControlButton button) {
    for (Component c : petriNetsButtonsPanel.getComponents()) {
      if (c instanceof AControlButton) {
        AControlButton ac = (AControlButton) c;
        if (button == null && ac.isSelected()) {
          button = ac;
        }
        else if (ac != button) {
          ac.setSelected(false);
        }
      }
    }
    return button;
  }

  /**
   * refresh buttons state
   */
  public void refreshButtons() {
    updateEnabling();
  }

  @Override
  public void updateEnabling() {
    switch (currentStateName) {
      case NONE:
        // set initial buttons state
        for (Component c : petriNetsButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            ac.setInitialEnabling();
            ac.setSelected(false);
          }
          else if (c instanceof JToggleButton) {
            c.setEnabled(true);
          }
        }
        break;
      case PLACE:
        for (Component c : petriNetsButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            ac.setSelected(false);
            switch (ac.getShapeButton()) {
              case TRANSITION:
              case FINAL:
                ac.setEnabled(true);
                break;
              default:
                ac.setEnabled(false);
                break;
            }
          }
          else if (c instanceof JToggleButton) {
            c.setEnabled(true);
          }
        }
        break;
      case TRANSITION:
        for (Component c : petriNetsButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            switch (ac.getShapeButton()) {
              case PLACE:
                ac.setEnabled(true);
                break;
              default:
                ac.setEnabled(false);
                break;
            }
          }
          else if (c instanceof JToggleButton) {
            c.setEnabled(true);
          }
        }
        break;

      default:
        break;
    }
  }


  private interface IState {
    public IState performAction(AControlButton currentButton, AShape currentShape);
  }

  /**
   * SETUP state
   */
  public class SetupState implements IState, Serializable {

    private static final long serialVersionUID = -5234286798541267092L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      currentStateName = EnablingState.NONE;
      processingAction = ActionMode.NONE;
      updateEnabling();
      return new WaitingState();
    }
  }

  /**
   * WAITING state
   */
  public class WaitingState implements IState, Serializable {

    private static final long serialVersionUID = -6897186083615246463L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      switch (processingAction) {
        case NONE:
          IState ret = dispatchAction(currentButton);
          if (ret != null) {
            isInitialState = false;
            return ret;
          }
          return this;

        case TRANSITION:
          switch (currentStateName) {
            case PLACE:
              if (isAllowedTransitionNode(currentShape)) {
                bindToTransition(currentShape);
              }
              break;
            case TRANSITION:
              if (isAllowedActionNode(currentShape)) {
                bindToStep(currentShape);
              }
              break;
            default:
              break;
          }
          break;

        default:
          break;
      }
      return this;
    }

    //
    private boolean isAllowedTransitionNode(AShape currentShape) {
      if (modeShape.getParent().getParent() != currentShape) {
        return currentShape instanceof TransitionNodeShape;
      }
      return false;
    }

    //
    private boolean isAllowedActionNode(AShape currentShape) {
      if (modeShape == null) {
        return false;
      }
      ABindingShape b = modeShape.getParent();
      return (b.getBindingType() == BindingType.DOWN || b.getBindingType() == BindingType.DOWN_MIDDLE) &&
              b.getParent() != currentShape &&
              (currentShape instanceof ActionShape);
    }

    //
    private void bindToTransition(AShape currentShape) {
      // TODO Auto-generated method stub
      
    }

    //
    private void bindToStep(AShape currentShape) {
      int deltax = currentShape.getPX() - modeShape.getPX() - modeShape.getWidth()/2 - 10;
      if (deltax >= -30) {
        deltax -= 50;
      }
      modeShape.setXoffset(deltax);
      if (modeShape.getPX() > petriNetsContainer.getLeftmost()) {
        modeShape.addToX(petriNetsContainer.getLeftmost() - modeShape.getPX());
      }
      int deltay = (currentShape.getPY() - modeShape.getParent().getParent().getPY())/2;
      modeShape.addToY(deltay);
      ABindingShape b = modeShape.getParent();
      b.setConditionnallyBindingType(BindingType.UP_LEFT_MIDDLE);
      String t = ((PetriNetsShape)petriNetsContainer.getContainerShape()).getWeightOrColor();
      b = new PNBindingShape(BindingType.UP_LEFT, t);
      b.setXshift(modeShape.getPX()+10);
      modeShape.setChild(b);
      b.setOnlyChild(currentShape);
      ((ActionShape)currentShape).addEntryPoint(b);
      petriNetsContainer.updateLimits(petriNetsContainer.getContainerShape(), modeShape);
      GeneralContext.getInstance().getGraphicsPanel().refreshUI();
    }
  }

  //
  private IState dispatchAction(AControlButton currentButton) {
    if (currentButton != null) {
      ShapeButton button = currentButton.getShapeButton();
      switch (button) {
        case EVENT:
          processingAction = ActionMode.NONE;
          return new EventState().performAction(currentButton, null);
        case PLACE:
          if (isInitialState) {
            processingAction = ActionMode.NONE;
            return new InitialState().performAction(currentButton, null);
          }
          processingAction = ActionMode.NONE;
          return new PlaceState().performAction(currentButton, null);
        case TRANSITION:
          processingAction = ActionMode.NONE;
          return new TransitionState().performAction(currentButton, null);
        case FINAL:
          return new FinalState().performAction(currentButton, null);
        default:
          break;
      }
    }
    return null;
  }

  /**
   * EVENT state
   */
  public class EventState implements IState, Serializable {

    private static final long serialVersionUID = 3482628681625291861L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      InvisibleNode init = new InvisibleNode();
      init.setYoffset(30);
      petriNetsContainer.addShape(init, null);
      PNBindingShape b = new PNBindingShape(BindingType.NONE, null);
      init.setChild(b);
      PetriNetsShape pnShape = (PetriNetsShape)petriNetsContainer.getContainerShape();
      TransitionNodeShape shape = new TransitionNodeShape(pnShape, pnShape.getDefaultTransitionType(), TransitionPosition.INITIAL);
      b.setChild(shape);
      petriNetsContainer.setCurrent(shape);
      petriNetsContainer.getRedShape().setAttachTo(shape);
      GeneralContext.getInstance().getGraphicsPanel().refreshUI();
      currentButton.setSelected(false);
      currentStateName = EnablingState.TRANSITION;
      updateEnabling();
      processingAction = ActionMode.NONE;
      currentButton.setSelected(false);
      return new WaitingState();
    }
  }

  /**
   * INITIAL state
   */
  public class InitialState implements IState, Serializable {

    private static final long serialVersionUID = -949073818278619984L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      InvisibleNode init = new InvisibleNode();
      init.setYoffset(30);
      petriNetsContainer.addShape(init, null);
      PNBindingShape b = new PNBindingShape(BindingType.NONE, null);
      init.setChild(b);
      PlaceNodeShape shape = new PlaceNodeShape(getIncrementingActionNo(), (PetriNetsShape) petriNetsContainer.getContainerShape());
      b.setChild(shape);
      petriNetsContainer.setCurrent(shape);
      petriNetsContainer.getRedShape().setAttachTo(shape);
      GeneralContext.getInstance().getGraphicsPanel().refreshUI();
      currentStateName = EnablingState.PLACE;
      updateEnabling();
      currentButton.setSelected(false);
      return new WaitingState();
    }
  }

  /**
   * PLACE state
   */
  public class PlaceState implements IState, Serializable {

    private static final long serialVersionUID = -3946883109574428632L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      switch (processingAction) {
        case NONE:
          return createSingleStep(currentButton, currentShape);
        case TRANSITION:
          return createActionForTransition((TransitionNodeShape)modeShape, currentButton, currentShape);
        default:
          break;
      }
      return new WaitingState();
    }

    //
    private IState createSingleStep(AControlButton currentButton, AShape currentShape) {
      if (currentButton != null && currentButton.getShapeButton() == ShapeButton.PLACE) {
        PlaceNodeShape shape = new PlaceNodeShape(getIncrementingActionNo(), (PetriNetsShape)petriNetsContainer.getContainerShape());
        petriNetsButtonsPanel.placeCorrespondingShape(shape);
        currentButton.setSelected(false);
        currentStateName = EnablingState.PLACE;
        updateEnabling();
      }
      else {
        if (currentButton != null) {
          return new WaitingState().performAction(currentButton, currentShape);
        }
      }
      return new WaitingState();
    }

    //
    private IState createActionForTransition(TransitionNodeShape dshape, AControlButton currentButton, AShape currentShape) {
      currentStateName = EnablingState.PLACE;
      updateEnabling();
      processingAction = ActionMode.NONE;
      petriNetsContainer.disableRedShape();
      if (currentButton != null) {
        return new WaitingState().performAction(currentButton, currentShape);
      }
      return new WaitingState();
    }
  }

  //
  private int getIncrementingActionNo() {
    return GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForPetriNets();
  }

  /**
   * TRANSITION state
   */
  public class TransitionState implements IState, Serializable {

    private static final long serialVersionUID = 5534613966360300385L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      PetriNetsShape pnShape = (PetriNetsShape)petriNetsContainer.getContainerShape();
      TransitionNodeShape shape = new TransitionNodeShape(pnShape, pnShape.getDefaultTransitionType(), TransitionPosition.MIDDLE);
      petriNetsButtonsPanel.placeCorrespondingShape(shape);
      currentButton.setSelected(false);
      currentStateName = EnablingState.TRANSITION;
      updateEnabling();
      processingAction = ActionMode.NONE;
      return new WaitingState();
    }
  }

  /**
   * FINAL state
   */
  public class FinalState implements IState, Serializable {

    private static final long serialVersionUID = -4101717847552976974L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      FinalNodeShape shape = new FinalNodeShape((PetriNetsShape)petriNetsContainer.getContainerShape());
      petriNetsButtonsPanel.placeCorrespondingShape(shape);
      currentButton.setSelected(false);
      petriNetsContainer.disableRedShape();
      return new WaitingState();
    }
  }

  @Override
  public void enabling(boolean isTrue) {
    if (isTrue) {
      updateEnabling();
      petriNetsContainer.popRedShape();
    }
    else {
      petriNetsButtonsPanel.setButtonsEnabled(false);
      petriNetsContainer.pushRedShape();
    }
  }
}
