package com.project.start;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReply {
    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        InitialContext initialContext=new InitialContext();
        Queue inboundQueue= (Queue) initialContext.lookup("queue/inboundQueue");
        Queue outboundQueue= (Queue) initialContext.lookup("queue/outboundQueue");

        try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()
        ){
            JMSProducer producer=jmsContext.createProducer();
           // producer.setTimeToLive(2000);
            //TemporaryQueue outboundQueue=jmsContext.createTemporaryQueue();
            TextMessage message=jmsContext.createTextMessage("THIS IS A REQUEST MESSAGE");
            message.setJMSReplyTo(outboundQueue);
            producer.send(inboundQueue,message);
            //Thread.sleep(4000);
            JMSConsumer consumer=jmsContext.createConsumer(inboundQueue);
            TextMessage msgReceived=(TextMessage) consumer.receive(5000);
            System.out.println(msgReceived.getText());

            JMSProducer replyProducer=jmsContext.createProducer();
            replyProducer.send(msgReceived.getJMSReplyTo(),"REPLY SENT BACK");

            JMSConsumer replyConsumer=jmsContext.createConsumer(outboundQueue);
            System.out.println(replyConsumer.receiveBody(String.class));


        }
    }
}
