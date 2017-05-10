package com.yongyida.robot.voice.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;

import com.google.gson.Gson;
import com.yongyida.robot.voice.bean.PhotoCmdInfo;
import com.yongyida.robot.voice.data.GeneralData;

public class GraphsUtil {
	private String PHOTO_PATH = "/PlayCamera/";
	
	/**
	 * 通过图片名称获取图片.
	 * 
	 * @param fileName
	 * @return
	 */
	public PhotoCmdInfo getPicturesFromName(String fileName, String type) {

		try {
			String sdPath = FileUtil.getSdcardPath();
			String path = null; 
			Gson gson = new Gson();
			byte[] bt = null;
			if(sdPath != null){

				path = sdPath + PHOTO_PATH + fileName;
				if(type.trim().equals(GeneralData.PHOTO_THUMBNAIL)){ 
					bt = imageThumbnailByteArray(path);
				}else if(type.trim().equals(GeneralData.PHOTO_ORIGINAL)){
					bt = imageOrignalByteArray(path);
				}
				PhotoCmdInfo photo = new PhotoCmdInfo();
				Map<String, String> map = new HashMap<String, String>();
				
				map.put("name", fileName.trim());
				
				map.put("cmd", "photo_query");
				
				String command = gson.toJson(map);
				
				Map<String, String> photoMap = new HashMap<String, String>();
				
				photoMap.put("command", command);
				photoMap.put("cmd", "/robot/callback");
				photo.setPhotoMap(photoMap);
				photo.setPhoto_Data(bt);
				return photo;
				
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean deletePhoto(String name){
		String sdPath = FileUtil.getSdcardPath();
		String path;
		if(sdPath != null){
			path = sdPath + PHOTO_PATH + name;
			File file = new File(path);
			if(file != null && file.exists()){
				return file.delete();
			}
		}
		
		return false;
		
	}
	
	public boolean deletePhoto(String path, String name){
		
		if(path == null){
			return false;
		}
		if(name == null){
			return false;
		}
		
		String sdPath = FileUtil.getSdcardPath();
		String fPath = sdPath + path + name;
		
		File file = new File(fPath);
		if(file != null && file.exists()){
				return file.delete();
		}
		
		return false;
		
	}

	public Map<String, String> getAllImageName(){
		
		try {
			synchronized (GraphsUtil.class){
				Map<String, String> map = new HashMap<String, String>();
				Map<String, String> names = new HashMap<String, String>();
				Gson gson = new Gson();
				String name = gson.toJson(getImageList());
				names.put("names", name); 
				names.put("cmd", "photo_names");
				String command = gson.toJson(names);
				map.put("command", command);
				map.put("cmd", "/robot/callback");
				
				return map;
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, String>> getImageList(){
		try {
			ComparatorImageName comparator = new ComparatorImageName();
			List<Map<String, String>> nameList = null;
			String sdPath = FileUtil.getSdcardPath();
			
			if(sdPath != null){
				nameList = new ArrayList<Map<String, String>>();
				File file = new File(sdPath + PHOTO_PATH);
				String[] fs = file.list();
				if(fs != null){
					for(String f : fs){
						Map<String, String> map = new HashMap<String, String>();
						map.put("name", f);
						nameList.add(map);
					}
				}
			}
			
			Collections.sort(nameList, comparator);
			LogUtils.showLogInfo(GeneralData.SUCCESS, nameList.toString());
			return nameList;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取缩略图byte[].
	 * 
	 * @param path
	 * @return
	 */
	private byte[] imageThumbnailByteArray(String path){
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if(bitmap != null){
			Bitmap bm = ThumbnailUtils.extractThumbnail(bitmap, 400, 400);
			
			return Bitmap2Bytes(bm);
		}
		
		return null;
	}
	
	/**
	 * 获取原图byte[].
	 * 
	 * @param path
	 * @return
	 */
	private byte[] imageOrignalByteArray(String path){
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if(bitmap != null){
			
			return Bitmap2Bytes(bitmap);
		}
		
		return null;
	}

	private byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);

			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	@SuppressWarnings("rawtypes")
	protected class ComparatorImageName implements Comparator{

		@SuppressWarnings("unchecked")
		public int compare(Object arg0, Object arg1) {
			Map<String, String> list0 =  (Map<String, String>) arg0;
			Map<String, String> list1 =  (Map<String, String>) arg1;
			int flag = list0.get("name").compareTo(list1.get("name"));
			return flag;
		}

	}

}
