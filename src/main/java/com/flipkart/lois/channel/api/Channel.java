package com.flipkart.lois.channel.api;

/**
 * A simple "go" like channel abstraction for java
 * @param <T>
 */

public interface Channel<T> extends SendChannel<T>, ReceiveChannel<T> {

}
