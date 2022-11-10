package clp.edit.handler;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import clp.edit.CLAppEditor;
import clp.edit.dialog.AboutDialog;
import clp.edit.dialog.ColorsDialog;
import clp.edit.dialog.HelpDialog;
import clp.edit.dialog.RecentlyOpenedDialog;
import clp.edit.dialog.SaveConfirmationOnLeave;


public class MenuHandler extends JMenuBar {

  private static final long serialVersionUID = 2903601541693798151L;

  private JMenuItem ccreate;    // create clapp project
  private JMenuItem fcreate;    // create flow chart project
  private JMenuItem open;
  private JMenuItem save;
  private JMenuItem saveAs;
  private JMenuItem export;

  private JMenu recent;

  private JMenuItem about;
  private JMenuItem cpreferences;
  private JMenuItem rpreferences;
  private JMenuItem help;
  private JMenuItem exit;

  private CLAppEditor editor;

  transient private RecentHandler rhandler;
  transient private ColorsHandler chandler;


  public void initialize(CLAppEditor editor) {
    this.editor = editor;

    rhandler = new RecentHandler();
    rhandler.populateRecentFiles();

    chandler = new ColorsHandler();
    chandler.retreivePreferredColors();

    ActionHandler ahandler = new ActionHandler();

    // Menu elements
    JMenu project = new JMenu("Project ");
    JMenu create = ahandler.createWithAction("New...", KeyEvent.VK_N, editor);
      ccreate = ahandler.createItemWithAction("CLApp Project", KeyEvent.VK_K, editor);
      fcreate = ahandler.createItemWithAction("Flow Chart Project", KeyEvent.VK_F, editor);
      create.add(ccreate);
      create.add(fcreate);
    open = ahandler.createItemWithAction("Open", KeyEvent.VK_O, editor);
    save = ahandler.createItemWithAction("Save", KeyEvent.VK_S, editor);
      save.setEnabled(false);
    saveAs = ahandler.createItemWithAction("Save As...", KeyEvent.VK_W, editor);
      saveAs.setEnabled(false);
    export = ahandler.createItemWithAction("Export...", KeyEvent.VK_E, editor);
      export.setEnabled(false);
  add(project);
    project.add(create);
    project.add(open);
    project.add(save);
    project.add(saveAs);
    project.addSeparator();
    project.add(export);
    project.addSeparator();
  recent = new JMenu("Recently opened...");
    project.add(recent);
    ArrayList<File> files = rhandler.getFiles();
    if (files != null && !files.isEmpty()) {
      fillRecent(files);
    }
    else {
      recent.setEnabled(false);
    }

  JMenu clapp = new JMenu("CLApp");
    cpreferences = ahandler.createItemWithAction("Color Preferences...", KeyEvent.VK_P, editor);
    rpreferences = ahandler.createItemWithAction("Recently Opened Preferences...", KeyEvent.VK_R, editor);
    exit = new JMenuItem("Exit");
      exit.addActionListener(editor);
    help = ahandler.createItemWithAction("Help", KeyEvent.VK_H, editor);
    about = ahandler.createItemWithAction("About", KeyEvent.VK_A, editor);
  add(clapp);
    clapp.add(about);
    clapp.addSeparator();
    clapp.add(cpreferences);
    clapp.addSeparator();
    clapp.add(rpreferences);
    clapp.addSeparator();
    clapp.add(help);
    clapp.addSeparator();
    clapp.add(exit);
  }

  //
  private void fillRecent(ArrayList<File> files) {
    recent.setEnabled(true);
    recent.removeAll();
    for (File file : files) {
      String text = file.getName();
      try {
        text += " ("+ file.getParentFile().getCanonicalPath() + ")";
      } catch (IOException e) {
        e.printStackTrace();
      }
      JMenuItem item = new JMenuItem(text);
      recent.add(item);
      item.addActionListener(editor);
    }
  }

  public void perform(Object source)
      throws InterruptedException, IOException {
    if (source == fcreate) {
      if (!isAnyDirty() || askForConfirmation()) {
        updateContext(null, true);
      }
    }
    else if (source == ccreate) {
      if (!isAnyDirty() || askForConfirmation()) {
        updateContext(null, false);
      }
    }
    else if (source == open) {
      if (!isAnyDirty() || askForConfirmation()) {
        File selectedFile = editor.chooseFile(false, false);
        if (selectedFile != null) {
          if (rhandler.add(selectedFile)) {
            fillRecent(rhandler.getFiles());
          }
          updateContext(selectedFile, null);
        }
      }
    }
    else if (source == exit) {
      if (!isAnyDirty() || askForConfirmation()) {
        System.exit(0);
      }
    }
    else if (source == about) {
      new AboutDialog(editor.getFrame()).setVisible(true);
    }
    else if (source == help) {
      new HelpDialog(editor.getFrame()).setVisible(true);
    }
    else if (source == cpreferences) {
      new ColorsDialog(editor.getFrame(), chandler).setVisible(true);
    }
    else if (source == rpreferences) {
      RecentlyOpenedDialog rd = new RecentlyOpenedDialog(editor.getFrame(), rhandler);
      rd.setVisible(true);
      if (rd.isRefreshNeeded()) {
        fillRecent(rhandler.getFiles());
      }
    }
    else if (source == save) {
      editor.saveContent(false);
    }
    else if (source == saveAs) {
      editor.saveContent(true);
    }
    else if (source == export) {
      editor.exportProject();
    }
    else {
      if (!isAnyDirty() || askForConfirmation()) {
        File file = rhandler.getFile(((JMenuItem)source).getText());
        editor.setSelectedFile(file);
        updateContext(file, null);
      }
    }
  }

  private boolean isAnyDirty() {
    return editor.isAnyDirty();
  }

  //
  private void updateContext(File selectedFile, Boolean isFlowChart) throws IOException {
    editor.setup(selectedFile, isFlowChart);
    save.setEnabled(true);
    saveAs.setEnabled(true);
  }

  //
  private boolean askForConfirmation() {
    SaveConfirmationOnLeave conf = new SaveConfirmationOnLeave(editor.getFrame());
    conf.setVisible(true);
    boolean ok = conf.isOk();
    conf.dispose();
    if (ok) {
      editor.setDirty(false);
    }
    return ok;
  }

  public void enableExport() {
    export.setEnabled(true);
  }
}
