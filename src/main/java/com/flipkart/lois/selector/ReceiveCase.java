package com.flipkart.lois.selector;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: shashwat
 * Created: 04/10/15.
 */
public class ReceiveCase<E> implements ChannelSelector.Case {
    public final ReceiveChannel<E> ch;
    public final Consumer<E> consumer;

    public ReceiveCase(ReceiveChannel<E> ch, Consumer<E> consumer) {
        this.ch = ch;
        this.consumer = consumer;
    }

    public boolean tryExec() throws ChannelClosedException {
        final E val = ch.isReceivable() ? ch.tryReceive() : null;
        if (val != null) {
            consumer.accept(val);
            return true;
        }

        return false;
    }

    public static class Builder<T> {
        private final SelectorBuilder selectorBuilder;
        private final ReceiveChannel<T> channel;

        Builder(SelectorBuilder selectorBuilder, ReceiveChannel<T> channel) {
            this.selectorBuilder = selectorBuilder;
            this.channel = channel;
        }

        public SelectorBuilder use(Consumer<T> consumer) {
            selectorBuilder.add(new ReceiveCase<T>(channel, consumer));
            return selectorBuilder;
        }
    }

}

