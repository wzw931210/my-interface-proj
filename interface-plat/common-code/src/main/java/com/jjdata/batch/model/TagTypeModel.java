package com.jjdata.batch.model;

/**
 * @author shipeien
 * @version 1.0
 * @Title: TagTypeModel
 * @ProjectName run-batch
 * @Description: 标签类型类
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1712:57
 */
public class TagTypeModel {
    private Integer id;
    private String tagName;
    private String tagChName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagChName() {
        return tagChName;
    }

    public void setTagChName(String tagChName) {
        this.tagChName = tagChName;
    }

    public TagTypeModel(Integer id, String tagName, String tagChName) {
        this.id = id;
        this.tagName = tagName;
        this.tagChName = tagChName;
    }

    @Override
    public String toString() {
        return "TagTypeModel{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                ", tagChName='" + tagChName + '\'' +
                '}';
    }
}
