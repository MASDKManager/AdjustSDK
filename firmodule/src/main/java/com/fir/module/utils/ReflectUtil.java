package com.fir.module.utils;

import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    static {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Class classClazz = Class.class;
                Field classLoaderField = classClazz.getDeclaredField("classLoader");
                classLoaderField.setAccessible(true);
                classLoaderField.set(ReflectUtil.class, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static Method getDeclaredMethod(Class<?> clz, String name, Class<?>... parameterType)
            throws NoSuchMethodException {
        return clz.getDeclaredMethod(name, parameterType);
    }

}