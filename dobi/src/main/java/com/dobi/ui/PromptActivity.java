package com.dobi.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.dobi.R;
import com.dobi.common.CommonMethod;

public class PromptActivity extends BaseActivity {
	private WebView webView;
	private Dialog dialog;
	private TextView showText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prompt);
		webView = (WebView) findViewById(R.id.webView);
		dialog = CommonMethod.showMyDialog(PromptActivity.this);
		showText = (TextView) findViewById(R.id.showText);
		webView.getSettings().setJavaScriptEnabled(true);
		String url = getIntent().getExtras().getString("url");
		try {
			String title = getIntent().getExtras().getString("title");
			showText.setText(title);
		} catch (Exception e) {
			
		}
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {
			// 网页加载开始时调用，显示加载提示旋转进度条
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				dialog.show();
			}

			// 网页加载完成时调用，隐藏加载提示旋转进度条
			@Override
			public void onPageFinished(WebView view, String url) {
				if (dialog != null & dialog.isShowing()) {
					dialog.dismiss();
				}
			}

			// 网页加载失败时调用，隐藏加载提示旋转进度条
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				if (dialog != null & dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
	}

	public void finishActivity(View v) {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null & dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
