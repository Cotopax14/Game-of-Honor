package com.airbus.alna.framework.utils;

/**
 * Utility functions for handling of string.
 *
 * @author Wilko Kempa
 * @version 1.0
 */
public class Strings {

    /**
     * Returns true if s is null or has length 0 or only contains spaces, else false.
     * @param s  a string to be tested.
     * @return true if s is null or has length 0 or only contains spaces, else false.
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.trim().length() == 0;
    }
}
