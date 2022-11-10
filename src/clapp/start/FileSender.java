package clapp.start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clapp.run.http.ClappSender;
import clapp.run.http.IKrypter;

public class FileSender {

  private static final int BASE_WIDTH = 360;

  private JDialog frame;
  private String content = "";
  private String title = "Meta_Scenario//4500/com.krp.test.MyKryper";
  private String[] fileNames = new String[] {
      "hello2.clp"
  };
  private String[] cmdarray = new String[] {
      "bash;-c;\"/Users/dragan/clapp/Examples/1_hello/with_encryption/\"helloMeta_Scenario.sh"
  };
  private String host;
  private int port;
  private long pid;
  private ClappSender sender;


  public static void main(String[] args) {
    FileSender launcher = new FileSender();
    if (args.length == 2) {
      launcher.launch(args[0]);
      launcher.sender = new ClappSender(getCryptingInstance(args[1]));
    }
    else {
      if (args.length == 1) {
        launcher.launch(args[0]);
      }
      else {
        launcher.launch("CLApp launcher");
      }
      launcher.sender = new ClappSender();
    }
  }

  //
  private static IKrypter getCryptingInstance(String string) {
    Class<?> cl;
    try {
      cl = Class.forName(string);
      Constructor<?> cons = cl.getConstructor();
      return (IKrypter) cons.newInstance();
    }
    catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
           IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void launch(String title) {
    frame = new JDialog((JFrame)null, title, false);
    frame.setLayout(new BorderLayout());
    frame.setBackground(Color.gray);
    JPanel globalPanel = new JPanel();
    globalPanel.setLayout(new SpringLayout());
    globalPanel.setBackground(Color.gray);
    fillUIElements(globalPanel);
    frame.add(globalPanel);
    frame.pack();
    frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    showFrame(title);
  }

  //
  private void fillUIElements(JPanel globalPanel) {
    int nbRows = 2;
    globalPanel.add(createControlsPanel());
    globalPanel.add(createTitlesPanel());
    if (fileNames.length > 0) {
      nbRows++;
      globalPanel.add(createFilesPanel());
    }

    makeCompactGrid(globalPanel, nbRows, 1, 6, 6, 6, 6);
  }

  //
  private JPanel createControlsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Controls");
    p.setBorder(border);

    JButton start = new JButton("start");
    start.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          exec(cmdarray);
        }
        catch (IOException | InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });
    JButton stop = new JButton("stop");
    stop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          killPid();
        }
        catch (IOException | InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });
    p.add(start);
    p.add(stop);

    //Lay out the panel.
    makeCompactGrid(p, 1, 2, 6, 6, 6, 6);

    //Set up the content pane.
    p.setOpaque(true);  //content panes must be opaque
    return p;
  }

  //
  private JPanel createTitlesPanel() {
    JPanel p = new JPanel(new SpringLayout());

    String[] sp = title.split("/");
    String title = sp[0];
    if (sp.length == 4) {
      title += " listening on port " + sp[2];
      if (!sp[1].isBlank()) {
        title += " of host " + sp[1];
        host = sp[1];
      }
      if (!sp[3].isBlank()) {
        title += " crypting with " + sp[3];
      }
      port = Integer.parseInt(sp[2]);
    }
    JPanel jp = new JPanel();
    jp.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            title));
    JEditorPane infoArea = new JEditorPane();
    infoArea.setContentType("text/html");
    int height = content.length() / 4;
    if (height < 30) {
      height = 30;
    }
    infoArea.setPreferredSize(new Dimension(BASE_WIDTH, height));
    infoArea.setText(content);
    infoArea.setEditable(false);
    jp.add(infoArea);
    p.add(jp);

    //Lay out the panel.
    makeCompactGrid(p, 1, 1, 6, 6, 6, 6);

    //Set up the content pane.
    p.setOpaque(true);  //content panes must be opaque
    return p;
  }

  //
  private JPanel createFilesPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Files to send");
    p.setBorder(border);

    for (int i=0; i<fileNames.length; i++) {
      File file = new File(fileNames[i]);
      JToggleButton btn = new JToggleButton(fileNames[i]);
      btn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            sender.sendFile(host, port, file);
          }
          catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      });
      p.add(btn);
    }

    //Lay out the panel.
    makeCompactGrid(p, fileNames.length, 1, 6, 6, 6, 6);

    //Set up the content pane.
    p.setOpaque(true);  //content panes must be opaque
    return p;
  }

  //
  private void makeCompactGrid(Container parent,
      int rows, int cols,
      int initialX, int initialY,
      int xPad, int yPad) {

    SpringLayout layout;
    try {
        layout = (SpringLayout)parent.getLayout();
    } catch (ClassCastException exc) {
        System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
        return;
    }

    //Align all cells in each column and make them the same width.
    Spring x = Spring.constant(initialX);
    //Align all cells in each column and make them the same width.
    for (int c = 0; c < cols; c++) {
        Spring width = Spring.constant(0);
        for (int r = 0; r < rows; r++) {
            width = Spring.max(width,
                               getConstraintsForCell(r, c, parent, cols).getWidth());
        }
        for (int r = 0; r < rows; r++) {
            SpringLayout.Constraints constraints =
                    getConstraintsForCell(r, c, parent, cols);
            constraints.setX(x);
            constraints.setWidth(width);
        }
        x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }

    //Align all cells in each row and make them the same height.
    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
        Spring height = Spring.constant(0);
        for (int c = 0; c < cols; c++) {
            height = Spring.max(height,
                                getConstraintsForCell(r, c, parent, cols).
                                    getHeight());
        }
        for (int c = 0; c < cols; c++) {
            SpringLayout.Constraints constraints =
                    getConstraintsForCell(r, c, parent, cols);
            constraints.setY(y);
            constraints.setHeight(height);
        }
        y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }

    //Set the parent's size.
    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint(SpringLayout.SOUTH, y);
    pCons.setConstraint(SpringLayout.EAST, x);
  }

  //
  private Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
    SpringLayout layout = (SpringLayout) parent.getLayout();
    Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }

  //
  private void killPid() throws IOException, InterruptedException {
    String cmd = "kill -9 "+pid;
    String[] sp = cmd.split(" ");
    Process p = Runtime.getRuntime().exec(sp);
    p.waitFor();
  }

  //
  private void exec(String[] cmdarray) throws IOException, InterruptedException {
    for (String cmd : cmdarray) {
      String[] sp = cmd.split(";");
      ProcessBuilder pb = new ProcessBuilder(sp);
      Process p = pb.start();
      long pid = p.pid();
      System.out.printf("command %s started on pid %d\n", sp[2], pid);
      pid++;
    }
  }

  //
  private void showFrame(String title) {
    Rectangle screenRect = frame.getGraphicsConfiguration().getBounds();
    Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
        frame.getGraphicsConfiguration());

    int centerWidth = screenRect.width < frame.getSize().width ? screenRect.x
        : screenRect.x + screenRect.width / 2 - frame.getSize().width / 2;
    int centerHeight = screenRect.height < frame.getSize().height ? screenRect.y
        : screenRect.y + screenRect.height / 2 - frame.getSize().height / 2;

    centerHeight = centerHeight < screenInsets.top ? screenInsets.top
        : centerHeight;

    frame.setLocation(centerWidth, centerHeight);
    frame.setVisible(true);
  }
}
