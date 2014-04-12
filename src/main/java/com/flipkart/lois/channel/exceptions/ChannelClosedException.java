package com.flipkart.lois.channel.exceptions;

/**
 * This is thrown when invalid operations on Channel are executed after it has been closed
 */

public class ChannelClosedException extends Exception{
    public ChannelClosedException() {
        super();
    }

    public ChannelClosedException(String message) {
        super(message);
    }

    public ChannelClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelClosedException(Throwable cause) {
        super(cause);
    }
}
