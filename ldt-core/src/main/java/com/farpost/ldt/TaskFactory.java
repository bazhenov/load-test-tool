package com.farpost.ldt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class TaskFactory {

	public static Task createTask(String fqnClass)
		throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
		InvocationTargetException {

		String methodName = null;
		if (fqnClass.contains("#")) {
			String[] parts = fqnClass.split("#", 2);
			fqnClass = parts[0];
			methodName = parts[1];
		}
		Class<?> type = Class.forName(fqnClass);

		if (isAbstract(type)) {
			throw new IllegalArgumentException(type.getName() + " should not be not abstract class nor interface");
		}

		Object o = type.getConstructor().newInstance();
		if ( Task.class.isAssignableFrom(type) && methodName == null ) {
			return (Task) o;
		}else{
			return methodName == null
				? new PojoTask<Object>(o)
				: new PojoTask<Object>(o, methodName);
		}
	}

	static boolean isAbstract(Class<?> type) {
		return (type.getModifiers() & Modifier.ABSTRACT) != 0;
	}
}
