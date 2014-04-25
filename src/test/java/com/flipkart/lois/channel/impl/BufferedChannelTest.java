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
    private class DummyChannel<T> implements Channel<T>{

        private String value = "secret";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public T receive() throws ChannelClosedException, InterruptedException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public T receive(long timeOut, TimeUnit timeUnit) throws ChannelClosedException, InterruptedException, TimeoutException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public T tryReceive() throws ChannelClosedException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isReceivable() throws ChannelClosedException {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void send(T message) throws ChannelClosedException, InterruptedException {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void send(T message, long timeOut, TimeUnit timeUnit) throws ChannelClosedException, TimeoutException, InterruptedException {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean trySend(T message) throws ChannelClosedException {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void close() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isOpen() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isSendable() throws ChannelClosedException {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

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
        channel.receive();
        assert channel.isSendable();
        channel.close();
        assert !channel.isSendable();
    }

    @Test
    public void replicateTest() throws Exception{
        Channel<Channel> channelChannel = new SimpleChannel<Channel>();
        Channel<Pojo> pojoChannel = new SimpleChannel<Pojo>();

        Pojo suman = new Pojo("suman");
        DummyChannel<String> stringChannel = new DummyChannel<String>();

        pojoChannel.send(suman);
        Pojo karthik = pojoChannel.receive();
        karthik.setName("karthik");

        assert suman.getName().equals("suman"); //assert deepcopy has happened while passing message

        channelChannel.send(stringChannel);
        DummyChannel<String> targetChannel = (DummyChannel<String>)channelChannel.receive();
        targetChannel.setValue("changed");

        assert stringChannel.getValue().equals("changed");
    }
}
