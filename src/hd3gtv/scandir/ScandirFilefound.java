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

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class ScandirFilefound {
	
	private File file;
	private long lastsize;
	private long lasttime;
	private long lastcheck;
	private static final long timefortwochecks = 1000;
	private static final long minageforfile = 30000;
	
	@SuppressWarnings("nls")
	ScandirFilefound(File file) {
		this.file = file;
		if (file == null) {
			throw new NullPointerException("\"file\" can't to be null");
		}
		lastsize = file.length();
		lasttime = file.lastModified();
		lastcheck = System.currentTimeMillis();
	}
	
	boolean isUsableFile() {
		if (file.exists() == false) {
			return false;
		}
		
		if (file.canRead() == false) {
			return false;
		}
		
		if (file.isFile() == false) {
			return false;
		}
		
		if (file.isHidden()) {
			return false;
		}
		
		return true;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof ScandirFilefound) {
			return ((ScandirFilefound) obj).file.getPath().equals(file.getPath());
		} else {
			return false;
		}
	}
	
	boolean isFileIsStable() {
		if ((file.lastModified() + minageforfile) > System.currentTimeMillis()) {
			/**
			 * fichier trop jeune
			 */
			return false;
		}
		
		if ((System.currentTimeMillis() - (lastcheck + timefortwochecks)) < 0) {
			/**
			 * Fichier encore en evolution
			 */
			return false;
		}
		
		if (file.length() == 0) {
			return false;
		}
		if ((file.length() == lastsize) && (file.lastModified() == lasttime)) {
			return true;
		} else {
			lastsize = file.length();
			lasttime = file.lastModified();
			return false;
		}
	}
	
	public File getFile() {
		return file;
	}
	
}
