/*
 * This file is part of VideoVerify.
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

package hd3gtv.videoverify;

import hd3gtv.javasimpleservice.ServiceInformations;
import hd3gtv.javasimpleservice.ServiceManager;
import hd3gtv.javasimpleservice.ServiceMessageError;
import hd3gtv.log2.Log2;
import hd3gtv.log2.Log2Dump;
import hd3gtv.scandir.Directory;
import hd3gtv.scandir.ScandirEvents;
import hd3gtv.scandir.ScandirFilefound;
import hd3gtv.scandir.ScandirFoundfiles;
import hd3gtv.scandir.ScandirManager;
import hd3gtv.tools.XmlData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class VideoverifyService extends ServiceManager implements ServiceInformations, ScandirEvents, ScandirFoundfiles {
	
	public VideoverifyService(String[] args, ServiceInformations serviceinformations) {
		super(args, serviceinformations);
	}
	
	public Log2Dump getLog2Dump() {
		Log2Dump dump = new Log2Dump();
		dump.add("applicationname", getApplicationName()); //$NON-NLS-1$
		dump.add("version", getApplicationVersion()); //$NON-NLS-1$
		return null;
	}
	
	public String getApplicationName() {
		return "Copyright (C) hdsdi3g for hd3g.tv 2008-2011"; //$NON-NLS-1$
	}
	
	public float getApplicationVersion() {
		return 0.01f;
	}
	
	public String getApplicationCopyright() {
		return "VideoVerify"; //$NON-NLS-1$
	}
	
	private boolean alive;
	
	private ScandirManager scandirmanager;
	
	private ProcessAnalyst processanalyst;
	
	private XmlData scandir_xmlconfiguration;
	
	public ProcessAnalyst getProcessanalyst() {
		return processanalyst;
	}
	
	protected void startApplicationService(XmlData xmlconfiguration) throws Exception {
		
		processanalyst = new ProcessAnalyst(xmlconfiguration, this);
		
		scandir_xmlconfiguration = xmlconfiguration;
		/**
		 * read xml + add directories
		 */
		ArrayList<Directory> directories = new ArrayList<Directory>();
		
		Element xmlelementconfig = XmlData.getElementByName(xmlconfiguration.getDocumentElement().getChildNodes(), "scandir");//$NON-NLS-1$
		if (xmlelementconfig == null) {
			throw new NullPointerException("There is not configuration for scandir in xml configuration");//$NON-NLS-1$
		}
		ArrayList<Element> xmlementsdir = XmlData.getElementsByName(xmlelementconfig.getChildNodes(), "dir");//$NON-NLS-1$
		String thisdirpath;
		for (int pos = 0; pos < xmlementsdir.size(); pos++) {
			thisdirpath = xmlementsdir.get(pos).getAttribute("path");//$NON-NLS-1$
			if (thisdirpath == null) {
				Log2.log.error("Xml configuration : element \"dir\" in element \"scandir\" must have \"path\" attribute", null);//$NON-NLS-1$
				continue;
			}
			try {
				Directory dir = new Directory(thisdirpath);
				directories.add(dir);
			} catch (IOException e) {
				Log2.log.error("Xml configuration : \"path\" attribute is invalid. Please verify your paths and associated rights.", e);//$NON-NLS-1$
			}
		}
		
		if (directories.size() == 0) {
			Log2.log.error("Xml configuration are empty/missing/invalid entries for scandir", null);//$NON-NLS-1$
			getMessagemanager().sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("VideoverifyService.xmlerror"), null)); //$NON-NLS-1$
			throw new NullPointerException("No directory to scan");//$NON-NLS-1$
		} else {
			if (xmlementsdir.size() != directories.size()) {
				getMessagemanager().sendMessage(new ServiceMessageError(xmlconfiguration, Messages.getString("VideoverifyService.directorydontexist"), null)); //$NON-NLS-1$
				throw new NullPointerException("Some directories from xml configuration are missing or invalid.");//$NON-NLS-1$
			}
		}
		
		scandirmanager = new ScandirManager(this, directories);
		scandirmanager.getFoundfilehandlers().add(this);
		
		alive = true;
		while (alive) {
			Thread.sleep(100);
		}
		
	}
	
	protected void stopApplicationService() throws Exception {
		alive = false;
		if (scandirmanager != null) {
			scandirmanager.end();
		}
	}
	
	public String getApplicationShortName() {
		return getApplicationName();
	}
	
	/**
	 * @return false si une demande d'arret est en cours.
	 */
	public synchronized boolean isAlive() {
		return alive;
	}
	
	protected void postClassInit() throws Exception {
	}
	
	public void onThreadScanError(Exception e) {
		Log2.log.error("Scandir error", e);//$NON-NLS-1$
		getMessagemanager().sendMessage(new ServiceMessageError(scandir_xmlconfiguration, Messages.getString("VideoverifyService.scanerror"), e)); //$NON-NLS-1$
	}
	
	public void onStartScan(ArrayList<Directory> directories) {
		Log2Dump dump = new Log2Dump();
		dump.add("directory", directories);//$NON-NLS-1$
		Log2.log.info("Start directories scan", dump);//$NON-NLS-1$
	}
	
	public void onStopScan() {
		Log2.log.info("Stop directories scan");//$NON-NLS-1$
	}
	
	public void onNewFilesFound(ArrayList<ScandirFilefound> newfiles) {
		Log2Dump dump = new Log2Dump();
		for (int posnf = 0; posnf < newfiles.size(); posnf++) {
			dump.add("file", newfiles.get(posnf).getFile());//$NON-NLS-1$
		}
		Log2.log.info("Found files", dump);//$NON-NLS-1$
		
		try {
			getProcessanalyst().analyst(newfiles);
		} catch (Exception e) {
			Log2.log.error("Error while analyst files", e);//$NON-NLS-1$
			getMessagemanager().sendMessage(new ServiceMessageError(scandir_xmlconfiguration, Messages.getString("VideoverifyService.analysterror"), e)); //$NON-NLS-1$
		}
	}
	
	public void onCleanFiles(ArrayList<File> cleanfiles) {
		Log2Dump dump = new Log2Dump();
		dump.add("files", cleanfiles);//$NON-NLS-1$
		Log2.log.debug("Clean list files", dump);//$NON-NLS-1$
	}
	
}