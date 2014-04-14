#Lois
####Golang like channels for java

Lois is a Java library that provides golang like channel abstraction and implementation.
Go's channel abstraction is heavily influenced by Communicating Sequential Processes(CSP) and
Process calculus. The pivotal idea behind concurrent process communicating over channels is

**"Don't communicate by sharing state, share state by communicating"**

Lois brings the power and flexibility of this concurrent computational paradigm to Java.

###Channel
####A conduit for communication and coordination

In Lois a channel is a mechanism for two independent threads of execution or **Routines** to either communicate or
coordinate with each other. A channel can be typed and will carry a message only of the appropriate type, it can
also be untyped allowing it to carry a message of any type.

```java
/**
* This is a typed channel
*/
Channel<String> typedChannel = new SimpleChannel<String>();

/**
* This is an untyped channel
*/
Channel untypedChannel = new SimpleChannel();
```

####Send and Receive

Send and receive are the most basic operations on a channel. The variants of these operations are the fundamental
way in which threads and Routines use to communicate and coordinate with each other. Let's look at the basic send
and receive operations over a channel.

```java
/**
 * Thread 1 send's a message over a typed channel
 */
typedChannel.send(" Hello ");

/**
 * Thread 2 receives a message over a typed channel
 */
String message = typedChannel.receive();
```

In the above example we see how a channel can be used to send and receive messages between concurrent threads or Routines.
Both send and receive can block and place the calling thread in a wait state until the message is either sendable or
receivable.

```java
Channel<String> typedChannel = new SimpleChannel<String>();

typedChannel.send(" Hello ");

/**
 * Since a SimpleChannel can carry only one message
 * at a time, calling send on the channel when the
 * previous message hasn't been "received" yet
 * blocks the thread and puts it in a wait state.
 */
typedChannel.send(" World! ");
```

Similarly receive blocks on a channel until there is a message to receive on it.

```java
 /**
 * Receive blocks the thread and put's it in a wait
 * state until there is something to receive over
 * the channel.
 */
String message = typedChannel.receive();
```

One can use variants of send and receive with timeouts to avoid blocking threads indefinitely.

```java
/**
 * A variant of send that takes a long and a TimeUnit to
 * timeout on a channel. The following code waits for 10
 * milliseconds and timesout to throw a  TimeoutException.
 */
typedChannel.send(" time's running out! ",10, TimeUnit.MILLISECONDS);

/**
 * A variant of receive that takes a long and a TimeUnit to
 * timeout on a channel. The following code waits for 10
 * milliseconds and timesout to throw a  TimeoutException.
 */
typedChannel.receive(10, TimeUnit.MILLISECONDS);
```

One can also use non blocking variants of send and receive.

```java
/**
 * A non blocking variant of send that attempts to send a
 * message over the channel. It returns "true" if the
 * message could be successfully sent, or a false if the
 * message could not be sent over the channel.
 */
typedChannel.trySend(" trying to send ");

/**
 * A non blocking variant of receive that attempts to
 * receive a message over the channel. It returns the
 * message if a message was successfully received, or
 * a "null" if a message could not be received over
 * the channel.
 */
typedChannel.tryReceive();
```

One can also check whether a channel is ready to send or receive messages by calling **isSendable** and **isReceivable**
on the channel. However, if a channel is being shared by multiple threads that send and multiple threads that receive
then these can't be safely used to send/receive because the state of the channel could be modified by the time a
send/receive is called.

```java
/**
 * Return's true if the channel has space to accept
 * messages
 */
channel.isSendable();

/**
 * Return's true if the channel has atleast one message
 * that can be received.
 */
channel.isReceivable();
```

####Closed for business

A channel can be in one of two states; either **open** or **closed**. By default, at creation all channel's are open
and can send or receive messages freely. But a channel can be closed and once closed cannot be opened again.

```java
/**
 * One can close a channel by calling close on it
 */
channel.close();
```

Once a channel is closed trying to send any message's over it will throw a **ChannelClosedException**. One can still
receive all pending messages in the channel, but once all the pending messages have been received calling receive on
the channel results in a **ChannelClosedException**.

```java
channel.close();

/**
 * Throws a ChannelClosedException
 */
channel.send(" doomed to fail ");
```

One has to think carefully about how and when to close a channel. Since a channel could potentially be shared by multiple
threads of execution, closing a channel would make it impossible for other channels to send messages over it. One can
check whether a channel is open or closed in the following way.

```java
/**
 * Return's true if the channel is open false if closed
 */
channel.isOpen();
```

####Buffered and Simple channels

The difference between a Buffered and a Simple channel is the number of messages each can successfully hold. A simple
channel can hold only one message in the channel.

```java
/**
 * This is a simple channel
 */
Channel simpleChannel = new SimpleChannel();

simpleChannel.send("hello");

/**
 * This blocks if the first message isn't received yet
 * because a SimpleChannel has a capacity of one message
 */
simpleChannel.send("world");
```

A buffered channel on the other hand can hold a variable number of messages. The capacity of a buffered channel is
specified at the time of creation.

```java
/**
 * A buffered channel with a capacity of 3
 */
Channel bufferedChannel = new BufferedChannel(3);

channel.send(1);
channel.send(2);
channel.send(3);

/**
 * This blocks on send if the first 3 messages haven't
 * been received yet.
 */
channel.send(4);
```

Simple channels are useful for fine grained coordination while buffered channels are performant and useful when dealing
with multiple or bursty senders and receivers.

#### Send only or Receive only channels

A channel by default can be used for full duplex communication, i.e it can be used to both send and receive messages by
any thread that has access to it. However, most of the time a thread would only use a channel to either send or recieve
messages exclusively. To enforce this behaviour one can use send or recieve channels

```java
/**
 * This channel can only be used to send messages
 */
SendChannel sendChannel = new SimpleChannel();
sendChannel.send("I can send only");

/**
 * This channel can only be used to receive messages
 */
ReceiveChannel receiveChannel = new SimpleChannel();
receiveChannel.receive();
```

###Routines

Routines are simple runnables that can be run by Lois on independent threads.

```java
/**
* Simple routine that accepts a channel as a constructor
* parameter.
*/
Routine sampRoutine = new SampRoutine(stringChannel);

/**
* Start the routine on an independent thread which can then
* receive or send messages over the channel.
*/
Lois.go(sampRoutine);
```

One need not use routines to use channels. Any way of sharing reference to a channel by independent threads should
enable them to use the channel to send and receive messages.

###Value vs Reference

The value of **"Don't communicate by sharing state, share state by communicating"** can only be realized if there is
no shared state among concurrent threads. To accomplish this one should refrain from sharing references to the same
object, hence any message that is sent over a channel is passed on as a value rather than a reference. This is
accomplished by deep cloning the message before sending it across. This makes sure that multiple threads can have access
to the value of the message without a danger of shared state being accidentally modified.

However, there is one exception to this pass by value semantic. Any message that is a channel will be passed by reference.
This ensures that a channel can be sent over channels while still retaining the ability to communicate/coordinate with
any threads that still have a reference to the sent/received channel. This leads to incredible flexibility and power
where channels can be used to dynamically alter the network of communicating and coordinating nodes at runtime.

###Higher order channel usage

Lois also provides several simple ways of connecting channels together to create useful patterns.

#####Multiplexing several channels into one

The **mux** call multiplexes the messages from several source channels onto one sink channel.

```java
/**
 * Send only channels that will be multiplexed
 */
SendChannel sourceChannel1 = new SimpleChannel();
SendChannel sourceChannel2 = new BufferedChannel(3);

/**
 * Receive only channel that will be used to output
 * the muxed messages
 */
ReceiveChannel combinedChannel = new SimpleChannel();

/**
 * A variadic method that muxes source channels into
 * the sink channel. It takes all messages recieved on
 * souceChannels and transfers them to the combined
 * channel.
 */
Lois.mux(combinedChannel,sourceChannel1, sourceChannel2);
```

#####Demultiplexing a single channel into several

The **deMux** call de-multiplexes the messages from a single source channel onto several sink channels.

```java
/**
 * Receive only channel that will be Demultiplexed
 */
ReceiveChannel sourceChannel = new SimpleChannel();

/**
 * Send only channels that will be used to output
 * the Demuxed messages
 */
SendChannel sinkChannel1 = new SimpleChannel();
SendChannel sinkChannel2 = new SimpleChannel();

/**
 * A variadic method that Demuxes source channel into
 * the sink channels. It takes all messages recieved on
 * souceChannel and transfers them to exactly one of
 * the sink channels
 */
Lois.deMux(sourceChannel,sinkChannel1, sinkChannel2);
```

#####Multicasting

The **multiCast** call multicasts the messages from a single source channel onto several sink channels.

```java
/**
 * Receive only channel that will be multicasted
 */
ReceiveChannel sourceChannel = new SimpleChannel();

/**
 * Send only channels that will be used to output
 * the multicasted messages
 */
SendChannel sinkChannel1 = new SimpleChannel();
SendChannel sinkChannel2 = new SimpleChannel();

/**
 * A variadic method that multicasts source channel into
 * the sink channels. It takes all messages recieved on
 * souceChannel and sends them on all of  the sink channels
 */
Lois.multiCast(sourceChannel,sinkChannel1, sinkChannel2);
```

These are just some simple ways in which channels can be combined, they are by no means exhaustive and similar higher
order constructs between can be built with ease, one is only limited by one's imagination.

###Examples

####Simple parllelization

In this example we create a simple web page downloader using multiple parallel crawlers
and a web page persister.

```java
//Create a list to hold worker channels
List<Channel<WebPage>> crawlerChannels = new ArrayList<Channel<WebPage>>();

//create 10 crawlers each with a dedicated channel over which they will
//send the webpages they crawl.
for (int workerCount=0;workerCount<10;workerCount++){

    //create a crawlerChannel
    Channel<WebPage> crawlerChannel = new BufferedChannel<WebPage>(10);

    //run a crawler on an independent thread with a beginning url and
    //a crawlerChannel over which to send web pages
    Lois.go(new Crawler(getBeginUrl(), crawlerChannel));

    //add the crawler channel to list of crawlerchannels
    crawlerChannels.add(crawlerChannel);
}

//create a sink channel to consume messages from all the crawler channels
SendChannel<WebPage> sinkChannel = new BufferedChannel<WebPage>(10);

//multiplex crawler channels on to a sink channel
Lois.mux(sinkChannel, crawlerChannels);

//persist webpages on disk
Lois.go(new WebPagePersister(sinkChannel));
```

####Rudimentary Connection Pool

In this example we create a simple, threadsafe connection pool.

```java
//creates a list of 5 connections
List<Connection> connectionList = ConnectionFactory.createConnections("localhost", 80, 5);

//create a BufferedChannel to hold 5 connections
Channel<Connection> connectionPoolChannel = new BufferedChannel<Connection>(5);

for (Connection connection: connectionList){
    connectionPoolChannel.send(connection);
}

//to take a connection from the pool any thread can receive a connection
Connection connection = connectionPoolChannel.receive();

//to release a connection any thread can send it to the channel
connectionPoolChannel.send(connection);
```

##Maven Artifact

Add the following repository to your pom.xml

```xml
    <repository>
      <id>clojars</id>
      <name>Clojars repository</name>
      <url>https://clojars.org/repo</url>
    </repository>
```

And add the following dependency to start using Lois in your maven project.

```xml
   <dependency>
     <groupId>com.flipkart.lego</groupId>
     <artifactId>lois</artifactId>
     <version>1.1.0</version>
   </dependency>
```

##Documentation

The api docs can be found [here](http://flipkart-incubator.github.io/Lois/javadoc/index.html)

##Contribution, Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/flipkart-incubator/Lois/issues).
Please follow the [contribution guidelines](https://github.com/flipkart-incubator/Lois/blob/master/CONTRIBUTING.md) when submitting pull requests.

##License

Copyright 2014 Flipkart Internet, pvt ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

