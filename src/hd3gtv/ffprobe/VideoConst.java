/*
 * This file is part of VideoConst : Java constants for professional audios and videos values
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

@SuppressWarnings("nls")
/**
 * @author hdsdi3g
 * @version 1.0
 */
public class VideoConst {
	
	public enum Systemvideo {
		PAL, NTSC, CINEMA, OTHER;
		
		public static Systemvideo getSystem(int width, int height, Framerate framerate) {
			Systemvideo system_from_framerate = Framerate.getSystem(framerate);
			/**
			 * Cas officiels
			 */
			if ((width == 720) & (height == 576) & (system_from_framerate == PAL)) {
				return PAL;
			}
			if ((width == 720) & (height == 480) & (system_from_framerate == NTSC)) {
				return NTSC;
			}
			if ((width == 720) & (height == 608) & (system_from_framerate == PAL)) {
				return PAL;
			}
			if ((width == 720) & (height == 512) & (system_from_framerate == NTSC)) {
				return NTSC;
			}
			/**
			 * Cas batards
			 */
			if ((width == 720) & (height == 576) & (system_from_framerate == NTSC)) {
				return OTHER;
			}
			if ((width == 720) & (height == 480) & (system_from_framerate == PAL)) {
				return OTHER;
			}
			if ((width == 720) & (height == 608) & (system_from_framerate == NTSC)) {
				return OTHER;
			}
			if ((width == 720) & (height == 512) & (system_from_framerate == PAL)) {
				return OTHER;
			}
			return system_from_framerate;
		}
		
		public static Systemvideo getSystem(String systemname) {
			if (systemname.equalsIgnoreCase("PAL")) {
				return PAL;
			}
			if (systemname.equalsIgnoreCase("NTSC")) {
				return NTSC;
			}
			if (systemname.equalsIgnoreCase("CINEMA")) {
				return CINEMA;
			}
			return OTHER;
		}
	}
	
	public enum Framerate {
		FPS_25, FPS_30, FPS_2997, FPS_50, FPS_60, FPS_23976, FPS_5994, FPS_24;
		
		public static Systemvideo getSystem(Framerate framerate) {
			if (framerate == FPS_25) {
				return Systemvideo.PAL;
			}
			if (framerate == FPS_30) {
				return Systemvideo.NTSC;
			}
			if (framerate == FPS_2997) {
				return Systemvideo.NTSC;
			}
			if (framerate == FPS_50) {
				return Systemvideo.PAL;
			}
			if (framerate == FPS_60) {
				return Systemvideo.NTSC;
			}
			if (framerate == FPS_24) {
				return Systemvideo.CINEMA;
			}
			if (framerate == FPS_23976) {
				return Systemvideo.CINEMA;
			}
			if (framerate == FPS_5994) {
				return Systemvideo.NTSC;
			}
			return Systemvideo.OTHER;
		}
		
		public static Framerate getFramerate(float fps) throws NumberFormatException {
			if (fps == 25f) {
				return FPS_25;
			}
			if (fps == 30f) {
				return FPS_30;
			}
			if ((fps > 29.8f) && (fps < 30f)) {
				return FPS_2997;
			}
			if (fps == 50f) {
				return FPS_50;
			}
			if (fps == 60f) {
				return FPS_60;
			}
			if (fps == 24f) {
				return FPS_24;
			}
			if ((fps > 23.8f) && (fps < 24f)) {
				return FPS_23976;
			}
			if ((fps > 59.8f) && (fps < 60f)) {
				return FPS_5994;
			}
			throw new NumberFormatException("Unknow fps value : " + String.valueOf(fps));
		}
		
		public float getNumericValue() {
			if (this == FPS_25) {
				return 25f;
			}
			if (this == FPS_30) {
				return 30f;
			}
			if (this == FPS_2997) {
				return 29.97f;
			}
			if (this == FPS_50) {
				return 50f;
			}
			if (this == FPS_60) {
				return 60f;
			}
			if (this == FPS_24) {
				return 24f;
			}
			if (this == FPS_23976) {
				return 23.976f;
			}
			if (this == FPS_5994) {
				return 59.94f;
			}
			return 25;
		}
		
		public String toString() {
			return String.valueOf(getNumericValue());
		}
	}
	
	public enum Aspectratio {
		AR_43, AR_169, AR_VGA;
		
		/**
		 * @return -1 si VGA
		 */
		public float toFloat() {
			if (this == Aspectratio.AR_43) {
				return 4f / 3f;
			}
			if (this == Aspectratio.AR_169) {
				return 16f / 9f;
			}
			return -1;
		}
		
		public String toString() {
			if (this == Aspectratio.AR_43) {
				return "4/3";
			}
			if (this == Aspectratio.AR_169) {
				return "16/9";
			}
			if (this == Aspectratio.AR_VGA) {
				return "1/1";
			}
			return "1/1";
		}
		
		/**
		 * @param value une valeur telle que "16/9", "4:3", "4-3", "16_9", "1.333", "1.77".
		 */
		public static Aspectratio parseAR(String value) {
			String splitchar = null;
			if (value.indexOf(":") > 0) {
				splitchar = ":";
			}
			if (value.indexOf("_") > 0) {
				splitchar = "_";
			}
			if (value.indexOf("-") > 0) {
				splitchar = "-";
			}
			if (value.indexOf("/") > 0) {
				splitchar = "/";
			}
			if (splitchar != null) {
				String[] raw = value.split(splitchar);
				if (raw.length != 2) {
					throw new NumberFormatException("Invalid aspect ratio : " + value);
				}
				raw[0] = raw[0].trim();
				raw[1] = raw[1].trim();
				if (raw[0].equals("4") & raw[1].equals("3")) {
					return AR_43;
				}
				if (raw[0].equals("16") & raw[1].equals("9")) {
					return AR_169;
				}
			}
			
			if (value.startsWith("1.33") | value.startsWith("1,33")) {
				return AR_43;
			}
			if (value.startsWith("1.77") | value.startsWith("1,77") | value.startsWith("1.78") | value.startsWith("1,78")) {
				return AR_169;
			}
			
			return AR_VGA;
		}
	}
	
	public enum ChromaFormat {
		CHROMA_420, CHROMA_422, CHROMA_411, CHROMA_RVB, CHROMA_4444, OTHER;
		
		public String toString() {
			if (this == CHROMA_420) {
				return "4:2:0";
			}
			if (this == CHROMA_422) {
				return "4:2:2";
			}
			if (this == CHROMA_411) {
				return "4:1:1";
			}
			if (this == CHROMA_RVB) {
				return "RVB";
			}
			if (this == CHROMA_4444) {
				return "4:4:4:4";
			}
			return "4:2:0";
		}
		
		public static ChromaFormat parseCF(String value) {
			if (value.equals("4:2:0")) {
				return CHROMA_420;
			}
			if (value.equals("4:2:2")) {
				return CHROMA_422;
			}
			if (value.equals("4:1:1")) {
				return CHROMA_411;
			}
			if (value.equals("RVB")) {
				return CHROMA_RVB;
			}
			if (value.equals("4:4:4:4")) {
				return CHROMA_4444;
			}
			return null;
		}
	}
	
	public enum Interlacing {
		Progressive,
		/**
		 * odd
		 */
		TopFieldFirst,
		/**
		 * even
		 */
		BottomFieldFirst, Unknow;
	}
	
	public static String audioChannelCounttoString(int channelcount) {
		if (channelcount == 1) {
			return "mono";
		}
		if (channelcount == 2) {
			return "stereo";
		}
		if (channelcount == 6) {
			return "5.1";
		}
		return "CH_" + String.valueOf(channelcount);
	}
	
	public static int stringToAudioChannelCount(String channelcount) throws NumberFormatException {
		if (channelcount.equals("mono")) {
			return 1;
		}
		if (channelcount.equals("stereo")) {
			return 2;
		}
		if (channelcount.equals("5.1")) {
			return 6;
		}
		if (channelcount.startsWith("CH_")) {
			return Integer.parseInt(channelcount.substring(3));
		}
		return Integer.parseInt(channelcount);
	}
	
}
