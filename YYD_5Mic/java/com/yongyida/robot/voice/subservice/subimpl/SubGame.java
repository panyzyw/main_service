package com.yongyida.robot.voice.subservice.subimpl;

import org.json.JSONObject;

import android.text.TextUtils;

import com.yongyida.robot.voice.R;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.ShowDialog;

/**
 * VR游戏显示图片
 * @author Administrator
 *
 */
public class SubGame extends SubFunction {

	@Override
	public void run() {
	
		try {
			if (json == null || context == null)
				return;
			String imageType = null;

			JSONObject jsonObject;

			jsonObject = new JSONObject(json);
			imageType = jsonObject.getJSONObject("semantic")
					.getJSONObject("slots").getString("imageType");

			if (TextUtils.isEmpty(imageType))
				return;
			if (imageType.equals("dogImage")) {
				ShowDialog.showImageDialog(context, R.drawable.img_dog);
			}
			if (imageType.equals("catImage")) {
				ShowDialog.showImageDialog(context, R.drawable.img_cat);
			}
			
			if (imageType.equals("eagleImage")) {
				ShowDialog.showImageDialog(context, R.drawable.img_eagle);
			}
			if (imageType.equals("phoenixImage")) {
				ShowDialog.showImageDialog(context, R.drawable.img_phoenix);
			}
			if (imageType.equals("dinosaurImage")) {
				ShowDialog.showImageDialog(context, R.drawable.img_dinosaur);
			}
			if (imageType.equals("virtualImage")) {
				ShowDialog.showImageDialog(context, R.drawable.img_virtual);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stop() { 
		ShowDialog.cancleImageDialog();
	}
}
