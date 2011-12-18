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

import hd3gtv.javamailwrapper.MailConfigurator;
import hd3gtv.javamailwrapper.MailSendException;
import hd3gtv.javamailwrapper.SendMail;
import hd3gtv.javamailwrapper.SendMail.Priority;
import hd3gtv.javamailwrapper.SendMailContent;
import hd3gtv.log2.Log2;
import hd3gtv.log2.Log2Dump;
import hd3gtv.tools.XmlData;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class ReportProcessor {
	
	private ArrayList<ReportEvent> events;
	private ArrayList<FFprobeFormatreference> ffprobeformatreference;
	private MailConfigurator mailconfigurator;
	private SendMail mail_users;
	
	public ReportProcessor(XmlData xmlconfiguration, VideoverifyService service, ArrayList<FFprobeFormatreference> ffprobeformatreference) {
		if (xmlconfiguration == null) {
			throw new NullPointerException("\"xmlconfiguration\" can't to be null"); //$NON-NLS-1$
		}
		if (service == null) {
			throw new NullPointerException("\"service\" can't to be null"); //$NON-NLS-1$
		}
		Element javamailconfiguration = XmlData.getElementByName(xmlconfiguration.getDocumentElement().getChildNodes(), "usermails"); //$NON-NLS-1$
		if (javamailconfiguration == null) {
			throw new NullPointerException("No usermails configuration in xmlconfig"); //$NON-NLS-1$
		}
		mailconfigurator = MailConfigurator.importToXMLElement(javamailconfiguration);
		mail_users = new SendMail(mailconfigurator);
		
		events = new ArrayList<ReportEvent>();
		
		this.ffprobeformatreference = ffprobeformatreference;
		if (ffprobeformatreference == null) {
			throw new NullPointerException("\"ffprobeformatreference\" can't to be null"); //$NON-NLS-1$
		}
	}
	
	public void add(ReportEvent event) {
		events.add(event);
	}
	
	/**
	 * @return true if there are events
	 */
	public boolean createAndSendAReport() throws MailSendException {
		
		if (events.size() == 0) {
			return false;
		}
		
		StringBuffer plaintext = new StringBuffer();
		ArrayList<String> html = new ArrayList<String>();
		
		html.add("<html><body>"); //$NON-NLS-1$
		
		plaintext.append(Messages.getString("ReportProcessor.filesinerrorplain")); //$NON-NLS-1$
		html.add(Messages.getString("ReportProcessor.filesinerrorhtml")); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append(Messages.getString("ReportProcessor.somefilesareinvalidplain")); //$NON-NLS-1$
		html.add(Messages.getString("ReportProcessor.somefilesareinvalidhtml")); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append(Messages.getString("ReportProcessor.pleasereplaceplain")); //$NON-NLS-1$
		html.add(Messages.getString("ReportProcessor.pleasereplacehtml")); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append(Messages.getString("ReportProcessor.youcanignoreplain")); //$NON-NLS-1$
		html.add(Messages.getString("ReportProcessor.youcanignorehtml")); //$NON-NLS-1$
		plaintext.append("\r\n"); //$NON-NLS-1$
		
		ReportEvent currentevent = null;
		File lastfile = null;
		html.add("<table style=\"border : 1px solid black;\">"); //$NON-NLS-1$
		String color[] = { "#F0F0F0", "#FFFFFF" }; //$NON-NLS-1$ //$NON-NLS-2$
		int filecount = 0;
		for (int posev = 0; posev < events.size(); posev++) {
			currentevent = events.get(posev);
			plaintext.append(currentevent.getFile().getPath());
			plaintext.append("\t"); //$NON-NLS-1$
			plaintext.append(currentevent.getCompleteEvent(false));
			plaintext.append("\r\n"); //$NON-NLS-1$
			
			if (lastfile != null) {
				if (lastfile != currentevent.getFile()) {
					html.add("</td></tr><tr bgcolor=\"" + color[filecount % 2] + "\"><td>"); //$NON-NLS-1$ //$NON-NLS-2$
					html.add(currentevent.getFile().getPath());
					html.add("</td><td>"); //$NON-NLS-1$
					filecount++;
				}
			} else {
				html.add("<tr bgcolor=\"" + color[filecount % 2] + "\"><td>"); //$NON-NLS-1$ //$NON-NLS-2$
				html.add(currentevent.getFile().getPath());
				html.add("</td><td>"); //$NON-NLS-1$
				filecount++;
			}
			html.add(currentevent.getCompleteEvent(true));
			lastfile = currentevent.getFile();
		}
		
		html.add("</td></tr></table>"); //$NON-NLS-1$
		
		plaintext.append("\r\n"); //$NON-NLS-1$
		plaintext.append(Messages.getString("ReportProcessor.formatrefplain")); //$NON-NLS-1$
		html.add(Messages.getString("ReportProcessor.formatrefhtml")); //$NON-NLS-1$
		
		for (int posffr = 0; posffr < ffprobeformatreference.size(); posffr++) {
			plaintext.append(" * "); //$NON-NLS-1$
			html.add("<b>"); //$NON-NLS-1$
			plaintext.append(ffprobeformatreference.get(posffr).getName());
			html.add(ffprobeformatreference.get(posffr).getName());
			plaintext.append("\r\n"); //$NON-NLS-1$
			html.add("</b><br />"); //$NON-NLS-1$
			
			plaintext.append(Messages.getString("ReportProcessor.containerplain")); //$NON-NLS-1$
			html.add(Messages.getString("ReportProcessor.containerhtml")); //$NON-NLS-1$
			plaintext.append(ffprobeformatreference.get(posffr).getRecommended_ContainerFormat("\r\n")); //$NON-NLS-1$
			html.add(ffprobeformatreference.get(posffr).getRecommended_ContainerFormat("<br />\r\n")); //$NON-NLS-1$
			plaintext.append("\r\n"); //$NON-NLS-1$
			html.add("<br />\r\n"); //$NON-NLS-1$
			
			plaintext.append(Messages.getString("ReportProcessor.videoplain")); //$NON-NLS-1$
			html.add(Messages.getString("ReportProcessor.videohtml")); //$NON-NLS-1$
			plaintext.append(ffprobeformatreference.get(posffr).getRecommended_VideoFormat("\r\n")); //$NON-NLS-1$
			html.add(ffprobeformatreference.get(posffr).getRecommended_VideoFormat("<br />\r\n")); //$NON-NLS-1$
			plaintext.append("\r\n"); //$NON-NLS-1$
			html.add("<br />\r\n"); //$NON-NLS-1$
			
			plaintext.append(Messages.getString("ReportProcessor.audioplain")); //$NON-NLS-1$
			html.add(Messages.getString("ReportProcessor.audiohtml")); //$NON-NLS-1$
			plaintext.append(ffprobeformatreference.get(posffr).getRecommended_AudioFormat("\r\n")); //$NON-NLS-1$
			html.add(ffprobeformatreference.get(posffr).getRecommended_AudioFormat("<br />\r\n")); //$NON-NLS-1$
			plaintext.append("\r\n"); //$NON-NLS-1$
			html.add("<br />\r\n"); //$NON-NLS-1$
			plaintext.append("\r\n"); //$NON-NLS-1$
		}
		
		html.add("</body></html>"); //$NON-NLS-1$
		
		SendMailContent smcth = new SendMailContent();
		smcth.setSubject(Messages.getString("ReportProcessor.mailsubject")); //$NON-NLS-1$
		smcth.setPlaintext(plaintext.toString());
		smcth.setHtmltext(html);
		mail_users.setPriority(Priority.Normal);
		mail_users.send(smcth);
		
		Log2Dump dump = new Log2Dump();
		dump.add("events", events); //$NON-NLS-1$
		Log2.log.info("Mail report sended", dump); //$NON-NLS-1$
		events.clear();
		return true;
	}
}
