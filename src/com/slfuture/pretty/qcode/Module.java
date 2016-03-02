package com.slfuture.pretty.qcode;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.etc.Controller;
import com.slfuture.pretty.qcode.view.form.CaptureActivity;

/**
 * 二维码模块
 */
public class Module {
	/**
	 * 扫描二维码
	 * 
	 * @param context 上下文
	 * @param callback 回调
	 */
	public static void capture(Activity context, IEventable<String> callback) {
		Intent intent = new Intent(context, CaptureActivity.class);
		int commandId = Serial.makeLoopInteger();
		intent.putExtra("commandId", commandId);
		Controller.<String>doJoin(commandId, callback);
		context.startActivity(intent);
	}

	/**
	 * 生成二维码 要转换的地址或字符串,可以是中文
	 * 
	 * @param content 内容
	 * @param width 宽度
	 * @param height 高度
	 * @return 位图
	 */
	public static Bitmap createQRImage(String content, final int width, final int height) {
		try {
			if(Text.isBlank(content)) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					}
					else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		}
		catch (Exception e) {
			Log.e("pretty", "生成二维码失败", e);
		}
		return null;
	}
}
