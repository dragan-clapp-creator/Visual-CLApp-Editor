package clp.edit.panel;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import clapp.run.ui.util.CommonScrollPanel;
import clp.edit.util.ColorSet;
import clp.edit.util.ProjectInfo;

public class SourcePanel extends JTabbedPane {

  private static final long serialVersionUID = 2738949034851857495L;

  private ProjectInfo info;

  private Rectangle rect;


  public SourcePanel(Rectangle rect, ProjectInfo info) {
    this.info = info;
    this.rect = rect;
  }

  public void setup() {
    removeAll();
    if (info != null) {
      for (String ref : info.getContent().keySet()) {
        CommonScrollPanel sp = new CommonScrollPanel(rect);
        sp.setBackground(ColorSet.notEditableArea.getLight());
        JTextPane ed = new JTextPane();
        ed.setBackground(ColorSet.notEditableProperty.getLight());
        ed.setEditable(false);
        ed.setBorder(BorderFactory.createLineBorder(Color.black));
        String source = info.getContent().get(ref);
        ed.setText(source);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        sp.getViewport().add(jp);
        jp.add(ed);
        add(ref, sp);
      }
    }
  }
}
