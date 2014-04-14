/**
 * Copyright 2014 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.lois.channel.impl;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.utils.Replicant;
import com.rits.cloning.Cloner;

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
    private final int bufferSize;

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
            buffer.put(replicateMessage(message));
        else
            throw new ChannelClosedException("Channel has been closed");
    }

    @Override
    public void send(final T message, final long timeOut, final TimeUnit timeUnit) throws ChannelClosedException, InterruptedException, TimeoutException {
        boolean sent=false;
        if(isOpen())
            sent = buffer.offer(replicateMessage(message), timeOut, timeUnit);
        else
            throw new ChannelClosedException("Channel has been closed");

        if(!sent)
            throw new TimeoutException("Send Operation Timed Out");
    }

    @Override
    public boolean trySend(T message) throws ChannelClosedException {
        if (isOpen())
            return buffer.offer(replicateMessage(message));
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

    //creates deep copies of all messages that aren't channels
    private static <T> T replicateMessage(T message){
        if (message instanceof SendChannel ||message instanceof ReceiveChannel){
            return message;
        } else {
            return Replicant.replicate(message);
        }
    }
}
