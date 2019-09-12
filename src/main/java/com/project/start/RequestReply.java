package com.project.start;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

public class RequestReply {
    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext=new InitialContext();
        Queue inboundQueue= (Queue) initialContext.lookup("queue/inboundQueue");


        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()
        ){
            JMSProducer producer=jmsContext.createProducer();
            TemporaryQueue outboundQueue=jmsContext.createTemporaryQueue();
            TextMessage message=jmsContext.createTextMessage("THIS IS A REQUEST MESSAGE");
            message.setJMSReplyTo(outboundQueue);
            producer.send(inboundQueue,message);
            System.out.println(message.getJMSMessageID());

            Map<String,TextMessage> requestMessages=new HashMap<>();
            requestMessages.put(message.getJMSMessageID(),message);


            JMSConsumer consumer=jmsContext.createConsumer(inboundQueue);
            TextMessage msgReceived=(TextMessage) consumer.receive();
            System.out.println(msgReceived.getText());

            JMSProducer replyProducer=jmsContext.createProducer();
            TextMessage replyMessage=jmsContext.createTextMessage("REPLY SENT BACK");
            replyMessage.setJMSCorrelationID(msgReceived.getJMSMessageID());
            replyProducer.send(msgReceived.getJMSReplyTo(),replyMessage);

            JMSConsumer replyConsumer=jmsContext.createConsumer(outboundQueue);
            TextMessage replyReceived=(TextMessage)replyConsumer.receive();
            System.out.println(replyReceived.getJMSCorrelationID());
            System.out.println(requestMessages.get(replyReceived.getJMSCorrelationID()).getText());


        }
    }
}
