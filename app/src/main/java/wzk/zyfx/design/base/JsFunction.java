package wzk.zyfx.design.base;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import wzk.zyfx.design.core.SpiderCore;
import wzk.zyfx.design.util.SettingUtil;
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

    //无法立即返回数据的,统一用异步函数包裹

    @JavascriptInterface
    public void postPageListInfo(int index, boolean init) {
        if (init) {
            if (index == 0) {
                SettingUtil.getInstance().reload("tj");
            } else if (index == 1) {
                SettingUtil.getInstance().reload("tjra");
            }
        }
        ThreadUtil.execAsync(() -> {
            while (true) {
                //轮询一下数据,刚初始化时有可能数据还没有完全下来
                JSONArray pageListInfo = SpiderCore.getInstance().getPageListInfo();
                if (pageListInfo != null) {
                    runOnUiThread(() -> x5WebView.evaluateJavascript(
                            StaticTextUtil.getInstance().getFunctionText("backPageListInfo",
                                    EscapeUtil.escape(pageListInfo.toString())), s -> {
                            }));
                    break;
                }
                ThreadUtil.sleep(1000);
            }
        });
    }

    @JavascriptInterface
    public void postCurInfoList(int index, int size) {
        JSONObject curInfoList = SpiderCore.getInstance().getCurInfoList(index, size);
        runOnUiThread(() -> x5WebView.evaluateJavascript(
                StaticTextUtil.getInstance().getFunctionText("backCurInfoList",
                        curInfoList.toString()), s -> {
                }));
    }

    @JavascriptInterface
    public void postArticleContent(String url) {
        ThreadUtil.execAsync(() -> {
            String content = SpiderCore.getInstance().getArticleContent(url);
            runOnUiThread(() -> x5WebView.evaluateJavascript(
                    StaticTextUtil.getInstance().getFunctionText("backArticleContent",
                            EscapeUtil.escape(content)), s -> {
                    }));
        });
    }
}
