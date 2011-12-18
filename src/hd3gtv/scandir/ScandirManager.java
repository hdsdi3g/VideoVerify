/*
 * This file is part of Scandir
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

package hd3gtv.scandir;

import java.util.ArrayList;

@SuppressWarnings("nls")
/**
 * @author hdsdi3g
 * @version 1.0
 */
public class ScandirManager {
	
	private ScandirEvents events;
	private ArrayList<Directory> directoriestoscan;
	private ArrayList<ScandirFoundfiles> foundfilehandlers;
	private ArrayList<ScandirFilefound> workedfiles;
	private ScandirWorker worker;
	
	public ScandirManager(ScandirEvents scandirevents, ArrayList<Directory> directoriestoscan) {
		this.events = scandirevents;
		if (scandirevents == null) {
			throw new NullPointerException("\"scandirevents\" can't to be null");
		}
		this.directoriestoscan = directoriestoscan;
		if (directoriestoscan == null) {
			throw new NullPointerException("\"directoriestoscan\" can't to be null");
		}
		workedfiles = new ArrayList<ScandirFilefound>();
		foundfilehandlers = new ArrayList<ScandirFoundfiles>();
		worker = new ScandirWorker(this);
		worker.start();
	}
	
	public void end() {
		worker.stopScan();
		try {
			while (worker.isAlive()) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
		}
	}
	
	synchronized ArrayList<ScandirFilefound> getWorkedfiles() {
		return workedfiles;
	}
	
	public ArrayList<ScandirFoundfiles> getFoundfilehandlers() {
		return foundfilehandlers;
	}
	
	void newFiles(ArrayList<ScandirFilefound> newfiles) {
		for (int posf = newfiles.size() - 1; posf > -1; posf--) {
			if (newfiles.get(posf).isUsableFile() == false) {
				newfiles.remove(posf);
			}
		}
		for (int posh = 0; posh < foundfilehandlers.size(); posh++) {
			foundfilehandlers.get(posh).onNewFilesFound(newfiles);
		}
	}
	
	ScandirEvents getEvents() {
		return events;
	}
	
	ArrayList<Directory> getDirectoriestoscan() {
		return directoriestoscan;
	}
	
}
