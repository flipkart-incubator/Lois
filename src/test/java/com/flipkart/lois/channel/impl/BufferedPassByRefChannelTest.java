package com.flipkart.lois.channel.impl;

import com.flipkart.lois.Lois;
import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class BufferedPassByRefChannelTest {

    private class Pojo{
        private String name;

        private Pojo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private class Sender implements Routine {

        private SendChannel<String> sendChannel;
        public Sender(SendChannel<String> stringSendChannel){
            this.sendChannel = stringSendChannel;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
                try {
                    sendChannel.send("dude");
                } catch (ChannelClosedException e) {
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private class Receiver implements Routine{

        private ReceiveChannel<String> receiveChannel;

        private Receiver(ReceiveChannel<String> receiveChannel) {
            this.receiveChannel = receiveChannel;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
                receiveChannel.receive();
            } catch (InterruptedException e) {
            } catch (ChannelClosedException e) {
            }
        }
    }


    @Test
    public void testSend() throws Exception {

        Channel<String> channel = new BufferedPassByRefChannel<String>(1);
        boolean timeoutExceptionThrown = false;
        boolean channelClosedExceptionThrown = false;
        Lois.go(new Receiver((ReceiveChannel<String>) channel));

        try {
            channel.send("dude",10, TimeUnit.MICROSECONDS);
        } catch (TimeoutException e){
            timeoutExceptionThrown = true;
        }
        assert !timeoutExceptionThrown;

        timeoutExceptionThrown = false;

        try {
            channel.send("dude",10,TimeUnit.MICROSECONDS);
        } catch (TimeoutException e){
            timeoutExceptionThrown = true;
        }
        assert timeoutExceptionThrown;

        Thread.sleep(100);
        channel.close();

        try {
            channel.send("dude",10,TimeUnit.MICROSECONDS);
        } catch (ChannelClosedException e){
            channelClosedExceptionThrown = true;
        }
        assert channelClosedExceptionThrown;

        channelClosedExceptionThrown = false;

        try {
            channel.send("dude");
        } catch (ChannelClosedException e){
            channelClosedExceptionThrown = true;
        }
        assert channelClosedExceptionThrown;

    }

    @Test
    public void testTrySend() throws Exception{
        Channel<String> channel = new BufferedChannel<String>(1);
        boolean channelClosedExceptionThrown = false;

        assert channel.trySend("dude");
        assert !channel.trySend("dude");
        channel.close();

        try {
            channel.trySend("dude");
        } catch(ChannelClosedException e) {
            channelClosedExceptionThrown = true;
        }
        assert channelClosedExceptionThrown;
    }
}
