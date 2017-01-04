package org.robolectric.res;

import org.robolectric.util.Strings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StyleData implements Style {
  private final String packageName;
  private final String name;
  private final String parent;
  private final Map<Integer, AttributeResource> items = new LinkedHashMap<>();

  public StyleData(String packageName, String name, String parent) {
    this.packageName = packageName;
    this.name = name;
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public String getParent() {
    return parent;
  }

  public void add(int resId, AttributeResource attribute) {
    items.put(resId, attribute);
  }

  @Override public AttributeResource getAttrValue(int resId) {
    return items.get(resId);
  }

  public boolean grep(Pattern pattern) {
    for (AttributeResource attributeResource : items.values()) {
      if (pattern.matcher(attributeResource.resName.getFullyQualifiedName()).find()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof StyleData)) {
      return false;
    }
    StyleData other = (StyleData) obj;

    return Strings.equals(packageName, other.packageName)
        && Strings.equals(name, other.name)
        && Strings.equals(parent, other.parent)
        && items.size() == other.items.size();
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    hashCode = 31 * hashCode + Strings.nullToEmpty(packageName).hashCode();
    hashCode = 31 * hashCode + Strings.nullToEmpty(name).hashCode();
    hashCode = 31 * hashCode + Strings.nullToEmpty(parent).hashCode();
    hashCode = 31 * hashCode + items.size();
    return hashCode;
  }

  @Override public String toString() {
    return "Style " + packageName + ":" + name;
  }

  public String getPackageName() {
    return packageName;
  }
}
