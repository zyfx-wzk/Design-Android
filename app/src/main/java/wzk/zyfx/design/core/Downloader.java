package wzk.zyfx.design.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import wzk.zyfx.design.util.HeaderUtil;
import wzk.zyfx.design.util.SettingUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author WangZhiKang
 * @date 2023/3/12 2:34
 */
public class Downloader {
    private static Downloader instance;

    public static Downloader getInstance() {
        if (instance == null) {
            instance = new Downloader();
        }
        return instance;
    }

    /**
     * 请求超时时间
     */
    private static int REQUEST_TIME_OUT;

    private Downloader() {
        REQUEST_TIME_OUT = SettingUtil.getInstance().getInt("downloader-timeout");
    }

    /**
     * 通用请求处理
     */
    public HttpResponse getResponse(String url) {
        HttpRequest request = HttpRequest.get(url)
                .header(Header.USER_AGENT, HeaderUtil.getInstance().getUserAgent())
                .header(Header.REFERER, HeaderUtil.getInstance().getRefererUrl());
        request.timeout(REQUEST_TIME_OUT);
        return request.execute();
    }

    public String getHtml(String url) {
        return getResponse(url).body();
    }

    public InputStream getInputStream(String url) {
        return getResponse(url).bodyStream();
    }

    public void saveContent(String path, String url) throws IOException {
        OutputStream outputStream = FileUtil.getOutputStream(path);
        InputStream inputStream = getInputStream(url);
        IoUtil.copy(inputStream, outputStream);
        outputStream.close();
        inputStream.close();
    }
}
