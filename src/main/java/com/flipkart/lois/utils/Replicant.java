package com.flipkart.lois.utils;

import com.rits.cloning.Cloner;

/**
 * Deep clone's objects, and messages to make sure that different processes don't share
 * state through reference to the same message.
 */
public class Replicant {
    private static Cloner cloner = new Cloner();

    /**
     * Return the deepclone of an object
     * @param object
     * @param <T>
     * @return deep clone of an object
     */
    public static <T> T replicate(T object){
        return cloner.deepClone(object);
    }

}
