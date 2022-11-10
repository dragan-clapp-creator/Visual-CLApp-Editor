package clp.edit.graphics.shapes.pn;

import java.util.ArrayList;
import java.util.List;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.PNCTransitionDialog;
import clp.edit.graphics.dial.PNTransitionDialog;
import clp.edit.graphics.dial.PNTransitionDialog.TransitionPosition;
import clp.edit.graphics.dial.TransitionType;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AContainer;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ATransitionShape;
import clp.edit.graphics.shapes.BindingType;
import clp.edit.graphics.shapes.TriBinding;
import clp.edit.graphics.shapes.util.CellInfo;
import clp.run.res.Unit;

public class TransitionNodeShape extends ATransitionShape {

  private static final long serialVersionUID = 1743153968064818872L;

  transient private PNTransitionDialog dialog;
  private PetriNetsShape petriNetsShape;

  private String trName;
  private TransitionPosition trPos;
  private Unit selectedUnit;
  private char selectedArrow;


  /**
   * CONSTRUCTOR
   * 
   * @param pnShape 
   * @param tt: transition type 
   * @param tp: transition position 
   */
  public TransitionNodeShape(PetriNetsShape pnShape, TransitionType tt, TransitionPosition tp) {
    super(tt);
    trName = "Tr"+GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().getTrIncrementingCount();
    petriNetsShape = pnShape;
    this.trPos = tp;
    dialog = pnShape.isColored() ?
        new PNCTransitionDialog(GeneralContext.getInstance().getFrame(), pnShape, this, tt, tp, 340) :
        new PNTransitionDialog(GeneralContext.getInstance().getFrame(), pnShape, this, tt, tp, 320);
    setName(dialog.getTransitionText());
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    String t = petriNetsShape.getWeightOrColor();
    PNBindingShape b = new PNBindingShape(bindingType, t);
    setChild(b);
    b.setChild(shape);
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = petriNetsShape.isColored() ?
          new PNCTransitionDialog(GeneralContext.getInstance().getFrame(), petriNetsShape, this, getTrType(), trPos, 340) :
          new PNTransitionDialog(GeneralContext.getInstance().getFrame(), petriNetsShape, this, getTrType(), trPos, 320);
      dialog.reinitializeHelpers();
      dialog.pnsetup(null);
      if (dialog.getDelayInfo() != null) {
        dialog.getDelayInfo().setUnit(selectedUnit);
      }
      if (dialog.getClassicInfo() != null) {
        dialog.getClassicInfo().setArrow(selectedArrow);
      }
    }
    return dialog;
  }

  public PNTransitionDialog getCustDialog() {
    return (PNTransitionDialog) getDialog();
  }

  @Override
  public void setName(String name) {
    super.setName(name);
    if (getChild() != null) {
      ABindingShape b = getChild();
      if (b instanceof TriBinding) {
        TriBinding t = (TriBinding)b;
        List<ABindingShape> bindings = new ArrayList<>();
        if (t.getLeft() != null) {
          bindings.add(t.getLeft());
        }
        bindings.add(t.getMiddle());
        if (t.getRight() != null) {
          bindings.add(t.getRight());
        }
        bindings.remove(0);
      }
    }
  }

  /**
   * @return the upleft
   */
  public ABindingShape getUpleft() {
    if (getParent() instanceof TriBinding) {
      return ((TriBinding)getParent()).getLeft();
    }
    return null;
  }

  /**
   * @param upleft the upleft to set
   */
  public void setUpleft(ABindingShape upleft) {
    if (getParent() instanceof TriBinding) {
      ((TriBinding)getParent()).setLeft(upleft);
    }
    else {
      TriBinding tri = new TriBinding();
      tri.setMiddle(getParent());
      tri.setLeft(upleft);
      tri.setParent(getParent().getParent());
      setParent(tri);
    }
    getCustDialog().pnsetup(null);
  }

  /**
   * @return the upright
   */
  public ABindingShape getUpright() {
    if (getParent() instanceof TriBinding) {
      return ((TriBinding)getParent()).getRight();
    }
    return null;
  }

  /**
   * @param upright the upright to set
   */
  public void setUpright(ABindingShape upright) {
    if (getParent() instanceof TriBinding) {
      ((TriBinding)getParent()).setRight(upright);
    }
    else {
      TriBinding tri = new TriBinding();
      tri.setMiddle(getParent());
      tri.setRight(upright);
      tri.setParent(getParent().getParent());
      setParent(tri);
    }
    getCustDialog().pnsetup(null);
  }

  @Override
  public boolean generateActiveCode(AContainer container) {
    if (getParent().getParent() instanceof InvisibleNode) {
      if (getChild() != null) {
        String name = getChild().getChild().getName();
        String init_name = "INIT_"+name;
        if (!container.isRegistered(init_name)) {
          CellInfo info = new CellInfo(init_name);
          String code = getDeactivationCondition(name);
          if (code != null) {
            info.setDd("      DD { " + code + "; }\n");
          }
          container.register(init_name, info, true);
        }
        return getChild().generateCode(container);
      }
      return true;
    }
    return super.generateActiveCode(container);
  }

  //
  private String getDeactivationCondition(String name) {
    return getCustDialog().getDeactivationCondition(name, 'P', null);
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    String s = "";
    ABindingShape b = getParent();
    if (b instanceof TriBinding) {
      s = gatherUpTokensFromDialog((TriBinding) b);
    }
    else {
      AShape p = b.getParent();
      String marks = getCustDialog().getCheckMarks();
      if (marks != null) {
        s = getTrName() + " { " + p.getName() + " " + marks + " } ";
        p = bs.getParent().getParent().getParent();
        String c = getCustDialog().getActivationCondition(null, 'P', p);
        if (c != null) {
          s += "AND " + c;
        }
      }
      else {
        s = getCustDialog().getActivationCondition("INIT_"+getChild().getChild().getName(), 'P', getChild().getChild());
      }
    }
    b = getChild();
    String marks;
    if (b instanceof TriBinding) {
      marks = getSetMarks((TriBinding) b, bs);
    }
    else {
      marks = getCustDialog().getSetMarks();
    }
    if (marks != null) {
      s += " > " + marks;
    }
    return s;
  }

  //
  protected String gatherUpTokensFromDialog(TriBinding tb) {
    StringBuilder buffer = new StringBuilder();
    PlaceNodeShape tbp = (PlaceNodeShape) tb.getParent();
    PlaceNodeShape p;
    String sl = null;
    String sm = null;
    String sr = null;
    buffer.append(getTrName() + " { ");
    if (tb.getLeft() != null) {
      p = (PlaceNodeShape) tb.getLeft().getParent();
      if (p == null) {
        p = tbp;
      }
      else {
        sl = getCustDialog().getActivationCondition(null, 'P', p);
      }
      buffer.append(p.getName() + "[ "+getCustDialog().getUplefthelper().getMarks()+" ], ");
    }
    p = tbp;
    sm = getCustDialog().getActivationCondition(null, 'P', p);
    buffer.append(p.getName() + "[ "+getCustDialog().getUphelper().getMarks()+" ]");
    if (tb.getRight() != null) {
      p = (PlaceNodeShape) tb.getRight().getParent();
      if (p == null) {
        p = tbp;
      }
      else {
        sr = getCustDialog().getActivationCondition(null, 'P', p);
      }
      buffer.append(", " + p.getName() + "[ "+getCustDialog().getUprighthelper().getMarks()+" ] ");
    }
    buffer.append(" }");
    return fillBuffer(buffer, sl, sm, sr);
  }

  //
  private String fillBuffer(StringBuilder buffer, String sl, String sm, String sr) {
    String str = null;
    if (sl != null && !sl.isEmpty()) {
      str = sl;
    }
    if (sm != null && !sm.isEmpty()) {
      if (str == null) {
        str = sm;
      }
      else if (!sm.equals(str)) {
        str += " AND " + sm;
      }
    }
    if (sr != null && !sr.isEmpty()) {
      if (str == null) {
        str = sr;
      }
      else if (!sr.equals(str) && !str.contains(" AND "+sr)) {
        str += " AND " + sr;
      }
    }
    if (buffer.length() == 0) {
      buffer.append(str);
    }
    else {
      buffer.append(" AND " + str);
    }
    return buffer.toString();
  }

  //
  private String getSetMarks(TriBinding tb, ABindingShape bs) {
    if (tb.getLeft() == bs) {
      return "[ " + getCustDialog().getDownlefthelper().getMarks() + " ]";
    }
    if (tb.getMiddle() == bs) {
      return "[ " + getCustDialog().getDownhelper().getMarks() + " ]";
    }
    if (tb.getRight() == bs) {
      return "[ " + getCustDialog().getDownrighthelper().getMarks() + " ]";
    }
    return null;
  }

  @Override
  public String getDeactivationCondition() {
    if (getChild() instanceof TriBinding) {
      TriBinding tri = (TriBinding) getChild();
      return tri.getDeactivationCondition(this, getCustDialog());
    }
    else if (getChild() != null) {
      AShape chld = getChild().getChild();
      addInputVariable(chld.getName(), chld.getDesc(), null);
      return getCustDialog().getDeactivationCondition(chld.getName(), 'P', chld);
    }
    AShape p = getParent().getParent();
    return getCustDialog().getDeactivationCondition("FINAL_"+p.getName(), 'F', p);
  }

  /**
   * @return the trName
   */
  public String getTrName() {
    return "/ " + trName + " /";
  }

  public void setupDialogHelpers() {
    getCustDialog().pnsetup(null);
  }

  /**
   * @return the trPos
   */
  public synchronized TransitionPosition getTrPos() {
    return trPos;
  }

  @Override
  public void cacheFromTransients() {
    if (getCustDialog().getDelayInfo() != null) {
      selectedUnit = getCustDialog().getDelayInfo().getUnit();
    }
    if (getCustDialog().getClassicInfo() != null) {
      selectedArrow = getCustDialog().getClassicInfo().getArrow();
    }
  }
}
