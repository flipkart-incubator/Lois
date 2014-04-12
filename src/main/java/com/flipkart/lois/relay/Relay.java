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

package com.flipkart.lois.relay;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;

/**
 * A {@link Routine} that takes a {@link SendChannel} and a {@link ReceiveChannel}.
 * When executed it passes on any messages received on the {@link ReceiveChannel}
 * to the {@link SendChannel}. The Routine finishes if the {@link ReceiveChannel}
 * is closed and there are no more messages to consume or when the {@link Routine}
 * is interrupted
 * @param <T>
 */
public class Relay<T> implements Routine{

    private final SendChannel<T> sendChannel;
    private final ReceiveChannel<T> receiveChannel;

    /**
     * Constructor that takes a {@link SendChannel} and {@link ReceiveChannel}
     * @param sendChannel
     * @param receiveChannel
     */
    public Relay(SendChannel<T> sendChannel, ReceiveChannel<T> receiveChannel) {
        this.sendChannel = sendChannel;
        this.receiveChannel = receiveChannel;
    }

    /**
     * executes a loop which is constantly trying to receive on the {@link ReceiveChannel}
     * and send it to the {@link SendChannel}
     */
    @Override
    public void run() {
        T message;
        while (true){
            try {
                message = receiveChannel.receive();
                sendChannel.send(message);
            } catch (ChannelClosedException e) {
                break;
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
