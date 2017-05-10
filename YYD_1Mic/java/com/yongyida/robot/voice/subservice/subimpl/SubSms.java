package com.yongyida.robot.voice.subservice.subimpl;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;

import com.yongyida.robot.voice.subservice.SubFunction;

public class SubSms extends SubFunction {
	//ParseFactory factory = new ParseFactory();
	//List<SmsInfo> infos ;
	//private final String uri = "content://sms/inbox";
	//private final String uri2 = "content://sms/";
	@Override
	public void run() {

		if (json == null || context == null)
			return;
		String smsType = null;

		JSONObject jsonObject;

		try {
			jsonObject = new JSONObject(json);
			smsType = jsonObject.getJSONObject("semantic")
					.getJSONObject("slots").getString("smsType");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (TextUtils.isEmpty(smsType))
			return;
		
//		factory.setFactory(VoiceRead.getInstence(context));
		
		if (smsType.equals("ttsNoRead")) {
//			factory.parseStart(setText(getsmsInfos()));
		}
		if (smsType.equals("seeNoRead")) {
//			factory.parseStart(setText(getsmsInfos()));
			openSms();
		}
		if (smsType.equals("ttsPersonNoRead")) {
//			factory.parseStart(setText(getsmsInfos()));
		}
		
	}
	
	public void openSms(){
		Intent intent = new Intent();
		intent.setClassName("com.android.mms","com.android.mms.ui.ConversationList");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);  
	}
	
/*	public String setText(List<SmsInfo> infos) {
		StringBuilder sb = new StringBuilder("");
		for (SmsInfo info : infos) {
			sb.append(info.toString() + "\n");
		}
		return sb.toString();
	}*/


	/*// 获取短信的内容
	public List<SmsInfo> getsmsInfos() {

		if (infos == null) {
			infos = new ArrayList<SmsInfo>();
		} else
			infos.clear();

		String[] projection = new String[] { "_id", "address", "person",
				"read", "body", "date", "type" };

		Cursor cusor = context.getContentResolver().query(Uri.parse(uri), projection,
				"type = 1 and read = 0", null, "date asc");

		// Cursor cusor = activity.managedQuery(uri, projection, null, null,
		//
		// "date desc");

		int nameColumn = cusor.getColumnIndex("person");

		int phoneNumberColumn = cusor.getColumnIndex("address");

		int smsbodyColumn = cusor.getColumnIndex("body");

		int dateColumn = cusor.getColumnIndex("date");

		int typeColumn = cusor.getColumnIndex("type");

		int _idColumn = cusor.getColumnIndex("_id");

		int readColumn = cusor.getColumnIndex("read");

		if (cusor != null) {

			while (cusor.moveToNext()) {

				SmsInfo smsinfo = new SmsInfo();

				smsinfo.set_id(cusor.getLong(_idColumn));

				smsinfo.setPerson(cusor.getString(nameColumn));

				smsinfo.setDate(cusor.getString(dateColumn));

				smsinfo.setAddress(cusor.getString(phoneNumberColumn));

				smsinfo.setSmsbody(cusor.getString(smsbodyColumn));

				smsinfo.setType(cusor.getString(typeColumn));

				smsinfo.setRead(cusor.getString(readColumn));

				infos.add(smsinfo);

				// modify(cusor);
				ContentValues values = new ContentValues();
				values.put("seen",1);
				values.put("read", 1);
				values.put("body", "你好");
				// Long id=cusor.get_id();
				
				
				int column=context.getContentResolver().update(Uri.parse(uri2), values, "id=?",
						new String[] { cusor.getLong(_idColumn) + "" });
				Log.i("column==",column+"");
			}

			cusor.close();

		}

		return infos;

	}*/

	@Override
	public void stop() {

	//	factory.parseStop();

	}

}
