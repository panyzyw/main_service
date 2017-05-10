package com.zccl.ruiqianqi.mind.voice.iflytek.beans;

import com.google.gson.annotations.SerializedName;

public class OperaBean extends BaseInfo{

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

		// 如果指明了歌手
		@SerializedName("artist")
		public String artist;

		// 如果指明了歌曲
		@SerializedName("song")
		public String song;

		// 戏剧种类
		@SerializedName("category")
		public String category;

	}

}
