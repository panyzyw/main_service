package com.yongyida.robot.voice.robot;

import org.json.JSONObject;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.frame.iflytek.CommVoiceParse;
import com.yongyida.robot.voice.frame.iflytek.VoiceRead;

public class CmdPush extends BaseCmd {

	// private JSONObject obJson;

	@Override
	public void execute() {
		robot = RobotInfo.getInstance();
		if (robot.getContrallState().equals(RobotStateData.STATE_CONTRALL)) {
			try {

				String cmdJson = json.optString("command");
				if ("".equals(cmdJson)) {
					return;
				}
				cmdJson = cmdJson.trim();
				JSONObject obJson = new JSONObject(cmdJson);

				String strCmd = obJson.optString("cmd");

				if ("".equals(strCmd)) {
					return;
				}

				strCmd = strCmd.trim();
				
				
				//临时修改
//				if (strCmd.equals("talk")) {
//
//					CommVoiceParse factory = VoiceRead.getInstence(context);
//					factory.setWords(obJson.optString("type"));
//					factory.start();
//
//				} else {
//
//					Class<? extends BaseCmd> obj = messageMap.get(strCmd);
//					if (obj != null) {
//						BaseCmd baseCmd = obj.newInstance();
//						if (baseCmd != null) {
//							baseCmd.execute();
//						}
//					}
//				}
				
				//临时修改
				Class<? extends BaseCmd> obj = messageMap.get(strCmd);
				if (obj != null) {
					BaseCmd baseCmd = obj.newInstance();
					if (baseCmd != null) {
						baseCmd.execute();
					}
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

}
