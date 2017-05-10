package com.zccl.ruiqianqi.tools.anim;

import android.widget.ImageView;

/**
 * Created by ruiqianqi on 2016/7/29 0029.
 */
public class FrameAnimation {

    /** 需要播放动画的控件 */
    private ImageView mImageView;
    /** 播放的序列图片 */
    private int[] mFrameRes;
    /** 每个序列帧的延时时间 */
    private int[] mDurations;
    /** 统一的延时时间 */
    private int mDuration;
    /** 最后的序列帧编号 */
    private int mLastFrameNo;
    /** 一个周期播放完了之后的延时 */
    private long mBreakDelay;
    /** 是否停止了 */
    private boolean isStop = true;

    /**
     *
     * @param pImageView 需要播放动画的控件
     * @param pFrameRes  播放的序列图片编号数组
     * @param pDurations 每个序列帧的延时时间
     */
    public FrameAnimation(ImageView pImageView, int[] pFrameRes,
                          int[] pDurations) {
        mImageView = pImageView;
        mFrameRes = pFrameRes;
        mDurations = pDurations;
        mLastFrameNo = pFrameRes.length - 1;
        mImageView.setBackgroundResource(mFrameRes[0]);
        play(1);
    }

    /**
     *
     * @param pImageView 需要播放动画的控件
     * @param pFrameRes  播放的序列图片编号数组
     * @param pDuration  统一的延时时间
     */
    public FrameAnimation(ImageView pImageView, int[] pFrameRes, int pDuration) {
        mImageView = pImageView;
        mFrameRes = pFrameRes;
        mDuration = pDuration;
        mLastFrameNo = pFrameRes.length - 1;
        mBreakDelay = 0;
        mImageView.setBackgroundResource(mFrameRes[0]);
        playConstant(1);
    }

    /**
     *
     * @param pImageView  需要播放动画的控件
     * @param pFrameRes   播放的序列图片编号数组
     * @param pDuration   统一的延时时间
     * @param pBreakDelay 一个周期播放完了之后的延时
     */
    public FrameAnimation(ImageView pImageView, int[] pFrameRes,
                          int pDuration, long pBreakDelay) {
        mImageView = pImageView;
        mFrameRes = pFrameRes;
        mDuration = pDuration;
        mLastFrameNo = pFrameRes.length - 1;
        mBreakDelay = pBreakDelay;

        mImageView.setBackgroundResource(mFrameRes[0]);
        playConstant(1);
    }

    /**
     * 开始播放序列帧，每个都有自己的延时时间
     * @param pFrameNo
     */
    private void play(final int pFrameNo) {
        isStop = false;
        mImageView.postDelayed(new Runnable() {
            public void run() {
                if(!isStop) {
                    mImageView.setBackgroundResource(mFrameRes[pFrameNo]);
                    if (pFrameNo == mLastFrameNo) {
                        play(0);
                    } else {
                        play(pFrameNo + 1);
                    }
                }
            }
        }, mDurations[pFrameNo]);
    }

    /**
     * 开始播放序列帧，用统一的延时时间
     * @param pFrameNo
     */
    private void playConstant(final int pFrameNo) {
        isStop = false;
        mImageView.postDelayed(new Runnable() {
            public void run() {
                if(!isStop) {
                    mImageView.setBackgroundResource(mFrameRes[pFrameNo]);
                    if (pFrameNo == mLastFrameNo) {
                        playConstant(0);
                    } else {
                        playConstant(pFrameNo + 1);
                    }
                }
            }
        }, pFrameNo == mLastFrameNo && mBreakDelay > 0 ? mBreakDelay : mDuration);
    }

    /**
     * 停止播放
     */
    public void stop(){
        isStop = true;
    }
}

