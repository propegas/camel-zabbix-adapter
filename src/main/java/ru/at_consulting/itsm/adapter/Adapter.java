package ru.at_consulting.itsm.adapter;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.at_consulting.itsm.adapter.EventToObjectBean;

/**
 * A Camel Application
 */
public class Adapter {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
	
	private static Logger logger = LoggerFactory.getLogger(Adapter.class);
	public static String activemq_port = null;
	public static String activemq_ip = null;
	public static String eventpath = null;
	
    public static void main(String[] args) throws Exception {
   
		if ( args.length == 3  ) {
			activemq_port = (String)args[1];
			activemq_ip = (String)args[0];
			eventpath = (String)args[2];
		}
		
		if (activemq_port == null || activemq_port == "" )
			activemq_port = "61616";
		if (activemq_ip == null || activemq_ip == "" )
			activemq_ip = "172.20.19.195";
		if (eventpath == null || eventpath == "" )
			eventpath = "C:\\incoming_adapter_events";
		
		System.setProperty("eventpath", eventpath);
		
		logger.info("activemq_ip: " + activemq_ip);
		logger.info("activemq_port: " + activemq_port);
		logger.info("eventpath: " + eventpath);
    	
    	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory
				("tcp://" + activemq_ip + ":" + activemq_port);
    	
    	SimpleRegistry registry = new SimpleRegistry();
    	registry.put("XmlToObject", new EventToObjectBean());
    	
    	CamelContext context = new DefaultCamelContext(registry);
    	
    	context.addComponent("activemq",JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
    	
    	context.addRoutes(new MyRouteBuilder());
    	context.start();
    	
    	
    	while (true) { Thread.sleep(1000); }
    	
    }

}

