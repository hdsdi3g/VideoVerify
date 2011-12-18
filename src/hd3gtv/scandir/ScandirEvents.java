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

/**
 * @author hdsdi3g
 * @version 1.0
 */
public interface ScandirEvents {
	
	void onThreadScanError(Exception e);
	
	void onStartScan(ArrayList<Directory> directories);
	
	void onStopScan();
	
	/**
	 * On supprime en interne les fichiers qui n'existent plus physiquement dans les dossiers.
	 */
	void onCleanFiles(ArrayList<File> cleanfiles);
	
}
