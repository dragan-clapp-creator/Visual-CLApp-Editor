package clp.edit.graphics.dial;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.ADecisionShape.DecisionInfo;

public class DecisionDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = 4988804803199444714L;

  private InputVariablePanel ifPartControl;
  private InputVariablePanel elsePartControl;

  private JTextField descriptionField;

  private GenericActionListener gal;


  public DecisionDialog(Frame parent, String varName) {
    super(parent, "Defining Decision Node", true);
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setPreferredSize(new Dimension(700, 200));
    JButton okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);
    okButton.setPreferredSize(new Dimension(50, 20));

    ifPartControl = new InputVariablePanel(varName+"_0");
    elsePartControl = new InputVariablePanel(varName+"_1");

    getContentPane().removeAll();

    c.gridwidth = 3;
    c.gridy = 0;
    c.gridx = 0;
    getContentPane().add(createDescriptionPanel(), c);

    c.gridwidth = 1;
    c.gridy = 1;
    c.gridx = 0;
    getContentPane().add(createLeftPanel(), c);
    c.gridx = 1;
    DecisionShape shape = new DecisionShape();
    shape.setPreferredSize(new Dimension(50, 65));
    getContentPane().add(shape, c);
    c.gridx = 2;
    getContentPane().add(createRightPanel(), c);

    c.gridy = 2;
    c.gridx = 1;
    getContentPane().add(okButton, c);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  private JPanel createDescriptionPanel() {
    JPanel jp = new JPanel();
    jp.add(new JLabel("Description"));
    descriptionField = new JTextField("Decision "+getName());
    jp.add(descriptionField);
    return jp;
  }

  //
  private JPanel createLeftPanel() {
    JPanel jp = new JPanel();
    jp.add(Box.createVerticalStrut(80));
    jp.add(ifPartControl);
    return jp;
  }

  //
  private JPanel createRightPanel() {
    JPanel jp = new JPanel();
    jp.add(Box.createVerticalStrut(80));

    jp.add(elsePartControl);
    jp.add(Box.createVerticalStrut(5));
    return jp;
  }

  public void actionPerformed(ActionEvent e) {
    setVisible(false); 
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }

  @Override
  public void edit(String t, String d) {
    setVisible(true);
  }

  @Override
  public String getTransitionText() {
    return null;
  }

  public void setupInfo(DecisionInfo info) {
    info.setLeftPartName(ifPartControl.getVariableName());
    info.setRightPartName(elsePartControl.getVariableName());
    info.setLeftPartDescription(ifPartControl.getDescription());
    info.setRightPartDescription(elsePartControl.getDescription());
  }

  public void updateFromInfo(DecisionInfo info) {
    ifPartControl.setVariableName(info.getLeftPartName());
    elsePartControl.setVariableName(info.getRightPartName());
    ifPartControl.setDescription(info.getLeftPartDescription());
    elsePartControl.setDescription(info.getRightPartDescription());
  }

  @Override
  public String getDescription() {
    return descriptionField.getText();
  }

  //==============================================================================

  //
  @SuppressWarnings("serial")
  class DecisionShape extends JPanel {

    public void paint(Graphics g) {
      g.drawLine(25, 0, 25, 40);
      int[] px = {25, 15, 25, 35};
      int[] py = {40, 50, 60, 50};
      g.drawPolygon(px, py, 4);
    }
  }
}
