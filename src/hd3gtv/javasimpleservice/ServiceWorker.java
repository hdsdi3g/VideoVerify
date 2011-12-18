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
import hd3gtv.tools.XmlData;

import java.io.File;

/**
 * Thread for handle the service
 * @author hdsdi3g
 * @version 1.0
 */
final class ServiceWorker extends Thread {
	
	private ServiceManager servicereferer;
	private long servicestarttime;
	private boolean keepworking;
	private XmlData xmlconfiguration;
	private ServiceMessagemanager messagemanager;
	private File xmlconfigfile;
	
	ServiceWorker(ServiceManager servicereferer, XmlData xmlconfiguration, ServiceMessagemanager messagemanager, File xmlconfigfile) {
		setDaemon(false);
		setName("ServiceManager for " + servicereferer.getServiceinformations().getApplicationShortName()); //$NON-NLS-1$
		this.servicereferer = servicereferer;
		this.xmlconfiguration = xmlconfiguration;
		this.messagemanager = messagemanager;
		this.xmlconfigfile = xmlconfigfile;
	}
	
	synchronized void setStopworking() {
		this.keepworking = false;
	}
	
	long getServicestarttime() {
		return servicestarttime;
	}
	
	private Thread createWorkingThread() {
		Thread t = new Thread() {
			public void run() {
				try {
					Log2.log.info("Startup service...", servicereferer.getServiceinformations()); //$NON-NLS-1$
					servicestarttime = System.currentTimeMillis();
					servicereferer.startApplicationService(xmlconfiguration);
				} catch (Exception e) {
					Log2.log.error("ServiceManager execution error...", e, servicereferer.getServiceinformations()); //$NON-NLS-1$
					messagemanager.sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("ServiceManager.35"), e, servicereferer.getServiceinformations())); //$NON-NLS-1$
				}
			};
		};
		t.setDaemon(false);
		t.setName("ServiceManager Daemon"); //$NON-NLS-1$
		return t;
	}
	
	public void run() {
		
		long configfilelastmodified;
		ServiceMessagemanager tempmessagemanager = null;
		
		keepworking = true;
		Thread internalworkingthread;
		try {
			while (keepworking) {
				configfilelastmodified = xmlconfigfile.lastModified();
				
				internalworkingthread = createWorkingThread();
				internalworkingthread.start();
				
				while (keepworking) {
					Thread.sleep(1000);
					if (xmlconfigfile.lastModified() > configfilelastmodified) {
						configfilelastmodified = xmlconfigfile.lastModified();
						Log2.log.info("ServiceManager configuration has change", servicereferer.getServiceinformations()); //$NON-NLS-1$
						try {
							xmlconfiguration = XmlData.loadFromFile(xmlconfigfile);
							tempmessagemanager = new ServiceMessagemanager(xmlconfiguration, servicereferer);
						} catch (Exception e) {
							Log2.log.error("New service configuration error, can't reload service.", e, servicereferer.getServiceinformations()); //$NON-NLS-1$
							messagemanager.sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("ServiceManager.39"), e, servicereferer.getServiceinformations())); //$NON-NLS-1$
							continue;
						}
						messagemanager = tempmessagemanager;
						Log2.log.info("Reload service...", servicereferer.getServiceinformations()); //$NON-NLS-1$
						servicereferer.stopApplicationService();
						
						while (internalworkingthread.isAlive()) {
							sleep(10);
						}
						internalworkingthread = createWorkingThread();
						internalworkingthread.start();
					}
				}
			}
			
		} catch (Exception e) {
			Log2.log.error("Fatal error during service execution", e); //$NON-NLS-1$
			messagemanager.sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("ServiceManager.42"), e, servicereferer.getServiceinformations())); //$NON-NLS-1$
		}
		try {
			servicereferer.stopApplicationService();
		} catch (Exception e) {
			Log2.log.error("Error : can't stop proper the service", e); //$NON-NLS-1$
			messagemanager.sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("ServiceManager.44"), e, servicereferer.getServiceinformations())); //$NON-NLS-1$
		}
	}
	
}
