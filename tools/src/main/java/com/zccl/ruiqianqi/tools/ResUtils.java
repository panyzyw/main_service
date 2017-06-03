package com.zccl.ruiqianqi.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ResUtils {

	/** 类的标志 */
	private static String TAG = ResUtils.class.getSimpleName();
	/**传说中的图片缓存机制*/
	private static LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 初始化图片缓存
	 */
	static{
		// 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置图片缓存大小为程序最大可用内存的1/8
        int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            
			@SuppressLint("NewApi")
			@Override
            protected int sizeOf(String key, Bitmap bitmap) {
            	
            	// The cache size will be measured in bytes rather than number of items. 
            	// sizeOf返回为单个hashmap value的大小 
            	if (bitmap != null){
            		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {//----4.0及其之上
            			return bitmap.getByteCount();
            			
        			}else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//----3.1及其之上
        				return bitmap.getByteCount();
        				
        			}else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//--------3.0及其之上
        				return bitmap.getRowBytes() * bitmap.getHeight(); 
        				
        			}else{//------------------------------------------------------------------2.3及其之下
        				return bitmap.getRowBytes() * bitmap.getHeight(); 
        			}
                    
            	}else{  
                    return 0;
            	}
            }
        };
	}

	/************************************* 图片ID转其他 *******************************************/
	/**
	 * 资源ID转Drawable
	 * @param context
	 * @param id
	 * @return
	 */
	public static Drawable ID2Drawable(Context context, int id) {
		 Drawable drawable = context.getResources().getDrawable(id);
		 return drawable;
	}

	/**
	 * 资源ID转BitmapDrawable
	 * @param context
	 * @param id
	 * @return
	 */
	public static BitmapDrawable ID2BitmapDrawable(Context context,int id) {
		 BitmapDrawable drawable = (BitmapDrawable) ID2Drawable(context,id);
		 //drawable = new BitmapDrawable(context.getResources(), IDToBitmap(context,id));
		 return drawable;
	}

	/**
	 * 资源ID转Bitmap
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap ID2Bitmap(Context context,int id) {
		 Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
		 return bitmap;
	}
	
	/**
	 * 数组资源转换
	 * @param context
	 * @param id
	 * @return
	 */
	@SuppressLint("Recycle")
	public static TypedArray getIDArrays(Context context, int id){
		return context.getResources().obtainTypedArray(id);
	} 
	
	/*********************************** InputStream转其他 ****************************************/
	/**
	 * InputStream 转换成 byte[]
	 * @param is
	 * @return
	 */
	public static byte[] InputStream2Bytes(InputStream is){
		return FileUtils.readStreamToBytes(is);
	}
	
	/**
	 * InputStream 转换成 bitmap
	 * @param is
	 * @return
	 */
	public static Bitmap InputStream2Bitmap(InputStream is){
		if(is!=null){
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		}
		return null;
	}
    
    /**
     * InputStream转换成Drawable  
     * @param context
     * @param is
     * @return
     */
    public static Drawable InputStream2Drawable(Context context, InputStream is){  
        if(context != null){
        	Bitmap bitmap = InputStream2Bitmap(is);
            return Bitmap2Drawable(context, bitmap);
        }else{
        	return Drawable.createFromStream(is, "picture");
        }
    }  
    
    /*********************************** byte[]转其他 *********************************************/
	/**
	 * 将byte[]转换成InputStream  
	 * @param bytes
	 * @return
	 */
    public static InputStream Bytes2InputStream(byte[] bytes) {  
    	if(bytes!=null){
	        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);  
	        return bais;  
    	}
    	return null;
    }  
  
    /**
     * byte[]转换成Bitmap  
     * @param bytes
     * @return
     */
    public static Bitmap Bytes2Bitmap(byte[] bytes) {  
        if (bytes!=null && bytes.length!=0) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opts.inSampleSize = 1;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }  
        return null;  
    }  
    
    /**
     * byte[]转换成Drawable  
     * @param context
     * @param bytes
     * @return
     */
    public static Drawable Bytes2Drawable(Context context,byte[] bytes) {  
        Bitmap bitmap = Bytes2Bitmap(bytes);  
        return Bitmap2Drawable(context,bitmap);  
    }  

    
    /*********************************** Bitmap转其他 *********************************************/
	/**
	 * Bitmap转换成byte[]
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm, int quality, Bitmap.CompressFormat compressFormat) {
		ByteArrayOutputStream baoS = new ByteArrayOutputStream();
		bm.compress(compressFormat, quality, baoS);
		try {
			baoS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baoS.toByteArray();
	}

    /**
     * Bitmap转换成byte[]  
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, int quality) {
        ByteArrayOutputStream baoS = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baoS);
		try {
			baoS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return baoS.toByteArray();
    } 
    
    /**
     * 将Bitmap转换成InputStream  
     * @param bm
     * @return
     */
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality, Bitmap.CompressFormat compressFormat) {
        ByteArrayOutputStream baoS = new ByteArrayOutputStream();
        bm.compress(compressFormat, quality, baoS);
        InputStream is = new ByteArrayInputStream(baoS.toByteArray());
		try {
			baoS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return is;  
    }  
    
    /**
     * 将Bitmap转换成InputStream  
     * @param bm
     * @param quality   保存原来的百分比
     * @return
     */
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {  
        ByteArrayOutputStream baoS = new ByteArrayOutputStream();
        
        //30是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0  
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baoS中
        //第二个参数影响的是图片的质量，但是图片的宽度与高度是不会受影响滴
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baoS);
        
        InputStream is = new ByteArrayInputStream(baoS.toByteArray());
		try {
			baoS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return is;  
    }  
    
    /**
     * Bitmap转换成Drawable  
     * @param context
     * @param bitmap
     * @return
     */
    public static Drawable Bitmap2Drawable(Context context, Bitmap bitmap) {  
    	if(bitmap!=null){
	        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);  
	        return drawable; 
    	}
    	return null;
    } 
    
    
  
    /*********************************** Drawable转其他 *******************************************/
    /**
     * Drawable转换成InputStream  
     * @param drawable
     * @return
     */
    public static InputStream Drawable2InputStream(Drawable drawable) {  
        Bitmap bitmap = Drawable2Bitmap(drawable);
        return Bitmap2InputStream(bitmap, 100);
    }
    
    /**
     * Drawable转换成byte[]  
     * @param drawable
     * @return
     */
    public static byte[] Drawable2Bytes(Drawable drawable) {  
        Bitmap bitmap = Drawable2Bitmap(drawable);
        return Bitmap2Bytes(bitmap, 100);
    }  
  
    /**
     * Drawable就是一个可画的对象，
     * 其可能是一张位图（BitmapDrawable），
     * 也可能是一个图形（ShapeDrawable），
     * 还有可能是一个图层（LayerDrawable）
     * 
     * Drawable转换成Bitmap  
     * @param drawable
     * @return
     */
	public static Bitmap Drawable2Bitmap(Drawable drawable) {
		if(drawable instanceof BitmapDrawable){
			BitmapDrawable bd = (BitmapDrawable) drawable;
			Bitmap bitmap = bd.getBitmap();
			return bitmap;
		}else{
			return DrawableToBitmap(drawable);
		}
	}
	
    /**
     * Drawable转换成Bitmap    
     * @param drawable
     * @return
     */
    private static Bitmap DrawableToBitmap(Drawable drawable) {  
        Bitmap bitmap = Bitmap.createBitmap(  
                        drawable.getIntrinsicWidth(),  
                        drawable.getIntrinsicHeight(),  
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);  
        
        //这个bitmap是Canvas的载体
        Canvas canvas = new Canvas(bitmap);  
        
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());  
        //这句是说，传个画布进去，将drawable画在canvas上，就相当于 canvas.drawRect(r, paint);
        drawable.draw(canvas);  
        return bitmap;  
    }  
  

    /************************************ 得到图片的缩略图 ****************************************/
    /**
     * 返回缩略图
     * @param source     原图对象
     * @param reqWidth   要求的宽
     * @param reqHeight  要求的高
     * @param recycle    原图需不需要在方法内部回收
     * @return
     */
    public static Bitmap getSmallBitmap(Bitmap source, int reqWidth, int reqHeight, boolean recycle){
    	if(recycle){
    		return ThumbnailUtils.extractThumbnail(source, reqWidth, reqHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    	}else{
    		return ThumbnailUtils.extractThumbnail(source, reqWidth, reqHeight);
    	}
    }
    
    
	/**
	 * 返回缩略图
	 * @param context
	 * @param resId	   要测量的图片的ID
	 * @param reqWidth   要求的宽
	 * @param reqHeight  要求的高
	 * @return
	 */
	public static Bitmap getSmallBitmap(Context context, int resId, int reqWidth, int reqHeight) {
	    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
	    BitmapFactory.Options options = new BitmapFactory.Options();
	   
	    // 仅仅测量宽高，不加载图片数据
	    options.inJustDecodeBounds = true;
	    
	    // 开始测量
	    BitmapFactory.decodeResource(context.getResources(), resId, options);
	    
	    // 调用上面定义的方法计算【缩放比例】
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    
	    // 使用获取到的inSampleSize值再次解析图片，加载图片数据
	    options.inJustDecodeBounds = false;
	    
	    //生成图片
	    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
	    
	    return bitmap;
	}
    
	/**
	 * 计算缩放比例
	 * @param options    携带原图信息的 BitmapFactory.Options
	 * @param reqWidth   要求的宽
	 * @param reqHeight  要求的高
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    
		//源图片的宽度
		int width = options.outWidth;
		//源图片的高度
	    int height = options.outHeight;
	    
	    int inSampleSize = 1;
	    
	    if (height > reqHeight || width > reqWidth) {
	        // 计算出实际宽高和目标宽高的比率
	        int heightRatio = Math.round((float) height / (float) reqHeight);
	        
	        int widthRatio = Math.round((float) width / (float) reqWidth);
	        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
	        // 一定都会大于等于目标的宽和高。
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}

	/**
	 * 获取音视频缩略图
	 * @param path
	 * @param fd
	 * @param flag 0音频，1视频
	 * @return
	 */
	public static Bitmap createMediaThumbnail(String path, FileDescriptor fd, int flag){
		Bitmap bitmap = null;
		// 获取多媒体文件元数据的类
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			if(!StringUtils.isEmpty(path)){
				// 设置数据源
				retriever.setDataSource(path);
			}else {
				// 设置数据源
				retriever.setDataSource(fd);
			}
			if(flag==0) {
				// 得到字节型的数据
				byte[] embeddedPicture = retriever.getEmbeddedPicture();
				// 字节转换成图片
				bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);
			}else if(flag==1){
				// 取得视频截图
				bitmap = retriever.getFrameAtTime(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(retriever!=null){
				try {
					retriever.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	/*********************************** 测量图片大小 *********************************************/
	/**
	 * 这是用来测量图片大小的，而不加载图片内容
	 * @param imagePath 图片绝对路径
	 * @return
	 */
	public static BitmapFactory.Options getBitmapOptions(String imagePath){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			//仅仅测量大小，而不加载图片内容
			options.inJustDecodeBounds = true;
			//此时返回的bitmap为null
			BitmapFactory.decodeFile(imagePath, options);
			//options.outWidth, options.outHeight
			return options;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 这是用来测量图片大小的，而不加载图片内容
	 * @param context  图片所在上下文
	 * @param id       图片ID
	 * @return
	 */
	public static BitmapFactory.Options getBitmapOptions(Context context, int id){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			//仅仅测量大小，而不加载图片内容
			options.inJustDecodeBounds = true;
			//此时返回的bitmap为null
			BitmapFactory.decodeResource(context.getResources(), id, options);
			//options.outWidth, options.outHeight
			return options;
		} catch (Exception e) {
		}
		return null;
	}

	/************************************ 通过各种方式构造图片 ***********************************/
	/**
	 * 采集图片
	 * @param fileName
	 * @param flag
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName, int flag){
		Bitmap bit = getBitmapFromMemoryCache(fileName);
		if(null != bit){
			return bit;
		}else{
			try{
				InputStream is = null;
				if(flag== MyConfigure.ZERO_MYRES){//读我的资源（用户版）
					is = FileUtils.class.getClassLoader().getResourceAsStream(MyConfigure.ZCCL_CLIENT+fileName);
					
				}else if(flag==MyConfigure.ONE_ASSETS){//读assets
					is = context.getResources().getAssets().open(MyConfigure.ZCCL_ASSETS+fileName);
					
				}else if(flag==MyConfigure.TWO_SDCARD){//可以读SD卡和内存
					is = new FileInputStream(MyConfigure.ZCCL_SDCARD+fileName);
					
				}else if(flag==MyConfigure.THREE_FILES){//读内存(能不能级连打开)
					is = context.openFileInput(fileName);
					
				}else if(flag==MyConfigure.FOUR_SYSTEMRES){//读我的资源（系统版）
					is = FileUtils.class.getClassLoader().getResourceAsStream(MyConfigure.ZCCL_SYSTEM+fileName);
					
				}else if(flag==MyConfigure.FIVE_RESRAW){ //apk的res目录 
					int id = MYUIUtils.getResID(context, fileName, "drawable");
					bit = ID2Bitmap(context, id);
					
				}else if(flag==MyConfigure.SIX_ABSOLUTE){ //加载传入的原始资源 
					is = new FileInputStream(fileName);
					
				}
				
				if(null != is){
					bit = InputStream2Bitmap(is);
					is.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(null != bit){
				addBitmapToMemoryCache(fileName, bit);
			}
		}
		return bit;
	}

	/************************************* 图片缓存的操作 *****************************************/
	/**
	 * 清空图片缓存
	 */
	public static void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                LogUtils.d(TAG, "MemoryCache.size BeforeClear: " + mMemoryCache.size());
                mMemoryCache.evictAll();
                LogUtils.d(TAG, "MemoryCache.size AfterClear:" + mMemoryCache.size());
            }
            mMemoryCache = null;
        }
    }

	/**
     * 将指定的KEY移除缓存
     * @param key
     */
    public static void removeImageCache(String key) {
        if (key != null) {
            if (null != mMemoryCache) {
                Bitmap bm = mMemoryCache.remove(key);
                if (null != bm && !bm.isRecycled()){
                    bm.recycle();
                }
            }
        }
    }

	/**
     * 将一张图片存储到LruCache中。
     * @param key     LruCache的键，这里传入图片的URL地址。
     * @param bitmap  LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
        	if(null != key && null != bitmap){
        		mMemoryCache.put(key, bitmap);
        	}
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     * @param key  LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public static Bitmap getBitmapFromMemoryCache(String key) {
         if (null != key) {
             return mMemoryCache.get(key);
         }
         return null;
    }

	/************************************* 释放图片资源相关 ***************************************/
    /**
     * 释放图片
     * @param bmp
     */
    public static void releaseBitmap(Bitmap ... bmp){
    	if(bmp!=null){
			for (int i = 0; i < bmp.length; i++){
				if(bmp[i]!=null){
					if(!bmp[i].isRecycled()){
						bmp[i].recycle();
						bmp[i]=null;
					}
				}
			}
		}
    }

    /**
     * 释放图片
     * @param bmp
     */
    public static void releaseBitmap(List<Bitmap> bmp){
    	if(bmp!=null){
			for (int i = 0; i < bmp.size(); i++){
				if(bmp.get(i)!=null){
					if(!bmp.get(i).isRecycled()){
						bmp.get(i).recycle();
					}
				}
			}
			bmp.clear();
		}
    }

	/**
	 * 回收AnimationDrawable
	 * @param animationDrawable
     */
	public static void recycleAnimationDrawable(AnimationDrawable animationDrawable) {
		if (animationDrawable != null) {
			animationDrawable.stop();
			for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
				Drawable frame = animationDrawable.getFrame(i);
				if (frame instanceof BitmapDrawable) {
					Bitmap bitmap = ((BitmapDrawable) frame).getBitmap();
					if(!bitmap.isRecycled()){
						bitmap.recycle();
					}
				}
				frame.setCallback(null);
			}
			animationDrawable.setCallback(null);
		}
	}


    /**
     * 从资源包中解析图片
     * @param filelist
     * @param name
     * @return
     */
    /*private static Bitmap getBitmap(String name,int flag){
    	if(Configs.USEXPKG){
    		if(name.contains("/")){
    			String [] arr = name.split("/");
        		name=arr[arr.length-1];
    		}
    		byte[] data = decompressBytes(name);
        	if(data!=null){
        		return BitmapFactory.decodeByteArray(data, 0, data.length);
        	}
        	return null;
    	}else{
        	PayLogicMain paymain = PayLogicMain.getInstance();
        	String pkgname = paymain.getGameargs().getPkgName();
    		return BitmapFactory.decodeStream(getFileStream(paymain.getMyContext(), pkgname, flag));
    	}
    	return null;
    }*/
    
    
    
    /**
     * 解出指定文件的bytes数组（解压ZIP数组）
     * @param filelist
     * @param name
     * @return
     */
   /* public static byte[] decompressBytes(String name){
    	PayLogicMain paymain = PayLogicMain.getInstance();
    	List<FileHeader> filelist = paymain.getLuarunjava().getHeaderlist();
    	String pkgname = paymain.getGameargs().getPkgName();

    	int startoff = 0;
    	int size = 0;
    	int realsize = 0;
    	for (int i = 0; i < filelist.size(); i++) {
    		if(filelist.get(i).name.equals(name)){
    			startoff = filelist.get(i).startoff;
    			size = filelist.get(i).size;
    			realsize = filelist.get(i).realsize;
    			break;
    		}
		}

    	if(startoff!=0 && size!=0){
    		byte[] buf = new byte[size];
    		byte[] bufmid = new byte[1024];
    		byte[] backbytes = null;

    		InputStream is = getFileStream(paymain.getMyContext(),pkgname,Configs.WHICH);
			ByteArrayOutputStream byteaout = new ByteArrayOutputStream(realsize);
			try {
				is.skip(startoff);
				is.read(buf, 0, size);

				//解压缩文件
				ByteArrayInputStream bat = new ByteArrayInputStream(buf);
				//解压缩文件
				InputStream in = new InflaterInputStream(bat);
				int ch = 0;
				while ((ch = in.read(bufmid,0,1024))!= -1){
					byteaout.write(bufmid,0,ch);
				}

				is.close();
				in.close();
				bat.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			backbytes = byteaout.toByteArray();
    		try {
    			buf = null;
    			bufmid = null;
				byteaout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		return backbytes;
    	}
    	return null;
    } */
    
    
}
