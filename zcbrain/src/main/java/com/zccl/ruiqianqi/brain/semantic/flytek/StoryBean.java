package com.zccl.ruiqianqi.brain.semantic.flytek;

import com.google.gson.annotations.SerializedName;

public class StoryBean extends BaseInfo{

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

	// 定义信息
	public class Slots {

		// 没有其他信息，就一个
		@SerializedName("department")
		public String department;

		// 如果指明了故事，如果返回了这个字段，说明有这个故事
		// 如果没有返回这个字段，说明服务器没有这个故事
		@SerializedName("name")
		public String name;

		// 如果指明了故事类型
		@SerializedName("type")
		public String type;
	}

}
