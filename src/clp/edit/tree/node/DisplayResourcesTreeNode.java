package clp.edit.tree.node;

import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import clp.edit.GeneralContext;
import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.tree.node.util.ResourcesContent;
import clp.edit.tree.node.util.ResourcesContent.CommonVariable;
import clp.edit.tree.node.util.ResourcesContent.EventVariable;
import clp.edit.tree.node.util.ResourcesContent.MarkVariable;
import clp.edit.tree.node.util.ResourcesContent.WeaveVariable;
import clp.edit.util.ColorSet;
import clp.run.res.Resources;

public class DisplayResourcesTreeNode extends ResourcesTreeNode {

  private static final long serialVersionUID = -7219485971866134381L;


  public DisplayResourcesTreeNode(String name, Resources res, File ref, String source) {
    super(name, res, ref, source);
  }

  @Override
  public void setProperties(JPanel panel) {
    super.setProperties(panel);
    disablePanelContents(panel);
  }

  @Override
  protected void fillVariablesHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JLabel tf = new JLabel(" type ");
    tf.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JLabel(" array ");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JLabel(" name ");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JLabel(" initial ");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
  }

  @Override
  protected void fillVariables(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 1;
    JTextField tf;
    for (CommonVariable var : ((ResourcesContent)getContent()).getSimpleVariables()) {
      tf = new JTextField(var.getType().getVal());
      tf.setForeground(info.getDark());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
   
      JCheckBox cb = new JCheckBox();
      cb.setSelected(var.isArray());
      cb.setForeground(info.getDark());
      cb.setBackground(info.getLight());
      c.gridx = 1;
      jp.add(cb, c);

      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 2;
      jp.add(tf, c);
   
      tf = new JTextField(var.getInitial() == null ? "" :var.getInitial().toString());
      tf.setBackground(info.getLight());
      c.gridx = 3;
      jp.add(tf, c);

      c.gridy++;
    }
  }

  @Override
  protected void fillEventsHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JLabel tf = new JLabel(" type ");
    tf.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JLabel(" name ");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JLabel(" time ");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JLabel("delay");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JLabel("unit");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JLabel("is cyclic");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
  }

  @Override
  protected void fillEvents(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 1;
    JTextField tf;
    for (EventVariable var : ((ResourcesContent)getContent()).getEventVariables()) {
      tf = new JTextField(var.getType());
      tf.setForeground(info.getLight());
      tf.setEnabled(false);
      c.gridx = 0;
      jp.add(tf, c);

      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      tf.setEnabled(false);
      c.gridx++;
      jp.add(tf, c);

      if (var.getTime() != null) {
        tf = new JTextField(var.getTime());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);

        tf = new JTextField(""+var.getDelay());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);

        tf = new JTextField(var.getUnit().getVal());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);

        JCheckBox cb = new JCheckBox();
        cb.setSelected(var.isCyclic());
        cb.setEnabled(false);
        tf.setBackground(info.getLight());
        c.gridx++;
        jp.add(cb, c);
      }
      c.gridy++;
    }
  }

  @Override
  protected void fillMarksHeader(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 0;
    JTextField tf = new JTextField("cell name");
    tf.setForeground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("marks");
    tf.setForeground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
  }

  @Override
  protected void fillMarks(JPanel jp, GridBagConstraints c) {
    ColorSet info = getInfo();
    c.gridy = 1;
    JTextField tf;
    for (MarkVariable var : ((ResourcesContent)getContent()).getMarkVariables()) {
      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      c.gridx = 0;
      jp.add(tf, c);
      tf = new JTextField(var.getMarks());
      tf.setBackground(info.getLight());
      c.gridx++;
      jp.add(tf, c);

      c.gridy++;
    }
  }

  @Override
  public String getDeclaration(WeaveVariable var) {
    GeneralShapesContainer sc = GeneralContext.getInstance().getGraphicsPanel().getShapesContainer();
    return sc.getJavaContext().getBciInfo(var.getName()).getStatement();
  }
}
