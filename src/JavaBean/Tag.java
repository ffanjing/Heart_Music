package JavaBean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by yanzhang2 on 2017/4/17.
 */
public class Tag {

    @JSONField(name = "tag_name")
    private String tagName;
    @JSONField(name = "tag_count")
    private int tagCount;


    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }
}
