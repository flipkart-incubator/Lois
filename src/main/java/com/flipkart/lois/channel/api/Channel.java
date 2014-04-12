package com.flipkart.lois.channel.api;

import com.flipkart.lois.channel.api.exceptions.ChannelClosedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple "go" like channel abstraction for java
 * @param <T>
 */

public interface Channel<T> {

    /**
     * Receive a message of Type {@link T} if message is available or block the thread and put it in wait state
     * until there is a message available. If the channel has been closed and there are no more messages to be
     * delivered, throw a {@link ChannelClosedException}
     * @return a message of Type {@link T}
     * @throws ChannelClosedException
     * @throws UnsupportedOperationException
     */
    T receive() throws ChannelClosedException, UnsupportedOperationException;

    /**
     * Receive a message of Type {@link T} if message is available or block the thread and put it in wait state
     * until there is a message available or timeout {@link TimeUnit}'s have passed. If the channel has been closed
     * and there are no more messages to be delivered, throw a {@link ChannelClosedException}. If the timeout has
     * been breached throw a {@link TimeoutException}
     * @param timeOut
     * @param timeUnit
     * @return a message of Type {@link T}
     * @throws ChannelClosedException
     * @throws UnsupportedOperationException
     * @throws TimeoutException
     */
    T receive(Long timeOut, TimeUnit timeUnit) throws ChannelClosedException, UnsupportedOperationException, TimeoutException;

    /**
     * Send a message of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not free put the calling thread in a wait state until it becomes available to send
     * a message. If the channel has been closed then a {@link ChannelClosedException} is thrown.
     * @param message
     * @throws ChannelClosedException
     * @throws UnsupportedOperationException
     */
    void send(T message) throws ChannelClosedException, UnsupportedOperationException;

    /**
     * Send a message of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not, free put the calling thread in a wait state until it becomes available to send
     * a message until timeOut {@link TimeUnit}'s have passed, at which point throw a {@link TimeoutException}.
     * If the channel has been closed then a {@link ChannelClosedException} is thrown.
     * @param message
     * @param timeOut
     * @param timeUnit
     * @throws ChannelClosedException
     * @throws UnsupportedOperationException
     * @throws TimeoutException
     */
    void send(T message, Long timeOut, TimeUnit timeUnit) throws ChannelClosedException, UnsupportedOperationException, TimeoutException;

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
     * Returns true if a message can be sent on the channel
     * @return true if a message can be sent on the channel
     * @throws UnsupportedOperationException
     * @throws ChannelClosedException
     */
    boolean isSendable() throws ChannelClosedException, UnsupportedOperationException;

    /**
     * Returns true if a message can be received on the channel
     * @return true if a message can be received on the channel
     * @throws UnsupportedOperationException
     * @throws ChannelClosedException
     */
    boolean isReceivable() throws ChannelClosedException, UnsupportedOperationException;
}
