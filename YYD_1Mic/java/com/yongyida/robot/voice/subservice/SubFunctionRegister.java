package com.yongyida.robot.voice.subservice;

import com.yongyida.robot.voice.subservice.subimpl.SubForward;
import com.yongyida.robot.voice.subservice.subimpl.SubAddFriends;
import com.yongyida.robot.voice.subservice.subimpl.SubGame;
import com.yongyida.robot.voice.subservice.subimpl.SubMove;
import com.yongyida.robot.voice.subservice.subimpl.SubRemoveFriends;
import com.yongyida.robot.voice.subservice.subimpl.SubSms;

public class SubFunctionRegister extends SubFunction{

	public static void register(){
		
		subContext.put("game", SubGame.class);
		subContext.put("move", SubMove.class);
		subContext.put("sms", SubSms.class);
		subContext.put("/media/friend/add", SubAddFriends.class);
		subContext.put("/media/friend/remove", SubRemoveFriends.class);
		
		
//		subContext.put("/media/invite", SubForward.class);
//		subContext.put("/media/reply", SubForward.class);
//		subContext.put("/media/cancel", SubForward.class);
		subContext.put("/avm/invite", SubForward.class);
		subContext.put("/avm/invite/reply", SubForward.class);
		subContext.put("/avm/invite/cancel", SubForward.class);
		subContext.put("/media/meeting/report", SubForward.class);	
	}
}
