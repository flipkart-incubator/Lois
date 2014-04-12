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

package com.flipkart.lois.channel.api;

import com.flipkart.lois.channel.exceptions.ChannelClosedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple "go" like channel abstraction for java
 * over which messages can only be sent
 * @param <T>
 */
public interface SendChannel<T> {

    /**
     * Send a message of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not free put the calling thread in a wait state until it becomes available to send
     * a message. If the channel has been closed then a {@link com.flipkart.lois.channel.exceptions.ChannelClosedException} is thrown.
     * @param message
     * @throws com.flipkart.lois.channel.exceptions.ChannelClosedException
     * @throws InterruptedException
     */
    void send(T message) throws ChannelClosedException, InterruptedException;

    /**
     * Send a message of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not free put the calling thread in a wait state until it becomes available to send
     * a message until timeOut {@link java.util.concurrent.TimeUnit}'s have passed, at which point throw a {@link java.util.concurrent.TimeoutException}.
     * If the channel has been closed then a {@link ChannelClosedException} is thrown.
     * @param message
     * @param timeOut
     * @param timeUnit
     * @throws ChannelClosedException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    void send(T message, long timeOut, TimeUnit timeUnit) throws ChannelClosedException, TimeoutException ,InterruptedException;

    /**
     * A non blocking send of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not free return false, if the channel is available and send was successful return true.
     * @param message
     * @return true if send was successful, false if send failed at this instance.
     * @throws ChannelClosedException
     */
    boolean trySend(T message) throws ChannelClosedException;

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
     * @throws ChannelClosedException
     */
    boolean isSendable() throws ChannelClosedException;

}
