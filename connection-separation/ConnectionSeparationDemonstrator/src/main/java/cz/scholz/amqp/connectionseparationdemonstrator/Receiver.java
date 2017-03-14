package cz.scholz.amqp.connectionseparationdemonstrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Broadcast Receiver
 * Receives broadcasts from the persistent broadcast queue
 */
public class Receiver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private final InitialContext context;
    private final int timeoutInMillis = 1000000;
    private final Listener listener = new Listener();

    public Receiver() throws NamingException
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        try
        {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            properties.setProperty("connectionfactory.connection", String.format(
                    "amqp://%s:%d?amqp.idleTimeout=30000",
                    "localhost",
                    5672));
            properties.setProperty("queue.queue1", String.format(
                    "%s",
                    "myQueue1"));
            properties.setProperty("queue.queue2", String.format(
                    "%s",
                    "myQueue2"));
            this.context = new InitialContext(properties);
        }
        catch (NamingException ex)
        {
            LOGGER.error("Unable to proceed with broadcast receiver", ex);
            throw ex;
        }
    }

    public void run() throws JMSException, NamingException, InterruptedException
    {
        /*
        * Step 1: Initializing the context based on the properties file we prepared
        */
        Connection connection = null;
        Session session = null;
        MessageConsumer broadcastConsumer = null, broadcastConsumer2 = null;

        try
        {
            /*
            * Step 2: Preparing the connection and session
            */
            LOGGER.info("Creating connection");
            connection = ((ConnectionFactory) context.lookup("connection")).createConnection();
            connection.setExceptionListener(listener);
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            /*
            * Step 3: Creating a broadcast receiver / consumer
            */
            broadcastConsumer = session.createConsumer((Destination) context.lookup("queue1"));
            broadcastConsumer.setMessageListener(listener);
            broadcastConsumer2 = session.createConsumer((Destination) context.lookup("queue2"));
            broadcastConsumer2.setMessageListener(listener);

            /*
            * Step 4: Starting the connection
            */
            connection.start();
            LOGGER.info("Connected");

            /*
            * Step 5: Receiving broadcast messages using listener for timeout seconds
            */
            LOGGER.info("Receiving broadcast messages for {} seconds", this.timeoutInMillis / 1000);
            synchronized (this)
            {
                this.wait(this.timeoutInMillis);
            }
            LOGGER.info("Finished receiving broadcast messages for {} seconds", this.timeoutInMillis / 1000);
        }
        catch (JMSException | NamingException | InterruptedException e)
        {
            LOGGER.error("Unable to proceed with broadcast receiver", e);
            throw e;
        }
        finally
        {
            /*
            * Step 6: Closing the connection
            */
            if (broadcastConsumer != null)
            {
                System.out.println("Closing consumer");
                broadcastConsumer.close();
            }
            if (session != null)
            {
                System.out.println("Closing session");
                session.close();
            }
            if (connection != null)
            {
                // implicitly closes session and producers/consumers
                System.out.println("Closing connection");
                connection.close();
            }
        }
    }

    public int getMessagesReceivedCount()
    {
        return this.listener.getMessagesReceivedCount();
    }

    public boolean isExceptionReceived()
    {
        return this.listener.isExceptionReceived();
    }

    public static void main(String[] args) throws JMSException, NamingException, InterruptedException
    {
        Receiver receiver = new Receiver();
        receiver.run();
    }
}
