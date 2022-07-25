package com.fis.fisdk;

import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect28Util {

    static {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Class classClazz = Class.class;
                Field classLoaderField = classClazz.getDeclaredField("classLoader");
                classLoaderField.setAccessible(true);
                classLoaderField.set(Reflect28Util.class, null);
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