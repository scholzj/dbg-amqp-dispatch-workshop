package cz.scholz.amqp.democlient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.UUID;

/**
 * Broadcast Receiver
 * Receives broadcasts from the persistent broadcast queue
 */
public class DateRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateRequest.class);

    private final InitialContext context;

    public DateRequest() throws NamingException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        try {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
            properties.setProperty("connectionfactory.connection", String.format(
                    "amqp://%s:%d?amqp.idleTimeout=30000",
                    "localhost",
                    5672));
            properties.setProperty("queue.time", "/time");
            properties.setProperty("queue.date", "/date");
            properties.setProperty("queue.response", UUID.randomUUID().toString());
            this.context = new InitialContext(properties);
        } catch (NamingException ex) {
            LOGGER.error("Unable to proceed with broadcast receiver", ex);
            throw ex;
        }
    }

    public void run() throws JMSException, NamingException, InterruptedException {
        Connection connection = null;
        Session session = null;
        MessageProducer requestProducer = null;
        MessageConsumer responseConsumer = null;

        try {
            LOGGER.info("Creating connection");
            connection = ((ConnectionFactory) context.lookup("connection")).createConnection();
            connection.setExceptionListener(new ExceptionListener());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            LOGGER.info("Connected");

            responseConsumer = session.createConsumer((Destination) context.lookup("response"));
            requestProducer = session.createProducer((Destination) context.lookup("date"));

            Message request = session.createMessage();
            request.setJMSReplyTo((Destination) context.lookup("response"));
            requestProducer.send(request);

            Message response = responseConsumer.receive(1000000);

            LOGGER.info("RECEIVED RESPONSE:");
            LOGGER.info("#################");
            if (response instanceof TextMessage)
            {
                TextMessage textMessage = (TextMessage) response;
                String messageText = textMessage.getText();
                LOGGER.info("Response = {}", messageText);
            }
            else if (response instanceof BytesMessage)
            {
                BytesMessage bytesMessage = (BytesMessage) response;
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < bytesMessage.getBodyLength(); i++)
                {
                    builder.append((char) bytesMessage.readByte());
                }
                LOGGER.info("Response = {}", builder.toString());
            }
            else
            {
                LOGGER.error("Unexpected message type delivered: {}", response.toString());
            }
            LOGGER.info("#################");


        } catch (JMSException | NamingException e) {
            LOGGER.error("Unable to proceed with request receiver", e);
            throw e;
        } finally {
            if (responseConsumer != null) {
                System.out.println("Closing consumer");
                responseConsumer.close();
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
        DateRequest client = new DateRequest();
        client.run();
    }
}