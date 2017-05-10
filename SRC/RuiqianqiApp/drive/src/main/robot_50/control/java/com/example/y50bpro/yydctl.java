package com.example.y50bpro;

public class yydctl {

	static {
		System.loadLibrary("yydctl");
	}
	public static native int LedDanceCtl(int val); //1:on  		 0:off
	public static native int Reset5Mic();			  //1:success    0:fail

}
