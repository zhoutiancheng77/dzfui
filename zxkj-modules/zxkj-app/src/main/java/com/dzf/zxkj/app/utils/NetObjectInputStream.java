package com.dzf.zxkj.app.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;



public class NetObjectInputStream extends ObjectInputStream {

	private int bufferSize;

	private boolean compressed;

	private boolean encrypted;

	private ObjectInputStream objIn;

	public NetObjectInputStream(InputStream in) throws IOException {
		this(in, NetStreamConstants.NC_STREAM_BUFFER_SIZE);
	}

	public NetObjectInputStream(InputStream in, int bufferSize) throws IOException {
		super();
		this.bufferSize = bufferSize;
		ObjectResolver resolver = new NCObjectResolver();
		//		try {
		//			resolver = (ObjectResolver) Class.forName("nc.bs.framework.comn.NCObjectResolver").newInstance();
		//		} catch (Throwable e) {
		//		}
		objIn = new NCObjectInputStream(new NCInputStream(in), resolver);

	}

	public NetObjectInputStream(InputStream in, int bufferSize, ObjectResolver resolver) throws IOException {
		super();
		this.bufferSize = bufferSize;
		objIn = new NCObjectInputStream(new NCInputStream(in), resolver);

	}

	private static class NCObjectInputStream extends ObjectInputStream {
		private ObjectResolver resolver;

		public NCObjectInputStream(InputStream in, ObjectResolver resolver) throws IOException {
			super(in);
			this.resolver = resolver;
			if (this.resolver != null) {
				enableResolveObject(true);
			}

		}

		public NCObjectInputStream(InputStream in) throws IOException {
			this(in, null);
		}

		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			try {
				return super.resolveClass(desc);
			} catch (ClassNotFoundException nfe) {
				String name = desc.getName();
				ClassLoader loader = (ClassLoader) System.getProperties().get("nc.classLoader");
				if (loader != null) {
					return Class.forName(name, false, (ClassLoader) System.getProperties().get("nc.classLoader"));
				} else
					throw new ClassNotFoundException("class is not found: " + name + " nc classloader is null");
			}
		}

		protected Object resolveObject(Object obj) throws IOException {
			if (resolver == null) {
				return obj;
			} else {
				return resolver.resolveObject(obj);
			}
		}

	}

	private class NCInputStream extends InputStream {

		private InputStream input;

		public NCInputStream(InputStream inputStream) throws IOException {

			input = new BufferedInputStream(inputStream, bufferSize * 4);

			if (NetStreamConstants.STREAM_NEED_STATISTIC) {
				input = new CountInputStream(input);
			}
			byte[] bs=new byte[3];
			input.read(bs);
			//int magicCode = input.read() | input.read() << 8 | input.read() << 16;

			if (bs[0] != NetStreamConstants.NC_STREAM_HEADER[0]
				||bs[1] != NetStreamConstants.NC_STREAM_HEADER[1]
					||bs[2] != NetStreamConstants.NC_STREAM_HEADER[2]
					) {
				throw new IOException("IOException error");
			}

			int header = input.read();

			encrypted = (header & 0x1) != 0;

			compressed = (header & 0x2) != 0;

			if (encrypted) {
				input = new FastDESInputStream(input, NetStreamConstants.des);
			}

			if (compressed) {
				input = new InflaterInputStream(input, new Inflater(), bufferSize);
				input = new BufferedInputStream(input, bufferSize);
			}

		}

		public int read() throws IOException {
			return input.read();
		}

		public int read(byte[] b, int offset, int len) throws IOException {
			return input.read(b, offset, len);
		}

		public void close() throws IOException {
			if (input != null)
				input.close();
			input = null;
		}

	}

	public int getBufferSize() {
		return bufferSize;
	}

	public boolean isCompressed() {
		return compressed;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public int available() throws IOException {
		return objIn.available();
	}

	public void close() throws IOException {
		objIn.close();
	}

	public void defaultReadObject() throws IOException, ClassNotFoundException {
		objIn.defaultReadObject();
	}

	public void mark(int readlimit) {
		objIn.mark(readlimit);
	}

	public boolean markSupported() {
		return objIn.markSupported();
	}

	public int read() throws IOException {
		return objIn.read();
	}

	public int read(byte[] buf, int off, int len) throws IOException {
		//return objIn.read(buf, off, len);
		readFully(buf, off, len);
		return len;
	}

	public int read(byte[] b) throws IOException {
		//return objIn.read(b);
		int n=0;
		int readLen=0;
		int len=b.length;
		while ((n = objIn.read(b, readLen, len - readLen)) > 0)
		{ readLen += n; 
		if (readLen >= len) 
			break; }
		return len;
	}

	public boolean readBoolean() throws IOException {
		return objIn.readBoolean();
	}

	public byte readByte() throws IOException {
		return objIn.readByte();
	}

	public char readChar() throws IOException {
		return objIn.readChar();
	}

	public double readDouble() throws IOException {
		return objIn.readDouble();
	}

	public GetField readFields() throws IOException, ClassNotFoundException {
		return objIn.readFields();
	}

	public float readFloat() throws IOException {
		return objIn.readFloat();
	}

	public void readFully(byte[] buf, int off, int len) throws IOException {
		objIn.readFully(buf, off, len);
	}

	public void readFully(byte[] buf) throws IOException {
		objIn.readFully(buf);
	}

	public int readInt() throws IOException {
		return objIn.readInt();
	}

	@Deprecated
	public String readLine() throws IOException {
		return objIn.readLine();
	}

	public long readLong() throws IOException {
		return objIn.readLong();
	}

	public Object readObjectOverride() throws ClassNotFoundException, IOException {
		return objIn.readObject();

	}

	public short readShort() throws IOException {
		return objIn.readShort();
	}

	public Object readUnshared() throws IOException, ClassNotFoundException {
		return objIn.readUnshared();
	}

	public int readUnsignedByte() throws IOException {
		return objIn.readUnsignedByte();
	}

	public int readUnsignedShort() throws IOException {
		return objIn.readUnsignedShort();
	}

	public String readUTF() throws IOException {
		return objIn.readUTF();
	}

	public void registerValidation(ObjectInputValidation obj, int prio) throws NotActiveException,
			InvalidObjectException {
		objIn.registerValidation(obj, prio);
	}

	public long skip(long n) throws IOException {
		return objIn.skip(n);
	}

	public int skipBytes(int len) throws IOException {
		return objIn.skipBytes(len);
	}

	public void reset() throws IOException {
		objIn.reset();
	}

	public static Object readObject(InputStream in, boolean[] retValue) throws IOException, ClassNotFoundException {
		BufferedInputStream bin = new BufferedInputStream(in);
		int len = readInt(bin);

		byte[] bytes = new byte[len];

		int readLen = bin.read(bytes);

		while (readLen < len) {
			int tmpLen = bin.read(bytes, readLen, len - readLen);
			if (tmpLen < 0)
				break;
			readLen += tmpLen;
		}

		if (readLen < len) {
			throw new EOFException("ReadObject EOF error readLen: " + readLen + " expected: " + len);
		}

		NetObjectInputStream objIn = new NetObjectInputStream(new ByteArrayInputStream(bytes));

		if (retValue != null) {
			retValue[0] = objIn.isCompressed();
			retValue[1] = objIn.isEncrypted();
		}

		return objIn.readObject();

	}

	public static int readInt(InputStream in) throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}
}
