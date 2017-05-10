package com.zccl.ruiqianqi.mind.service.aidl.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.zccl.ruiqianqi.mind.service.aidl.server.IMyUserService;
import com.zccl.ruiqianqi.mind.service.aidl.server.MyUserBean;

import java.util.List;

public class MyUserClient {

	/**应用上下文*/
	private Context context;
	/**接口绑定好了吗*/
	private boolean isusing = false;
	/**封装成对应接口*/
	private IMyUserService mRemoteService;

	/** 处理来自服务器回调的第一步 */
	private MyUserHandler myUserHandler;

	/**
	 * 通信接口是我连接服务时，由系统的回调传递给我的
	 */
	private ServiceConnection mRemoteConnection = new ServiceConnection() {
		/**
		 * 这个回调是由ActiviManagerService所在进程，通过Binder间通迅机制（Binder引用为ApplicationThread）
		 * 通知当前进程（ActivityThread），当前进程再通过自己的消息分发处理机制，来处理这个调用，其实也就是发送到
		 * 主线程循环中进行调用。所以我们无法确认在调用方法之前是否已经绑定好了（如果绑定和调用是一起执行的话）
		 */
		public void onServiceConnected(ComponentName className, IBinder service) {
			mRemoteService = IMyUserService.Stub.asInterface(service);
			myUserHandler = new MyUserHandler();

			isusing = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			isusing = false;
			mRemoteService = null;
		}
	};

	public MyUserClient(Context context) {
		this.context = context.getApplicationContext();
	}

	/**
	 * 绑定服务
	 */
	public void bindMyService(){
		context.bindService(new Intent(IMyUserService.class.getName()), mRemoteConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 解除绑定
	 */
	public void unbindMyService(){
		context.unbindService(mRemoteConnection);
	}

	/**
	 * 根据名字查询用户
	 * @param name
	 * @return
     */
	public MyUserBean query(String name){
		if(!isusing || mRemoteService==null){
			return null;
		}
		try {
			MyUserBean userBean = mRemoteService.query(name);
			return userBean;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据名字查询用户
	 * @param name
	 * @return
	 */
	public String insert(String name, IMyUserListener myUserListener){
		if(!isusing || mRemoteService==null){
			return null;
		}
		try {
			myUserHandler.setMyUserListener(myUserListener);
			String ret = mRemoteService.insert(name, myUserHandler.getMyUserCallback());
			return ret;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到所有的对象
	 * @return
	 */
	public List<MyUserBean> getMyUserBeans(){
		if(!isusing || mRemoteService==null){
			return null;
		}
		try {
			return mRemoteService.getMyUserBeans();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 接收到服务器消息后的处理类
	 */
	private static class MyUserHandler extends Handler {

		/**
		 * 服务器回调的实际接收者，
		 */
		private MyUserCallback myUserCallback;
		/** 对外的回调处理接口 */
		private IMyUserListener myUserListener;

		private MyUserHandler(){
			super(Looper.getMainLooper());
			myUserCallback = new MyUserCallback(this);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MyUserCallback.SEND_SUCCESS:
					if(myUserListener != null){
						myUserListener.OnSuccess("");
					}
					break;

				case MyUserCallback.SEND_FAILURE:
					if(myUserListener != null){
						myUserListener.OnFailure("");
					}
					break;

				default:
					break;
			}
		}

		/**
		 * 返回远程接口原来的回调接口
		 * @return
         */
		public MyUserCallback getMyUserCallback() {
			return myUserCallback;
		}

		/**
		 * 设置给用户的回调接口
		 * @param myUserListener
         */
		public void setMyUserListener(IMyUserListener myUserListener) {
			this.myUserListener = myUserListener;
		}

	}
}
