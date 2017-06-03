package com.zccl.ruiqianqi.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Process.exitValue() 采用非阻塞的方式返回，如果没有立即拿到返回值，则抛出异常
 * Process.waitFor() 当前线程等待，如有必要，一直要等到由该 Process 对象表示的进程已经终止。
 * 但是如果我们在调用此方法时，如果不注意的话，很容易出现主线程阻塞，Process也挂起的情况。
 * 在调用waitFor() 的时候，Process需要向主线程汇报运行状况，所以要注意清空缓存区，
 * 即InputStream和ErrorStream，在网上，很多只提到处理InputStream，忽略了ErrorStream。
 */
public class ShellUtils {

	private final static int kSystemRootStateUnknow = -1;
	private final static int kSystemRootStateDisable = 0;
	private final static int kSystemRootStateEnable = 1;
	private static int systemRootState = kSystemRootStateUnknow;

	/** \link #root()\endlink后的父进程的标准输入 */
	//private static DataOutputStream dos;

	/** \link #root()\endlink后的进程 */
	//private static Process process;
	
	
	/**
	 * 手机root检测,手机是否已Root（不弹框）
	 * @return
	 */
	public static boolean isRootSystem() {
		if (systemRootState == kSystemRootStateEnable) {
			return true;
		} else if (systemRootState == kSystemRootStateDisable) {
			return false;
		}
		File f = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
				"/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					systemRootState = kSystemRootStateEnable;
					return true;
				}
			}
		} catch (Exception e) {
		}
		systemRootState = kSystemRootStateDisable;
		return false;
	}

	/**
	 * 【返回高级的Root之Process，还有授权验证】
	 *
	 * 他妈的不知道干嘛的，把错误流重定向到了无底洞中
	 * （0、1和2分别表示标准输入、标准输出和标准错误信息输出）
	 * 1>/dev/null 首先表示标准输出重定向到空设备文件，也就是不输出任何信息到终端
	 * 2>&1 标准错误输出重定向 到 标准输出，因为之前标准输出已经重定向到了空设备文件，所以标准错误输出也重定向到空设备文件
	 * command > file  2>file 后面这个是附加条件，如果有错误输出，也输出到file文件，两个管道都打开file文件
	 * command > file  2>&1   后面这个是附加条件，如果有错误输出，被重定向到标准输出流，只有标准输出流占用file文件
	 *
	 * 小结，使用readLine()一定要注意：
	 * 读入的数据要注意有/r或/n或/r/n
	 * 没有数据时会阻塞，在数据流异常或断开时才会返回null
	 * 使用socket之类的数据流时，要避免使用readLine()，以免为了等待一个换行/回车符而一直阻塞
	 */
	public static Process isAcesssGiven() {
		try {
			ProcessBuilder lpr = new ProcessBuilder(new String[] { "su" });
			lpr.redirectErrorStream(true);
			Process localProcess = lpr.start();
			DataOutputStream localout = new DataOutputStream(localProcess.getOutputStream());

			//id命令一般返回uid=0(root) gid=0(root)
			localout.writeBytes("id 2>/dev/null \n");
			localout.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
			boolean bool = false;
			do {
				String str = br.readLine();
				if (str == null)
					break;
				bool = str.contains("uid=0");
			} while (!bool);

			if(bool){
				return localProcess;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	/**
	 * 【返回一般的Root之Process，没有授权验证】
	 *
	 * 指令-----与----参数
	 * su------process.getOutputStream()------>各种指令【因为su指令比较特殊，它的参数就是各种指令】
	 * 指令-----process.getOutputStream()------>该指令需要的参数
	 *
	 * @return
	 */
	public static Process getRootProcess() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"su"});
			processBuilder.redirectErrorStream(true);
			return processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 拿到运行命令Process
	 * 【列表中第一个参数是可执行命令程序，其他的是命令行执行是需要的参数。】
	 *
	 * @param cmdS 空------返回null
	 *                【这种参数什么也不传表示有值，但length====0】
	 *
	 * 一种：【Runtime.getRuntime()和ProcessBuilder】
	 * process = Runtime.getRuntime().exec(cmds[0]);
	 * DataOutputStream os = new DataOutputStream(process.getOutputStream());
	 * for (int i = 1; i < cmds.length; i++) {
	 * 	   os.writeBytes(cmds[i] +"\n");
	 * }
	 * process = new ProcessBuilder(cmds[0]);
	 * DataOutputStream os = new DataOutputStream(process.getOutputStream());
	 * for (int i = 1; i < cmds.length; i++) {
	 * 	   os.writeBytes(cmds[i] +"\n");
	 * }
	 *
	 * 二种：【Runtime.getRuntime()和ProcessBuilder】
	 * process = Runtime.getRuntime().exec(new String[]{"chmod", "777", MyConfigure.RESROOT});
	 * process = new ProcessBuilder("chmod", "777", MyConfigure.RESROOT);
	 *
	 * 三种：【Runtime.getRuntime()】
	 * process = Runtime.getRuntime().exec("chmod 777 "+MyConfigure.RESROOT);
	 * @return
	 */
	public static Process getProcess(String... cmdS) {
		try {
			if(cmdS==null || (cmdS!=null && cmdS.length==0)){
				return null;
			}else{
				Process process = new ProcessBuilder(cmdS).redirectErrorStream(true).start();
				return process;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * ROOT权限下执行多条命令
	 *
	 * 当作su的参数写进去的，写入的指令必须返回，下面的指令才能得到运行；
	 * 如果一进去就是无限循环，那后面的指令就无法运行了，循环运行的程序写到最后一项。
	 *
	 * @param cmds 一项就是一条指令
	 * @return
	 */
	public static void runRootComm(Process process, List<String> cmds) {
		DataOutputStream os = null;
		boolean dismiss = false;
		try {

			//局部变量，需要销毁
			if(process == null){
				process = getRootProcess();
				dismiss = true;
			}
			os = new DataOutputStream(process.getOutputStream());

			for(String cmd : cmds){
				os.writeBytes(cmd +"\n");
			}
			os.writeBytes("exit\n");
			os.flush();


			//这表示程序正在运行中，有标准输出，在这里可以接收。。。
			{
				int len = -1;
				StringBuffer ret = new StringBuffer();
				//获取返回内容
				InputStream inputStream = process.getInputStream();
				DataInputStream dataInputStream = new DataInputStream(inputStream);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];

				LogUtils.e("runRootComm", "批量运行开始!");

				while ((len = dataInputStream.read(buf)) > -1) {
					baos.write(buf, 0, len);
					String line = new String(baos.toByteArray(), "utf-8");
					baos.reset();
					//ret.append(line);

					LogUtils.e("runRootComm", "批量标准输出="+line);
				}
				inputStream.close();
				dataInputStream.close();
				baos.close();

				ret.delete(0, ret.length());
				ret.setLength(0);
			}

			//这个值就是main的返回值，及exit(n)n的值，
			//所以程序中你写几，就是几，一般0当作正常返回值。
			int exitValue = process.waitFor();

			LogUtils.e("runRootComm", "批量运行结束="+exitValue);
			if(exitValue==0){

			}
			else if(exitValue!=0){
				//获取错误提示
				InputStream inputis = process.getErrorStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputis));
				String line = null;
				while ((line = bufferedReader.readLine()) != null){
					LogUtils.e("runRootComm", "批量错误输出=" + line);
				}
				inputis.close();
				bufferedReader.close();
			}

		} catch (Exception e){
			e.printStackTrace();

		} finally {

			//如果是局部变量，全部销毁
			if(dismiss){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				process.destroy();
			}

		}
	}


	/**
	 * 只有命令才能拿到root权限，才能执行需要root权限的操作，
	 * 运行单个指令，并监听标准输出，指令当作su的参数写进去，
	 * 所有的指令必须加上exit，不然不会结束。
	 * @param command
	 * @return
	 */
	public static boolean runRootComm(Process process, String command){
		DataOutputStream os = null;
		boolean dismiss = false;
		try {

			//如果是局部变量，要销毁
			if(process == null){
				process = getRootProcess();
				dismiss = true;
			}

			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command+"\n");
			os.writeBytes("exit\n");
			os.flush();

			int len = -1;
			//获取返回内容
			InputStream inputStream = process.getInputStream();
			DataInputStream dataInputStream = new DataInputStream(inputStream);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];

			LogUtils.e("runRootComm", "运行开始="+command);

			while ((len = dataInputStream.read(buf)) > -1) {
				baos.write(buf, 0, len);
				String line = new String(baos.toByteArray(),"utf-8");
				baos.reset();

				LogUtils.e("runRootComm", "标准输出=" + line);
			}

			//这个值就是main的返回值，及exit(n)n的值，
			//所以程序中你写几，就是几，一般0当作正常返回值。
			int exitValue = process.waitFor();

			LogUtils.e("runRootComm", "运行结束="+exitValue);

			if (exitValue == 0){
				return true;
			} else {

				//获取错误提示
				InputStream inputis = process.getErrorStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputis));
				String line = null;
				while ((line = bufferedReader.readLine()) != null){
					LogUtils.e("runRootComm", "错误输出=" + line);
				}
				inputis.close();
				bufferedReader.close();

				return false;
			}

		} catch (Exception e){
			return false;

		} finally{

			//如果是局部变量，要销毁
			if(dismiss){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				process.destroy();
			}
		}
	}

	/**
	 * 发送数据到指定进程【标准输入接收参数】
	 * @param process
	 * @param cmds
	 * @return
	 */
	public static void sendProcessData(Process process, List<String> cmds){
		try {
			DataOutputStream os = new DataOutputStream(process.getOutputStream()); 
			if(cmds!=null){
				for(String cmd : cmds){
					os.writeBytes(cmd+"\n");
				}
				os.flush();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到本地C返回的数据【标准输出输出结果】【需要传入一个接口，来处理接收到的数据】
	 * @param process 需要接收输出的程序
	 * @param stdoutListener 处理接收数据的接口
	 * @return
	 */
	public static void getProcessData(Process process, OnStdoutListener stdoutListener){
		try{
	    	String line = null;
			String [] args = null;
			int len = -1;

			//获取返回内容
			InputStream inputStream = process.getInputStream();
			DataInputStream dataInputStream = new DataInputStream(inputStream);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			
			LogUtils.e("getProcessData", "等待程序的标准输出");

			while ((len = dataInputStream.read(buf)) > -1) {
				baos.write(buf, 0, len);
				line = new String(baos.toByteArray(), "utf-8");

				//如果碰到以”\n“结尾的表示一次信息输送完毕了
				if(line.endsWith("\n")){
					baos.reset();
					if(stdoutListener!=null){
						stdoutListener.processData(line);
					}
				}

				/*
				if(line.endsWith("\n")){					
					baos.reset();
					
					if(line.contains(" ")){
		    			args = line.split(" ");
		    		}else{
		    			args = new String[]{line};
		    		}
		    		if(args[0].contains("Segmentation")){//Segmentation fault
		    			innerorder.OnChangeUI("segmentation");
		    			break;
		    		}else if(args[0].contains("Timer expired")){//connect ui: Timer expired
		    			innerorder.OnChangeUI("timeout");
		    			break;
		    		}else if(args[0].equals("exit")){//退出命令
		    			innerorder.OnChangeUI("exit");
						break;
					}else{
						innerorder.OnChangeUI(args);
					}
				}
				*/
			}
			try {
				inputStream.close();
				dataInputStream.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {

		}
	}

	
	/**
	 * 执行一般的指令，一次只能执行一条，返回Process进行标准流交互
	 * 
	 * ProcessBuilder的构造函数是一个字符串列表或者数组。
	 * 列表中第一个参数是可执行命令程序，其他的是命令行执行是需要的参数。
	 * 
	 * Runtime.exec()可接受一个单独的字符串，这个字符串是通过空格来分隔可执行命令程序和参数的；
	 * 也可以接受字符串数组参数。
	 * 
	 * Runtime.exec()最终是通过调用ProcessBuilder来真正执行操作的。
	 * 
	 * @param cmds 单条指令的字符串数组
	 * 
	 * 指令-----与----参数
	 * su ----------> 各种指令
	 * 指令 --------> 标准输入输出
	 * 
	 * 因为su指令比较特殊，它的参数就是各种指令
	 * @return
	 */
	public static String runCommand(String... cmds) {
		InputStream inputis = null;
		BufferedReader bufferis = null;
		DataInputStream erroris = null;
		Process process = null;
		String results = null;
		try {
			/*
			 * 一种：
			 * process = Runtime.getRuntime().exec(cmds[0]); 
			 * DataOutputStream os = new DataOutputStream(process.getOutputStream()); 
			 * for (int i = 1; i < cmds.length; i++) {
			 * 	   os.writeBytes(cmds[i] +"\n");
			 * }
			 * 
			 * 二种：【参数是字符串数组】
			 * process = Runtime.getRuntime().exec(new String[]{"chmod","777",MyConfigure.RESROOT});
			 * 
			 * ProcessBuilder有以上两种用法
			 *
			 * 
			 * 三种：【参数是单独的字符串，以空格作为间隔符】
			 * process = Runtime.getRuntime().exec("chmod 777 "+MyConfigure.RESROOT);
			 */
			process = new ProcessBuilder(cmds).redirectErrorStream(true).start();

			//阻塞程序，直到运行结束
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				// 获取返回内容
				inputis = process.getInputStream();
				erroris = new DataInputStream(inputis);
				int len = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				while ((len = inputis.read(buf)) > -1) {
					baos.write(buf, 0, len);
				}
				results = new String(baos.toByteArray(), "utf-8");

				inputis.close();
				erroris.close();
				baos.close();
				
			} else {
				// 获取错误提示
				StringBuffer ret = new StringBuffer();
				inputis = process.getErrorStream();
				bufferis = new BufferedReader(new InputStreamReader(inputis));
				String line = null;
				while ((line = bufferis.readLine()) != null) {
					ret.append(line);
				}
				results = ret.toString();

				inputis.close();
				bufferis.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return results;
	}
	
	/**
	 * 清空命令缓冲区
	 * @param is
	 * @param iserror
	 */
	public static void clearCache(final InputStream is,final InputStream iserror){
		if(is!=null){
			new Thread() {  
	            public void run() {  
	                BufferedReader br = new BufferedReader(new InputStreamReader(is));
	                try {  
	                    String line = null;
	                    while ((line = br.readLine()) != null) {
	                        if (line != null) {
	                        	LogUtils.s(line);
	                        }
	                    }
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }.start();  
		}
		
		if(iserror!=null){
	        new Thread() {  
	            public void run() {  
	                BufferedReader br2 = new BufferedReader(new InputStreamReader(iserror));
	                try {  
	                    String line = null;
	                    while ((line = br2.readLine()) != null) {
	                        if (line != null) {
	                        	LogUtils.s(line);
	                        }
	                    }  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }.start();  
		}
	}

	/**
	 * 标准输出数据的处理接口
	 */
	public static interface OnStdoutListener{
		 void processData(String dataStr);
	}

	/**
	 * 判断手机有没有root的接口
	 */
	public interface CheckRootListener {
		
		void AccessGiven(Process process);

		void noAccessGiven();

		void noRoot();

		void rooted();
		
		void onResult(String result);
	}
	
}
