package clp.edit.graphics.code.gui.elts;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import clp.edit.graphics.code.ClappInstruction;
import clp.edit.graphics.code.InstructionDialog;
import clp.edit.graphics.code.gui.GuiContextMenu;
import clp.edit.graphics.code.gui.GuiContextMenu.Element;
import clp.edit.graphics.code.gui.GuiHandlerDialog;
import clp.edit.graphics.code.gui.GuiProperties;
import clp.edit.graphics.code.gui.GuiTreeContainer;
import clp.edit.graphics.dial.GenericActionListener;
import clp.parse.CLAppParser;
import clp.parse.res.ui.UiVar;

public class GuiDialog extends JDialog implements InstructionDialog {

  private static final long serialVersionUID = -7847829983575549053L;

  private GuiProperties uiproperties;
  private GuiTreeContainer uitree;
  private JPanel uilayout;

  private GenericActionListener gal;

  private String uiName;

  private ClappInstruction instruction;

  private GuiInfo uiInfo;

  private GuiHandlerDialog caller;

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param guiHandlerDialog 
   */
  public GuiDialog(Frame owner, GuiHandlerDialog guiHandlerDialog) {
    super(owner, "Adding a UI handling", true);
    this.caller = guiHandlerDialog;
    setup(owner, null);
   }

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param uiInfo
   * @param guiHandlerDialog 
   */
  public GuiDialog(Frame owner, GuiInfo uiInfo, GuiHandlerDialog guiHandlerDialog) {
    super(owner, "Adding a UI handling", true);
    this.uiInfo = uiInfo;
    this.caller = guiHandlerDialog;
    setup(owner, uiInfo);
  }

  //
  private void setup(Frame owner, GuiInfo uiInfo) {
    if (owner != null) {
      Dimension parentSize = owner.getSize(); 
      Point p = owner.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    Dimension dim = new Dimension(880, 500);
    getContentPane().setPreferredSize(dim);
    fillContent(dim, uiInfo);
    setVisible(true);
  }

  //
  private void fillContent(Dimension dim, GuiInfo uiInfo) {
    uilayout = new JPanel();
    uilayout.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Layout"));
    uilayout.setLayout(new BoxLayout(uilayout, BoxLayout.Y_AXIS));

    uiproperties = new GuiProperties();
    uiproperties.setPreferredSize(new Dimension((int)(dim.getWidth()/3), (int)dim.getHeight()));
    if (uiInfo == null) {
      uitree = new GuiTreeContainer(this);
      uitree.setPreferredSize(new Dimension((int)(dim.getWidth()/3), (int)dim.getHeight()));
    }
    else {
      uitree = new GuiTreeContainer(this, uiInfo.getUiVar());
      refresh(uitree.getRoot());
    }
    JSplitPane rightPart = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, uitree, uilayout);
    JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, uiproperties, rightPart);
    JPanel control = new JPanel();
    JSplitPane topPart = new JSplitPane(JSplitPane.VERTICAL_SPLIT, control, pane);

    JButton okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);
    control.add(okButton);
    JButton cancel = new JButton("cancel");
    cancel.addActionListener(gal);
    control.add(cancel);

    getContentPane().add(topPart);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  public GuiProperties getProperties() {
    return uiproperties;
  }

  @Override
  public String getRefName() {
    return "UI";
  }

  @Override
  public String getInstructionContent() {
    if (instruction == null || instruction.getStatement() == null) {
      setupInstruction();
    }
    return instruction.getStatement();
  }

  private ArrayList<String> getVariables() {
    ArrayList<String> variables = new ArrayList<>();
    JTree tree = uitree.getTree();
    DefaultTreeModel data = (DefaultTreeModel) tree.getModel();
    GUIRoot root = (GUIRoot) data.getRoot();
    variables = root.getVariables();
    return variables;
  }

  @Override
  public String getInstructionName() {
    JTree tree = uitree.getTree();
    DefaultTreeModel data = (DefaultTreeModel) tree.getModel();
    GUIRoot root = (GUIRoot) data.getRoot();
    uiName = root.getName();
    return uiName;
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }

  public void displayButtonContext(Component invoker, int x, int y, GUIButton obj) {
    GuiContextMenu pop = new GuiContextMenu(true, null, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayFieldContext(Component invoker, int x, int y, GUIField obj) {
    GuiContextMenu pop = new GuiContextMenu(true, null, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayGroupContext(Component invoker, int x, int y, GUIGroup obj) {
    ArrayList<Element> list = new ArrayList<>();
    list.add(Element.LINE);
    GuiContextMenu pop = new GuiContextMenu(true, list, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayLabelContext(Component invoker, int x, int y, GUILabel obj) {
    GuiContextMenu pop = new GuiContextMenu(true, null, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayLineContext(Component invoker, int x, int y, GUILine obj) {
    ArrayList<Element> list = new ArrayList<>();
    list.add(Element.LABEL);
    list.add(Element.FIELD);
    list.add(Element.BUTTON);
//    list.add(Element.TABLE);
    list.add(Element.TEXT);
    GuiContextMenu pop = new GuiContextMenu(true, list, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayRootContext(Component invoker, int x, int y, GUIRoot obj) {
    ArrayList<Element> list = new ArrayList<>();
    list.add(Element.GROUP);
    GuiContextMenu pop = new GuiContextMenu(false, list, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayTableContext(Component invoker, int x, int y, GUITable obj) {
    GuiContextMenu pop = new GuiContextMenu(true, null, obj, this);
    pop.show(invoker, x, y);
  }

  public void displayTextAreaContext(Component invoker, int x, int y, GUITextArea obj) {
    GuiContextMenu pop = new GuiContextMenu(true, null, obj, this);
    pop.show(invoker, x, y);
  }

  //
  public void refresh(AguiLeaf n) {
    JTree tree = uitree.getTree();
    DefaultTreeModel data = (DefaultTreeModel) tree.getModel();
    data.reload();

    TreePath path = new TreePath(data.getPathToRoot(n));
    tree.scrollPathToVisible(path);
    tree.setSelectionPath(path);

    DefaultMutableTreeNode root = (DefaultMutableTreeNode) data.getRoot();
    uilayout.removeAll();
    createGroupsLayout(uilayout, (GUIGroup) root.getFirstChild());
  }

  //
  private void createGroupsLayout(JPanel uilayout, GUIGroup n) {
    if (n != null) {
      if (!n.isLeaf()) {
        JPanel jp = new JPanel();
        jp.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createBevelBorder(EtchedBorder.RAISED),
                "GROUP "+n.getName()));
        jp.setLayout(new GridBagLayout());
        processLines(jp, (GUILine) n.getFirstChild());
        uilayout.add(jp);
      }
      createGroupsLayout(uilayout, (GUIGroup) n.getNextSibling());
    }
  }

  //
  private void processLines(JPanel jp, GUILine n) {
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.gridy = 0;
    while (n != null) {
      c.gridx = 0;
      if (!n.isLeaf()) {
        createLayoutElements(jp, c, (AguiLeaf) n.getFirstChild());
      }
      n = (GUILine) n.getNextSibling();
      c.gridy++;
    }
  }

  //
  private void createLayoutElements(JPanel jp, GridBagConstraints c, AguiLeaf n) {
    while (n != null) {
      jp.add(n.createUIElement(), c);
      n = (AguiLeaf) n.getNextSibling();
      c.gridx++;
    }
  }

  @Override
  public void reset() {
    instruction.reset();
  }

  @Override
  public ClappInstruction getInstruction() {
    return instruction;
  }

  @Override
  public void setInstruction(ClappInstruction inst) {
    instruction = inst;
  }

  @Override
  public boolean setupInstruction() {
    instruction = new ClappInstruction();
    JTree tree = uitree.getTree();
    DefaultTreeModel data = (DefaultTreeModel) tree.getModel();
    GUIRoot root = (GUIRoot) data.getRoot();
    uiName = root.getName();
    if (uiName == null) {
      System.err.println("ERROR: please enter an identifier (UI name)");
      return false;
    }
    String statement = "UI " + uiName + root.getContent();
    instruction.setStatement(statement);
    UiVar pui = new UiVar();
    InputStream is = new ByteArrayInputStream(statement.getBytes(), 0, statement.length());
    CLAppParser parser = new CLAppParser(new BufferedReader(new InputStreamReader(is)));
    try {
      pui.parse(parser, false);
      if (uiInfo != null) {
        String oldName = uiInfo.getUiVar().getName();
        uiInfo.setUiVar(pui.getUiVar());
        uiInfo.setVariables(getVariables());
        uiInfo.setDeclarationStatement(statement);
        if (!oldName.equals(uiName)) {
          caller.getGuiContext().rename(oldName, uiName);
          caller.reloadUiList();
        }
        return true;
      }
      return caller.getGuiContext().addUiInfo(uiName, pui.getUiVar(), getVariables(), statement);
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
   }
  }

  /**
   * @return the uiproperties
   */
  public GuiProperties getUiproperties() {
    return uiproperties;
  }

  /**
   * @return the uitree
   */
  public GuiTreeContainer getUitree() {
    return uitree;
  }
}
