package com.flipkart.lois.channel.api;

import com.flipkart.lois.channel.exceptions.ChannelClosedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple "go" like channel abstraction for java
 * over which messages can only be received
 * @param <T>
 */
public interface ReceiveChannel<T> {

    /**
     * Receive a message of Type {@link T} if message is available or block the thread and put it in wait state
     * until there is a message available. If the channel has been closed and there are no more messages to be
     * delivered, throw a {@link com.flipkart.lois.channel.exceptions.ChannelClosedException}
     * @return a message of Type {@link T}
     * @throws com.flipkart.lois.channel.exceptions.ChannelClosedException
     */
    T receive() throws ChannelClosedException, InterruptedException;

    /**
     * Receive a message of Type {@link T} if message is available or block the thread and put it in wait state
     * until there is a message available or timeout {@link java.util.concurrent.TimeUnit}'s have passed. If the channel has been closed
     * and there are no more messages to be delivered, throw a {@link ChannelClosedException}. If the timeout has
     * been breached throw a {@link java.util.concurrent.TimeoutException}
     * @param timeOut
     * @param timeUnit
     * @return a message of Type {@link T}
     * @throws ChannelClosedException
     * @throws java.util.concurrent.TimeoutException
     */
    T receive(long timeOut, TimeUnit timeUnit) throws ChannelClosedException, TimeoutException, InterruptedException;

    /**
     * Close channel so that no new messages can be sent over this channel. The messages that are already available
     * on the channel can be consumed.
     */
    void close();

    /**
     * Returns true if channel is open and false if channel is closed.
     * @return true if channel is open, false if channel is closed
     */
    boolean isOpen();

    /**
     * Returns true if a message can be received on the channel
     * @return true if a message can be received on the channel
     * @throws ChannelClosedException
     */
    boolean isReceivable() throws ChannelClosedException;
}
