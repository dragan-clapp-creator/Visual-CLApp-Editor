package clp.edit.util;

import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import clapp.run.ui.util.AConsoleHandler;
import clp.run.msc.MscOutput;
import clp.run.msc.Output;
import clp.run.msc.OutputTarget;

public class ConsoleHandler extends AConsoleHandler {

  /**
   * constructor
   */
  public ConsoleHandler() {
    super();
  }

  /**
   * initialize console according to given info
   * 
   * @param mscOutput
   * @param tp
   */
  public void initialize(MscOutput mscOutput, JTabbedPane tp) {
    if (!isInitialized() && mscOutput != null) {
      ArrayList<Output> outList = mscOutput.getOutputs();
      outList.add(0, mscOutput.getOutput());
      for (Output out : outList) {
        OutputTarget target = out.getOutputTarget();
        if (target.isStringCONSOLE()) {
          JTextPane standard = addTab(tp, target.getStringCONSOLE());
          setColors(standard, out, target.getStringCONSOLE());
        }
        else if (target.getName() != null) {
          JTextPane cons = addTab(tp, target.getName());
          setColors(cons, out, target.getName());
        }
      }
      setInitialized(true);
    }
  }

  @Override
  public void initialize(MscOutput mscOutput) {
  }

  @Override
  public void refresh() {
  }
}
