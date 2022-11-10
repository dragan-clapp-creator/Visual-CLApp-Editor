package clp.edit.graphics.dial;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import clp.edit.graphics.shapes.AEventShape;

public class InputVariablePanel extends JPanel {

  private static final long serialVersionUID = 1805358252096725711L;

  private JTextField varfield;
  private JTextField description;

  private AEventShape event;

  public InputVariablePanel(String name) {
    super();
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(EtchedBorder.RAISED),
            "Variable name"));
    setLayout(new GridLayout(2,1));
    varfield = new JTextField(name);
    add(varfield);
    add(Box.createHorizontalStrut(5));
    description = new JTextField("variable description");
    add(description);
  }

  public InputVariablePanel(AEventShape event) {
    this(event.getName());
    this.event = event;
  }


  public String getVariableName() {
    String text = varfield.getText();
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
    return text;
  }

  public void setVariableName(String string) {
    varfield.setText(string);
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description.getText();
  }

  public void setDescription(String string) {
    description.setText(string);
  }

  /**
   * @return the event
   */
  public AEventShape getEvent() {
    return event;
  }
}
