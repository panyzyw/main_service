package com.zccl.ruiqianqi.brain.semantic.flytek;

import com.google.gson.annotations.SerializedName;

import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MOVE;

public class VideoBean extends BaseInfo{

	public VideoBean(){
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
		@SerializedName("action")
		public String action;

		// 指明了机器人移动的类型
		@SerializedName("value")
		public String value;
	}

}
