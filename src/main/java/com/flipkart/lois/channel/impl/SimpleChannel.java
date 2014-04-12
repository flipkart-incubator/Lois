package com.flipkart.lois.channel.impl;

/**
 * A simple non buffered channel with a capacity form one message only.
 */
public class SimpleChannel<T> extends BufferedChannel<T>{
    public SimpleChannel() {
        super(1);
    }
}
