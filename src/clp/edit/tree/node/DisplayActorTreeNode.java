package clp.edit.tree.node;

import java.io.File;

import javax.swing.JPanel;

import clp.run.act.Actor;

public class DisplayActorTreeNode extends ActorTreeNode {

  private static final long serialVersionUID = 2820432158042382724L;

  public DisplayActorTreeNode(String name, Actor a, File ref, String source) {
    super(name, a, ref, source);
  }

  @Override
  public void setProperties(JPanel panel) {
    super.setProperties(panel);
    disablePanelContents(panel);
  }
}
