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


public class BufferedChannelTest {

    private class Sender implements Routine{

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
    public void testReceive() throws Exception {
        Channel<String> channel = new BufferedChannel<String>(2);
        boolean channelClosedExceptionThrown = false;
        boolean timeoutExceptionThrown = false;

        Lois.go(new Sender(channel));
        assert channel.receive().equals("dude");

        Lois.go(new Sender(channel));
        assert channel.receive(200, TimeUnit.MILLISECONDS).equals("dude");

        Lois.go(new Sender(channel));
        try {
            //sender can't send anything because channel is closed before it sends
            channel.receive(1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e){
            timeoutExceptionThrown = true;
        }
        assert timeoutExceptionThrown;

        channel.send("dudet");
        channel.close();
        try {
            channel.receive();
        } catch (ChannelClosedException e){
            channelClosedExceptionThrown = true;
        }
        assert !channelClosedExceptionThrown;

        try {
            channel.receive();
        } catch (ChannelClosedException e){
            channelClosedExceptionThrown = true;
        }
        assert channelClosedExceptionThrown;
    }

    @Test
    public void testTryReceive() throws Exception{
        Channel<String> channel = new BufferedChannel<String>(1);
        boolean channelClosedExceptionThrown = false;

        assert channel.tryReceive() == null;
        channel.send("dude");
        assert channel.tryReceive().equals("dude");

        channel.send("dude");
        channel.close();
        channel.tryReceive();

        try {
            channel.tryReceive();
        } catch(ChannelClosedException e) {
            channelClosedExceptionThrown = true;
        }
        assert channelClosedExceptionThrown;
    }


    @Test
    public void testSend() throws Exception {

        Channel<String> channel = new BufferedChannel<String>(1);
        boolean timeoutExceptionThrown = false;
        boolean channelClosedExceptionThrown = false;
        Lois.go(new Receiver((ReceiveChannel<String>)channel));

        try {
            channel.send("dude",10,TimeUnit.MICROSECONDS);
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


    @Test
    public void testClose() throws Exception {
        Channel<String> channel = new BufferedChannel<String>(1);
        boolean channelClosedExceptionThrown = false;
        channel.close();
        assert !channel.isOpen();

        try {
            channel.receive();
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
    public void testIsOpen() throws Exception {
        Channel<String> channel = new BufferedChannel<String>(1);
        assert channel.isOpen();
        channel.close();
        assert !channel.isOpen();
    }

    @Test
    public void testIsReceivable() throws Exception {
        Channel<String> channel = new BufferedChannel<String>(2);
        assert !channel.isReceivable();
        channel.send("dude");
        assert channel.isReceivable();
    }

    @Test
    public void testIsSendable() throws Exception {
        Channel<String> channel = new BufferedChannel<String>(2);
        assert channel.isSendable();
        channel.send("dude");
        assert channel.isSendable();
        channel.send("dude");
        assert !channel.isSendable();
    }
}
