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

import hd3gtv.ffprobe.VideoConst.Aspectratio;
import hd3gtv.ffprobe.VideoConst.ChromaFormat;
import hd3gtv.ffprobe.VideoConst.Framerate;
import hd3gtv.ffprobe.VideoConst.Systemvideo;
import hd3gtv.log2.Log2Dumpable;

/**
 * Challenge pose pour chaque fichier analyse
 * @author hdsdi3g
 * @version 1.0
 */
public interface FFprobeFormatreference extends Log2Dumpable {
	
	boolean validContainername(String name);
	
	boolean validStreamcount(int video_count, int audio_count, int data_count);
	
	boolean validVideoResolution(int x_width, int y_height);
	
	boolean validVideoBitrate(long video_bps);
	
	boolean validVideoAspectratio(Aspectratio aspectratio);
	
	boolean validVideoChromaFormat(ChromaFormat chromaformat);
	
	boolean validVideoCodec(String video_codec);
	
	boolean validVideoFramerate(Framerate framerate);
	
	boolean validVideoSystem(Systemvideo systemvideo);
	
	boolean validAudioBitrate(long audio_bps);
	
	boolean validAudioChannelCount(int count);
	
	boolean validAudioCodec(String audio_codec);
	
	boolean validAudioSamplefreq(int audio_sample_freq);
	
	boolean validFileMime(String mime);
	
	boolean validFileExtension(String extension);
	
	String getRecommended_VideoFormat(String newline);
	
	String getRecommended_AudioFormat(String newline);
	
	String getRecommended_ContainerFormat(String newline);
	
	String getName();
}
