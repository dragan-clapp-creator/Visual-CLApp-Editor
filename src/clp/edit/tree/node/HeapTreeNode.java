package clp.edit.tree.node;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.tree.node.util.HeapContent;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.ScenarioContent;
import clp.edit.util.ColorSet;
import clp.run.act.Actor;
import clp.run.cel.Heap;
import clp.run.scn.ScnQueue;

public class HeapTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = -4184520733780786054L;

  private Heap heap;

  private ColorSet info;  // this object's context
  private HeapContent content;
  private ArrayList<ScnQueue> queues;


  public HeapTreeNode(String name, Heap h, File ref, String source) {
    super(name, source, ref);
    this.heap = h;
    this.heap.setName(name);
    info = ColorSet.HeapProperties;
    initializeContext(h);
  }

  //
  private void initializeContext(Heap h) {
    content = new HeapContent(h != null && h.getActor() != null);
    content.setResourcesName(h.getRes());
    content.setQueue(h.getLoad());
    content.setActivity(h.getActivity());
  }

  @Override
  public Object getAssociatedObject() {
    return heap;
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
      jp1.add( createField("act", heap.getAct()) );
    }
    jp1.add(Box.createHorizontalStrut(500));

    panel.add(jp1);
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Queuing"));
    jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

    jp.add(createRelatedResourcesPanel());
    jp.add(createLoadOnPanel());
    jp.add(createActivityPanel());

    panel.add(jp);
  }

  //
  private JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(50);
    field.setBackground(info.getLight());
    field.setText(text);
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  //
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private JPanel createRelatedResourcesPanel() {
    JPanel jp = new JPanel();
    jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
    JLabel label = new JLabel("related resources ");
    jp.add(label);

    JComboBox<String> cmb = new JComboBox(getResourecesList());
    cmb.setSelectedItem(content.getResourcesName());
    cmb.setBackground(info.getLight());
    cmb.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        JComboBox<String> box = (JComboBox<String>) e.getSource();
        content.setResourcesName((String)box.getSelectedItem());
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    jp.add(cmb);
    jp.add(Box.createHorizontalStrut(500));
    return jp;
  }

  //
  private Object[] getResourecesList() {
    ATreeNode p = (ATreeNode) getParent();
    while (p != null && !(p instanceof ProjectTreeNode)) {
      p = (ATreeNode) p.getParent();
    }
    if (p != null) {
      return ((ProjectTreeNode)p).createResourceNamesList().toArray();
    }
    return new String[] {content.getResourcesName()};
  }

  //
  private JPanel createLoadOnPanel() {
    JPanel jp = new JPanel();
    jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
    jp.add(new JLabel("load on "));

    JComboBox<String> cmb = new JComboBox<>();
    createQueueList();
    if (queues != null) {
      for (ScnQueue q : queues) {
        cmb.addItem(q.getName());
      }
    }
    cmb.setSelectedItem(content.getQueue());
    cmb.setBackground(info.getLight());
    cmb.addFocusListener(new FocusListener() {
      @SuppressWarnings("unchecked")
      @Override
      public void focusLost(FocusEvent e) {
        JComboBox<String> box = (JComboBox<String>) e.getSource();
        content.setQueue((String)box.getSelectedItem());
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    jp.add(cmb);
    jp.add(Box.createHorizontalStrut(500));
    return jp;
  }

  //
  private void createQueueList() {
    ATreeNode p = (ATreeNode) getParent();
    if (content.isAssigned()) {
      ActorTreeNode anode = (ActorTreeNode) getParent();
      queues = anode.gatherQueues();
    }
    else if (p instanceof FileTreeNode) {
      ProjectTreeNode pn = (ProjectTreeNode) p.getParent();
      if (pn != null) {
        ScenarioTreeNode snode = findScenario(pn);
        if (snode != null) {
          ScenarioContent scontent = (ScenarioContent) snode.getContent();
          if (scontent != null) {
            queues = scontent.getQueues();
          }
        }
      }
    }
  }

  //
  private ScenarioTreeNode findScenario(ProjectTreeNode p) {
    if (heap.getAct() != null) {
      ActorTreeNode anode = p.findActor(heap.getAct());
      if (anode != null) {
        Actor act = (Actor) anode.getAssociatedObject();
        if (act.getScn() != null) {
          return p.findScenario(act.getScn());
        }
        return p.findScenario(anode);
      }
    }
    return null;
  }

  //
  private JPanel createActivityPanel() {
    JPanel jp = new JPanel();
    jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
    jp.add(new JLabel("initial activity "));
    JTextField qfield = new JTextField(4);
    qfield.setText(""+content.getActivity());
    qfield.setBackground(info.getLight());
    qfield.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        content.setActivity(Integer.parseInt(((JTextField)e.getSource()).getText()));
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    });
    jp.add(qfield);
    jp.add(Box.createHorizontalStrut(500));
    return jp;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Heap";
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public JMenu addChildContextMenu() {
    JMenu menu = new JMenu("add");
    menu.add(PopupContext.getInstance().createSubItem(this, "a CELL", Action.INSERT, Argument.CELL));
    return menu;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    if (tparent instanceof ActorTreeNode) {
      return null;
    }
    JMenu menu = new JMenu("wrap with");
    menu.add(PopupContext.getInstance().createSubItem(this, "a File", Action.WRAP, Argument.FILE));
    menu.add(PopupContext.getInstance().createSubItem(this, "an ACTOR", Action.WRAP, Argument.ACT));
    return menu;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return false;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
    heap.setActor((Actor) newParent.getAssociatedObject());
    heap.setAct(null);
    content.setAssigned(true);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
    if (heap.getActor() != null) {
      heap.setAct(parent.getName());
      heap.setActor(null);
    }
    content.setAssigned(false);
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
    updateField((JTextField) e.getSource());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    updateField((JTextField) e.getSource());
  }

  //
  private void updateField(JTextField field) {
    boolean isRename = false;
    switch (field.getName()) {
      case "name":
        isRename = !getName().equals(field.getText());
        if (isRename) {
          setName(field.getText());
          ((Heap)getAssociatedObject()).setName(getName());
          GeneralContext.getInstance().setCodeDirty(true);
       }
       break;
      case "act":
        if (!field.getText().equals(heap.getAct())) {
          heap.setAct(field.getText());
          GeneralContext.getInstance().setCodeDirty(true);
          refresh(this);
        }
        break;

      default:
        break;
    }
    super.updateNode(isRename, HeapTreeNode.this);
  }

  @Override
  public String getSource() {
    return "  heap " + getName() + getHeadInfo() + " {" + getChildrenSource() + "}";
  }

  @Override
  public String getUnassignedSource() {
    return "  heap " + getName() + " assignTo "+ heap.getAct() + getHeadInfo() + " {\n" + getChildrenSource() + "}\n";
  }

  //
  private String getHeadInfo() {
    return  (content.getResourcesName() == null ? " " : " usedResources " + content.getResourcesName())
        + " loadOn " + content.getQueue()
        + (content.getActivity() > 0 ? " ["+content.getActivity()+"]" : "");
  }

  //
  private String getChildrenSource() {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<getChildCount(); i++) {
      sb.append(((ATreeNode) getChildAt(i)).getSource());
    }
    return sb.toString();
  }
}
