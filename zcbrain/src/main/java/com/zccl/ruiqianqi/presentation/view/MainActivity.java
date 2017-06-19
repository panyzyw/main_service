package com.zccl.ruiqianqi.presentation.view;

import android.os.Bundle;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.view.activity.BaseCompatActivity;

import butterknife.ButterKnife;

public class MainActivity extends BaseCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //Configuration.checkVoiceResource(this);
        //MyAppUtils.developerMode(getApplicationContext());
        SystemUtils.getMemInfo(getApplicationContext());
    }

    /**
     * 初始化UI
     */
    private void initView(){
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
