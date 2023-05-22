package wzk.zyfx.design.util;

/**
 * @author WangZhiKang
 * @date 2023/3/5 1:08
 */
public enum FileSetting {
    PATCH_SETTING("static/setting-{0}.json"),
    PATCH_USER_AGENT("static/user-agent.json"),
    PATCH_STATIC_TEXT("static/static-text.json");

    private final String setting;

    FileSetting(String setting) {
        this.setting = setting;
    }

    public String getSetting() {
        return setting;
    }
}
