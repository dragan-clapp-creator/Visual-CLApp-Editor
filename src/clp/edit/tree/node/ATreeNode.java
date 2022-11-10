package clp.edit.tree.node;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import clp.edit.GeneralContext;
import clp.edit.PopupContext.PasteStatus;
import clp.edit.panel.ControlPanel;
import clp.edit.panel.PropertiesPanel;
import clp.edit.panel.TogglePanel;
import clp.edit.tree.node.util.IContent;

abstract public class ATreeNode extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 6413496949025575370L;

  private String name;
  private String source;
  private File fileReference;

  abstract public Object getAssociatedObject();
  abstract public IContent getContent();
  abstract public void setProperties(JPanel panel);
  abstract public Color getBackground();
  abstract public String getToolTipText();

  abstract public JMenu addChildContextMenu();
  abstract public JMenu addWrapperContextMenu(ATreeNode tparent);
  abstract public boolean isWrapperForCandidate(ATreeNode candidate);

  abstract public void removeReassigning(ATreeNode parent, ATreeNode newParent);
  abstract public void removeDeassigning(ATreeNode parent);

  abstract public String getSource();
  abstract public String getUnassignedSource();

  /**
   * CONSTRUCTOR
   * 
   * @param name
   * @param source
   * @param ref
   */
  public ATreeNode(String name, String source, File ref) {
    this.name = name;
    this.source = source;
    this.fileReference = ref;
  }

  public String getIcon() {
    if (GeneralContext.getInstance().isRuntimeTriggered()) {
      return getFileReference() == null ? null : "orange";
    }
    return getFileReference() == null ? null : "black";
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  public Font getFont() {
    return new Font("Monospaced", Font.BOLD, 14);
  }

  public String toString() {
    return getName();
  }
  /**
   * @return the source
   */
  public String getOriginalSource() {
    return source;
  }
  /**
   * @return the fileReference
   */
  public File getFileReference() {
    if (this instanceof FileTreeNode) {
      return fileReference;
    }
    return ((ATreeNode)getParent()).getFileReference();
  }
  /**
   * @param fileReference the fileReference to set
   */
  public void setFileReference(File fileReference) {
    if (this instanceof FileTreeNode) {
      this.fileReference = fileReference;
    }
    else {
      ((ATreeNode)getParent()).setFileReference(fileReference);
    }
  }

  public void populate(PropertiesPanel propertiesPanel, ControlPanel controlPanel) {
  }


  public void disablePanelContents(JPanel panel) {
    for (int i=0; i<panel.getComponentCount(); i++) {
      Component cp = panel.getComponent(i);
      if (cp instanceof TogglePanel) {
        continue;
      }
      else if (cp instanceof JPanel) {
        disablePanelContents((JPanel) cp);
      }
      else if (!(cp instanceof JLabel)) {
        cp.setEnabled(false);
      }
    }
  }

  public ATreeNode cloneWithSuffix() {
    ATreeNode copy = (ATreeNode) super.clone();
    copy.setName(name + "$");
    for (int i=0; i<getChildCount(); i++) {
      ATreeNode child = (ATreeNode) getChildAt(i);
      copy.add(child.cloneWithSuffix());
    }
    return (ATreeNode) copy;
  }

  public PasteStatus getPasteStatus(ATreeNode currentParent) {
    if (currentParent.isWrapperForCandidate(this)) {
      return PasteStatus.ALLOWED_IN;
    }
    if (currentParent instanceof FileTreeNode) {
      return  PasteStatus.ALLOWED_OUT;
    }
    return PasteStatus.NOT_ALLOWED;
  }

  public void updateNode(boolean isRename, ATreeNode node) {
    if (isRename) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          GeneralContext.getInstance().setSelection((ATreeNode) node.getParent());
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              GeneralContext.getInstance().setSelection(node);
            }
          });
        }
      });
    }
  }
  /**
   * @param source the source to set
   */
  public void setSource(String source) {
    this.source = source;
  }

  public void refresh(ATreeNode node) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        GeneralContext.getInstance().getPropertiesPanel().setNodesProperties(node);
      }
    });
  }

  public void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        PropertiesPanel panel = GeneralContext.getInstance().getPropertiesPanel();
        setProperties(panel);
        GeneralContext.getInstance().updateClappPanel();
      }
    });
  }

  public boolean hasWrapperForCandidate(ATreeNode candidate) {
    for (int i=0; i<getChildCount(); i++) {
      ATreeNode child = (ATreeNode) getChildAt(i);
      if (child.isWrapperForCandidate(candidate) || child.getChildCount() > 0 && child.hasWrapperForCandidate(candidate)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void remove(MutableTreeNode aChild) {
    try {
      super.remove(aChild);
    }
    catch(IllegalArgumentException e) {}
  }
}
