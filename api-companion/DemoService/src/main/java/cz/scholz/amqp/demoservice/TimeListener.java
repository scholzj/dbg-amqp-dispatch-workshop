package cz.scholz.amqp.demoservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.*;
import java.time.LocalTime;

/**
 * Message Listener
 */
public class TimeListener implements MessageListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeListener.class);
    private final Session session;

    public TimeListener(Session session)
    {
        this.session = session;
    }

    public void onMessage(Message msg)
    {
        LOGGER.info("RECEIVED /time REQUEST:");
        LOGGER.info("#################");
        respond(msg);
        LOGGER.info("#################");
    }

    public void respond(Message msg)
    {
        try {
            if (msg.getJMSReplyTo() != null) {
                LOGGER.info("Sending response to " + msg.getJMSReplyTo());
                Message responseMessage = session.createTextMessage(LocalTime.now().toString());
                MessageProducer responseProducer = session.createProducer(msg.getJMSReplyTo());
                responseProducer.send(responseMessage);
            }
        } catch (JMSException e) {
            LOGGER.error("Ups, something went wrong when responding ...", e);
        }
    }
}