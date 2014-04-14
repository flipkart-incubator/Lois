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

import java.util.Iterator;
import java.util.List;

/**
 * A {@link Routine} that takes a {@link List} of {@link SendChannel}s and a {@link ReceiveChannel}.
 * When executed it passes on any messages received on the {@link ReceiveChannel}
 * to the {@link SendChannel}s. The Routine finishes if the {@link ReceiveChannel}
 * is closed and there are no more messages to consume or when the {@link Routine}
 * is interrupted
 * @param <T>
 */
public class MulticastRelay<T> implements Routine{

    private final List<SendChannel<T>> sendChannels;
    private final ReceiveChannel<T> receiveChannel;

    /**
     * Constructor that takes a {@link List} of {@link SendChannel}s and {@link ReceiveChannel}
     * @param sendChannels
     * @param receiveChannel
     */
    public MulticastRelay(List<SendChannel<T>> sendChannels, ReceiveChannel<T> receiveChannel) {
        this.sendChannels = sendChannels;
        this.receiveChannel = receiveChannel;
    }

    /**
     * executes a loop which is constantly trying to receive on the {@link ReceiveChannel}
     * and send it to the {@link SendChannel}s. It only sends on those channels which are
     * open.
     */
    @Override
    public void run() {
        T message;
        while (true){
            try {
                message = receiveChannel.receive();
                for (Iterator<SendChannel<T>> sendChannelIterator = sendChannels.iterator(); sendChannelIterator.hasNext();){
                    try {
                        sendChannelIterator.next().send(message);
                    } catch (ChannelClosedException channelClosedException){
                        sendChannelIterator.remove();
                    }
                }
            } catch (ChannelClosedException channelClosedException) {
                break;
            } catch (InterruptedException channelClosedException) {
                break;
            }
        }
    }
}
