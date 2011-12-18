/*
 * This file is part of Java Simple ServiceManager
 * 
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * Copyright (C) hdsdi3g for hd3g.tv 2011
 * 
*/

package hd3gtv.javasimpleservice;

import hd3gtv.log2.Log2;
import hd3gtv.log2.Log2Dump;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

@SuppressWarnings("nls")
/**
 * @author hdsdi3g
 * @version 1.0
 */
public class JmxPublisher {
	
	private Log2 log;
	private MBeanServer mbs;
	
	public JmxPublisher(Log2 log, int port) throws IOException {
		if (log == null) {
			throw new NullPointerException("\"log\" can't to be null");
		}
		this.log = log;
		
		System.setProperty("java.rmi.server.randomIDs", "true");
		/*System.setProperty("com.sun.management.jmxremote.port", String.valueOf(port));
		System.setProperty("com.sun.management.jmxremote.ssl", "false");
		System.setProperty("com.sun.management.jmxremote.authenticate", "false");
		*/
		
		LocateRegistry.createRegistry(port);
		mbs = ManagementFactory.getPlatformMBeanServer();
		
		HashMap<String, Object> env = new HashMap<String, Object>();
		
		final String hostname = InetAddress.getLocalHost().getHostName();
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + hostname + ":" + port + "/jndi/rmi://" + hostname + ":" + port + "/jmxrmi");
		
		JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);
		
		cs.start();
		
		Log2Dump dump = new Log2Dump();
		dump.add("jmx.proto", url.getProtocol());
		dump.add("jmx.host", url.getHost());
		dump.add("jmx.port", url.getPort());
		dump.add("jmx.url", url.getURLPath());
		
		log.info("JMX is start", dump);
	}
	
	/**
	 * Publie un objet avec dans la categorie categoryname.
	 * Si l'objet est/etait deja publie, alors on depublie l'ancien, et on publie le nouveau.
	 */
	public synchronized void addPublishedobjects(String categoryname, Object object) {
		if (categoryname == null) {
			throw new NullPointerException("\"categoryname\" can't to be null");
		}
		if (object == null) {
			throw new NullPointerException("\"object\" can't to be null");
		}
		Log2Dump dump = new Log2Dump();
		dump.add("cat", categoryname);
		dump.add("obj", object);
		
		try {
			ObjectName name = new ObjectName(categoryname + ":type=" + object.getClass().getSimpleName());
			try {
				mbs.unregisterMBean(name);
			} catch (Exception e) {
			}
			mbs.registerMBean(object, name);
			log.debug("Register JMX MBean", dump);
		} catch (JMException e1) {
			log.error("Can't register JMX MBean", e1, dump);
		}
	}
	
	/**
	 * Publie un objet avec dans la categorie categoryname.
	 * Si l'objet est/etait deja publie, alors on depublie l'ancien, et on publie le nouveau.
	 */
	public synchronized void removePublishedobjects(String categoryname, Object object) {
		if (categoryname == null) {
			throw new NullPointerException("\"categoryname\" can't to be null");
		}
		if (object == null) {
			throw new NullPointerException("\"object\" can't to be null");
		}
		Log2Dump dump = new Log2Dump();
		dump.add("cat", categoryname);
		dump.add("obj", object);
		
		try {
			ObjectName name = new ObjectName(categoryname + ":type=" + object.getClass().getSimpleName());
			try {
				mbs.unregisterMBean(name);
				log.debug("Unregister JMX MBean", dump);
			} catch (Exception e) {
			}
		} catch (JMException e1) {
			log.error("Can't unregister JMX MBean", e1, dump);
		}
	}
	
}
