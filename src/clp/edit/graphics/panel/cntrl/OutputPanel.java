package clp.edit.graphics.panel.cntrl;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import clp.run.res.VarType;

public class OutputPanel extends JPanel implements IOutput {

  private static final long serialVersionUID = -7527104666141930080L;

  private Color fldcolor = new Color(0xe08700);

  private int line;
  private int col;

  private JTextField valueField;

  private VarType type;

  /**
   * CONSTRUCTOR
   * 
   * @param name
   * @param type
   */
  public OutputPanel(String name, VarType type) {
    super();
    this.type = type;
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.LOWERED),
        name+" output");
    setBorder(border);
    setToolTipText(name);
    setSize(160, 60);
    setPreferredSize(new Dimension(160, 60));
    if (type != null) {
      setup(name, type);
    }
  }

  //
  private void setup(String name, VarType type) {
    add(new JLabel(""+type.getVal().charAt(0)));
    JTextField fld = new JTextField(name);
    fld.setEnabled(false);
    fld.setBackground(fldcolor);
    add(fld);
    valueField = new JTextField(5);
    add(valueField);
  }


  /**
   * @return the line
   */
  public int getLine() {
    return line;
  }

  /**
   * @param line the line to set
   */
  public void setLine(int line) {
    this.line = line;
  }

  /**
   * @return the col
   */
  public int getCol() {
    return col;
  }

  /**
   * @param col the col to set
   */
  public void setCol(int col) {
    this.col = col;
  }

  public void setValue(Object value) {
    if (value == null) {
      return;
    }
    switch (type) {
      case TINT:
      case TFLOAT:
      case TLONG:
      case TSTRING:
      case TREF:
        valueField.setText(""+value);
        break;
      case TDATE:
        Date d = (Date)value;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        valueField.setText(formatter.format(d));
        break;
      case TTIME:
        Time t = (Time)value;
        formatter = new SimpleDateFormat("HH:mm:ss");
        valueField.setText(formatter.format(t));
        break;

      default:
        break;
    }
  }

  public void enableIt() {
    valueField.setEnabled(true);
  }

  public void disableIt() {
    valueField.setEnabled(false);
  }

  @Override
  public String getText() {
    return getToolTipText();
  }
}
