package com.csz.http.util;

/**
 * @author caishuzhan
 */
public class Utils {

    public static boolean isExitsClass(String className){
        try {
            Class<?> name = Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
