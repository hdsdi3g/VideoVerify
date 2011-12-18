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
import hd3gtv.log2.Log2Dump;
import hd3gtv.tools.XmlData;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

@SuppressWarnings("nls")
/**
 * @author hdsdi3g
 * @version 1.0
 */
public class FFprobeFormatreferenceXml implements FFprobeFormatreference {
	
	public FFprobeFormatreferenceXml(Element reference) throws IOException {
		if (reference == null) {
			throw new NullPointerException("\"reference\" can't to be null");
		}
		
		name = reference.getAttribute("name");
		if (name == null) {
			throw new NullPointerException("reference \"name\" can't to be null");
		}
		
		Element e_recommended = XmlData.getElementByName(reference, "recommended");
		try {
			recommended_videoformat = XmlData.getElementByName(e_recommended, "video").getTextContent();
		} catch (Exception e) {
		}
		try {
			recommended_audioformat = XmlData.getElementByName(e_recommended, "audio").getTextContent();
		} catch (Exception e) {
		}
		try {
			recommended_containerformat = XmlData.getElementByName(e_recommended, "container").getTextContent();
		} catch (Exception e) {
		}
		
		ArrayList<Element> e_files = XmlData.getElementsByName(reference, "file");
		if (e_files != null) {
			String mime_value;
			String ext_value;
			for (int pos = 0; pos < e_files.size(); pos++) {
				mime_value = e_files.get(pos).getAttribute("mime");
				ext_value = e_files.get(pos).getAttribute("extension");
				if (mime_value != null) {
					if (filemime == null) {
						filemime = new ArrayList<String>();
					}
					filemime.add(mime_value);
				}
				if (ext_value != null) {
					if (extensions == null) {
						extensions = new ArrayList<String>();
					}
					extensions.add(ext_value);
				}
			}
		}
		
		Element e_container = XmlData.getElementByName(reference, "container");
		try {
			containername = e_container.getAttribute("name");
		} catch (Exception e) {
		}
		try {
			streamcount_audio_min = Integer.valueOf(e_container.getAttribute("audio_min"));
		} catch (Exception e) {
		}
		try {
			streamcount_audio_max = Integer.valueOf(e_container.getAttribute("audio_max"));
		} catch (Exception e) {
		}
		try {
			streamcount_video_min = Integer.valueOf(e_container.getAttribute("video_min"));
		} catch (Exception e) {
		}
		try {
			streamcount_video_max = Integer.valueOf(e_container.getAttribute("video_max"));
		} catch (Exception e) {
		}
		try {
			streamcount_data_min = Integer.valueOf(e_container.getAttribute("data_min"));
		} catch (Exception e) {
		}
		try {
			streamcount_data_max = Integer.valueOf(e_container.getAttribute("data_max"));
		} catch (Exception e) {
		}
		
		Element e_video = XmlData.getElementByName(reference, "video");
		try {
			x_width = Integer.valueOf(e_video.getAttribute("width"));
		} catch (Exception e) {
		}
		try {
			y_height = Integer.valueOf(e_video.getAttribute("height"));
		} catch (Exception e) {
		}
		try {
			video_bitrate = Integer.valueOf(e_video.getAttribute("bitrate"));
		} catch (Exception e) {
		}
		
		try {
			if (e_video.getAttribute("ratio_4_3").equalsIgnoreCase("false")) {
				aspectratio_4_3 = false;
			}
		} catch (Exception e) {
		}
		try {
			if (e_video.getAttribute("ratio_16_9").equalsIgnoreCase("false")) {
				aspectratio_16_9 = false;
			}
		} catch (Exception e) {
		}
		try {
			if (e_video.getAttribute("ratio_vga").equalsIgnoreCase("false")) {
				aspectratio_VGA = false;
			}
		} catch (Exception e) {
		}
		
		try {
			chromaformat = ChromaFormat.parseCF(e_video.getAttribute("chroma"));
		} catch (Exception e) {
		}
		try {
			video_codec = e_video.getAttribute("codec");
		} catch (Exception e) {
		}
		try {
			framerate = Framerate.getFramerate(Float.parseFloat(e_video.getAttribute("framerate")));
		} catch (Exception e) {
		}
		try {
			systemvideo = Systemvideo.getSystem(e_video.getAttribute("system"));
		} catch (Exception e) {
		}
		
		Element e_audio = XmlData.getElementByName(reference, "audio");
		try {
			audio_bitrate = Integer.valueOf(e_audio.getAttribute("bitrate"));
		} catch (Exception e) {
		}
		try {
			audio_channel_count = Integer.valueOf(e_audio.getAttribute("channels"));
		} catch (Exception e) {
		}
		try {
			audio_sample_freq = Integer.valueOf(e_audio.getAttribute("frequency"));
		} catch (Exception e) {
		}
		try {
			audio_codec = e_audio.getAttribute("codec");
		} catch (Exception e) {
		}
		
	}
	
	private String recommended_videoformat = "";
	private String recommended_audioformat = "";
	private String recommended_containerformat = "";
	private String name = null;
	
	private String containername = null;
	private int streamcount_audio_min = 0;
	private int streamcount_audio_max = Integer.MAX_VALUE;
	private int streamcount_video_min = 0;
	private int streamcount_video_max = Integer.MAX_VALUE;
	private int streamcount_data_min = 0;
	private int streamcount_data_max = Integer.MAX_VALUE;
	
	private int x_width = -1;
	private int y_height = -1;
	private long video_bitrate = -1;
	private boolean aspectratio_16_9 = true;
	private boolean aspectratio_4_3 = true;
	private ChromaFormat chromaformat = null;
	private String video_codec = null;
	private Framerate framerate = null;
	private Systemvideo systemvideo = null;
	private boolean aspectratio_VGA = true;
	
	private long audio_bitrate = -1;
	private int audio_channel_count = -1;
	private String audio_codec = null;
	private int audio_sample_freq = -1;
	
	private ArrayList<String> extensions = null;
	private ArrayList<String> filemime = null;
	
	public String getRecommended_VideoFormat(String newline) {
		StringBuffer sb = new StringBuffer();
		sb.append(recommended_videoformat);
		// sb.append(newline);
		sb.append(" strictly [ ");
		
		if (video_codec != null) {
			sb.append("codec: ");
			sb.append(video_codec);
			sb.append(" ");
		}
		if (video_bitrate != -1) {
			sb.append("bitrate: ");
			sb.append(video_bitrate);
			sb.append(" ");
		}
		if (x_width != -1) {
			sb.append("width: ");
			sb.append(x_width);
			sb.append(" ");
		}
		if (y_height != -1) {
			sb.append("height: ");
			sb.append(y_height);
			sb.append(" ");
		}
		if (chromaformat != null) {
			sb.append("chroma: ");
			sb.append(chromaformat);
			sb.append(" ");
		}
		if (framerate != null) {
			sb.append("framerate: ");
			sb.append(framerate);
			sb.append(" ");
		}
		if (systemvideo != null) {
			sb.append("system: ");
			sb.append(systemvideo);
			sb.append(" ");
		}
		if (aspectratio_16_9 | aspectratio_4_3 | aspectratio_VGA) {
			sb.append("ratio: ");
			if (aspectratio_16_9) {
				sb.append("16/9 ");
			}
			if (aspectratio_4_3) {
				sb.append("4/3 ");
			}
			if (aspectratio_VGA) {
				sb.append("1/1 ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	public String getRecommended_AudioFormat(String newline) {
		StringBuffer sb = new StringBuffer();
		sb.append(recommended_audioformat);
		// sb.append(newline);
		sb.append(" strictly [ ");
		
		if (audio_codec != null) {
			sb.append("codec: ");
			sb.append(audio_codec);
			sb.append(" ");
		}
		if (audio_channel_count != -1) {
			sb.append("channels: ");
			sb.append(audio_channel_count);
			sb.append(" ");
		}
		if (audio_bitrate != -1) {
			sb.append("bitrate: ");
			sb.append(audio_bitrate);
			sb.append(" ");
		}
		if (audio_sample_freq != -1) {
			sb.append("sample: ");
			sb.append(audio_sample_freq);
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public String getRecommended_ContainerFormat(String newline) {
		StringBuffer sb = new StringBuffer();
		sb.append(recommended_containerformat);
		// sb.append(newline);
		sb.append(" strictly [ ");
		
		if (extensions != null) {
			sb.append("extension: ");
			for (int pos = 0; pos < extensions.size(); pos++) {
				sb.append(extensions.get(pos));
				if ((pos + 1) < extensions.size()) {
					sb.append(", ");
				}
			}
			sb.append(" ");
		}
		if (filemime != null) {
			sb.append("mime: ");
			for (int pos = 0; pos < filemime.size(); pos++) {
				sb.append(filemime.get(pos));
				if ((pos + 1) < filemime.size()) {
					sb.append(", ");
				}
			}
			sb.append(" ");
		}
		
		if (containername != null) {
			sb.append("container: ");
			sb.append(containername);
			sb.append(" ");
		}
		
		if (streamcount_video_min > 0) {
			sb.append("min-video-stream: ");
			sb.append(streamcount_video_min);
			sb.append(" ");
		}
		if (streamcount_video_max < Integer.MAX_VALUE) {
			sb.append("max-video-stream: ");
			sb.append(streamcount_video_max);
			sb.append(" ");
		}
		if (streamcount_audio_min > 0) {
			sb.append("min-audio-stream: ");
			sb.append(streamcount_audio_min);
			sb.append(" ");
		}
		if (streamcount_audio_max < Integer.MAX_VALUE) {
			sb.append("max-audio-stream: ");
			sb.append(streamcount_audio_max);
			sb.append(" ");
		}
		if (streamcount_data_min > 0) {
			sb.append("min-data-stream: ");
			sb.append(streamcount_data_min);
			sb.append(" ");
		}
		if (streamcount_data_max < Integer.MAX_VALUE) {
			sb.append("max-data-stream: ");
			sb.append(streamcount_data_max);
			sb.append(" ");
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	public boolean validContainername(String name) {
		if (containername != null) {
			return name.equalsIgnoreCase(this.containername);
		}
		return true;
	}
	
	public boolean validStreamcount(int video_count, int audio_count, int data_count) {
		if ((video_count >= this.streamcount_video_min) == false) {
			return false;
		}
		if ((video_count <= this.streamcount_video_max) == false) {
			return false;
		}
		if ((audio_count >= this.streamcount_audio_min) == false) {
			return false;
		}
		if ((audio_count <= this.streamcount_audio_max) == false) {
			return false;
		}
		if ((data_count >= this.streamcount_data_min) == false) {
			return false;
		}
		if ((data_count <= this.streamcount_data_max) == false) {
			return false;
		}
		return true;
	}
	
	public boolean validVideoResolution(int x_width, int y_height) {
		if ((this.x_width > 0) && (this.y_height > 0)) {
			if (this.x_width != x_width) {
				return false;
			}
			if (this.y_height != y_height) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validVideoBitrate(long video_bps) {
		if (this.video_bitrate == -1) {
			return true;
		}
		if (this.video_bitrate == video_bps) {
			return true;
		}
		return false;
	}
	
	public boolean validVideoAspectratio(Aspectratio aspectratio) {
		if (aspectratio_VGA && (aspectratio == Aspectratio.AR_VGA)) {
			return true;
		}
		if (aspectratio_4_3 && (aspectratio == Aspectratio.AR_43)) {
			return true;
		}
		if (aspectratio_16_9 && (aspectratio == Aspectratio.AR_169)) {
			return true;
		}
		return false;
	}
	
	public boolean validVideoChromaFormat(ChromaFormat chromaformat) {
		if (this.chromaformat != null) {
			if (this.chromaformat != chromaformat) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validVideoCodec(String video_codec) {
		if (this.video_codec != null) {
			if (this.video_codec.equalsIgnoreCase(video_codec) == false) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validVideoFramerate(Framerate framerate) {
		if (this.framerate != null) {
			if (this.framerate != framerate) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validVideoSystem(Systemvideo systemvideo) {
		if (this.systemvideo != null) {
			if (this.systemvideo != systemvideo) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validAudioBitrate(long audio_bps) {
		if (this.audio_bitrate == -1) {
			return true;
		}
		if (this.audio_bitrate == audio_bps) {
			return true;
		}
		return false;
	}
	
	public boolean validAudioChannelCount(int count) {
		if (this.audio_channel_count == -1) {
			return true;
		}
		if (this.audio_channel_count == count) {
			return true;
		}
		return false;
	}
	
	public boolean validAudioCodec(String audio_codec) {
		if (this.audio_codec != null) {
			if (this.audio_codec.equalsIgnoreCase(audio_codec) == false) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validAudioSamplefreq(int audio_sample_freq) {
		if (this.audio_sample_freq == -1) {
			return true;
		}
		if (this.audio_sample_freq == audio_sample_freq) {
			return true;
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean validFileMime(String mime) {
		if (this.filemime != null) {
			for (int pos = 0; pos < filemime.size(); pos++) {
				if (filemime.get(pos).equalsIgnoreCase(mime)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	public boolean validFileExtension(String extension) {
		if (this.extensions != null) {
			for (int pos = 0; pos < extensions.size(); pos++) {
				if (extensions.get(pos).equalsIgnoreCase(extension)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	public Log2Dump getLog2Dump() {
		Log2Dump dump = new Log2Dump();
		dump.add("name", name);
		return dump;
	}
}
