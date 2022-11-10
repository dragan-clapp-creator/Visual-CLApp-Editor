package clp.edit.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import clp.edit.dialog.help.Help;
import clp.edit.util.CommonScrollPanel;

public class HelpDialog extends JDialog {

  private static final long serialVersionUID = -4661432033766718045L;

  private CommonScrollPanel scrollPane;
  private JPanel panel;


  public HelpDialog(Frame parent) {
    super(parent, "Help to Visual CLApp Editor", false);
    Point p = parent.getLocation(); 
    setLocation(p.x + 700, p.y + 80);
    setLayout(new BorderLayout());
    defineContent();
    setAlwaysOnTop(false);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  public void defineContent() {
    scrollPane = new CommonScrollPanel(new Rectangle(550, 700));

    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    scrollPane.getViewport().add(panel);

    add(scrollPane, BorderLayout.CENTER);

    createSection("<h1>Help on Visual CLApp Editor</h1>", 50, new Color(0xaed6f1));

    createSection("<h2>Get started</h2>", 30, new Color(0xfdf2e9));
    createBlock(new JButton("What is it about?"), Help.INIT, 100);
    createBlock(new JButton("How do I begin?"), Help.START, 290);

    createSection("<h2>Flow Charts</h2>", 30, new Color(0xfdf2e9));
    createBlock(new JButton("Activity Diagrams"), Help.DIAGRAM, 370);
    createBlock(new JButton("Grafcets"), Help.GRAFCET, 360);
    createBlock(new JButton("Weighted Petri Nets"), Help.WPETRI, 280);
    createBlock(new JButton("Colored Petri Nets"), Help.CPETRI, 60);
    createBlock(new JButton("Instructions"), Help.INSTRUCTION, 300);
    createBlock(new JButton("Global Resources"), Help.GLOBAL, 60);
    createBlock(new JButton("Export Project"), Help.EXPORT, 140);

    createSection("<h2>Global Resources</h2>", 30, new Color(0xfdf2e9));
    createBlock(new JButton("Graphical User Interfaces"), Help.GUI, 340);
    createBlock(new JButton("Byte-Code Injection"), Help.BCI, 110);
    createBlock(new JButton("Web Variables Definition"), Help.WEB, 60);
    createBlock(new JButton("Consoles Definition"), Help.CSL, 60);

    createSection("<h2>Pure CLApp</h2>", 30, new Color(0xfdf2e9));
    createBlock(new JButton("CLApp structure"), Help.ELEMENTS, 270);
    createBlock(new JButton("CLApp behavior"), Help.BEHAVIOR, 70);
  }

  //
  private void createSection(String html, int height, Color color) {
    final JEditorPane jep = new JEditorPane();
    jep.setPreferredSize(new Dimension(450, height));
    jep.setEditable(false);
    jep.setContentType("text/html");
    jep.setText(html);
    jep.setBackground(color);
    JSeparator sep = new JSeparator();
    panel.add(sep);
    panel.add(jep);
  }

  //
  private void createBlock(JButton jButton, Help html, int height) {
    final JEditorPane jep = new JEditorPane();
    jep.setPreferredSize(new Dimension(450, height));
    jep.setEditable(false);
    jep.setContentType("text/html");
    jep.setText(html.getContent().toString());
    jep.setVisible(false);

    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jep.setVisible(!jep.isVisible());
        if (jep.isVisible()) {
          scrollPane.addHeight(height);
          Point p = jep.getLocation();
          scrollPane.getViewport().setViewPosition(new Point(p.x, p.y-30));
        }
        else {
          scrollPane.subHeight(height);
        }
      }
    });
    panel.add(jButton);
    panel.add(jep);
  }
}
