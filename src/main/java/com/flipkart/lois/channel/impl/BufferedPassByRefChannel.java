package com.flipkart.lois.channel.impl;

import com.flipkart.lois.channel.exceptions.ChannelClosedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A buffered channel that passes an object by reference. This should only be used with immutable
 * messages that ensure that any mutable state isn't leaked. This is more efficient than {@link BufferedChannel}
 * because it does not make deep copies of messages.
 * @param <T>
 */
public class BufferedPassByRefChannel<T> extends BufferedChannel<T>{

    public BufferedPassByRefChannel(int bufferSize) {
        super(bufferSize);
    }

    @Override
    public void send(final T message) throws ChannelClosedException, InterruptedException {
        if (isOpen())
            buffer.put(message);
        else
            throw new ChannelClosedException("Channel has been closed");
    }

    @Override
    public void send(final T message, final long timeOut, final TimeUnit timeUnit) throws ChannelClosedException, InterruptedException, TimeoutException {
        boolean sent;
        if(isOpen())
            sent = buffer.offer(message, timeOut, timeUnit);
        else
            throw new ChannelClosedException("Channel has been closed");

        if(!sent)
            throw new TimeoutException("Send Operation Timed Out");
    }

    @Override
    public boolean trySend(T message) throws ChannelClosedException {
        if (isOpen())
            return buffer.offer(message);
        else
            throw new ChannelClosedException("Channel has been closed");
    }

}
