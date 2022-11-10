package clp.edit.graphics.shapes;

public abstract class AInitialShape extends ARootShape {

  private static final long serialVersionUID = -2320119228677140698L;

  public AInitialShape(int width, int height, String text) {
    super(width, height, text);
  }

  @Override
  public boolean generateCode(AContainer container) {
    if (getChild() != null) {
      return getChild().generateActiveCode(container);
    }
    return true;
  }

  public String getActivationCondition(ABindingShape bsr) {
    return null;
  }
}
