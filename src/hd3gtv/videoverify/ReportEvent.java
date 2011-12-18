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
 * Copyright (C) hdsdi3g for hd3g.tv 2011
 * 
*/

package hd3gtv.videoverify;

import hd3gtv.log2.Log2Dumpable;

import java.io.File;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public interface ReportEvent extends Log2Dumpable {
	
	String getCompleteEvent(boolean html);
	
	File getFile();
	
}
