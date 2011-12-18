/*
 * This file is part of Java Simple ServiceManager
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

package hd3gtv.javasimpleservice;

import hd3gtv.log2.Log2Dump;
import hd3gtv.log2.Log2Dumpable;
import hd3gtv.log2.Log2Event;
import hd3gtv.tools.XmlData;

import java.util.ArrayList;

/**
 * @author hdsdi3g
 * @version 1.0
 */
public class ServiceMessageError implements ServiceMessage {
	
	private Throwable throwable;
	private String basemessagelocalized;
	private Log2Dump dump;
	protected XmlData xmlconfiguration;
	
	public ServiceMessageError(XmlData xmlconfiguration, String basemessagelocalized, Throwable throwable, Log2Dumpable dump) {
		this.xmlconfiguration = xmlconfiguration;
		if (xmlconfiguration == null) {
			throw new NullPointerException("\"xmlconfiguration\" can't to be null"); //$NON-NLS-1$
		}
		this.basemessagelocalized = basemessagelocalized;
		if (basemessagelocalized == null) {
			throw new NullPointerException("\"basemessagelocalized\" can't to be null"); //$NON-NLS-1$
		}
		this.throwable = throwable;
		if (dump != null) {
			this.dump = dump.getLog2Dump();
		}
	}
	
	public ServiceMessageError(XmlData xmlconfiguration, String basemessagelocalized, Throwable throwable) {
		this.xmlconfiguration = xmlconfiguration;
		if (xmlconfiguration == null) {
			throw new NullPointerException("\"xmlconfiguration\" can't to be null"); //$NON-NLS-1$
		}
		
		this.basemessagelocalized = basemessagelocalized;
		if (basemessagelocalized == null) {
			throw new NullPointerException("\"basemessagelocalized\" can't to be null"); //$NON-NLS-1$
		}
		this.throwable = throwable;
	}
	
	public ServiceMessageError(XmlData xmlconfiguration, String basemessagelocalized, Throwable throwable, Log2Dump dump) {
		this.xmlconfiguration = xmlconfiguration;
		if (xmlconfiguration == null) {
			throw new NullPointerException("\"xmlconfiguration\" can't to be null"); //$NON-NLS-1$
		}
		
		this.basemessagelocalized = basemessagelocalized;
		if (basemessagelocalized == null) {
			throw new NullPointerException("\"basemessagelocalized\" can't to be null"); //$NON-NLS-1$
		}
		this.throwable = throwable;
		this.dump = dump;
	}
	
	public Log2Dump getLog2Dump() {
		Log2Dump finaldump = new Log2Dump();
		finaldump.addAll(dump);
		finaldump.add("basemessagelocalized", basemessagelocalized); //$NON-NLS-1$
		return dump;
	}
	
	public String getSubjectContent() {
		return Messages.getString("ServiceMessageError.generalerror") + basemessagelocalized; //$NON-NLS-1$
	}
	
	public String getMessageHeader() {
		return Messages.getString("ServiceMessageError.thereisanerror"); //$NON-NLS-1$
	}
	
	public String getMessageFooter() {
		return Messages.getString("ServiceMessageError.thankstocheck"); //$NON-NLS-1$
	}
	
	public String getMessageContent(boolean htmlallowed) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(basemessagelocalized);
		sb.append("\r\n"); //$NON-NLS-1$
		sb.append("\r\n"); //$NON-NLS-1$
		
		sb.append("Thread : "); //$NON-NLS-1$
		sb.append(Thread.currentThread().getName());
		sb.append(" ("); //$NON-NLS-1$
		sb.append(Thread.currentThread().getId());
		sb.append(")\r\n"); //$NON-NLS-1$
		
		if (throwable != null) {
			sb.append("\r\n"); //$NON-NLS-1$
			sb.append(throwable.getClass().getName());
			sb.append(": "); //$NON-NLS-1$
			sb.append(throwable.getMessage());
			sb.append("\r\n"); //$NON-NLS-1$
			Log2Event.throwableToString(throwable, sb, "\r\n"); //$NON-NLS-1$
		}
		
		if (dump != null) {
			sb.append("\r\n"); //$NON-NLS-1$
			dump.dumptoString(sb, "\r\n"); //$NON-NLS-1$
			sb.append("\r\n"); //$NON-NLS-1$
		}
		
		return sb.toString();
	}
	
	public ArrayList<Log2Dump> getTablecontent() {
		return null;
	}
	
}
