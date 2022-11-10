package clp.edit.graphics.code.java.bci;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.bcel.generic.LocalVariableGen;

public class ActionInfo implements Serializable {

  private static final long serialVersionUID = 6710449069148797447L;

  private static final String[] actions = { "NOTIFY", "EXPORT VALUE" };

  private static final String[] places = { "TOP", "BOTTOM" };

  transient private LocalVariableGen[] lvgList;

  transient private Field[] fields;

  transient private JComboBox<String> placeCombo;     // select BCI location (TOP or BOTTOM)
  private String selectedPlace;

  private int line;

  transient private JComboBox<String> actionCombo;
  private String selectedAction;

  transient private JComboBox<String> varsCombo;
  private String selectedVar;

  private JTextField namefield;
  private String vtype;
  private String vname;
  private boolean isGlobal;

  public ActionInfo(ActionsDialog actionsDialog, int line, LocalVariableGen[] localVariables, Field[] fields) {
    this.line = line + 1;
    namefield = new JTextField(10);
    actionCombo = new JComboBox<>(actions);
    actionCombo.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          selectedAction = (String) actionCombo.getSelectedItem();
          actionsDialog.refresh();
        }
      }
    });
    varsCombo = new JComboBox<>();
    varsCombo.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          selectedVar = (String) varsCombo.getSelectedItem();
          String[] sp = selectedVar.split(" ");
          vname = sp[0];
          int i = vname.indexOf("/");
          if (i > 0) {
            vtype = vname.substring(i+1);
            vname = vname.substring(0, i);
            isGlobal = true;
          }
          else {
            i = sp[1].indexOf("/");
            vtype = sp[1].substring(i+1);
            isGlobal = false;
          }
          namefield.setText(vname);
        }
      }
    });
    this.lvgList = localVariables;
    this.fields = fields;
    placeCombo = new JComboBox<>(places);
    placeCombo.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          selectedPlace = (String) placeCombo.getSelectedItem();
        }
      }
    });
  }

  public int getLine() {
    return line;
  }

  public JTextField getNamefield() {
    return namefield;
  }

  public JComboBox<String> getActionCombo() {
    return actionCombo;
  }

  public boolean isNotification() {
    return actionCombo.getSelectedIndex() == 0;
  }

  public JComboBox<String> getVarsCombo() {
    return varsCombo;
  }

  public void fillVariables(LocalVariableGen[] localVariables, Field[] fields) {
    varsCombo.removeAllItems();
    for (Field field : fields) {
      varsCombo.addItem(field.getName()+"/"+field.getType().getTypeName());
    }
    for (int i=1; i<localVariables.length; i++) {
      LocalVariableGen lvg = localVariables[i];
      varsCombo.addItem(lvg.getName() + " (" + lvg.getStart().getPosition() + "-" + lvg.getEnd().getPosition() + ")/"+lvg.getType());
    }
  }

  public void gatherMethodInjection(JComboBox<String> placeCombo, BCICombos bciCombos, Hashtable<String, List<String>> hash) {
    String key = "\tonMethod \"" + bciCombos.getMethodName() + "\" (["
                 + bciCombos.getMethodArgumentsCount() + "]) place at " + placeCombo.getSelectedItem() + " {\n";
    String value;
    if (actionCombo.getSelectedIndex() == 0) {  // NOTIFY
      value = "\t  notify " + namefield.getText() +";\n";
      vtype = "boolean";
    }
    else {  // EXPORT VARIABLE
      String scope = isGlobal ? "GLOB " : "LOC ";
      value = "\t  export " + scope + "\"" + vtype + "\" : \"" + vname + "\" to " + namefield.getText() +";\n";
    }
    List<String> list = hash.get(key);
    if (list == null) {
      list = new ArrayList<>();
      hash.put(key, list);
    }
    list.add(value);
  }

  public void gatherVariable(ArrayList<String> list) {
    list.add(namefield.getText() + "/" + getType(vtype) + "/");
  }

  //
  private String getType(String tp) {
    switch (tp) {
      case "boolean":
        return "BOOL";
      case "float":
      case "double":
        return "FLOAT";
      case "int":
        return "INT";
      case "long":
        return "LONG";

      default:
        break;
    }
    return "REF";
  }

  /**
   * @return the localVariables
   */
  public LocalVariableGen[] getLocalVariables() {
    return lvgList;
  }
  /**
   * @param localVariables the localVariables to set
   */
  public void setLocalVariables(LocalVariableGen[] localVariables) {
    this.lvgList = localVariables;
  }
  /**
   * @return the fields
   */
  public Field[] getFields() {
    return fields;
  }
  /**
   * @param fields the fields to set
   */
  public void setFields(Field[] fields) {
    this.fields = fields;
  }
  /**
   * @return the placeCombo
   */
  public JComboBox<String> getPlaceCombo() {
    return placeCombo;
  }
  /**
   * @param placeCombo the placeCombo to set
   */
  public void setPlaceCombo(JComboBox<String> placeCombo) {
    this.placeCombo = placeCombo;
  }
  /**
   * @return the selectedPlace
   */
  public String getSelectedPlace() {
    return selectedPlace;
  }
  /**
   * @param selectedPlace the selectedPlace to set
   */
  public void setSelectedPlace(String selectedPlace) {
    this.selectedPlace = selectedPlace;
  }

  public void saveInfo(ActionInfo firstInfo) {
    selectedAction = (String) actionCombo.getSelectedItem();
    selectedPlace = firstInfo.selectedPlace;
    if (varsCombo != null) {
      selectedVar = (String) varsCombo.getSelectedItem();
    }
  }

  public void retreiveInfo(ActionsDialog actionsDialog, LocalVariableGen[] localVariables, Field[] fields) {
    if (selectedAction != null) {
      actionCombo = new JComboBox<>(actions);
      actionCombo.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            selectedAction = (String) actionCombo.getSelectedItem();
          }
        }
      });
      actionCombo.setSelectedItem(selectedAction);
      if (selectedPlace != null) {
        placeCombo = new JComboBox<>(places);
        placeCombo.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              selectedPlace = (String) placeCombo.getSelectedItem();
            }
          }
        });
        placeCombo.setSelectedItem(selectedPlace);
      }
      if (selectedVar != null) {
        varsCombo = new JComboBox<>();
        fillVariables(localVariables, fields);
        varsCombo.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              selectedVar = (String) varsCombo.getSelectedItem();
              String[] sp = selectedVar.split(" ");
              vname = sp[0];
              int i = vname.indexOf("/");
              if (i > 0) {
                vtype = vname.substring(i+1);
                vname = vname.substring(0, i);
                isGlobal = true;
              }
              else {
                i = sp[1].indexOf("/");
                vtype = sp[1].substring(i+1);
                isGlobal = false;
              }
              namefield.setText(vname);
            }
          }
        });
        varsCombo.setSelectedItem(selectedVar);
      }
    }
  }
}
