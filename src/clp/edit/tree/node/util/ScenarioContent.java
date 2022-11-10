package clp.edit.tree.node.util;

import java.io.Serializable;
import java.util.ArrayList;

import clp.run.scn.DeactType;
import clp.run.scn.Ltype;
import clp.run.scn.ScnQueue;
import clp.run.scn.ScnTask;
import clp.run.scn.ScnTaskName;
import clp.run.scn.SortOrder;


public class ScenarioContent implements IContent, Serializable {

  private static final long serialVersionUID = 1994907870234494064L;

  private DeactType deactivation;
  private int logicLevel;
  private Ltype type;

  private ArrayList<ScnQueue> queues;

  private ArrayList<ScnTask> tasks;

  private boolean isAssigned;

  /**
   * constructor
   * 
   * @param b
   */
  public ScenarioContent(boolean b) {
    isAssigned = b;
  }

  /**
   * @return the deactivation
   */
  public DeactType getDeactivation() {
    return deactivation;
  }

  /**
   * @param deactivation the deactivation to set
   */
  public void setDeactivation(DeactType deactivation) {
    this.deactivation = deactivation;
  }

  /**
   * @return the logicLevel
   */
  public int getLogicLevel() {
    return logicLevel;
  }

  /**
   * @param logicLevel the logicLevel to set
   */
  public void setLogicLevel(int logicLevel) {
    this.logicLevel = logicLevel;
  }

  /**
   * @return the type
   */
  public Ltype getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(Ltype type) {
    this.type = type;
  }

  /**
   * @return the queues
   */
  public ArrayList<ScnQueue> getQueues() {
    return queues;
  }

  /**
   * @param queues the queues to set
   */
  public void setQueues(ArrayList<ScnQueue> queues) {
    this.queues = queues;
  }

  /**
   * @return the tasks
   */
  public ArrayList<ScnTask> getTasks() {
    return tasks;
  }

  /**
   * @param tasks the tasks to set
   */
  public void setTasks(ArrayList<ScnTask> tasks) {
    this.tasks = tasks;
  }

  public void removeQueue(int i) {
    queues.remove(i);
  }

  public void removeTask(int i) {
    tasks.remove(i);
  }

  public void addEmptyQueue() {
    ScnQueue q = new ScnQueue();
    q.setSortOrder(SortOrder.QUEUE_END);
    queues.add(q);
  }

  public void addEmptyTask() {
    ScnTask t = new ScnTask();
    t.setScnTaskName(ScnTaskName.ACTIVATOR);
    tasks.add(t);
  }

  /**
   * @return the isAssigned
   */
  public boolean isAssigned() {
    return isAssigned;
  }

  /**
   * @param isAssigned the isAssigned to set
   */
  public void setAssigned(boolean isAssigned) {
    this.isAssigned = isAssigned;
  }
}
