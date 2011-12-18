/*
 * This file is part of Java wrapping and abstraction for ffprobe.
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

package hd3gtv.ffprobe;

import java.io.Serializable;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class FFprobeTag implements Serializable {
	
	private static final long serialVersionUID = -8853683552229206023L;
	
	String key;
	String value;
	
	@SuppressWarnings("nls")
	public String toString() {
		return key + "=" + value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public FFprobeTag() {
	}
	
	public FFprobeTag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
}
