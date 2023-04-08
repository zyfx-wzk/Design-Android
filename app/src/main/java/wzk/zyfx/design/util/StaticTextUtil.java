package wzk.zyfx.design.util;

import android.content.Context;
import android.content.res.AssetManager;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.elvishew.xlog.XLog;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * 静态文本工具类
 *
 * @author WangZhiKang
 * @date 2022/8/15 0:16
 */
public class StaticTextUtil {
    private static StaticTextUtil instance;

    public static void init(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            String userAgentPath = FileSetting.PATCH_STATIC_TEXT.getSetting();
            InputStream inputStream = assetManager.open(userAgentPath);
            String text = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            instance = new StaticTextUtil(text);
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public static StaticTextUtil getInstance() {
        return instance;
    }

    private final HashMap<String, String> staticTextList = new HashMap<>();

    private StaticTextUtil(String text) {
        JSONObject jsonObject = JSONUtil.parseObj(text);
        for (String key : jsonObject.keySet()) {
            staticTextList.put(key, jsonObject.getStr(key));
        }
    }

    public String getStaticText(String key) {
        return staticTextList.getOrDefault(key, "");
    }

    public String getStaticText(String key, Object... text) {
        return MessageFormat.format(staticTextList.getOrDefault(key, ""), text);
    }

    public String getFunctionText(String name) {
        return MessageFormat.format("javascript:{1}()", name);
    }

    public String getFunctionText(String name, String text) {
        return MessageFormat.format("javascript:{0}(''{1}'')", name, text);
    }
}
