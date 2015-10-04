package com.flipkart.lois.selector;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: shashwat
 * Created: 04/10/15.
 */
public class SelectorBuilder {
    public static final ChannelSelector.Case[] EMPTY_ARR = new ChannelSelector.Case[0];
    private final ArrayList<ChannelSelector.Case> cases = new ArrayList<ChannelSelector.Case>();

    public <T> SendCase.Builder<T> caseSend(SendChannel<T> channel) {
        return new SendCase.Builder<T>(this, channel);
    }

    public <T> ReceiveCase.Builder<T> caseReceive(ReceiveChannel<T> channel) {
        return new ReceiveCase.Builder<T>(this, channel);
    }

    public <T> void add(ChannelSelector.Case caseObj) {
        cases.add(caseObj);
    }

    public ChannelSelector build() {
        return new ChannelSelector(cases.toArray(EMPTY_ARR));
    }
}
