package clp.edit.tree.node;

import java.io.File;

import javax.swing.JPanel;

import clp.run.cel.Heap;

public class DisplayHeapTreeNode extends HeapTreeNode {

  private static final long serialVersionUID = -1548416018237467528L;

  public DisplayHeapTreeNode(String name, Heap h, File ref, String source) {
    super(name, h, ref, source);
  }

  @Override
  public void setProperties(JPanel panel) {
    super.setProperties(panel);
    disablePanelContents(panel);
  }
}
