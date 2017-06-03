package com.zccl.ruiqianqi.tools;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.TextView;

import java.io.File;

public class FontUtils {

	/**
	 * 返回字体
	 * @param context
	 * @param assets
     * @return
     */
	public static Typeface getFontType(Context context, String assets){
		return Typeface.createFromAsset(context.getAssets(), assets);
	}

	/**
	 * 返回字体
	 * @param file
	 * @return
	 */
	public static Typeface getFontType(File file){
		return Typeface.createFromFile(file);
	}

	/**
	 * 计算文字宽度1
	 * @param typeface 字体
	 * @param size  字体大小
	 * @param text  文字
	 * @return
	 */
	public static float getFontWidth1(Typeface typeface, float size, String text){
		Paint paint = new Paint();
		if(typeface!=null){
			paint.setTypeface(typeface);
		}
		paint.setTextSize(size);
		return paint.measureText(text);
	}

	/**
	 * 计算文字宽度2
	 * @param typeface 字体
	 * @param size 字体大小
	 * @param str  文字
	 * @return
	 */
	public static int getFontWidth2(Typeface typeface, float size, String str){
		int iRet = 0;
		Paint paint = new Paint();
		if(typeface!=null){
			paint.setTypeface(typeface);
		}
		paint.setTextSize(size);
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
	}

	/**
	 * 计算文字宽高
	 * @param typeface 字体
	 * @param size  字体大小
	 * @param text  文字
	 * @return
	 */
	public static int[] getFontWH(Typeface typeface, float size, String text){
		Paint paint = new Paint();
		if(typeface != null){
			paint.setTypeface(typeface);
		}
		paint.setTextSize(size);
		Rect rect = new Rect(); //返回包围整个字符串的最小的一个Rect区域    
		paint.getTextBounds(text, 0, text.length(), rect);
		int strWidth = rect.width();
		int strHeight = rect.height();
		return new int[]{strWidth, strHeight};
	}

	/**
	 * 计算出该TextView中文字的长度(像素)
	 */
	public static float getTextViewLength(TextView textView, String text){
		TextPaint paint = textView.getPaint();
		// 得到使用该paint写上text的时候,像素为多少
		float textLength = paint.measureText(text);
		return textLength;
	}
}
