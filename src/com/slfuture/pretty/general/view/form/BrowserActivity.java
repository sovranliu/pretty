package com.slfuture.pretty.general.view.form;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.slfuture.carrie.base.text.Text;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;
import com.slfuture.pluto.view.control.GifView;
import com.slfuture.pretty.R;
import com.slfuture.pretty.general.core.IBrowserHandler;
import com.slfuture.pretty.general.view.control.Browser;

/**
 * 浏览器界面
 */
@ResourceView(clazz=R.layout.class, field="activity_browser")
public class BrowserActivity extends ActivityEx {
	/**
	 * 头部
	 */
	@ResourceView(clazz=R.id.class, field="browser_layout_header")
	public View header = null;
	/**
	 * 后退
	 */
	@ResourceView(clazz=R.id.class, field="browser_image_back")
	public ImageView imgBack = null;
	/**
	 * 前进
	 */
	@ResourceView(clazz=R.id.class, field="browser_image_forward")
	public ImageView imgForward = null;
	/**
	 * 关闭
	 */
	@ResourceView(clazz=R.id.class, field="browser_label_close")
	public TextView labClose = null;
	/**
	 * 标题
	 */
	@ResourceView(clazz=R.id.class, field="browser_label_title")
	public TextView labTitle = null;
	/**
	 * 引导对象容器
	 */
	@ResourceView(clazz=R.id.class, field="browser_layout_loading")
	public View loadingContainer = null;
	/**
	 * 引导对象
	 */
	@ResourceView(clazz=R.id.class, field="browser_image_loading")
	public GifView loading = null;
	/**
	 * 浏览器对象
	 */
	@ResourceView(clazz=R.id.class, field="browser_web")
	public Browser browser = null;

	/**
	 * 加载的URL
	 */
	private String url = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		prepare();
		load();
	}

	/**
	 * 准备
	 */
	public void prepare() {
		prepareData();
		imgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(browser.canGoBack()) {
					browser.goBack();
				}
				else {
					BrowserActivity.this.finish();
				}
			}
		});
		imgForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(browser.canGoForward()) {
					browser.goForward();
				}
			}
		});
		labClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BrowserActivity.this.finish();
			}
		});
		prepareBrowser();
	}

	/**
	 * 准备数据
	 */
	public void prepareData() {
		this.url = this.getIntent().getStringExtra("url");
		Bundle bundle = this.getIntent().getBundleExtra("handler");
		if(null == bundle) {
			return;
		}
		int i = 0;
		while(true) {
			if(!bundle.containsKey(String.valueOf(i))) {
				break;
			}
			IBrowserHandler handler = (IBrowserHandler) bundle.getSerializable(String.valueOf(i));
			browser.register(handler);
			i++;
		}
	}

	/**
	 * 准备浏览器
	 */
	public void prepareBrowser() {
		browser.activity = this;
		browser.getSettings().setJavaScriptEnabled(true);
		browser.requestFocus();
		browser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("mailto:") || url.startsWith("geo:") ||url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	                startActivity(intent);
	                browser.pauseTimers();
	                return false;
	            }
				else if(url.startsWith("new://")) {
					Intent intent = new Intent(BrowserActivity.this, BrowserActivity.class);
					intent.putExtra("url", url.substring("new://".length()));
					Bundle bundle = BrowserActivity.this.getIntent().getBundleExtra("handler");
					if(null != bundle) {
						intent.putExtra("handler", bundle);
					}
					startActivity(intent);
	                browser.pauseTimers();
	                return false;
				}
				browser.loadUrl(url);
	            return true;
			}
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				browser.loadUrl("about:blank");
			}
			@Override 
	        public void onPageFinished(WebView view, String url) {  
				view.loadUrl("javascript: var allLinks = document.getElementsByTagName('a'); if (allLinks) {var i;for (i=0; i<allLinks.length; i++) {var link = allLinks[i];var target = link.getAttribute('target'); if (target && target == '_blank') {link.setAttribute('target','_self');link.href = 'new://'+link.href;}}}"); 
			}
		});
		browser.setDefaultHandler(new com.slfuture.pluto.js.DefaultHandler());
		browser.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(100 == newProgress) {
					view.setVisibility(View.VISIBLE);
					loadingContainer.setVisibility(View.GONE);
					loading.setVisibility(View.GONE);
				}
			}
			@Override  
            public void onReceivedTitle(WebView view, String title) {  
                super.onReceivedTitle(view, title);
                if(Text.isBlank(title)) {
                	header.setVisibility(View.GONE);
                }
                else {
                	header.setVisibility(View.VISIBLE);
                	labTitle.setText(title);
                }
            }
		});
		browser.setDownloadListener(new DownloadListener() {
			@Override
	        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {  
	            Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent);  
	        }
		});
	}

	/**
	 * 加载
	 */
	public void load() {
		if(null == url) {
			return;
		}
		loadingContainer.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		loading.setGifImage(R.drawable.bg_loading);
		//
		browser.setVisibility(View.INVISIBLE);
		browser.loadUrl(url);
		browser.register();
	}
}
