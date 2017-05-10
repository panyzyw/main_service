package com.zccl.ruiqianqi.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.tools.executor.asynctask.MyAsyncTask;
import com.zccl.ruiqianqi.view.dialog.MyDialogFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zc on 2016/3/18.
 * screenOrientation的属性值必须是以下常量值。
 *
 * ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED：
 * 不指定方向，让系统决定Activity的最佳方向。
 * 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择。
 *
 * ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE：
 * 希望Activity在横向屏上显示，也就是说横向的宽度要大于纵向的高度，并且忽略方向传感器的影响。
 *
 * ActivityInfo.SCREEN_ORIENTATION_PORTRAIT：
 * 希望Activity在纵向屏上显示，也就是说纵向的高度要大于横向的宽度，并且忽略方向传感器的影响。
 *
 * ActivityInfo.SCREEN_ORIENTATION_USER：
 * 使用用户设备的当前首选方向。
 * 用户当前的首选方向。
 *
 * ActivityInfo.SCREEN_ORIENTATION_BEHIND：
 * 始终保持与屏幕一致的方向，不管这个Activity在前台还是后台？
 * 继承Activity堆栈中当前Activity下面的那个Activity的方向？
 *
 * ActivityInfo.SCREEN_ORIENTATION_SENSOR：
 * Activity的方向由物理方向传感器来决定，按照用户旋转设备的方向来显示。
 * 由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化。
 *
 * ActivityInfo.SCREEN_ORIENTATION_NOSENSOR：
 * 始终忽略方向传感器的判断，当用户旋转设备时，显示不跟着旋转。
 * 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)。
 *
 * ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE：
 * 希望Activity在横向屏幕上显示，但是可以根据方向传感器指示的方向来进行改变。
 *
 * ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT：
 * 希望Activity在纵向屏幕上显示，但是可以根据方向传感器指示的方向来进行改变。
 *
 * ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE：
 * 希望Activity在横向屏幕上显示，但与正常的横向屏幕方向相反。
 *
 * ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT：
 * 希望Activity在纵向屏幕上显示，但与正常的纵向屏幕方向相反
 *
 * ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR:
 * Activity的方向由方向传感器来决定，显示会根据用户设备的移动情况来旋转。
 *
 * 除了对话框必须要 activity 之外，其他的地方context通用
 */
public class BaseCompatActivity extends AppCompatActivity {
    
    /** 类的标志 */
    protected String TAG = null;
    
    /**默认参数一的key*/
    public static final String KEY_PARAM1 = "param1";
    /**默认参数二的key*/
    public static final String KEY_PARAM2 = "param2";

    /**默认参数一*/
    protected String mParam1 = null;
    /**默认参数二*/
    protected String mParam2 = null;

    /**窗口是否暂停了*/
    private boolean isPaused = false;
    /**Activity窗口*/
    protected Window mWindow;
    /**手机设备及尺寸信息*/
    protected DisplayMetrics mDisplayMetrics =null;
    /**碎片管理器*/
    protected FragmentManager mFragmentManager =null;
    /**主线程Handler*/
    protected Handler mHandler = null;
    /** 手势监听 */
    //protected GestureDetectorCompat mDetector;

    /**是否调试UI生命周期*/
    protected boolean debugUI = false;
    // 是不是全屏
    protected boolean isFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        
        if(debugUI) {
            LogUtils.e(TAG, "onCreate");
        }

        if(isFullScreen) {
            // 全屏代码
            MYUIUtils.removeStatusTitle(this);
        }

        if(savedInstanceState==null) {
            //刚启动的时候，跳转的时候传递的
            mParam1 = getIntent().getStringExtra(KEY_PARAM1);
            mParam2 = getIntent().getStringExtra(KEY_PARAM2);
        } else {
            //Activity意外重启的时候保存的，也会保存Fragment信息
            mParam1 = savedInstanceState.getString(KEY_PARAM1);
            mParam2 = savedInstanceState.getString(KEY_PARAM2);
        }

        // 保持屏幕常亮
        SystemUtils.keepScreenOn(this);

        // 构造对象
        mDisplayMetrics = MYUIUtils.getMetrics(this);

        mWindow = getWindow();
        mFragmentManager = getSupportFragmentManager();
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                return handMessage(message);
            }
        });

        // 清空Fragment的栈
        clearFragmentStack();

        // 隐藏虚拟按键
        MYUIUtils.initVirtualKey(this);

        // 注册总线事件，测试
        //EventBus.getDefault().register(this);

        // 根据上次的语言设置，重新设置语言
        SystemUtils.switchLanguage(this, ShareUtils.getP(this).getString(MyConfigure.KEY_LANGUAGE, MyConfigure.getLanguage()));

        /**
         Android:windowSoftInputMode="stateUnspecified", 默认设置：软键盘的状态(隐藏或可见)没有被指定。
         "stateUnchanged",     软键盘被保持上次的状态。
         "stateHidden",        当用户选择该Activity时，软键盘被隐藏。
         "stateAlwaysHidden",  软键盘总是被隐藏的。
         "stateVisible",       软键盘是可见的。
         "stateAlwaysVisible", 当用户选择这个Activity时，软键盘是可见的。
         "adjustUnspecified",
         "adjustResize", （压缩模式） 当软键盘弹出时，要对主窗口调整屏幕的大小以便留出软键盘的空间。
         "adjustPan", （平移模式：当输入框不会被遮挡时，该模式没有对布局进行调整，然而当输入框将要被遮挡时，
                    窗口就会进行平移。也就是说，该模式始终是保持输入框为可见。（键盘遮挡使用这种方法就能解决了！）
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 有没有物理键盘都显示菜单
        setOverflowShowingAlways();

    }

    /************************************Activity启动**********************************************/
    @Override
    protected void onStart() {
        super.onStart();
        if(debugUI) {
            LogUtils.e(TAG, "onStart");
        }

        // 循环发送数字，这个问题现在用弱引用来解决，不用【RxAppCompatActivity】
        // 如果请求返回的时候，对象已不可见或已销毁则不做任何处理或取消订阅事件
        /*Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(MyRxUtils.<Long>handleSchedulers())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        LogUtils.e(TAG, "lifecycle-stop-" + aLong);
                    }
                });*/
    }

    /**
     * Bundle中的数据必须能够被序列化和反序列化
     *
     * Activity被系统杀死后再重建时被调用.
     * 例如:
     * 屏幕方向改变时,Activity被销毁再重建；
     * 当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity；
     * 当跳转到其他Activity或者按Home键回到主屏时，该方法也会被调用；
     *
     * 这三种情况下onRestoreInstanceState都会被调用,在onStart之后，onResume之前.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onRestoreInstanceState");
        }
        if(savedInstanceState!=null) {
            //Activity意外重启的时候保存的，也会保存Fragment信息
            mParam1 = savedInstanceState.getString(KEY_PARAM1);
            mParam2 = savedInstanceState.getString(KEY_PARAM2);
        }
    }

    /**
     * 当Activity彻底运行起来之后回调onPostCreate方法，从官方解释可以看出 "Called when activity start-up is complete
     * (after onStart() and onRestoreInstanceState(Bundle) have been called)."
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(debugUI) {
            LogUtils.e(TAG, "onPostCreate");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(debugUI) {
            LogUtils.e(TAG, "onResume");
        }
        isPaused = false;
    }

    /**
     * resume完全运行起来
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(debugUI) {
            LogUtils.e(TAG, "onPostResume");
        }
    }

    /**
     *
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /************************************Activity销毁及意外销毁************************************/

    /**
     * Bundle中的数据必须能够被序列化和反序列化
     *
     * Activity被系统杀死时被调用：
     * 1.屏幕方向改变时,Activity被销毁再重建；
     * 2.当前Activity处于后台，系统资源紧张将其杀死；
     * 3.当跳转到其他Activity或者按Home键回到主屏时，该方法也会被调用，系统是为了保存当前View组件的状态.
     *
     * 在onPause之前被调用？
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(debugUI) {
            LogUtils.e(TAG, "onSaveInstanceState");
        }
        if(outState!=null) {
            outState.putString(KEY_PARAM1, mParam1);
            outState.putString(KEY_PARAM2, mParam2);
        }

    }

    /**
     * 只要不显示出来，就会走到这里
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(debugUI) {
            LogUtils.e(TAG, "onPause");
        }
        isPaused = true;
    }

    /**
     * 只要不显示出来，就会走到这里
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(debugUI) {
            LogUtils.e(TAG, "onStop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(debugUI) {
            LogUtils.e(TAG, "onDestroy");
        }

        // 注销总线事件
        //EventBus.getDefault().unregister(this);

    }

    /***********************************【Event接收处理方法】**************************************/
    /**
     * 黏性事件，简单讲，就是在发送事件之后再订阅该事件也能收到该事件，跟黏性广播类似
     *
     * You may change the order of event delivery by providing a priority to the subscriber during registration.
     * Within the same delivery thread (ThreadMode), higher priority subscribers will receive events before others with a lower priority.
     *
     * You may cancel the event delivery process by calling cancelEventDelivery(Object event) from a subscriber’s event handling method.
     * Any further event delivery will be cancelled: subsequent subscribers won’t receive the event.
     *
     * Events are usually cancelled by higher priority subscribers. Cancelling is restricted to event handling methods running in posting thread ThreadMode.PostThread.
     * @param oneEvent
     */
    /*
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1, sticky = true)
    public void OnMessageMain(MainBusEvent.OneEvent oneEvent){

    }
    */

    /**
     * 难道只能用cancelEventDelivery及优先级来确定传输对象
     * 思路：设置订阅者的优先级，优先级高的接收事件处理，取消事件，事件无法继续往下传递，达到指定接收者的目的。
     *
     * 【明显priority值越大，越先收到事件或消息。注意：优先级必须针对同一类型的观察者，即ThreadMode相同。】
     * @param twoEvent
     */
    /*
    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 2)
    public void OnMessageBack(MainBusEvent.TwoEvent twoEvent){

        // Cancelling event delivery
        // This method may only be called from inside event handling methods on the posting thread
        //EventBus.getDefault().cancelEventDelivery(twoEvent);
    }
    */

    /**
     * 可以通过传递不同的对象，来指定不同的接收者，接收方法也可以随便命名
     * @param threeEvent
     */
    /*
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 3)
    public void OnMessageASYNC(MainBusEvent.ThreeEvent threeEvent){

    }
    */

    /**********************************************************************************************/
    /**
     *  standard-----------------------------------------------
     *	singleTop 如果实例正好在栈顶，就重用此实例调用 onNewIntent
     *	singTask  如果实例存在于栈中，就重用此实例调用 onNewIntent
     *	singleInstance 在一个独立的栈中创建实例，并让多个应用共享该栈中的实例，
     *	一旦该模式的实例已存在于某个栈中，都会调用就重用此实例调用 onNewIntent
     *
     *	主要用途就是解决已存在的实例，又不创建，又要接收新的参数
     *  @param intent
     */
    @Override
    public void onNewIntent(Intent intent){
        if(debugUI) {
            LogUtils.e(TAG, "onNewIntent");
        }
        setIntent(intent);

        // 可以处理UI......
    }


    /**
     * a、如果是少量数据，可以通过onSaveInstanceState()和onRestoreInstanceState()进行保存与恢复。
     *   Android会在销毁你的Activity之前调用onSaveInstanceState()方法，于是，你可以在此方法中存储关于应用状态的数据。
     *   然后你可以在onCreate()或onRestoreInstanceState()方法中恢复。
     *
     * b、如果是大量数据，使用Fragment保持需要恢复的对象。
     *
     * c1、自已处理配置变化。activity配置这个属性：android:configChanges="orientation|screenSize|keyboardHidden|locale"
     *   这个表示达到条件了就会触发---Changed事件---而调用onConfigurationChanged函数，不再重新调用onCreate方法。
     *   横竖屏切换时（触发了screenSize|orientation事件）不再重新调用onCreate方法，而调用onConfigurationChanged。
     *
     *   竖屏转横屏时只调用一次onConfigurationChanged
     *   横屏变竖屏时会调用两次onConfigurationChanged
     *
     *   自己的应用为窗体应用时，这里可以感应切屏的动作，以作对应的横竖屏布局变换，布局多样；
     *   自己的应用为浮动应用时，没有方法可以感应切屏，布局只能随着应用变换而变换，且布局唯一；
     *
     * c2、也就是当配置发生变化时，不会重新启动Activity。但是会回调此方法，用户自行进行对屏幕旋转后的事情进行处理。
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(debugUI) {
            LogUtils.e(TAG, "onConfigurationChanged: "+newConfig);
        }
        LogUtils.e(TAG, "rotation = " + MYUIUtils.getScreenRotation(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(debugUI) {
            LogUtils.e(TAG, "onBackPressed");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(debugUI) {
            LogUtils.e(TAG, "onWindowFocusChanged");
        }
    }

    /**
     * 来自用户的交互
     * 此方法是activity的方法,当此activity在栈顶时，用户对手机：触屏点击，按home，back，menu键都会触发此方法。
     * 注：下拉statubar,旋转屏幕,锁屏，不会触发此方法.
     * All calls to your activity's {@link #onUserLeaveHint} callback will
     * be accompanied by calls to {@link #onUserInteraction}.
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if(debugUI) {
            LogUtils.e(TAG, "onUserInteraction");
        }
    }

    /**
     * 1. FLAG_ACTIVITY_NO_USER_ACTION也就是带这个标志启动其他Activity的都不调用 onUserLeaveHint()
     * 如果加了下面这行代码的话，A-->B时，就不会回调A的onUserLeaveHint，因为我已经告诉系统，这不是用户的操作
     * intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
     * 2. 启动其它Activity，不带FLAG_ACTIVITY_NO_USER_ACTION标志，（调用onUserLeaveHint）
     * 3. 来电-------------------------------------不属于用户的选择（不调用onUserLeaveHint）
     * 4. 闹钟-------------------------------------神一般存在，（调用onUserLeaveHint）
     * 5. 从眉目滑下点开应用产生覆盖---------------用户的操作，（调用onUserLeaveHint）
     * 6. 按home键---------------------------------用户的操作，（调用onUserLeaveHint）
     * 7. 按最近使用，Activity 进入了后台----------用户的操作，（调用onUserLeaveHint）
     *
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(debugUI) {
            LogUtils.e(TAG, "onUserLeaveHint");
        }
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if(mDetector!=null) {
        //    mDetector.onTouchEvent(event);
        //}
        boolean bool = super.onTouchEvent(event);
        if(debugUI) {
            LogUtils.e(TAG, "onTouchEvent");
        }

        return bool;
    }
    */

    /**
     * 屏幕触摸响应
     * @param event
     * @return
     */
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        //event.getActionMasked()
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            break;
            case MotionEvent.ACTION_UP:
            break;
            case MotionEvent.ACTION_POINTER_DOWN:
            break;
            case MotionEvent.ACTION_POINTER_UP:
            break;
            case MotionEvent.ACTION_MOVE:
            break;
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(debugUI) {
            LogUtils.e(TAG, "onKeyDown");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(debugUI) {
            LogUtils.e(TAG, "onKeyUp");
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(debugUI) {
            LogUtils.e(TAG, "onActivityResult");
        }
    }

    /**
     * 主线程消息处理
     * @param msg
     * @return
     */
    protected boolean handMessage(Message msg){
        return false;
    }

    /**************************************基类提供的其他方法**************************************/
    /**
     * 动态添加fragment
     * commit方法一定要在Activity.onSaveInstance()之前调用。
     *
     * @param containerViewId 此fragment所依附的view
     * @param fragment fragment实体
     */
    protected void addFragment(int containerViewId, Fragment fragment, String tag) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag);
        // 注释掉的话，Fragment就和Activity绑定在一起
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    /**
     * 弹出最上层的事务
     * Pop the top state off the back stack
     */
    protected void popTopFragment(){
        mFragmentManager.popBackStack();
    }

    /**
     * 弹出指定名称Fragment事务之上的事务
     * Pop the top state off the back stack
     */
    protected void popAbovesByFragmentTag(String name){
        mFragmentManager.popBackStack(name, 0);
    }

    /**
     * 弹出指定名称Fragment事务之上的事务，包括自己
     * Pop the top state off the back stack
     */
    protected void popAbovesAndSelfByFragmentTag(String name){
        mFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * 弹出指定名称Fragment事务之上的事务
     * Pop the top state off the back stack
     */
    protected void popAbovesByFragmentId(int id){
        mFragmentManager.popBackStack(id, 0);
    }

    /**
     * 弹出指定名称Fragment事务之上的事务，包括自己
     * Pop the top state off the back stack
     */
    protected void popAbovesAndSelfByFragmentId(int id){
        mFragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * 清空Fragment的栈
     */
    protected void clearFragmentStack(){
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * 取消任务
     * @param task
     */
    protected void cancelTask(MyAsyncTask task){
        // UI结束时，任务没完成，就直接取消
        if(task != null){
            if(task.getStatus() != MyAsyncTask.Status.FINISHED){
                task.cancel(true);
            }
        }
    }

    /**
     * 窗口是否可见
     * @return
     */
    protected boolean isVisible(){
        return !isPaused;
    }

    /**
     * 判断点击是否在某个控件内
     * @param view
     * @param point 点的位置
     * @return
     */
    protected boolean inRangeOfView(View view, Point point){
        int[] location = new int[2];

        //在整个屏幕的位置，要加上状态栏的高度哦
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        if(point.x < x || point.x > (x + view.getWidth()) || point.y < y || point.y > (y + view.getHeight())){
            return false;
        }
        return true;
    }

    /**
     * Can not perform this action after onSaveInstanceState
     */
    public void showDialog(MyDialogFragment.MODE mode, String title, String content, MyDialogFragment.NoticeDialogListener dialogListener){
        if(!isVisible()){
            return;
        }
        MyDialogFragment dialogFragment = MyDialogFragment.newInstance(title, content);
        dialogFragment.setNoticeDialogListener(dialogListener);
        dialogFragment.setTitle(title);
        dialogFragment.setContent(content);
        dialogFragment.setMode(mode);

        // 如果是显示状态，先销毁
        if(dialogFragment.isVisible()){
            dialogFragment.dismiss();
        }
        dialogFragment.show(mFragmentManager, MyDialogFragment.TAG);
    }

    /**
     * 改变overflow菜单的显示方式为【图片+文字】
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 有没有物理键盘都显示菜单
     */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        private String TAG = MyGestureListener.class.getSimpleName();
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    }
    */

}
