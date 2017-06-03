package com.zccl.ruiqianqi.tools.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * 资源先压缩再打包
 *
 * @author zccl 格式设计
 *
 * 版本 文件数量
 * 文件名长度 文件名 起始 长度 原始长度 MD5
 * 文件名长度 文件名 起始 长度 原始长度 MD5
 * 文件名长度 文件名 起始 长度 原始长度 MD5 。。。。。。
 * 文件数据（压缩过的） 文件数据（压缩过的）。。。。。。
 **/
public class ZipUtils {

	/**
	 * Compresses a file with zlib compression.
	 *
	 * @param raw
	 * @param compressed
	 */
	public static void compressFile(File raw, File compressed) {
		try {
			InputStream in = new FileInputStream(raw);
			FileOutputStream fileout = new FileOutputStream(compressed);
			OutputStream out = new DeflaterOutputStream(fileout);

			shovelInToOut(in, out);

			fileout.close();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Decompresses a zlib compressed file.
	 *
	 * @param compressed
	 * @param raw
	 */
	public static void decompressFile(File compressed, File raw) {
		try {
			FileInputStream filein = new FileInputStream(compressed);
			InputStream in = new InflaterInputStream(filein);
			OutputStream out = new FileOutputStream(raw);
			shovelInToOut(in, out);
			filein.close();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shovels all data from an input stream to an output stream.
	 *
	 * @param in
	 * @param out
	 */
	private static void shovelInToOut(InputStream in, OutputStream out) {
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compresses a file with zlib compression.
	 *
	 * @param str
	 */
	public static byte[] compressString(String str) {
		byte[] reby = null;
		try {

			ByteArrayInputStream byin = new ByteArrayInputStream(str.getBytes());
			ByteArrayOutputStream byou = new ByteArrayOutputStream();
			OutputStream out = new DeflaterOutputStream(byou);

			shovelInToOut(byin, out);
			reby = byou.toByteArray();
			byin.close();
			out.close();
			byou.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reby;
	}
}
