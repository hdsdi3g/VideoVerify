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
public class MediaAudioStream extends MediaGenericStream implements Serializable {
	
	private static final long serialVersionUID = -7595155870919424932L;
	
	int channelscount = -1;
	
	public int getChannelsCount() {
		return channelscount;
	}
	
	int samplefrequency = -1;
	
	public int getSamplefrequency() {
		return samplefrequency;
	}
	
	@SuppressWarnings("nls")
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(super.toString());
		sb.append(" ");
		sb.append(channelscount);
		sb.append("ch ");
		sb.append(samplefrequency);
		sb.append(" Hz");
		return sb.toString();
	}
	
}
