package wzk.zyfx.design;

import android.app.Application;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

/**
 * @author WangZhiKang
 * @date 2023/2/26 0:45
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initXLog();
        initX5WebView();
    }

    /**
     * 日志框架初始化
     */
    private void initXLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.INFO)
                .build();
        Printer androidPrinter = new AndroidPrinter();
        XLog.init(config, androidPrinter);
    }

    /**
     * X5内核初始化
     */
    private void initX5WebView() {
        // 搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean init) {
                XLog.i("X5内核初始化状态: " + init);
            }
        };
        // x5内核初始化接口
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(this, cb);

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }
}
