package com.flipkart.lois.relay;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.impl.SimpleChannel;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RelayTest {

    @Test
    public void testRun() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Channel<String> sourceChannel = new SimpleChannel<String>();
        Channel<String> sinkChannel = new SimpleChannel<String>();

        Relay<String> stringRelay = new Relay<String>(sinkChannel, sourceChannel);
        executorService.execute(stringRelay);
        sourceChannel.send("dude");
        assert sinkChannel.receive().equals("dude");

        assert !sourceChannel.isReceivable();
        sourceChannel.close();
    }
}
