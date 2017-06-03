package com.zccl.ruiqianqi.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.zcui.R;

import static com.zccl.ruiqianqi.view.dialog.MyDialogFragment.MODE.DIALOG_CHOICE;
import static com.zccl.ruiqianqi.view.dialog.MyDialogFragment.MODE.DIALOG_TIPS;
import static com.zccl.ruiqianqi.view.dialog.MyDialogFragment.MODE.FRAGMENT_TIPS;

/**
 * Created by zc on 2015/10/28.
 */
public class MyDialogFragment extends BaseCompatDialogFragment implements
        View.OnClickListener{

    // 类标志
    public static final String TAG = MyDialogFragment.class.getSimpleName();

    public enum MODE{
        DIALOG_TIPS,
        DIALOG_CHOICE,
        FRAGMENT_TIPS,
        FRAGMENT_CHOICE,
    }
    private MODE mode = MODE.FRAGMENT_CHOICE;

    private NoticeDialogListener mListener;

    private TextView dialog_title;
    private TextView dialog_content;

    private LinearLayout dialog_choice;
    private Button dialog_btn_sure;
    private Button dialog_btn_cancel;

    private LinearLayout dialog_tips;
    private Button dialog_btn_sure2;

    /** 小小的微调 */
    private int padding = 0;

    public MyDialogFragment() {
        super();
    }

    /**
     * 这他妈是一个独立的对象
     * @param title
     * @param content
     * @return
     */
    public static MyDialogFragment newInstance(String title, String content) {
        MyDialogFragment mDialogFragment = new MyDialogFragment();
        mDialogFragment.mTitle = title;
        mDialogFragment.mContent = content;

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, content);
        mDialogFragment.setArguments(args);

        return mDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_PARAM1);
            mContent = getArguments().getString(ARG_PARAM2);
        }

        padding = MYUIUtils.dip2px(getActivity(), 1);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if(mode==DIALOG_TIPS) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setMessage(mContent)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(mListener!=null) {
                                mListener.onDialogPositiveClick();
                            }
                        }
                    })
                    .setCancelable(true);
            dialog = builder.create();
        }else if(mode==DIALOG_CHOICE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setMessage(mContent)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                            if(mListener!=null) {
                                mListener.onDialogPositiveClick();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog.cancel();
                            if(mListener!=null) {
                                mListener.onDialogNegativeClick();
                            }
                        }
                    })
                    .setCancelable(true);
            dialog = builder.create();
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(mode.ordinal() >= FRAGMENT_TIPS.ordinal()) {
            view = inflater.inflate(R.layout.dialog_fragment, container);
            dialog_title = (TextView) view.findViewById(R.id.dialog_title);
            dialog_content = (TextView) view.findViewById(R.id.dialog_content);
            if (!StringUtils.isEmpty(mTitle)) {
                dialog_title.setText(mTitle);
            }
            if (!StringUtils.isEmpty(mContent)) {
                dialog_content.setText(mContent);
            }

            dialog_tips = (LinearLayout) view.findViewById(R.id.dialog_tips);
            dialog_choice = (LinearLayout) view.findViewById(R.id.dialog_choice);

            if (mode == FRAGMENT_TIPS) {
                dialog_btn_sure2 = (Button) view.findViewById(R.id.dialog_btn_sure2);
                dialog_btn_sure2.setOnClickListener(this);
                dialog_choice.setVisibility(View.GONE);
                dialog_tips.setVisibility(View.VISIBLE);
            } else if (mode == MODE.FRAGMENT_CHOICE) {
                dialog_btn_sure = (Button) view.findViewById(R.id.dialog_btn_sure);
                dialog_btn_cancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
                dialog_btn_sure.setOnClickListener(this);
                dialog_btn_cancel.setOnClickListener(this);
                dialog_choice.setVisibility(View.VISIBLE);
                dialog_tips.setVisibility(View.GONE);
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //横屏
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mWindow.setLayout(mDisplayMetrics.widthPixels / 2, mDisplayMetrics.heightPixels / 2);
        }
        //竖屏
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mWindow.setLayout(mDisplayMetrics.widthPixels , mDisplayMetrics.heightPixels / 3);
        }

    }

    @Override
    public void onClick(View v) {
        if(v==dialog_btn_sure){
            dismiss();
            if(mListener!=null) {
                mListener.onDialogPositiveClick();
            }
        }else if(v==dialog_btn_cancel){
            dismiss();
            if(mListener!=null) {
                mListener.onDialogNegativeClick();
            }
        }else if(v==dialog_btn_sure2){
            dismiss();
            if(mListener!=null) {
                mListener.onDialogPositiveClick();
            }
        }
    }

    /**
     * 设置显示模式
     * @param mode
     */
    public void setMode(MODE mode){
        this.mode = mode;
    }

    /**********************************************************************************************/
    /**
     * 设置按键响应接口
     * @param mListener
     */
    public void setNoticeDialogListener(NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 按键回调接口
     */
    public interface NoticeDialogListener {
        void onDialogPositiveClick();
        void onDialogNegativeClick();
    }
}
