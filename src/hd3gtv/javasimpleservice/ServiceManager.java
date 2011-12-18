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
 * Copyright (C) hdsdi3g for hd3g.tv 2008-2011
 * 
*/

package hd3gtv.javasimpleservice;

import hd3gtv.log2.Log2;
import hd3gtv.log2.Log2Dump;
import hd3gtv.tools.TimeUtils;
import hd3gtv.tools.XmlData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public abstract class ServiceManager implements ServiceMBean {
	
	private ServiceInformations serviceinformations;
	private ServiceMessagemanager messagemanager;
	private ServiceWorker service;
	private File xmlconfigfile = null;
	private XmlData xmlconfiguration = null;
	private long javastarttime;
	
	protected abstract void startApplicationService(XmlData configuration) throws Exception;
	
	protected abstract void stopApplicationService() throws Exception;
	
	/**
	 * Le service c'est init, on le lance juste apres ceci.
	 */
	protected abstract void postClassInit() throws Exception;
	
	/**
	 * If this system property is not defined, it's defined.
	 * It allows to define a default value for a key and always have a no null value for this property key.
	 * @param key the key like -Dkey=value
	 * @param defaultvalue the value to set
	 */
	public static final void injectDefaultSystemProperty(String key, String defaultvalue) {
		String currentvalue = System.getProperty(key, ""); //$NON-NLS-1$
		if (currentvalue.trim().equals("")) { //$NON-NLS-1$
			System.setProperty(key, defaultvalue);
			
			Log2Dump dump = new Log2Dump();
			dump.add(key, defaultvalue);
			
			StringBuffer sb = new StringBuffer();
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			// 0 = Thread, 1 = ServiceManager, 2 = le vrai call
			sb.append(stack[2].getClassName());
			sb.append("."); //$NON-NLS-1$
			sb.append(stack[2].getMethodName());
			sb.append("("); //$NON-NLS-1$
			sb.append(stack[2].getFileName());
			sb.append(":"); //$NON-NLS-1$
			sb.append(stack[2].getLineNumber());
			sb.append(")"); //$NON-NLS-1$
			dump.add("by", sb); //$NON-NLS-1$
			
			Log2.log.debug("Set default SystemProperty", dump); //$NON-NLS-1$
		}
	}
	
	public synchronized void restart() {
		Log2.log.info("Manual restart service"); //$NON-NLS-1$
		stopService();
		startService();
	}
	
	public final ServiceInformations getServiceinformations() {
		return serviceinformations;
	}
	
	public final ServiceMessagemanager getMessagemanager() {
		return messagemanager;
	}
	
	public final String getJavaUptime() {
		return TimeUtils.secondsToYWDHMS((System.currentTimeMillis() - javastarttime) / 1000);
	}
	
	public final String getServiceUptime() {
		return TimeUtils.secondsToYWDHMS((System.currentTimeMillis() - service.getServicestarttime()) / 1000);
	}
	
	/**
	 * A la creation de la classe, lance le service.
	 * Arrete le service a la fermeture.
	 * @param serviceinformations peut etre null si la classe finale implemente ServiceInformations
	 */
	public ServiceManager(String[] args, ServiceInformations serviceinformations) {
		javastarttime = System.currentTimeMillis();
		
		if (args == null) {
			throw new NullPointerException("\"args\" can't to be null"); //$NON-NLS-1$
		}
		if (serviceinformations == null) {
			if (this instanceof ServiceInformations) {
				this.serviceinformations = this;
			} else {
				throw new NullPointerException("\"serviceinformations\" can't to be null"); //$NON-NLS-1$
			}
		} else {
			this.serviceinformations = serviceinformations;
		}
		
		if (args.length > 0) {
			xmlconfigfile = new File(args[0]);
		} else {
			injectDefaultSystemProperty("service.configfile", "config.xml"); //$NON-NLS-1$ //$NON-NLS-2$
			xmlconfigfile = new File(System.getProperty("service.configfile")); //$NON-NLS-1$
		}
		
		try {
			if (xmlconfigfile.exists() == false) {
				throw new IOException("XML config file don't exists"); //$NON-NLS-1$
			}
			if (xmlconfigfile.canRead() == false) {
				throw new IOException("Can't read XML config file"); //$NON-NLS-1$
			}
			if (xmlconfigfile.isFile() == false) {
				throw new IOException("XML config file is not a file"); //$NON-NLS-1$
			}
			xmlconfiguration = XmlData.loadFromFile(xmlconfigfile);
		} catch (Exception e) {
			Log2Dump dump = new Log2Dump();
			dump.add("xmlfile", xmlconfigfile); //$NON-NLS-1$
			dump.addAll(serviceinformations);
			Log2.log.error("Fatal service starting error", e, dump); //$NON-NLS-1$
			System.exit(1);
		}
		
		ArrayList<Element> e_properties = XmlData.getElementsByName(xmlconfiguration.getDocumentElement(), "property"); //$NON-NLS-1$
		if (e_properties != null) {
			for (int pos = 0; pos < e_properties.size(); pos++) {
				injectDefaultSystemProperty(e_properties.get(pos).getAttribute("name"), e_properties.get(pos).getAttribute("value")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		try {
			messagemanager = new ServiceMessagemanager(xmlconfiguration, this);
		} catch (Exception e) {
			Log2.log.error("Can't load mail configuration", e); //$NON-NLS-1$
			System.exit(1);
		}
		
		try {
			postClassInit();
			
			service = new ServiceWorker(this, xmlconfiguration, messagemanager, xmlconfigfile);
			service.start();
			
			Thread t = new Thread() {
				public void run() {
					try {
						Log2.log.info("Request shutdown application"); //$NON-NLS-1$
						Thread tkill = new Thread() {
							public void run() {
								try {
									sleep(8000);
									Log2.log.error("Request KILL application", null); //$NON-NLS-1$
									System.exit(2);
								} catch (Exception e) {
									Log2.log.error("Fatal service killing", e); //$NON-NLS-1$
									System.exit(2);
								}
							}
						};
						tkill.setName("Shutdown KILL"); //$NON-NLS-1$
						tkill.setDaemon(true);
						tkill.start();
						stopService();
					} catch (Exception e) {
						Log2.log.error("Fatal service stopping", e); //$NON-NLS-1$
						messagemanager.sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("ServiceManager.28"), e)); //$NON-NLS-1$
					}
				}
			};
			t.setName("Shutdown Hook"); //$NON-NLS-1$
			Runtime.getRuntime().addShutdownHook(t);
			
		} catch (Exception e) {
			Log2.log.error("Fatal service error", e, serviceinformations); //$NON-NLS-1$
			messagemanager.sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("ServiceManager.31"), e, serviceinformations)); //$NON-NLS-1$
			System.exit(2);
		}
	}
	
	/**
	 * @return true si le thread interne du service tourne encore
	 */
	public final synchronized boolean isRun() {
		return service.isAlive();
	}
	
	public final synchronized void startService() {
		if (isRun() == false) {
			service = new ServiceWorker(this, xmlconfiguration, messagemanager, xmlconfigfile);
			service.start();
		}
	}
	
	public final synchronized void stopService() {
		try {
			service.setStopworking();
			while (isRun()) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
