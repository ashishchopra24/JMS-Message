package com.project.start;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReply {
    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext=new InitialContext();
        Queue inboundQueue= (Queue) initialContext.lookup("queue/inboundQueue");
        Queue expiryQueue= (Queue) initialContext.lookup("queue/expiryQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()
        ){
            JMSProducer producer=jmsContext.createProducer();
            producer.setTimeToLive(2000);
            TextMessage message=jmsContext.createTextMessage("THIS IS A REQUEST MESSAGE");
            producer.send(inboundQueue,message);
            Thread.sleep(4000);

            JMSConsumer consumer=jmsContext.createConsumer(inboundQueue);
            TextMessage msgReceived=(TextMessage) consumer.receive(5000);
            System.out.println(msgReceived);

            System.out.println(jmsContext.createConsumer(expiryQueue).receiveBody(String.class));


        }
    }
}
