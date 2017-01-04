package org.robolectric.res;

public class StyleResourceLoader extends XpathResourceXmlLoader {
  private final PackageResourceTable resourceTable;

  public StyleResourceLoader(PackageResourceTable resourceTable) {
    super("/resources/style");
    this.resourceTable = resourceTable;
  }

  @Override
  protected void processNode(String name, XmlNode xmlNode, XmlContext xmlContext) {
    String styleName = xmlNode.getAttrValue("name");
    String styleParent = xmlNode.getAttrValue("parent");
    if (styleParent == null) {
      int lastDot = styleName.lastIndexOf('.');
      if (lastDot != -1) {
        styleParent = styleName.substring(0, lastDot);
      }
    }

    String styleNameWithUnderscores = underscorize(styleName);
    StyleData styleData = new StyleData(xmlContext.getPackageName(), styleNameWithUnderscores, underscorize(styleParent));

    for (XmlNode item : xmlNode.selectElements("item")) {
      String attrName = item.getAttrValue("name");
      String value = item.getTextContent();

      ResName attrResName = ResName.qualifyResName(attrName, xmlContext.getPackageName(), "attr");
      Integer resId = resourceTable.getResourceId(attrResName);
      styleData.add(resId, new AttributeResource(attrResName, value, xmlContext.getPackageName()));
    }

    resourceTable.addValue("style", styleNameWithUnderscores, new TypedResource<>(styleData, ResType.STYLE, xmlContext));
  }

  private String underscorize(String s) {
    return s == null ? null : s.replace('.', '_');
  }
}
