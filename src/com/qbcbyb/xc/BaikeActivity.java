package com.qbcbyb.xc;

import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qbcbyb.libandroid.msg.Msg;
import com.qbcbyb.libandroid.msg.Msg.Which;
import com.qbcbyb.xc.model.SpotModel;

public class BaikeActivity extends TitleWithBackActivity {
    private static final String BAIKE_URL = "http://baike.baidu.com/search/word?word=%s";
    private SpotModel model;

    private ProgressBar progressWebView;
    private WebView webview;

    @Override
    protected void initLayout(TextView title) {
        model = (SpotModel) getIntent().getSerializableExtra(KEY_SPOT);
        title.setText(model.getName());

        setContentView(R.layout.activity_webview);

        progressWebView = (ProgressBar) findViewById(R.id.progressWebView);
        webview = (WebView) findViewById(R.id.webview);

        webview.setWebChromeClient(new MyWebChromeClient());
        webview.setWebViewClient(new MyWebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放

        webview.loadUrl(String.format(BAIKE_URL, model.getName()));
    }

    @Override
    protected void addEventListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleMessage(Message arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onCancelProgress(DialogInterface arg0) {
        // TODO Auto-generated method stub

    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:setTimeout(function(){var container=document.getElementById('J-lemma');"
                    + "if(container&&container.childElementCount){"
                    + "var lastItem=container.children[container.childElementCount-1];"
                    + "if(lastItem&&lastItem.innerHTML&&lastItem.innerHTML.indexOf('立即下载')){"
                    + "lastItem.style.display='none';" + "}" + "}},10);");
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            Msg.confirm(BaikeActivity.this, url, message, "确定", null, new Msg.MsgCallback() {

                @Override
                public void callBack(Which arg0) {
                    result.confirm();
                }
            });
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            Msg.confirm(BaikeActivity.this, url, message, new Msg.MsgCallback() {
                @Override
                public void callBack(Which arg0) {
                    if (arg0 == Which.POSITIVE) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                }
            });
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressWebView.setVisibility(View.INVISIBLE);
            } else {
                if (View.INVISIBLE == progressWebView.getVisibility()) {
                    progressWebView.setVisibility(View.VISIBLE);
                }
                progressWebView.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
