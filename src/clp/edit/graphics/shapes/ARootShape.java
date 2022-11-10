package clp.edit.graphics.shapes;

public abstract class ARootShape extends AShape {

  private static final long serialVersionUID = 8406138797490840383L;

  private ARootShape sibling;

  public ARootShape(int width, int height, String name) {
    super(width, height, name, "");
  }

  public int setSibling(ARootShape ev) {
    if (ev == null) {
      this.sibling = null;
      return 0;
    }
    if (this.sibling == null) {
      this.sibling = ev;
      return 1;
    }
    
    return 1+this.sibling.setSibling(ev);
  }

  @Override
  public void addToX(int delta) {
    super.addToX(delta);
    if (sibling != null) {
      sibling.addToX(delta);
    }
  }

  /**
   * @return the sibling
   */
  public ARootShape getSibling() {
    return sibling;
  }

  public void updateOtherBranches(AShape shape, int delta) {
    AShape s;
    ABindingShape b = shape.getParent();
    if (b == null) {
      s = shape;
    }
    else {
      do {
        s = b.getParent();
        b = s.getParent();
      } while (b != null);
    }
    ARootShape ref = (ARootShape) s;
    ARootShape r = this;
    while (r != null) {
      if (r != ref) {
        r.addToY(delta);
      }
      r = r.getSibling();
    }
  }
}
