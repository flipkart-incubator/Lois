package com.flipkart.lois.selector;

import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;

import java.util.function.Supplier;

/**
 * Author: shashwat
 * Created: 04/10/15.
 */
public class SendCase<E> implements ChannelSelector.Case {
    public final SendChannel<E> ch;
    public final Supplier<E> supplier;

    public SendCase(SendChannel<E> ch, Supplier<E> supplier) {
        this.ch = ch;
        this.supplier = supplier;
    }

    public boolean tryExec() throws ChannelClosedException {
        return ch.isSendable() && ch.trySend(supplier.get());
    }

    public static class Builder<T> {
        private final SelectorBuilder selectorBuilder;
        private final SendChannel<T> channel;

        Builder(SelectorBuilder selectorBuilder, SendChannel<T> channel) {
            this.selectorBuilder = selectorBuilder;
            this.channel = channel;
        }

        public SelectorBuilder use(Supplier<T> supplier) {
            selectorBuilder.add(new SendCase<T>(channel, supplier));
            return selectorBuilder;
        }
    }
}
