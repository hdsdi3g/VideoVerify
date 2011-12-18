/*
 * This file is part of Java Tools for hdsdi3g'.
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

package hd3gtv.tools;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressWarnings("nls")
/**
 * @author hdsdi3g
 * @version 1.0
 */
public class TimeUtils {
	
	/**
	 * Convertie quelque chose comme 123:45:12 donne 445512
	 * @param hmsvalue La valeur temporelle telle que H:MM:SS avec autant de H que l'on veut.
	 *        On peut utiliser ce que l'on veut comme separateur.
	 * @return la valeur convertie en secondes
	 */
	public static int getSecondsFromHMS(String hmsvalue) {
		// 00000:00:00
		if (hmsvalue.length() < 7) {
			return -1;
		}
		
		try {
			int secs = Integer.valueOf(hmsvalue.substring(hmsvalue.length() - 2, hmsvalue.length()));
			int mins = Integer.valueOf(hmsvalue.substring(hmsvalue.length() - 5, hmsvalue.length() - 3));
			int hours = Integer.valueOf(hmsvalue.substring(0, hmsvalue.length() - 6));
			
			return (hours * 3600) + (mins * 60) + secs;
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * Calcul entre maintenant et la date future la difference de temps en secondes.
	 * @param dest_hour heure de destination, entre 0 et 23. Gere le passage au jour suivant.
	 * @param dest_min la minute de destination.
	 * @return la difference secondes, avec la precision d'une minute
	 */
	public static long timeDiff(int dest_hour, int dest_min) {
		Calendar calnow = new GregorianCalendar();
		
		int nowY = calnow.get(GregorianCalendar.YEAR);
		int nowM = calnow.get(GregorianCalendar.MONTH);
		int nowD = calnow.get(GregorianCalendar.DAY_OF_MONTH);
		int nowHr = calnow.get(GregorianCalendar.HOUR_OF_DAY);
		
		Calendar caldest = new GregorianCalendar();
		
		if (dest_hour < nowHr) {
			nowD++;
		}
		
		caldest.set(nowY, nowM, nowD, dest_hour, dest_min);
		
		return (caldest.getTimeInMillis() - calnow.getTimeInMillis()) / 1000; // en secondes
	}
	
	/**
	 * @param sec les secondes a calculer.
	 * @return le nombre d'heures que contient sec.
	 * @see getMinutesInSecWOHours.
	 */
	public static int getHoursInSec(long sec) {
		return (int) Math.floor((float) sec / 3600f); // en heures
	}
	
	/**
	 * @param sec les secondes a calculer.
	 * @return le nombre de minutes que contient sec sans les heures.
	 * @see getHoursInSec.
	 */
	public static float getMinutesInSecWOHours(long sec) {
		float _diff_hours = (float) sec / 3600f; // en heures,minutes
		int diff_hours = (int) Math.floor(_diff_hours); // en heures
		return ((float) _diff_hours - (float) diff_hours) * 60f;
	}
	
	/**
	 * Converti une valeur en secondes sous la forme HH:MM
	 */
	public static String secondstoHM(long sec) {
		StringBuffer sb = new StringBuffer();
		int hrs = getHoursInSec(sec);
		if (hrs < 10) {
			sb.append(0);
		}
		sb.append(hrs);
		sb.append(":");
		int min = Math.round(getMinutesInSecWOHours(sec));
		
		if (min < 10) {
			sb.append(0);
		}
		
		sb.append(min);
		return sb.toString();
	}
	
	/**
	 * Converti une valeur en secondes sous la forme HH:MM:SS
	 */
	public static String secondstoHMS(long sec) {
		StringBuffer sb = new StringBuffer();
		int hrs = getHoursInSec(sec);
		if (hrs < 10) {
			sb.append(0);
		}
		sb.append(hrs);
		sb.append(":");
		float min = getMinutesInSecWOHours(sec);
		
		if (min < 10) {
			sb.append(0);
		}
		sb.append((int) Math.floor(min));
		
		sb.append(":");
		// int secresult = (int) (Math.round((float)min-Math.floor(min))*60);
		
		int secresult = (int) Math.round((min - Math.floor(min)) * (float) 60);
		
		if (secresult < 10) {
			sb.append(0);
		}
		
		sb.append(secresult);
		// sb.append();
		
		// sb.append(secresult);
		
		return sb.toString();
	}
	
	/**
	 * Converti un nombre de secondes en duree (annees/semaines/jours/heures/min/sec).
	 * @return un resultat tel que "39 ans 29 sem 6j 12:32:11" avec an et semaine facultatifs
	 */
	public static String secondsToYWDHMS(long seconds) {
		if (seconds < 0) {
			return "-1";
		}
		
		StringBuffer sb = new StringBuffer();
		double _seconds = (double) seconds;
		
		double oneyear = 31536000d;
		/** 365 x 24 x 60 x 60 ) en secondes */
		double _years = _seconds / oneyear;
		double years = Math.floor(_years);
		if (years > 0) {
			sb.append((int) years);
			if (years > 1) {
				sb.append(" ans ");
			} else {
				sb.append(" an ");
			}
		}
		
		double oneweek = 604800d;
		/** 7 x 24 x 60 x 60 en secondes */
		double _weeks = (_years - years) * oneyear / oneweek;
		double weeks = Math.floor(_weeks);
		if (weeks > 0) {
			sb.append((int) weeks);
			sb.append(" sem ");
		}
		
		double oneday = 86400d;
		/** 24 x 60 x 60 en secondes */
		double _days = (_weeks - weeks) * oneweek / oneday;
		double days = Math.floor(_days);
		if (days > 0) {
			sb.append((int) days);
			sb.append(" jrs ");
		}
		
		double rest = (_days - days) * oneday;
		/** secondes restantes */
		sb.append(secondstoHMS((long) rest));
		
		return sb.toString();
	}
	
}
