package com.zccl.ruiqianqi.presentation.presenter;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.domain.model.pushback.QueryPhotosBack;
import com.zccl.ruiqianqi.domain.model.pushdown.QueryPhoto;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.ResUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_ORDER_CALLBACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_NAMES_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_PHOTO_DELETE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_PHOTO_ORIGINAL;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_PHOTO_QUERY_LIST;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_PHOTO_THUMBNAIL;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class PhotoPresenter extends BasePresenter {

    // 类标志
    private static String TAG = PhotoPresenter.class.getSimpleName();
    // 供手机查看机器人照片的文件夹
    private static final String PHOTO_PATH = "PlayCamera/";
    // 删除时，需要扫描的目录
    private static final String DEL_PHOTO_PATH_1 = "PhotosCamera/";
    // 删除时，需要扫描的目录
    private static final String DEL_PHOTO_PATH_2 = "BigPhotosCamera/";
    /**
     * 处理数据
     * @param result
     */
    public void parseData(String result){
        QueryPhoto queryPhoto = JsonUtils.parseJson(result, QueryPhoto.class);
        if(null != queryPhoto){
            QueryPhoto.BaseQueryPhoto baseQueryPhoto = JsonUtils.parseJson(queryPhoto.getCommand(), QueryPhoto.BaseQueryPhoto.class);

            if(null != baseQueryPhoto){

                // 查看所有照片
                if(TYPE_PHOTO_QUERY_LIST.equals(baseQueryPhoto.getType())){
                    QueryPhotosBack.PhotoNamesResult photoNamesResult = new QueryPhotosBack.PhotoNamesResult();
                    List<Map<String, String>> nameList = getImageNameList();
                    Gson gson = new Gson();
                    photoNamesResult.setNames(gson.toJson(nameList));
                    sendToServer(B_PHOTO_NAMES_RESULT, gson.toJson(photoNamesResult), null);

                }
                // 获取照片缩略图
                else if(TYPE_PHOTO_THUMBNAIL.equals(baseQueryPhoto.getType())){
                    QueryPhoto.QueryPhotoData queryPhotoData = JsonUtils.parseJson(queryPhoto.getCommand(), QueryPhoto.QueryPhotoData.class);
                    if(null==queryPhotoData)
                        return;
                    String filePath = MyConfigure.SDCARD + PHOTO_PATH + queryPhotoData.getName();
                    Bitmap bitmap = ResUtils.getBitmap(mContext, filePath, MyConfigure.SIX_ABSOLUTE);
                    Bitmap bitmapThumb = ResUtils.getSmallBitmap(bitmap, 400, 400, false);
                    ResUtils.removeImageCache(filePath);
                    byte[] thumbBytes = ResUtils.Bitmap2Bytes(bitmapThumb, 25);
                    ResUtils.releaseBitmap(bitmapThumb);
                    photoDataBack(queryPhotoData.getName(), thumbBytes);

                }
                // 获取照片原始图
                else if(TYPE_PHOTO_ORIGINAL.equals(baseQueryPhoto.getType())){
                    QueryPhoto.QueryPhotoData queryPhotoData = JsonUtils.parseJson(queryPhoto.getCommand(), QueryPhoto.QueryPhotoData.class);
                    if(null==queryPhotoData)
                        return;
                    String filePath = MyConfigure.SDCARD + PHOTO_PATH + queryPhotoData.getName();
                    InputStream is = FileUtils.getFileStream(mContext, filePath, MyConfigure.SIX_ABSOLUTE);
                    // 用这种方式获取原始的、未压缩的图片的数据
                    //byte[] originalBytes = ResUtils.InputStream2Bytes(is);

                    Bitmap bitmapOriginal = ResUtils.InputStream2Bitmap(is);
                    byte[] originalBytes = ResUtils.Bitmap2Bytes(bitmapOriginal, 25);
                    ResUtils.releaseBitmap(bitmapOriginal);
                    photoDataBack(queryPhotoData.getName(), originalBytes);

                }
                // 删除指定照片集
                else if(TYPE_PHOTO_DELETE.equals(baseQueryPhoto.getType())){
                    QueryPhoto.QueryPhotoDelete queryPhotoDelete = JsonUtils.parseJson(queryPhoto.getCommand(), QueryPhoto.QueryPhotoDelete.class);
                    if(null==queryPhotoDelete)
                        return;
                    ArrayList<Map<String, String>> names = queryPhotoDelete.getNames();
                    if(null != names){
                        for (int i = 0; i <names.size(); i++) {
                            Map<String, String> nameMap = names.get(i);
                            String fileName = nameMap.get("name");
                            if(!StringUtils.isEmpty(fileName)) {
                                FileUtils.deleteFile(MyConfigure.SDCARD + PHOTO_PATH + fileName);
                                FileUtils.deleteFile(MyConfigure.SDCARD + DEL_PHOTO_PATH_1 + fileName);
                                FileUtils.deleteFile(MyConfigure.SDCARD + DEL_PHOTO_PATH_2 + fileName);
                            }
                        }
                    }
                }else {

                }
            }
        }
    }

    /**
     * 返回指定目录下，文件名集合
     * @return
     */
    private List<Map<String, String>> getImageNameList(){
        List<Map<String, String>> nameList = new ArrayList<>();
        try {
            ComparatorImageName comparator = new ComparatorImageName();
            String path = MyConfigure.SDCARD + PHOTO_PATH;
            File file = new File(path);
            if(file.exists()){
                String[] filePaths = file.list();
                if(null != filePaths){
                    for(String filePath : filePaths){
                        Map<String, String> map = new HashMap<>();
                        map.put("name", filePath);
                        nameList.add(map);
                    }
                }
                Collections.sort(nameList, comparator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return nameList;
    }

    /**
     * 按照字母顺序排列
     */
    private class ComparatorImageName implements Comparator<Map<String, String>> {
        @Override
        public int compare(Map<String, String> o1, Map<String, String> o2) {
            return o1.get("name").compareTo(o2.get("name"));
        }
    }

    /**
     * 返回指定名称照片的数据给服务器
     * @param fileName
     * @param photoData
     */
    private void photoDataBack(String fileName, byte[] photoData){
        QueryPhotosBack.PhotoDataResult photoQueryResult = new QueryPhotosBack.PhotoDataResult();
        photoQueryResult.setName(fileName);
        Gson gson = new Gson();
        sendToServer(B_PHOTO_QUERY_RESULT, gson.toJson(photoQueryResult), photoData);
    }

    /**
     * 发数据给服务器
     * @param cmd       区分给服务器的响应的指令
     * @param text      响应的数据
     * @param data      响应的数据
     */
    private void sendToServer(String cmd, String text, byte[] data){
        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(cmd);
        forwardSocketEvent.setText(text);
        forwardSocketEvent.setData(data);
        EventBus.getDefault().post(forwardSocketEvent);
    }
}
