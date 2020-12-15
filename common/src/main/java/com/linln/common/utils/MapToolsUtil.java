package com.linln.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapToolsUtil {
    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> javaBeanMap(Object javaBean) {
        Map<String, Object> map = new HashMap<>();
        Method[] methods = javaBean.getClass().getMethods(); // 获取所有方法
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                String field = method.getName(); // 拼接属性名
                field = field.substring(field.indexOf("get") + 3);
                field = field.toLowerCase().charAt(0) + field.substring(1);
                Object value = null; // 执行方法
                try {
                    value = method.invoke(javaBean, (Object[]) null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                map.put(field, value);
            }
        }
        return map;
    }

    /**
     * Map转对象的方法
     */
  /*  public static Object mapJavaBean(Class<?> clazz, Map<String, Object> map) {
        Object javabean = null; // 构建对象
        try {
            javabean = clazz.newInstance();
            Method[] methods = clazz.getMethods(); // 获取所有方法
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    String field = method.getName(); // 截取属性名
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    if (map.containsKey(field)) {
                        method.invoke(javabean, map.get(field));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return javabean;
    } */

    /**
     * Map转对象的方法
     */
    public static Object mapJavaBean(Class<?> clazz, Map<String, String> map) {
        Object javabean = null; // 构建对象
        try {
            javabean = clazz.newInstance();
            Method[] methods = clazz.getMethods(); // 获取所有方法
            for (Method method : methods) {
                if (method.getName().startsWith("set")) {
                    String field = method.getName(); // 截取属性名
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    if (map.containsKey(field)) {
                        method.invoke(javabean, map.get(field));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return javabean;
    }
}
