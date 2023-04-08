package wzk.zyfx.design.util;

import android.content.Context;
import android.content.res.AssetManager;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.elvishew.xlog.XLog;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author WangZhiKang
 * @date 2023/3/5 1:08
 */
public class HeaderUtil {
    private static HeaderUtil instance;

    public static void init(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            String userAgentPath = FileSetting.PATCH_USER_AGENT.getSetting();
            InputStream inputStream = assetManager.open(userAgentPath);
            String text = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            instance = new HeaderUtil(text);
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public static HeaderUtil getInstance() {
        return instance;
    }

    private final List<String> userAgentList;
    private final String refererUrl;

    private HeaderUtil(String text) {
        JSONArray jsonArray = JSONUtil.parseArray(text);
        userAgentList = jsonArray.toList(String.class);
        refererUrl = "https://www.baidu.com/";
    }

    public String getUserAgent() {
        return userAgentList.get(RandomUtil.randomInt(userAgentList.size()));
    }

    public List<String> getUserAgentList() {
        return userAgentList;
    }

    public String getRefererUrl() {
        return refererUrl;
    }
}
