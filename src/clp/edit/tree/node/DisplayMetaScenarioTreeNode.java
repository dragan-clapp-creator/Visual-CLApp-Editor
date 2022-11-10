package clp.edit.tree.node;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import clp.edit.tree.node.util.MetaScenarioContent;
import clp.edit.util.ColorSet;
import clp.run.msc.MetaScenario;
import clp.run.msc.MscTaskName;

public class DisplayMetaScenarioTreeNode extends MetaScenarioTreeNode {

  private static final long serialVersionUID = -7536440002829380727L;

  /**
   * constructor
   * 
   * @param name
   * @param msc
   * @param ref
   * @param source
   */
  public DisplayMetaScenarioTreeNode(String name, MetaScenario msc, File ref, String source) {
    super(name, msc, ref, source);
  }
  @Override
  public void setProperties(JPanel panel) {
    super.setProperties(panel);
    disablePanelContents(panel);
  }

  @Override
  protected void fillTasksHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JLabel tf = new JLabel(" task name ");
    tf.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
  }

  @Override
  protected void fillTasks(JPanel jp, GridBagConstraints c) {
    MetaScenarioContent content = (MetaScenarioContent) getContent();
    c.gridy = 1;
    for (MscTaskName t : content.getTasks()) {
      JComboBox<String> cmb = new JComboBox<>();
      for (MscTaskName tn : MscTaskName.values()) {
        cmb.addItem(tn.name());
      }
      cmb.setSelectedItem(t.name());
      cmb.setName(""+c.gridy);
      cmb.setBackground(Color.white);
      cmb.addActionListener(new ActionListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
          JComboBox<String> box = (JComboBox<String>) e.getSource();
          int index = Integer.parseInt(box.getName())-1;
          content.setTaskNameAt(index, MscTaskName.valueOf((String)box.getSelectedItem()));
        }
      });
      c.gridx = 0;
      jp.add(cmb, c);

      c.gridy++;
    }
  }
}
