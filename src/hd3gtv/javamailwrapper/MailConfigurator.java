/*
 * This file is part of Javamail Wrapper
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

package hd3gtv.javamailwrapper;

import java.util.ArrayList;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.Session;

/**
 * Configuration generique pour l'envoi de mail.
 * @author hdsdi3g
 * @version 1.0
 */
@SuppressWarnings("nls")
public class MailConfigurator {
	
	String from;
	ArrayList<String> to;
	ArrayList<String> cc;
	ArrayList<String> bcc;
	String smtp_server;
	boolean debug;
	Session session;
	String mail_header_listid;
	
	MailConfigurator() {
		session = null;
		setDebug(false);
	}
	
	/**
	 * @param smtp_server Serveur a contacter pour envoyer le mail.
	 * @see importToXMLElement()
	 */
	public MailConfigurator(String smtp_server) {
		// T O D O fonctions de dig et de resolv MX, et A/AAAA
		Properties prop = System.getProperties();
		if (smtp_server == null) {
			smtp_server = "127.0.0.1";
		}
		if (smtp_server.equals("")) {
			smtp_server = "127.0.0.1";
		}
		prop.put("mail.smtp.host", smtp_server);
		session = Session.getDefaultInstance(prop);
		from = "root@localhost";
	}
	
	/**
	 * Expediteur par defaut.
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
	/**
	 * Destinataires par defaut.
	 */
	public void setTo(ArrayList<String> to) {
		this.to = to;
	}
	
	/**
	 * Destinataires par defaut.
	 */
	public void setCc(ArrayList<String> cc) {
		this.cc = cc;
	}
	
	/**
	 * Destinataires par defaut.
	 */
	public void setBcc(ArrayList<String> bcc) {
		this.bcc = bcc;
	}
	
	/**
	 * Mode de debug de JavaMail
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		if (session != null) {
			session.setDebug(debug);
		}
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	/**
	 * Signature dans l'entete des mails pour une interception de filtres mail.
	 */
	public void setMail_header_listid(String mailHeaderListid) {
		mail_header_listid = mailHeaderListid;
	}
	
	/**
	 * Importe une configuration XML.
	 * @param mailconf element XML qui contient directement la conf
	 * @return La configuration.
	 */
	public static MailConfigurator importToXMLElement(Element mailconf) {
		/*
		 * Structure de la forme
		 * <javamail>
		 * 	<option name="nom" value="valeur">
		 * </javamail>
		 * 
		 *  tel que nom == from | to | cc | bcc | server | debug
		 */
		MailConfigurator mailconfigurator = new MailConfigurator();
		
		NodeList subnodes = mailconf.getChildNodes();
		Node currentnode;
		NamedNodeMap attrs;
		Node currentattrname;
		Node currentattrvalue;
		
		for (int i = 0; i < subnodes.getLength(); i++) {
			currentnode = subnodes.item(i);
			
			if (currentnode.getNodeName().equalsIgnoreCase("option")) {
				attrs = currentnode.getAttributes();
				
				if (attrs != null) {
					currentattrname = attrs.getNamedItem("name");
					currentattrvalue = attrs.getNamedItem("value");
					if ((currentattrname != null) && (currentattrvalue != null)) {
						addValueFormXML(currentattrname.getTextContent(), currentattrvalue.getTextContent(), mailconfigurator);
					}
				}
			}
		}
		
		if (mailconfigurator.smtp_server != null) {
			Properties prop = System.getProperties();
			prop.put("mail.smtp.host", mailconfigurator.smtp_server);
			mailconfigurator.session = Session.getDefaultInstance(prop);
		}
		
		mailconfigurator.setDebug(mailconfigurator.debug);
		return mailconfigurator;
		
	}
	
	private static void addValueFormXML(String name, String value, MailConfigurator mailconfigurator) {
		
		if ((name == null) || (value == null)) {
			return;
		}
		
		String namecontent = name.trim();
		String valuecontent = value.trim();
		
		if (valuecontent.equals("")) {
			return;
		}
		
		if (namecontent.equals("from")) {
			mailconfigurator.from = valuecontent;
			return;
		}
		if (namecontent.equals("to")) {
			if (mailconfigurator.to == null) {
				mailconfigurator.to = new ArrayList<String>();
			}
			mailconfigurator.to.add(valuecontent);
			return;
		}
		if (namecontent.equals("cc")) {
			if (mailconfigurator.cc == null) {
				mailconfigurator.cc = new ArrayList<String>();
			}
			mailconfigurator.cc.add(valuecontent);
			return;
		}
		if (namecontent.equals("bcc")) {
			if (mailconfigurator.bcc == null) {
				mailconfigurator.bcc = new ArrayList<String>();
			}
			mailconfigurator.bcc.add(valuecontent);
			return;
		}
		if (namecontent.equals("server") | namecontent.equals("smtp") | namecontent.equals("smtp_server")) {
			mailconfigurator.smtp_server = valuecontent;
			return;
		}
		if (namecontent.equals("debug")) {
			if (valuecontent.equalsIgnoreCase(String.valueOf(true))) {
				mailconfigurator.debug = true;
			} else {
				mailconfigurator.debug = false;
			}
			return;
		}
		
	}
	
}
