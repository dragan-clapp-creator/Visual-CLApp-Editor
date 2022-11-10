package clp.edit.graphics.dial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class ClassicGroup implements Serializable {

  private static final long serialVersionUID = -710978414754124130L;

  public static final char upArrow = '\u2191';
  public static final char downArrow = '\u2193';

  transient private JComboBox<Character> arrowfield;
  private char selectedArrow;
  private JTextField eventfield;
  private JTextField eventdescriptionfield;
  private JTextField conditionfield;
  private JTextField conditiondescriptionfield;

  public ClassicGroup() {
    setArrow(upArrow);
    eventfield = new JTextField(5);
    eventfield.setText("e");
    conditionfield = new JTextField(10);
    conditionfield.setText("a");
    eventdescriptionfield = new JTextField(10);
    eventdescriptionfield.setText("event variable");
    conditiondescriptionfield = new JTextField(10);
    conditiondescriptionfield.setText("boolean condition");
  }

  public void parse(String t) {
    if (t != null && !t.isEmpty()) {
      String event;
      String condition = "";
      if (t.charAt(0) == upArrow || t.charAt(0) == downArrow) {
        selectedArrow = t.charAt(0);
        int i = t.indexOf('.');
        if (i < 0) {
          event = t.substring(1).trim();
        }
        else {
          event = t.substring(1, i).trim();
          condition = t.substring(i+1).trim();
          if (condition.charAt(0) == '(') {
            condition = condition.substring(1, condition.length()-1);
          }
        }
      }
      else {
        selectedArrow = 0;
        event = "";
        condition = t;
      }
      getArrowfield().setSelectedItem(selectedArrow);
      eventfield.setText(event);
      conditionfield.setText(condition);
    }
  }

  /**
   * @return the arrowfield
   */
  public JComboBox<Character> getArrowfield() {
    if (arrowfield == null) {
      arrowfield = new JComboBox<>(new Character[] { 0, upArrow, downArrow });
      arrowfield.setSelectedItem(selectedArrow);
      arrowfield.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (arrowfield.getSelectedIndex() == 0) {
            eventfield.setText("");
          }
          selectedArrow = (char) arrowfield.getSelectedItem();
        }
      });
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

  /**
   * @return the conditionfield
   */
  public JTextField getConditionfield() {
    return conditionfield;
  }

  /**
   * @param conditionfield the conditionfield to set
   */
  void setConditionfield(JTextField conditionfield) {
    this.conditionfield = conditionfield;
  }
  @Override
  public String toString() {
    String text;
    char arrow = (char) getArrow();
    if (arrow == 0) {
      text = conditionfield.getText().trim();
    }
    else {
      String cnd = getCondition();
      if (cnd.isEmpty()) {
        text = arrow + eventfield.getText().trim();
      }
      else {
        text = arrow + eventfield.getText().trim() + " . " + cnd;
      }
    }
    return text;
  }

  public char getArrow() {
    return selectedArrow;
  }

  public String getEvent() {
    return eventfield.getText().trim();
  }

  public String getCondition() {
    String text = conditionfield.getText().trim();
    if (!eventfield.getText().isBlank() && text.contains("+")) {
      text = "(" + text + ")";
    }
    text = text.replaceAll(" and ", " AND ");
    text = text.replaceAll(" or ", " OR ");
    text = text.replaceAll("not ", "NOT ");
    String[] sp = text.split("\\.");
    if (sp.length > 1) {
      text = sp[0];
      for (int i=1; i<sp.length; i++) {
        text += " AND " + sp[i];
      }
    }
    sp = text.split("\\+");
    if (sp.length > 1) {
      text = sp[0];
      for (int i=1; i<sp.length; i++) {
        text += " OR " + sp[i];
      }
    }
    sp = text.split("!");
    if (sp.length > 1) {
      text = sp[0];
      for (int i=1; i<sp.length; i++) {
        text += "NOT " + sp[i];
      }
    }
    if (text.contains(" OR ") && !text.startsWith("(")) {
      return "(" + text + ")";
    }
    return text;
  }

  public String getEventDescription() {
    return eventdescriptionfield.getText();
  }

  public JTextField getEventDescriptionField() {
    return eventdescriptionfield;
  }

  public String getConditionDescription() {
    return conditiondescriptionfield.getText();
  }

  public JTextField getConditionDescriptionField() {
    return conditiondescriptionfield;
  }

  public void setArrow(char selArrow) {
    selectedArrow = selArrow;
    arrowfield = null;
    getArrowfield();
  }
}
