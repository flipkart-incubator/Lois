package com.flipkart.lois;


import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.relay.MulticastRelay;
import com.flipkart.lois.relay.Relay;
import com.flipkart.lois.routine.Routine;

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
     * {@link SendChannel} sinks
     * @param sourceChannel
     * @param sinkChannels
     * @param <T>
     */
    public static <T> void multiCast(final ReceiveChannel<T> sourceChannel, final SendChannel<T>... sinkChannels){
        multiCast(sourceChannel,Arrays.asList(sinkChannels));
    }

    /**
     * Multicasts messages of type {@link T}, from a single {@link ReceiveChannel} source to multiple
     * {@link SendChannel} sinks
     * @param sourceChannel
     * @param sinkChannels
     * @param <T>
     */
    public static <T> void multiCast(final ReceiveChannel<T> sourceChannel, final List<SendChannel<T>> sinkChannels){
        Lois.go(new MulticastRelay<T>(sinkChannels,sourceChannel) );
    }



}
