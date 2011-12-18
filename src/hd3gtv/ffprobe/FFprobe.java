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
import hd3gtv.ffprobe.VideoConst.Framerate;
import hd3gtv.ffprobe.VideoConst.Systemvideo;
import hd3gtv.javasimpleservice.ServiceManager;
import hd3gtv.log2.Log2;
import hd3gtv.log2.Log2Dump;
import hd3gtv.tools.ExecprocessGettext;
import hd3gtv.tools.Timecode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class FFprobe implements Serializable {
	
	private static final long serialVersionUID = -1756048538335442124L;
	
	private transient ExecprocessGettext run;
	
	private File inputfile;
	
	private ArrayList<MediaGenericStream> mediastreams;
	private MediaContainer mediacontainer;
	
	private ArrayList<FFprobeTag> returnblocks;
	
	private boolean analystisdone;
	
	private enum Blocktype {
		STREAM, FORMAT
	}
	
	@SuppressWarnings("nls")
	public FFprobe(File inputfile) throws NullPointerException, FileNotFoundException {
		this.inputfile = inputfile;
		if (inputfile == null) {
			throw new NullPointerException("\"inputfile\" can't to be null");
		}
		if (inputfile.exists() == false) {
			throw new FileNotFoundException(inputfile.getPath());
		}
		mediastreams = new ArrayList<MediaGenericStream>();
		mediacontainer = new MediaContainer();
		
		returnblocks = new ArrayList<FFprobeTag>();
		
		// ServiceManager.injectDefaultSystemProperty("executable.ffmpeg", "/opt/local/bin/ffmpeg");
		ServiceManager.injectDefaultSystemProperty("executable.ffprobe", "ffprobe");
		analystisdone = false;
	}
	
	public ArrayList<MediaGenericStream> getStreams() {
		return mediastreams;
	}
	
	public MediaVideoStream getVideo() {
		return MediaGenericStream.getVideoStream(mediastreams);
	}
	
	public MediaContainer getContainer() {
		return mediacontainer;
	}
	
	public File getInputfile() {
		return inputfile;
	}
	
	public ArrayList<FFprobeTag> getReturnblocks() {
		return returnblocks;
	}
	
	@SuppressWarnings("nls")
	public void analyst() throws IOException {
		ArrayList<String> params = new ArrayList<String>();
		params.add("-show_format");
		params.add("-show_streams");
		params.add("-i");
		params.add(inputfile.getPath());
		
		run = new ExecprocessGettext(System.getProperty("executable.ffprobe"), params);
		run.setEndlinewidthnewline(true);
		run.setExitcodemusttobe0(true);
		
		try {
			run.start();
		} catch (IOException e) {
			String[] stderrarray = run.getResultstderr().toString().split("\n");
			Log2Dump dump = new Log2Dump();
			dump.add("exitcode", run.getRunprocess().getExitvalue());
			dump.add("stdout", run.getResultstdout());
			dump.add("stderr", run.getResultstderr());
			Log2.log.error("ffprobe execution error", null, dump);
			throw new IOException("ffprobe execution error : " + stderrarray[stderrarray.length - 1], e);
		}
		
		ArrayList<String> ffmpegfullreturn = new ArrayList<String>();
		
		String stderr = run.getResultstderr().toString();
		String[] stderrarray = stderr.split("\n");
		for (int i = 0; i < stderrarray.length; i++) {
			ffmpegfullreturn.add(stderrarray[i].trim());
		}
		
		String line;
		
		/**
		 * Pour toutes les lignes retournes par ffmpeg, stderr
		 */
		for (int i = 0; i < ffmpegfullreturn.size(); i++) {
			line = ffmpegfullreturn.get(i);
			
			/**
			 * On traite la duree
			 */
			if (line.startsWith("Duration:")) {
				/**
				 * Duration: 00:01:02.2, start: 0.000000, bitrate: 977 kb/s
				 */
				StringBuffer concat;
				
				String[] zones = line.split(",");
				String keyname;
				String keyvalue;
				
				for (int posz = 0; posz < zones.length; posz++) {
					String[] subzones = zones[posz].split(":");
					if (subzones.length > 1) {
						concat = new StringBuffer();
						for (int j = 1; j < subzones.length; j++) {
							concat.append(subzones[j]);
							if ((j + 1) < subzones.length) {
								concat.append(":");
							}
						}
						keyname = subzones[0].trim().toLowerCase();
						keyvalue = concat.toString().trim();
						if (keyname.equals("duration")) {
							if ((keyvalue.length() == "00:00:00.0".length()) & (keyvalue.charAt(8) == ".".charAt(0))) {
								mediacontainer.duration = new Timecode(keyvalue + "0", 100);
							} else {
								mediacontainer.duration = new Timecode(keyvalue, 100);
							}
							continue;
						}
						if (keyname.equals("start")) {
							continue;
						}
						if (keyname.equals("bitrate")) {
							String bitratevalue = keyvalue.split(" ")[0].trim();
							try {
								mediacontainer.bitrate = Long.parseLong(bitratevalue) * 1000l;
							} catch (NumberFormatException e) {
								mediacontainer.bitrate = -1;
							}
						}
					}
				}
				continue;
			}
			
			/**
			 * On traite une information de flux
			 */
			if (line.startsWith("Stream #")) {
				String[] maingroup = line.split(":");
				
				if (maingroup.length < 3) {
					continue;
				}
				String streamchannel = maingroup[0].trim(); // Stream #0.0[0x1e0]
				String streamtrack = streamchannel.substring(("Stream #").length()); // streamtrack = streamtrack.replace(".", "-");
				String streamtype = maingroup[1].trim(); // Video / Audio
				
				StringBuffer sb_streaminfo = new StringBuffer();
				for (int posmg = 2; posmg < maingroup.length; posmg++) {
					sb_streaminfo.append(maingroup[posmg].trim());
					if (posmg + 1 != maingroup.length) {
						sb_streaminfo.append(":");
					}
				}
				String streaminfo = sb_streaminfo.toString();
				
				String[] streaminfos = streaminfo.split(",");
				
				if (streamtype.equals("Video")) {
					/**
					 * h264, yuv420p, 1280x720 [PAR 1:1 DAR 16:9], 5995 kb/s, 25 fps, 25 tbr, 25k tbn, 50 tbc
					 * Apple ProRes 422, 1920x1080, 114762 kb/s, 25 fps, 25 tbr, 2500 tbn, 2500 tbc
					 * dvvideo, yuv420p, 720x576, 28800 kb/s, PAR 1024:702 DAR 640:351, 25 fps, 25 tbr, 25 tbn, 25 tbc
					 * dvvideo, yuv420p, 720x576, 28800 kb/s, PAR 118:81 DAR 295:162, 25 fps, 25 tbr, 25 tbn, 25 tbc
					 * dvvideo, yuv411p, 720x576, 28800 kb/s, PAR 64:45 DAR 16:9, 25 tbr, 25 tbn, 25 tbc
					 */
					
					MediaVideoStream videostream = new MediaVideoStream();
					videostream.streamid = streamtrack;
					
					videostream.codecname = streaminfos[0].trim();
					
					String streamelement;
					for (int posln = 1; posln < streaminfos.length; posln++) {
						streamelement = streaminfos[posln].trim();
						
						if (streamelement.indexOf("x") > 1) {
							int posx = streamelement.indexOf("x");
							/**
							 * Test si on a un chiffre juste avant le x et juste apres : 0x0
							 */
							Integer.parseInt(streamelement.substring(posx - 2, posx - 1));
							Integer.parseInt(streamelement.substring(posx + 1, posx + 2));
							
							String[] framesizepixr = streamelement.trim().split(" ");
							/**
							 * gestion de 720x576
							 */
							videostream.width = Integer.parseInt(framesizepixr[0].split("x")[0]);
							videostream.height = Integer.parseInt(framesizepixr[0].split("x")[1]);
							
							if (framesizepixr.length > 1) {
								/**
								 * gestion de [PAR 64:45 DAR 16:9]
								 */
								int from = framesizepixr[0].length() + 2;
								int to = streamelement.trim().length() - 1;
								String aspectratio = streamelement.trim().substring(from, to);
								videostream.aspectratio = Aspectratio.parseAR(aspectratio.substring(aspectratio.lastIndexOf(" ") + 1));
							} else {
								float rar = (float) videostream.width / (float) videostream.height;
								videostream.aspectratio = Aspectratio.parseAR(String.valueOf(rar));
							}
							
							continue;
						}
						
						if (streamelement.endsWith("kb/s")) {
							videostream.bitrate = Long.parseLong(streamelement.trim().split(" ")[0]) * 1000l;
							continue;
						}
						if (streamelement.endsWith("fps")) {
							videostream.framerate = Framerate.getFramerate(Float.parseFloat(streamelement.trim().split(" ")[0]));
							
							if (mediacontainer.duration != null) {
								mediacontainer.duration.setFps(videostream.framerate.getNumericValue());
							}
							if (videostream.systemvideo == null) {
								videostream.systemvideo = Systemvideo.getSystem(videostream.width, videostream.height, videostream.framerate);
							}
							
							continue;
						}
						
						if (streamelement.endsWith("tbr")) {
							if (videostream.framerate == null) {
								videostream.framerate = Framerate.getFramerate(Float.parseFloat(streamelement.trim().split(" ")[0]));
								if (mediacontainer.duration != null) {
									mediacontainer.duration.setFps(videostream.framerate.getNumericValue());
								}
							} else {
								Framerate framerate_tbr = Framerate.getFramerate(Float.parseFloat(streamelement.trim().split(" ")[0]));
								if (framerate_tbr != videostream.framerate) {
									if ((videostream.framerate == Framerate.FPS_50) & (framerate_tbr == Framerate.FPS_25)) {
										videostream.framerate = Framerate.FPS_25;
									}
									if ((videostream.framerate == Framerate.FPS_5994) & (framerate_tbr == Framerate.FPS_2997)) {
										videostream.framerate = Framerate.FPS_2997;
									}
								}
							}
							
							continue;
						}
						
						if (streamelement.endsWith("tbn") | streamelement.endsWith("tbc")) {
							continue;
						}
						
						if (streamelement.startsWith("PAR")) {
							/**
							 * PAR 1024:702 DAR 640:351
							 * On ignore les valeurs eventuelles calcules precedement car ici les resultats sont plus precis : gestion des pixels non carres
							 */
							String[] darvalue = streamelement.trim().split(" ")[3].split(":");
							
							float dar = Float.parseFloat(darvalue[0]) / Float.parseFloat(darvalue[1]);
							float rar = (float) videostream.width / (float) videostream.height;
							
							float normar = dar / rar;
							
							int convertedpar = (int) Math.floor(normar * 100);
							
							if (convertedpar == 106) {
								videostream.aspectratio = Aspectratio.AR_43;
								videostream.systemvideo = Systemvideo.PAL;
							}
							if (convertedpar == 109) {
								videostream.aspectratio = Aspectratio.AR_43;
								videostream.systemvideo = Systemvideo.PAL;
							}
							if (convertedpar == 145) {
								videostream.aspectratio = Aspectratio.AR_169;
								videostream.systemvideo = Systemvideo.PAL;
							}
							if (convertedpar == 142) {
								videostream.aspectratio = Aspectratio.AR_169;
								videostream.systemvideo = Systemvideo.PAL;
							}
							if (convertedpar == 90) {
								videostream.aspectratio = Aspectratio.AR_43;
								videostream.systemvideo = Systemvideo.NTSC;
							}
							if (convertedpar == 121) {
								videostream.aspectratio = Aspectratio.AR_169;
								videostream.systemvideo = Systemvideo.NTSC;
							}
							if (convertedpar == 109) {
								videostream.aspectratio = Aspectratio.AR_43;
								videostream.systemvideo = Systemvideo.PAL;
							}
							if (videostream.aspectratio == null) {
								videostream.aspectratio = Aspectratio.parseAR(String.valueOf(rar));
							}
							continue;
						}
						videostream.pixelcolor = streamelement.trim();
						videostream.setChromaformat();
					}
					if (videostream.pixelcolor == null) {
						if (videostream.codecname.startsWith("Apple ProRes 422")) {
							videostream.pixelcolor = "yuv422p";
							videostream.setChromaformat();
						}
						if (videostream.codecname.equals("Apple ProRes 4444")) {
							videostream.pixelcolor = "4444";
							videostream.setChromaformat();
						}
					}
					mediastreams.add(videostream);
				}
				
				if (streamtype.equals("Audio")) {
					if (streaminfos.length >= 3) {
						/**
						 * libfaad, 48000 Hz, stereo
						 */
						MediaAudioStream audiostream = new MediaAudioStream();
						/**
						 * 0x0162, 48000 Hz, stereo, 128 kb/s
						 */
						audiostream.streamid = streamtrack;
						audiostream.codecname = streaminfos[0].trim();
						
						if (audiostream.codecname.equals("ec-3 / 0x332D6365")) {
							audiostream.codecname = "E-AC3";
						}
						
						String streamelement;
						for (int posln = 1; posln < streaminfos.length; posln++) {
							streamelement = streaminfos[posln];
							if (streamelement.endsWith("Hz")) {
								audiostream.samplefrequency = Integer.parseInt(streamelement.trim().split(" ")[0]);
								continue;
							}
							if (streamelement.endsWith("kb/s")) {
								audiostream.bitrate = Long.parseLong(streamelement.trim().split(" ")[0]) * 1000l;
								continue;
							}
							
							if (streamelement.endsWith("channel")) {
								audiostream.channelscount = 1;
								continue;
							}
							
							if (streamelement.endsWith("channels")) {
								audiostream.channelscount = Integer.parseInt(streamelement.trim().split(" ")[0]);
								continue;
							}
						}
						if (audiostream.channelscount == -1) {
							/**
							 * Ancienne version
							 */
							audiostream.channelscount = VideoConst.stringToAudioChannelCount(streaminfos[2].trim());
						}
						mediastreams.add(audiostream);
					}
				}
				
				if (streamtype.equals("Data")) {
					MediaDataStream datastream = new MediaDataStream();
					datastream.streamid = streamtrack;
					datastream.codecname = "data";
					datastream.longname = streaminfos[0].trim();
					
					if (datastream.longname.equalsIgnoreCase("tmcd / 0x64636D74")) {
						datastream.codecname = "Time Code";
					}
					
					if (datastream.longname.equalsIgnoreCase("[0][0][0][0] / 0x0000")) {
						datastream.codecname = "data";
					}
					
					if (streaminfos.length > 1) {
						datastream.bitrate = Long.parseLong(streaminfos[1].trim().split(" ")[0]) * 1000l;
					}
					mediastreams.add(datastream);
				}
				
				continue;
			}
			if (line.startsWith("Input #0,")) {
				mediacontainer.name = line.split(",")[1].trim();
				continue;
			}
		}
		
		/**
		 * ffprobe "pur" : ajout de metadatas
		 */
		ffmpegfullreturn = new ArrayList<String>();
		
		String stdout = run.getResultstdout().toString();
		String[] stdoutarray = stdout.split("\n");
		for (int i = 0; i < stdoutarray.length; i++) {
			ffmpegfullreturn.add(stdoutarray[i].trim());
		}
		
		/**
		 * Pour toutes les lignes retournes par ffmpeg, stdout
		 */
		Blocktype currentblocktype = null;
		int current_index = -1;
		int posequalschar;
		String key;
		String value;
		for (int i = 0; i < ffmpegfullreturn.size(); i++) {
			line = ffmpegfullreturn.get(i);
			
			if (line.equalsIgnoreCase("[STREAM]")) {
				currentblocktype = Blocktype.STREAM;
				current_index = -1;
				continue;
			}
			if (line.equalsIgnoreCase("[/STREAM]")) {
				currentblocktype = null;
				current_index = -1;
				continue;
			}
			if (line.equalsIgnoreCase("[FORMAT]")) {
				currentblocktype = Blocktype.FORMAT;
				current_index = -1;
				continue;
			}
			if (line.equalsIgnoreCase("[/FORMAT]")) {
				currentblocktype = null;
				current_index = -1;
				continue;
			}
			
			if (currentblocktype == null) {
				continue;
			}
			
			posequalschar = line.indexOf("=");
			
			if (line.substring(0, posequalschar).equalsIgnoreCase("index")) {
				current_index = Integer.parseInt(line.substring(posequalschar + 1));
				continue;
			}
			
			if ((current_index > -1) | (currentblocktype == Blocktype.FORMAT)) {
				if (line.startsWith("TAG:")) {
					key = line.substring(4, posequalschar);
					value = line.substring(posequalschar + 1);
					
					if (currentblocktype == Blocktype.FORMAT) {
						if (mediacontainer.tags == null) {
							mediacontainer.tags = new ArrayList<FFprobeTag>();
						}
						mediacontainer.tags.add(new FFprobeTag(key, value));
						continue;
					}
					if (current_index > -1) {
						if (mediastreams.get(current_index).tags == null) {
							mediastreams.get(current_index).tags = new ArrayList<FFprobeTag>();
						}
						mediastreams.get(current_index).tags.add(new FFprobeTag(key, value));
						continue;
					}
					
				}
			}
			
		}
		
		/*
		 * ffprobe "pur" desactive
		 */
		/*for (int pos = 0; pos < returnblocks.size(); pos++) {
			currentblock = returnblocks.get(pos);
			if (currentblock.blocktype == Blocktype.STREAM) {
				String codectype = currentblock.datas.get("codec_type");
				
				if (codectype.equalsIgnoreCase("video")) {
					MediaVideoStream ffsi = new MediaVideoStream();
					ffsi.codecname = currentblock.datas.get("codec_name");
					ffsi.width = Integer.parseInt(currentblock.datas.get("width"));
					ffsi.height = Integer.parseInt(currentblock.datas.get("height"));
					ffsi.pixelcolor = currentblock.datas.get("pix_fmt");// yuv420p
					
					//		sample_aspect_ratio=1:1
					//		display_aspect_ratio=16:9
					//		r_frame_rate=25/1
					//		avg_frame_rate=25/1
					//		time_base=1/25000
					//		start_time=0.040000
					//		duration=20.920000
					//		nb_frames=523
					//		codec_long_name=H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10
					//		codec_tag_string=avc1
					//		codec_tag=0x31637661
					//		has_b_frames=1
					//		TAG:creation_time=2011-10-12 22:21:34
					//		TAG:language=eng
					mediastreams.add(ffsi);
				}
				if (codectype.equalsIgnoreCase("audio")) {
					
				}
				if (codectype.equalsIgnoreCase("data")) {
					
				}
				
			}
			if (currentblock.blocktype == Blocktype.FORMAT) {
				mediacontainer = new MediaContainer();
				
			}
		}
		}*/
		
		MediaVideoStream siv;
		for (int i = 0; i < mediastreams.size(); i++) {
			if (mediastreams.get(i) instanceof MediaVideoStream) {
				siv = (MediaVideoStream) mediastreams.get(i);
				siv.systemvideo = Systemvideo.getSystem(siv.width, siv.height, siv.framerate);
			}
		}
		
		analystisdone = true;
	}
	
	public boolean isAnalystIsDone() {
		return analystisdone;
	}
	
	public String getRawFFprobeOut() {
		return run.getResultstdout().toString();
	}
	
	public String getRawFFprobeErr() {
		return run.getResultstderr().toString();
	}
}
