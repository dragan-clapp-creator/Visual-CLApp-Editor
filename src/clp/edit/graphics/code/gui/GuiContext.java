package clp.edit.graphics.code.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import clp.edit.graphics.code.gui.elts.GuiInfo;
import clp.run.res.ui.UiVar;

public class GuiContext implements Serializable {

  private static final long serialVersionUID = 1777225061283608921L;

  private Hashtable<String, GuiInfo> uiInfo;

  public GuiContext() {
    uiInfo = new Hashtable<>();
  }

  public GuiInfo getUiInfo(String name) {
    if (name == null) {
      return null;
    }
    return uiInfo.get(name);
  }

  public Set<String> getUiList() {
    return uiInfo.keySet();
  }

  public boolean addUiInfo(String uiName, UiVar uiVar, ArrayList<String> variables, String statement) {
    uiInfo.put(uiName, new GuiInfo(uiVar, variables, statement));
    return true;
  }

  public void remove(String ui) {
    uiInfo.remove(ui);
  }

  public void rename(String oldName, String uiName) {
    GuiInfo gui = uiInfo.remove(oldName);
    uiInfo.put(uiName, gui);
  }

  public String getVariableType(String varName) {
    for (GuiInfo info : uiInfo.values()) {
      for (String str : info.getVariables()) {
        String[] sp = str.split("/");
        if (sp[0].equals(varName)) {
          return sp[1];
        }
      }
    }
    return null;
  }
}
