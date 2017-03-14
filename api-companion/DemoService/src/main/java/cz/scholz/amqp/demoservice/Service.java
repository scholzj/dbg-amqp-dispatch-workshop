package cz.scholz.amqp.demoservice;

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
public class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private final InitialContext context;
    private final int timeoutInMillis = 100000000;

    public Service() throws NamingException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        try {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            properties.setProperty("connectionfactory.connection", String.format(
                    "amqp://%s:%d?amqp.idleTimeout=30000&jms.forceAsyncSend=true",
                    "localhost",
                    5672));
            properties.setProperty("queue.time", "/time");
            properties.setProperty("queue.date", "/date");
            this.context = new InitialContext(properties);
        } catch (NamingException ex) {
            LOGGER.error("Unable to proceed with broadcast receiver", ex);
            throw ex;
        }
    }

    public void run() throws JMSException, NamingException, InterruptedException {
        /*
        * Step 1: Initializing the context based on the properties file we prepared
        */
        Connection connection = null;
        Session session = null;
        MessageConsumer requestConsumer = null, requestConsumer2 = null;

        try {
            /*
            * Step 2: Preparing the connection and session
            */
            LOGGER.info("Creating connection");
            connection = ((ConnectionFactory) context.lookup("connection")).createConnection();
            connection.setExceptionListener(new ExceptionListener());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            /*
            * Step 3: Creating a broadcast receiver / consumer
            */
            requestConsumer = session.createConsumer((Destination) context.lookup("time"));
            requestConsumer.setMessageListener(new TimeListener(session));
            requestConsumer2 = session.createConsumer((Destination) context.lookup("date"));
            requestConsumer2.setMessageListener(new DateListener(session));

            /*
            * Step 4: Starting the connection
            */
            connection.start();
            LOGGER.info("Connected");

            /*
            * Step 5: Receiving broadcast messages using listener for timeout seconds
            */
            synchronized (this) {
                this.wait(this.timeoutInMillis);
            }
        } catch (JMSException | NamingException | InterruptedException e) {
            LOGGER.error("Unable to proceed with request receiver", e);
            throw e;
        } finally {
            /*
            * Step 6: Closing the connection
            */
            if (requestConsumer != null) {
                System.out.println("Closing consumer");
                requestConsumer.close();
            }
            if (session != null) {
                System.out.println("Closing session");
                session.close();
            }
            if (connection != null) {
                // implicitly closes session and producers/consumers
                System.out.println("Closing connection");
                connection.close();
            }
        }
    }

    public static void main(String[] args) throws JMSException, NamingException, InterruptedException {
        Service service = new Service();
        service.run();
    }
}