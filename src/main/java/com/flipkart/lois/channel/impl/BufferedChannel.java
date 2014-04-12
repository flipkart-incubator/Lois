package com.flipkart.lois.channel.impl;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Bufferred Channel that can contain "buffer size" messages before it
 * blocks on send.
 * @param <T>
 */

public class BufferedChannel<T> implements Channel<T> {

    private final ArrayBlockingQueue<T> buffer;
    private boolean isChannelOpen = true;
    private int bufferSize;

    public BufferedChannel(final int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new ArrayBlockingQueue<T>(bufferSize,true);
    }

    @Override
    public T receive() throws ChannelClosedException, InterruptedException {
        if (!isOpen() && buffer.isEmpty())
            throw new ChannelClosedException("Channel has been closed");
        else
            return buffer.take();
    }

    @Override
    public T receive(long timeOut, TimeUnit timeUnit) throws ChannelClosedException, TimeoutException, InterruptedException {
        if (!isOpen() && buffer.isEmpty())
            throw new ChannelClosedException("Channel has been closed");
        else
            return buffer.poll(timeOut,timeUnit);
    }


    @Override
    public void send(T message) throws ChannelClosedException, InterruptedException {
        if (isOpen())
            buffer.put(message);
    }

    @Override
    public void send(T message, long timeOut, TimeUnit timeUnit) throws ChannelClosedException, TimeoutException, InterruptedException {
        if(isOpen())
            buffer.offer(message, timeOut, timeUnit);
    }

    @Override
    public void close() {
        isChannelOpen = false;
    }

    @Override
    public boolean isOpen() {
        return isChannelOpen;
    }

    @Override
    public boolean isReceivable() throws ChannelClosedException {
        return buffer.remainingCapacity() < bufferSize;
    }

    @Override
    public boolean isSendable() throws ChannelClosedException {
        return buffer.remainingCapacity() > 0;
    }
}
