package wzk.zyfx.design.util;

import android.content.Context;
import android.content.res.AssetManager;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.elvishew.xlog.XLog;
import wzk.zyfx.design.core.Downloader;
import wzk.zyfx.design.core.SpiderCore;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author WangZhiKang
 * @date 2023/3/12 2:25
 */
public class SettingUtil {
    private static SettingUtil instance;
    private static AssetManager assetManager;

    public static void init(Context context) {
        instance = new SettingUtil();
        assetManager = context.getAssets();
    }

    public static SettingUtil getInstance() {
        return instance;
    }

    private final HashMap<String, String> settingList = new HashMap<>();

    private SettingUtil() {
    }

    public void reload(String text) {
        try {
            String userAgentPath = FileSetting.PATCH_SETTING.getSetting();
            text = MessageFormat.format(userAgentPath, text);
            InputStream inputStream = assetManager.open(text);
            String content = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            //重置设置
            settingList.clear();
            JSONObject jsonObject = JSONUtil.parseObj(content);
            for (String key : jsonObject.keySet()) {
                settingList.put(key, jsonObject.getStr(key));
            }
        } catch (Exception e) {
            XLog.e(e);
        }
        Downloader.getInstance().reload();
        SpiderCore.getInstance().reload();
    }

    public String getStr(String key) {
        return settingList.getOrDefault(key, "");
    }

    public int getInt(String key) {
        String str = settingList.getOrDefault(key, "0");
        assert str != null;
        return Integer.parseInt(str);
    }
}
