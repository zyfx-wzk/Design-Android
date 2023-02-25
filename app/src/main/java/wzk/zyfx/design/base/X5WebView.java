package wzk.zyfx.design.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @author WangZhiKang
 * @date 2023/2/23 0:53
 */
public class X5WebView extends WebView {
    public X5WebView(Context context) {
        super(context);
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        WebViewClient client = new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };
        this.setWebViewClient(client);
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        //设置是否启用js
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        //设置是否启用缓存
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置是否启用缩放
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setDisplayZoomControls(false);
        //设置文件访问权限
        webSetting.setAllowFileAccess(true);
        //设置屏幕自适应
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
    }
}
