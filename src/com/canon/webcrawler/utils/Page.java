package com.canon.webcrawler.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Canon on 2015-04-18.
 */
public class Page {
    private String url;
    private String mimeType;
    private LinkedList<String> outgoingUrls;
    private long pageId;

    public Page(String url, String mimeType, long pageId) {
        this.url = url;
        this.mimeType = mimeType;
        this.pageId = pageId;
        this.outgoingUrls = new LinkedList<String>();
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getUrl() {
        return this.url;
    }

    public long getPageId() {
        return this.pageId;
    }

    public void add(String outgoingUrl) {
        this.outgoingUrls.add(outgoingUrl);
    }

    public void add(Set<String> outgoingUrls) {
        this.outgoingUrls.addAll(outgoingUrls);
    }

    public LinkedList<String> getOutgoingUrls() {
        return this.outgoingUrls;
    }
}
