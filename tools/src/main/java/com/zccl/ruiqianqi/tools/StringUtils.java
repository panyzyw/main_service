package com.zccl.ruiqianqi.tools;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StringUtils {
	
	/**
	 * 检查字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(TextUtils.isEmpty(str)){
			return true;
		}else{
			str = str.trim();
			if(str.length()==0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 截取字符串
	 * @param str
	 * @param length
	 * @return
	 */
	public static String substring(String str, int length){
		if (isEmpty(str)) {
			return null;
		}
		
		if (length<=0 || str.length()<=length) {
			return str;
		}
		return str.substring(0, length);
	}
	
	/**
	 * 按照指定的格式，输出对象字符串化的对象
	 * 
	 * 占位符可以使用0和#两种，当使用0的时候会严格按照样式来进行匹配，不够的时候会补0，
	 * 而使用#时会将前后的0进行忽略  
	 * @param format 0000 ###
	 * @param obj
	 * @return
	 */
	public static String obj2FormatStr(String format, Object obj){
		try {
			DecimalFormat df = new DecimalFormat(format); 
			return df.format(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保留小数点后一位
	 * @param value
	 * @return
     */
	public static String pointOne(float value) {
		DecimalFormat df = new DecimalFormat("#.0");
		return df.format(value);
	}

	/**
	 * 保留小数点后两位
	 * @param value
	 * @return
     */
	public static String pointTwo(float value) {
		return String.format("%.2f", value);
	}

	/**
	 * 1、转换符
	 * %s: 字符串类型，如："ljq"
	 * %b: 布尔类型，如：true
	 * %d: 整数类型(十进制)，如：99
	 * %f: 浮点类型，如：99.99
	 * %%: 百分比类型，如：%
	 * %n: 换行符
	 *
	 * 2、常见日期时间格式化
	 * tc: 包括全部日期和时间信息 星期六 十月 27 14:21:20 CST 2007
	 * tF: "年-月-日"格式，如：2007-10-27
	 * tD: "月/日/年"格式，如：10/27/07
	 * tr: "HH:MM:SS PM"格式(12时制)，如：02:25:51 下午
	 * tT: "HH:MM:SS"格式(24时制)，如：14:28:16
	 * tR: "HH:MM"格式(24时制)，如：14:28
	 * 
	 * 3、格式化日期字符串
	 * b或者h: 月份简称，如
	 * 中：十月
	 * 英：Oct
	 * 
	 * B: 月份全称，如
	 * 中：十月
	 * 英：October
	 *	  
	 * a: 星期的简称，如
	 * 中：星期六
	 * 英：Sat
	 *	  
	 * A: 星期的全称，如：
	 * 中：星期六
	 * 英：Saturday
	 *	 
	 * C: 年的前两位数字(不足两位前面补0)，如：20
	 * y: 年的后两位数字(不足两位前面补0)，如：07
	 * Y: 4位数字的年份(不足4位前面补0)，如：2007
	 * j: 一年中的天数(即年的第几天)，如：300 
	 * m: 两位数字的月份(不足两位前面补0)，如：10
	 * d: 两位数字的日(不足两位前面补0)，如：27
	 * e: 月份的日(前面不补0)，如：5
	 *	
	 * 4、格式化时间字符串
	 * H: 2位数字24时制的小时(不足2位前面补0)，如：15
	 * I: 2位数字12时制的小时(不足2位前面补0)，如：03
	 * k: 2位数字24时制的小时(前面不补0)，如：15
	 * l: 2位数字12时制的小时(前面不补0)，如：3
	 * M: 2位数字的分钟(不足2位前面补0)，如：03
	 * S: 2位数字的秒(不足2位前面补0)，如：09
	 * L: 3位数字的毫秒(不足3位前面补0)，如：015
	 * N: 9位数字的毫秒数(不足9位前面补0)，如：562000000
	 *	 
	 * p: 小写字母的上午或下午标记，如：
	 * 中：下午
	 * 英：pm
	 *	  
	 * z: 相对于GMT的RFC822时区的偏移量，如：+0800
	 * Z: 时区缩写字符串，如：CST
	 * s: 1970-1-1 00:00:00 到现在所经过的秒数，如：1193468128
	 * Q: 1970-1-1 00:00:00 到现在所经过的毫秒数，如：1193468128984
	 * @return
	 */
	public static String formatTime(){
		// tF: "年-月-日"格式，如：2007-10-27
		return String.format("%tF", new Date());
	}

	/**
	 * 时长格式化显示
	 */
	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = totalSeconds / 60;
		return minutes > 99 ? String.format("%d:%02d", minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
	}


	/**
	 * 获取系统当前时间
	 *
	 SimpleDateFormat函数语法：

	 G 年代标志符
	 y 年
	 M 月
	 d 日
	 h 时 在上午或下午 (1~12)
	 H 时 在一天中 (0~23)
	 m 分
	 s 秒
	 S 毫秒
	 E 星期
	 D 一年中的第几天
	 F 一月中第几个星期几
	 w 一年中第几个星期
	 W 一月中第几个星期
	 a 上午 / 下午 标记符
	 k 时 在一天中 (1~24)
	 K 时 在上午或下午 (0~11)
	 z 时区
	 */
	public static String getDate() {
		Calendar ca = Calendar.getInstance();
		DateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
		//ca.getTime().toLocaleString();
		return matter.format(ca.getTime());
	}

	/**
	 * 取开年时间戳【设置成一月】
	 * @return
	 */
	public static long getYearBeginTime(){
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.MONTH, Calendar.JANUARY);
		return ca.getTimeInMillis();
	}

	/**
	 * 取开月时间戳【设置成当月】
	 * @return
	 */
	public static long getMonthBeginTime(){
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.MONTH, ca.get(Calendar.MONTH));
		return ca.getTimeInMillis();
	}

	/**
	 * 得到可订票日期
	 * @return
	 */
	public static String getOrderTime(){
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		long myTime = (date.getTime() / 1000) + 19 * 24 * 60 * 60;
		date.setTime(myTime * 1000);
		String datestring = format2.format(date);
		return datestring;
	}
}
