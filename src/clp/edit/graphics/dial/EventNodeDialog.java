package clp.edit.graphics.dial;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import clp.edit.dialog.ADialog;
import clp.edit.graphics.shapes.ATransitionRoot;
import clp.edit.graphics.shapes.act.EventNodeShape.InfoGroup;


public class EventNodeDialog extends ADialog implements ActionListener {

  private static final long serialVersionUID = 1263715839017787680L;

  private InfoGroup info;

  private GenericActionListener gal;

  private GridBagConstraints c;

  private JButton okButton;

  private ATransitionRoot transition;


  /**
   * CONSTRUCTOR
   * 
   * @param frame
   * @param info
   * @param t transition
   */
  public EventNodeDialog(Frame frame, InfoGroup info, ATransitionRoot t) {
    super(frame, "Event Node", true);
    this.info = info;
    transition = t;
    if (frame != null) {
      Dimension frameSize = frame.getSize(); 
      Point p = frame.getLocation(); 
      setLocation(p.x + frameSize.width / 4, p.y + frameSize.height / 4);
    }

    setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setPreferredSize(new Dimension(800, 400));

    okButton = new JButton("ok");
    gal = new GenericActionListener(this);
    okButton.addActionListener(gal);

    defineContent(c, okButton);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  public void defineContent(GridBagConstraints c, JButton okButton) {
    getContentPane().removeAll();
    c.gridy = 0;
    c.gridx = 0;
    c.gridwidth = 1;
    getContentPane().add(Box.createVerticalStrut(5), c);

    c.gridy++;
    c.gridx = 0;
    getContentPane().add(new JLabel("Crossing"), c);
    c.gridx++;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx++;
    boolean isDescriptionRequested = createCrossing(c);

    if (isDescriptionRequested) {
      c.gridy++;
      c.gridx = 0;
      getContentPane().add(new JLabel("Description"), c);
      c.gridx++;
      getContentPane().add(Box.createVerticalStrut(5), c);
      c.gridx++;
      c.gridwidth = 3;
      getContentPane().add(info.getEventDescriptionField(), c);
    }

    c.gridy++;
    c.gridx = 0;
    getContentPane().add(Box.createVerticalStrut(5), c);
    
    c.gridy++;
    c.gridx = 1;
    c.gridwidth = 2;
    getContentPane().add(okButton, c);
  }

  //
  protected boolean createCrossing(GridBagConstraints c) {
    getContentPane().add(info.getArrowfield(), c);
    c.gridx++;
    getContentPane().add(Box.createVerticalStrut(5), c);
    c.gridx++;
    getContentPane().add(info.getEventfield(), c);
    return true;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    setVisible(false); 
  }

  public String getTransitionText() {
    return info.toString();
  }

  public void edit(String t, String d) {
    info.parse(t);
    setVisible(true);
  }

  public void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        defineContent(c, okButton);
        getRootPane().updateUI();
      }
    });
  }

  @Override
  public boolean isOk() {
    return gal.isOk();
  }

  /**
   * @return the okButton
   */
  public JButton getOkButton() {
    return okButton;
  }

  @Override
  public String getDescription() {
    return null;
  }

  public String getActivationCondition() {
    switch (info.getArrow()) {
      case ClassicGroup.upArrow:
        transition.addInputVariable(info.getEvent(), info.getEventDescription(), null);
        return "isSetUp(" + info.getEvent() + ")";
      case ClassicGroup.downArrow:
        transition.addInputVariable(info.getEvent(), info.getEventDescription(), null);
        return "isSetDown(" + info.getEvent() + ")";
    }
    return null;
  }
}
