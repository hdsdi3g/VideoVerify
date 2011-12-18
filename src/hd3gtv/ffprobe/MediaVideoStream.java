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

import hd3gtv.ffprobe.VideoConst.Aspectratio;
import hd3gtv.ffprobe.VideoConst.ChromaFormat;
import hd3gtv.ffprobe.VideoConst.Framerate;
import hd3gtv.ffprobe.VideoConst.Interlacing;
import hd3gtv.ffprobe.VideoConst.Systemvideo;

import java.io.Serializable;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class MediaVideoStream extends MediaGenericStream implements Serializable {
	
	private static final long serialVersionUID = 6875067493511459901L;
	
	Aspectratio aspectratio;
	
	public Aspectratio getAspectratio() {
		return aspectratio;
	}
	
	ChromaFormat chromaformat;
	
	public ChromaFormat getChromaformat() {
		return chromaformat;
	}
	
	@SuppressWarnings("nls")
	void setChromaformat() {
		chromaformat = null;
		if (pixelcolor.equalsIgnoreCase("BGR24")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("BGR32")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("BGR32_1")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("RGB24")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("RGB32")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("RGB32_1")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("UYVY422")) {
			chromaformat = ChromaFormat.CHROMA_422;
		}
		
		if (pixelcolor.equalsIgnoreCase("UYYVYY411")) {
			chromaformat = ChromaFormat.CHROMA_411;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUV410P")) {
			chromaformat = ChromaFormat.CHROMA_411;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUV411P")) {
			chromaformat = ChromaFormat.CHROMA_411;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUV420P")) {
			chromaformat = ChromaFormat.CHROMA_420;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUV422P")) {
			chromaformat = ChromaFormat.CHROMA_422;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUV440P")) {
			chromaformat = ChromaFormat.CHROMA_422;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUV444P")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUVJ420P")) {
			chromaformat = ChromaFormat.CHROMA_420;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUVJ422P")) {
			chromaformat = ChromaFormat.CHROMA_422;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUVJ440P")) {
			chromaformat = ChromaFormat.CHROMA_422;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUVJ444P")) {
			chromaformat = ChromaFormat.CHROMA_RVB;
		}
		
		if (pixelcolor.equalsIgnoreCase("YUYV422")) {
			chromaformat = ChromaFormat.CHROMA_422;
		}
		if (pixelcolor.equalsIgnoreCase("4444")) {
			chromaformat = ChromaFormat.CHROMA_4444;
		}
		if (chromaformat == null) {
			chromaformat = ChromaFormat.OTHER;
		}
	}
	
	Framerate framerate;
	
	public Framerate getFramerate() {
		return framerate;
	}
	
	public Interlacing getInterlacing() {
		return Interlacing.Unknow;
	}
	
	String pixelcolor;
	
	public String getPixelcolor() {
		return pixelcolor;
	}
	
	Systemvideo systemvideo;
	
	public Systemvideo getSystemvideo() {
		return systemvideo;
	}
	
	int width = -1;
	
	public int get_X_Width() {
		return width;
	}
	
	int height = -1;
	
	public int get_Y_Height() {
		return height;
	}
	
	@SuppressWarnings("nls")
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(super.toString());
		sb.append(" ");
		sb.append(width);
		sb.append("x");
		sb.append(height);
		sb.append(" ");
		sb.append(framerate);
		sb.append(" [");
		sb.append(systemvideo);
		sb.append(" ");
		sb.append(aspectratio);
		sb.append(" ");
		sb.append(chromaformat);
		sb.append(" ");
		sb.append(pixelcolor);
		sb.append("]");
		
		return sb.toString();
	}
	
}
