package clp.edit.tree.node;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.TreeNode;

import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.tree.node.util.ActorContent;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.ScenarioContent;
import clp.edit.util.ColorSet;
import clp.run.act.Actor;
import clp.run.scn.Scenario;
import clp.run.scn.ScnQueue;

public class ActorTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = -3540012179687265755L;

  private Actor act;

  private ColorSet info;  // this object's context
  private ActorContent content;

  /**
   * CONSCTUCTOR
   * 
   * @param name
   * @param a actor
   * @param ref
   * @param source
   */
  public ActorTreeNode(String name, Actor a, File ref, String source) {
    super(name, source, ref);
    this.act = a;
    this.act.setName(name);
    this.info = ColorSet.ActorProperties;
    initializeContext(a);
  }

  //
  private void initializeContext(Actor a) {
    content = new ActorContent(a != null && a.getScenario() != null);
  }

  @Override
  public Object getAssociatedObject() {
    return act;
  }

  @Override
  public void setProperties(JPanel panel) {
    JPanel jp1 = new JPanel();
    jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
    jp1.add(new JLabel("Name: "));
    jp1.add( createField("name", getName()) );
    if (!content.isAssigned()) {
      JLabel lbl = new JLabel("assigned to ");
      lbl.setBackground(info.getLight());
      jp1.add(lbl);
      jp1.add( createField("assign", act.getScn()) );
    }
    jp1.add(Box.createHorizontalStrut(500));

    panel.add(jp1);
  }

  //
  private JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(8);
    field.setText(text);
    field.setBackground(info.getLight());
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Actor";
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public JMenu addChildContextMenu() {
    JMenu menu = new JMenu("add");
    menu.add(PopupContext.getInstance().createSubItem(this, "a HEAP", Action.INSERT, Argument.HEAP));
    menu.add(PopupContext.getInstance().createSubItem(this, "a Grep", Action.INSERT, Argument.GREP));
    return menu;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    if (tparent instanceof ScenarioTreeNode) {
      return null;
    }
    JMenu menu = new JMenu("wrap with");
    menu.add(PopupContext.getInstance().createSubItem(this, "a File", Action.WRAP, Argument.FILE));
    menu.add(PopupContext.getInstance().createSubItem(this, "a SCENARIO", Action.WRAP, Argument.SCN));
    return menu;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return candidate instanceof HeapTreeNode;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
    act.setScenario((Scenario) newParent.getAssociatedObject());
    act.setScn(null);
    content.setAssigned(true);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
    if (act.getScenario() != null) {
      act.setScn(parent.getName());
      act.setScenario(null);
    }
    content.setAssigned(false);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateField((JTextField) e.getSource());
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
    updateField((JTextField) e.getSource());
  }

  //
  private void updateField(JTextField field) {
    boolean isRename = false;
    if ("name".equals(field.getName())) {
      isRename = !getName().equals(field.getText());
      if (isRename) {
        setName(field.getText());
        ((Actor)getAssociatedObject()).setName(getName());
        GeneralContext.getInstance().setCodeDirty(true);
      }
    }
    else if (!field.getText().equals(act.getScn())) {
      act.setScn(field.getText());
      GeneralContext.getInstance().setCodeDirty(true);
    }
    super.updateNode(isRename, ActorTreeNode.this);
  }

  @Override
  public String getSource() {
    return "actor " + getName() + " {" + getChildrenSource() + "}";
  }

  @Override
  public String getUnassignedSource() {
    return "actor " + getName() + " assignTo "+act.getScn() + " {\n" + getChildrenSource() + "}\n";
  }

  //
  private String getChildrenSource() {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<getChildCount(); i++) {
      sb.append(((ATreeNode) getChildAt(i)).getSource());
    }
    return sb.toString();
  }

  public ArrayList<ScnQueue> gatherQueues() {
    TreeNode p = getParent();
    if (content.isAssigned()) {
      ScenarioContent scontent = (ScenarioContent) ((ScenarioTreeNode)p).getContent();
      if (scontent != null) {
        return scontent.getQueues();
      }
    }
    else {
      while (p != null && !(p instanceof ProjectTreeNode)) {
        p = p.getParent();
      }
      if (p != null) {
        ScenarioTreeNode s = ((ProjectTreeNode)p).findScenario(act.getScn());
        if (s != null) {
          ScenarioContent scontent = (ScenarioContent) s.getContent();
          if (scontent != null) {
            return scontent.getQueues();
          }
        }
      }
    }
    return null;
  }
}
