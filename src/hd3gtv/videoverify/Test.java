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

import hd3gtv.ffprobe.FFprobe;
import hd3gtv.javasimpleservice.ServiceManager;
import hd3gtv.mimeutils.MimeutilsWrapper;

import java.io.File;

/**
 * Use it for test the behavior of ffprobe and VideoVerify
 * @author hdsdi3g
 * @version 1.0
 */
@SuppressWarnings("nls")
public class Test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		/**
		 * For MacOSX / MacPort ffmpeg package
		 */
		ServiceManager.injectDefaultSystemProperty("executable.ffprobe", "/opt/local/bin/ffprobe");
		
		/**
		 * Get the file from command line, or add your file name here
		 */
		String filename = args[0];
		
		System.out.print(filename);
		System.out.print(" -> ");
		System.out.println(MimeutilsWrapper.getMime(new File(filename)));
		
		FFprobe ffprobe = new FFprobe(new File(filename));
		ffprobe.analyst();
		
		System.out.println(ffprobe.getContainer().toString());
		if (ffprobe.getContainer().getTags() != null) {
			for (int post = 0; post < ffprobe.getContainer().getTags().size(); post++) {
				System.out.print(" * ");
				System.out.println(ffprobe.getContainer().getTags().get(post));
			}
		}
		
		/**
		 * Don't forget: you can have many video streams in 1 file, especially with MPEG2 TS and QuickTime
		 */
		for (int pos = 0; pos < ffprobe.getStreams().size(); pos++) {
			System.out.println(ffprobe.getStreams().get(pos).toString());
			if (ffprobe.getStreams().get(pos).getTags() != null) {
				for (int post = 0; post < ffprobe.getStreams().get(pos).getTags().size(); post++) {
					System.out.print(" * ");
					System.out.println(ffprobe.getStreams().get(pos).getTags().get(post));
				}
			}
			
		}
		
	}
}
