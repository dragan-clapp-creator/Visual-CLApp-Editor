package clp.edit.graphics.shapes.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import clp.edit.graphics.panel.GeneralShapesContainer;
import clp.edit.graphics.shapes.AContainer;

public class ContainerHeaderContextMenu extends JPopupMenu {

  private static final long serialVersionUID = 8652507670300545462L;

  /**
   * CONSTRUCTOR for shapes container
   * 
   * @param controlsContainer
   */
  public ContainerHeaderContextMenu(GeneralShapesContainer controlsContainer, AContainer container) {
    setup(controlsContainer, container.getCheckBox());
  }

  //
  private void setup(GeneralShapesContainer controlsContainer, JCheckBoxMenuItem container) {
    JMenuItem item = new JMenuItem("edit");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controlsContainer.editContainer();
      }
    });
    add(item);

    add(new Separator());

    item = new JMenuItem("move left");
    item.setEnabled(!controlsContainer.isFirst());
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controlsContainer.moveLeft();
      }
    });
    add(item);

    item = new JMenuItem("move right");
    item.setEnabled(!controlsContainer.isLast());
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controlsContainer.moveRight();
      }
    });
    add(item);

    add(new Separator());

    item = new JMenuItem("delete");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controlsContainer.removeContainer();
      }
    });
    add(item);
    add(new Separator());
    add(container);
  }
}
