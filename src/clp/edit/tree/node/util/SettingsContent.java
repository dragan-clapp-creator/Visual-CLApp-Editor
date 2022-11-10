package clp.edit.tree.node.util;

import java.util.ArrayList;

import clp.edit.tree.node.util.ResourcesContent.CommonVariable;
import clp.edit.tree.node.util.ResourcesContent.GraphVariable;
import clp.edit.tree.node.util.ResourcesContent.UiVariable;
import clp.edit.tree.node.util.ResourcesContent.WeaveVariable;
import clp.edit.tree.node.util.ResourcesContent.WebVariable;
import clp.run.msc.ClassReference;
import clp.run.res.Setting;

public class SettingsContent implements IContent {

  private ArrayList<Setting> clappSettings;

  private ArrayList<CommonVariable> simpleVars;
  private ArrayList<UiVariable> uiVars;
  private ArrayList<WebVariable> webVars;
  private ArrayList<WeaveVariable> weaveVars;
  private ArrayList<GraphVariable> graphVars;

  public SettingsContent() {
    simpleVars = new ArrayList<>();
    uiVars = new ArrayList<>();
    webVars = new ArrayList<>();
    weaveVars = new ArrayList<>();
    graphVars = new ArrayList<>();
  }

  /**
   * @param settings the (existing or new) variables to set
   */
  public void setSettings(ArrayList<Setting> settings) {
    this.clappSettings = settings;
    extractSettings();
  }

  //
  private void extractSettings() {
    for (Setting set : clappSettings) {
      ClpGetSettingVisitor vis = new ClpGetSettingVisitor();
      set.accept(vis);
      ClassReference cr;
      switch (vis.getVarType()) {
        case TUI:
          UiVariable uv = new UiVariable();
          uiVars.add(uv);
          break;
        case TGRAPH:
          GraphVariable gv = new GraphVariable();
          gv.setName(vis.getName());
          gv.setSentences(vis.getSentences());
          graphVars.add(gv);
          break;
        case TWEAVER:
          WeaveVariable bv = new WeaveVariable();
          bv.setName(vis.getName());
          bv.setPackCst(vis.getPack().getCst());
          bv.setPack(vis.getPack().getId());
          bv.setClazzCst(vis.getClazz().getCst());
          bv.setClazz(vis.getClazz().getId());
          bv.setItems(vis.getItems());
          weaveVars.add(bv);
          break;
        case TWEB:
          WebVariable wv = new WebVariable();
          wv.setPort(vis.getPort());
          wv.setName(vis.getName());
          cr = vis.getEncryption().getClazz();
          if (cr != null) {
            String address = cr.getPack() + "." + cr.getClazz();
            wv.setAddress(address);
          }
          if (vis.getEncryption() != null) {
            cr = vis.getEncryption().getClazz();
            String encryption = cr.getPack() + "." + cr.getClazz();
            wv.setEncryption(encryption);
          }
          webVars.add(wv);
          break;

        default:
          CommonVariable cv = new CommonVariable();
          cv.setName(vis.getName());
          cv.setType(vis.getVarType());
          cv.setInitial(vis.getValue());
          cv.setArray(vis.hasArray());
          simpleVars.add(cv);
          break;
      }
    }
  }

  public ArrayList<CommonVariable> getSimpleVariables() {
    return simpleVars;
  }

  public void removeVariable(int index) {
    // TODO Auto-generated method stub
    
  }

  public ArrayList<WebVariable> getWebVariables() {
    return webVars;
  }

  public ArrayList<WeaveVariable> getWeaveVariables() {
    return weaveVars;
  }

  public ArrayList<GraphVariable> getGraphVariables() {
    return graphVars;
  }

  public ArrayList<UiVariable> getUIVariables() {
    return uiVars;
  }

  @Override
  public boolean isAssigned() {
    return true;
  }
}
