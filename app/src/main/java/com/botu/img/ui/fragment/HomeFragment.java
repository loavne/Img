package com.botu.img.ui.fragment;

import com.botu.img.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 主页
 * @author: swolf
 * @date : 2016-11-03 09:58
 */
public class HomeFragment extends BaseFragment{

    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mWebView = (WebView) mActivity.findViewById(R.id.webView);
    }

    @Override
    public void initData() {
        mWebView.loadUrl("http://www.baidu.com");
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });
    }
}
