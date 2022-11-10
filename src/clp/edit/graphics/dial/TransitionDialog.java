package clp.edit.graphics.dial;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.ATransitionRoot;
import clp.edit.graphics.shapes.ATransitionShape;
import clp.edit.graphics.shapes.pn.InvisibleNode;
import clp.edit.graphics.shapes.pn.PlaceNodeShape;
import clp.edit.graphics.shapes.pn.TransitionNodeShape;
import clp.run.res.Unit;
import clp.run.res.VarType;


public class TransitionDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = -3695542361226180194L;

  private GenericActionListener gal;

  private GridBagConstraints c;

  private JButton okButton;

  private ATransitionShape transitionNodeShape;
  private ATransitionRoot transitionRootShape;


  /**
   * CONSTRUCTOR
   * 
   * @param frame
   * @param ts transitionNodeShape
   * @param tr transitionRootShape
   * @param height 
   */
  public TransitionDialog(Frame frame, ATransitionShape ts, ATransitionRoot tr, int height) {
    super(frame, "Defining a Transition Item", true);
    transitionNodeShape = ts;
    transitionRootShape = tr;
    setup(frame, height);
  }

  //
  private void setup(Frame frame, int height) {
    if (frame != null) {
      Dimension frameSize = frame.getSize(); 
      Point p = frame.getLocation(); 
      setLocation(p.x + frameSize.width / 4, p.y + frameSize.height / 4);
    }


    setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setPreferredSize(new Dimension(650, height));

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    defineContent(c, okButton);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  public void defineContent(GridBagConstraints c, JButton okButton) {
    getContentPane().removeAll();
    c.gridy = 0;
    c.gridx = 0;
    getContentPane().add(new JLabel("Transition Type"), c);
    c.gridx++;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx++;
    c.gridwidth = getGridWidthAccordingToType();
    JPanel jp1 = createRadioButtonsForType();
    getContentPane().add(jp1, c);

    c.gridy++;
    c.gridx = 0;
    c.gridwidth = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);

    c.gridy++;
    c.gridx = 0;
    getContentPane().add(new JLabel("Crossing"), c);
    c.gridx++;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx++;
    boolean isDescriptionRequested = createCrossingFromType(c);

    if (isDescriptionRequested) {
      c.gridy++;
      c.gridx = 0;
      getContentPane().add(new JLabel("Description"), c);
      c.gridx++;
      getContentPane().add(Box.createVerticalStrut(5), c);
      c.gridx++;
      c.gridwidth = 3;
      getContentPane().add(getClassicInfo().getEventDescriptionField(), c);
      c.gridx += 3;
      c.gridwidth = 1;
      getContentPane().add(Box.createVerticalStrut(5), c);
      c.gridx++;
      getContentPane().add(getClassicInfo().getConditionDescriptionField(), c);
    }

    c.gridy++;
    c.gridx = 0;
    getContentPane().add(Box.createVerticalStrut(5), c);
    
    c.gridy++;
    c.gridx = 1;
    c.gridwidth = 2;
    getContentPane().add(okButton, c);
  }

  //
  private int getGridWidthAccordingToType() {
    switch (getTrType()) {
      case TAUTOLOGY:
        return 1;
      case CLASSICAL:
        return 5;
      case DELAY:
        return 7;
      default:
        break;
    }
    return 0;
  }

  //
  protected boolean createCrossingFromType(GridBagConstraints c) {
    switch (getTrType()) {
      case TAUTOLOGY:
        getContentPane().add(getTautology(), c);
        break;
      case CLASSICAL:
        getContentPane().add(getClassicInfo().getArrowfield(), c);
        c.gridx++;
        getContentPane().add(Box.createVerticalStrut(5), c);
        c.gridx++;
        getContentPane().add(getClassicInfo().getEventfield(), c);
        c.gridx++;
        getContentPane().add(Box.createVerticalStrut(5), c);
        c.gridx++;
        getContentPane().add(getClassicInfo().getConditionfield(), c);
        return true;
      case DELAY:
        getContentPane().add(new JLabel("Step N°"), c);
        c.gridx++;
        getContentPane().add(Box.createVerticalStrut(5), c);
        c.gridx++;
        getContentPane().add(getDelayInfo().getStepfield(), c);
        c.gridx++;
        getContentPane().add(Box.createVerticalStrut(5), c);
        c.gridx++;
        getContentPane().add(new JLabel("Delay"), c);
        c.gridx++;
        getContentPane().add(getDelayInfo().getDelayfield(), c);
        c.gridx++;
        getContentPane().add(getDelayInfo().getUnitfield(), c);
        break;

      default:
        break;
    }
    return false;
  }

  //
  protected JPanel createRadioButtonsForType() {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        "Choose Transition Type");
    jp.setBorder(border);

    ItemListener radioListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb.isSelected()) {
          setTrType( TransitionType.getName(rb.getText()) );
          refresh();
        }
      }
    };
    ButtonGroup group = new ButtonGroup();
    for (TransitionType t : TransitionType.values()) {
      JRadioButton rb = new JRadioButton(t.getVal(), true);
      jp.add(rb); rb.addItemListener(radioListener);
      if (t == getTrType()) {
        if (!rb.isSelected()) {
          rb.setSelected(true);
        }
      }
      else if (rb.isSelected()) {
        rb.setSelected(false);
      }
      group.add(rb);
    }
    return jp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    setVisible(false); 
  }

  public String getTransitionText() {
    switch (getTrType()) {
      case TAUTOLOGY:
        return getTautology().getText();
      case CLASSICAL:
        return getClassicInfo().toString();
      case DELAY:
        return getDelayInfo().toString();

      default:
        break;
    }
    return null;
  }

  public void edit(String t, String d) {
    switch (getTrType()) {
      case TAUTOLOGY:
        getTautology().setText(t);
        break;
      case CLASSICAL:
        getClassicInfo().parse(t);
        break;
      case DELAY:
        getDelayInfo().parse(t);
        break;

      default:
        break;
    }
    setVisible(true);
  }

  public void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        defineContent(c, okButton);
        getRootPane().updateUI();
      }
    });
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }  

  /**
   * @return the classicInfo
   */
  public ClassicGroup getClassicInfo() {
    if (transitionNodeShape != null) {
      return transitionNodeShape.getClassicInfo();
    }
    return transitionRootShape.getClassicInfo();
  }

  /**
   * @return the delayInfo
   */
  public DelayGroup getDelayInfo() {
    if (transitionNodeShape != null) {
      return transitionNodeShape.getDelayInfo();
    }
    return transitionRootShape.getDelayInfo();
  }

  public String getActivationCondition(String name, TransitionNodeShape node) {
    boolean isParentInvisibleNode = (node.getParent().getParent() instanceof InvisibleNode);
    String s = null;
    switch (getTrType()) {
      case CLASSICAL:
        if (isParentInvisibleNode) {
          name = null;
        }
        s = fillFromClassicInfo(name, true);
        break;
      case DELAY:
        DelayGroup dinfo = node.getDelayInfo();
        s = dinfo.getStep('P');
        boolean isCyclic = s.equals("C");
        boolean isAccepted = isCyclic || s.endsWith("0") || s.equals(name);
        String step = isCyclic || name.startsWith("INIT") && s.endsWith("0") ? name : s;
        String timeIdentifier = dinfo.getTimeIdentifier();
        if (isAccepted) {
          s = " activated(" + step + ") SINCE " + timeIdentifier;
          addDelay(step, timeIdentifier, dinfo.getDelay(), dinfo.getUnit(), isCyclic);
        }
        else if (!isParentInvisibleNode) {
          s = " activated(" + name + ")";
        }
        else {
          s = "";
        }
        break;
      case TAUTOLOGY:
        if (node != null) {
          if (!isParentInvisibleNode) {
            addInputVariable(node.getName(), "", null, true);
          }
          if (!isParentInvisibleNode) {
            s = " activated(" + name + ")";
          }
        }
        break;
    }
    return s;
  }

  public String getActivationCondition(String n, char prefix, AShape node) {
//    clearResources();
    boolean isToken = node instanceof PlaceNodeShape ? true : false;
    boolean isParentInvisibleNode = (node instanceof InvisibleNode);
    String name = n != null ? n : node.getName();
    String s = null;
    switch (getTrType()) {
      case CLASSICAL:
        if (isParentInvisibleNode) {
          name = null;
        }
        s = fillFromClassicInfo(name, isToken);
        break;
      case DELAY:
        s = fillFromDelayInfo(name, prefix, true);
        break;
      case TAUTOLOGY:
        if (node != null) {
          if (isToken && !isParentInvisibleNode) {
            addInputVariable(node.getName(), "", null, true);
          }
          if (!isParentInvisibleNode) {
            s = " activated(" + name + ")";
          }
        }
        break;
    }
    return s;
  }

  public String getDeactivationCondition(String name, char prefix, AShape node) {
    String s = null;
    switch (getTrType()) {
      case TAUTOLOGY:
        if (prefix == 'F') {
          s = " TRUE";
        }
        else {
          s = " activated(" + name + ")";
        }
        break;
      case CLASSICAL:
        if (name == null) { // case of final node
          s = "TRUE";
        }
        else {
          s = "activated(" + name + ")";
        }
        break;
      case DELAY:
        if (name == null) { // case of final node
          s = fillFromDelayInfo(node.getName(), prefix, false);
        }
        else {
          DelayGroup dinfo = getDelayInfo();
          if (!dinfo.getStep(prefix).equals("C")) {
            s = "activated(" + name + ")";
          }
        }
        break;
    }
    return s;
  }

  //
  private String fillFromClassicInfo(String name, boolean isToken) {
    ClassicGroup cinfo = getClassicInfo();
    String s = name == null ? "" : " activated(" + name + ")";
    switch (cinfo.getArrow()) {
      case ClassicGroup.upArrow:
        if (!s.isEmpty()) {
          s += " AND ";
        }
        s += "isSetUp(" + cinfo.getEvent() + ")";
        addInputVariable(cinfo.getEvent(), cinfo.getEventDescription(), null, isToken);
        checkRemovingInitActivation(name);
       break;
      case ClassicGroup.downArrow:
        if (!s.isEmpty()) {
          s += " AND ";
        }
        s += "isSetDown(" + cinfo.getEvent() + ")";
        addInputVariable(cinfo.getEvent(), cinfo.getEventDescription(), null, isToken);
        checkRemovingInitActivation(name);
        break;
      default:
        break;
    }
    String cnd = adaptToClapp(cinfo.getCondition());
    addInputVariable(cnd, cinfo.getConditionDescription(), VarType.TBOOL, isToken);
    if (cnd != null && !cnd.isEmpty()) {
      cnd = cnd.replace("+", " OR ");
      cnd = cnd.replace(".", " AND ");
      if (!s.isEmpty()) {
        s += " AND ";
      }
      s += cnd;
    }
    return s;
  }

  //
  private String adaptToClapp(String str) {
    if (str != null) {
      int op = getOperatorPosition(str);
      if (op > 0) { // condition pattern is: <variable> <comparison op> <value>
        String[] sp = str.split(" ");
        if (sp.length == 3) {
          return sp[1] + "(" + sp[0] + ", " + sp[2] + ")";
        }
      }
    }
    return str;
  }

  //
  private int getOperatorPosition(String str) {
    int i = str.indexOf("<");
    if (i > 0) {
      return i;
    }
    i = str.indexOf(">");
    if (i > 0) {
      return i;
    }
    i = str.indexOf("=");
    if (i > 0) {
      return i;
    }
    i = str.indexOf("#");
    if (i > 0) {
      return i;
    }
    return 0;
  }

  //
  private void addInputVariable(String cnd, String desc, VarType type, boolean isToken) {
    if (transitionNodeShape != null) {
      transitionNodeShape.addInputVariable(cnd, desc, type);
    }
    else {
      transitionRootShape.addInputVariable(cnd, desc, type);
    }
  }

  //
  private void checkRemovingInitActivation(String name) {
    if (name != null && name.startsWith("INIT")) {
      GeneralContext.getInstance().getGraphicsPanel().getShapesContainer().removeDeactivationForInit(name);
    }
  }

  //
  private String fillFromDelayInfo(String name, char prefix, boolean isActivation) {
    DelayGroup dinfo = getDelayInfo();
    String s = prefix == 'F' ? name : dinfo.getStep(prefix);
    boolean isCyclic = s.equals("C");
    boolean isAccepted = isCyclic || s.endsWith("0") || name.equals(dinfo.getStep(prefix));
    if (!isAccepted) {
      s = " activated(" + name + ")";
    }
    else {
      String step = isCyclic || name.startsWith("INIT") && s.endsWith("0") ? name : s;
      String timeIdentifier = dinfo.getTimeIdentifier();
      if (isActivation || step.startsWith("INIT")) {
        s = " activated(" + step + ") SINCE " + timeIdentifier;
        addDelay(step, timeIdentifier, dinfo.getDelay(), dinfo.getUnit(), isCyclic);
      }
    }
    return s;
  }

  //
  private void addDelay(String step, String timeIdentifier, int delay, Unit unit, boolean isCyclic) {
    if (transitionNodeShape != null) {
      transitionNodeShape.addDelay(step, timeIdentifier, delay, unit, isCyclic);
    }
    else {
      transitionRootShape.addDelay(step, timeIdentifier, delay, unit, isCyclic);
    }
  }

  /**
   * @return the okButton
   */
  public JButton getOkButton() {
    return okButton;
  }

  /**
   * @return the tautology
   */
  public JLabel getTautology() {
    if (transitionNodeShape != null) {
      return transitionNodeShape.getTautology();
    }
    return transitionRootShape.getTautology();
  }

  @Override
  public String getDescription() {
    return null;
  }

  public TransitionType getTrType() {
    if (transitionNodeShape != null) {
      return transitionNodeShape.getTrType();
    }
    return transitionRootShape.getTrType();
  }

  public void setTrType(TransitionType tp) {
    if (transitionNodeShape != null) {
      transitionNodeShape.setTrType(tp);
    }
    else {
      transitionRootShape.setTrType(tp);
    }
  }
}
