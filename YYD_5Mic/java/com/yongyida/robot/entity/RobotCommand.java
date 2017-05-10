/**
 * Copyright (C) 2015 Zhensheng Yongyida Robot Co.,Ltd. All rights reserved.
 *
 * @author: hujianfeng@yongyida.com
 * @version 0.1
 * @date 2015-09-01
 *
 */
package com.yongyida.robot.entity;

/**
 * 机器人控制命令
 * 命令格式：{"command":{"cmd":"move","type":"up","param":"45"}}
 *
 */
public class RobotCommand {
	public String cmd;
	public Command command;

	public RobotCommand() {
		command = new Command();
	}

	public class Command {
		public String cmd;
		public String type;
		public int param;
	}
}
