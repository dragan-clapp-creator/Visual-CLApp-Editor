package clp.edit.graphics.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import clapp.run.token.EventHandler;
import clapp.run.util.ResourceUtility;
import clp.edit.graphics.panel.cntrl.AInputButton;
import clp.edit.graphics.panel.cntrl.ConditionButton;
import clp.edit.graphics.panel.cntrl.EventButton;
import clp.edit.graphics.panel.cntrl.OutputButton;
import clp.edit.graphics.panel.cntrl.OutputPanel;
import clp.run.res.VarType;

public class ControlInfo implements Serializable {

  private static final long serialVersionUID = 3215829060572545836L;

  private String name;
  private String text;
  private VarType varType;
  private JComponent component;

  private String content;

  private int index;

  private String init;

  private boolean isInput;

  public ControlInfo(String n, String x, VarType t, boolean b) {
    name = n;
    text = x;
    varType = t;
    isInput = b;
    setup(true);
  }

  public void setup(boolean isCreate) {
    if (varType == null) {
      // this one is an event
      createEventToggleButton(isCreate);
    }
    else {
      switch (varType) {
        case TBOOL:
          if (isInput) {
            createConditionButton(isCreate);
          }
          else {
            if (isCreate) {
              createOutputButton();
            }
          }
          break;
        case TREF:
        case TDATE:
        case TTIME:
        case TINT:
        case TFLOAT:
        case TLONG:
        case TSTRING:
          if (isCreate) {
            createOutputPanel();
          }
          break;

        default:
          break;
      }
    }
  }

  //
  private void createEventToggleButton(boolean isCreate) {
    EventButton eb;
    if (isCreate) {
      eb = new EventButton(name, text);
      component = eb;
    }
    else {
      eb = (EventButton) component;
    }
    eb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JToggleButton b = (JToggleButton) e.getSource();
        EventHandler em = EventHandler.getInstance();
        em.markVarEvent(name, b.isSelected());
      }
    });
    eb.setEnabled(false);
  }

  //
  private void createConditionButton(boolean isCreate) {
    ConditionButton cb;
    if (isCreate) {
      cb = new ConditionButton(name, text);
      component = cb;
    }
    else {
      cb = (ConditionButton) component;
    }
    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JToggleButton b = (JToggleButton) e.getSource();
        ResourceUtility util = ResourceUtility.getInstance();
        util.setValue(name, b.isSelected());
      }
    });
    cb.setEnabled(false);
  }

  //
  private void createOutputButton() {
    component = new OutputButton(name);
  }

  //
  private void createOutputPanel() {
    component = new OutputPanel(name, varType);
  }

  public String toString() {
    String txt;
    switch (varType) {
      case TBOOL:
        txt = "  BOOL " + name;
        break;
      case TINT:
        txt = "  INT " + name;
        break;
      case TFLOAT:
        txt = "  DOUBLE " + name;
        break;
      case TDATE:
        txt = "  DATE " + name;
        break;
      case TREF:
        txt = "  REF " + name;
        break;
      case TSTRING:
        if (index > 0) {
          txt = "  STRING array[" + index + "] " + name;
        }
        else {
          txt = "  STRING " + name;
        }
        break;
      case TUI:
        return "  UI " + name + content;
      default:
        txt = "";
        break;
    }
    if (init != null) {
      txt += " = " + init;
    }
    return txt + ";";
  }

  public JComponent getComponent() {
    return component;
  }

  public String getName() {
    return name;
  }

  public VarType getType() {
    return varType;
  }

  public int getColumn() {
    if (component instanceof AInputButton) {
      return ((AInputButton)component).getCol();
    }
    return -1;
  }

  public int getLine() {
    if (component instanceof AInputButton) {
      return ((AInputButton)component).getLine();
    }
    return -1;
  }

  public void updateOutput() {
    ResourceUtility util = ResourceUtility.getInstance();
    if (!isInput && component != null) {
      if (component instanceof JToggleButton) {
        ((JToggleButton)component).setSelected((boolean) util.getValue(name));
      }
      else {
        ((OutputPanel)component).setValue(util.getValue(name));
      }
    }
  }

  public void setupActionListener() {
    if (component instanceof ConditionButton) {
      ((ConditionButton)component).addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JToggleButton b = (JToggleButton) e.getSource();
          ResourceUtility util = ResourceUtility.getInstance();
          util.setValue(name, b.isSelected());
        }
      });
    }
    else if (component instanceof EventButton) {
      ((EventButton)component).addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JToggleButton b = (JToggleButton) e.getSource();
          EventHandler em = EventHandler.getInstance();
          em.markVarEvent(name, b.isSelected());
        }
      });
    }
  }
}
