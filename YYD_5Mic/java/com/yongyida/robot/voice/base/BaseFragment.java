package com.yongyida.robot.voice.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	protected  View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(getContentView(), container, false);
		initVariable();
		initExtraData();
		initView();
		return view;
	}
	
	protected abstract int getContentView();
	
	protected abstract void initVariable();
	
	protected abstract void initExtraData();
	
	protected abstract void initView();
	
}
