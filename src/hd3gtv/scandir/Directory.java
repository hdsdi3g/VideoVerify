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
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class Directory {
	
	File directory;
	
	@SuppressWarnings("nls")
	public Directory(File path) throws IOException {
		if (path == null) {
			throw new NullPointerException("\"pathname\" can't to be null");
		}
		directory = path;
		
		if (directory.exists() == false) {
			throw new IOException("\"" + path.getPath() + "\" don't exist");
		}
		
		if (directory.canRead() == false) {
			throw new IOException("Can't read \"" + path.getPath() + "\"");
		}
		
		if (directory.isDirectory() == false) {
			throw new IOException("\"" + path.getPath() + "\" is not a directory");
		}
	}
	
	public Directory(String pathname) throws IOException {
		this(new File(pathname));
	}
	
	public File getDirectory() {
		return directory;
	}
	
	public ArrayList<Directory> getSubDirs() {
		File[] subdir = directory.listFiles();
		
		if (subdir.length == 0) {
			return null;
		}
		
		ArrayList<Directory> result = new ArrayList<Directory>(subdir.length);
		
		File currentfile;
		for (int pos = 0; pos < subdir.length; pos++) {
			currentfile = subdir[pos];
			try {
				result.add(new Directory(currentfile));
			} catch (IOException e) {
				/**
				 * File is not a valid directory
				 */
			}
		}
		
		return result;
	}
	
	public String toString() {
		return directory.getPath();
	}
	
}
