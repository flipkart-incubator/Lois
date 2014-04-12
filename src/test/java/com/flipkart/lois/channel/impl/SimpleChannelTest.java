package com.flipkart.lois.channel.impl;


import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import org.testng.annotations.Test;

public class SimpleChannelTest {

    @Test
    public void simpleChannelTest() throws ChannelClosedException, InterruptedException {
        Channel<String> channel = new SimpleChannel<String>();
        assert channel.isSendable();
        channel.send("dude");
        assert !channel.isSendable();
    }

}
