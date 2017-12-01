// tag container
import java.util.Vector;


public class Tag {
  private XMLTag inner;

  public Tag(String name) {
    inner = new XMLTag(name);
  }

  public void SVGUpgrade() {
    SVGPathTag path = new SVGPathTag();
    path.setDeep(inner.getDeep());
    inner = path;
  }

  public void addAttribute(Attribute attr) {
    inner.addAttribute(attr);
  }

  public void addAttribute(Vector<Attribute> a) {
    inner.addAttribute(a);
  }

  public void addAttributes(Vector<Attribute> a) {
    inner.addAttributes(a);
  }

  public void addChild(Tag tag) {
    inner.addChild(tag);
  }

  public void setAutoClose() {
    inner.setAutoClose();
  }

  public void setContent(String content) {
    inner.setContent(content);
  }

  public void setDeep(int deep) {
    inner.setDeep(deep);
  }

  public String getName() {
    return inner.getName();
  }

  public String getLowerName() {
    return inner.getLowerName();
  }

  public Vector<Tag> getChilds() {
    return inner.getChilds();
  }

  public String toString() {
    return inner.toString();
  }
}