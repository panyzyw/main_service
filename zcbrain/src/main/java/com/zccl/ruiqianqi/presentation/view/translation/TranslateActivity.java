package com.zccl.ruiqianqi.presentation.view.translation;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.brain.handler.FirstHandler;
import com.zccl.ruiqianqi.domain.model.translate.TransInfoD;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.Speaker;
import com.zccl.ruiqianqi.presentation.presenter.TranslatePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.utils.AppUtils;
import com.zccl.ruiqianqi.view.activity.BaseCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_EXIT;
import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_FAILURE;
import static com.zccl.ruiqianqi.brain.handler.BaseHandler.SCENE_MY_TRANS;
import static com.zccl.ruiqianqi.brain.receiver.MainReceiver.KEY_SCENE_NAME;
import static com.zccl.ruiqianqi.brain.receiver.MainReceiver.KEY_SCENE_STATUS;
import static com.zccl.ruiqianqi.brain.receiver.MainReceiver.ROBOT_SCENE;
import static com.zccl.ruiqianqi.mind.voice.impl.VoiceManager.DEFAULT_SPEAKER;
import static com.zccl.ruiqianqi.plugin.voice.Speaker.OFF_LINE_SPEAKER;
import static com.zccl.ruiqianqi.plugin.voice.Speaker.ON_LINE_SPEAKER;

/**
 * Created by ruiqianqi on 2017/4/20 0020.
 */

public class TranslateActivity extends BaseCompatActivity implements TranslatePresenter.TranslateCallback2V<TransInfoD> {

    // 翻译循环监听标志
    private final String mTrans = "listen_Trans";

    @BindView(R.id.trans_iv_from)
    ImageView trans_iv_from;

    @BindView(R.id.trans_content_from)
    TextView trans_content_from;

    @BindView(R.id.trans_iv_to)
    ImageView trans_iv_to;

    @BindView(R.id.trans_content_to)
    TextView trans_content_to;

    @BindView(R.id.trans_switch_language)
    ToggleButton trans_switch_language;

    @BindView(R.id.trans_tip_view)
    TextView trans_tip_view;

    // 翻译任务处理中心
    private TranslatePresenter translatePresenter;
    // 语音操作对象
    private AbstractVoice mRobotVoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_activity);
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
        translatePresenter = new TranslatePresenter(this);
        translatePresenter.setTranslateCallback2V(this);
        mRobotVoice = MindPresenter.getInstance().getVoiceDevice();

        // 默认是中文翻译成英文，使用英文发音人
        Speaker speaker = new Speaker();
        speaker.setOffOnType(ON_LINE_SPEAKER);
        speaker.setSpeakerName("catherine");
        mRobotVoice.setTtsParams(speaker);

        // 设置当前为翻译场景
        sceneStatus(true);
        // 注册事件总线
        EventBus.getDefault().register(this);
        // 开始翻译场景
        startListen();
    }

    /**
     * 初始化UI
     */
    private void initView(){
        trans_iv_from.setImageResource(R.drawable.trans_cn_quote);
        trans_iv_to.setImageResource(R.drawable.trans_cn_result);
    }

    /***********************************【生命周期】***********************************************/

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

        // 设置回离线发音人jiajia
        Speaker speaker = new Speaker();
        speaker.setOffOnType(OFF_LINE_SPEAKER);
        speaker.setSpeakerName(DEFAULT_SPEAKER);
        mRobotVoice.setTtsParams(speaker);

        // 退出音乐场景
        sceneStatus(false);
        // 注销事件总线
        EventBus.getDefault().unregister(this);
        // 结束监听
        //stopListen();
        // 切换回中文
        mRobotVoice.switchLanguage("zh");

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 结束监听
        stopListen();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /********************************【私有方法】**************************************************/
    /**
     * 进入，退出，翻译场景
     * @param status
     */
    private void sceneStatus(boolean status){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SCENE_NAME, SCENE_MY_TRANS);
        bundle.putBoolean(KEY_SCENE_STATUS, status);
        MyAppUtils.sendBroadcast(getApplicationContext(), ROBOT_SCENE, bundle);
    }

    /**
     * 开始监听
     */
    private void startListen(){
        AppUtils.startListen(this, mTrans, false, false);
    }

    /**
     * 结束监听
     */
    private void stopListen(){
        AppUtils.stopListen(this, mTrans);
    }


    /*********************************【事件总线】*************************************************/
    /**
     * 接收到网络变化事件，从
     * {@link FirstHandler#handleAsr} 发过来的
     * @param transEvent
     */
    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void OnTransEvent(MindBusEvent.TransEvent transEvent){

        // 隐藏错误显示
        trans_tip_view.setVisibility(View.GONE);

        // 退出翻译
        if(TRANS_EXIT == transEvent.getType()){
            finish();
        }
        // 听写出错，继续听写
        else if(TRANS_FAILURE == transEvent.getType()){
            if(trans_switch_language.isChecked()){
                trans_content_from.setText(getString(R.string.identify_error_en));
            }else {
                trans_content_from.setText(transEvent.getText() + "");
            }
            trans_content_to.setText("");
            startListen();
        }
        // 听写没错，开始翻译
        else {
            trans_content_from.setText(transEvent.getText());
            translatePresenter.translateRx(transEvent.getText());
        }
    }

    /**********************************【按键事件】************************************************/
    /**
     * 切换翻译语言
     */
    @OnClick(R.id.trans_switch_language)
    void switchLanguage(){

        // 结束当前监听
        stopListen();

        // 翻译英文，识别英文，设置成中文发音人
        if(trans_switch_language.isChecked()){
            trans_iv_from.setImageResource(R.drawable.trans_en_quote);
            trans_iv_to.setImageResource(R.drawable.trans_en_result);
            mRobotVoice.switchLanguage("en");

            Speaker speaker = new Speaker();
            speaker.setOffOnType(ON_LINE_SPEAKER);
            speaker.setSpeakerName("aisxa");
            mRobotVoice.setTtsParams(speaker);
        }
        // 翻译中文，识别是文，设置成英文发音人
        else{
            trans_iv_from.setImageResource(R.drawable.trans_cn_quote);
            trans_iv_to.setImageResource(R.drawable.trans_cn_result);
            mRobotVoice.switchLanguage("zh");

            Speaker speaker = new Speaker();
            speaker.setOffOnType(ON_LINE_SPEAKER);
            speaker.setSpeakerName("catherine");
            mRobotVoice.setTtsParams(speaker);
        }
        trans_content_from.setText("");
        trans_content_to.setText("");

        // 开始当前监听
        startListen();
    }

    /***********************************【翻译的回调】*********************************************/
    @Override
    public void OnSuccess(TransInfoD transInfoD) {
        if(null != transInfoD) {

            trans_content_to.setText(transInfoD.getTranslation().get(0));

            mRobotVoice.startTTS(transInfoD.getTranslation().get(0), new Runnable() {
                @Override
                public void run() {
                    startListen();
                }
            });
        }else {
            if(trans_switch_language.isChecked()){
                trans_tip_view.setText(getString(R.string.trans_result_exception_en));
            }else {
                trans_tip_view.setText(getString(R.string.trans_result_exception_zh));
            }
            trans_tip_view.setVisibility(View.VISIBLE);
            startListen();
        }
    }

    @Override
    public void OnFailure(Throwable e) {
        if(trans_switch_language.isChecked()){
            trans_tip_view.setText(getString(R.string.trans_result_exception_en));
        }else {
            trans_tip_view.setText(getString(R.string.trans_result_exception_zh));
        }
        trans_tip_view.setVisibility(View.VISIBLE);
        startListen();
    }

}
