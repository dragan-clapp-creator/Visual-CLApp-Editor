package clp.edit.graphics.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.btn.IAutomaton;
import clp.edit.graphics.btn.IAutomaton.ActionMode;
import clp.edit.graphics.code.gui.GuiContext;
import clp.edit.graphics.code.java.JavaContext;
import clp.edit.graphics.code.prt.CslContext;
import clp.edit.graphics.code.prt.CslContext.CslInfo;
import clp.edit.graphics.code.web.WebContext;
import clp.edit.graphics.code.web.WebContext.WebInfo;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AContainerShape;
import clp.edit.graphics.shapes.ADecisionShape;
import clp.edit.graphics.shapes.AEventShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ATransitionShape;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.act.ActigramContainer;
import clp.edit.graphics.shapes.act.ActivityShape;
import clp.edit.graphics.shapes.gc.GrafcetContainer;
import clp.edit.graphics.shapes.gc.GrafcetShape;
import clp.edit.graphics.shapes.menu.ContainerHeaderContextMenu;
import clp.edit.graphics.shapes.pn.PetriNetsContainer;
import clp.edit.graphics.shapes.pn.PetriNetsShape;
import clp.edit.panel.GraphicsPanel;
import clp.edit.panel.TooltipPopupProvider;
import clp.run.msc.Output;

public class GeneralShapesContainer extends JPanel implements MouseListener {

  private static final long serialVersionUID = -8882230841386866337L;

  private List<AContainer> containers;
  private AContainer currentContainer;

  private Point firstPoint;
  private Point lastPoint;

  private GraphicsPanel graphicsPanel;
  private TooltipPopupProvider popupProvider;

  private boolean isEnabled;

  private int actionNoForActigram;
  private int counterForActigramDecisions;
  private int actionNoForGrafcet;
  private int actionNoForPetriNets;

  private int delayNo;
  private int trCount;

  private JavaContext jcontext;
  private GuiContext gcontext;
  private WebContext wcontext;
  private CslContext ccontext;

  /**
   * Constructor
   * @param graphicsPanel 
   * @param popupProvider 
   */
  public GeneralShapesContainer(GraphicsPanel graphicsPanel, TooltipPopupProvider popupProvider) {
    setLayout(new BorderLayout());
    containers = new ArrayList<>();
    this.graphicsPanel = graphicsPanel;
    this.popupProvider = popupProvider;
    isEnabled = true;
    jcontext = new JavaContext();
    gcontext = new GuiContext();
    wcontext = new WebContext();
    ccontext = new CslContext();
    createListeners();
  }

  public void createListeners() {
    for (MouseListener ml : getMouseListeners()) {
      removeMouseListener(ml);
    }
    for (MouseMotionListener mml : getMouseMotionListeners()) {
      removeMouseMotionListener(mml);
    }
    addMouseListener(this);
    addMouseMotionListener(new MouseMotion());
  }

  public void paint(Graphics g) {
    super.paint(g);
    int offset = 0;
    int height = 0;
    for (AContainer container : containers) {
      Dimension dim = container.paint(g, offset, currentContainer == container);
      offset += dim.width;
      if (height < dim.height) {
        height = dim.height;
      }
    }
    Dimension psize = getPreferredSize();
    if (psize.width < offset) {
      psize.width = offset+10;
    }
    if (psize.height < height) {
      psize.height = height+60;
    }
    setPreferredSize(psize);
    revalidate();
  }

  /**
   * create activity shapes container and, within it, the corresponding CLApp Actor, evtl. wrapped by
   * the right scenario. 
   */
  public void addActivityContainer() {
    currentContainer = new ActigramContainer(graphicsPanel);
    containers.add(currentContainer);
    ActivityShape as = new ActivityShape(400, 600, "Activity ", containers.size());
    currentContainer.setContainerShape(as);
    GeneralContext.getInstance().getClappEditor().enableExport();
  }

  /**
   * create grafcet shapes container and, within it, the corresponding CLApp Actor, evtl. wrapped by
   * the right scenario. 
   */
  public void addGrafcetContainer() {
    currentContainer = new GrafcetContainer(graphicsPanel);
    containers.add(currentContainer);
    GrafcetShape as = new GrafcetShape(400, 600, "Grafcet ", containers.size());
    currentContainer.setContainerShape(as);
    GeneralContext.getInstance().getClappEditor().enableExport();
  }

  /**
   * create Petri Nets shapes container and, within it, the corresponding CLApp Actor, evtl. wrapped by
   * the right scenario. 
   */
  public void addPNContainer(String text, boolean isColored) {
    currentContainer = new PetriNetsContainer(graphicsPanel, isColored);
    containers.add(currentContainer);
    PetriNetsShape as = new PetriNetsShape(400, 600, text, containers.size(), isColored);
    currentContainer.setContainerShape(as);
    GeneralContext.getInstance().getClappEditor().enableExport();
  }

  public void addShape(AShape shape) {
    currentContainer.addShape(shape);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (currentContainer == null || !isEnabled) {
      return;
    }
    int px = e.getX();
    int py = e.getY();
    switch (e.getClickCount()) {
      case 1:
        if (SwingUtilities.isRightMouseButton(e)) {
          if (isInContainerHeader(px, py)) {
            ContainerHeaderContextMenu contextMenu = new ContainerHeaderContextMenu(this, currentContainer);
            contextMenu.show(currentContainer.getButtonsPanel(), px+10, py);
          }
          // context menu (swap - delete - insert)
          AShape shape = currentContainer.getContainerShape().getSelectedShape(px, py, currentContainer.getOffset());
          if (shape != null) {
            currentContainer.showContextMenu(this, shape, e);
          }
        }
        else {
          // change container / add shape / select shape(s)
          handleAction(px, py, e.isMetaDown() || e.isControlDown());
        }
        break;
      case 2:
        // edit container
        popupProvider.hide();
        if (isInContainerHeader(px, py)) {
          editContainer();
        }
        else {
          // find and edit shape
          checkForShapeEdition(px, py);
        }
        break;
      default:
        break;
    }
  }

  //
  private void handleAction(int px, int py, boolean isMultiSelect) {
    AContainer csel = getSelectedContainer(px, py);
    if (csel != null) {  // change selected container
      if (csel.getType() != currentContainer.getType()) {
        GeneralContext.getInstance().getGraphicsPanel().getButtonsContainer().updateRadioButtons(csel.getType());
      }
      currentContainer = csel;
      refreshUI();
    }
    else {  // still in same container
      AShape sel = currentContainer.getContainerShape().getSelectedShape(px, py, currentContainer.getOffset());
      currentContainer.handleAction(sel, px, py, isMultiSelect);
    }
  }

  //
  private void refreshUI() {
    currentContainer.getAutomaton().refreshButtons();
    revalidate();
    updateUI();
  }

  //
  private AContainer getSelectedContainer(int px, int py) {
    for (AContainer container : containers) {
      if (container != currentContainer) {
        if (container.isSelected(px, py)) {
          return container;
        }
      }
    }
    return null;
  }


  //
  private boolean isInContainerHeader(int px, int py) {
    if (currentContainer != null) {
      int x = px - currentContainer.getOffset();
      AContainerShape shapesContainer = currentContainer.getContainerShape();
      int xref = shapesContainer.getPX();
      int yref = shapesContainer.getPY();
      return x > xref &&  x < xref+shapesContainer.getWidth()
         && py > yref && py < yref+40;
    }
    return false;
  }

  //
  private void checkForShapeEdition(int px, int py) {
    AShape sel = currentContainer.getContainerShape().getSelectedShape(px, py, currentContainer.getOffset());
    edit(sel);
    currentContainer.setDirty(true);
  }

  //
  private boolean isEnabled(AShape sel) {
    if (sel instanceof ADecisionShape) {
      return ((ADecisionShape) sel).isComplete();
    }
    if (sel instanceof clp.edit.graphics.shapes.gc.FinalNodeShape ||
        sel instanceof clp.edit.graphics.shapes.pn.FinalNodeShape) {
      return true;
    }
    if (sel instanceof ATransitionShape || sel instanceof AEventShape) {
      return sel.getChild() != null;
    }
    return true;
  }

  public void edit(AShape shape) {
    if (shape != null && shape.getDialog() != null && isEnabled(shape)) {
      shape.upateDialog();
      ADialog dialog = (ADialog) shape.getDialog();
      dialog.edit(shape.getName(), shape.getDesc());
      if (dialog.isOk()) {
        shape.setDesc(dialog.getDescription());
        if (shape instanceof ActionShape) {
          ((ActionShape)shape).setupInstructions();
        }
        else {
          shape.setName(dialog.getTransitionText());
          shape.cacheFromTransients();
        }
        currentContainer.setDirty(true);
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (currentContainer != null) {
      IAutomaton auto = currentContainer.getAutomaton();
      if (auto.getMode() == ActionMode.MOVE) {
        auto.setMode(ActionMode.NONE);
        currentContainer.unselectAll();
        refresh();
      }
      firstPoint = null;
      lastPoint = null;
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  /**
   * @return the currentContainer
   */
  public AContainer getCurrentContainer() {
    return currentContainer;
  }

  /**
   * @param currentContainer the currentContainer to set
   */
  public void setCurrentContainer(AContainer currentContainer) {
    this.currentContainer = currentContainer;
    currentContainer.getAutomaton().updateEnabling();
  }

  public void refresh() {
    SwingUtilities.invokeLater( new Runnable() { 
      public void run() {
        updateUI();
      } 
    } );
  }

  class MouseMotion extends MouseMotionAdapter {

    @Override
    public void mouseMoved(MouseEvent e) {
      if (currentContainer != null) {
        AShape sel = currentContainer.getContainerShape().getSelectedShape(e.getX(), e.getY(), currentContainer.getOffset());
        if (sel != null) {
          popupProvider.show(sel, e.getLocationOnScreen());
        }
        else {
          popupProvider.hide();
        }
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (firstPoint == null) {
        firstPoint = e.getPoint();
      }
      else {
        lastPoint = e.getPoint();
        Point delta = new Point(lastPoint.x-firstPoint.x, lastPoint.y-firstPoint.y);
        switch (currentContainer.getAutomaton().getMode()) {
          case SELECT:
          case TRANSITION:
            if (currentContainer.getSelected() instanceof ADecisionShape) {
              if (e.isShiftDown() && delta.y > 15 && (delta.x >= 0 && delta.x < 5 ||delta.x < 0 && delta.x > -5)) {
                if (currentContainer.checkForBindingUpdate((int) lastPoint.getX())) {
                  firstPoint = null;
                  lastPoint = null;
                  refresh();
                  break;
                }
              }
            }
            if (!e.isShiftDown() && (delta.x > 5 || delta.y > 5 || delta.x < -5 || delta.y < -5)) {
              currentContainer.getAutomaton().setMode(ActionMode.MOVE);
            }
            break;
          case MOVE:
             if (delta.x > 5 || delta.y > 5 || delta.x < -5 || delta.y < -5) {
               currentContainer.getAutomaton().performAction(ActionMode.MOVE, delta, null, false);
               firstPoint = lastPoint;
               refresh();
             }
             break;
          default:
            break;
        }
      }
    }
  }

  /**
   * @return the containers
   */
  public List<AContainer> getContainers() {
    return containers;
  }

  /**
   * @param containers the containers to set
   */
  public void setContainers(List<AContainer> containers) {
    this.containers = containers;
  }

  public boolean isFirst() {
    return currentContainer == containers.get(0);
  }

  public boolean isLast() {
    return currentContainer == containers.get(containers.size()-1);
  }

  public void moveLeft() {
    int index = containers.indexOf(currentContainer);
    containers.remove(index);
    containers.add(index-1, currentContainer);
    validate();
    repaint();
  }

  public void moveRight() {
    int index = containers.indexOf(currentContainer);
    containers.remove(index);
    containers.add(index+1, currentContainer);
    validate();
    repaint();
  }

  public void editContainer() {
    AContainerShape shapesContainer = currentContainer.getContainerShape();
    ADialog dialog = (ADialog) shapesContainer.getDialog();
    dialog.edit(shapesContainer.getName(), shapesContainer.getDesc());
    shapesContainer.setName(dialog.getTransitionText());
    shapesContainer.setDesc(dialog.getDescription());
  }

  /**
   * remove current shapes container and, within it, the corresponding CLApp Actor, evtl. wrapped by
   * the right scenario. 
   */
  public void removeContainer() {
    if (currentContainer.getCurrent() != null) {
      ConfirmationDialog dial = new ConfirmationDialog(GeneralContext.getInstance().getFrame());
      if (dial.isOk()) {
        doRemove();
      }
    }
    else {
      doRemove();
    }
  }

  //
  private void doRemove() {
    currentContainer.getContainerShape().removeActor();
    containers.remove(currentContainer);
    if (containers.isEmpty()) {
      currentContainer = null;
    }
    else {
      currentContainer = containers.get(0);
      GeneralContext.getInstance().getGraphicsPanel().getButtonsContainer().updateRadioButtons(currentContainer.getType());
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().repaint();
      }
    });
  }

  /**
   * @param isEnabled the isEnabled to set
   */
  public void setEnabled(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public void removeDeactivationForInit(String name) {
    for (AContainer container : containers) {
      if (container.removeDeactivationForInit(name)) {
        return;
      }
    }
  }

  /**
   * @return the incremented actionNoForActigram
   */
  public int getIncrementingActionNoForActigram() {
    return ++actionNoForActigram;
  }

  /**
   * @return the actionNoForActigram
   */
  public int getActionNoForActigram() {
    return actionNoForActigram;
  }

  /**
   * @return the incremented actionNoForGrafcet
   */
  public int getIncrementingActionNoForGrafcet() {
    return ++actionNoForGrafcet;
  }

  /**
   * @return the actionNoForGrafcet
   */
  public int getActionNoForGrafcet() {
    return actionNoForGrafcet;
  }

  /**
   * @return the incremented actionNoForPetriNets
   */
  public int getIncrementingActionNoForPetriNets() {
    return ++actionNoForPetriNets;
  }

  /**
   * @return the actionNoForActigram
   */
  public int getActionNoForPetriNets() {
    return actionNoForPetriNets;
  }

  /**
   * @return the counterForActigramDecisions
   */
  public synchronized int getIncrentingCounterForActigramDecisions() {
    return counterForActigramDecisions++;
  }

  /**
   * @return the delayNo and increment it
   */
  public int getIncrementingDelayNo() {
    return delayNo++;
  }

  public Set<String> getUiList() {
    return gcontext.getUiList();
  }

  public String getVariableTypeForUI(String varName) {
    return gcontext.getVariableType(varName);
  }

  public Collection<WebInfo> getWebInfos() {
    return wcontext.getWebInfos().values();
  }

  public Set<String> getWebInfoNames() {
    return wcontext.getWebList();
  }

  public boolean addCslInfo(String cslName, Output out) {
    ccontext.addCslInfo(cslName, out);
    return true;
  }

  public Collection<CslInfo> getCslInfos() {
    return ccontext.getCslInfos().values();
  }

  public Set<String> getCslInfoNames() {
    return ccontext.getCslInfos().keySet();
  }

  /**
   * @return the trCount
   */
  public int getTrIncrementingCount() {
    return trCount++;
  }

  /**
   * @return the jcontext
   */
  public JavaContext getJavaContext() {
    return jcontext;
  }

  /**
   * @return the gcontext
   */
  public GuiContext getGuiContext() {
    return gcontext;
  }

  /**
   * @return the wcontext
   */
  public WebContext getWebContext() {
    return wcontext;
  }

  /**
   * @return the ccontext
   */
  public CslContext getConsoleContext() {
    return ccontext;
  }
}
