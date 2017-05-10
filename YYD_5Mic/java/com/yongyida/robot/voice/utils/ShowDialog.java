package com.yongyida.robot.voice.utils;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yongyida.robot.voice.R;

public class ShowDialog {
	
	private static final String TAG = "ShowDialog";
	private static Dialog dialog;
	public static void showImageDialog(Context context, int resId) {
		if(context == null) return;
		
		try {
			if(dialog==null)
			dialog = new Dialog(context,R.style.dialog);
			View v =  View.inflate(context, R.layout.dialog_vr_game, null);
			ImageView view = (ImageView) v.findViewById(R.id.iv_game);
			view.setImageResource(resId);
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.setContentView(v);
			dialog.show();
			

		} catch (Throwable e) {
			LogUtils.showLogError(TAG, TAG + " : showImageDialog : " + e);
			e.printStackTrace();
		}
		
	}
	
	public static void cancleImageDialog() {
		if(dialog == null) return;
		dialog.dismiss();
		
	}
}
