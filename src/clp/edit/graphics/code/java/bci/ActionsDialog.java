package clp.edit.graphics.code.java.bci;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.bcel.generic.LocalVariableGen;

import clp.edit.graphics.dial.GenericActionListener;

public class ActionsDialog extends JDialog {

  private static final long serialVersionUID = -6386629955258638569L;

  private ArrayList<ActionInfo> infos;

  private JButton okButton;
  private GenericActionListener gal;

  private JButton cancelButton;

  private GridBagConstraints c;

  private ActionInfo topInfo;

  /**
   * CONSTRUCTOR
   * 
   * @param parent
   * @param title
   * @param localVariables 
   * @param fields 
   */
  public ActionsDialog(Frame parent, String title, LocalVariableGen[] localVariables, Field[] fields) {
    super(parent, title, true);
    infos = new ArrayList<>();
    topInfo = new ActionInfo(this, 0, localVariables, fields);
    setup(parent);
  }

  public void showDialog() {
    setVisible(true);
  }

  //
  private void setup(Frame parent) {
    Dimension parentSize = parent.getSize(); 
    Point p = parent.getLocation(); 
    setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    setPreferredSize(new Dimension(800, 200));
    setLayout(new GridBagLayout());

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    cancelButton = new JButton("cancel");
    cancelButton.addActionListener(gal);

    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    fillContent(true);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  //
  private void fillContent(boolean isNew) {
    if (isNew) {
      getContentPane().removeAll();
    }
    c.gridy = 0;
    c.gridx = 0;
    JLabel lbl = new JLabel("Place at:", JLabel.TRAILING);
    lbl.setLabelFor(topInfo.getPlaceCombo());
    getContentPane().add(lbl, c);
    c.gridx = 1;
    getContentPane().add(topInfo.getPlaceCombo(), c);

    c.gridy = 1;
    c.gridx = 0;
    JButton button = new JButton("+");
    getContentPane().add(button, c);
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        infos.add(new ActionInfo(ActionsDialog.this, infos.size(), topInfo.getLocalVariables(), topInfo.getFields()));
        refresh();
     }
    });

    for (ActionInfo ai : infos) {
      c.gridy = ai.getLine();
      c.gridx = 1;
      getContentPane().add(ai.getActionCombo(), c);
      if (ai.isNotification()) {
        c.gridx = 2;
        getContentPane().add(Box.createVerticalStrut(5), c);
      }
      else {
        if (ai.getVarsCombo() == null || ai.getVarsCombo().getItemCount() == 0) {
          ai.fillVariables(topInfo.getLocalVariables(), topInfo.getFields());
        }
        c.gridx = 2;
        getContentPane().add(ai.getVarsCombo(), c);
      }
      c.gridx = 3;
      lbl = new JLabel("to:", JLabel.TRAILING);
      lbl.setLabelFor(ai.getNamefield());
      getContentPane().add(lbl, c);
      c.gridx = 4;
      getContentPane().add(ai.getNamefield(), c);
    }

    c.gridy++;
    c.gridx = 1;
    c.gridwidth = 2;
    getContentPane().add(getCancelButton(), c);
    c.gridx = 3;
    getContentPane().add(getOkButton(), c);
    c.gridwidth = 1;
  }

  public void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        fillContent(true);
        validate();
      }
    });
  }

  public boolean isOk() {
    return gal.isOk();
  }

  /**
   * @return the okButton
   */
  public JButton getOkButton() {
    return okButton;
  }

  /**
   * @return the cancelButton
   */
  public JButton getCancelButton() {
    return cancelButton;
  }

  /**
   * @return the infos
   */
  public ArrayList<ActionInfo> getInfos() {
    return infos;
  }

  public void saveInfo(ActionInfo topInfo) {
    for (ActionInfo ai : infos) {
      ai.saveInfo(topInfo);
    }
  }

  /**
   * @param infos the infos to set
   * @param localVariables 
   * @param fields 
   */
  public void setInfos(ArrayList<ActionInfo> infos, LocalVariableGen[] localVariables, Field[] fields) {
    this.infos = infos;
    for (ActionInfo ai : infos) {
      ai.retreiveInfo(this, localVariables, fields);
      topInfo.getPlaceCombo().setSelectedItem(ai.getSelectedPlace());
    }
    fillContent(false);
  }

  public String buildupStatement(BCICombos bciCombos) {
    String statement = "";
    Hashtable<String, List<String>> hash = new Hashtable<>();
    for (ActionInfo info : infos) {
      info.gatherMethodInjection(topInfo.getPlaceCombo(), bciCombos, hash);
    }
    for (String key : hash.keySet()) {
      statement += key;
      List<String> list = hash.get(key);
      for (String value : list) {
        statement += value;
      }
      statement += "\t}\n";
    }
    return statement;
  }

  public void gatherVariables(ArrayList<String> list) {
    for (ActionInfo info : infos) {
      if (!info.getNamefield().getText().isBlank()) {
        info.gatherVariable(list);
      }
    }
  }

  /**
   * @return the topInfo
   */
  public ActionInfo getTopInfo() {
    return topInfo;
  }
}
