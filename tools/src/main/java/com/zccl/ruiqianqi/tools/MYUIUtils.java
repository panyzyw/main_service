package com.zccl.ruiqianqi.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by zc on 2015/10/26.
 */
public class MYUIUtils {

    private static String TAG = MYUIUtils.class.getSimpleName();

    private static Toast mToast;
    private static TextView mTextView;

    /**
     * 初始化UI资源
     * @param context
     */
    public static void initMyUI(Context context){
        try {
            //Typeface fontFace = FontUtils.getFontType(context, "fonts/FrLtDFGirl.ttf");
            Typeface fontFace = FontUtils.getFontType(new File("/system/fonts/FrLtDFGirl.ttf"));
            View view = LayoutInflater.from(context).inflate(R.layout.toast_show_text, null);
            mTextView = (TextView) view.findViewById(R.id.tv_show);
            mTextView.setTypeface(fontFace);
            if(mToast == null){
                mToast = new Toast(context);
            }
            mToast.setView(view);
            //mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            mToast.setDuration(Toast.LENGTH_SHORT);
        } catch (Exception e) {

        }
    }

    /**
     * Shows a Toast message.
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        //if(BuildConfig.LOG_DEBUG)
        {
            if (mToast == null) {
                Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
            } else {
                mTextView.setText(msg);
                mToast.show();
            }
        }
    }

    /**
     * inflate(int resource, ViewGroup root, boolean attachToRoot)
     * 1. 如果root为null，attachToRoot将失去作用，设置任何值都没有意义。
     * 2. 如果root不为null，attachToRoot设为true，则会在加载的布局文件的最外层再嵌套一层root布局。
     * (外面会再嵌套一层布局，控件外面必须有一层ViewGroup时，layout_width和layout_height才有用)
     *
     * 3. 如果root不为null，attachToRoot设为false，则root参数失去作用。
     * 4. 在不设置attachToRoot参数的情况下，如果root不为null，attachToRoot参数默认为true。
     *
     * setContentView()方法中，Android会自动在布局文件的最外层再嵌套一个FrameLayout
     *
     * 将布局转换成View
     * @param context
     * @param resource
     * @param root
     * @return
     */
    public static View getLayoutView(Context context, int resource, ViewGroup root){
        //LayoutInflater inflater = LayoutInflater.from(context);
        //inflater = ((Activity)context).getLayoutInflater();
        //inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = inflater.inflate(resource, null);
        View view = View.inflate(context, resource, root);
        return view;
    }

    /**
     * 【状态栏、StatusBar】
     * <item name="android:windowFullscreen">true</item>
     * 【标题栏、ToolBar】
     *  <item name="android:windowActionBar">false</item>
     *  <item name="android:windowNoTitle">true</item>
     * 【导航栏】
     *
     * 如果想只是去除标题栏就后面不用加Fullscreen了，另外，如果想要整个应用都去除标题栏和状态栏，
     * 就把这句代码加到<application>签里面，如果只是想某个activity起作用，这句代码就加到相应的activity上
     * @param activity
     */
    public static void removeStatusTitle(Activity activity){

        // 去掉状态栏
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去除标题栏
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);

        /*

        */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        // 第一次触摸屏幕的时候，无论有没有设置为全屏，系统都会将事件用于显示状态栏（在设置全屏的时候也有显示状态栏的动作，
        // 只是没显示出来而已），而不会去触发我们的监听。
        // 这就是补救方法
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // 强制为横屏
        //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 强制为竖屏
        //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    /**
     * 【状态栏、StatusBar】
     * 【标题栏、ToolBar】
     * 【导航栏】
     *
     1. View.SYSTEM_UI_FLAG_VISIBLE：显示状态栏，Activity不全屏显示(恢复到有状态的正常情况)。
     2. View.INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示。
     3. View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。
     4. View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
     5. View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     6. View.SYSTEM_UI_LAYOUT_FLAGS：               效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     7. View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
     8. View.SYSTEM_UI_FLAG_LOW_PROFILE：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。
     * @param activity
     */
    public static void immersiveMode(Activity activity){

        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        View decorView = activity.getWindow().getDecorView();
        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setImmersive(true);

            flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // 表示会让应用的主体内容占用系统状态栏的空间
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 表示会让应用的主体内容占用系统导航栏的空间
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

            decorView.setSystemUiVisibility(flag);
            params.systemUiVisibility |= flag;
            activity.getWindow().setAttributes(params);
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.setImmersive(true);

            // 第一次触摸屏幕的时候，无论有没有设置为全屏，系统都会将事件用于显示状态栏
            //（在设置全屏的时候也有显示状态栏的动作，只是没显示出来而已），而不会去触发我们的监听。
            // 这就是补救方法
            flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN        // hide status bar
                    ;

            decorView.setSystemUiVisibility(flag);
            params.systemUiVisibility |= flag;
            activity.getWindow().setAttributes(params);
        }
    }





    /**
     *
     * Activity元素中增加这么一个属性：
     * android:screenOrientation="portrait"，则无论手机如何变动一直竖屏显示
     * android:screenOrientation="landscape"，则无论手机如何变动一直横屏显示
     *
     * android:screenOrientation="sensor"，接收传感器事件响应
     * 否则屏幕会响应手机的变动，而产生事件onConfigurationChanged回调
     *
     *
     * 设置屏幕方向适用条例
     * @param orientation
     */
    public static void setScreenOrientation(Activity activity, int orientation){
        // 强制为横屏
        if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        // 强制为竖屏
        else if(orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // 其他的屏幕模式
        else {
            activity.setRequestedOrientation(orientation);
        }

        //默认为-1: SCREEN_ORIENTATION_UNSPECIFIED

        //这个是用户屏幕自适应
        //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        //得到屏幕方向适用条例
        activity.getRequestedOrientation();
    }

    /**
     * 用户屏幕自适应
     * activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
     *
     * user屏幕状态下，获得屏幕的方向
     * @param context
     * @return
     */
    public static int getScreenRotation(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = manager.getDefaultDisplay().getRotation();

        //【0度直接旋转到180度】【90度直接旋转到270度】【不回调onConfigurationChanged】
        // 初始情况。这个时候设备是横屏还是竖屏与硬件设备安装时默认的显示方向有关。
        if(rotation == Surface.ROTATION_0){
            LogUtils.e(TAG, "ROTATION_0");
        }
        // 设置屏幕方向自动旋转后，【逆时针】旋转90
        else if(rotation == Surface.ROTATION_90){
            LogUtils.e(TAG, "ROTATION_90");
        }
        // 设置屏幕方向自动旋转后，【逆时针】旋转180
        else if(rotation == Surface.ROTATION_180){
            LogUtils.e(TAG, "ROTATION_180");
        }
        // 设置屏幕方向自动旋转后，【逆时针】旋转270
        else if(rotation == Surface.ROTATION_270){
            LogUtils.e(TAG, "ROTATION_270");
        }
        return rotation;
    }

    /**
     * 第一种后缀：sw<N>dp,如layout-sw600dp, values-sw600dp
     * 这里的sw代表smallwidth的意思，当你所有屏幕的最小宽度都大于600dp时,屏幕就会自动到带sw600dp后缀的资源文件里去
     * 寻找相关资源文件，这里的最小宽度是指屏幕宽高的较小值，每个屏幕都是固定的，不会随着屏幕横向纵向改变而改变。
     *
     * 第二种后缀w<N>dp 如layout-w600dp, values-w600dp
     * 带这样后缀的资源文件的资源文件制定了屏幕宽度的大于Ndp的情况下使用该资源文件，但它和sw<N>dp不同的是，
     * 当屏幕横向纵向切换时，屏幕的宽度是变化的，以变化后的宽度来与N相比，看是否使用此资源文件下的资源。
     *
     * 第三种后缀h<N>dp 如layout-h600dp, values-h600dp
     * 这个后缀的使用方式和w<N>dp一样，随着屏幕横纵向的变化，屏幕高度也会变化，根据变化后的高度值来判断是否使用h<N>dp ，
     * 但这种方式很少使用，因为屏幕在纵向上通常能够滚动导致长度变化，不像宽度那样基本固定，因为这个方法灵活性不是很好，
     * google官方文档建议尽量少使用这种方式。
     *
     * config.smallestScreenWidthDp
     * 为帮助您针对不同的设备类型确定某些设计，下面提供了一些 常见的屏幕宽度值：
     * 320dp：常见手机屏幕1
     * 360dp：常见手机屏幕2
     * 400dp：小平板
     * 480dp：中间平板电脑，例如 Streak (480x800 mdpi)。
     * 600dp：7 英寸平板电脑 (600x1024 mdpi)。
     * 720dp：10 英寸平板电脑（720x1280 mdpi、800x1280 mdpi 等）。
     *
     * int densityDpi = metric.densityDpi
     * （0.75 /1      / 1.5    / 2      / 3      / 4）
     * 36*36 |48*48   |72*72   |96*96   |144*144 |192*192
     * ldpi  | mdpi   | hdpi   | xhdpi  | xxhdpi | xxxhdpi
     * 0~120 |120~160 |160~240 |240~320 |320~480 |480~640
     * 3     |4       |6       |8       |12      |16
     * 加载图片的内存大小 = 手机dpi/图片所在dpi * 图片所占内存
     * 所以只做一套配图的话，要放在drawable-xxxdpi文件夹中
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static DisplayMetrics getMetrics(Context context){
        //WindowManager window = activity.getWindowManager();
        WindowManager window=((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        Display display = window.getDefaultDisplay();
        DisplayMetrics metric = new DisplayMetrics();

        int flag = 1;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){//----------------4.2及其之上
            flag = 1;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {//---4.0及其之上
            flag = 2;
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//--------------3.0及其之上
            flag = 3;
        }else{//----------------------------------------------------------------------------2.3及其之下
            flag = 3;
        }

        if(flag==1){
            display.getRealMetrics(metric);
            Point wh = new Point();
            display.getRealSize(wh);

        }else if(flag==2){
            display.getMetrics(metric);
            Point wh = new Point();
            display.getSize(wh);

            Method mGetRawH;
            try {
                mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                wh.x = (Integer) mGetRawW.invoke(display);
                wh.y = (Integer) mGetRawH.invoke(display);

                metric.widthPixels = wh.x;
                metric.heightPixels = wh.y;

            } catch (Throwable e) {
                e.printStackTrace();
            }

        }else if(flag==3){
            metric = context.getResources().getDisplayMetrics();
            Point wh = new Point();
            wh.x = metric.widthPixels;
            wh.y = metric.heightPixels;
        }

        // 屏幕密度DPI（120-ldpi / 160-mdpi / 240-hdpi / 320-xhdpi / 480-xxhdpi / 640-xxxhdpi）
        // 每英寸多少个像素，跟硬件相关，可以理解为什么材质的屏，但这并不影响用相同的材质的屏，做不同大小的屏
        int densityDpi = metric.densityDpi;

        // 屏幕密度（0.75 / 1 / 1.5 / 2 / 3 / 4）
        float density = metric.density;

        // 屏幕宽dip值（就是多少DP）
        float xdpi = metric.xdpi;

        // 屏幕高dip值（就是多少DP）
        float ydpi = metric.ydpi;

        // 获取设备的最短边的代码
        Configuration config = context.getResources().getConfiguration();
        int smallestScreenWidth = config.smallestScreenWidthDp;

        LogUtils.e(TAG, "densityDpi="+densityDpi+" density="+density+" smallestScreenWidth="+smallestScreenWidth);
        LogUtils.e(TAG, "xdpi="+xdpi+" ydpi="+ydpi+" widthPixels="+metric.widthPixels+" heightPixels="+metric.heightPixels);

        return metric;
    }

    /**
     * 得到屏幕的绝对宽和高，竖屏时候的值
     * @param context
     * @return
     */
    public static Point getAbsScreenSize(Context context){
        Point wh = new Point();
        // 取手机的绝对宽高
        int screen_w = 0;
        int screen_h = 0;
        DisplayMetrics metric = getMetrics(context);
        metric = context.getResources().getDisplayMetrics();

        // 竖屏
        if(metric.widthPixels < metric.heightPixels){
            screen_w = metric.widthPixels;
            screen_h = metric.heightPixels;
        }
        // 横屏
        else{
            screen_w = metric.heightPixels;
            screen_h = metric.widthPixels;
        }
        wh.set(screen_w, screen_h);
        return wh;
    }

    /**
     * 状态栏的高
     * 刚启动是不能调的，大概100MS之后，才有值
     * @param act
     * @return
     */
    public static int getStatusH(Activity act){
        Rect frame = new Rect();
        // 不包括状态栏，包括标题栏的的VIEW
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    /**
     * 状态栏的高
     * @param context
     * @return
     */
    public static int getStatusH(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 标题栏的高
     * 刚启动是不能调的，大概100MS之后，才有值
     * @param act
     * @return
     */
    public static int getTitleH(Activity act){
        Rect frame = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //不包括状态栏，不包括标题栏的的VIEW
        View v = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        int titleBarHeight = v.getTop() - frame.top;
        titleBarHeight = frame.height() - v.getHeight();
        return titleBarHeight;
    }

    /**
     * DP转PX
     * @param metric
     * @param dipValue
     * @return
     */
    public static int dip2px(DisplayMetrics metric,float dipValue) {
        final float scale = metric.density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * PX转DP
     * @param metric
     * @param pxValue
     * @return
     */
    public static int px2dip(DisplayMetrics metric,float pxValue) {
        final float scale = metric.density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * DP转PX
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * PX转DP
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据名字拿到资源ID
     *
     * strings.xml:
     * <?xml version="1.0" encoding="utf-8"?>
     * <resources>
     *   <string name="hello">Hello World!</string>
     *   <string name="app_name">TestGetIdentifier</string>
     * </resources>
     *
     * int stringId = context.getResources().getIdentifier("hello","string",context.getPackageName());
     * int drawable = context.getResources().getIdentifier("ic_launcher","drawable", context.getPackageName());
     * int drawable = context.getResources().getIdentifier("data001","raw", context.getPackageName());
     * @param context
     * @param name
     * @param defType
     * @return
     */
    public static int getResID(Context context, String name, String defType){
        return context.getResources().getIdentifier(name, defType, context.getPackageName());
    }

    /**
     * 是平板返回true 不是平板返回false
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }



    /**
     * 弹出输入法
     * @param context
     * @param view
     */
    public static void openInputMethod(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);

        // 如果输入法打开则关闭，如果没打开则打开
        //inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭输入法
     * @param context
     * @param view
     */
    public static void closeInputMethod(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 输入法是否是打开的
     * @param context
     * @return
     */
    public static boolean isInputOpen(Context context){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen=imm.isActive();
        return isOpen;
    }

    /**
     * 创建悬浮按钮
     * @param context
     * @param wmParams
     * @param resId
     * @return
     */
    public static Button createFloatView(Context context, WindowManager.LayoutParams wmParams, int resId) {
        Button localMyFloatView = new Button(context);
        localMyFloatView.setBackgroundResource(resId);
        //WindowManager localWindowManager = (WindowManager) context.getSystemService("window");

        // 设置window type 这里是关键，你也可以试试2003
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // wmParams.format = PixelFormat.TRANSPARENT;

        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;

        // 调整悬浮窗显示的停靠位置为右下角
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;


        //DisplayMetrics metrics = getMetrics(context);
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = dip2px(context, 5.0F);
        wmParams.y = dip2px(context, 5.0F);

        // 设置悬浮窗口长宽数据
        // wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.width =  dip2px(context, 100.0F);
        wmParams.height =  dip2px(context, 100.0F);

        // 设置悬浮窗口长宽数据
        // wmParams.width = 200;
        // wmParams.height = 80;

        //localWindowManager.addView(localMyFloatView, wmParams);
        localMyFloatView.setClickable(true);

        //showFloatView(context,localMyFloatView,wmParams);
        return localMyFloatView;
    }

}
