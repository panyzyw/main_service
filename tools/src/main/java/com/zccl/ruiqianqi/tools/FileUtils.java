package com.zccl.ruiqianqi.tools;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SyncFailedException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

/**
 * 文件管理
 * @author zccl
 *
 */
public class FileUtils {

	private static String TAG = FileUtils.class.getSimpleName();

	/**************************************SD卡相关操作********************************************/
	/**
	 * 判断SD卡是否存在
	 * @return
	 */
    public static String existSDCard(Context context) {
    	// SD卡存在
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	        File path = Environment.getExternalStorageDirectory();
	        MyConfigure.SDEXIST = true;
			MyConfigure.SDCARD = File.separator + "sdcard" + File.separator;
			MyConfigure.SDCARD = path.getAbsolutePath() + File.separator;
	        MyConfigure.ZCCL_SDCARD = MyConfigure.SDCARD + MyConfigure.ZCCLRES;
	    }
		// SD卡不存在
		else {
	        MyConfigure.SDEXIST = false;
			MyConfigure.SDCARD = context.getFilesDir().getAbsolutePath() + File.separator;
	        MyConfigure.ZCCL_SDCARD = MyConfigure.SDCARD + MyConfigure.ZCCLRES;
		}

	    // 这个一定设置为可以，无论如何
        MyConfigure.SDEXIST = true;
        // 在C中打不开这个文件，会失败，现在好像又能打开了
	    // MyConfigure.ZCCL_SDCARD = context.getFilesDir().getPath()+File.separator+MyConfigure.ZCCLRES;
	    LogUtils.e(TAG, "SD卡资源路径: "+MyConfigure.ZCCL_SDCARD);

		/*
		context.getCacheDir().getAbsolutePath();
		context.getExternalCacheDir().getAbsolutePath();
		context.getPackageResourcePath();
		context.getFilesDir();
		context.getFileStreamPath("");
		*/

		//没有就创建这个目录
	    FileUtils.createDirs(MyConfigure.ZCCL_SDCARD);
		//改变这个目录的权限
        ShellUtils.runCommand(new String[]{"chmod", "777", MyConfigure.ZCCL_SDCARD});

        return MyConfigure.ZCCL_SDCARD;
    }

	/**
	 * 获取SD卡剩余大小
	 * @return
	 */
	public static long getSDFreeSize(){
		//取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		//获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		//空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		//返回SD卡空闲大小
		//return freeBlocks * blockSize;  //单位Byte
		//return (freeBlocks * blockSize)/1024;   //单位KB
		long size = (freeBlocks * blockSize)/1024 /1024;
		return size; //单位MB
	}

	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
	/**
	 * 各种日志缓存
	 * @param dirName 什么类型的日志
	 * @param content 日志内容
	 */
	public static void saveLog(Context context, String dirName, String content){
		try {
			String time = formatter.format(new Date());
			long timeStamp = System.currentTimeMillis();
			String fileName = time + "-" + timeStamp + ".log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File exCacheDir = context.getExternalCacheDir();

				String path = exCacheDir.getAbsolutePath() + File.separator + dirName + File.separator;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(content.getBytes());
				fos.close();
			}
		} catch (Exception e) {

		}
	}

	/***************************************各种资源的文件写入*************************************/
	/**
	 * 将数据写入文件
	 * @param fromBytes  要写入的文件内容
	 * @param toFilePath 写入的文件全路径
	 * @param append     true 追加写入，false覆盖写入
	 * @return 
	 */
	public static void writeBytesToFile(byte[] fromBytes, String toFilePath, boolean append){
		File file = new File(toFilePath);
		FileOutputStream fos = null;
		try {
			if(fromBytes != null) {
				fos = new FileOutputStream(file, append);
				//创建上级目录
				if (checkParentDirs(file)) {
					fos.write(fromBytes);
					fos.flush();
				}
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将数据写入文件
	 * @param context    全局上下文
	 * @param fromBytes  要写入的文件内容
	 * @param fileName   写入的文件名 [应用私有目录中]
	 * @return
	 */
	public static void writeBytesToFile(Context context, byte[] fromBytes, String fileName){
		try {
			if(fromBytes != null){
				FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				out.write(fromBytes);
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将流写到文件系统中
	 * @param fromStream 要写入的文件流
	 * @param toFilePath 写入的文件全路径
	 * @param append    true追加 false覆盖
	 */
	public static boolean writeStreamToFile(InputStream fromStream, String toFilePath, boolean append) {
		try {
			File file = new File(toFilePath);
			if(fromStream != null){
				//创建上级目录
				if(checkParentDirs(file)){
					OutputStream outputStream = new FileOutputStream(file, append);
					byte data[] = new byte[1024];
					int length = -1;
					while ((length = fromStream.read(data)) != -1) {
						outputStream.write(data, 0, length);
					}
					outputStream.flush();
					outputStream.close();
					fromStream.close();
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 拷贝流文件到files
	 * @param context    全局上下文
	 * @param fromStream 要写入的文件流
	 * @param fileName   写入的文件名 [应用私有目录中]
	 * @param context
	 */
	public static boolean writeStreamToFile(Context context, InputStream fromStream, String fileName){
		byte [] buf = new byte[1024];
		int byteRead = 0;
		try {
			if(fromStream != null){
				FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				while ((byteRead = fromStream.read(buf)) != -1) {
					out.write(buf, 0, byteRead);
				}
				out.flush();
				out.close();
				fromStream.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/****************************************各种文件拷贝******************************************/
	/**
	 * 拷贝文件 
	 * @param fromFile 源文件路径
	 * @param toFile   目的文件路径
	 * @param append   true追加 false覆盖
	 */
    private static void copyFile_(File fromFile, File toFile, boolean append){
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(fromFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(toFile, append));
            byte[] bytes = new byte[1024];
            int len = -1;
            while((len = inBuff.read(bytes))!= -1) {
                outBuff.write(bytes, 0, len);
            }
            outBuff.flush();
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(inBuff != null){
					inBuff.close();
				}
				if(outBuff != null){
		            outBuff.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    /**
	 * 移动文件，可以支持跨目录
	 * @param fromFile 源文件全路径
	 * @param toFile  目标文件全路径
	 */
	private static boolean moveFile(File fromFile, File toFile){
		if(fromFile.exists()){
			toFile.delete();
			return fromFile.renameTo(toFile);
		} else {
			return false;
		}
	}

	/**
	 * 拷贝文件,视情况覆盖
	 * @param fromFile 源文件全路径
	 * @param toFile   目标文件全路径
	 * @param append   true追加 false覆盖
	 */
	public static void copyFile(File fromFile, File toFile, boolean append){
		//创建上级目录
		if(isFileExist(fromFile.getAbsolutePath()) && checkParentDirs(toFile)){
			// 这个不适合大文件拷贝
			//writeBytesToFile(readFileToBytes(fromFile.getAbsolutePath()), toFile.getAbsolutePath(), append);
			// 文件流拷贝
			writeStreamToFile(getFileStream(null, fromFile.getAbsolutePath(), MyConfigure.SIX_ABSOLUTE), toFile.getAbsolutePath(), append);
			// 文件流拷贝
			copyFile_(fromFile, toFile, append);
			// 重命名实现
			//moveFile(fromFile, toFile);
			// 采用命令拷贝
			//ShellUtils.runCommand(new String[]{"/system/bin/cat", fromFile.getAbsolutePath(), ">", toFile.getAbsolutePath()});
		}
	}

	/**
	 * 拷贝资源到files
	 * @param context
	 * @param sourceId
	 * @param name
	 */
	public static void copyRawToFiles(Context context, int sourceId, String name){
		 InputStream iss = context.getResources().openRawResource(sourceId);
         ReadableByteChannel rfc = Channels.newChannel(iss);
         FileOutputStream oss = null;
		try {
			oss = context.openFileOutput(name, Context.MODE_PRIVATE);
			FileChannel ofc = oss.getChannel();
			long pos = 0;
			long size = iss.available();
			while ((pos += ofc.transferFrom(rfc, pos, size- pos)) < size);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(oss!=null){
	         	try {
					oss.flush();
					oss.getFD().sync();
					oss.close();
					iss.close();
				} catch (SyncFailedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    /**
     * 拷贝asset文件到files
     * @param context
     * @param assetName  assets/zcclres/ 目录下的文件名
     * @param fileName   "/data/data/"+getPackageName()+"/files" 目录下的文件名
     */
    public static boolean copyAssetsToFiles(Context context, String assetName, String fileName){
    	byte [] buf = new byte[1024];
    	int byteRead = 0;
    	try {
    		InputStream ins = getFileStream(context, assetName, MyConfigure.ONE_ASSETS);
    		if(ins != null){
				FileOutputStream out = context.openFileOutput(fileName,Context.MODE_PRIVATE);
	            while ((byteRead = ins.read(buf)) != -1) {
	                out.write(buf, 0, byteRead);
	            }
				out.close();
				ins.close();
				return true;
    		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    /**
     * 拷贝asset文件到files
     * @param context
     * @param assetName    assets/zcclres/ 目录下的文件名
     * @param path          文件完整路径
     */
    public static void copyAssetsToFileSystem(Context context, String assetName, String path){
    	InputStream iss = getFileStream(context, assetName, MyConfigure.ONE_ASSETS);
        ReadableByteChannel rfc = Channels.newChannel(iss);
        FileOutputStream oss = null;
		try {
			oss = new FileOutputStream(path);
			FileChannel ofc = oss.getChannel();
			long pos = 0;
			long size = iss.available();
			while ((pos += ofc.transferFrom(rfc, pos, size- pos)) < size);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(oss!=null){
	         	try {
					oss.flush();
					oss.getFD().sync();
					oss.close();
					iss.close();
				} catch (SyncFailedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }


    /***************************************读取各种资源文件流*************************************/
    /**
     * 返回asset文件流
     * @param context
     * @param fileName
     * flag
     * 0 读用户资源
     * 1 读assets
     * 2 读SD卡和内存
     * 3 读files
     * 4 读系统资源
	 * 5 读raw目录
	 * 6 读绝对路径
     * @return
     */
    public static InputStream getFileStream(Context context, String fileName, int flag){
		try {
			if(flag==MyConfigure.ZERO_MYRES){// 读我的资源（用户版）
				return FileUtils.class.getClassLoader().getResourceAsStream(MyConfigure.ZCCL_CLIENT + fileName);
				
			}else if(flag==MyConfigure.ONE_ASSETS){// 读assets
				return context.getResources().getAssets().open(MyConfigure.ZCCL_ASSETS + fileName);
				
			}else if(flag==MyConfigure.TWO_SDCARD){// 可以读SD卡和内存
				return new FileInputStream(MyConfigure.ZCCL_SDCARD + fileName);
				
			}else if(flag==MyConfigure.THREE_FILES){// 读内存(能不能级连打开)
				return context.openFileInput(fileName);
				
			}else if(flag==MyConfigure.FOUR_SYSTEMRES){// 读我的资源（系统版）
				return FileUtils.class.getClassLoader().getResourceAsStream(MyConfigure.ZCCL_SYSTEM + fileName);
				
			}else if(flag==MyConfigure.FIVE_RESRAW){ // apk的res目录
				int id = MYUIUtils.getResID(context, fileName, "raw");
				return context.getResources().openRawResource(id);
				
			}else if(flag==MyConfigure.SIX_ABSOLUTE){// 加载传入的原始资源
				return new FileInputStream(fileName);
				
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * 得到文件描述符
	 * @param context
	 * @param fileName
	 * @param flag
	 * 1 读assets
	 * 5 读raw目录
     * @return
     */
	public static AssetFileDescriptor getAssetsFileFD(Context context, String fileName, int flag){
		AssetFileDescriptor assetFileDescriptor = null;
		try {
			if(flag==MyConfigure.ONE_ASSETS){
				assetFileDescriptor = context.getResources().getAssets().openFd(fileName);
			}else if(flag==MyConfigure.FIVE_RESRAW){
				int id = MYUIUtils.getResID(context, fileName, "raw");
				assetFileDescriptor = context.getResources().openRawResourceFd(id);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return assetFileDescriptor;
	}

	/**
	 * 得到文件流
	 * @param context
	 * @param fileName
     * @return
     */
	public static InputStream getAssetsFileStream(Context context, String fileName){
		try {
			return context.getResources().getAssets().open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**********************************文件或流转成数组********************************************/
	/**
	 * 读文件，供内部使用
	 * @param filePath 读取文件全路径
	 * @return byte[]  文件内容
	 */
	public static byte[] readFileToBytes(String filePath){
		byte[] result = null;
		FileInputStream fis = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			if(fis != null){
				result = new byte[fis.available()];
				fis.read(result);
				fis.close();
			}
		} catch (Exception e) {
            return null;
		} 
		return result;
	}

    /**
     * 文件流转字节数组
     * @param is
     * @return
     */
    public static byte[] readStreamToBytes(InputStream is) {
    	byte [] buf = new byte[1024];
    	byte[] result = null;
        try {
        	if(is != null){
	            ByteArrayOutputStream bo = new ByteArrayOutputStream();
	            int count = 0;
				while ((count = is.read(buf,0,1024))!= -1){
					bo.write(buf, 0, count);
				}
		        //result = new byte[is.available()];
				//is.read(result);			
				result = bo.toByteArray();
				
				bo.close();
				is.close();
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return result;
    }

    /**
     * 文件流转字符串
     * @param is
     * @return
     */
    public static String readStreamToStr(InputStream is) {
    	byte[] buf = new byte[1024];
        try {
        	if(is != null){
        		int len = -1;
        		ByteArrayOutputStream baoS = new ByteArrayOutputStream();
        		while((len = is.read(buf)) > -1) {
					baoS.write(buf, 0, len);
        		}
        		String str = baoS.toString("UTF-8");
				baoS.close();
        		is.close();
        		return str;
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return null;
    }

	/**
	 * 读取assets目录下文件
	 * @param context
	 * @param fileName
	 * @param code
     * @return
     */
	public static String readAssetsToStr(Context context, String fileName, String code){
		byte [] buf = null;
		String result = "";
		try {
			InputStream in = context.getAssets().open(fileName);
			int len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			result = new String(buf, code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    /***************************************文件及目录相关的操作***********************************/
    /**
     * 创建上级目录
     * @param file
     * 转成File是为了避免"/"引起的麻烦,
     * file.getAbsolutePath();会转换成标准路径，不会以"/"结尾
     * @return
     */
    public static boolean checkParentDirs(File file) {
    	String path = file.getAbsolutePath();
    	int lastIndex = path.lastIndexOf(File.separator);
    	// 没找到
    	if(lastIndex==-1){
    		return false;
    	}
    	// 根目录下
    	else if(lastIndex==0){
    		return true;
    	}
    	// 开始创建
    	else{
    		String subDir = path.substring(0, lastIndex);
    		return createDirs(subDir);
    	}
    }
    
    /**
     * 创建目录，可创建多级目录
     * @param path
     * @return
     */
    public static boolean createDirs(String path) {
    	if(TextUtils.isEmpty(path)){
    		return false;
    	}else{
    		File folder = new File(path);
    		if(folder.exists() && folder.isDirectory()){
    			return true;
    		}else{
    			return folder.mkdirs();
    		}
    	}
    }
    
    /**
     * 判断文件是否存在
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return false;
        }else{
        	File file = new File(filePath);
        	return (file.exists() && file.isFile());
        }
    }
    
    /**
     * 判断文件夹是否存在
     * @param folderPath
     * @return
     */
    public static boolean isFolderExist(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return false;
        }else{
	        File dire = new File(folderPath);
	        return (dire.exists() && dire.isDirectory());
        }
    }
    

    /*****************************************删除文件********************************************/
    /**
 	 * 删除文件夹下所有文件，采用递归的方式
 	 * @param file
 	 */
 	public static boolean deleteFile(File file) {
 		if (!file.exists()) { 
            return false;
        } else { 
            if (file.isFile()) { 
                return file.delete();
			}
            else if (file.isDirectory()) { 
                File[] childFile = file.listFiles(); 
                if (childFile == null || childFile.length == 0) {
					return file.delete();
                }
                for(File f : childFile) {
                	deleteFile(f);
                } 
                return file.delete();//删除文件夹
            } else {
				return file.delete();
			}
        } 
 	}

	/**
	 * 删除该目录下的所有文件，非递归的方式
	 * @param  dir
	 * @return boolean
	 * true :   删除成功
	 * false ： 删除失败
	 */
	public static boolean deleteFile(String dir){
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			return false;
		}
		if(dirFile.isFile()){
			return dirFile.delete();
		}
		Stack<File> stack = new Stack<>();
		stack.add(dirFile);
		
		//作为List使用时, 一般采用 add/get 方法来压入/获取对象
		//作为Queue使用时,才会采用 offer/poll/take 等方法
		LinkedList<File> list = new LinkedList<File>();
		list.add(dirFile);

		//遍历文件夹，删除文件，收集文件夹
		while (!list.isEmpty()) {
			File elementDir = list.poll();
			
			File[] subFiles = elementDir.listFiles();
			if (null != subFiles) {
				for (File subFile : subFiles) {
					if (subFile.isDirectory()) {
						//将指定元素添加到此列表的末尾
						list.offer(subFile);
						//将指定元素添加到栈顶
						stack.push(subFile);
					} else {
						subFile.delete();
					}
				}
			}
		}
		//删除文件夹
		while (!stack.isEmpty()) {
			File file = stack.pop();
			file.delete();
		}
		return true;
	}

	/**************************************得到文件的相关信息**************************************/
	/**
	 * 获得全路径下的文件名
	 * @param filePath
	 * @return file name from path, include suffix
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return filePath;
		}
		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
	}

	/**
	 * 获得全路径下的文件名
	 * @param file
	 * @return
	 */
	public static String getFileName(File file) {
		String filePath = file.getAbsolutePath();
		file.getName();
		return getFileName(filePath);
	}

	/***************************************获取文件列表*******************************************/
	/**
	 * 获取文件列表（可根据文件后缀名过滤）
	 * @param sourceDir  要过滤的文件夹
	 * @param filters    {".zip",".txt","!.tmp","!sb"} !表示非
	 */
	public static List<File> getFileList(String sourceDir, String... filters) {

		if (TextUtils.isEmpty(sourceDir)) {
			return null;
		}
		File fileDir = new File(sourceDir);
		if(!fileDir.exists() || fileDir.isFile()){
			return null;
		}
		List<File> fileList = new ArrayList<>();

		//获取源文件夹当前下的文件或目录
		File[] files = fileDir.listFiles();
		for (int i = 0; files!=null && i < files.length; i++) {
			//检查此抽象路径名是否是绝对的
			if (files[i].isAbsolute()) {
				//源文件
				File sourceFile = files[i];
				//过滤
				if(filters != null && !filters.equals("") && filters.length != 0){
					boolean add = true;
					for(String filter : filters){
						if (filter.indexOf("!")!=-1) {
							if(sourceFile.getName().contains(filter.substring(filter.indexOf("!")+1, filter.length()))){
								add = false;
								break;
							}
						}else{
							if(!sourceFile.getName().contains(filter)){
								add = false;
								break;
							}
						}
					}
					if(add){
						fileList.add(sourceFile);
					}
				}else{
					fileList.add(sourceFile);
				}
			}
		}
		return fileList;
	}

	/**
	 * 将图片数据保存制定格式文件
	 * @param bitmap   需要保存的图片数据
	 * @param strPath  保存文件的绝对路径
	 * @param format   保存格式，决定了保存文件的后缀名
	 * @param quality  压缩质量，100为全部保存，80表示压缩20%
	 * @return boolean 是否保存成功
	 */
	public static String saveImage(Bitmap bitmap, String strPath, Bitmap.CompressFormat format, int quality){
		if(bitmap==null || format==null || StringUtils.isEmpty(strPath)){
			return null;
		}
		String suffix;
		// 格式化保存文件全路径名，包含后缀名
		switch (format) {
			case PNG:
				suffix = ".png";
				break;
			case JPEG:
				suffix = ".jpg";
				break;
			default:
				suffix = ".bmp";
				break;
		}
		strPath = new File(strPath).getAbsolutePath();
		if(strPath.contains(".")) {
			int index = strPath.lastIndexOf(".");
			strPath = strPath.substring(0, index) + suffix;
		}else {
			strPath = strPath + File.separator + System.currentTimeMillis() + suffix;
		}
		File file = new File(strPath);
		if(checkParentDirs(file)){
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file, false);
				bitmap.compress(format, quality, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
				return strPath;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将图片数据保存制定格式文件
	 * @param bitmap
	 * @param strPath
	 * @param format   保存格式，决定了保存文件的后缀名
     * @return
     */
	public static String saveImage(Bitmap bitmap, String strPath, Bitmap.CompressFormat format){
		return saveImage(bitmap, strPath, format, 100);
	}

	/**
	 * 将图片数据保存制定格式文件
	 * @param bitmap
	 * @param strPath
	 * @return
	 */
	public static String saveImage(Bitmap bitmap, String strPath){
		return saveImage(bitmap, strPath, Bitmap.CompressFormat.PNG, 100);
	}

	/**
	 * 解析Uri
	 * https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
	 * URI（Uniform Resource Identifier） 和URL（Uniform Resource Locator ）两者名称都不一样，所以必然有区别，
	 * 前者是统一资源标识符，后者是统一资源定位符，后者是网络上用于定位互联网上Web资源的，如HTML文档、图像、视频片段、程序等。
	 *
	 * <scheme name> : <hierarchical part> [ ? <query> ] [ # <fragment> ]
	 * 其中：
	 * <scheme name>：很明显，这是scheme的名称，对于上面五个scheme，它们scheme名分别是http, file, git, ftp, ed2k（电驴协议），实际上，它们也代表着协议名称。
	 * http://
	 * git://
	 * file:///
	 * content://
	 * <hierarchical part>：实际上，一般情况，它包含 authority 和 path。
	 * <query>：可选项目，一般使用；隔开或&隔开的键值对<key>=<value>
	 * <fragment> ：可选项目包，其它额外的标识信息
	 *
	 * xl://goods:8888/goodsDetail?goodsId=10011002
	 * 通过上面的路径 Scheme、Host、port、path、query全部包含，基本上平时使用路径就是这样子的。
	 * xl - 代表该Scheme 协议名称
	 * goods - 代表Scheme作用于哪个地址域
	 * goodsDetail - 代表Scheme指定的页面
	 * goodsId - 代表传递的参数
	 * 8888 - 代表该路径的端口号
	 *
	 * <!--要想在别的App上能成功调起App，必须添加intent过滤器-->
	 * <intent-filter>
	 * <!--协议部分，随便设置-->
	 * <data android:scheme="xl" android:host="goods" android:path="/goodsDetail" android:port="8888"/>
	 * <!--下面这几行也必须得设置-->
	 * <category android:name="android.intent.category.DEFAULT"/>
	 * <action android:name="android.intent.action.VIEW"/>
	 * <category android:name="android.intent.category.BROWSABLE"/>
	 * </intent-filter>
	 *
	 * file:///android_asset/run.gif
	 * file:///sdcard/zcclres/run.gif
	 *
	 * @param uri
     */
	public static void parseUri(Uri uri){
		uri = Uri.parse("xl://goods:8888/goodsDetail/sb?goodsId=10011002&yes=1");
		// scheme部分 xl
		String scheme = uri.getScheme();
		// host部分 goods
		String host = uri.getHost();
		// port部分 8888
		int port = uri.getPort();
		// 访问路径 /goodsDetail/sb
		String path = uri.getPath();
		// 路径分段 goodsDetail，sb
		List<String> pathSegments = uri.getPathSegments();
		// Query部分 goodsId=10011002&yes=1
		String query = uri.getQuery();
		// 获取指定参数值
		String goodsId = uri.getQueryParameter("goodsId");
		LogUtils.e(TAG, "scheme = " + scheme);
		LogUtils.e(TAG, "host = " + host);
		LogUtils.e(TAG, "port = " + port);
		LogUtils.e(TAG, "path = " + path);
		LogUtils.e(TAG, "pathSegments = " + pathSegments);
		LogUtils.e(TAG, "query = " + query);
		LogUtils.e(TAG, "goodsId = " + goodsId);

	}
}
