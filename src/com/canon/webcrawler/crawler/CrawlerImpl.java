package com.canon.webcrawler.crawler;

import com.canon.webcrawler.net.Request;
import com.canon.webcrawler.net.Response;
import com.canon.webcrawler.utils.Other;
import com.canon.webcrawler.utils.Page;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Canon on 2015-04-18.
 */
public class CrawlerImpl extends Thread implements Crawler {
    private LinkedBlockingQueue<String> urlQueue;
    private Set<String> urlSet;
    private HashSet<String> newUrlSet;
    private int maxNumPage = 50;
    private int maxNumPagePerPage = 500;
    private long waitTime = 3;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private Request req;
    private int maxWaitCount = 10;
    private String domain = "";
    private Document doc = null;
    private long[] numPageVisited;
    private ConcurrentHashMap<String, Page> urlPageMap = null;
    //private ConcurrentHashMap<String, String> linkMap = null;

    //Map m = Collections.synchronizeMap(hashMap);
    public CrawlerImpl(String threadName, LinkedBlockingQueue<String> urlQueue, Set<String> urlSet, Request req, long[] numPageVisited) {
        super(threadName);
        this.req = req;
        this.urlQueue = urlQueue;
        this.urlSet = urlSet;
        this.numPageVisited = numPageVisited;
        this.newUrlSet = new HashSet<String>();

    }

    public CrawlerImpl setRecordMap(ConcurrentHashMap<String, Page> urlPageMap) {
        this.urlPageMap = urlPageMap;
        //this.linkMap = linkMap;
        return this;
    }

    public CrawlerImpl setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    @Override
    public void pageHandler(Document doc,String mimeType, String nextUrl) {
        this.doc = doc;
        this.clearRecord();

        if(mimeType == null)
            return;
        if(this.doc == null && mimeType.startsWith("text"))
            return;
        //if(!mimeType.startsWith("text"))
        //    return;
        long pageId;
        synchronized (this) {
            if(this.urlSet.contains(nextUrl))
                return;
            this.numPageVisited[0] += 1;
            pageId = this.numPageVisited[0]-1;
            this.urlSet.add(nextUrl);
        }

        long remainNumPage = this.maxNumPagePerPage;
        remainNumPage = this.handleLinks(remainNumPage);
        remainNumPage = this.handleImg(remainNumPage);
        this.handleObj(remainNumPage);
        this.urlQueue.addAll(newUrlSet);

        this.savePageLink(nextUrl,mimeType,pageId);
        System.out.println(nextUrl+"  "+ pageId+" Complete");
    }

    private void savePageLink(String nextUrl, String mimeType,long pageId) {
        Page p = new Page(nextUrl, mimeType, pageId);
        if(this.newUrlSet != null)
            p.add(this.newUrlSet);
        this.urlPageMap.put(nextUrl, p);

    }


    public CrawlerImpl setMaxNumPage(int maxNumPage) {
        this.maxNumPage = maxNumPage;
        return this;
    }


    private void clearRecord() {
        this.newUrlSet.clear();
    }

    private long handleLinks(long remainNumPage) {
        if(remainNumPage == 0L)
            return 0L;
        if(doc == null)
            return remainNumPage;
        Elements links = this.doc.select("a[href]");
        for(Element link: links) {
            String url = link.attr("abs:href");
            if(this.urlPreFilter(url)) {
                this.newUrlSet.add(safeUrl(url));
                remainNumPage -= 1;
            }
        }
        return remainNumPage;
    }

    private long handleImg(long remainNumPage) {
        if(remainNumPage == 0L)
            return 0L;
        if(doc == null)
            return remainNumPage;
        Elements imgs = this.doc.select("img[src]");
        for(Element img: imgs) {
            String url = img.attr("abs:src");
            if(this.urlPreFilter(url)) {
                this.newUrlSet.add(safeUrl(url));
                remainNumPage -= 1;
            }
        }
        return remainNumPage;
    }

    private long handleObj(long remainNumPage) {
        if(remainNumPage == 0L)
            return 0L;
        if(doc == null)
            return remainNumPage;
        Elements objs = this.doc.select("embed[src]");
        for(Element obj: objs) {
            String url = obj.attr("abs:src");
            int posDot = url.lastIndexOf(".");
            if(posDot == -1)
                continue;
            if(this.urlPreFilter(url)) {
                this.newUrlSet.add(safeUrl(url));
                remainNumPage -= 1;
            }
        }
        return remainNumPage;
    }

    private String safeUrl(String url) {
        return Other.safeUrl(url);
    }

    public void buildGraph() {
        //TODO to add node to the webgraph
    }

    private String urlFilter(String url) {
        if(url == null)
            return null;
        if(!url.startsWith("http"))
            return null;

        if (this.urlSet.contains(url))
            return null;


        return url;
    }

    private boolean urlPreFilter(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if(host == null)
                return false;
            if(this.urlSet.contains(url))
                return false;
            if(host.endsWith(this.domain) && url.startsWith("http"))
                return true;
        } catch (URISyntaxException ex) {
            return false;
        }

        return false;
    }



    @Override
    public void run() {
        int waitCount = 0;

        while(this.numPageVisited[0] < this.maxNumPage && waitCount < this.maxWaitCount) {
            String nextUrl = null;
            try {
                nextUrl = this.urlQueue.poll(this.waitTime, this.timeUnit);
                nextUrl = urlFilter(nextUrl);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if(nextUrl == null) {
                waitCount += 1;
            } else {
                waitCount = 0;
                Response res = this.req.getResponse(nextUrl);
                if(res == null)
                    continue;
                try {
                    String mimeType = res.getMimeType();
                    Document doc = res.getDoc();
                    this.pageHandler(doc, mimeType, nextUrl);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }

        }

    }
}
