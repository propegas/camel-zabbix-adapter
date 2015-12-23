package ru.at_consulting.itsm.adapter;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
//import org.apache.camel.*;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.at_consulting.itsm.event.Event;
//import ru.atc.camel.nnm.devices.Main;


public class MyRouteBuilder extends RouteBuilder {
	
	private static Logger logger = LoggerFactory.getLogger(MyRouteBuilder.class);
	String eventFilesPath = System.getProperty("eventpath");
	String outQueueName = System.getProperty("outqueuename", "activemq:Zabbix-tgc1-Events.queue");
	
    public void configure() {
    	
    	JsonDataFormat myJson = new JsonDataFormat();
		myJson.setPrettyPrint(true);
		myJson.setLibrary(JsonLibrary.Jackson);
		myJson.setJsonView(Event.class);
		
		PropertiesComponent properties = new PropertiesComponent();
		properties.setLocation("classpath:zabbix.properties");
		getContext().addComponent("properties", properties);

    	from("file:".concat(eventFilesPath))
                    .log("File received")

                    .beanRef("XmlToObject", "toObject")
                    .marshal(myJson)
                    .to("activemq:{{eventsqueue}}")
                    .log(LoggingLevel.INFO, "*** NEW EVENT: ${id}");;
    }

}


