package com.yongyida.robot.voice.dao;

import com.yongyida.robot.voice.app.MyApp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MyProvider extends ContentProvider {
	
	private final static String URL = "com.yongyida.robot.voice.master.httprequest";
	private final static String HTTP_REQUEST = "http_request";
	static UriMatcher sUriMatcher;
	static {
	    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	    sUriMatcher.addURI(URL, HTTP_REQUEST, 1);
	}
	
	@Override
	public int delete(Uri uri, String s, String[] as) {

		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
		switch (sUriMatcher.match(uri)) {
		case 1:
			DatabaseOpera dbo = new DatabaseOpera(MyApp.getContext());
			return dbo.query();
		default:
			throw new IllegalArgumentException("unknown uri" + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s,
			String[] as) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
