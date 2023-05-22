package wzk.zyfx.design.core;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.elvishew.xlog.XLog;
import cn.hutool.json.JSONArray;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import wzk.zyfx.design.util.SettingUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangZhiKang
 * @date 2023/3/12 2:43
 */
public class SpiderCore {
    private static SpiderCore instance;

    public static SpiderCore getInstance() {
        if (instance == null) {
            instance = new SpiderCore();
        }
        return instance;
    }

    private String baseUrl;
    private String newsUrl;
    private String annoUrl;
    private String flashUrl;

    private final Map<Integer, List<ArticleBean>> pageInfoList = new HashMap<>();

    private final Map<Integer, Set<ArticleBean>> pageInfoMap = new ConcurrentHashMap<>();

    public void reload() {
        baseUrl = SettingUtil.getInstance().getStr("spider-url-base");
        newsUrl = baseUrl + SettingUtil.getInstance().getStr("spider-url-news");
        annoUrl = baseUrl + SettingUtil.getInstance().getStr("spider-url-anno");
        flashUrl = baseUrl + SettingUtil.getInstance().getStr("spider-url-flash");
        initPageInfoMap();
    }

    public JSONArray getPageListInfo() {
        if (pageInfoMap.size() != 3) {
            return null;
        }
        JSONArray result = new JSONArray();
        for (Map.Entry<Integer, Set<ArticleBean>> entry : pageInfoMap.entrySet()) {
            sortPageInfoList();
            List<ArticleBean> tempList = pageInfoList.get(entry.getKey());
            if (entry.getValue().size() == 0 || tempList == null) {
                return null;
            }
            //组装发给前端的数据
            JSONArray array = new JSONArray();
            for (int i = 0; i < Math.min(tempList.size(), 20); i++) {
                JSONObject json = JSONUtil.createObj()
                        .putOnce("id", tempList.get(i).getId())
                        .putOnce("title", tempList.get(i).getTitle())
                        .putOnce("time", tempList.get(i).getTime())
                        .putOnce("url", tempList.get(i).getUrl());
                array.add(json);
            }
            JSONObject object = JSONUtil.createObj()
                    .putOnce("index", entry.getKey())
                    .putOnce("info", array);
            result.add(object);
        }
        return result;
    }

    public JSONObject getCurInfoList(int index, int size) {
        JSONObject result = new JSONObject();
        result.putOnce("index", index);
        if (pageInfoMap.get(index) == null) {
            return result;
        }
        sortPageInfoList();
        List<ArticleBean> tempList = pageInfoList.get(index);
        if (tempList == null || tempList.size() <= size) {
            return result;
        }
        JSONArray array = new JSONArray();
        for (int i = size; i < Math.min(tempList.size(), size + 20); i++) {
            JSONObject json = JSONUtil.createObj()
                    .putOnce("id", tempList.get(i).getId())
                    .putOnce("title", tempList.get(i).getTitle())
                    .putOnce("time", tempList.get(i).getTime())
                    .putOnce("url", tempList.get(i).getUrl());
            array.add(json);
        }
        return result.putOnce("info", array);
    }

    //获取页面具体内容
    public String getArticleContent(String url) {
        String content = Downloader.getInstance().getHtml(baseUrl + url);
        String ruleArticle = SettingUtil.getInstance().getStr("spider-rule-article");
        //提取具体文章内容
        JXDocument jxDocument = JXDocument.create(content);
        List<JXNode> jxNodeList = jxDocument.selN(ruleArticle);
        StringBuilder stringBuilder = new StringBuilder();
        for (JXNode jxNode : jxNodeList) {
            stringBuilder.append(jxNode.asElement().html());
        }
        content = stringBuilder.toString();
        //处理下所有的图片地址和宽度
        content = ReUtil.replaceAll(content, "<img src=\"(.+?)\"",
                "<img src=\"" + baseUrl + "$1\" style=\"width:680px");
        content = ReUtil.replaceAll(content, "px", "upx");
        return content;
    }

    //按时间对已有数据排序
    private void sortPageInfoList() {
        for (Map.Entry<Integer, Set<ArticleBean>> entry : pageInfoMap.entrySet()) {
            //当已排序的列表与已存列表数目不同时,则认为有数据更新,重新进行排序
            List<ArticleBean> tempList = pageInfoList.getOrDefault(entry.getKey(), new ArrayList<>());
            if (tempList != null && tempList.size() != entry.getValue().size()) {
                tempList.clear();
                tempList.addAll(entry.getValue());
                tempList.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            }
            pageInfoList.put(entry.getKey(), tempList);
        }
    }

    public void initPageInfoMap() {
        for (int i = 0; i < 3; i++) {
            int index = i;
            ThreadUtil.execAsync(() -> {
                Set<ArticleBean> set = new ConcurrentHashSet<>();
                pageInfoMap.put(index, set);
                initPageInfo(index, set);
            });
        }
    }

    private void initPageInfo(int index, Set<ArticleBean> set) {
        switch (index) {
            case 0: {
                initInfoList(annoUrl, set);
                break;
            }
            case 1: {
                initInfoList(newsUrl, set);
                break;
            }
            case 2: {
                initInfoList(flashUrl, set);
            }
            default:
        }
    }

    private void initInfoList(String url, Set<ArticleBean> set) {
        List<ArticleBean> infoList = new ArrayList<>();
        List<String> pageList = new ArrayList<>();
        //初始化查询页面,可能不存在页面列表
        initInfoAndPageList(url, false, infoList, pageList);
        if (pageList.size() > 0) {
            ThreadUtil.execAsync(() -> {
                List<ArticleBean> tempList = new ArrayList<>();
                for (String page : pageList) {
                    tempList.clear();
                    initInfoAndPageList(baseUrl + page, true, tempList, null);
                    set.addAll(tempList);
                    XLog.d(url + "类型总数: " + set.size());
                }
            });
            return;
        }
        set.addAll(infoList);
        XLog.d(url + "类型总数: " + set.size());
    }

    private void initInfoAndPageList(String url, boolean isLoop,
                                     List<ArticleBean> infoList, List<String> pageList) {
        JXDocument jxDocument = JXDocument.create(Downloader.getInstance().getHtml(url));
        String ruleInfo = SettingUtil.getInstance().getStr("spider-rule-info-list");
        String ruleTime = SettingUtil.getInstance().getStr("spider-rule-time_list");
        String rulePage = SettingUtil.getInstance().getStr("spider-rule-page-list");
        List<JXNode> infoNodeList = jxDocument.selN(ruleInfo);
        List<JXNode> timeNodeList = jxDocument.selN(ruleTime);
        List<JXNode> pageNodeList = jxDocument.selN(rulePage);
        //拼接文章信息
        for (int i = 0; i < infoNodeList.size(); i++) {
            try {
                ArticleBean articleBean = new ArticleBean();
                Element temp = infoNodeList.get(i).asElement();
                articleBean.setUrl(temp.attr("href"));
                articleBean.setTitle(temp.text());
                if (timeNodeList.get(i) != null) {
                    articleBean.setTime(timeNodeList.get(i).asElement().ownText());
                }
                infoList.add(articleBean);
            } catch (Exception e) {
                e.printStackTrace();
                XLog.e("Xpath匹配错误");
            }
        }
        if (isLoop || pageList == null) {
            return;
        }
        //拼接页面信息
        for (JXNode page : pageNodeList) {
            pageList.add(page.asElement().attr("href"));
        }
    }
}
