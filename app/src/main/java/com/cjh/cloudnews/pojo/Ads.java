package com.cjh.cloudnews.pojo;

/**
 * @author ChangJiahong
 * @date 2019/4/26
 */
public class Ads {
    private String subtitle;
    private String skipType;
    private String skipID;
    private String tag;
    private String title;
    private String imgsrc;
    private String url;
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public String getSubtitle() {
        return subtitle;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }
    public String getSkipType() {
        return skipType;
    }

    public void setSkipID(String skipID) {
        this.skipID = skipID;
    }
    public String getSkipID() {
        return skipID;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
    public String getImgsrc() {
        return imgsrc;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
}
