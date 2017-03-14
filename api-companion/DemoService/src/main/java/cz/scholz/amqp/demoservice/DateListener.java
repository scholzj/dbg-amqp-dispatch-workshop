package cz.scholz.amqp.demoservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.time.LocalDate;

/**
 * Message Listener
 */
public class DateListener implements MessageListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DateListener.class);
    private final Session session;

    public DateListener(Session session)
    {
        this.session = session;
    }

    public void onMessage(Message msg)
    {
        LOGGER.info("RECEIVED /date REQUEST:");
        LOGGER.info("#################");
        respond(msg);
        LOGGER.info("#################");
    }

    public void respond(Message msg)
    {
        try {
            if (msg.getJMSReplyTo() != null) {
                LOGGER.info("Responding to " + msg.getJMSReplyTo());
                Message responseMessage = session.createTextMessage(LocalDate.now().toString());
                MessageProducer responseProducer = session.createProducer(msg.getJMSReplyTo());
                responseProducer.send(responseMessage);
            }
        } catch (JMSException e) {
            LOGGER.error("Ups, something went wrong when responding ...", e);
        }
    }
}