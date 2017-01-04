package org.robolectric.res;

public class EmptyStyle implements Style {
  @Override
  public AttributeResource getAttrValue(int resId) {
    return null;
  }

  @Override
  public String toString() {
    return "Empty Style";
  }
}
