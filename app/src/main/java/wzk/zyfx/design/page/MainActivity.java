package wzk.zyfx.design.page;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import wzk.zyfx.design.R;
import wzk.zyfx.design.base.JsFunction;
import wzk.zyfx.design.base.X5WebView;

public class MainActivity extends AppCompatActivity {
    private X5WebView x5WebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        x5WebView = findViewById(R.id.x5WebView);
        if (x5WebView.getSettingsExtension() != null) {
            try {
                //前进后退缓存
                x5WebView.getSettingsExtension().setContentCacheEnable(true);
                //刘海屏适配
                x5WebView.getSettingsExtension().setDisplayCutoutEnable(true);
                //支持缩放
                x5WebView.getSettingsExtension().setAutoRecoredAndRestoreScaleEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //JS提示框适配
        x5WebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView webView, String s1, String s2, JsResult jsResult) {
                return super.onJsAlert(webView, s1, s2, jsResult);
            }
        });

        //加载页面和JS映射
        JsFunction.getInstance().setX5WebView(x5WebView);
        x5WebView.loadUrl("http://192.168.3.43:8081/");
        x5WebView.addJavascriptInterface(JsFunction.getInstance(), "jsFunction");
    }

    /**
     * 结束资源销毁
     */
    @Override
    protected void onDestroy() {
        if (x5WebView != null) {
            x5WebView.destroy();
        }
        super.onDestroy();
    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (x5WebView != null && x5WebView.canGoBack()) {
                x5WebView.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}