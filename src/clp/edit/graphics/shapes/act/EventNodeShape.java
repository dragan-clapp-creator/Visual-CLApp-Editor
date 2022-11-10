package clp.edit.graphics.shapes.act;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import clp.edit.GeneralContext;
import clp.edit.dialog.ADialog;
import clp.edit.graphics.dial.ClassicGroup;
import clp.edit.graphics.dial.EventNodeDialog;
import clp.edit.graphics.shapes.ABindingShape;
import clp.edit.graphics.shapes.AEventShape;
import clp.edit.graphics.shapes.AShape;
import clp.edit.graphics.shapes.BindingType;

public class EventNodeShape extends AEventShape {

  private static final long serialVersionUID = 759259210396824663L;

  transient private EventNodeDialog dialog;

  private InfoGroup info;

  public EventNodeShape() {
    super(20, 20, null);
    info = new InfoGroup();
    dialog = new EventNodeDialog(GeneralContext.getInstance().getFrame(), info, this);
    super.setName(dialog.getTransitionText());
    setXoffset(200);
  }

  @Override
  public void paintShape(Graphics g, int offset) {
    Color c = g.getColor();
    int sx = getPX() + offset;
    int sy = getPY();
    int[] px = {sx, sx-10, sx,    sx+10};
    int[] py = {sy, sy+10, sy+20, sy+10};
    if (isSelected() || getChild() == null) {
      g.setColor(Color.lightGray);
    }
    g.drawPolygon(px, py, 4);
    g.setColor(c);
    g.drawString(getName(), sx+15, sy+15);
    if (getChild() != null) {
      super.checkDownType();
      getChild().paintShape(g, offset);
    }
    if (getSibling() != null) {
      getSibling().paintShape(g, offset);
    }
  }

  public void recreateListeners() {
    info.createListener();
  }

  @Override
  public int getPY() {
    return 50 + getYoffset();
  }

  @Override
  public void setChild(AShape shape, BindingType bindingType) {
    getChild().setChild(shape);
  }

  @Override
  public AShape getSelectedShape(int x, int y) {
    AShape s = super.getSelectedShape(x, y);
    if (s == null && getSibling() != null) {
      return getSibling().getSelectedShape(x, y);
    }
    return s;
  }

  @Override
  public ADialog getDialog() {
    if (dialog == null) {
      dialog = new EventNodeDialog(GeneralContext.getInstance().getFrame(), info, this);
    }
    return dialog;
  }

  @Override
  public String getActivationCondition(ABindingShape bs) {
    return ((EventNodeDialog)getDialog()).getActivationCondition();
  }

  //=================================================================

  public class InfoGroup implements Serializable {
    private static final long serialVersionUID = -7079142346284252966L;
    transient private JComboBox<Character> arrowfield;
    private char selectedItem = ClassicGroup.upArrow;
    private JTextField eventfield;
    private JTextField eventdescriptionfield;
    InfoGroup() {
      arrowfield = new JComboBox<>(new Character[] { ClassicGroup.upArrow, ClassicGroup.downArrow });
      arrowfield.setSelectedItem(selectedItem);
      createListener();
      eventfield = new JTextField(5);
      eventfield.setText("e");
      eventdescriptionfield = new JTextField(10);
      eventdescriptionfield.setText("event variable");
    }
    public void createListener() {
      arrowfield.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (arrowfield.getSelectedIndex() == 0) {
            eventfield.setText("");
          }
          selectedItem = (char) arrowfield.getSelectedItem();
        }
      });
    }
    public void parse(String t) {
      if (t != null && !t.isEmpty()) {
        arrowfield.setSelectedItem(t.charAt(0));
        eventfield.setText(t.substring(1).trim());
      }
    }
    /**
     * @return the arrowfield
     */
    public JComboBox<Character> getArrowfield() {
      if (arrowfield == null) {
        arrowfield = new JComboBox<>(new Character[] { ClassicGroup.upArrow, ClassicGroup.downArrow });
        arrowfield.setSelectedItem(selectedItem);
      }
      return arrowfield;
    }
    /**
     * @param arrowfield the arrowfield to set
     */
    public void setArrowfield(JComboBox<Character> arrowfield) {
      this.arrowfield = arrowfield;
    }
    /**
     * @return the eventfield
     */
    public JTextField getEventfield() {
      return eventfield;
    }
    /**
     * @param eventfield the eventfield to set
     */
    public void setEventfield(JTextField eventfield) {
      this.eventfield = eventfield;
    }
    @Override
    public String toString() {
      char arrow = (char) arrowfield.getSelectedItem();
      return arrow + eventfield.getText().trim();
    }
    public char getArrow() {
      return (char) arrowfield.getSelectedItem();
    }
    public String getEvent() {
      return eventfield.getText().trim();
    }
    public String getEventDescription() {
      return eventdescriptionfield.getText();
    }
    public JTextField getEventDescriptionField() {
      return eventdescriptionfield;
    }
  }
}
