package clp.edit.tree.node;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;

import clapp.cmp.ClappMain;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.SaveInfo;
import clp.edit.util.FileInfo;

public class FileTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = 6877311241406849419L;

  private FileInfo info;

  public FileTreeNode(File file, String source) {
    super(file.getName(), source, file);
    info = new FileInfo();
    info.setFile(file);
    info.setColor(Color.white);
  }

  @Override
  public Object getAssociatedObject() {
    return info.getFile();
  }

  @Override
  public void setProperties(JPanel panel) {
    JPanel jp = new JPanel();
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 0;
    c.gridx = 0;
    jp.add(new JLabel("File Name: "), c);
    c.gridx = 1;
    jp.add( createField("name", info.getName()), c );
    c.gridx = 2;
    jp.add(Box.createHorizontalStrut(200), c);

    if (info.getError() != null) {
      c.gridy++;
      c.gridx = 0;
      jp.add(new JLabel("Error: "), c);
      c.gridx = 1;
      JTextField field = new JTextField(50);
      field.setEditable(false);
      field.setText(info.getError());
      jp.add( field, c );
      c.gridx = 2;
      jp.add(Box.createHorizontalStrut(200), c);
    }

    panel.add(jp);
  }

  //
  private JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(50);
    field.setBackground(info.getColor());
    field.setText(text);
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  @Override
  public String getIcon() {
    return null;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return info.getName();
  }
  @Override
  public IContent getContent() {
    return null;
  }
  /**
   * @return the color
   */
  public Color getColor() {
    return info.getColor();
  }
  /**
   * @param color the color to set
   */
  public void setColor(Color color) {
    info.setColor(color);
  }
  @Override
  public JMenu addChildContextMenu() {
    JMenu menu = new JMenu("add");
    menu.add(PopupContext.getInstance().createSubItem(this, "a META-SCENARIO", Action.INSERT, Argument.MSC));
    menu.add(PopupContext.getInstance().createSubItem(this, "a SCENARIO", Action.INSERT, Argument.SCN));
    menu.add(PopupContext.getInstance().createSubItem(this, "a RESOURCES block", Action.INSERT, Argument.RES));
    menu.add(PopupContext.getInstance().createSubItem(this, "a SETTER block", Action.INSERT, Argument.SET));
    menu.add(PopupContext.getInstance().createSubItem(this, "an ACTOR", Action.INSERT, Argument.ACT));
    menu.add(PopupContext.getInstance().createSubItem(this, "a HEAP", Action.INSERT, Argument.HEAP));
    menu.add(PopupContext.getInstance().createSubItem(this, "a Grep", Action.INSERT, Argument.GREP));
    return menu;
  }
  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    return null;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return candidate instanceof MetaScenarioTreeNode || candidate instanceof ScenarioTreeNode || candidate instanceof ResourcesTreeNode ||
           candidate instanceof ActorTreeNode || candidate instanceof HeapTreeNode;
  }
  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
  }
  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
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
    boolean isRename = !getName().equals(field.getText());
    if (isRename) {
       ((ProjectTreeNode)getParent()).renameFile(info, field.getText());
    }
    super.updateNode(isRename, FileTreeNode.this);
  }

  public void setError(String error) {
    info.setError(error);
  }

  public boolean isGraphics() {
    return info.isGraphic();
  }

  public void setGraphics() {
    info.setGraphic(true);
  }

  @Override
  public String getSource() {
    if (getChildCount() == 0) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<getChildCount(); i++) {
      sb.append(((ATreeNode) getChildAt(i)).getUnassignedSource());
    }
    return new ClappMain().render( sb.toString() ).toString();
  }

  @Override
  public String getUnassignedSource() {
    return getSource();
  }

  /**
   * normal save; will be overwritten by {@link FlowTreeNode#save(SaveInfo)}, which performs a serialization
   * 
   * @param sinfo SaveInfo
   * @throws IOException
   */
  public void save(SaveInfo sinfo) throws IOException {
    File file = new File(sinfo.getPath()+File.separator+info.getName());
    FileOutputStream fos = new FileOutputStream(file);
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
    out.write(getSource());
    out.close();
    fos.close();
    System.out.printf("clapp file %s saved\n", file.getName());
  }

  /**
   * @return the info
   */
  public FileInfo getInfo() {
    return info;
  }

  @Override
  public void setName(String name) {
    super.setName(name);
    info.setName(name);
  }
}
