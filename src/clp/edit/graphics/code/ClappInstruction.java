package clp.edit.graphics.code;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import clp.edit.graphics.code.prt.PrintElementInfo;
import clp.edit.graphics.code.web.WEBcallDialog.SendInfo;

public class ClappInstruction implements Serializable {

  private static final long serialVersionUID = -4276649529637478083L;

  private String statement;
  private String oldStatement;
  private Color color;

  private String intructionType;

  private String name;
  private String ifname;
  private boolean iskeepalive;
  private String value;
  private ArrayList<PrintElementInfo> printInfoList;
  private String console;
  private int index;

  private ArrayList<SendInfo> sendInfoList;
  private String refvar;


  public void reset() {
    oldStatement = statement;
    statement = null;   // forces re-evaluation on change
  }

  public String getStatement() {
    return statement;
  }

  public void setStatement(String statement) {
    this.statement = statement;
    this.oldStatement = statement;
  }

  /**
   * @return the color
   */
  public Color getColor() {
    return color;
  }

  /**
   * @param color the color to set
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * @return the intructionType
   */
  public String getInstructionType() {
    return intructionType;
  }

  /**
   * @param intructionType the intructionType to set
   */
  public void setIntructionType(String intructionType) {
    this.intructionType = intructionType;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the ifname
   */
  public String getIfname() {
    return ifname;
  }

  /**
   * @param ifname the ifname to set
   */
  public void setIfname(String ifname) {
    this.ifname = ifname;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return the console
   */
  public String getConsole() {
    return console;
  }

  /**
   * @param console the console to set
   */
  public void setConsole(String console) {
    this.console = console;
  }

  /**
   * @return the printInfoList
   */
  public ArrayList<PrintElementInfo> getPrintInfoList() {
    return printInfoList;
  }

  /**
   * @param infos the infos to set
   */
  public void setInfos(ArrayList<PrintElementInfo> infos) {
    this.printInfoList = new ArrayList<>();
    for (PrintElementInfo info : infos) {
      if (!info.getText().isEmpty()) {
        this.printInfoList.add(info);
      }
    }
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * @param index the index to set
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * @return the iskeepalive
   */
  public boolean isIskeepalive() {
    return iskeepalive;
  }

  /**
   * @param iskeepalive the iskeepalive to set
   */
  public void setIskeepalive(boolean iskeepalive) {
    this.iskeepalive = iskeepalive;
  }

  public String getOldStatement() {
    statement = oldStatement;
    return statement;
  }

  /**
   * @return the sendInfoList
   */
  public ArrayList<SendInfo> getSendInfoList() {
    return sendInfoList;
  }

  /**
   * @param sendInfoList the sendInfoList to set
   */
  public void setSendInfoList(ArrayList<SendInfo> sendInfoList) {
    this.sendInfoList = sendInfoList;
  }

  /**
   * @return the refvar
   */
  public String getRefvar() {
    return refvar;
  }

  /**
   * @param refvar the refvar to set
   */
  public void setRefvar(String refvar) {
    this.refvar = refvar;
  }
}
