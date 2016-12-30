package org.robolectric.internal;

import org.jetbrains.annotations.NotNull;
import org.robolectric.internal.bytecode.ShadowInvalidator;
import org.robolectric.internal.dependency.DependencyResolver;
import org.robolectric.internal.bytecode.ShadowMap;
import org.robolectric.res.*;

public class VirtualEnvironment {
  private final SdkConfig sdkConfig;
  private final ClassLoader robolectricClassLoader;
  private final ShadowInvalidator shadowInvalidator;
  private ShadowMap shadowMap = ShadowMap.EMPTY;
  private ResourceTable systemResourceTable;
  public static final String ANDROID_PACKAGE_NAME = android.R.class.getPackage().getName();

  public VirtualEnvironment(SdkConfig sdkConfig, ClassLoader robolectricClassLoader) {
    this.sdkConfig = sdkConfig;
    this.robolectricClassLoader = robolectricClassLoader;
    shadowInvalidator = new ShadowInvalidator();
  }

  public synchronized ResourceTable getSystemResourceTable(DependencyResolver dependencyResolver) {
    if (systemResourceTable == null) {
      ResourcePath resourcePath = createRuntimeSdkResourcePath(dependencyResolver);
      PackageResourceIndex resourceIndex = new PackageResourceIndex(ANDROID_PACKAGE_NAME);
      ResourceExtractor.populate(resourceIndex, resourcePath.getRClass(), resourcePath.getInternalRClass());
      systemResourceTable = new ResourceTable(resourceIndex);
      ResourceParser.load(ANDROID_PACKAGE_NAME, resourcePath, systemResourceTable);
    }
    return systemResourceTable;
  }

  @NotNull
  private ResourcePath createRuntimeSdkResourcePath(DependencyResolver dependencyResolver) {
    try {
      Fs systemResFs = Fs.fromJar(dependencyResolver.getLocalArtifactUrl(sdkConfig.getAndroidSdkDependency()));
      Class<?> androidRClass = getRobolectricClassLoader().loadClass("android.R");
      Class<?> androidInternalRClass = getRobolectricClassLoader().loadClass("com.android.internal.R");
      return new ResourcePath(androidRClass,
          systemResFs.join("res"), systemResFs.join("assets"),
          androidInternalRClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> Class<T> bootstrappedClass(Class<?> testClass) {
    return bootstrappedClass(testClass.getName());
  }

  public <T> Class<T> bootstrappedClass(String className) {
    try {
      return (Class<T>) robolectricClassLoader.loadClass(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public ClassLoader getRobolectricClassLoader() {
    return robolectricClassLoader;
  }

  public ShadowInvalidator getShadowInvalidator() {
    return shadowInvalidator;
  }

  public SdkConfig getSdkConfig() {
    return sdkConfig;
  }

  public ShadowMap replaceShadowMap(ShadowMap shadowMap) {
    ShadowMap oldMap = this.shadowMap;
    this.shadowMap = shadowMap;
    return oldMap;
  }
}