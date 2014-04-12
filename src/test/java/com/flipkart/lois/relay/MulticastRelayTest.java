package com.flipkart.lois.relay;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.impl.SimpleChannel;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulticastRelayTest {
    @Test
    public void testRun() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Channel<String> sourceChannel = new SimpleChannel<String>();
        Channel<String> sinkChannel1 = new SimpleChannel<String>();
        Channel<String> sinkChannel2 = new SimpleChannel<String>();

        List<SendChannel<String>> sinkChannels = new ArrayList<SendChannel<String>>();
        sinkChannels.add(sinkChannel1);
        sinkChannels.add(sinkChannel2);


        MulticastRelay<String> stringRelay = new MulticastRelay<String>(sinkChannels,sourceChannel);
        executorService.execute(stringRelay);
        sourceChannel.send("dude");

        assert sinkChannel1.receive().equals("dude");
        assert sinkChannel2.receive().equals("dude");

        sinkChannel1.close();
        sourceChannel.send("dudet");
        assert sinkChannel2.receive().equals("dudet");
    }
}
