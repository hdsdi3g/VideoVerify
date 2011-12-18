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

import hd3gtv.tools.Timecode;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class MediaContainer implements Serializable {
	
	private static final long serialVersionUID = 193782820796338737L;
	
	/**
	 * En bits par secondes.
	 */
	long bitrate = -1;
	
	/**
	 * En bits par secondes.
	 */
	public long getBitrate() {
		return bitrate;
	}
	
	String name = ""; //$NON-NLS-1$
	
	public String getContainerName() {
		return name;
	}
	
	Timecode duration;
	
	public Timecode getDuration() {
		return duration;
	}
	
	ArrayList<FFprobeTag> tags;
	
	public ArrayList<FFprobeTag> getTags() {
		return tags;
	}
	
	@SuppressWarnings("nls")
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(name);
		sb.append(" ");
		sb.append(duration);
		sb.append(" ");
		sb.append(bitrate);
		sb.append(" bps");
		
		return sb.toString();
	}
	
}
