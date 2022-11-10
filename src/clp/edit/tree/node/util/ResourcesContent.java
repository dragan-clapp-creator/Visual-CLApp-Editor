package clp.edit.tree.node.util;

import java.io.Serializable;
import java.util.ArrayList;

import clapp.run.ui.util.ConsoleProvider;
import clapp.run.vis.ClpGetVariableVisitor;
import clp.edit.GeneralContext;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.run.msc.ClassReference;
import clp.run.res.Binpath;
import clp.run.res.CellEvent;
import clp.run.res.CellMarks;
import clp.run.res.Event;
import clp.run.res.EventVisitor;
import clp.run.res.Events;
import clp.run.res.Jarpath;
import clp.run.res.Marks;
import clp.run.res.Unit;
import clp.run.res.UsedJava;
import clp.run.res.UsedJavaVisitor;
import clp.run.res.UsedLib;
import clp.run.res.VarEvent;
import clp.run.res.VarType;
import clp.run.res.Variable;
import clp.run.res.Weighting;
import clp.run.res.weave.CstOrVar;
import clp.run.res.weave.MethodEnhancement;

public class ResourcesContent implements IContent, Serializable {

  private static final long serialVersionUID = 1326703834573104133L;

  public enum LibType {
    BIN, JAR;
  }
  private ArrayList<CommonVariable> simpleVars;
  private ArrayList<EventVariable> eventVars;
  private ArrayList<MarkVariable> markVars;
  private ArrayList<UiVariable> uiVars;
  private ArrayList<WebVariable> webVars;
  private ArrayList<WeaveVariable> weaveVars;
  private ArrayList<GraphVariable> graphVars;

  private ArrayList<String> jars;
  private ArrayList<String> bins;
  private UsedLib usedLib;

  private boolean isAssigned;

  public ResourcesContent(boolean b) {
    eventVars = new ArrayList<>();
    markVars = new ArrayList<>();
    simpleVars = new ArrayList<>();
    uiVars = new ArrayList<>();
    webVars = new ArrayList<>();
    weaveVars = new ArrayList<>();
    graphVars = new ArrayList<>();

    jars = new ArrayList<>();
    bins = new ArrayList<>();

    isAssigned = b;
  }

  /**
   * @param v variable to add
   */
  public void addVariable(Variable v) {
    ClpGetVariableVisitor vis = new ClpGetVariableVisitor();
    v.accept(vis);
    switch (vis.getVarType()) {
      case TUI:
        UiVariable uv = new UiVariable();
        uv.setName(vis.getName());
        CstOrVar cov = (CstOrVar) vis.getInitial();
        if (cov != null) {
          uv.setTitle(cov.getCst());
        }
        uiVars.add(uv);
        break;
      case TGRAPH:
        GraphVariable gv = new GraphVariable();
        gv.setName(vis.getName());
        gv.setSentences(vis.getSentences());
        graphVars.add(gv);
        break;
      case TWEAVER:
        WeaveVariable bv = new WeaveVariable();
        bv.setName(vis.getName());
        bv.setPackCst(vis.getPack().getCst());
        bv.setPack(vis.getPack().getId());
        bv.setClazzCst(vis.getClazz().getCst());
        bv.setClazz(vis.getClazz().getId());
        bv.setItems(vis.getItems());
        weaveVars.add(bv);
        break;
      case TWEB:
        WebVariable wv = new WebVariable();
        wv.setPort(vis.getPort());
        wv.setName(vis.getName());
        if (vis.getEncryption() != null) {
          ClassReference cr = vis.getEncryption().getClazz();
          if (cr != null) {
            String address = cr.getPack() + "." + cr.getClazz();
            wv.setAddress(address);
          }
          if (vis.getEncryption() != null) {
            cr = vis.getEncryption().getClazz();
            String encryption = cr.getPack() + "." + cr.getClazz();
            wv.setEncryption(encryption);
          }
        }
        webVars.add(wv);
        break;

      default:
        CommonVariable cv = new CommonVariable();
        cv.setName(vis.getName());
        cv.setType(vis.getVarType());
        cv.setInitial(vis.getInitial());
        cv.setArray(vis.hasArray());
        simpleVars.add(cv);
        break;
    }
  }

  public void setUsedLibraries(UsedLib usedLib) {
    this.usedLib = usedLib;
    if (usedLib != null) {
      extractLibraries();
    }
  }

  public void setEvents(Events events) {
    if (events != null) {
      Event ev = events.getEvent();
      ArrayList<Event> list = new ArrayList<>();
      if (ev != null) {
        list.add(ev);
      }
      list.addAll(events.getEvents());
      for (Event e : list) {
        EventVariable var = new EventVariable();
        e.accept(new EventVisitor() {
          @Override
          public void visitCellEvent(CellEvent x) {
            var.setType("CELL_EVENT");
            var.setName(x.getCellName());
            if (x.getTime() != null) {
              var.setTime(x.getTime());
              var.setDelay(x.getDelay());
              var.setUnit(x.getUnit());
              var.setCyclic(x.getCycle());
            }
          }
          @Override
          public void visitVarEvent(VarEvent x) {
            var.setType("EVENT");
            var.setName(x.getName());
          }
        });
        eventVars.add(var);
      }
    }
  }

  public void setMarks(Marks marks) {
    if (marks != null) {
      CellMarks cm = marks.getCellMarkSet().getCellMarks();
      ArrayList<CellMarks> list = new ArrayList<>();
      if (cm != null) {
        list.add(cm);
      }
      list.addAll(marks.getCellMarkSet().getCellMarkss());
      for (CellMarks m : list) {
        ArrayList<Weighting> weights = new ArrayList<>();
        if (m.getCellMarkCheck() != null) {
          Weighting wt = m.getCellMarkCheck().getWeighting();
          if (wt != null) {
            weights.add(wt);
          }
          weights.addAll(m.getCellMarkCheck().getWeightings());
          ArrayList<String> check = new ArrayList<>();
          for (Weighting w : weights) {
            check.add(w.getMark()+":"+w.getWeight());
          }
          if (!check.isEmpty()) {
            MarkVariable var = new MarkVariable();
            var.setName(m.getCellName());
            String str = check.toString();
            var.setMarks(str.substring(1, str.length()-1));
            markVars.add(var);
          }
        }
      }
    }
  }

  public void addLibrary(String path, LibType type) {
    if (usedLib == null) {
      usedLib = new UsedLib();
      if (type == LibType.BIN) {
        Binpath x = new Binpath();
        x.setDir(path);
        usedLib.setUsedJava(x);
      }
      else {
        Jarpath x = new Jarpath();
        x.setJar(path);
        usedLib.setUsedJava(x);
      }
    }
    else {
      if (type == LibType.BIN) {
        Binpath x = new Binpath();
        x.setDir(path);
        usedLib.addUsedJava(x);
      }
      else {
        Jarpath x = new Jarpath();
        x.setJar(path);
        usedLib.addUsedJava(x);
      }
    }
    extractLibraries();
  }

  //
  private void extractLibraries() {
    ArrayList<UsedJava> libraries = new ArrayList<>();
    libraries.add(usedLib.getUsedJava());
    libraries.addAll(usedLib.getUsedJavas());
    jars.clear();
    bins.clear();
    for (UsedJava uj : libraries) {
      uj.accept(new UsedJavaVisitor() {
        @Override
        public void visitJarpath(Jarpath x) {
          jars.add(x.getJar());
        }
        @Override
        public void visitBinpath(Binpath x) {
          bins.add(x.getDir());
        }
      });
    }
  }

  /**
   * @return the uiVars
   */
  public ArrayList<UiVariable> getUIVariables() {
    return uiVars;
  }

  /**
   * @return the webVars
   */
  public ArrayList<WebVariable> getWebVariables() {
    return webVars;
  }

  /**
   * @return the weaveVars
   */
  public ArrayList<WeaveVariable> getWeaveVariables() {
    return weaveVars;
  }

  /**
   * @return the graphVars
   */
  public ArrayList<GraphVariable> getGraphVariables() {
    return graphVars;
  }

  /**
   * @return the simpleVars
   */
  public ArrayList<CommonVariable> getSimpleVariables() {
    return simpleVars;
  }

  public ArrayList<EventVariable> getEventVariables() {
    return eventVars;
  }

  public ArrayList<MarkVariable> getMarkVariables() {
    return markVars;
  }

  /**
   * @return the jars
   */
  public ArrayList<String> getJars() {
    return jars;
  }

  /**
   * @return the bins
   */
  public ArrayList<String> getBins() {
    return bins;
  }

  public void removeLibrary(int index) {
    if (index == 0) {
      if (usedLib.getUsedJavas().isEmpty()) {
        usedLib = null;
      }
      else {
        UsedJava uj = usedLib.getUsedJavas().remove(0);
        usedLib.setUsedJava(uj);
      }
    }
    else {
      usedLib.getUsedJavas().remove(index-1);
    }
    if (index < bins.size()) {
      bins.remove(index);
    }
    else {
      jars.remove(index-bins.size());
    }
  }

  public int getLibIndexOf(String str) {
    int i = -1;
    for (String s : bins) {
      i++;
      if (s.equals(str)) {
        return i;
      }
    }
    for (String s : jars) {
      i++;
      if (s.equals(str)) {
        return i;
      }
    }
    return i;
  }

  public void removeVariable(int index) {
    if (index > 0) {
      simpleVars.remove(index-1);
    }
  }

  public void removeEvent(int index) {
    if (index > 0) {
      eventVars.remove(index-1);
    }
  }

  public void removeMark(int index) {
    if (index > 0) {
      markVars.remove(index-1);
    }
  }

  /**
   * @return the isAssigned
   */
  public boolean isAssigned() {
    return isAssigned;
  }

  /**
   * @param isAssigned the isAssigned to set
   */
  public void setAssigned(boolean isAssigned) {
    this.isAssigned = isAssigned;
  }

  public void addVariableFrom(String stype, boolean selected, String name, String initial) {
    if (!stype.isBlank() && !name.isBlank()) {
      VarType type = null;
      for (VarType vt : VarType.values()) {
        if (vt.getVal().equals(stype)) {
          type = vt;
          break;
        }
      }
      CommonVariable cv = new CommonVariable();
      cv.setType(type);
      cv.setName(name);
      cv.setArray(selected);
      cv.setInitial(initial);
      simpleVars.add(cv);
    }
  }

  public void addEventFrom(String type, String name, String time, String delay, String sunit, boolean isCyclic) {
    if (!type.isBlank() && !name.isBlank()) {
      EventVariable ev = new EventVariable();
      ev.setType(type);
      ev.setName(name);
      if (!time.isBlank()) {
        ev.setTime(time);
        ev.setDelay(Integer.parseInt(delay));
        ev.setCyclic(isCyclic);
        Unit unit = null;
        for (Unit u : Unit.values()) {
          if (u.getVal().equals(sunit)) {
            unit = u;
            break;
          }
        }
        ev.setUnit(unit);
      }
      eventVars.add(ev);
    }
  }

  public void addMarksFrom(String name, String marks) {
    if (!name.isBlank() && !marks.isBlank() && checkOk(marks)) {
      MarkVariable mv = new MarkVariable();
      mv.setName(name);
      mv.setMarks(marks);
      markVars.add(mv);
    }
  }

  //
  private boolean checkOk(String marks) {
    String[] sp0 = marks.split(",");
    for (String s0 : sp0) {
      String[] sp1 = s0.split(":");
      if (sp1.length != 2) {
        ConsoleProvider.getInstance().eprint("incorrect marks setting: " + marks);
        return false;
      }
      if (sp1[0].length() != 1) {
        ConsoleProvider.getInstance().eprint("incorrect marks setting: " + marks + " at " + sp1[0]);
        return false;
      }
      try {
        Integer.parseInt(sp1[1]);
      }
      catch (NumberFormatException e) {
        ConsoleProvider.getInstance().eprint("incorrect marks setting: " + marks + " at " + sp1[1]);
        return false;
      }
    }
    return true;
  }

  //=======================================================

  public static class WeaveVariable implements Serializable {
    private static final long serialVersionUID = -6642894942308849812L;
    private String packCst;
    private String pack;
    private String clazzCst;
    private String clazz;
    private String name;
    private ArrayList<MethodEnhancement> items;
    /**
     * @return the packCst
     */
    public String getPackCst() {
      return packCst;
    }
    /**
     * @param packCst the packCst to set
     */
    public void setPackCst(String packCst) {
      this.packCst = packCst;
    }
    /**
     * @return the pack
     */
    public String getPack() {
      return pack;
    }
    /**
     * @param pack the pack to set
     */
    public void setPack(String pack) {
      this.pack = pack;
    }
    /**
     * @return the clazzCst
     */
    public String getClazzCst() {
      return clazzCst;
    }
    /**
     * @param clazzCst the clazzCst to set
     */
    public void setClazzCst(String clazzCst) {
      this.clazzCst = clazzCst;
    }
    /**
     * @return the clazz
     */
    public String getClazz() {
      return clazz;
    }
    /**
     * @param clazz the clazz to set
     */
    public void setClazz(String clazz) {
      this.clazz = clazz;
    }
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the items
     */
    public ArrayList<MethodEnhancement> getItems() {
      return items;
    }
    /**
     * @param items the items to set
     */
    public void setItems(ArrayList<MethodEnhancement> items) {
      this.items = items;
    }
    public String getPath() {
      String path;
      if (packCst != null) {
        path = packCst;
      }
      else {
        path = "<" + pack + ">";
      }
      if (clazzCst != null) {
        path += clazzCst;
      }
      else {
        path += "<" + clazz + ">";
      }
      return path;
    }
    public String getSentences() {
      StringBuilder sb = new StringBuilder();
      for (MethodEnhancement m : items) {
        m.getMethodTarget().getCst(); // TODO ...
      }
      return sb.toString();
    }
  }

  //=======================================================

  public static class UiVariable implements Serializable {
    private static final long serialVersionUID = 3487587210441820186L;

    private String name;
    private String title;
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the title
     */
    public String getTitle() {
      return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
      this.title = title;
    }
    public String getSentences() {
      GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
      return sc.getGuiContext().getUiInfo(name).getDeclarationStatement();
    }
  }

  //=======================================================

  public static class GraphVariable implements Serializable {
    private static final long serialVersionUID = 8658416348022224853L;

    private String name;
    private String sentences;
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the sentences
     */
    public String getSentences() {
      return sentences;
    }
    /**
     * @param sentences the sentences to set
     */
    public void setSentences(String sentences) {
      this.sentences = sentences;
    }
  }

  //=======================================================

  public static class EventVariable implements Serializable {
    private static final long serialVersionUID = 445095263056511067L;

    private String type;
    private String name;
    private String time;
    private int delay;
    private Unit unit;
    private boolean cyclic;
    /**
     * @return the type
     */
    public String getType() {
      return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
      this.type = type;
    }
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the time
     */
    public String getTime() {
      return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(String time) {
      this.time = time;
    }
    /**
     * @return the delay
     */
    public int getDelay() {
      return delay;
    }
    /**
     * @param delay the delay to set
     */
    public void setDelay(int delay) {
      this.delay = delay;
    }
    /**
     * @return the unit
     */
    public Unit getUnit() {
      return unit;
    }
    /**
     * @param unit the unit to set
     */
    public void setUnit(Unit unit) {
      this.unit = unit;
    }
    /**
     * @return the cyclic
     */
    public boolean isCyclic() {
      return cyclic;
    }
    /**
     * @param cyclic the cyclic to set
     */
    public void setCyclic(boolean cyclic) {
      this.cyclic = cyclic;
    }
    public String getDeclaration() {
      if ("EVENT".equals(type)) {
        return "VAR " + getName();
      }
      return "CELL " + getName() + getDelayInfo();
    }
    private String getDelayInfo() {
      if (time != null) {
        return " " + time + " " + unit.getVal() + " " + isCyclic();
      }
      return "";
    }
  }

  //=======================================================

  public static class MarkVariable implements Serializable {
    private static final long serialVersionUID = -5857617104104661362L;
    private String name;
    private String marks;
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the marks
     */
    public String getMarks() {
      return marks;
    }
    /**
     * @param marks the marks to set
     */
    public void setMarks(String marks) {
      this.marks = marks;
    }
    public String getDeclaration() {
      return " " + name + " [" + marks + "]";
    }
  }

  //=======================================================

  public static class WebVariable implements Serializable {
    private static final long serialVersionUID = 1905768568514424958L;

    private String name;
    private String encryption;
    private String address;
    private int port;
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the encryption
     */
    public String getEncryption() {
      return encryption;
    }
    /**
     * @param encryption the encryption to set
     */
    public void setEncryption(String encryption) {
      this.encryption = encryption;
    }
    /**
     * @return the address
     */
    public String getAddress() {
      return address;
    }
    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
      this.address = address;
    }
    /**
     * @return the port
     */
    public int getPort() {
      return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
      this.port = port;
    }
    /**
     * @return the declaration
     */
    public String getDeclaration() {
      GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
      return sc.getWebContext().getWebInfo(name).getDeclaration();
    }
  }

  //=======================================================

  public static class CommonVariable implements Serializable {
    private static final long serialVersionUID = -3455251424795432909L;

    private VarType type;
    private boolean isArray;
    private String name;
    private Object initial;
    /**
     * @return the type
     */
    public VarType getType() {
      return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(VarType type) {
      this.type = type;
    }
    /**
     * @return the isArray
     */
    public boolean isArray() {
      return isArray;
    }
    /**
     * @param isArray the isArray to set
     */
    public void setArray(boolean isArray) {
      this.isArray = isArray;
    }
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }
    /**
     * @return the initial
     */
    public Object getInitial() {
      if (initial == null && type != null) {
        switch (type) {
          case TBOOL:
            return false;
          case TINT:
          case TLONG:
          case TFLOAT:
            return 0;
          default:
            return null;
        }
      }
      if (initial instanceof String) {
        return "\"" + initial + "\"";
      }
      return initial;
    }
    /**
     * @param initial the initial to set
     */
    public void setInitial(Object initial) {
      if (initial instanceof String) {
        String str = (String) initial;
        switch (type) {
          case TBOOL:
            this.initial = Boolean.parseBoolean(str);
            break;
          case TINT:
            this.initial = Integer.parseInt(str);
            break;
          case TLONG:
            this.initial = Long.parseLong(str);
            break;
          case TFLOAT:
            this.initial = Double.parseDouble(str);
            break;
          default:
            this.initial = str;
            break;
        }
      }
      else {
        this.initial = initial;
      }
    }
    public String getDeclaration() {
      return getType().getVal() + "   " + getName() + " = " + getInitial();
    }
  }
}
