package com.dzf.zxkj.platform.util.taxrpt.jiangsu;

/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------------------------------
 * AbstractElementDefinitionHandler.java
 * -------------------------------------
 * (C)opyright 2003, by Thomas Morgner and Contributors.
 *
 * Original Author:  Kevin Kelley <kelley@ruralnet.net> -
 *                   30718 Rd. 28, La Junta, CO, 81050  USA.                                                         //
 *
 * $Id: Base64.java,v 1.5 2004/01/01 23:59:29 mungady Exp $
 *
 * Changes
 * -------------------------
 * 23.09.2003 : Initial version
 *
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Provides encoding of raw bytes to base64-encoded characters, and decoding of
 * base64 characters to raw bytes. date: 06 August 1998 modified: 14 February
 * 2000 modified: 22 September 2000
 *
 * @author Kevin Kelley (kelley@ruralnet.net)
 * @version 1.3
 */
public class Base64 {

	/**
	 * returns an array of base64-encoded characters to represent the passed
	 * data array.
	 *
	 * @param data
	 *            the array of bytes to encode
	 * @return base64-coded character array.
	 */
	public static char[] encode(byte[] data) {
		char[] out = new char[((data.length + 2) / 3) * 4];

		//
		// 3 bytes encode to 4 chars. Output is always an even
		// multiple of 4 characters.
		//
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;

			int val = (0xFF & data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		return out;
	}

	/**
	 * Decodes a BASE-64 encoded stream to recover the original data. White
	 * space before and after will be trimmed away, but no other manipulation of
	 * the input will be performed.
	 *
	 * As of version 1.2 this method will properly handle input containing junk
	 * characters (newlines and the like) rather than throwing an error. It does
	 * this by pre-parsing the input and generating from that a count of VALID
	 * input characters.
	 **/
	public static byte[] decode(char[] data) {
		// as our input could contain non-BASE64 data (newlines,
		// whitespace of any sort, whatever) we must first adjust
		// our count of USABLE data so that...
		// (a) we don't misallocate the output array, and
		// (b) think that we miscalculated our data length
		// just because of extraneous throw-away junk

		int tempLen = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || codes[data[ix]] < 0)
				--tempLen; // ignore non-valid chars and padding
		}
		// calculate required length:
		// -- 3 bytes for every 4 valid base64 chars
		// -- plus 2 bytes if there are 3 extra base64 chars,
		// or plus 1 byte if there are 2 extra.

		int len = (tempLen / 4) * 3;
		if ((tempLen % 4) == 3)
			len += 2;
		if ((tempLen % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0; // # of excess bits stored in accum
		int accum = 0; // excess bits
		int index = 0;

		// we now go through the entire array (NOT using the 'tempLen' value)
		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : codes[data[ix]];

			if (value >= 0)// skip over non-code
			{
				accum <<= 6; // bits shift up by 6 each time thru
				shift += 6; // loop, with new bits being put in
				accum |= value; // at the bottom.
				if (shift >= 8)// whenever there are 8 or more shifted in,
				{
					shift -= 8; // write them out (from the top, leaving any
					out[index++] = // excess at the bottom for next iteration.
							(byte) ((accum >> shift) & 0xff);
				}
			}
			// we will also have skipped processing a padding null byte ('=')
			// here;
			// these are used ONLY for padding to an even length and do not
			// legally
			// occur as encoded data. for this reason we can ignore the fact
			// that
			// no index++ operation occurs in that special case: the out[] array
			// is
			// initialized to all-zero bytes to start with and that works to our
			// advantage in this combination.
		}

		// if there is STILL something wrong we just have to throw up now!
		if (index != out.length) {
			throw new Error("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
		}

		return out;
	}

	//
	// code characters for values 0..63
	//
	private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

	//
	// lookup table for converting base64 characters to value in range 0..63
	//
	private static byte[] codes = new byte[256];

	static {
		for (int i = 0; i < 256; i++)
			codes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			codes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			codes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			codes[i] = (byte) (52 + i - '0');
		codes['+'] = 62;
		codes['/'] = 63;
	}

	///////////////////////////////////////////////////
	// remainder (main method and helper functions) is
	// for testing purposes only, feel free to clip it.
	///////////////////////////////////////////////////

	public static void main(String[] args) {
		boolean decode = false;

		if (args.length == 0) {
			System.out.println("usage:  java Base64 [-d[ecode]] filename");
			System.exit(0);
		}
		for (int i = 0; i < args.length; i++) {
			if ("-decode".equalsIgnoreCase(args[i]))
				decode = true;
			else if ("-d".equalsIgnoreCase(args[i]))
				decode = true;
		}

		String filename = args[args.length - 1];
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("Error:  file '" + filename + "' doesn't exist!");
			System.exit(0);
		}

		if (decode) {
			char[] encoded = readChars(file);
			byte[] decoded = decode(encoded);
			writeBytes(file, decoded);
		} else {
			byte[] decoded = readBytes(file);
			char[] encoded = encode(decoded);
			writeChars(file, encoded);
		}
	}

	private static byte[] readBytes(File file) {
		ByteArrayOutputStream baos = null;
		InputStream fis = null;
		InputStream is = null;
		try {
			fis = new FileInputStream(file);
			is = new BufferedInputStream(fis);
			baos = new ByteArrayOutputStream();
			int count = 0;
			byte[] buf = new byte[16384];
			while ((count = is.read(buf)) != -1) {
				if (count > 0)
					baos.write(buf, 0, count);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
		
		return baos == null ? null:baos.toByteArray();
	}

	private static char[] readChars(File file) {
		CharArrayWriter caw = null;
		Reader fr = null;
		Reader in = null;
		try {
			fr = new FileReader(file);
			in = new BufferedReader(fr);
			caw = new CharArrayWriter();
			int count = 0;
			char[] buf = new char[16384];
			while ((count = in.read(buf)) != -1) {
				if (count > 0)
					caw.write(buf, 0, count);
			}
			in.close();
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (caw != null) {
				caw.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}

		return caw == null ?null:caw.toCharArray();
	}

	private static void writeBytes(File file, byte[] data) {
		OutputStream fos = null;
		OutputStream os = null;
		try {
			fos = new FileOutputStream(file);
			os = new BufferedOutputStream(fos);
			os.write(data);
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static void writeChars(File file, char[] data) {
		Writer fos = null;
		Writer os = null;

		try {
			fos = new FileWriter(file);
			os = new BufferedWriter(fos);
			os.write(data);
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	///////////////////////////////////////////////////
	// end of test code.
	///////////////////////////////////////////////////

}