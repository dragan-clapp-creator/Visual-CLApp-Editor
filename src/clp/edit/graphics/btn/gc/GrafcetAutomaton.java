package clp.edit.graphics.btn.gc;

import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.JToggleButton;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AControlButton;
import clp.edit.graphics.btn.AControlButton.ShapeButton;
import clp.edit.graphics.btn.IAutomaton;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.BindingShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.gc.EventNodeShape;
import clp.edit.graphics.shapes.gc.FinalNodeShape;
import clp.edit.graphics.shapes.gc.ForkBindingShape;
import clp.edit.graphics.shapes.gc.GrafcetContainer;
import clp.edit.graphics.shapes.gc.GrafcetShape;
import clp.edit.graphics.shapes.gc.InitialStepNodeShape;
import clp.edit.graphics.shapes.gc.InvisibleNode;
import clp.edit.graphics.shapes.gc.JoinBindingShape;
import clp.edit.graphics.shapes.gc.StepNodeShape;
import clp.edit.graphics.shapes.gc.TransitionNodeShape;

public class GrafcetAutomaton implements IAutomaton {

  private static final long serialVersionUID = 2196397507570578624L;

  private IState state;
  private EnablingState currentStateName;
  private ActionMode processingAction;
  private AShape modeShape;

  private GrafcetContainer grafcetContainer;
  private GrafcetButtonsPanel grafcetButtonsPanel;

  private boolean isInitialState;

  private enum EnablingState {
    NONE, STEP, TRANSITION, FORK, JOIN;
  }

  /**
   * constructor
   * 
   * @param container 
   * @param grafcetButtonsPanel 
   */
 public GrafcetAutomaton(GrafcetContainer container, GrafcetButtonsPanel buttonsPanel) {
   grafcetContainer = container;
   grafcetButtonsPanel = buttonsPanel;
   state = new SetupState().performAction(null, null);
   isInitialState = true;
  }

  @Override
  public void performAction(AControlButton currentButton, AShape shape) {
    AControlButton currentSelection = checkSelectedButton(currentButton);
    if (processingAction == ActionMode.SELECT) {
      performAction(processingAction, null, shape, false);
      processingAction = ActionMode.NONE;
    }
    if (isInitialState ||
        (currentSelection != null || processingAction != ActionMode.NONE)
          && grafcetContainer.isReadyToAddShape()) {
      state = state.performAction(currentSelection, shape);
    }
    else {
      grafcetContainer.unselectAll();
      grafcetContainer.getContainerShape().refresh();
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
          grafcetContainer.multiselect(shape);
        }
        else if (shape != null) {
          if (grafcetContainer.getSelected() != shape) {
            grafcetContainer.select(shape);
            grafcetContainer.setCurrent(shape);
            if (shape.getChild() == null) {
              grafcetContainer.getRedShape().setAttachTo(shape);
              grafcetContainer.getRedShape().setReady(true);
            }
            if (shape instanceof ActionShape) {
              currentStateName = EnablingState.STEP;
            }
            else {
              currentStateName = EnablingState.TRANSITION;
            }
          }
          else {
            grafcetContainer.unselectAll();
            currentStateName = EnablingState.NONE;
            grafcetContainer.getRedShape().setAttachTo(null);
            grafcetContainer.getRedShape().setReady(true);
            processingAction = ActionMode.NONE;
          }
          updateEnabling();
        }
        else {
          grafcetContainer.unselectAll();
          grafcetContainer.getContainerShape().refresh();
          processingAction = ActionMode.NONE;
        }
        break;
      case MOVE:
        grafcetContainer.moveSelection(delta);
        this.processingAction = ActionMode.SELECT;
        break;
      case INIT:
        this.processingAction = ActionMode.NONE;
        state = new SetupState().performAction(null, null);
        isInitialState = true;
        break;

      default:
        processingAction = ActionMode.NONE;
        break;
    }
  }

  /**
   * bind underlying transition to given step shape
   * 
   * @param shape
   */
  public void bindShape(AShape shape) {
    this.processingAction = ActionMode.TRANSITION;
    modeShape = grafcetContainer.getRedShape().getAttach();
    state = state.performAction(null, shape);
    this.processingAction = ActionMode.NONE;
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

  public void checkForJoinType(int px, int py) {
    if (py < modeShape.getPY()) {
      if (px-grafcetContainer.getOffset() < modeShape.getPX()) {
        ((JoinBindingShape)modeShape).setBindingType(BindingType.UP_LEFT);
      }
      else {
        ((JoinBindingShape)modeShape).setBindingType(BindingType.UP_RIGHT);
      }
    }
    else {
      ((JoinBindingShape)modeShape).setBindingType(BindingType.DOWN);
    }
  }

  //
  private AControlButton checkSelectedButton(AControlButton button) {
    for (Component c : grafcetButtonsPanel.getComponents()) {
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
        for (Component c : grafcetButtonsPanel.getComponents()) {
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
      case STEP:
        for (Component c : grafcetButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            ac.setSelected(false);
            switch (ac.getShapeButton()) {
              case EVENT:
                ac.setEnabled(!grafcetContainer.getRedShape().isReady());
                break;
              case TRANSITION:
              case FORK:
              case JOIN:
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
        for (Component c : grafcetButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            switch (ac.getShapeButton()) {
              case EVENT:
                ac.setEnabled(!grafcetContainer.getRedShape().isReady());
                break;
              case STEP:
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
      case JOIN:
      case FORK:
        for (Component c : grafcetButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            if (ac.getShapeButton().name().equals(currentStateName.name())) {
              if (!ac.isSelected()) {
                ac.setSelected(true);
                ac.setEnabled(true);
              }
            }
            else {
              ac.setSelected(false);
              ac.setEnabled(false);
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

    private static final long serialVersionUID = 2361473767087867323L;

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

    private static final long serialVersionUID = -8397712627945211883L;

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
            case STEP:
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
        case FORK:
        case JOIN:
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
      if (modeShape.getPX() > grafcetContainer.getLeftmost()) {
        modeShape.addToX(grafcetContainer.getLeftmost() - modeShape.getPX());
      }
      int deltay = (currentShape.getPY() - modeShape.getParent().getParent().getPY())/2;
      modeShape.addToY(deltay);
      ABindingShape b = modeShape.getParent();
      b.setConditionnallyBindingType(BindingType.UP_LEFT_MIDDLE);
      b = new BindingShape(BindingType.UP_LEFT, "");
      b.setXshift(modeShape.getPX()+10);
      modeShape.setChild(b);
      b.setOnlyChild(currentShape);
      ((ActionShape)currentShape).addEntryPoint(b);
      grafcetContainer.updateLimits(grafcetContainer.getContainerShape(), modeShape);
      GeneralContext.getInstance().getGraphicsPanel().refreshUI();
    }
  }

  //
  private IState dispatchAction(AControlButton currentButton) {
    if (currentButton != null) {
      ShapeButton button = currentButton.getShapeButton();
      switch (button) {
        case INIT:
          processingAction = ActionMode.NONE;
          return new InitialState().performAction(currentButton, null);
        case EVENT:
          processingAction = ActionMode.NONE;
          return new EventState().performAction(currentButton, null);
        case STEP:
          processingAction = ActionMode.NONE;
          return new StepState().performAction(currentButton, null);
        case TRANSITION:
          processingAction = ActionMode.NONE;
          return new TransitionState().performAction(currentButton, null);
        case FORK:
          processingAction = ActionMode.FORK;
          return new ForkState().performAction(currentButton, null);
        case JOIN:
          processingAction = ActionMode.JOIN;
          return new JoinState().performAction(currentButton, null);
        case FINAL:
          return new FinalState().performAction(currentButton, null);
        default:
          break;
      }
    }
    return null;
  }

  /**
   * INITIAL state
   */
  public class InitialState implements IState, Serializable {

    private static final long serialVersionUID = 6379401731390022939L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      InvisibleNode init = new InvisibleNode();
      grafcetContainer.addShape(init, null);
      BindingShape b = new BindingShape(BindingType.NONE, null);
      init.setChild(b);
      InitialStepNodeShape shape = new InitialStepNodeShape(getIncrementingActionNo());
      b.setChild(shape);
      grafcetContainer.setCurrent(shape);
      grafcetContainer.getRedShape().setAttachTo(shape);
      GeneralContext.getInstance().getGraphicsPanel().refreshUI();
      currentStateName = EnablingState.STEP;
      updateEnabling();
      currentButton.setSelected(false);
      return new WaitingState();
    }
  }

  /**
   * EVENT state
   */
  public class EventState implements IState, Serializable {

    private static final long serialVersionUID = 3482628681625291861L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      InvisibleNode init = new InvisibleNode();
      grafcetContainer.addShape(init, null);
      BindingShape b = new BindingShape(BindingType.NONE, null);
      init.setChild(b);
      GrafcetShape grShape = (GrafcetShape)grafcetContainer.getContainerShape();
      EventNodeShape shape = new EventNodeShape(grShape, grShape.getDefaultTransitionType());
      b.setChild(shape);
      grafcetContainer.setCurrent(shape);
      grafcetContainer.getRedShape().setAttachTo(shape);
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
   * STEP state
   */
  public class StepState implements IState, Serializable {

    private static final long serialVersionUID = 5985972552459600011L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      switch (processingAction) {
        case NONE:
          return createSingleStep(currentButton, currentShape);
        case TRANSITION:
          return createActionForTransition((TransitionNodeShape)modeShape, currentButton, currentShape);
        case FORK:
          return createActionForFork((ForkBindingShape)modeShape, currentButton, currentShape);
        case JOIN:
          return createActionForJoin((JoinBindingShape)modeShape, currentButton, currentShape);
        default:
          break;
      }
      return new WaitingState();
    }

    //
    private IState createSingleStep(AControlButton currentButton, AShape currentShape) {
      if (currentButton != null && currentButton.getShapeButton() == ShapeButton.STEP) {
        StepNodeShape shape = new StepNodeShape(getIncrementingActionNo());
        grafcetButtonsPanel.placeCorrespondingShape(shape);
        currentButton.setSelected(false);
        currentStateName = EnablingState.STEP;
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
      currentStateName = EnablingState.STEP;
      updateEnabling();
      processingAction = ActionMode.NONE;
      grafcetContainer.disableRedShape();
      if (currentButton != null) {
        return new WaitingState().performAction(currentButton, currentShape);
      }
      return new WaitingState();
    }

    //
    private IState createActionForFork(ForkBindingShape fshape, AControlButton currentButton, AShape currentShape) {
      if (currentButton != null) {
        if (currentButton.isSelected()) {
          if (isEnabled(currentShape) && fshape.getChild() != null) {
            grafcetButtonsPanel.bindToCorrespondingShape(currentShape);
            grafcetContainer.updateLimits(fshape.getLeftmost()-10, fshape.getRightmost()+10);
          }
          else {
            StepNodeShape shape = new StepNodeShape(getIncrementingActionNo());
            grafcetButtonsPanel.placeCorrespondingShape(shape);
          }
        }
        else {
          currentStateName = EnablingState.STEP;
          updateEnabling();
          processingAction = ActionMode.NONE;
          grafcetContainer.disableRedShape();
          modeShape = null;
          return new WaitingState();
        }
      }
      return this;
    }

    //
    private IState createActionForJoin(JoinBindingShape jshape, AControlButton currentButton, AShape currentShape) {
      if (currentButton != null) {
        if (currentButton.isSelected()) {
          if (isEnabled(currentShape)) {
            grafcetButtonsPanel.bindToCorrespondingShape(currentShape);
          }
          else {
            if (jshape.getBindingType() == BindingType.UP_LEFT
             || jshape.getBindingType() == BindingType.UP_RIGHT) {
              EventNodeShape event = jshape.addEventAbove();
              grafcetButtonsPanel.bindToCorrespondingShape(event);
            }
            else if (jshape.getParents().size() > 1) {
              StepNodeShape shape = new StepNodeShape(getIncrementingActionNo());
              grafcetButtonsPanel.placeCorrespondingShape(shape);
              currentStateName = EnablingState.STEP;
              updateEnabling();
              processingAction = ActionMode.NONE;
              grafcetContainer.select(shape);
              currentButton.setSelected(false);
              modeShape = null;
              return new WaitingState();
            }
          }
        }
        else {
          currentStateName = EnablingState.STEP;
          updateEnabling();
          processingAction = ActionMode.NONE;
          grafcetContainer.disableRedShape();
          return new WaitingState();
        }
      }
      return this;
    }

    //
    private boolean isEnabled(AShape currentShape) {
      if (currentShape instanceof ActionShape) {
        ABindingShape b = currentShape.getChild();
        return b == null || !(b instanceof TriBinding) || !((TriBinding)b).isFull();
      }
      return false;
    }
  }

  //
  private int getIncrementingActionNo() {
    return GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForGrafcet();
  }

  /**
   * FORK state
   */
  public class ForkState implements IState, Serializable {

    private static final long serialVersionUID = 1820618935126643847L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      GrafcetShape grShape = (GrafcetShape)grafcetContainer.getContainerShape();
      modeShape = new ForkBindingShape(grShape, grShape.getDefaultTransitionType());
      grafcetButtonsPanel.placeCorrespondingShape(modeShape);
      currentButton.setSelected(false);
      currentStateName = EnablingState.FORK;
      updateEnabling();
      return new TemporaryState();
    }
  }

  /**
   * JOIN state
   */
  public class JoinState implements IState, Serializable {

    private static final long serialVersionUID = -5014366528322631345L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      modeShape = new JoinBindingShape((GrafcetShape) grafcetContainer.getContainerShape());
      grafcetButtonsPanel.placeCorrespondingShape(modeShape);
      currentButton.setSelected(false);
      currentStateName = EnablingState.JOIN;
      updateEnabling();
      return new TemporaryState();
    }
  }

  /**
   * TEMPORARY state
   */
  public class TemporaryState implements IState, Serializable {

    private static final long serialVersionUID = 788771626341950052L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      if (currentButton != null) {
        switch (currentButton.getShapeButton()) {
          case FORK:
          case JOIN:
            return new StepState().performAction(currentButton, currentShape);
          default:
            break;
        }
      }
      return this;
    }
  }

  /**
   * TRANSITION state
   */
  public class TransitionState implements IState, Serializable {

    private static final long serialVersionUID = -8650736433477568633L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      TransitionNodeShape shape = new TransitionNodeShape((GrafcetShape)grafcetContainer.getContainerShape());
      grafcetButtonsPanel.placeCorrespondingShape(shape);
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

    private static final long serialVersionUID = -2173443481327486963L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      GrafcetShape grShape = (GrafcetShape)grafcetContainer.getContainerShape();
      FinalNodeShape shape = new FinalNodeShape(grShape, grShape.getDefaultTransitionType());
      grafcetButtonsPanel.placeCorrespondingShape(shape);
      currentButton.setSelected(false);
      grafcetContainer.disableRedShape();
      return new WaitingState();
    }
  }

  @Override
  public void enabling(boolean isTrue) {
    if (isTrue) {
      updateEnabling();
      grafcetContainer.popRedShape();
    }
    else {
      grafcetContainer.getButtonsPanel().setButtonsEnabled(false);
      grafcetContainer.pushRedShape();
    }
  }
}
