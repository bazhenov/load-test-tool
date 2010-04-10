package com.farpost.ldt;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class PojoTask<T> implements Task {

  private T object;
  private Method method;

  public PojoTask(Class<T> type, String methodName)
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
    if (methodName == null) {
      throw new NullPointerException("Method name should be not null");
    }
    object = type.getConstructor().newInstance();
    method = type.getMethod(methodName);
  }

  public PojoTask(T object, String methodName) throws NoSuchMethodException {
    if (methodName == null) {
      throw new NullPointerException("Method name should be not null");
    }
    this.object = object;
    method = object.getClass().getMethod(methodName);
  }

  public PojoTask(T object) throws NoSuchMethodException {
    this(object, "execute");
  }

  public void prepare() throws Exception {
    try {
      object.getClass().getMethod("prepare").invoke(object);
    } catch (NoSuchMethodException e) {
    }
  }

  public void execute() throws Exception {
    method.invoke(object);
  }

  public void cleanup() throws Exception {
    try {
      object.getClass().getMethod("cleanup").invoke(object);
    } catch (NoSuchMethodException e) {
    }
  }

  public void setParameters(Map<String, String> parameters) throws IllegalArgumentException {
    try {
      BeanUtils.populate(object, parameters);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public T getTaskObject() {
    return object;
  }
}
