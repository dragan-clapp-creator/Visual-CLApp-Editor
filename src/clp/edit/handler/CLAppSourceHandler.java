package clp.edit.handler;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import clapp.cmp.ClappMain;
import clapp.run.Supervisor;
import clapp.run.util.ResourceUtility;
import clp.edit.GeneralContext;
import clp.edit.graphics.dial.Token;
import clp.edit.graphics.panel.ControlsContainer;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.ActionShape;
import clp.edit.graphics.shapes.ContainerHelper;
import clp.edit.graphics.shapes.pn.PlaceNodeShape;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ResourcesTreeNode;
import clp.edit.tree.node.util.ResourcesContent;
import clp.edit.tree.node.util.ResourcesContent.EventVariable;
import clp.run.cel.Cell;
import clp.run.msc.MetaScenario;
import clp.run.res.BVar;
import clp.run.res.Binpath;
import clp.run.res.CellEvent;
import clp.run.res.CellMarkCheck;
import clp.run.res.CellMarkSet;
import clp.run.res.CellMarks;
import clp.run.res.DVar;
import clp.run.res.Event;
import clp.run.res.EventVisitor;
import clp.run.res.Events;
import clp.run.res.FVar;
import clp.run.res.IVar;
import clp.run.res.Jarpath;
import clp.run.res.LVar;
import clp.run.res.Marks;
import clp.run.res.PVar;
import clp.run.res.Resources;
import clp.run.res.SVar;
import clp.run.res.SimpleBVar;
import clp.run.res.SimpleDVar;
import clp.run.res.SimpleFVar;
import clp.run.res.SimpleIVar;
import clp.run.res.SimpleLVar;
import clp.run.res.SimpleSVar;
import clp.run.res.SimpleTVar;
import clp.run.res.TVar;
import clp.run.res.Unit;
import clp.run.res.UsedJava;
import clp.run.res.UsedLib;
import clp.run.res.VarEvent;
import clp.run.res.VarType;
import clp.run.res.Variable;
import clp.run.res.WebVariable;
import clp.run.res.Weighting;
import clp.run.res.ui.UiVar;
import clp.run.res.weave.WeaveVar;
import clp.run.scn.Scenario;

public class CLAppSourceHandler implements Serializable {

  private static final long serialVersionUID = -2711015713992556693L;

  private Hashtable<String, CellsInfoList> cellInfoList;

  private ResourcesTreeNode rnode;
  private Resources res;

  /**
   * CONSTRUCTOR
   */
  public CLAppSourceHandler() {
    cellInfoList = new Hashtable<>();
  }
 
  public void resetBackgroundColor(String key) {
    cellInfoList.get(key).resetBackgroundColor();
  }

  public void resetBackgroundColorForAll() {
    for (CellsInfoList list : cellInfoList.values()) {
      list.resetBackgroundColor();
    }
  }

  public void reassignCellsInfoList(String oldKey, String newkey) {
    CellsInfoList info = cellInfoList.remove(oldKey);
    if (info != null) {
      cellInfoList.put(newkey, info);
    }
  }

  public void resetAllCellMarks() {
    for (CellsInfoList list : cellInfoList.values()) {
      list.resetAllCellMarks(this);
    }
  }

  public void setBackgroundColor(String key, String name) {
    cellInfoList.get(key).setBackgroundColor(name);
  }

  public void updateCellMarks(String key, String name, Cell cell) {
    cellInfoList.get(key).updateCellMarks(name, cell);
  }

  public void startSimulation(ControlsContainer controlsContainer) {
    ProjectTreeNode rootNode = GeneralContext.getInstance().getClappEditor().getRootNode();
    MetaScenarioTreeNode mnode = rootNode.findMetaScenario();
    if (mnode != null) {
      ClappMain clapp = mnode.getClappMain();
      if (clapp == null) {
        clapp = new ClappMain();
        clapp.setMetaScenario((MetaScenario) mnode.getAssociatedObject());
        mnode.setClappMain(clapp);
      }
      Supervisor.startRunning(controlsContainer, clapp);
    }
  }

  public void cleanMetaScenario() {
    ProjectTreeNode rootNode = GeneralContext.getInstance().getClappEditor().getRootNode();
    MetaScenarioTreeNode mnode = rootNode.findMetaScenario();
    if (mnode != null) {
      MetaScenario msc = (MetaScenario) mnode.getAssociatedObject();
      if (msc != null) {
        for (Scenario scn : msc.getMetaScenarioBody().getScenarios()) {
          scn.getScenarioBody().getActors().clear();
        }
      }
    }
  }

  public void addTokensToRes(Token[] tokens, String cellName) {
    createResourcesIfNull();
    if (res != null) {
      if (tokens != null) {
        declareTokens(tokens, cellName);
      }
    }
  }

  //
  private void declareTokens(Token[] tokens, String name) {
    Marks marks = res.getMarks();
    if (marks == null) {
      marks = new Marks();
      res.setIsMarks(true);
      res.setMarks(marks);
      CellMarkSet ms = new CellMarkSet();
      marks.setCellMarkSet(ms);
      CellMarks cm = new CellMarks();
      ms.setCellMarks(cm);
      cm.setCellName(name);
      fillMarks(cm, tokens);
    }
    else {
      CellMarkSet ms = marks.getCellMarkSet();
      CellMarks cm = ms.getCellMarks();
      if (name.equals(cm.getCellName())) {
        fillMarks(cm, tokens);
      }
      else {
        ArrayList<CellMarks> cms = ms.getCellMarkss();
        boolean found = false;
        for (CellMarks lcm : cms) {
          if (name.equals(lcm.getCellName())) {
            fillMarks(lcm, tokens);
            found = true;
          }
        }
        if (!found) {
          cm = new CellMarks();
          ms.addCellMarks(cm);
          cm.setCellName(name);
          fillMarks(cm, tokens);
        }
      }
    }
  }

  //
  private void fillMarks(CellMarks cm, Token[] tokens) {
    CellMarkCheck cmc = new CellMarkCheck();
    cm.setCellMarkCheck(cmc);
    for (Token t : tokens) {
      Weighting w = new Weighting();
      w.setMark(t.getC());
      w.setWeight(t.getNb());
      cmc.addWeighting(w);
    }
  }

  public void addUIVariableToRes(String name, UiVar uiVar) {
    createResourcesIfNull();
    Variable v = ResourceUtility.getInstance().getVariable(res, name);
    if (v != null) {
      if (v instanceof UiVar) {
        res.removeVariable(v);
        res.addVariable(uiVar);
        rnode.addVariable(uiVar);
      }
      else {
        System.err.printf("Variable %s already exists but is not declared as UI variable\n", name);
      }
    }
    else {
      res.addVariable(uiVar);
      rnode.addVariable(uiVar);
    }
  }

  public void addWeaveVariableToRes(String name, WeaveVar wvar) {
    createResourcesIfNull();
    Variable v = ResourceUtility.getInstance().getVariable(res, name);
    if (v != null) {
      if (v instanceof WeaveVar) {
        res.removeVariable(v);
        res.addVariable(wvar);
        rnode.addVariable(wvar);
      }
      else {
        System.err.printf("Variable %s already exists but is not declared as Weaver variable\n", name);
      }
    }
    else {
      res.addVariable(wvar);
      rnode.addVariable(wvar);
    }
  }

  public void addWebVariableToRes(String name, WebVariable wvar) {
    createResourcesIfNull();
    Variable v = ResourceUtility.getInstance().getVariable(res, name);
    if (v != null) {
      if (v instanceof WebVariable) {
        res.removeVariable(v);
        res.addVariable(wvar);
        rnode.addVariable(wvar);
      }
      else {
        System.err.printf("Variable %s already exists but is not declared as Web variable\n", name);
      }
    }
    else {
      res.addVariable(wvar);
      rnode.addVariable(wvar);
    }
  }

  public Boolean addVariableToRes(String name, VarType varType) {
    Boolean isResult = null;
    createResourcesIfNull();
    if (res != null) {
      if (varType == null) {
        declareVarEvent(name);
      }
      else {
        Variable v = ResourceUtility.getInstance().getVariable(res, name);
        if (v != null) {
          isResult = isRightType(v, varType);
        }
        else {
          switch (varType) {
            case TBOOL:
              createBoolVar(name);
              break;
            case TINT:
              createIntVar(name);
              break;
            case TFLOAT:
              createFloatVar(name);
              break;
            case TLONG:
              createLongVar(name);
              break;
            case TSTRING:
              createStringVar(name);
              break;
            case TDATE:
              createDateVar(name);
              break;
            case TTIME:
              createTimeVar(name);
              break;
            case TREF:
              createRefVar(name);
              break;
            case TWEB:
              GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
              WebVariable wvar = sc.getWebContext().getWebInfo(name).getWebVar();
              res.addVariable(wvar);
              rnode.addVariable(wvar);
              break;

            default:
              break;
          }
        }
      }
    }
    return isResult;
  }

  //
  private void createResourcesIfNull() {
    if (res == null) {
      ProjectTreeNode rootNode = GeneralContext.getInstance().getClappEditor().getRootNode();
      rnode = rootNode.findResources(ContainerHelper.resName);
      if (rnode != null) {
        res = (Resources) rnode.getAssociatedObject();
      }
    }
  }

  public boolean existsVariableInRes(String name) {
    createResourcesIfNull();
    if (res != null) {
      Variable v = ResourceUtility.getInstance().getVariable(res, name);
      return v != null;
    }
    return false;
  }

  //
  private boolean isRightType(Variable v, VarType varType) {
    switch (varType) {
      case TBOOL:
        return v instanceof BVar;
      case TINT:
        return v instanceof IVar;
      case TFLOAT:
        return v instanceof FVar;
      case TLONG:
        return v instanceof LVar;
      case TSTRING:
        return v instanceof SVar;
      case TDATE:
        return v instanceof DVar;
      case TTIME:
        return v instanceof TVar;
      case TWEAVER:
        return v instanceof WeaveVar;

      default:
        break;
    }
    return true;
  }

  public void addLibToRes(String lib, boolean isJar) {
    createResourcesIfNull();
    if (res != null) {
      UsedLib libs = res.getUsedLib();
      if (libs != null) {
        ArrayList<UsedJava> list = new ArrayList<>();
        UsedJava uj = libs.getUsedJava();
        if (uj != null) {
          list.add(uj);
        }
        list.addAll(libs.getUsedJavas());
        boolean found = false;
        for (UsedJava j : list) {
          if (j instanceof Binpath) {
            if (((Binpath)j).getDir().equals(lib)) {
              found = true;
              break;
            }
            if (((Jarpath)j).getJar().equals(lib)) {
              found = true;
              break;
            }
          }
        }
        if (!found) {
          addLib(libs, lib, isJar);
        }
      }
      else {
        libs = new UsedLib();
        res.setUsedLib(libs);
        addLib(libs, lib, isJar);
        res.setIsUsedLib(true);
      }
    }
  }

  //
  private void addLib(UsedLib libs, String lib, boolean isJar) {
    UsedJava uj;
    if (isJar) {
      uj = new Jarpath();
      ((Jarpath)uj).setJar(lib);
    }
    else {
      uj = new Binpath();
      ((Binpath)uj).setDir(lib);
    }
    if (libs.getUsedJava() == null) {
      libs.setUsedJava(uj);
    }
    else {
      libs.addUsedJava(uj);
    }
  }

  //
  private void declareVarEvent(String name) {
    Events events = res.getEvents();
    if (events == null) {
      events = new Events();
      res.setEvents(events);
      res.setIsEvents(true);
      VarEvent event = new VarEvent();
      event.setName(name);
      events.setEvent(event);
    }
    else {
      ArrayList<Event> list = new ArrayList<>();
      list.add(events.getEvent());
      list.addAll(events.getEvents());
      for (Event event : list) {
        if (event instanceof VarEvent) {
          if (((VarEvent) event).getName().equals(name)) {
            return;
          }
        }
      }
      VarEvent event = new VarEvent();
      event.setName(name);
      events.addEvent(event);
    }
  }

  //
  public void addCellEventToRes(String cellName) {
    createResourcesIfNull();
    if (res != null) {
      Events events = res.getEvents();
      if (events == null) {
        events = new Events();
        res.setEvents(events);
        res.setIsEvents(true);
        CellEvent event = new CellEvent();
        event.setCellName(cellName);
        events.setEvent(event);
      }
      else {
        ArrayList<Event> list = new ArrayList<>();
        list.add(events.getEvent());
        list.addAll(events.getEvents());
        for (Event event : list) {
          if (event instanceof CellEvent) {
            if (((CellEvent) event).getCellName().equals(cellName)) {
              return;
            }
          }
        }
        CellEvent event = new CellEvent();
        event.setCellName(cellName);
        events.addEvent(event);
      }
    }
  }

  //
  public void declareDelay(String cellName, String timeIdentifier, int delay, Unit unit, boolean cycle) {
    createResourcesIfNull();
    if (res != null) {
      if (unit == null) {
        unit = findUnit(timeIdentifier);
      }
      Events events = res.getEvents();
      if (events == null) {
        events = new Events();
        res.setEvents(events);
        res.setIsEvents(true);
        CellEvent event = createCellEvent(cellName, timeIdentifier, delay, unit, cycle);
        events.setEvent(event);
      }
      else {
        ArrayList<Event> list = new ArrayList<>();
        list.add(events.getEvent());
        list.addAll(events.getEvents());
        for (int i=0; i< list.size(); i++) {
          Event event = list.get(i);
          if (event instanceof CellEvent) {
            CellEvent cevent = (CellEvent) event;
            if (cevent.getCellName().equals(cellName)) {
              if (!cevent.isTime() || cevent.getTime().equals(timeIdentifier)) {
                updateEvent(cevent, timeIdentifier, delay, unit, cycle);
                if (i == 0) {
                  events.setEvent(cevent);
                }
                else {
                  events.getEvents().set(i-1, cevent);
                }
                return;
              }
            }
          }
        }
        CellEvent event = createCellEvent(cellName, timeIdentifier, delay, unit, cycle);
        events.addEvent(event);
      }
    }
  }

  //
  private Unit findUnit(String timeIdentifier) {
    ResourcesContent rcontent = (ResourcesContent) rnode.getContent();
    for (EventVariable ev : rcontent.getEventVariables()) {
      if (timeIdentifier.equals(ev.getTime())) {
        return ev.getUnit();
      }
    }
    return null;
  }

  //
  private CellEvent createCellEvent(String cellName, String timeIdentifier, int delay, Unit unit, boolean cycle) {
    CellEvent event = new CellEvent();
    event.setCellName(cellName);
    updateEvent(event, timeIdentifier, delay, unit, cycle);
    return event;
  }

  //
  private void updateEvent(CellEvent event, String timeIdentifier, int delay, Unit unit, boolean cycle) {
    event.setIsTime(true);
    event.setTime(timeIdentifier);
    event.setDelay(delay);
    event.setIsDelay(true);
    event.setUnit(unit);
    event.setIsUnit(true);
    event.setCycle(cycle);
    event.setIsCycle(cycle);
  }

  //
  private void createBoolVar(String name) {
    SimpleBVar sbv = new SimpleBVar();
    sbv.setName(name);
    BVar v = new BVar();
    v.setTBOOL(VarType.TBOOL);
    v.setBoolVar(sbv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createIntVar(String name) {
    SimpleIVar siv = new SimpleIVar();
    siv.setName(name);
    IVar v = new IVar();
    v.setTINT(VarType.TINT);
    v.setIntVar(siv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createFloatVar(String name) {
    SimpleFVar sfv = new SimpleFVar();
    sfv.setName(name);
    FVar v = new FVar();
    v.setTFLOAT(VarType.TFLOAT);
    v.setFloatVar(sfv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createLongVar(String name) {
    SimpleLVar slv = new SimpleLVar();
    slv.setName(name);
    LVar v = new LVar();
    v.setTLONG(VarType.TLONG);
    v.setLongVar(slv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createStringVar(String name) {
    SimpleSVar ssv = new SimpleSVar();
    ssv.setName(name);
    SVar v = new SVar();
    v.setTSTRING(VarType.TSTRING);
    v.setStringVar(ssv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createDateVar(String name) {
    SimpleDVar sdv = new SimpleDVar();
    sdv.setName(name);
    DVar v = new DVar();
    v.setTDATE(VarType.TDATE);
    v.setDateVar(sdv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createTimeVar(String name) {
    SimpleTVar stv = new SimpleTVar();
    stv.setName(name);
    TVar v = new TVar();
    v.setTTIME(VarType.TTIME);
    v.setTimeVar(stv);
    res.addVariable(v);
    rnode.addVariable(v);
  }

  //
  private void createRefVar(String name) {
    PVar pvar = new PVar();
    pvar.setTREF(VarType.TREF);
    pvar.setName(name);
    res.addVariable(pvar);
    rnode.addVariable(pvar);
  }


  public void addShapeAsCell(String key, ActionShape actionShape, String cellName) {
    CellsInfoList list = cellInfoList.get(key);
    if (list == null) {
      list = new CellsInfoList();
      cellInfoList.put(key, list);
    }
    list.put(cellName, actionShape);
  }

  public void updateTreeResources() {
    if (rnode != null) {
      rnode.updateContext(res);
    }
  }

  /**
   * @param cellInfoList the cellInfoList to set
   */
  public void setCellInfoList(Hashtable<String, CellsInfoList> cellInfoList) {
    this.cellInfoList = cellInfoList;
  }

  /**
   * @return the cellInfoList
   */
  public Hashtable<String, CellsInfoList> getCellInfoList() {
    return cellInfoList;
  }

  public void removeResources() {
    createResourcesIfNull();
    if (res == null) {
      return;
    }
    if (res.getEvents() != null) {
      res.setEvents(null);
      res.setIsEvents(false);
    }
    if (res.getMarks() != null) {
      res.setMarks(null);
      res.setIsMarks(false);
    }
    if (res.getUsedLib() != null) {
      res.setUsedLib(null);
      res.setIsUsedLib(false);
    }
    if (res.getVariables() != null) {
      res.getVariables().clear();
    }
  }

  //=================================================================

  public class CellsInfoList implements Serializable {
    private static final long serialVersionUID = -6853251245089404975L;
    private Hashtable<String, ActionShape> cellInfo;

    public CellsInfoList() {
      cellInfo = new Hashtable<>();
    }
    public void put(String cellName, ActionShape actionShape) {
      cellInfo.put(cellName, actionShape);
    }
    public void updateCellMarks(String name, Cell cell) {
      ActionShape as = cellInfo.get(name);
      if (as instanceof PlaceNodeShape) {
        PlaceNodeShape shape = (PlaceNodeShape)as;
        shape.setTokens(cell.getWeightings());
      }
    }
    public void setBackgroundColor(String name) {
      ActionShape shape = cellInfo.get(name);
      if (shape != null) {
        shape.setBgcolor(Color.green);
      }
    }
    public void resetAllCellMarks(CLAppSourceHandler clAppSourceHandler) {
      for (ActionShape as : cellInfo.values()) {
        if (as instanceof PlaceNodeShape) {
          PlaceNodeShape shape = (PlaceNodeShape)as;
          shape.resetTokens(clAppSourceHandler);
        }
      }
    }
    public void resetBackgroundColor() {
      for (ActionShape as : cellInfo.values()) {
        as.setBgcolor(null);
      }
    }
  }

  //=================================================================

  class ClpEventVisitor implements EventVisitor {
    private String name;
    private boolean isFound;
    ClpEventVisitor(String n) {
      name = n;
      isFound = false;
    }
    boolean isFound() {
      return isFound;
    }
    @Override
    public void visitVarEvent(VarEvent x) {
    }
    @Override
    public void visitCellEvent(CellEvent x) {
      if (x.isDelay() && x.getTime().equals(name)) {
        isFound = true;
      }
    }
  }
}
