package com.yongyida.robot.voice.robot;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.RobotInfo;

/**
 * app帮机器人改名.
 * 
 * @author Administrator
 *
 */
public class CmdFlush extends BaseCmd {

	@Override
	public void execute() {

		try {
			robot = RobotInfo.getInstance();
			String ret = json.optString("ret", "0");

			if (ret != null && !ret.equals("")) {
				ret = ret.trim();
			}

			if (ret.equals("0")) {
				String str = json.optString("Robot");
				if ("".equals(str)) {
					return;
				}
				str = str.trim();
				JSONObject obj = new JSONObject(str);
				String name = obj.optString("rname");
				if (name != null && !name.equals("")) {
					name = name.trim();
					robot.setName(name);
					Uri uriName = Uri.parse("content://com.yongyida.robot.nameprovider//name");
					ContentResolver resolver = context.getContentResolver();
					ContentValues values = new ContentValues();
					values.put("name", name);
					resolver.update(uriName, values, null, null);
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
