package wzk.zyfx.design.base;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import cn.hutool.json.JSONArray;
import com.elvishew.xlog.XLog;
import com.tencent.smtt.sdk.ValueCallback;
import wzk.zyfx.design.core.SpiderCore;
import wzk.zyfx.design.util.StaticTextUtil;

/**
 * @author WangZhiKang
 * @date 2023/2/27 1:22
 */
public class JsFunction extends Activity {
    private static final JsFunction INSTANCE = new JsFunction();

    public static JsFunction getInstance() {
        return INSTANCE;
    }

    private X5WebView x5WebView;

    public void setX5WebView(X5WebView x5WebView) {
        this.x5WebView = x5WebView;
    }

    @JavascriptInterface
    public void postPageListInfo() throws InterruptedException {
        while (true) {
            //轮询一下数据,刚初始化时有可能数据还没有完全下来
            JSONArray pageListInfo = SpiderCore.getInstance().getPageListInfo();
            if (pageListInfo != null) {
                runOnUiThread(() -> x5WebView.evaluateJavascript(
                        StaticTextUtil.getInstance().getFunctionText("backPageListInfo", pageListInfo.toString()),
                        s -> {

                        }));
                break;
            }
            Thread.sleep(1000);
        }

    }
}
