package com.yongyida.robot.voice.bean;

public class SmsInfo {

	// _id：短信序号，如100 　　
	// * 　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的 　　
	// * 　　address：发件人地址，即手机号，如+8613811810000 　　
	// * 　　person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null 　　
	// * 　　date：日期，long型，如1256539465022，可以对日期显示格式进行设置 　　
	// * 　　protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
	// * 　　read：是否阅读0未读，1已读 　　
	// * 　　status：短信状态-1接收，0complete,64pending,128failed 　　
	// * 　　type：短信类型1是接收到的，2是已发出 　　 　　
	// * 　　body：短信具体内容 　　
	// * 　　service_center：短信服务中心号码编号，如+8613800755500

	private Long _id;
	
	/**
	 * 短信内容
	 */
	private String body;

	/**
	 * 阅读状态 0未读，1已读
	 */
	private String read;

	/**
	 * 发送短信的电话号码
	 */
	private String address;

	/**
	 * 发送短信的日期和时间
	 */
	private String date;

	/**
	 * 发送短信人的姓名
	 */
	private String person;

	/**
	 * 短信类型1是接收到的，2是已发出
	 */
	private String type;

	public String getSmsbody() {
		return body;
	}

	public void setSmsbody(String smsbody) {
		this.body = smsbody;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		// return "SmsInfo [smsbody=" + smsbody + ", phoneNumber=" + phoneNumber
		// + ", name=" + name + "]";
		if (person.equals("0"))
			return "发送人：" + address + "。" + body;
		else
			return "发送人：" + person + "。" + body;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

}
