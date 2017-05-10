package com.yongyida.robot.voice.frame.socket.localscket;


public class Data {

	/**
	 * 
	 * 打包发送数据.
	 * 
	 * @param output
	 * @param offset
	 * @param data
	 * @param length
	 */
	public void packData(byte[] output, String data, int offset, int length) {

		// byte[] buffer = new byte[length + 20];
		byte[] jsonBuffer = data.getBytes();

		byte[] allLen = intToByte(length + 8);
		byte[] jsonLen = intToByte(length);

		output[0] = 2;
		output[8] = allLen[0];
		output[9] = allLen[1];
		output[10] = allLen[2];
		output[11] = allLen[3];

		output[12] = jsonLen[0];
		output[13] = jsonLen[1];
		output[14] = jsonLen[2];
		output[15] = jsonLen[3];

		System.arraycopy(jsonBuffer, offset, output, 16, length);

	}

	/**
	 * 将int转换成byte
	 * 
	 * @param num
	 * @return
	 */
	private byte[] intToByte(int num) {

		byte[] b = new byte[4];
		b[3] = (byte) (num & 0xff);
		b[2] = (byte) (num >> 8 & 0xff);
		b[1] = (byte) (num >> 16 & 0xff);
		b[0] = (byte) (num >> 24 & 0xff);

		return b;
	}
}
