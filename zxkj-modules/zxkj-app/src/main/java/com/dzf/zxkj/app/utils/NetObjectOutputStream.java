package com.dzf.zxkj.app.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;



public class NetObjectOutputStream extends ObjectOutputStream {

	private NCOutputStream ncOut;

	protected ObjectOutputStream objOut;

	private boolean needCompress, needEncryp;

	private int bufferSize;

	private boolean finished;

	public NetObjectOutputStream(OutputStream out, boolean needCompress, boolean needEncryp, int bufferSize,
			ObjectReplacer replacer) throws IOException {
		super();

		this.needCompress = needCompress;
		this.needEncryp = needEncryp;
		this.bufferSize = bufferSize;

		ncOut = new NCOutputStream(out);
		objOut = new NCObjectOuputStream(ncOut, replacer);

	}

	public NetObjectOutputStream(OutputStream out, boolean needCompress, boolean needEncryp, int bufferSize)
			throws IOException {
		super();

		this.needCompress = needCompress;
		this.needEncryp = needEncryp;
		this.bufferSize = bufferSize;

		ncOut = new NCOutputStream(out);

		ObjectReplacer replacer = new NCObjectReplacer();
		//		try {
		//			replacer = (ObjectReplacer) Class.forName("nc.bs.framework.comn.NCObjectReplacer").newInstance();
		//		} catch (Throwable e) {
		//		}

		objOut = new NCObjectOuputStream(ncOut, replacer);
	}

	public NetObjectOutputStream(OutputStream out, boolean needCompress) throws IOException {
		this(out, needCompress, NetStreamConstants.STREAM_NEED_ENCRYPTED, NetStreamConstants.NC_STREAM_BUFFER_SIZE);
	}

	public NetObjectOutputStream(OutputStream out, boolean needCompress, boolean needEncryp) throws IOException {
		this(out, needCompress, needEncryp, NetStreamConstants.NC_STREAM_BUFFER_SIZE);
	}

	public NetObjectOutputStream(OutputStream out) throws IOException {
		this(out, NetStreamConstants.STREAM_NEED_COMPRESS, NetStreamConstants.STREAM_NEED_ENCRYPTED,
				NetStreamConstants.NC_STREAM_BUFFER_SIZE);
	}

	private static class NCObjectOuputStream extends ObjectOutputStream {

		ObjectReplacer replacer;

		protected NCObjectOuputStream() throws IOException, SecurityException {
			super();
		}

		public NCObjectOuputStream(OutputStream out) throws IOException {
			super(out);
			// TODO Auto-generated constructor stub
		}

		public NCObjectOuputStream(OutputStream out, ObjectReplacer replacer) throws IOException {
			super(out);
			this.replacer = replacer;
			if (this.replacer != null) {
				enableReplaceObject(true);
			}
		}

		protected Object replaceObject(Object obj) throws IOException {
			if (replacer == null) {
				return obj;
			} else {
				return replacer.replaceObject(obj);
			}
		}

	}

	public void close() throws IOException {
		try {
			finish();
		} catch (Exception exp) {
		}
		if (objOut != null)
			objOut.close();
		objOut = null;
		ncOut = null;
	}

	/**
	 * must call if compuress
	 * 
	 * @throws IOException
	 */
	public void finish() throws IOException {
		if (!finished) {
			objOut.flush();
			ncOut.finish();
			finished = true;
		}
	}

	public void defaultWriteObject() throws IOException {
		objOut.defaultWriteObject();
	}

	public void flush() throws IOException {
		objOut.flush();
	}

	public PutField putFields() throws IOException {
		return objOut.putFields();
	}

	public void reset() throws IOException {
		objOut.reset();
	}

	public void useProtocolVersion(int version) throws IOException {
		objOut.useProtocolVersion(version);
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		objOut.write(buf, off, len);
	}

	public void write(byte[] buf) throws IOException {
		objOut.write(buf);
	}

	public void write(int val) throws IOException {
		objOut.write(val);
	}

	public void writeBoolean(boolean val) throws IOException {
		objOut.writeBoolean(val);
	}

	public void writeByte(int val) throws IOException {
		objOut.writeByte(val);
	}

	public void writeBytes(String str) throws IOException {
		objOut.writeBytes(str);
	}

	public void writeChar(int val) throws IOException {
		objOut.writeChar(val);
	}

	public void writeChars(String str) throws IOException {
		objOut.writeChars(str);
	}

	public void writeDouble(double val) throws IOException {
		objOut.writeDouble(val);
	}

	public void writeFields() throws IOException {
		objOut.writeFields();
	}

	public void writeFloat(float val) throws IOException {
		objOut.writeFloat(val);
	}

	public void writeInt(int val) throws IOException {
		objOut.writeInt(val);
	}

	public void writeLong(long val) throws IOException {
		objOut.writeLong(val);
	}

	public void writeShort(int val) throws IOException {
		objOut.writeShort(val);
	}

	public void writeUnshared(Object obj) throws IOException {
		objOut.writeUnshared(obj);
	}

	public void writeUTF(String str) throws IOException {
		objOut.writeUTF(str);
	}

	protected void writeObjectOverride(Object obj) throws IOException {
		objOut.writeObject(obj);
	}

	private class NCOutputStream extends OutputStream {
		private OutputStream out;

		private int header;

		private DeflaterOutputStream gzipOut;

		FastDESOutputStream desOut;

		public NCOutputStream(OutputStream output) throws IOException {

			out = new BufferedOutputStream(output, 1024 * 100);
			if (NetStreamConstants.STREAM_NEED_STATISTIC)
				out = new CountOutputStream(out);

			if (needEncryp) {
				header |= 0x1;
			}

			if (needCompress) {
				header |= 0x2;
			}

			out.write(NetStreamConstants.NC_STREAM_HEADER);
			out.write(header);

			if (needEncryp) {
				out = desOut = new FastDESOutputStream(out, NetStreamConstants.des);
			}

			if (needCompress) {
				// 512byte
				out = gzipOut = new FastDeflaterOutputStream(out, bufferSize);
				// FIX: size by compression rate
				out = new BufferedOutputStream(out, bufferSize);
			}

		}

		public void write(int b) throws IOException {
			out.write(b);
		}

		public void write(byte[] b, int offset, int len) throws IOException {
			out.write(b, offset, len);
		}

		public void flush() throws IOException {
			out.flush();
		}

		public void finish() throws IOException {
			flush();

			if (gzipOut != null) {
				gzipOut.finish();
				gzipOut.flush();
			}

			if (desOut != null) {
				desOut.finish();
				desOut.flush();
			}
		}

		public void close() throws IOException {
			try {
				finish();
			} catch (IOException ioe) {

			}
			out.close();
			out = null;
			gzipOut = null;
		}

	}

	public static void writeObject(OutputStream output, Object obj) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		NetObjectOutputStream objOut = new NetObjectOutputStream(bout);
		objOut.writeObject(obj);
		objOut.finish();
		objOut.flush();
		// byte[] bytes = bout.toByteArray();

		writeInt(output, bout.size());
		bout.writeTo(output);
		// output.write(bytes);
		output.flush();
	}

	public static ByteArrayOutputStream convertObjectToBytes(Object obj, boolean compressed, boolean encrypted)
			throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		NetObjectOutputStream objOut = new NetObjectOutputStream(bout, compressed, encrypted);
		objOut.writeObject(obj);
		objOut.finish();
		objOut.flush();
		return bout;
	}

	public static void writeInt(OutputStream output, int v) throws IOException {
		byte bytes[] = new byte[4];

		bytes[0] = (byte) ((v >>> 24) & 0xFF);
		bytes[1] = (byte) ((v >>> 16) & 0xFF);
		bytes[2] = (byte) ((v >>> 8) & 0xFF);
		bytes[3] = (byte) ((v >>> 0) & 0xFF);

		output.write(bytes);
	}

}
