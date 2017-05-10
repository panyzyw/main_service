package com.zccl.ruiqianqi.presentation.mictest;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PcmFileUtil {
	private String WRITE_PCM_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() 
											+ "/PCM/";
	
	private final static String PCM_SURFFIX = ".pcm";
	
	private FileOutputStream mFos;
	
	private FileInputStream mFis;
	
	public PcmFileUtil() {
		
	}
	
	public PcmFileUtil(String writeDir) {
		WRITE_PCM_DIR = writeDir;
	}
	
	public boolean openPcmFile(String filePath) {
		File file = new File(filePath);
		try {
			mFis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mFis = null;
			return false;
		}
		
		return true;
	}
	
	public int read(byte[] buffer) {
		if (null != mFis) {
			try {
				return mFis.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				closeReadFile();
				return 0;
			}
		}
		
		return -1;
	}
	
	public void closeReadFile() {
		if (null != mFis) {
			try {
				mFis.close();
				mFis = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createPcmFile() {
		File dir = new File(WRITE_PCM_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (null != mFos) {
			return;
		}
		
		DateFormat df = new SimpleDateFormat("MM-dd-hh-mm-ss", Locale.CHINA);
		String filename = df.format(new Date());
		String pcmPath = WRITE_PCM_DIR + filename + PCM_SURFFIX;
		
		File pcm = new File(pcmPath);
		try {
			if(pcm.createNewFile()) {
				mFos = new FileOutputStream(pcm);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createPcmFile(String fileName) {
		File dir = new File(WRITE_PCM_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (null != mFos) {
			return;
		}
		String pcmPath = WRITE_PCM_DIR + fileName + PCM_SURFFIX;
		File pcm = new File(pcmPath);
		if(pcm.exists()){
			pcm.delete();
		}
		try {
			if(pcm.createNewFile()) {
				mFos = new FileOutputStream(pcm);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static long start;
	static long stop;
	static int count=1;
	public void write(byte[] data) {
		synchronized (PcmFileUtil.this) {
			if (null != mFos) {
				try {
					if(count==1){
						start=System.currentTimeMillis();
						count=2;
					}
					mFos.write(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void write(byte[] data, int offset, int len) {
		synchronized (PcmFileUtil.this) {
			if (null != mFos) {
				try {
					mFos.write(data, offset, len);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void closeWriteFile() {
		synchronized (PcmFileUtil.this) {
			if (null != mFos) {
				try {
					count=1;
					stop=System.currentTimeMillis();
					Log.d("jlog","savePcm time:"+(stop-start));
					mFos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mFos = null;
			}
		}
	}
	
}
