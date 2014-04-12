package com.flipkart.lois;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.channel.impl.BufferedChannel;
import com.flipkart.lois.channel.impl.SimpleChannel;
import com.flipkart.lois.routine.Routine;
import org.testng.annotations.Test;

public class LoisTest {

    private class SampRoutine implements Routine{

        Channel<String> channel;

        private SampRoutine(Channel<String> channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                channel.send("dude");
            } catch (ChannelClosedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Test
    public void testGo() throws Exception {
        Channel<String> stringChannel = new SimpleChannel<String>();
        SampRoutine sampRoutine = new SampRoutine(stringChannel);
        SampRoutine sampRoutine1 = new SampRoutine(stringChannel);

        Lois.go(sampRoutine, sampRoutine1);
        assert stringChannel.receive().equals("dude");
        assert stringChannel.receive().equals("dude");
        assert !stringChannel.isReceivable();
    }

    @Test
    public void testMux() throws Exception {
        SendChannel<String> sendChannel = new BufferedChannel<String>(2);
        ReceiveChannel<String> receiveChannel1 = new SimpleChannel<String>();
        ReceiveChannel<String> receiveChannel2 = new SimpleChannel<String>();

        Lois.mux(sendChannel, receiveChannel1, receiveChannel2);

        ((Channel<String>)receiveChannel1).send("ch1");
        ((Channel<String>)receiveChannel2).send("ch2");

        assert ((Channel<String>)sendChannel).receive().equals("ch1");
        assert ((Channel<String>)sendChannel).receive().equals("ch2");

        receiveChannel1.close();
        receiveChannel2.close();

        assert sendChannel.isOpen();
    }

    @Test
    public void testDeMux() throws Exception {
        Channel<String> sink1 = new BufferedChannel<String>(2);
        Channel<String> sink2 = new BufferedChannel<String>(2);
        Channel<String> channel = new BufferedChannel<String>(1);

        Lois.deMux(channel,sink1,sink2);
        channel.send("I");
        channel.send("Am");
        channel.send("The");
        channel.send("Champion");
        channel.send("My");
        channel.send("Friend");
        assert channel.isSendable();
        channel.send("!");
        assert !channel.isSendable();
        assert !sink1.isSendable();
        assert !sink2.isSendable();

        int sink1count = 0;
        int sink2count = 0;

        while (sink1.isReceivable()){
            sink1.receive();
            sink1count++;
        }
        while (sink2.isReceivable()){
            sink2.receive();
            sink2count++;
        }

        assert sink1count+sink2count == 7;
    }

    @Test
    public void testMultiCast() throws Exception {
        Channel<String> sink1 = new BufferedChannel<String>(2);
        Channel<String> sink2 = new BufferedChannel<String>(2);
        Channel<String> channel = new BufferedChannel<String>(1);

        Lois.multiCast(channel,sink1,sink2);
        channel.send("I");
        channel.send("Am");
        channel.send("The");
        channel.send("Champion");
        assert !channel.isSendable();
        assert !sink1.isSendable();
        assert !sink2.isSendable();

        int sink1count = 0;
        int sink2count = 0;

        while (sink1.isReceivable()){
            if (sink1count==0)
                assert sink1.receive().equals("I");
            else
                sink1.receive();
            sink1count++;
        }
        //count is 3 because sink2 is blocked at 3 and the next element in source can't be processed yet
        assert sink1count==3;

        while (sink2.isReceivable()){
            if (sink2count==0)
                assert sink2.receive().equals("I");
            else
                sink2.receive();
            sink2count++;
        }
        assert sink2count==4;

        assert sink1.receive().equals("Champion");

    }
}
