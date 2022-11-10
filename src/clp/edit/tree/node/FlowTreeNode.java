package clp.edit.tree.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import clp.edit.handler.CLAppSourceHandler.CellsInfoList;
import clp.edit.panel.GraphicsPanel;
import clp.edit.tree.node.util.SaveInfo;

public class FlowTreeNode extends FileTreeNode {

  private static final long serialVersionUID = 5757070253596578173L;

  public FlowTreeNode(File file, String source) {
    super(file, source);
    setGraphics();
  }

  @Override
  public void save(SaveInfo sinfo) throws IOException {
    File file =  new File(sinfo.getPath()+File.separator+getInfo().getName());
    FileOutputStream fos = new FileOutputStream(file);
    ObjectOutputStream out = new ObjectOutputStream(fos);
    out.writeObject(sinfo.getRoot());
    out.writeObject(sinfo.getGraphicsPanel());
    out.writeObject(sinfo.getCellInfoList());
    out.close();
    fos.close();
    System.out.printf("flow chart %s serialized\n", file.getName());
  }

  @SuppressWarnings("unchecked")
  public SaveInfo retrieveContainers(String path) {
    try {
      File file = new File(path+File.separator+getInfo().getName());
      if (file.exists()) {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fis);
        SaveInfo sinfo = new SaveInfo();
        sinfo.setRoot((ProjectTreeNode) in.readObject());
        sinfo.setGraphicsPanel((GraphicsPanel) in.readObject());
        sinfo.setCellInfoList((Hashtable<String, CellsInfoList>) in.readObject());
        in.close();
        return sinfo;
      }
    }
    catch(IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
