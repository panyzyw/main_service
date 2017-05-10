package com.yongyida.robot.voice.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.voice.R;


public class ShowToast {

	private static final String TAG = "ShowToast";
	private Context context;
	private View view;
	private Toast mToast;
	private TextView mTextView;
	private Typeface fontFace;
	private static ShowToast st;
	
	
	private ShowToast(Context context){
		if(context == null){
			Log.e(TAG, TAG + new Exception("context is null"));
			return;
		}
		
		try {
			fontFace = Typeface.createFromAsset(context.getAssets(), "fonts/FrLtDFGirl.ttf");
			view = LayoutInflater.from(context).inflate(R.layout.toast_show_text, null);
			mTextView = (TextView) view.findViewById(R.id.tv_show);
			mTextView.setTypeface(fontFace);
			if(mToast == null){
				mToast = new Toast(context);
			}
			
			//mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);  
			mToast.setDuration(Toast.LENGTH_LONG);  
		} catch (Exception e) {
			LogUtils.showLogError(TAG, e + "");
		}
	}
	
	
	
	public void show(String text){
		mTextView.setText(text);
		mToast.setView(view);
		mToast.show();
		
	}
	
	
	public void show(String text1, String text2){
		mTextView.setTextColor(Color.RED);
		mTextView.setTextSize(50);
		mTextView.setText(text1 + " " + text2);
		mToast.setView(view);
		mToast.show();
//		mTextView.setTextColor(Color.WHITE);
//		mTextView.setTextSize(30);
		
	}

	public static ShowToast getInstance(Context context){
		
		if(st == null){
			st = new ShowToast(context);
		}
			
		return st;
		
	}
	
	
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
		
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Toast getmToast() {
		return mToast;
	}

	public void setmToast(Toast mToast) {
		this.mToast = mToast;
	}

	public TextView getmTextView() {
		return mTextView;
	}

	public void setmTextView(TextView mTextView) {
		this.mTextView = mTextView;
	}
	
	
	
	
	
	
	
	
	
	
}
