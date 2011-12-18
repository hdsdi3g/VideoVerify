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
 * Copyright (C) hdsdi3g for hd3g.tv 2008-2011
 * 
*/

package hd3gtv.videoverify;

import hd3gtv.ffprobe.FFprobe;
import hd3gtv.ffprobe.FFprobeTag;
import hd3gtv.ffprobe.MediaAudioStream;
import hd3gtv.ffprobe.MediaContainer;
import hd3gtv.ffprobe.MediaDataStream;
import hd3gtv.ffprobe.MediaGenericStream;
import hd3gtv.ffprobe.MediaVideoStream;
import hd3gtv.log2.Log2;
import hd3gtv.log2.Log2Dump;
import hd3gtv.mimeutils.MimeutilsWrapper;
import hd3gtv.scandir.ScandirFilefound;
import hd3gtv.tools.XmlData;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class ProcessAnalyst {
	
	private VideoverifyService service;
	private ArrayList<FFprobeFormatreference> ffprobeformatreference;
	private ReportProcessor reportprocessor;
	
	// private XmlData xmlconfiguration;
	
	ProcessAnalyst(XmlData xmlconfiguration, VideoverifyService service) {
		if (xmlconfiguration == null) {
			throw new NullPointerException("\"xmlconfiguration\" can't to be null"); //$NON-NLS-1$
		}
		this.service = service;
		if (service == null) {
			throw new NullPointerException("\"service\" can't to be null"); //$NON-NLS-1$
		}
		
		Element e_format = XmlData.getElementByName(xmlconfiguration.getDocumentElement(), "format"); //$NON-NLS-1$
		if (e_format == null) {
			throw new NullPointerException("No XML element \"format\" in XML configuration"); //$NON-NLS-1$
		}
		
		ArrayList<Element> e_reference = XmlData.getElementsByName(e_format, "reference"); //$NON-NLS-1$
		if (e_reference == null) {
			throw new NullPointerException("No XML element \"reference\" in XML configuration"); //$NON-NLS-1$
		}
		if (e_reference.size() == 0) {
			throw new NullPointerException("No XML element \"reference\" in XML configuration"); //$NON-NLS-1$
		}
		
		ffprobeformatreference = new ArrayList<FFprobeFormatreference>(e_reference.size());
		for (int pos = 0; pos < e_reference.size(); pos++) {
			try {
				FFprobeFormatreferenceXml fffr = new FFprobeFormatreferenceXml(e_reference.get(pos));
				ffprobeformatreference.add(fffr);
				Log2.log.info("Load format reference from XML configuration", fffr); //$NON-NLS-1$
			} catch (Exception e) {
				Log2.log.error("Can't load format reference from XML configuration", e); //$NON-NLS-1$
			}
		}
		
		if (ffprobeformatreference.size() == 0) {
			throw new NullPointerException("No valid XML element \"reference\" in XML configuration"); //$NON-NLS-1$
		}
		
		reportprocessor = new ReportProcessor(xmlconfiguration, service, ffprobeformatreference);
		
	}
	
	public void analyst(ArrayList<ScandirFilefound> newfiles) throws Exception {
		
		File foundfile;
		FFprobeFormatreference reference;
		boolean isok = true;
		FFprobe ffprobe;
		
		for (int pos = newfiles.size() - 1; pos > -1; pos--) {
			/**
			 * Pour tous les fichiers a analyser
			 */
			if (service.isAlive() == false) {
				return;
			}
			foundfile = newfiles.get(pos).getFile();
			
			Log2Dump dump = new Log2Dump();
			dump.add("file", foundfile); //$NON-NLS-1$
			if (newfiles.size() > 1) {
				dump.add("src_id", String.valueOf(pos)); //$NON-NLS-1$
			}
			
			if (foundfile.exists() == false) {
				Log2.log.error("File is vanished !", null, dump); //$NON-NLS-1$
			}
			
			Log2.log.info("Check file", dump); //$NON-NLS-1$
			
			ffprobe = new FFprobe(foundfile);
			
			for (int posffr = 0; posffr < ffprobeformatreference.size(); posffr++) {
				/**
				 * Pour toutes les references
				 */
				isok = true;
				
				reference = ffprobeformatreference.get(posffr);
				
				dump = new Log2Dump();
				dump.add("file", foundfile); //$NON-NLS-1$
				dump.add("reference", reference.getName()); //$NON-NLS-1$
				
				String foundfilemime = MimeutilsWrapper.getMime(foundfile);
				if (reference.validFileMime(foundfilemime) == false) {
					dump.add("mime", foundfilemime); //$NON-NLS-1$
					Log2.log.debug("Invalid file type/mime", dump); //$NON-NLS-1$
					isok = false;
				}
				
				if (reference.validFileExtension(foundfile.getName().substring(foundfile.getName().indexOf(".") + 1)) == false) { //$NON-NLS-1$
					Log2.log.debug("Invalid file extension", dump); //$NON-NLS-1$
					isok = false;
				}
				
				if (ffprobe.isAnalystIsDone() == false) {
					/**
					 * On ne fait qu'une fois l'analyse.
					 */
					ffprobe.analyst();
				}
				
				MediaContainer ffsic = ffprobe.getContainer();
				
				if (reference.validContainername(ffsic.getContainerName()) == false) {
					Log2.log.debug("Invalid container", dump); //$NON-NLS-1$
					isok = false;
				}
				
				ArrayList<MediaGenericStream> ffsi_list = ffprobe.getStreams();
				int video_count = 0;
				int audio_count = 0;
				int data_count = 0;
				for (int posfsi = 0; posfsi < ffsi_list.size(); posfsi++) {
					if (ffsi_list.get(posfsi) instanceof MediaVideoStream) {
						video_count++;
					}
					if (ffsi_list.get(posfsi) instanceof MediaAudioStream) {
						audio_count++;
					}
					if (ffsi_list.get(posfsi) instanceof MediaDataStream) {
						data_count++;
					}
				}
				
				if (reference.validStreamcount(video_count, audio_count, data_count) == false) {
					dump.add("video_count", video_count); //$NON-NLS-1$
					dump.add("audio_count", audio_count); //$NON-NLS-1$
					dump.add("data_count", data_count); //$NON-NLS-1$
					Log2.log.debug("Invalid streamcount", dump); //$NON-NLS-1$
					isok = false;
				}
				
				MediaVideoStream ffsiv;
				MediaAudioStream ffsia;
				// MediaDataStream ffsim;
				
				for (int posffsil = 0; posffsil < ffsi_list.size(); posffsil++) {
					/**
					 * pour tous les flux du fichier
					 */
					if (ffsi_list.get(posffsil) instanceof MediaVideoStream) {
						/**
						 * Le flux est de la video
						 */
						ffsiv = (MediaVideoStream) ffsi_list.get(posffsil);
						
						if (reference.validVideoCodec(ffsiv.getCodecName()) == false) {
							dump.add("codec", ffsiv.getCodecName()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoCodec", dump); //$NON-NLS-1$
							isok = false;
						}
						
						if (reference.validVideoSystem(ffsiv.getSystemvideo()) == false) {
							dump.add("VideoSystem", ffsiv.getSystemvideo()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoSystem", dump); //$NON-NLS-1$
							isok = false;
						}
						
						if (reference.validVideoResolution(ffsiv.get_X_Width(), ffsiv.get_Y_Height()) == false) {
							dump.add("width", ffsiv.get_X_Width()); //$NON-NLS-1$
							dump.add("height", ffsiv.get_Y_Height()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoResolution", dump); //$NON-NLS-1$
							isok = false;
						}
						
						if (reference.validVideoBitrate(ffsiv.getBitrate()) == false) {
							dump.add("bitrate", ffsiv.getBitrate()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoBitrate", dump); //$NON-NLS-1$
							isok = false;
						}
						
						if (reference.validVideoAspectratio(ffsiv.getAspectratio()) == false) {
							dump.add("AspectRatio", ffsiv.getAspectratio()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoAspectratio", dump); //$NON-NLS-1$
							isok = false;
						}
						
						if (reference.validVideoChromaFormat(ffsiv.getChromaformat()) == false) {
							dump.add("ChromaFormat", ffsiv.getChromaformat()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoChromaFormat", dump); //$NON-NLS-1$
							isok = false;
						}
						
						if (reference.validVideoFramerate(ffsiv.getFramerate()) == false) {
							dump.add("framerate", ffsiv.getFramerate()); //$NON-NLS-1$
							Log2.log.debug("Invalid VideoFramerate", dump); //$NON-NLS-1$
							isok = false;
						}
						
					}
					if (ffsi_list.get(posffsil) instanceof MediaAudioStream) {
						/**
						 * le flux est de l'audio
						 */
						ffsia = (MediaAudioStream) ffsi_list.get(posffsil);
						
						if (reference.validAudioCodec(ffsia.getCodecName()) == false) {
							dump.add("codec", ffsia.getCodecName()); //$NON-NLS-1$
							Log2.log.debug("Invalid AudioCodec", dump); //$NON-NLS-1$
							isok = false;
						}
						if (reference.validAudioChannelCount(ffsia.getChannelsCount()) == false) {
							dump.add("ChannelsCount", ffsia.getChannelsCount()); //$NON-NLS-1$
							Log2.log.debug("Invalid audio ChannelsCount", dump); //$NON-NLS-1$
							isok = false;
						}
						if (reference.validAudioSamplefreq(ffsia.getSamplefrequency()) == false) {
							dump.add("Samplefrequency", ffsia.getSamplefrequency()); //$NON-NLS-1$
							Log2.log.debug("Invalid audio sample frequency", dump); //$NON-NLS-1$
							isok = false;
						}
						if (reference.validAudioBitrate(ffsia.getBitrate()) == false) {
							dump.add("bitrate", ffsia.getBitrate()); //$NON-NLS-1$
							Log2.log.debug("Invalid AudioBitrate", dump); //$NON-NLS-1$
							isok = false;
						}
						/**
						 * FIN analyse flux audio
						 */
					}
					// if (ffsi_list.get(posffsil) instanceof MediaDataStream) {
					// ffsim = (MediaDataStream) ffsi_list.get(posffsil);
					// }
					/** FIN analyse de tous les flux */
				}
				if (isok) {
					/**
					 * Le fichier est bien valide, on n'a pas besoin d'analyser avec la suite
					 */
					break;
				}
				/**
				 * FIN analyse reference
				 */
			}
			
			if (isok == false) {
				reportprocessor.add(new Event(foundfile, ffprobe));
			}
			
			/**
			 * FIN Pour tous les fichiers a analyser
			 */
		}
		
		if (reportprocessor.createAndSendAReport() == false) {
			Log2.log.info("Done. No problems."); //$NON-NLS-1$
		}
	}
	
	private class Event implements ReportEvent {
		
		private File file;
		private FFprobe ffprobe;
		
		public Event(File file, FFprobe ffprobe) {
			this.file = file;
			this.ffprobe = ffprobe;
		}
		
		public Log2Dump getLog2Dump() {
			Log2Dump dump = new Log2Dump();
			dump.add("file", file); //$NON-NLS-1$
			return dump;
		}
		
		public File getFile() {
			return file;
		}
		
		public String getCompleteEvent(boolean html) {
			StringBuffer plaintext = new StringBuffer();
			StringBuffer htmltext = new StringBuffer();
			
			String mime = MimeutilsWrapper.getMime(ffprobe.getInputfile());
			plaintext.append(Messages.getString("ProcessAnalyst.filetypeplain")); //$NON-NLS-1$
			htmltext.append(Messages.getString("ProcessAnalyst.filetypehtml")); //$NON-NLS-1$
			plaintext.append(mime);
			htmltext.append(mime);
			plaintext.append("\r\n"); //$NON-NLS-1$
			htmltext.append("<br />\r\n"); //$NON-NLS-1$
			
			MediaContainer ffsic = ffprobe.getContainer();
			plaintext.append(Messages.getString("ProcessAnalyst.containerplain")); //$NON-NLS-1$
			htmltext.append(Messages.getString("ProcessAnalyst.containerhtml")); //$NON-NLS-1$
			plaintext.append(ffsic.toString());
			htmltext.append(ffsic.toString());
			plaintext.append("\r\n"); //$NON-NLS-1$
			htmltext.append("<br />\r\n"); //$NON-NLS-1$
			tagsToString(plaintext, htmltext, ffsic.getTags());
			
			ArrayList<MediaGenericStream> ffsi_list = ffprobe.getStreams();
			for (int posffsil = 0; posffsil < ffsi_list.size(); posffsil++) {
				/**
				 * pour tous les flux du fichier
				 */
				if (ffsi_list.get(posffsil) instanceof MediaVideoStream) {
					plaintext.append(Messages.getString("ProcessAnalyst.videoplain")); //$NON-NLS-1$
					htmltext.append(Messages.getString("ProcessAnalyst.videohtml")); //$NON-NLS-1$
				}
				if (ffsi_list.get(posffsil) instanceof MediaAudioStream) {
					plaintext.append(Messages.getString("ProcessAnalyst.audioplain")); //$NON-NLS-1$
					htmltext.append(Messages.getString("ProcessAnalyst.audiohtml")); //$NON-NLS-1$
				}
				if (ffsi_list.get(posffsil) instanceof MediaDataStream) {
					plaintext.append(Messages.getString("ProcessAnalyst.dataplain")); //$NON-NLS-1$
					htmltext.append(Messages.getString("ProcessAnalyst.datahtml")); //$NON-NLS-1$
				}
				plaintext.append(ffsi_list.get(posffsil).toString());
				htmltext.append(ffsi_list.get(posffsil).toString());
				plaintext.append("\r\n"); //$NON-NLS-1$
				htmltext.append("<br />\r\n"); //$NON-NLS-1$
				tagsToString(plaintext, htmltext, ffsi_list.get(posffsil).getTags());
			}
			if (html) {
				return htmltext.toString();
			} else {
				return plaintext.toString();
			}
		}
		
		private void tagsToString(StringBuffer plaintext, StringBuffer htmltext, ArrayList<FFprobeTag> tags) {
			if (tags == null) {
				return;
			}
			for (int pos = 0; pos < tags.size(); pos++) {
				plaintext.append(" * "); //$NON-NLS-1$
				htmltext.append("&nbsp;&bull;&nbsp;"); //$NON-NLS-1$
				plaintext.append(tags.get(pos).toString());
				htmltext.append(tags.get(pos).toString());
				plaintext.append("\r\n"); //$NON-NLS-1$
				htmltext.append("<br />\r\n"); //$NON-NLS-1$
			}
		}
		
	}
}
