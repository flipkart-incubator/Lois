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
