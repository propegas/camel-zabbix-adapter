package ru.at_consulting.itsm.adapter;

import org.apache.camel.language.NamespacePrefix;
import org.apache.camel.language.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.at_consulting.itsm.event.Event;

public class EventToObjectBean {
	
	private static Logger logger = LoggerFactory.getLogger(Event.class);
	
	public enum PersistentEventSeverity {
	    OK, INFO, WARNING, MINOR, MAJOR, CRITICAL;
		
	    public String value() {
	        return name();
	    }

	    public static PersistentEventSeverity fromValue(String v) {
	        return valueOf(v);
	    }
	}
	
	public Event toObject(
			@XPath(value = "/ns:zabbixEvent/ns:host/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String host,
			@XPath(value = "/ns:zabbixEvent/ns:triggername/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String message,
			@XPath(value = "/ns:zabbixEvent/@eventid", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String externalid,
			@XPath(value = "/ns:zabbixEvent/ns:severity/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String severity,
			@XPath(value = "/ns:zabbixEvent/ns:date/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String date,
			@XPath(value = "/ns:zabbixEvent/ns:time/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String time,
			@XPath(value = "/ns:zabbixEvent/ns:status/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String status,
			@XPath(value = "/ns:zabbixEvent/ns:hostgroup/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String hostgroup,
			@XPath(value = "/ns:zabbixEvent/ns:template/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String template,
			@XPath(value = "/ns:zabbixEvent/value/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String value,
			@XPath(value = "/ns:zabbixEvent/ns:triggerid/text()", namespaces = @NamespacePrefix(prefix = "ns", uri = "http://skuf.gosuslugi.ru/mon/") ) String object) {

		
		TimeConversion tc = new TimeConversion();
		long timestamp = tc.timeConversion(date.concat("-").concat(time));
		
		if (status.equals("PROBLEM")) {
			status = "OPEN";
		} else {
			status = "CLOSED";
		}
		/*
		if (severity.equals("Disaster")) {
			severity = "CRITICAL";
		}
		
		if (severity.equals("High")) {
			severity = "MAJOR";
		}
		
		if (severity.equals("Average")) {
			severity = "MINOR";
		}			
		*/
		
		Event ev = new Event();
		ev.setMessage(String.format("%s: %s", message, value));
		ev.setSeverity(severity.toUpperCase());
		ev.setSeverity(setRightSeverity(severity.toUpperCase()));
		ev.setStatus(status);
		//ev.setParametr(value);
		ev.setExternalid(externalid);
		ev.setTimestamp(timestamp);
		ev.setHost(host);
		ev.setObject(object);
		ev.setEventCategory(hostgroup.replaceFirst("\\s+$", ""));
		ev.setModule(template.replaceFirst("\\s+$", ""));
		ev.setEventsource("ZABBIX");
		
		
//		StringBuilder sb = new StringBuilder();
//		sb.append("insert into event ");
//		sb.append("(Date_reception, Event_handle, Mc_host, Severity, Msg) values (");
//		sb.append("'").append(reception).append("', ");
//		sb.append("'").append(id).append("', ");
//		sb.append("'").append(host).append("', ");
//		sb.append("'").append(severity.toUpperCase()).append("', ");
//		sb.append("'").append(message).append("') ");


//		System.out.println("##################################################");

//		System.out.println(ev.getMessage().toString());
//		System.out.println(ev.host);
		return ev;
	}
	
	private String setRightSeverity(String severity)
	{
		String newseverity = "";
		/*
		 * 
		Severity
 “NORMAL”
 “WARNING”
 “MINOR”
 “MAJOR”
 “CRITICAL”
		 */
		
		
		
		switch (severity) {
        	case "DISASTER":  newseverity = PersistentEventSeverity.CRITICAL.name();break;
        	case "HIGH":  newseverity = PersistentEventSeverity.MAJOR.name();break;
        	case "AVERAGE":  newseverity = PersistentEventSeverity.MINOR.name();break;
        	case "WARNING":  newseverity = PersistentEventSeverity.WARNING.name();break;
        	case "INFORMATION":  newseverity = PersistentEventSeverity.INFO.name();break;
        	
        	default:  newseverity = PersistentEventSeverity.INFO.name();break;

		}
		logger.debug("***************** severity: " + severity);
		logger.debug("***************** newseverity: " + newseverity);
		return newseverity;
	}
}
