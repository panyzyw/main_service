package com.zccl.ruiqianqi.mind.voice.iflytek.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicBean extends BaseInfo{

	// 语义
	@SerializedName("semantic")
	public Semantic semantic;

	// 数据
	//@SerializedName("data")
	//public Data data;

	/****************************************【Semantic】******************************************/
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
		@SerializedName("default")
		public String mDefault;

		// 如果指明了歌手
		@SerializedName("artist")
		public String artist;

		// 如果指明了歌曲
		@SerializedName("song")
		public String song;

		// 如果指明了种类，高兴，悲伤
		@SerializedName("category")
		public String category;
	}

	/****************************************【Data】**********************************************/
	// 定义数据，如果有的话
	public class Data{
		@SerializedName("result")
		public List<Result> result;
	}

	// 歌手的歌曲信息
	public class Result{
		// 歌曲名字
		@SerializedName("singer")
		public String singer;

		// 自产音乐
		@SerializedName("sourceName")
		public String sourceName;

		// 歌名
		@SerializedName("name")
		public String name;

		// 下载地址
		@SerializedName("downloadUrl")
		public String downloadUrl;
	}
}
