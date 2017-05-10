package com.yongyida.robot.voice.bean;

import com.google.gson.annotations.SerializedName;

public class MoveDataInfo {

	@SerializedName("semantic")
	public Semantic semantic;
	
	
	
	public Semantic getSemantic() {
		return semantic;
	}

	public void setSemantic(Semantic semantic) {
		this.semantic = semantic;
	}
	
	public class Semantic{
		@SerializedName("slots")
		private Slots slots;

		public Slots getSlots() {
			return slots;
		}

		public void setSlots(Slots slots) {
			this.slots = slots;
		}
		
	}
	
	public class Slots{
		@SerializedName("direct")
		private String direct;

		public String getDirect() {
			return direct;
		}

		public void setDirect(String direct) {
			this.direct = direct;
		}
		
		
	}
}
