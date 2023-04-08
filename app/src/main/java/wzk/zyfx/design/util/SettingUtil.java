package wzk.zyfx.design.util;

import android.content.Context;
import android.content.res.AssetManager;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.elvishew.xlog.XLog;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author WangZhiKang
 * @date 2023/3/12 2:25
 */
public class SettingUtil {
    private static SettingUtil instance;

    public static void init(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            String userAgentPath = FileSetting.PATCH_SETTING.getSetting();
            InputStream inputStream = assetManager.open(userAgentPath);
            String text = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            instance = new SettingUtil(text);
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public static SettingUtil getInstance() {
        return instance;
    }

    private final HashMap<String, String> settingList = new HashMap<>();

    private SettingUtil(String text) {
        JSONObject jsonObject = JSONUtil.parseObj(text);
        for (String key : jsonObject.keySet()) {
            settingList.put(key, jsonObject.getStr(key));
        }
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
