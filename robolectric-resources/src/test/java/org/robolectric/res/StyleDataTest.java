package org.robolectric.res;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class StyleDataTest {

  private final int androidSearchViewStyle = 1;
  private final int myLibSearchViewStyle = 2;
  private final int myAppSearchViewStyle = 3;

  @Test
  public void getAttrValue_willFindLibraryResourcesWithSameName() {
    StyleData styleData = new StyleData("library.resource", "Theme_MyApp", "Theme_Material");
    styleData.add(myLibSearchViewStyle, new AttributeResource(new ResName("a", "b", "c"), "lib_value", "library.resource"));

    assertThat(styleData.getAttrValue(myAppSearchViewStyle).value).isEqualTo("lib_value");
    assertThat(styleData.getAttrValue(myLibSearchViewStyle).value).isEqualTo("lib_value");

    assertThat(styleData.getAttrValue(androidSearchViewStyle)).isNull();
  }

  @Test
  public void getAttrValue_willNotFindFrameworkResourcesWithSameName() {
    StyleData styleData = new StyleData("android", "Theme_Material", "Theme");
    styleData.add(androidSearchViewStyle, new AttributeResource(new ResName("a", "b", "c"), "android_value", "android"));

    assertThat(styleData.getAttrValue(androidSearchViewStyle).value).isEqualTo("android_value");

    assertThat(styleData.getAttrValue(myAppSearchViewStyle)).isNull();
    assertThat(styleData.getAttrValue(myLibSearchViewStyle)).isNull();
  }

  @Test
  public void getAttrValue_willChooseBetweenAmbiguousAttributes() {
    StyleData styleData = new StyleData("android", "Theme_Material", "Theme");
    styleData.add(myLibSearchViewStyle, new AttributeResource(new ResName("a", "b", "c"), "lib_value", "library.resource"));
    styleData.add(androidSearchViewStyle, new AttributeResource(new ResName("a", "b", "c"), "android_value", "android"));

    assertThat(styleData.getAttrValue(androidSearchViewStyle).value).isEqualTo("android_value");
    assertThat(styleData.getAttrValue(myLibSearchViewStyle).value).isEqualTo("lib_value");

    // todo: any packageNames that aren't 'android' should be treated as equivalent
//    assertThat(styleData.getAttrValue(myAppSearchViewStyle).value).isEqualTo("lib_value");
  }
}
