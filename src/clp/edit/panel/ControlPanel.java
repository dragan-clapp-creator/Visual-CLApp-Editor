package clp.edit.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import com.krp.test.MyKrypter;

import clapp.run.Supervisor;
import clapp.run.http.ClappSender;
import clp.edit.GeneralContext;
import clp.edit.tree.node.ATreeNode;
import clp.edit.tree.node.FileTreeNode;
import clp.edit.tree.node.MetaScenarioTreeNode;
import clp.edit.tree.node.ProjectTreeNode;
import clp.edit.util.ColorSet;
import clp.run.msc.Port;

public class ControlPanel extends JPanel {

  private static final long serialVersionUID = 3838618554381546545L;

  transient private GeneralContext context;

  private JRadioButton radio1;
  private JRadioButton radio2;

  private JPanel jp;

  private JCheckBox kryptCheckBok;

  public ControlPanel(JTree mainTree, ProjectTreeNode root, PropertiesPanel propsPanel, JScrollPane outPanel, boolean isStartEnabled) {
    context = GeneralContext.getInstance();
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Control Area"));
    setup(mainTree, root, outPanel, propsPanel, isStartEnabled);
    setPreferredSize(getMaximumSize());
  }

  //
  public void setup(JTree mainTree, ProjectTreeNode root, JScrollPane outPanel, PropertiesPanel propsPanel, boolean isStartEnabled) {
    removeAll();
    JToggleButton tb = new JToggleButton();
    tb.setEnabled(isStartEnabled);
    setupAreas(propsPanel, outPanel, tb);
    tb.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        tb.setSelected(false);
        MetaScenarioTreeNode msc = findMetaScenarioTreeNode(mainTree, root);
        if (msc == null) {
          return;
        }
        if (context.isDesignTime()) {
          context.setDesignRuntime(false);
          mainTree.setSelectionRow(root.getIndex(msc)+1);
          context.startClappApplication(msc);
        }
        else {
          context.setDesignRuntime(true);
          mainTree.setSelectionRow(0);
          ClappSender sender;
          if (kryptCheckBok != null && kryptCheckBok.isSelected()) {
            sender = new ClappSender(new MyKrypter());
          }
          else {
            sender = new ClappSender();
          }
          if (!context.isCodeDirty()) {
            Supervisor.getInstance().stopAll(context, sender);    // will call GeneralContext#onFinish()
          }
          outPanel.getViewport().removeAll();
          outPanel.getViewport().add(new JTextArea());
          removeAllInJp();
        }
        context.setupOutput();
        SwingUtilities.invokeLater( new Runnable() { 
          public void run() { 
            context.updateClappPanel();
          } 
        } );
      }
    });
    add(tb);
    jp = new JPanel();
    add(jp);
  }

  //
  private void setupAreas(PropertiesPanel propsPanel, JScrollPane outPanel, JToggleButton tb) {
    context.registerStartButton(tb);
    if (context.isDesignTime()) {
      tb.setText("Start");
      propsPanel.setBackground(Color.white);
    }
    else {
      tb.setText("Stop");
      outPanel.setBackground(Color.lightGray);
    }
  }

  //
  private MetaScenarioTreeNode findMetaScenarioTreeNode(JTree tree, ProjectTreeNode proj) {
    for (int i=0; i<proj.getChildCount(); i++) {
      ATreeNode node = (ATreeNode) proj.getChildAt(i);
      if (node instanceof FileTreeNode) {
        for (int j=0; j<node.getChildCount(); j++) {
          ATreeNode n = (ATreeNode) node.getChildAt(j);
          if (n instanceof MetaScenarioTreeNode) {
            return (MetaScenarioTreeNode) n;
          }
        }
      }
    }
    return null;
  }

  public void addSendTo(ATreeNode node, Port port, ClappPanel clappPanel) {
    jp.removeAll();
    JPanel jp1 = new JPanel();
    jp1.setLayout(new BoxLayout(jp1, BoxLayout.Y_AXIS));
    jp1.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
            "Selection Area"));
    jp.add(jp1);

    JPanel jp2 = new JPanel();
    jp2.setLayout(new BoxLayout(jp2, BoxLayout.X_AXIS));
    jp2.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
            "Send Area"));
    jp.add(jp2);

    ItemListener radioListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        JRadioButton rb = (JRadioButton) e.getSource();
        if (rb == radio1) {
          addLocal(jp2, node, port.getNum());
        }
        else if (rb == radio2) {
          addRemote(jp2, node, port);
        }
      }
    };

    JPanel jp1_1 = new JPanel();
    jp1_1.setLayout(new BoxLayout(jp1_1, BoxLayout.Y_AXIS));
    jp1_1.setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "send option"));
    jp1.add(jp1_1);
    ButtonGroup bg1 = new ButtonGroup();
    radio1 = (JRadioButton) jp1_1.add( new JRadioButton("on local port") );
    radio1.addItemListener(radioListener);
    radio1.setSelected(true);
    bg1.add(radio1);
    radio2 = (JRadioButton) jp1_1.add( new JRadioButton("remote") );
    radio2.addItemListener(radioListener);
    bg1.add(radio2);
  }

  //
  private void addLocal(JPanel jp2, ATreeNode node, int port) {
    jp2.removeAll();
    jp2.add(new JLabel("on port"));
    JTextField tf1 = new JTextField(""+port);
    tf1.setEditable(false);
    tf1.setBackground(ColorSet.notEditableProperty.getLight());
    jp2.add(tf1);
    jp2.add(new JLabel("source"));
    JTextField tf2 = new JTextField(node.getFileReference().getName());
    tf2.setEditable(false);
    tf2.setBackground(ColorSet.notEditableProperty.getLight());
    jp2.add(tf2);
    kryptCheckBok = new JCheckBox("using encryption");
    jp2.add(kryptCheckBok);
    JButton btn = new JButton("Send");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          ClappSender sender;
          if (kryptCheckBok.isSelected()) {
            sender = new ClappSender(new MyKrypter());
          }
          else {
            sender = new ClappSender();
          }
          sender.sendSource(port, getSourceFromFileNode(node));
        }
        catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    jp2.add(btn);
  }

  //
  private String getSourceFromFileNode(ATreeNode node) {
    ATreeNode p = node;
    while (p != null && !(p instanceof FileTreeNode)) {
      p = (ATreeNode) p.getParent();
    }
    if (p != null) {
      return ((FileTreeNode)p).getSource();
    }
    return null;
  }

  //
  private void addRemote(JPanel jp2, ATreeNode node, Port port) {
    jp2.removeAll();
  }

  //
  private void removeAllInJp() {
    Dimension size = getSize();
    setSize(size.width, size.height-300);
    jp.removeAll();
  }
}
