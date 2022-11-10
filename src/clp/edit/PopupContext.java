package clp.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.ActorTreeNode;
import clp.edit.tree.node.CellTreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.HeapTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.tree.node.ResourcesTreeNode;
import clp.edit.tree.node.ScenarioTreeNode;
import clp.edit.tree.node.SetterTreeNode;
import clp.run.act.Actor;
import clp.run.cel.Cell;
import clp.run.cel.Heap;
import clp.run.msc.MetaScenario;
import clp.run.msc.MetaScenarioBody;
import clp.run.msc.MscTaskName;
import clp.run.msc.MscTasks;
import clp.run.res.Resources;
import clp.run.res.Setter;
import clp.run.scn.DeactType;
import clp.run.scn.Scenario;
import clp.run.scn.ScenarioBody;
import clp.run.scn.ScnDeact;
import clp.run.scn.ScnLevel;
import clp.run.scn.ScnLogBody;
import clp.run.scn.ScnLogic;
import clp.run.scn.ScnPropBody;
import clp.run.scn.ScnQueues;
import clp.run.scn.ScnTasks;

public class PopupContext {

  private static final PopupContext instance = new PopupContext();

  static public PopupContext getInstance() {
    return instance;
  }

  static public enum Action {
    MOVE_UP, MOVE_DOWN,
    MOVE_UP_IN, MOVE_DOWN_IN,
    MOVE_UP_IN_OTHER, MOVE_DOWN_IN_OTHER,
    MOVE_UP_OUT, MOVE_DOWN_OUT,
    INSERT, REMOVE, WRAP, REPLACE,
    COPY, CUT, PASTE,
    FIND, FIND_IGNORED, SOURCE;
  }

  static public enum Argument {
    FILE, SET, MSC, RES, SCN, ACT, HEAP, CELL, GREP;
  }

  static public enum PasteStatus {
    NOT_ALLOWED, ALLOWED_IN, ALLOWED_OUT;
  }

  /**
   * PRIVATE CONSTRUCTOR
   */
  private PopupContext() {
    selection = new ArrayList<>();
  }

  private ArrayList<ATreeNode> selection;

  private static List<ATreeNode> cutOrCopy = new ArrayList<>();
  private static boolean isCut;


  public boolean isListEmpty() {
    return cutOrCopy.isEmpty();
  }

  public JMenu createAndAddMenu(String name, Action act) {
    JMenu menu = new JMenu(name);
    menu.setName(act.name());
    return menu;
  }

  public JMenuItem createAndAddItem(ATreeNode tnode, String name, Action act) {
    JMenuItem item = new JMenuItem(name);
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (performActionFromPopup(tnode, act)) {
          GeneralContext.getInstance().setDirty();
        }
      }
    });
    return item;
  }

  public JMenuItem createSubItem(ATreeNode tnode, String name, Action act, Argument sub) {
    JMenuItem item = new JMenuItem(name);
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (performActionFromPopup(tnode, act, sub)) {
          GeneralContext.getInstance().setDirty();
        }
      }
    });
    return item;
  }

  public boolean performActionFromPopup(ATreeNode tnode, Action action) {
    if (!selection.contains(tnode)) {
      selection.add(0, tnode);
    }
    boolean isRefresh = true;
    switch (action) {
      case COPY:
        cutOrCopy.addAll(selection);
        isCut = false;
        break;
      case CUT:
        cutOrCopy.addAll(selection);
        isCut = true;
        break;
      case PASTE:
        processCutOrCopy();
        cutOrCopy.clear();
        break;
      case MOVE_DOWN:
        processMoveDown();
        break;
      case MOVE_UP:
        processMoveUp();
        break;
      case MOVE_DOWN_OUT:
        processMoveDownOut();
        break;
      case MOVE_UP_OUT:
        processMoveUpOut();
        break;
      case REMOVE:
        processRemove();
        isRefresh = false;
        break;
      case MOVE_DOWN_IN:
        processMoveDownIn();
        break;
      case MOVE_UP_IN:
        processMoveUpIn();
        break;
      case MOVE_DOWN_IN_OTHER:
        processMoveDownInOther();
        break;
      case MOVE_UP_IN_OTHER:
        processMoveUpInOther();
        break;
      default:
        isRefresh = false;
        break;
    }
    if (isRefresh) {
      invokeLaterSelect(tnode);
    }
    return isRefresh;
  }

  //
  private void processCutOrCopy() {
    ATreeNode tnode = null;
    ATreeNode currentParent = selection.get(0);
    ATreeNode previousParent = isCut ? (ATreeNode) cutOrCopy.get(0).getParent() : null;
    if (!isCut || previousParent != null && currentParent != previousParent) {
      for (ATreeNode node : cutOrCopy) {
        tnode = node;
        PasteStatus status = node.getPasteStatus(currentParent);
        if (status == PasteStatus.NOT_ALLOWED) {
          System.err.println(currentParent + "cannot be parent of " + node);
        }
        else {
          if (isCut) {
            if (status == PasteStatus.ALLOWED_IN) {
              if (currentParent instanceof FileTreeNode) {
                previousParent.remove(node);
              }
              else {
                node.removeReassigning(previousParent, currentParent);
              }
              currentParent.add(node);
            }
            else {
              node.removeDeassigning(currentParent);
            }
          }
          else {
            ATreeNode copy = node.cloneWithSuffix();
            currentParent.add(copy);
          }
          node.setParent(currentParent);
        }
      }
      invokeLaterSelect(tnode);
    }
  }

  //
  private void processMoveDown() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      int i = parent.getIndex(tnode);
      int size = selection.size();
      for (int c=0; c<size; c++) {
        parent.remove(i);
      }
      for (int c=size-1; c>=0; c--) {
        parent.insert(selection.get(c), i + 1);
      }
    }
  }

  //
  private void processMoveUp() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      int i = parent.getIndex(tnode);
      int size = selection.size();
      for (int c=0; c<size; c++) {
        parent.remove(i);
      }
      for (int c=size-1; c>=0; c--) {
        parent.insert(selection.get(c), i - 1);
      }
    }
  }

  //
  private void processMoveDownIn() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      int i = parent.getIndex(tnode);
      ATreeNode newParent = (ATreeNode) parent.getChildAt(i + 1);
      int size = selection.size();
      for (int c=0; c<size; c++) {
        selection.get(c).removeReassigning(parent, newParent);
      }
      for (int c=size-1; c>=0; c--) {
        newParent.insert(selection.get(c), 0);
      }
    }
  }

  //
  private void processMoveUpIn() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      int i = parent.getIndex(tnode);
      ATreeNode newParent = (ATreeNode) parent.getChildAt(i - 1);
      int size = selection.size();
      for (int c=0; c<size; c++) {
        selection.get(c).removeReassigning(parent, newParent);
      }
      for (int c=0; c<size; c++) {
        newParent.add(selection.get(c));
      }
    }
  }

  //
  private void processMoveDownInOther() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      int i = parent.getIndex(tnode);
      ATreeNode newParent = findWrapper((ATreeNode) parent.getChildAt(i + 1), tnode);
      if (newParent != null) {
        int size = selection.size();
        for (int c=0; c<size; c++) {
          selection.get(c).removeReassigning(parent, newParent);
        }
        for (int c=size-1; c>=0; c--) {
          newParent.insert(selection.get(c), 0);
        }
      }
    }
  }

  //
  private void processMoveUpInOther() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      int i = parent.getIndex(tnode);
      ATreeNode newParent = findWrapper((ATreeNode) parent.getChildAt(i - 1), tnode);
      if (newParent != null) {
        int size = selection.size();
        for (int c=0; c<size; c++) {
          selection.get(c).removeReassigning(parent, newParent);
        }
        for (int c=0; c<size; c++) {
          newParent.add(selection.get(c));
        }
      }
    }
  }

  //
  private ATreeNode findWrapper(ATreeNode p, ATreeNode tnode) {
    for (int i=0; i<p.getChildCount(); i++) {
      ATreeNode c = (ATreeNode) p.getChildAt(i);
      if (c.isWrapperForCandidate(tnode)) {
        return c;
      }
      if (c.getChildCount() > 0) {
        c = findWrapper(c, tnode);
        if (c != null) {
          return c;
        }
      }
    }
    return null;
  }

  //
  private void processMoveDownOut() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      ATreeNode granpa = (ATreeNode) parent.getParent();
      if (granpa != null) {
        int i = granpa.getIndex(parent);
        int size = selection.size();
        for (int c=0; c<size; c++) {
          selection.get(c).removeDeassigning(parent);
        }
        for (int c=size-1; c>=0; c--) {
          granpa.insert(selection.get(c), i + 1);
        }
      }
    }
  }

  //
  private void processMoveUpOut() {
    ATreeNode tnode = selection.get(0);
    ATreeNode parent = (ATreeNode) tnode.getParent();
    if (parent != null) {
      ATreeNode granpa = (ATreeNode) parent.getParent();
      if (granpa != null) {
        int i = granpa.getIndex(parent);
        int size = selection.size();
        for (int c=0; c<size; c++) {
          selection.get(c).removeDeassigning(parent);
        }
        for (int c=size-1; c>=0; c--) {
          granpa.insert(selection.get(c), i);
        }
      }
    }
  }

  //
  private void processRemove() {
    ATreeNode parent = (ATreeNode) selection.get(0).getParent();
    if (parent != null) {
      for (int i = selection.size() - 1; i >= 0; i--) {
        ATreeNode node = selection.get(i);
        parent.remove(node);
      }
      invokeLaterSelect(parent);
    }
  }

  //
  private void invokeLaterSelect(ATreeNode node) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (node != null) {
          GeneralContext.getInstance().setSelection(node);
          GeneralContext.getInstance().setCodeDirty(true);
        }
      }
    });
  }

  public boolean performActionFromPopup(ATreeNode tnode, Action action, Argument arg) {
    if (!selection.contains(tnode)) {
      selection.add(tnode);
    }
    ATreeNode node = createNodeFromArgument(arg, tnode);
    if (node != null) {
      switch (action) {
        case INSERT:
          if (arg != Argument.GREP) {
            tnode.add(node);
            if (tnode instanceof ProjectTreeNode) {
              ((ProjectTreeNode)tnode).insertNodeInfo((FileTreeNode) node, arg);
            }
          }
          GeneralContext.getInstance().setSelection(node);
          break;
        case WRAP:
          boolean isFile = false;
          ATreeNode parent = (ATreeNode) tnode.getParent();
          if (node instanceof FileTreeNode) {
            if (!(parent instanceof FileTreeNode)) {
              break;
            }
            ((ProjectTreeNode)parent.getParent()).add(node);
            isFile = true;
          }
          ArrayList<ATreeNode> children = new ArrayList<>();
          for (int i=0; i<parent.getChildCount(); i++) {
            ATreeNode child = (ATreeNode) parent.getChildAt(i);
            if (!selection.contains(child)) {
              children.add(child);
            }
          }
          parent.removeAllChildren();
          for (ATreeNode child : children) {
            parent.add(child);
          }
          for (ATreeNode tn : selection) {
            if (!isFile) {
              tn.removeReassigning(parent, node);
            }
            node.add(tn);
          }
          if (!isFile) {
            parent.add(node);
            
          }
          GeneralContext.getInstance().setSelection(node);
          break;
        default:
          break;
      }
    }
    selection.clear();
    return node != null;
  }

  //
  private ATreeNode createNodeFromArgument(Argument arg, ATreeNode tnode) {
    ATreeNode node = null;
    if (arg == Argument.FILE) {
      node = new FileTreeNode(new File("./NoName.clp"), null);
    }
    else if (arg == Argument.CELL) {
      node = new CellTreeNode("Cell", new Cell());
    }
    else {
      File ref = tnode.getFileReference();
      switch (arg) {
        case RES:
          Resources res = new Resources();
          if (tnode instanceof MetaScenarioTreeNode) {
            res.setMetaScenario((MetaScenario) tnode.getAssociatedObject());
          }
          node = new ResourcesTreeNode("Resources", res, ref, null);
          break;
        case SET:
          node = new SetterTreeNode(new Setter(), "Setter", ref, null);
          break;
        case MSC:
          MetaScenario msc = new MetaScenario();
          msc.setName("Meta_Scenario");
          MetaScenarioBody mb = new MetaScenarioBody();
          MscTasks mtasks = new MscTasks();
          mtasks.addMscTaskName(MscTaskName.SCHEDULER);
          mb.setMscTasks(mtasks);
          msc.setMetaScenarioBody(mb);
          node = new MetaScenarioTreeNode("Meta_Scenario", msc, ref, null);
          break;
        case SCN:
          Scenario scn = new Scenario();
          ScenarioBody sb = new ScenarioBody();
          ScnPropBody spb = new ScnPropBody();
          ScnLogic sl = new ScnLogic();
          ScnLogBody slb = new ScnLogBody();
          slb.setScnLevel(new ScnLevel());
          ScnDeact sd = new ScnDeact();
          sd.setDeactType(DeactType.MANUAL);
          slb.setScnDeact(sd);
          sl.setScnLogBody(slb);
          spb.setScnLogic(sl);
          spb.setScnQueues(new ScnQueues());
          spb.setScnTasks(new ScnTasks());
          sb.setScnPropBody(spb);
          scn.setScenarioBody(sb);
          if (tnode instanceof MetaScenarioTreeNode) {
            scn.setMetaScenario((MetaScenario) tnode.getAssociatedObject());
          }
          node = new ScenarioTreeNode("Scenario", scn, ref, null);
          break;
        case ACT:
          Actor act = new Actor();
          if (tnode instanceof ScenarioTreeNode) {
            act.setScenario((Scenario) tnode.getAssociatedObject());
          }
          node = new ActorTreeNode("Actor", act, ref, null);
          break;
        case HEAP:
          Heap heap = new Heap();
          if (tnode instanceof ActorTreeNode) {
            heap.setActor((Actor) tnode.getAssociatedObject());
          }
          node = new HeapTreeNode("Heap", heap, ref, null);
          break;
        case GREP:
          node = processGrep(tnode);
          break;
        default:
          break;
      }
    }
    return node;
  }

  //
  private ATreeNode processGrep(ATreeNode tnode) {
    ATreeNode node = null;
    while (!(node instanceof CellTreeNode)) {
      Argument arg = getUnderlyingArgument(tnode);
      node = createNodeFromArgument(arg, tnode);
      tnode.add(node);
      tnode = node;
    }
    return node;
  }

  //
  private Argument getUnderlyingArgument(ATreeNode tnode) {
    if (tnode instanceof FileTreeNode) {
      return Argument.MSC;
    }
    if (tnode instanceof MetaScenarioTreeNode) {
      return Argument.SCN;
    }
    if (tnode instanceof ScenarioTreeNode) {
      return Argument.ACT;
    }
    if (tnode instanceof ActorTreeNode) {
      return Argument.HEAP;
    }
    if (tnode instanceof HeapTreeNode) {
      return Argument.CELL;
    }
    return null;
  }

  public void setSelection(ArrayList<ATreeNode> selection) {
    this.selection = selection;
  }
}
