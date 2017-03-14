package cz.scholz.amqp.democlient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

/**
 * Message Listener
 */
public class ExceptionListener implements javax.jms.ExceptionListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionListener.class);
    private boolean exceptionReceived = false;

    public void onException(JMSException ex)
    {
        LOGGER.error("Exception caught from connection object. Reconnect needed. Exiting ... ", ex);
        this.exceptionReceived = true;
    }

    public boolean isExceptionReceived()
    {
        return this.exceptionReceived;
    }
}