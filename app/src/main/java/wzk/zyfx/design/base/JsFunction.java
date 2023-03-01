package wzk.zyfx.design.base;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.elvishew.xlog.XLog;
import com.tencent.smtt.sdk.ValueCallback;

/**
 * @author WangZhiKang
 * @date 2023/2/27 1:22
 */
public class JsFunction extends Activity {
    private static JsFunction instance;

    private X5WebView x5WebView;

    public static JsFunction getInstance() {
        if (instance == null) {
            instance = new JsFunction();
        }
        return instance;
    }

    public void setX5WebView(X5WebView x5WebView) {
        this.x5WebView = x5WebView;
    }

    @JavascriptInterface
    public void testFunction() {
        XLog.i("App连接测试成功");
        runOnUiThread(() -> x5WebView.evaluateJavascript("javascript:testFunction()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                XLog.i(s);
            }
        }));
    }
}
