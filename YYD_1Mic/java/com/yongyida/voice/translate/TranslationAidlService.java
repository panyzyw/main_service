package com.yongyida.voice.translate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by Administrator on 2017/1/18.
 */

public class TranslationAidlService {
    private  Context context;
    private static TranslationAidlService instance ;
    public ITranslationAidlInterface mstub;
    public static boolean isConn;
    public TranslationAidlService(Context context){
        this.context = context;
        bindTranslacionService();
    }

    private void bindTranslacionService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.yongyida.voice.translate","com.yongyida.voice.service.TranslationService"));
        context.bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }
     ServiceConnection conn = new ServiceConnection() {

         @Override
         public void onServiceConnected(ComponentName name, IBinder service) {
              mstub = ITranslationAidlInterface.Stub.asInterface(service);
/*              try {
				mstub.sendVoiceData("xxxx", "22");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
             isConn = true;
         }

         @Override
         public void onServiceDisconnected(ComponentName name) {
                     isConn = false;
         }
     };
    public static TranslationAidlService getInstance(Context context){
        if(instance == null){
            instance = new TranslationAidlService(context);
        }
        return instance;
    }
    public void unbindTranService(){ 
    	    instance = null;
    		context.unbindService(conn);    		
    }

}
