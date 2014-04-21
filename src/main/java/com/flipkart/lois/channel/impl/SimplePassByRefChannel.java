package com.flipkart.lois.channel.impl;

/**
 * A simple channel that can accomodate only one message at a time,
 * This channel passes by reference, so it should be used to only pass
 * immutable messages. It's more efficient than {@link SimpleChannel}
 * because it doesn't do a deep copy of the message.
 */
public class SimplePassByRefChannel<T> extends BufferedPassByRefChannel<T>{

    public SimplePassByRefChannel() {
        super(1);
    }
}
