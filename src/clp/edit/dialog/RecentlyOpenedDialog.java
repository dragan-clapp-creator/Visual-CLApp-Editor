package clp.edit.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import clp.edit.handler.RecentHandler;

public class RecentlyOpenedDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 4916979670471672369L;

  private JButton select;
  private JButton unselect;

  private JButton okButton;
  private JButton cancelButton;

  transient private RecentHandler rhandler;

  private boolean isRefreshNeeded;

  public RecentlyOpenedDialog(Frame parent, RecentHandler handler) {
    super(parent, "Customize Recently Opened Projects", true);
    this.rhandler = handler;
    Point p = parent.getLocation(); 
    setLocation(p.x + 300, p.y + 180);
    setLayout(new BorderLayout());
    defineContent();
    setAlwaysOnTop(false);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  public void defineContent() {
    getContentPane().removeAll();

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 0;
    c.gridwidth = 3;
    for (File f : rhandler.getFiles()) {
      String text = f.getName();
      try {
        text += " ("+ f.getParentFile().getCanonicalPath() + ")";
        JCheckBox cb = new JCheckBox(text);
        cb.setToolTipText(f.getCanonicalPath());
        getContentPane().add(cb, c);
        cb.addActionListener(this);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      c.gridy++;
    }
    c.gridwidth = 1;

    c.gridx = 0;
    select = new JButton("select all");
    select.addActionListener(this);
    getContentPane().add(select, c);
    c.gridx = 1;
    unselect = new JButton("unselect all");
    unselect.addActionListener(this);
    getContentPane().add(unselect, c);

    c.gridy++;
    c.gridx = 0;
    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(this);
    getContentPane().add(cancelButton, c);
    c.gridx = 1;
    okButton = new JButton("keep selected");
    okButton.addActionListener(this);
    getContentPane().add(okButton, c);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cancelButton) {
      dispose();
    }
    else if (e.getSource() == okButton) {
      removeUnselected();
      isRefreshNeeded = true;
      dispose();
    }
    else if (e.getSource() == select) {
      selectAll();
    }
    else if (e.getSource() == unselect) {
      unselectAll();
    }
  }

  private void selectAll() {
    for (int i=0; i< getContentPane().getComponentCount(); i++) {
      Component cmp = getContentPane().getComponent(i);
      if (cmp instanceof JCheckBox) {
        JCheckBox cb = (JCheckBox) cmp;
        if (!cb.isSelected()) {
          cb.setSelected(true);
        }
      }
    }
  }

  private void unselectAll() {
    for (int i=0; i< getContentPane().getComponentCount(); i++) {
      Component cmp = getContentPane().getComponent(i);
      if (cmp instanceof JCheckBox) {
        JCheckBox cb = (JCheckBox) cmp;
        if (cb.isSelected()) {
          cb.setSelected(false);
        }
      }
    }
  }

  private void removeUnselected() {
    ArrayList<File> files = new ArrayList<>();
    for (int i=0; i< getContentPane().getComponentCount(); i++) {
      Component cmp = getContentPane().getComponent(i);
      if (cmp instanceof JCheckBox) {
        JCheckBox cb = (JCheckBox) cmp;
        if (cb.isSelected()) {
          File file = new File(cb.getToolTipText());
          files.add(file);
        }
      }
    }
    rhandler.setFiles(files);
  }

  /**
   * @return the isRefreshNeeded
   */
  public boolean isRefreshNeeded() {
    return isRefreshNeeded;
  }

}
