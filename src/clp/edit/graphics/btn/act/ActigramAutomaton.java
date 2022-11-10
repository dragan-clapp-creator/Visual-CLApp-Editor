package clp.edit.graphics.btn.act;

import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.JToggleButton;

import clp.edit.GeneralContext;
import clp.edit.graphics.btn.AControlButton;
import clp.edit.graphics.btn.AControlButton.ShapeButton;
import clp.edit.graphics.btn.IAutomaton;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.act.ActigramContainer;
import clp.edit.graphics.shapes.act.ActionNodeShape;
import clp.edit.graphics.shapes.act.DecisionNodeShape;
import clp.edit.graphics.shapes.act.EventNodeShape;
import clp.edit.graphics.shapes.act.FinalNodeShape;
import clp.edit.graphics.shapes.act.ForkBindingShape;
import clp.edit.graphics.shapes.act.InitialNodeShape;
import clp.edit.graphics.shapes.act.JoinBindingShape;
import clp.edit.panel.GraphicsPanel;

public class ActigramAutomaton implements IAutomaton {

  private static final long serialVersionUID = -3214524010521723925L;

  private GraphicsPanel graphicsPanel;
  private ActigramContainer actigramContainer;
  private ActigramButtonsPanel actigramButtonsPanel;

  private IState state;

  private enum EnablingState {
    NONE, INIT, DECISION, FORK, JOIN;
  }

  private EnablingState key;
  private ActionMode mode;
  private AShape modeShape;


  /**
   * constructor
   * 
   * @param actigramContainer 
   * @param graphicsPanel 
   */
  public ActigramAutomaton(ActigramContainer actigramContainer, GraphicsPanel graphicsPanel) {
    this.actigramContainer = actigramContainer;
    this.graphicsPanel = graphicsPanel;
    this.actigramButtonsPanel = graphicsPanel.getButtonsContainer().getActigramButtonsPanel();
    state = new SetupState().performAction(null, null);
  }

  /**
   * define next state according to given button clicked
   * 
   * @param currentButton 
   * @param shape 
   */
  @Override
  public void performAction(AControlButton currentButton, AShape shape) {
    if (key == EnablingState.INIT && currentButton != null && !actigramContainer.isReadyToAddShape()) {
      return;
    }
    AControlButton currentSelection = checkSelectedButton(currentButton);
    if (mode == ActionMode.SELECT) {
      performAction(mode, null, shape, false);
      setMode( ActionMode.NONE );
    }
    if ((currentSelection != null || mode != ActionMode.NONE) && actigramContainer.isReadyToAddShape()) {
      state = state.performAction(currentSelection, shape);
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
    setMode( mode );
    switch (mode) {
      case SELECT:
        if (isMultiSelect) {
          actigramContainer.multiselect(shape);
        }
        else if (shape != null) {
          actigramContainer.select(shape);
          actigramContainer.setCurrent(shape);
          if (actigramContainer.getRedShape().isReady() && key == EnablingState.NONE) {
            key = EnablingState.INIT;
            updateEnabling();
          }
        }
        else {
          actigramContainer.unselectAll();
          actigramContainer.getContainerShape().refresh();
          setMode( ActionMode.NONE );
          if (!actigramContainer.getRedShape().isReady()) {
            key = EnablingState.NONE;
            actigramContainer.getRedShape().setAttachTo(null);
            actigramContainer.getRedShape().setReady(true);
            updateEnabling();
          }
        }
        break;
      case MOVE:
        actigramContainer.moveSelection(delta);
        break;

      default:
        setMode( ActionMode.NONE );
        break;
    }
  }

  /**
   * @return the mode
   */
  public ActionMode getMode() {
    return mode;
  }

  /**
   * @param mode the mode to set
   */
  public void setMode(ActionMode m) {
    if (m == ActionMode.NONE && actigramContainer.hasSelection()) {
      this.mode = ActionMode.SELECT;
    }
    else {
      this.mode = m;
    }
  }

  /**
   * refresh buttons state
   */
  public void refreshButtons() {
    updateEnabling();
  }

  /**
   * reset buttons state
   */
  public void resetButtons() {
    state = new SetupState().performAction(null, null);
    actigramContainer.setReadyToAddShape();
  }

  //
  private AControlButton checkSelectedButton(AControlButton button) {
    for (Component c : actigramButtonsPanel.getComponents()) {
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

  @Override
  public void updateEnabling() {
    switch (key) {
      case NONE:
        // set initial buttons state
        for (Component c : actigramButtonsPanel.getComponents()) {
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
      case INIT:
        for (Component c : actigramButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            ac.setSelected(false);
            switch (ac.getShapeButton()) {
              case ACTION:
              case DECISION:
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
      case DECISION:
      case JOIN:
      case FORK:
        for (Component c : actigramButtonsPanel.getComponents()) {
          if (c instanceof AControlButton) {
            AControlButton ac = (AControlButton) c;
            if (ac.getShapeButton().name().equals(key.name())) {
              ac.setSelected(true);
              ac.setEnabled(true);
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

    private static final long serialVersionUID = 2217394213339437262L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      key = EnablingState.NONE;
      setMode( ActionMode.NONE );
      updateEnabling();
      return new WaitingState();
    }
  }

  /**
   * WAITING state
   */
  public class WaitingState implements IState, Serializable {

    private static final long serialVersionUID = -499286851130612902L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      switch (mode) {
        case NONE:
          IState ret = dispatchAction(currentButton);
          if (ret != null) {
            return ret;
          }
          return this;

        case DECISION:
          if (currentButton != null && currentButton.getShapeButton() == ShapeButton.DECISION) {
            return new ActionState().performAction(currentButton, currentShape);
          }
          else {
            setMode( ActionMode.NONE );
          }
          break;

        case FORK:
          break;
        case JOIN:
          break;

        default:
          break;
      }
      return this;
    }
  }

  //
  private IState dispatchAction(AControlButton currentButton) {
    if (currentButton != null) {
      ShapeButton button = currentButton.getShapeButton();
      switch (button) {
        case INIT:
          return new InitialState().performAction(currentButton, null);
        case EVENT:
          return new EventState().performAction(currentButton, null);
        case ACTION:
          setMode( ActionMode.NONE );
          return new ActionState().performAction(currentButton, null);
        case DECISION:
          setMode( ActionMode.DECISION );
          return new DecisionState().performAction(currentButton, null);
        case FORK:
          setMode( ActionMode.FORK );
          return new ForkState().performAction(currentButton, null);
        case JOIN:
          setMode( ActionMode.JOIN );
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

    private static final long serialVersionUID = -3694186865116007588L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      InitialNodeShape shape = new InitialNodeShape();
      actigramButtonsPanel.placeCorrespondingShape(shape);
      key = EnablingState.INIT;
      updateEnabling();
      currentButton.setSelected(false);
      setMode( ActionMode.NONE );
      return new ActionState().performAction(currentButton, null);
    }
  }

  /**
   * EVENT state
   */
  public class EventState implements IState, Serializable {

    private static final long serialVersionUID = 2118351895539880273L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      EventNodeShape shape = new EventNodeShape();
      actigramButtonsPanel.placeCorrespondingShape(shape);
      currentButton.setSelected(false);
      key = EnablingState.INIT;
      updateEnabling();
      currentButton.setSelected(false);
      setMode( ActionMode.NONE );
      return new ActionState().performAction(currentButton, null);
    }
  }

  /**
   * ACTION state
   */
  public class ActionState implements IState, Serializable {

    private static final long serialVersionUID = 6656843099048688599L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      switch (mode) {
        case NONE:
          return createSimpleAction(currentButton, currentShape);
        case DECISION:
          return createActionForDecision((DecisionNodeShape)modeShape, currentButton, currentShape);
        case FORK:
          return createActionForFork(currentButton, currentShape);
        case JOIN:
          return createActionForJoin((JoinBindingShape)modeShape, currentButton, currentShape);
        default:
          break;
      }
      return new WaitingState();
    }

    //
    private IState createSimpleAction(AControlButton currentButton, AShape currentShape) {
      if (currentButton != null && (currentButton.getShapeButton() == ShapeButton.ACTION
                                    || currentButton.getShapeButton() == ShapeButton.INIT
                                    || currentButton.getShapeButton() == ShapeButton.EVENT)) {
        ActionNodeShape shape = new ActionNodeShape(getIncrementingActionNo());
        actigramButtonsPanel.placeCorrespondingShape(shape);
        currentButton.setSelected(false);
      }
      else {
        if (currentButton != null) {
          return new WaitingState().performAction(currentButton, currentShape);
        }
      }
      return new WaitingState();
    }

    //
    private IState createActionForDecision(DecisionNodeShape dshape, AControlButton currentButton, AShape currentShape) {
      if (dshape.isComplete()) {
        key = EnablingState.INIT;
        updateEnabling();
        setMode( ActionMode.NONE );
        actigramContainer.disableRedShape();
        if (currentButton != null) {
          return new WaitingState().performAction(currentButton, currentShape);
        }
      }
      else if (isAllowed(currentShape)) {
        if (currentShape == null) {
          ActionNodeShape shape = new ActionNodeShape(getIncrementingActionNo());
          actigramButtonsPanel.placeCorrespondingShape(shape);
        }
        else if (currentShape instanceof ActionNodeShape) {
          actigramButtonsPanel.bindToCorrespondingShape(currentShape);
        }
        else {
          return this;
        }
        if (dshape.isComplete()) {
          key = EnablingState.INIT;
          updateEnabling();
          if (dshape.getRight() != null) {
            actigramContainer.select(dshape.getRight().getChild());
          }
          else if (dshape.getRightup() != null && dshape.getLeft() != null) {
            actigramContainer.setCurrent(dshape.getLeft().getChild());
            actigramContainer.getRedShape().setAttachTo(dshape.getLeft().getChild());
            actigramContainer.getRedShape().setReady(true);
          }
          else {
            actigramContainer.disableRedShape();
          }
          setMode( ActionMode.NONE );
          currentButton.setSelected(false);
        }
      }
      return new WaitingState();
    }

    //
    private boolean isAllowed(AShape currentShape) {
      if (currentShape != null) {
        if (currentShape instanceof DecisionNodeShape) {
          return false;
        }
        return currentShape.getChild().getChild() != actigramContainer.getCurrent();
      }
      return true;
    }

    //
    private IState createActionForFork(AControlButton currentButton, AShape currentShape) {
      if (currentButton != null) {
        if (currentButton.isSelected()) {
          ActionNodeShape shape = new ActionNodeShape(getIncrementingActionNo());
          actigramButtonsPanel.placeCorrespondingShape(shape);
        }
        else {
          key = EnablingState.INIT;
          updateEnabling();
          setMode( ActionMode.NONE );
          actigramContainer.disableRedShape();
          return new WaitingState();
        }
      }
      return this;
    }

    //
    private IState createActionForJoin(JoinBindingShape jshape, AControlButton currentButton, AShape currentShape) {
      if (currentButton != null) {
        if (currentButton.isSelected()) {
          if (currentShape instanceof ActionNodeShape && currentShape.getChild() == null) {
            actigramButtonsPanel.bindToCorrespondingShape(currentShape);
          }
          else {
            if (jshape.getBindingType() == BindingType.UP_LEFT
             || jshape.getBindingType() == BindingType.UP_RIGHT) {

              EventNodeShape event = jshape.addEventAbove();
              actigramButtonsPanel.bindToCorrespondingShape(event);
            }
            else if (jshape.getParents().size() > 1) {
              ActionNodeShape shape = new ActionNodeShape(getIncrementingActionNo());
              actigramButtonsPanel.placeCorrespondingShape(shape);
              key = EnablingState.INIT;
              updateEnabling();
              actigramContainer.select(shape);
              setMode( ActionMode.NONE );
              currentButton.setSelected(false);
              return new WaitingState();
            }
          }
        }
        else {
          key = EnablingState.INIT;
          updateEnabling();
          setMode( ActionMode.NONE );
          actigramContainer.disableRedShape();
          return new WaitingState();
        }
      }
      return this;
    }
  }

  //
  private int getIncrementingActionNo() {
    return GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getIncrementingActionNoForActigram();
  }

  /**
   * DECISION state
   */
  public class DecisionState implements IState, Serializable {

    private static final long serialVersionUID = 1272024838782016224L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      key = EnablingState.DECISION;
      updateEnabling();
      modeShape = new DecisionNodeShape(graphicsPanel.getShapesContainer());      
      actigramButtonsPanel.placeCorrespondingShape(modeShape);
      return new TemporaryState();
    }
  }

  /**
   * FORK state
   */
  public class ForkState implements IState, Serializable {

    private static final long serialVersionUID = 6798784072413119031L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      key = EnablingState.FORK;
      updateEnabling();
      modeShape = new ForkBindingShape();      
      actigramButtonsPanel.placeCorrespondingShape(modeShape);
      return new TemporaryState();
    }
  }

  /**
   * JOIN state
   */
  public class JoinState implements IState, Serializable {

    private static final long serialVersionUID = -7209390965009505996L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      key = EnablingState.JOIN;
      updateEnabling();
      modeShape = new JoinBindingShape();      
      actigramButtonsPanel.placeCorrespondingShape(modeShape);
      return new TemporaryState();
    }
  }

  /**
   * TEMPORARY state
   */
  public class TemporaryState implements IState, Serializable {

    private static final long serialVersionUID = -5166637785579134238L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      if (currentButton != null) {
        switch (currentButton.getShapeButton()) {
          case DECISION:
          case FORK:
          case JOIN:
            return new ActionState().performAction(currentButton, currentShape);
          default:
            break;
        }
      }
      return this;
    }
  }

  /**
   * FINAL state
   */
  public class FinalState implements IState, Serializable {

    private static final long serialVersionUID = 6207712800884369186L;

    @Override
    public IState performAction(AControlButton currentButton, AShape currentShape) {
      FinalNodeShape shape = new FinalNodeShape();
      actigramButtonsPanel.placeCorrespondingShape(shape);
      currentButton.setSelected(false);
      actigramContainer.disableRedShape();
      return new WaitingState();
    }
  }

  public void checkForJoinType(int px, int py) {
    if (py < modeShape.getPY()) {
      if (px-actigramContainer.getOffset() < modeShape.getPX()) {
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

  @Override
  public void enabling(boolean isTrue) {
    if (isTrue) {
      updateEnabling();
      actigramContainer.popRedShape();
    }
    else {
      actigramButtonsPanel.setButtonsEnabled(false);
      actigramContainer.pushRedShape();
    }
  }
}
