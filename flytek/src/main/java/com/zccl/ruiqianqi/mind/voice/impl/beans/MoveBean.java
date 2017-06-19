package com.zccl.ruiqianqi.mind.voice.impl.beans;

import com.google.gson.annotations.SerializedName;

import static com.zccl.ruiqianqi.mind.voice.impl.function.FuncType.FUNC_MOVE;

public class MoveBean extends BaseInfo{

	public MoveBean(){
		mServiceType = FUNC_MOVE;
	}

	// 语义
	@SerializedName("semantic")
	public Semantic semantic;

	// 定义语义
	public class Semantic {
		// 没有对应的值

		// 有对应的值
		@SerializedName("slots")
		public Slots slots;
	}

	// 定义动作
	public class Slots {

		// 指明了机器人移动的类型
		@SerializedName("direct")
		public String direct;

	}

}
