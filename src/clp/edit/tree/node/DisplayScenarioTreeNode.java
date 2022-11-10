package clp.edit.tree.node;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import clp.edit.tree.node.util.ScenarioContent;
import clp.edit.util.ColorSet;
import clp.run.scn.DeactType;
import clp.run.scn.Scenario;
import clp.run.scn.ScnQueue;
import clp.run.scn.ScnTask;
import clp.run.scn.ScnTaskName;
import clp.run.scn.SortOrder;

public class DisplayScenarioTreeNode extends ScenarioTreeNode {

  private static final long serialVersionUID = 1709088064239989846L;


  public DisplayScenarioTreeNode(String name, Scenario scn, File ref, String source) {
    super(name, scn, ref, source);
  }

  @Override
  public void setProperties(JPanel panel) {
    super.setProperties(panel);
    disablePanelContents(panel);
  }

  @Override
  protected void fillQueuesHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JLabel tf = new JLabel(" queue name ");
    tf.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JLabel(" income ");
    tf.setForeground(info.getDark());
    c.gridx = 1;
    jp.add(tf, c);
  }

  @Override
  protected void fillQueues(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 1;
    for (ScnQueue q : ((ScenarioContent) getContent()).getQueues()) {
      c.gridx = 0;
      jp.add(createQueueTextField(q), c);
   
      JComboBox<String> cmb = new JComboBox<>();
      for (SortOrder so : SortOrder.values()) {
        cmb.addItem(so.name());
      }
      cmb.setSelectedItem(q.getSortOrder().name());
      cmb.setBackground(info.getLight());
      c.gridx = 1;
      jp.add(cmb, c);

      c.gridy++;
    }
  }

  @Override
  protected void fillTasksHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JLabel tf = new JLabel(" task name ");
    tf.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JLabel(" operating on ");
    tf.setForeground(info.getDark());
    c.gridx = 1;
    jp.add(tf, c);
 
    tf = new JLabel(" passing to ");
    tf.setForeground(info.getDark());
    c.gridx = 2;
    jp.add(tf, c);
  }

  @Override
  protected void fillTasks(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 1;
    for (ScnTask t : ((ScenarioContent) getContent()).getTasks()) {
      JComboBox<String> cmb = new JComboBox<>();
      for (ScnTaskName tn : ScnTaskName.values()) {
        cmb.addItem(tn.name());
      }
      cmb.setSelectedItem(t.getScnTaskName().name());
      cmb.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(cmb, c);
   
      c.gridx = 1;
      jp.add(createTaskTextField(t, true), c);
      
      c.gridx = 2;
      jp.add(createTaskTextField(t, false), c);

      c.gridy++;
    }
  }

  @Override
  protected JPanel createDeactType() {
    JPanel jp = new JPanel();
    jp.setLayout(new GridLayout(1, 3));

    DeactType sel = ((ScenarioContent) getContent()).getDeactivation();
    JLabel lbl = new JLabel(sel == DeactType.AUTO ? " automatic deactivation " : " deactivation through DEACTIVATOR ");
    jp.add(lbl);

    JComboBox<String> cmb = new JComboBox<>();
    for (DeactType dt : DeactType.values()) {
      cmb.addItem(dt.name());
    }
    cmb.setSelectedItem(sel.name());
    jp.add(cmb);

    return jp;
  }
}
