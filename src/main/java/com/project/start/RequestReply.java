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

            MapMessage mapMessage=jmsContext.createMapMessage();
            mapMessage.setBoolean("isSingle",true);
            mapMessage.setString("Gender","Male");

            producer.send(inboundQueue,mapMessage);


            MapMessage messageReceived=(MapMessage) jmsContext.createConsumer(inboundQueue).receive();
            System.out.println(messageReceived.getBoolean("isSingle")+" "+messageReceived.getString("Gender"));


        }
    }
}
