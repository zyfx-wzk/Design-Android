package wzk.zyfx.design.core;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/**
 * @author WangZhiKang
 * @date 2023/3/12 3:51
 */
@Getter
@Setter
public class ArticleBean {
    private String title;
    private Date date;
    private String time;
    private String url;

    public void setTime(String time) {
        this.time = time;
        this.date = DateUtil.parseDate(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleBean that = (ArticleBean) o;
        return Objects.equals(title, that.title)
                && Objects.equals(time, that.time) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, time, url);
    }
}
