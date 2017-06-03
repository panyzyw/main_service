package com.zccl.ruiqianqi.mind.service.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.LinkedList;
import java.util.List;

public class MyUserService extends Service {

	/** 类标志 */
	private static String TAG = MyUserService.class.getSimpleName();

	private LinkedList<MyUserBean> myUserBeanLinkedList = new LinkedList<>();

	public MyUserService() {
	}

	/**
	 * 这个是服务体，是服务具体实现的地方，在这边没有通讯相关的了，通讯已经过来了
	 */
	private final IMyUserService.Stub mBinder = new IMyUserService.Stub() {

		@Override
		public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

		}

		/**
		 * 在onTransact被调用
		 */
		@Override
		public MyUserBean query(String name) throws RemoteException {
			return null;
		}

		@Override
		public String insert(String name, IMyUserCallback callBack) throws RemoteException {
			return null;
		}

		/**
		 * 在onTransact被调用
		 */
		@Override
		public List<MyUserBean> getMyUserBeans() throws RemoteException {
			return myUserBeanLinkedList;
		}

	}; 

	/**
	 * 返回一个通信通道
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
