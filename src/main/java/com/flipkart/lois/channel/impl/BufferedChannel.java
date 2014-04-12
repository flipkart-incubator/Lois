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
    private volatile boolean isChannelOpen = true;
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
    public T receive(final long timeOut, final TimeUnit timeUnit) throws ChannelClosedException, InterruptedException, TimeoutException {
        T message;
        if (!isOpen() && buffer.isEmpty())
            throw new ChannelClosedException("Channel has been closed");
        else
            message = buffer.poll(timeOut,timeUnit);

        if (message==null)
            throw new TimeoutException("Receive Operation Timed Out");
        else
            return message;
    }

    @Override
    public T tryReceive() throws ChannelClosedException {
        T message;
        message = buffer.poll();
        if (message==null && !isOpen())
            throw new ChannelClosedException("Channel has been closed");
        else
            return message;
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
        boolean sent=false;
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
