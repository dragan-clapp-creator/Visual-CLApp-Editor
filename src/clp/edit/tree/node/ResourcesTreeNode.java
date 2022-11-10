package clp.edit.tree.node;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import clp.edit.GeneralContext;
import clp.edit.PopupContext;
import clp.edit.PopupContext.Action;
import clp.edit.PopupContext.Argument;
import clp.edit.graphics.code.gui.GuiContext;
import clp.edit.graphics.code.gui.GuiHandlerDialog;
import clp.edit.graphics.code.java.JavaContext;
import clp.edit.graphics.code.java.bci.JavaBCIHandlerDialog;
import clp.edit.graphics.code.web.WebContext;
import clp.edit.graphics.code.web.WebContext.WebInfo;
import clp.edit.graphics.code.web.WebHandlerDialog;
import clp.edit.panel.TogglePanel;
import clp.edit.tree.node.util.IContent;
import clp.edit.tree.node.util.ResourcesContent;
import clp.edit.tree.node.util.ResourcesContent.CommonVariable;
import clp.edit.tree.node.util.ResourcesContent.EventVariable;
import clp.edit.tree.node.util.ResourcesContent.GraphVariable;
import clp.edit.tree.node.util.ResourcesContent.LibType;
import clp.edit.tree.node.util.ResourcesContent.MarkVariable;
import clp.edit.tree.node.util.ResourcesContent.UiVariable;
import clp.edit.tree.node.util.ResourcesContent.WeaveVariable;
import clp.edit.tree.node.util.ResourcesContent.WebVariable;
import clp.edit.util.ColorSet;
import clp.run.msc.MetaScenario;
import clp.run.res.CellEvent;
import clp.run.res.EventVisitor;
import clp.run.res.Resources;
import clp.run.res.Unit;
import clp.run.res.VarEvent;
import clp.run.res.VarType;
import clp.run.res.Variable;

public class ResourcesTreeNode extends ATreeNode implements ActionListener,FocusListener {

  private static final long serialVersionUID = -8499269080807958377L;

  private static final String[] types = { "BOOL", "FLOAT", "INT", "LONG", "DATE","TIME", "REF",  "STRING" };
  private static final String[] evtypes = { "EVENT", "CELL_EVENT" };

  private Resources res;

  private JavaContext jcontext;
  private GuiContext gcontext;
  private WebContext wcontext;

  private String[] unitList;

  private ArrayList<String> uiList;
  private ArrayList<String> bciList;
  private ArrayList<String> webList;

  private ColorSet info;  // this object's context
  private ResourcesContent content;
  private File currentFolder;

  private boolean isCreated;

  private JPanel container;
  private JPanel libPanel;
  private JPanel eventPanel;
  private JPanel markPanel;
  private JPanel varPanel;

  private JToggleButton libButton;


  enum ResourcesType {
    VARIABLES, LIBRARIES, EVENTS, MARKS, WEB, GRAPH, UI, WEAVING;
  }


  public ResourcesTreeNode(String name, Resources res, File ref, String source) {
    super(name, source, ref);
    this.res = res;
    this.res.setName(name);
    info = ColorSet.ResourcesProperties;
    initializeContext(res);
    currentFolder = new File(".");
  }

  //
  private void initializeContext(Resources res) {
    content = new ResourcesContent(res != null && res.getMetaScenario() != null);
    if (res != null) {
      for (Variable v : res.getVariables()) {
        content.addVariable(v);
      }
      content.setUsedLibraries(res.getUsedLib());
      content.setEvents(res.getEvents());
      content.setMarks(res.getMarks());
    }
    unitList = new String[Unit.values().length+1];
    unitList[0] = "";
    int i = 1;
    for (Unit u : Unit.values()) {
      unitList[i++] = u.getVal();
    }
    uiList = new ArrayList<>();
    gcontext = new GuiContext();
    bciList = new ArrayList<>();
    jcontext = new JavaContext();
    webList = new ArrayList<>();
    wcontext = new WebContext();
  }

  public void addVariable(Variable v) {
    content.addVariable(v);
  }

  public void updateContext(Resources res) {
    initializeContext(res);
  }

  @Override
  public Object getAssociatedObject() {
    return res;
  }

  @Override
  public void setProperties(JPanel panel) {
    if (!isCreated || panel.getComponentCount() == 0) {
      panel.add(createNamePanel());
      panel.add(createToggleButtonsPanel());
      panel.add(createCallButtonsPanel());
      container = new JPanel();
      container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
      panel.add(container);
      isCreated = true;
    }
    if (libPanel != null) {
      container.add(libPanel);
    }
    if (eventPanel != null) {
      container.add(eventPanel);
    }
    if (markPanel != null) {
      container.add(markPanel);
    }
    if (varPanel != null) {
      container.add(varPanel);
    }
  }

  //
  private JPanel createNamePanel() {
    JPanel jp = new JPanel();
    jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
    jp.add(new JLabel("Name: "));
    jp.add( createField("name", getName()) );
    if (!content.isAssigned()) {
      JLabel lbl = new JLabel("assigned to ");
      lbl.setBackground(info.getLight());
      jp.add(lbl);
      jp.add( createField("res", res.getMsc()) );
    }
    jp.add(Box.createHorizontalStrut(500));
    jp.setSize(jp.getPreferredSize());
    return jp;
  }

  //
  private JPanel createCallButtonsPanel() {
    JPanel jp = new JPanel();
    jp.setLayout(new FlowLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Global Complex Items");
    jp.setBorder(border);
    JButton uiButton = new JButton("GUI");
    uiButton.setToolTipText("Declare Graphical User Interface");
    jp.add(uiButton);
    uiButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GuiHandlerDialog dial = new GuiHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(uiList), gcontext);
        if (dial.isOk()) {
          GeneralContext.getInstance().setCodeDirty(true);
          uiList = (ArrayList<String>) dial.getUiList();
        }
      }
    });
    JButton bciButton = new JButton("BCI");
    bciButton.setToolTipText("Declare Byte-Code Injection");
    jp.add(bciButton);
    bciButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JavaBCIHandlerDialog dial = new JavaBCIHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(bciList), jcontext);
        if (dial.isOk()) {
          GeneralContext.getInstance().setCodeDirty(true);
          bciList = (ArrayList<String>) dial.getBciList();
        }
      }
    });
    JButton webButton = new JButton("WEB");
    webButton.setToolTipText("Declare Web Sender");
    jp.add(webButton);
    webButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        WebHandlerDialog dial = new WebHandlerDialog(GeneralContext.getInstance().getFrame(), new ArrayList<>(webList), wcontext);
        if (dial.isOk()) {
          GeneralContext.getInstance().setCodeDirty(true);
          webList = (ArrayList<String>) dial.getWebList();
        }
      }
    });
    JButton graphButton = new JButton("GRAPH");
    graphButton.setToolTipText("Declare AKDL Graph (not implemented yet)");
    jp.add(graphButton);
    graphButton.setEnabled(false);
    graphButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });
    return jp;
  }

  //
  private TogglePanel createToggleButtonsPanel() {
    TogglePanel jp = new TogglePanel();
    jp.setLayout(new FlowLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Global Simple Items");
    jp.setBorder(border);
    libButton = new JToggleButton("LIB");
    libButton.setToolTipText("Declare Used Library");
    jp.add(libButton);
    libButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeneralContext.getInstance().setCodeDirty(true);
        if (libButton.isSelected()) {
          libPanel = createUsedLibrariesPanel();
        }
        else {
          container.remove(libPanel);
          libPanel = null;
        }
        refresh();
      }
    });
    JToggleButton eventButton = new JToggleButton("EVENT");
    eventButton.setToolTipText("Declare Event");
    jp.add(eventButton);
    eventButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeneralContext.getInstance().setCodeDirty(true);
        if (eventButton.isSelected()) {
          eventPanel = createEventsPanel();
        }
        else {
          container.remove(eventPanel);
          eventPanel = null;
        }
        refresh();
      }
    });
    JToggleButton markButton = new JToggleButton("MARK");
    markButton.setToolTipText("Declare Marks");
    jp.add(markButton);
    markButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeneralContext.getInstance().setCodeDirty(true);
        if (markButton.isSelected()) {
          markPanel = createMarkssPanel();
        }
        else {
          container.remove(markPanel);
          markPanel = null;
        }
        refresh();
      }
    });
    JToggleButton varButton = new JToggleButton("VARIABLE");
    varButton.setToolTipText("Declare Simple Variable");
    jp.add(varButton);
    varButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeneralContext.getInstance().setCodeDirty(true);
        if (varButton.isSelected()) {
          varPanel = createVariablesPanel();
        }
        else {
          container.remove(varPanel);
          varPanel = null;
        }
        refresh();
      }
    });
    return jp;
  }

  //
  private JTextField createField(String lbl, String text) {
    JTextField field = new JTextField(8);
    field.setText(text);
    field.setBackground(info.getLight());
    field.setName(lbl);
    field.addActionListener(this);
    field.addFocusListener(this);
    return field;
  }

  //
  private JPanel createUsedLibrariesPanel() {
    final JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Used Libraries"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    jp.add(createAddButtonForLibraries(), c);

    DefaultListSelectionModel selModel = new DefaultListSelectionModel();
    selModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    DefaultListModel<String> listModel = new DefaultListModel<>();
    int i = 0;
    for (String path : content.getBins()) {
      listModel.add(i++, "BIN " + path);
    }
    for (String path : content.getJars()) {
      listModel.add(i++, "JAR " + path);
    }
    JList<String> list = new JList<String>(listModel);
    list.setSelectionModel(selModel);
    list.setBackground(info.getLight());
    c.gridy++;
    jp.add(list, c);

    if (!listModel.isEmpty()) {
      c.gridx++;
      jp.add(createRemoveButton(list), c);
    }

    return jp;
  }

  //
  private JButton createRemoveButton(JList<String> list) {
    JButton btn = new JButton("Remove Selection");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (String line : list.getSelectedValuesList()) {
          int index = content.getLibIndexOf(line.substring(4));
          content.removeLibrary(index);
        }
        container.remove(libPanel);
        libPanel = createUsedLibrariesPanel();
        refresh();
      }
    });
    return btn;
  }

  //
  private JPanel createAddButtonForLibraries() {
    final JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
            "Add Libraries"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    JButton selectFolder = new JButton("Select a BIN folder");
    selectFolder.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fc = new JFileChooser(currentFolder);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showDialog(jp, "Select a BIN folder");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          currentFolder = fc.getSelectedFile();
          content.addLibrary(currentFolder.getAbsolutePath(), LibType.BIN);
          container.remove(libPanel);
          libPanel = createUsedLibrariesPanel();
          refresh();
        }
      }
    });
    jp.add(selectFolder, c);
    c.gridx = 1;
    c.gridy = 0;
    JButton selectJar = new JButton("Select a JAR file");
    selectJar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fc = new JFileChooser(currentFolder);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        fc.addChoosableFileFilter(new FileFilter() {
          @Override
          public String getDescription() {
            return "JAR files";
          }
          @Override
          public boolean accept(File f) {
            return f.getName().endsWith(".jar");
          }
        });
        int returnVal = fc.showDialog(jp, "Select file");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          currentFolder = fc.getSelectedFile();
          content.addLibrary(currentFolder.getAbsolutePath(), LibType.JAR);
          container.remove(libPanel);
          libPanel = createUsedLibrariesPanel();
          refresh();
        }
      }
    });
    jp.add(selectJar, c);
    return jp;
  }

  //
  private JPanel createEventsPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Events"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillEventsHeader(jp, c);

    fillEvents(jp, c);

    return jp;
  }

  //
  protected void fillEventsHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("type");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("time");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("delay");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("unit");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("is cyclic");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("deletion");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);

    c.gridy = 1;
    JComboBox<String> combo = new JComboBox<>(evtypes);
    c.gridx = 0;
    jp.add(combo, c);
   
    JTextField tfn = new JTextField(10);
    c.gridx++;
    jp.add(tfn, c);
    
    JTextField tft = new JTextField(10);
    c.gridx++;
    jp.add(tft, c);
    
    JTextField tfd = new JTextField(10);
    c.gridx++;
    jp.add(tfd, c);

    JComboBox<String> unit = new JComboBox<>(unitList);
    c.gridx++;
    jp.add(unit, c);
    
    JCheckBox cb = new JCheckBox();
    c.gridx++;
    jp.add(cb, c);

    JButton btn = new JButton("add");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        GeneralContext.getInstance().setDirty();
        content.addEventFrom((String) combo.getSelectedItem(), tfn.getText(), tft.getText(), tfd.getText(), (String) unit.getSelectedItem(), cb.isSelected());
        refresh(jp);
      }
    });
    c.gridx++;
    jp.add(btn, c);
  }

  //
  protected void fillEvents(JPanel jp, GridBagConstraints c) {
    c.gridy++;
    JTextField tf;
    for (EventVariable var : content.getEventVariables()) {
      tf = new JTextField(var.getType());
      tf.setForeground(info.getLight());
      tf.setEnabled(false);
      c.gridx = 0;
      jp.add(tf, c);

      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      tf.setEnabled(false);
      c.gridx++;
      jp.add(tf, c);

      if (var.getTime() != null) {
        tf = new JTextField(var.getTime());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);

        tf = new JTextField(""+var.getDelay());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);

        tf = new JTextField(var.getUnit().getVal());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx++;
        jp.add(tf, c);

        JCheckBox cb = new JCheckBox();
        cb.setSelected(var.isCyclic());
        cb.setEnabled(false);
        tf.setBackground(info.getLight());
        c.gridx++;
        jp.add(cb, c);
      }
      c.gridx++;
      jp.add(createRemoveButton(jp, ResourcesType.EVENTS, ""+c.gridy), c);

      c.gridy++;
    }
  }

  //
  private JPanel createMarkssPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Marks"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillMarksHeader(jp, c);

    fillMarks(jp, c);

    return jp;
  }

  //
  protected void fillMarksHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("cell name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("marks");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("deletion");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);

    c.gridy = 1;
    JTextField tfn = new JTextField(10);
    c.gridx = 0;
    jp.add(tfn, c);
 
    JTextField tfm = new JTextField(50);
    c.gridx++;
    jp.add(tfm, c);

    JButton btn = new JButton("add");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        GeneralContext.getInstance().setDirty();
        content.addMarksFrom(tfn.getText(), tfm.getText());
        refresh(jp);
      }
    });
    c.gridx++;
    jp.add(btn, c);
  }

  //
  protected void fillMarks(JPanel jp, GridBagConstraints c) {
    c.gridy++;
    JTextField tf;
    for (MarkVariable var : content.getMarkVariables()) {
      tf = new JTextField(var.getName());
      tf.setBackground(info.getLight());
      tf.setEnabled(false);
      c.gridx = 0;
      jp.add(tf, c);
      tf = new JTextField(var.getMarks());
      tf.setBackground(info.getLight());
      tf.setEnabled(false);
      c.gridx++;
      jp.add(tf, c);
      c.gridx++;
      jp.add(createRemoveButton(jp, ResourcesType.MARKS, ""+c.gridy), c);

      c.gridy++;
    }
  }

  //
  private JPanel createVariablesPanel() {
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Variables"));
    jp.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    fillVariablesHeader(jp, c);

    fillVariables(jp, c);

    return jp;
  }

  //
  protected void fillVariablesHeader(JPanel jp, GridBagConstraints c) {
    c.gridy = 0;
    JTextField tf = new JTextField("type");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx = 0;
    jp.add(tf, c);
 
    tf = new JTextField("array");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("name");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
    
    tf = new JTextField("initial");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);
 
    tf = new JTextField("deletion");
    tf.setForeground(info.getLight());
    tf.setBackground(info.getDark());
    c.gridx++;
    jp.add(tf, c);

    c.gridy = 1;
    JComboBox<String> combo = new JComboBox<>(types);
    c.gridx = 0;
    jp.add(combo, c);
   
    JCheckBox cb = new JCheckBox();
    c.gridx++;
    jp.add(cb, c);

    JTextField tfn = new JTextField(10);
    c.gridx++;
    jp.add(tfn, c);
 
    JTextField tfi = new JTextField(10);
    c.gridx++;
    jp.add(tfi, c);

    JButton btn = new JButton("add");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        GeneralContext.getInstance().setDirty();
        content.addVariableFrom((String) combo.getSelectedItem(), cb.isSelected(), tfn.getText(), tfi.getText());
        refresh(jp);
      }
    });
    c.gridx++;
    jp.add(btn, c);
  }

  //
  private void refresh(JPanel jp) {
    container.remove(jp);
    if (jp == varPanel) {
      varPanel = createVariablesPanel();
    }
    else if (jp == markPanel) {
      markPanel = createMarkssPanel();
    }
    else if (jp == eventPanel) {
      eventPanel = createEventsPanel();
    }
    refresh();
  }

  //
  protected void fillVariables(JPanel jp, GridBagConstraints c) {
    c.gridy++;
    JTextField tf;
    for (CommonVariable var : content.getSimpleVariables()) {
      if (var.getType() != null) {
        tf = new JTextField(var.getType().getVal());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx = 0;
        jp.add(tf, c);
     
        JCheckBox cb = new JCheckBox();
        cb.setSelected(var.isArray());
        cb.setBackground(info.getLight());
        cb.setEnabled(false);
        c.gridx = 1;
        jp.add(cb, c);

        tf = new JTextField(var.getName());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx = 2;
        jp.add(tf, c);
     
        tf = var.getInitial() == null ?  new JTextField(10) : new JTextField(var.getInitial().toString());
        tf.setBackground(info.getLight());
        tf.setEnabled(false);
        c.gridx = 3;
        jp.add(tf, c);
      }
      c.gridx = 4;
      jp.add(createRemoveButton(jp, ResourcesType.VARIABLES, ""+c.gridy), c);

      c.gridy++;
    }
  }

  //
  private JButton createRemoveButton(JPanel jp, ResourcesType type, String id) {
    JButton btn = new JButton("remove");
    btn.setName(id);
    btn.setBackground(info.getLight());
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        int index = Integer.parseInt(b.getName())-1;
        switch (type) {
          case LIBRARIES:
            content.removeLibrary(index);
            break;
          case EVENTS:
            content.removeEvent(index);
            break;
          case MARKS:
            content.removeMark(index);
            break;

          default:
            content.removeVariable(index);
            break;
        }
        GeneralContext.getInstance().setDirty();
        refresh(jp);
      }
    });
    return btn;
  }

  @Override
  public Color getBackground() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return "Resources";
  }

  @Override
  public IContent getContent() {
    return content;
  }

  @Override
  public JMenu addChildContextMenu() {
    return null;
  }

  @Override
  public JMenu addWrapperContextMenu(ATreeNode tparent) {
    if (tparent instanceof MetaScenarioTreeNode) {
      return null;
    }
    JMenu menu = new JMenu("wrap with");
    menu.add(PopupContext.getInstance().createSubItem(this, "a File", Action.WRAP, Argument.FILE));
    menu.add(PopupContext.getInstance().createSubItem(this, "a META-SCENARIO", Action.WRAP, Argument.MSC));
    return menu;
  }

  @Override
  public boolean isWrapperForCandidate(ATreeNode candidate) {
    return false;
  }

  @Override
  public void removeReassigning(ATreeNode parent, ATreeNode newParent) {
    parent.remove(this);
    res.setMetaScenario((MetaScenario) newParent.getAssociatedObject());
    res.setMsc(null);
    content.setAssigned(true);
  }

  @Override
  public void removeDeassigning(ATreeNode parent) {
    parent.remove(this);
    res.setMsc(parent.getName());
    res.setMetaScenario(null);
    content.setAssigned(false);
  }

  @Override
  public void focusGained(FocusEvent e) {
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (updateField((JTextField) e.getSource())) {
      GeneralContext.getInstance().setCodeDirty(true);
    }
  }

  //
  private boolean updateField(JTextField field) {
    boolean isRename = false;
    boolean isAssign = false;
    switch (field.getName()) {
      case "name":
        isRename = !getName().equals(field.getText());
        setName(field.getText());
        ((Resources)getAssociatedObject()).setName(getName());
        break;
      case "res":
        isAssign = !field.getText().equals(res.getMsc());
        res.setMsc(field.getText());
        break;

      default:
        break;
    }
    super.updateNode(isRename, ResourcesTreeNode.this);
    return isRename || isAssign;
  }

  @Override
  public void focusLost(FocusEvent e) {
    if (updateField((JTextField) e.getSource())) {
      GeneralContext.getInstance().setCodeDirty(true);
    }
  }

  public String getExportSource() {
    String src = "resources " + getName() + " {\n";
    return fillContent(src, true);
  }

  @Override
  public String getSource() {
    String src = "resources " + getName() + " {\n";
    return fillContent(src, false);
  }

  @Override
  public String getUnassignedSource() {
    String src = "resources " + getName() + " assignTo "+res.getMsc() + " {\n";
    return fillContent(src, false);
  }

  //
  private String fillContent(String src, boolean isExport) {
    if (!content.getBins().isEmpty() || !content.getJars().isEmpty()) {
      src += gatherLibs();
    }
    for (CommonVariable var : content.getSimpleVariables()) {
      src += "        " + var.getDeclaration() + ";\n";
    }
    if (!content.getEventVariables().isEmpty()) {
      src += "        events {\n";
      for (int i=0; i<content.getEventVariables().size()-1; i++) {
        EventVariable var = content.getEventVariables().get(i);
        src += "        " + var.getDeclaration() + ",\n";
      }
      src += "        " + content.getEventVariables().get(content.getEventVariables().size()-1).getDeclaration() + "\n";
      src += "        }\n";
    }
    if (!content.getMarkVariables().isEmpty()) {
      src += "        marks {\n";
      for (int i=0; i<content.getMarkVariables().size()-1; i++) {
        MarkVariable var = content.getMarkVariables().get(i);
        src += "        " + var.getDeclaration() + ",\n";
      }
      src += "        " + content.getMarkVariables().get(content.getMarkVariables().size()-1).getDeclaration() + "\n";
      src += "        }\n";
    }
    for (GraphVariable var : content.getGraphVariables()) {
      src += "        " + var.getSentences() + ";\n";
    }
    for (UiVariable var : content.getUIVariables()) {
      src += "        " + var.getSentences() + "\n";
    }
    for (WeaveVariable var : content.getWeaveVariables()) {
      src += "        " + getDeclaration(var) + "\n";
    }
    for (WebVariable var : content.getWebVariables()) {
      src += "        " + var.getDeclaration() + "\n";
    }
    return src + "}";
  }

  //
  private String gatherLibs() {
    String src = "usedJavaList {\n";
    for (String s : content.getBins()) {
      src += "   bin \"" + s + "\"\n";
    }
    for (String s : content.getJars()) {
      src += "   jar \"" + s + "\"\n";
    }
    src += "}\n";
    return src;
  }

  public String getDeclaration(WeaveVariable var) {
    return var.getSentences();
  }

  /**
   * @return the info
   */
  public ColorSet getInfo() {
    return info;
  }

  public Collection<? extends WebInfo> getWebInfos() {
    return wcontext.getWebInfos().values();
  }

  public boolean containsControlVariables() {
    for (EventVariable ev : content.getEventVariables()) {
      if ("EVENT".equals( ev.getType() )) {
        if (ev.getTime() == null) {
          return true;
        }
      }
    }
    for (CommonVariable cv : content.getSimpleVariables()) {
      if (cv.getType() == VarType.TBOOL) {
        return true;
      }
    }
    return false;
  }

  public Collection<? extends String> getControlVariables() {
    ArrayList<String> buttons = new ArrayList<>();
    for (EventVariable ev : content.getEventVariables()) {
      if ("EVENT".equals( ev.getType() )) {
        if (ev.getTime() == null) {
          buttons.add(ev.getName());
        }
      }
    }
    for (CommonVariable cv : content.getSimpleVariables()) {
      if (cv.getType() == VarType.TBOOL) {
        buttons.add(cv.getName());
      }
    }
    return buttons;
  }

  //=================================================================

  static class RenderEventVisitor implements EventVisitor {

    private String definition;

    @Override
    public void visitCellEvent(CellEvent x) {
      definition = "CELL " + x.getCellName();
      if (x.isTime()) {
        definition += " " + x.getTime() + " " + x.getDelay() + " " + x.getUnit().getVal() + " " + x.getCycle();
      }
    }

    @Override
    public void visitVarEvent(VarEvent x) {
      definition = "VAR " + x.getName();
    }

    public String getDefinition() {
      return definition;
    }
  }
}
