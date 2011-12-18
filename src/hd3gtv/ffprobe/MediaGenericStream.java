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
import java.util.ArrayList;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public abstract class MediaGenericStream implements Serializable {
	
	private static final long serialVersionUID = 6696764790103627196L;
	
	long bitrate;
	
	public long getBitrate() {
		return bitrate;
	}
	
	String codecname;
	
	public String getCodecName() {
		return codecname;
	}
	
	String streamid;
	
	public String getStreamid() {
		return streamid;
	}
	
	@SuppressWarnings("nls")
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(streamid);
		sb.append(" ");
		sb.append(codecname);
		sb.append(" ");
		sb.append(bitrate);
		sb.append(" bps");
		
		return sb.toString();
	}
	
	ArrayList<FFprobeTag> tags;
	
	public ArrayList<FFprobeTag> getTags() {
		return tags;
	}
	
	public static MediaVideoStream getVideoStream(ArrayList<MediaGenericStream> streaminformation) {
		for (int pos = 0; pos < streaminformation.size(); pos++) {
			if (streaminformation.get(pos) instanceof MediaVideoStream) {
				return (MediaVideoStream) streaminformation.get(pos);
			}
		}
		return null;
	}
	
	public static ArrayList<MediaAudioStream> getAudioStreams(ArrayList<MediaGenericStream> streaminformation) {
		ArrayList<MediaAudioStream> result = new ArrayList<MediaAudioStream>();
		for (int pos = 0; pos < streaminformation.size(); pos++) {
			if (streaminformation.get(pos) instanceof MediaAudioStream) {
				result.add((MediaAudioStream) streaminformation.get(pos));
			}
		}
		return result;
	}
	
}
