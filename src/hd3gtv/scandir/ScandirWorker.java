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

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("nls")
/**
 * @author hdsdi3g
 * @version 1.0
 */
class ScandirWorker extends Thread {
	
	private ScandirManager manager;
	
	private boolean working;
	
	private ArrayList<ScandirFilefound> newfilestoprocess;
	
	private static final long maxtimeforwaitnewfiles = 2000;
	private static final long timebetweentocheck = 500;
	
	ScandirWorker(ScandirManager manager) {
		this.manager = manager;
		if (manager == null) {
			throw new NullPointerException("\"manager\" can't to be null");
		}
		setName("Scandir");
		newfilestoprocess = new ArrayList<ScandirFilefound>();
	}
	
	public void run() {
		manager.getEvents().onStartScan(manager.getDirectoriestoscan());
		
		try {
			working = true;
			Directory currentsrcdir;
			int loopcount = 0;
			int maxloopcount = 10;
			
			while (working) {
				sleep(timebetweentocheck);
				for (int possrcdir = 0; possrcdir < manager.getDirectoriestoscan().size(); possrcdir++) {
					/**
					 * pour tous les dossiers que l'on recherche
					 */
					currentsrcdir = manager.getDirectoriestoscan().get(possrcdir);
					
					scanAllfiles(currentsrcdir.directory.listFiles());
					
				}
				
				validateNewFiles();
				
				if (working == false) {
					break;
				}
				
				/**
				 * lancer la/les taches pour les fichier a traiter
				 */
				if (newfilestoprocess.size() > 0) {
					manager.newFiles(newfilestoprocess);
				}
				
				/**
				 * puis add dans la liste des fichiers trouves (persistant)
				 */
				manager.getWorkedfiles().addAll(newfilestoprocess);
				newfilestoprocess = new ArrayList<ScandirFilefound>();
				
				/**
				 * purge de la liste des fichiers trouves (persistant)
				 */
				loopcount++;
				if (loopcount > maxloopcount) {
					loopcount = 0;
					cleanningDoneFiles();
				}
			}
			
		} catch (Exception e) {
			manager.getEvents().onThreadScanError(e);
		}
		manager.getEvents().onStopScan();
	}
	
	synchronized void stopScan() {
		working = false;
	}
	
	private void scanAllfiles(File[] filesfound) {
		ScandirFilefound scandirFilefound = null;
		ArrayList<ScandirFilefound> workedfiles = manager.getWorkedfiles();
		boolean thisfileisknow;
		
		for (int posff = 0; posff < filesfound.length; posff++) {
			if (working == false) {
				return;
			}
			scandirFilefound = new ScandirFilefound(filesfound[posff]);
			if (scandirFilefound.isUsableFile() == false) {
				continue;
			}
			
			if (scandirFilefound.getFile().getName().equalsIgnoreCase("Thumbs.db")) {
				continue;
			}
			
			thisfileisknow = false;
			for (int poswf = 0; poswf < workedfiles.size(); poswf++) {
				/**
				 * test si fichier existe dans la liste des fichiers trouver
				 */
				if (workedfiles.get(poswf).equals(scandirFilefound)) {
					thisfileisknow = true;
					break;
				}
				if (working == false) {
					return;
				}
			}
			if (thisfileisknow == false) {
				/**
				 * add a la liste des fichiers a traiter (non persistant)
				 */
				newfilestoprocess.add(scandirFilefound);
			}
			
		}
		
	}
	
	private void validateNewFiles() throws InterruptedException {
		/**
		 * scan de liste des fichiers a traiter (fichiers en remplissage de taille)
		 * au bout d'un certain temps arreter d'attendre et lancer la suite, ne pas ajouter aux listes les fichiers ignores
		 */
		
		ArrayList<ScandirFilefound> filesvalidate = new ArrayList<ScandirFilefound>();
		
		ScandirFilefound currentfile;
		
		long starttime = System.currentTimeMillis();
		
		while (working && ((starttime + maxtimeforwaitnewfiles) > System.currentTimeMillis()) && (newfilestoprocess.size() > 0)) {
			for (int posnf = newfilestoprocess.size() - 1; posnf > -1; posnf--) {
				currentfile = newfilestoprocess.get(posnf);
				if (currentfile.isFileIsStable()) {
					filesvalidate.add(currentfile);
					newfilestoprocess.remove(posnf);
				}
			}
			sleep(timebetweentocheck);
		}
		
		newfilestoprocess.clear();
		newfilestoprocess.addAll(filesvalidate);
	}
	
	private void cleanningDoneFiles() {
		ArrayList<File> cleanfiles = new ArrayList<File>();
		for (int pos = manager.getWorkedfiles().size() - 1; pos > -1; pos--) {
			if (manager.getWorkedfiles().get(pos).isUsableFile() == false) {
				cleanfiles.add(manager.getWorkedfiles().get(pos).getFile());
				manager.getWorkedfiles().remove(pos);
			}
			if (working == false) {
				return;
			}
		}
		if (cleanfiles.size() > 0) {
			manager.getEvents().onCleanFiles(cleanfiles);
		}
	}
	
}