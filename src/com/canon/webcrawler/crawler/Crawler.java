package com.canon.webcrawler.crawler;
import org.jsoup.nodes.Document;

/**
 * Created by Canon on 2015-04-15.
 */


public interface Crawler {
    public void pageHandler(Document doc,String mimeType, String nextUrl);

}
