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

package com.flipkart.lois;


import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.relay.MulticastRelay;
import com.flipkart.lois.relay.Relay;
import com.flipkart.lois.routine.Routine;
import com.flipkart.lois.utils.Replicant;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Lois {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Runs {@link Routine}es on a threadpool where each routine is executed on a single thread
     * @param routines
     */
    public static void go(Routine... routines) {
        go(Arrays.asList(routines));
    }

    /**
     * Runs {@link Routine}es on a threadpool where each routine is executed on a single thread
     * @param routines
     */
    public static void go(List<Routine> routines) {
        for (Routine routine: routines){
            executorService.execute((Runnable)routine);
        }
    }

    /**
     * Multiplexes messages of type {@link T}, from multiple {@link ReceiveChannel} sources to a single
     * {@link SendChannel} sink
     * @param sinkChannel
     * @param sourceChannels
     * @param <T>
     */
    public static <T> void mux(final SendChannel<T> sinkChannel, final ReceiveChannel<T>... sourceChannels) {
        mux(sinkChannel, Arrays.asList(sourceChannels));
    }

    /**
     * Multiplexes messages of type {@link T}, from multiple {@link ReceiveChannel} sources to a single
     * {@link SendChannel} sink
     * @param sinkChannel
     * @param sourceChannels
     * @param <T>
     */
    public static <T> void mux(final SendChannel<T> sinkChannel, final List<ReceiveChannel<T>> sourceChannels){
        for (ReceiveChannel<T> sourceChannel: sourceChannels){
            Lois.go(new Relay<T>(sinkChannel, sourceChannel) );
        }
    }

    /**
     * Demultiplexes messages of type {@link T}, from a single {@link ReceiveChannel} source to multiple
     * {@link SendChannel} sinks
     * @param sourceChannel
     * @param sinkChannels
     * @param <T>
     */
    public static <T> void deMux(final ReceiveChannel<T> sourceChannel, final SendChannel<T>... sinkChannels){
        deMux(sourceChannel, Arrays.asList(sinkChannels));
    }

    /**
     * Demultiplexes messages of type {@link T}, from a single {@link ReceiveChannel} source to multiple
     * {@link SendChannel} sinks
     * @param sourceChannel
     * @param sinkChannels
     * @param <T>
     */
    public static <T> void deMux(final ReceiveChannel<T> sourceChannel, final List<SendChannel<T>>sinkChannels){
        for (SendChannel<T> sinkChannel: sinkChannels){
            Lois.go(new Relay<T>(sinkChannel, sourceChannel) );
        }
    }

    /**
     * Multicasts messages of type {@link T}, from a single {@link ReceiveChannel} source to multiple
     * {@link SendChannel} sinks. Can block on a sink that's not isSendable
     * @param sourceChannel
     * @param sinkChannels
     * @param <T>
     */
    public static <T> void multiCast(final ReceiveChannel<T> sourceChannel, final SendChannel<T>... sinkChannels){
        multiCast(sourceChannel,Arrays.asList(sinkChannels));
    }

    /**
     * Multicasts messages of type {@link T}, from a single {@link ReceiveChannel} source to multiple
     * {@link SendChannel} sinks. Can block on a sink that's not isSendable
     * @param sourceChannel
     * @param sinkChannels
     * @param <T>
     */
    public static <T> void multiCast(final ReceiveChannel<T> sourceChannel, final List<SendChannel<T>> sinkChannels){
        Lois.go(new MulticastRelay<T>(sinkChannels,sourceChannel) );
    }

}
