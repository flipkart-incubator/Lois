package com.flipkart.lois.channel.impl;


import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import org.testng.annotations.Test;

public class SimplePassByRefChannelTest {

    @Test
    public void simpleChannelTest() throws ChannelClosedException, InterruptedException {
        Channel<String> channel = new SimplePassByRefChannel<String>();
        assert channel.isSendable();
        channel.send("dude");
        assert !channel.isSendable();
    }

}

