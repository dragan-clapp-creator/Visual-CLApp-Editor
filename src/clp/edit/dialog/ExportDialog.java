package clp.edit.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import clp.edit.CLAppEditor;
import clp.edit.exp.ExportActor;
import clp.edit.exp.ExportMetaScenrio;
import clp.edit.exp.ExportScenrio;
import clp.edit.graphics.code.web.WebContext.WebInfo;
import clp.edit.graphics.dial.GenericActionListener;

public class ExportDialog extends ADialog {

  private static final long serialVersionUID = -8675197103752804632L;

  private enum Labels {
    HOST("Host: "),
    PORT("Port: "),
    ACTORS("Actors: ");

    private String val;

    private Labels(String s) {
      val = s;
    }

    String getVal() {
      return val;
    }
  }
  
  private CLAppEditor clappEditor;
  private ArrayList<ExportMetaScenrio> elist;

  private GenericActionListener gal;

  private JTextField dfield;    // destination

  private ExportMetaScenrio baseEmsc;

  private ArrayList<String> buttons;
  private ArrayList<String> selbuttons;
  private String[] selports;

  private long checkedTime;
  private int nbcol;

  /**
   * CONSTRUCTOR
   * 
   * @param owner
   * @param ce clappEditor 
   * @param cb control buttons
   * @param emsc
   * @param webInfos
   */
  public ExportDialog(Frame owner, CLAppEditor ce, ArrayList<String> cb, ExportMetaScenrio emsc, Collection<WebInfo> webInfos) {
    super(owner, "Export Dialog", true);
    clappEditor = ce;
    baseEmsc = emsc;
    elist = new ArrayList<>();
    buttons = cb;
    selbuttons = new ArrayList<>();

    if (webInfos.isEmpty()) {
      nbcol = 1;
      elist.add(emsc);
      emsc.setDescription("");
    }
    else {
      nbcol = getActorsNumber(emsc);
      initializeWebPorts(webInfos);
      for (int i=0; i<nbcol; i++) {
        ExportMetaScenrio em = createExportMeta(emsc, i);
        elist.add(em);
      }
    }
    Dimension parentSize = owner.getSize(); 
    Point p = owner.getLocation(); 
    setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);

    setup();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack(); 
  }

  //
  private int getActorsNumber(ExportMetaScenrio emsc) {
    int nb = 0;
    for (ExportScenrio escn : emsc.getScenarios()) {
      nb += escn.getActors().size();
    }
    return nb;
  }

  //
  private void initializeWebPorts(Collection<WebInfo> webInfos) {
    selports = new String[webInfos.size()+1];
    selports[0] = "";
    int i=1;
    for (WebInfo wi : webInfos) {
      selports[i] = ""+wi.getWebVar().getPort().getNum();
      i++;
    }
  }

  //
  private void setup() {
    setLayout(new SpringLayout());

    getContentPane().add(createInfoPanel());
    getContentPane().add(createButtonsPanel());
    getContentPane().add(createDestinationPanel());
    getContentPane().add(createControlsPanel());

    makeCompactGrid(getContentPane(), 4, 1, 6, 6, 6, 6);
  }

  //
  private ExportMetaScenrio createExportMeta(ExportMetaScenrio emsc, int i) {
    ExportMetaScenrio em = new ExportMetaScenrio();
    em.setName("MSC"+i);
    em.setDescription("");
    em.setProperties(baseEmsc.getProperties());
    em.setResources(baseEmsc.getResources());
    if (i == 1) {
      em.setScenarios(emsc.getScenarios());
    }
    return em;
  }

  //
  private JPanel createInfoPanel() {
    int nbLines = Labels.values().length;

    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Export Information");
    p.setBorder(border);
    p.add(new JLabel());
    for (ExportMetaScenrio em : elist) {
      p.add(new JLabel(em.getName()+em.getDescription()));
    }
    for (Labels lbl : Labels.values()) {
      JLabel l = new JLabel(lbl.getVal(), JLabel.TRAILING);
      p.add(l);
      for (int j=0; j<nbcol; j++) {
        ExportMetaScenrio em = elist.get(j);
        JTextField textField = new JTextField(10);
        l.setLabelFor(textField);
        switch (lbl) {
          case HOST:
            textField.setText(em.getHost());
            if (em.getHost() == null) {
              textField.setText("localhost");
            }
            textField.setEnabled(false);
            p.add(textField);
            break;
          case PORT:
            if (selports == null) {
              JTextField pfield = new JTextField(10);
              pfield.setText(baseEmsc.getPort());
              pfield.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(FocusEvent e) {
                  if (isNumeric(pfield.getText())) {
                    baseEmsc.setPort(pfield.getText());
                  }
                }
                @Override
                public void focusGained(FocusEvent e) {
                }
              });
              p.add(pfield);
            }
            else {
              JComboBox<String> pcombo = new JComboBox<>(selports);
              pcombo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  em.setPort((String) pcombo.getSelectedItem());
                }
              });
              em.setPort((String) pcombo.getSelectedItem());
              p.add(pcombo);
            }
            break;
          case ACTORS:
            DefaultListModel<String> listModel = new DefaultListModel<>();
            listModel.addAll(getActorsNames(em.getScenarios()));
            JList<String> list = new JList<String>(listModel);
            list.setName(""+j);
            l.setLabelFor(list);
            ListSelectionModel sm = list.getSelectionModel();
            list.addFocusListener(new FocusListener() {
              @Override
              public void focusLost(FocusEvent e) {
                sm.clearSelection();
              }
              @Override
              public void focusGained(FocusEvent e) {
              }
            });
            p.add(list);
            break;

          default:
            break;
        }
      }
    }
    if (nbcol > 1) {
      p.add(new JLabel());
      JButton lbtn = new JButton("<<<");
      lbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          moveLeftIfPossible(lbtn.getParent());
        }
      });
      lbtn.setForeground(Color.blue);
      p.add(lbtn);
      JButton rbtn = new JButton(">>>");
      rbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          moveRightIfPossible(rbtn.getParent());
        }
      });
      rbtn.setForeground(Color.blue);
      p.add(rbtn);
      for (int i=2; i<nbcol; i++) {
        p.add(new JLabel());
      }
    }
    else {
      nbLines--;
    }

    makeCompactGrid(p, nbLines+2, nbcol+1, 6, 6, 6, 6);

    p.setOpaque(true);
    return p;
  }

  //
  private void moveLeftIfPossible(Container container) {
    String name = findSelected(container);
    if (name != null) {
      String[] sp = name.split(":");
      for (int i=0; i<elist.size(); i++) {
        ArrayList<ExportScenrio> scenarios = elist.get(i).getScenarios();
        for (ExportScenrio es : scenarios) {
          if (es.getName().equals(sp[0])) {
            for (ExportActor ea : es.getActors()) {
              if (ea.getName().equals(sp[1])) {
                if (i > 0) {
                  ArrayList<ExportScenrio> lscenarios = elist.get(i-1).getScenarios();
                  ExportScenrio l_es = findScenario(lscenarios, sp[0]);
                  if (l_es == null) {
                    l_es = new ExportScenrio();
                    l_es.setName(sp[0]);
                    l_es.setProperties(es.getProperties());
                    l_es.setActors(new ArrayList<ExportActor>());
                  }
                  lscenarios.add(l_es);
                  l_es.getActors().add(ea);
                  es.getActors().remove(ea);
                  if (es.getActors().isEmpty()) {
                    scenarios.remove(es);
                  }
                  refresh();
                  return;
                }
              }
            }
          }
        }
      }
    }
  }

  //
  private void moveRightIfPossible(Container container) {
    String name = findSelected(container);
    if (name != null) {
      String[] sp = name.split(":");
      for (int i=0; i<elist.size(); i++) {
        ArrayList<ExportScenrio> scenarios = elist.get(i).getScenarios();
        for (ExportScenrio es : scenarios) {
          if (es.getName().equals(sp[0])) {
            for (ExportActor ea : es.getActors()) {
              if (ea.getName().equals(sp[1])) {
                if (i < elist.size()-1) {
                  ArrayList<ExportScenrio> rscenarios = elist.get(i+1).getScenarios();
                  ExportScenrio r_es = findScenario(rscenarios, sp[0]);
                  if (r_es == null) {
                    r_es = new ExportScenrio();
                    r_es.setName(sp[0]);
                    r_es.setProperties(es.getProperties());
                    r_es.setActors(new ArrayList<ExportActor>());
                  }
                  rscenarios.add(r_es);
                  r_es.getActors().add(ea);
                  es.getActors().remove(ea);
                  if (es.getActors().isEmpty()) {
                    scenarios.remove(es);
                  }
                  refresh();
                  return;
                }
              }
            }
          }
        }
      }
    }
  }

  //
  private ExportScenrio findScenario(ArrayList<ExportScenrio> rscenarios, String name) {
    for (ExportScenrio es : rscenarios) {
      if (es.getName().equals(name)) {
        return es;
      }
    }
    return null;
  }

  //
  private void refresh() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        getContentPane().removeAll();
        setup();
        validate();
      }
    });
  }

  //
  @SuppressWarnings("unchecked")
  private String findSelected(Container p) {
    for (int i=0; i<p.getComponentCount(); i++) {
      if (p.getComponent(i) instanceof JList) {
        JList<String> list = (JList<String>)p.getComponent(i);
        ListSelectionModel sm = list.getSelectionModel();
        int index = sm.getLeadSelectionIndex();
        if (index >= 0) {
          return findActorsName(index);
        }
      }
    }
    return null;
  }

  //
  private String findActorsName(int index) {
    int i = 0;
    for (ExportMetaScenrio em : elist) {
      for (int j=0; j<em.getScenarios().size(); j++) {
        ExportScenrio es = em.getScenarios().get(j);
        for (ExportActor ea : es.getActors()) {
          if (i < index) {
            i++;
          }
          else {
            return es.getName()+":"+ea.getName();
          }
        }
      }
    }
    return "";
  }

  //
  private boolean isNumeric(String text) {
    try {
      Integer.parseInt(text);
      return true;
    }
    catch (NumberFormatException e) {
    }
    return false;
  }

  //
  private JPanel createButtonsPanel() {
    int nbLines = buttons.size();
    int nbCols = 2;

    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Export Buttons");
    p.setBorder(border);
    for (String name : buttons) {
      JCheckBox cb = new JCheckBox();
      cb.setName(name);
      cb.setSelected(selbuttons.contains(name));
      cb.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          long ct = System.currentTimeMillis();
          if (ct - checkedTime > 200) {
            checkedTime = ct;
            String n = cb.getName();
            if (cb.isSelected()) {
              if (selbuttons.contains(n))  {
                selbuttons.remove(n);
              }
            }
            else if (!selbuttons.contains(n)) {
              selbuttons.add(n);
            }
          }
        }
      });
      p.add(cb);
      JButton btn = new JButton(name);
      p.add(btn);
    }
    p.add(new JLabel());
    JButton btn = new JButton("select all");
    btn.setForeground(Color.blue);
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (btn.getText().startsWith("sel")) {
          for (int i=0; i<p.getComponentCount(); i++) {
            if (p.getComponent(i) instanceof JCheckBox) {
              ((JCheckBox)p.getComponent(i)).setSelected(true);
            }
          }
          selbuttons.addAll(buttons);
          btn.setText("unselect all");
        }
        else {
          for (int i=0; i<p.getComponentCount(); i++) {
            if (p.getComponent(i) instanceof JCheckBox) {
              ((JCheckBox)p.getComponent(i)).setSelected(false);
            }
          }
          selbuttons.clear();
          btn.setText("select all");
        }
      }
    });
    p.add(btn);

    makeCompactGrid(p,
                    nbLines+1, nbCols, //rows, cols
                    6, 6,              //initX, initY
                    6, 6);             //xPad, yPad

    p.setOpaque(true);
    return p;
  }

  //
  private JPanel createDestinationPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Export Destination");
    p.setBorder(border);
    JLabel l = new JLabel("Destination:", JLabel.TRAILING);
    p.add(l);
    dfield = new JTextField(10);
    l.setLabelFor(dfield);
    dfield.setText(clappEditor.getselectedDir());
    dfield.setEnabled(false);
    p.add(dfield);
    JButton btn = new JButton("Browse");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (clappEditor.chooseForExport()) {
          dfield.setText(clappEditor.getselectedDir());
        }
      }
    });
    p.add(btn);

    makeCompactGrid(p, 1, 3, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private JPanel createControlsPanel() {
    JPanel p = new JPanel(new SpringLayout());
    TitledBorder border = BorderFactory.createTitledBorder(
        BorderFactory.createBevelBorder(EtchedBorder.RAISED),
        "Control");
    p.setBorder(border);
    p.add(new JLabel());
    gal = new GenericActionListener(this);
    JButton cbtn = new JButton("cancel");
    cbtn.setForeground(Color.blue);
    cbtn.addActionListener(gal);
    p.add(cbtn);
    JButton okbtn = new JButton("ok");
    okbtn.setForeground(Color.blue);
    okbtn.addActionListener(gal);
    p.add(okbtn);

    makeCompactGrid(p, 1, 3, 6, 6, 6, 6);
    p.setOpaque(true);
    return p;
  }

  //
  private ArrayList<String> getActorsNames(ArrayList<ExportScenrio> scenarios) {
    ArrayList<String> names = new ArrayList<>();
    if (scenarios.isEmpty()) {
      names.add("...");
    }
    else {
      for (ExportScenrio escn : scenarios) {
        for (ExportActor eact : escn.getActors()) {
          names.add(escn.getName()+":"+eact.getName());
        }
      }
    }
    return names;
  }

  public boolean isOk() {
    return gal.isOk();
  }

  public ArrayList<ExportMetaScenrio> getExports() {
    return elist;
  }

  public ArrayList<String> getSelectedButtons() {
    return selbuttons;
  }

  @Override
  public void edit(String text, String desc) {
  }

  @Override
  public String getTransitionText() {
    return null;
  }

  @Override
  public String getDescription() {
    return null;
  }
}
