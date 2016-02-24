package com.slfuture.pretty.general.view.form;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pretty.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 文本编辑页面
 */
@ResourceView(clazz=R.layout.class, field="activity_textedit")
public class TextEditActivity extends ActivityEx {
	/**
	 * 放弃修改
	 */
	public final static int RESULT_CANCEL = 0;
	/**
	 * 已更新
	 */
	public final static int RESULT_UPDATED = 1;
	
	@ResourceView(clazz=R.id.class, field="textedit_button_close")
	public ImageView imgClose;
	@ResourceView(clazz=R.id.class, field="textedit_label_title")
	public TextView labTitle;
	@ResourceView(clazz=R.id.class, field="textedit_label_save")
	public TextView labSave;
	@ResourceView(clazz=R.id.class, field="textedit_text_content")
	public EditText txtContent;
	@ResourceView(clazz=R.id.class, field="textedit_label_description")
	public TextView labDescription;

	/**
	 * 校验正则表达式
	 */
	private String check = null;
	
	
	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		String title = this.getIntent().getStringExtra("title");
		if(null == title) {
			title = "";
		}
		String defaultValue = this.getIntent().getStringExtra("default");
		if(null == defaultValue) {
			defaultValue = "";
		}
		String description = this.getIntent().getStringExtra("description");
		if(null == description) {
			description = "";
		}
		check = this.getIntent().getStringExtra("check");
		//
		labTitle.setText(title);
		txtContent.setText(defaultValue);
		labDescription.setText(description);
		
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				TextEditActivity.this.setResult(RESULT_CANCEL, intent);
				TextEditActivity.this.finish();
			}
		});
		labSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = txtContent.getText().toString();
				if(null != check) {
					Pattern pattern = Pattern.compile(check);
					Matcher matcher = pattern.matcher(result);
					if(!matcher.matches()) {
						Toast.makeText(TextEditActivity.this, "输入不合法", Toast.LENGTH_LONG).show();
						return;
					}
				}
				Intent intent = new Intent();
				intent.putExtra("result", result);
				TextEditActivity.this.setResult(RESULT_UPDATED, intent);
				TextEditActivity.this.finish();
			}
		});
	}
}
