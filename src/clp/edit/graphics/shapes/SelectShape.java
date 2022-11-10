package clp.edit.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;

public class SelectShape {

  private int x;
  private int y;
  private int width;
  private int height;

  public SelectShape(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public void paintShape(Graphics g) {
    Color c = g.getColor();
    g.setColor(Color.blue);
    g.drawRect(x, y, width, height);
    g.setColor(c);
  }
}
