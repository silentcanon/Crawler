package com.canon.webcrawler.test;

import com.canon.webcrawler.crawler.CrawlerImpl;
import com.canon.webcrawler.net.*;
import com.canon.webcrawler.utils.Graph;
import com.canon.webcrawler.utils.Other;
import com.canon.webcrawler.utils.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.awt.image.ImageWatched;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Canon on 2015-04-15.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        test();
    }




    public static void test() throws Exception {
        String url = "http://www.csd.uwo.ca/faculty/solis/cs868b/2014/index.html";
        LinkedBlockingQueue<String> urlQueue = new LinkedBlockingQueue<String>();
        urlQueue.add(url);
        Set<String> urlSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        ConcurrentHashMap<String, Page> urlPageMap = new ConcurrentHashMap<String, Page>();
        Request req = new Request();
        long[] numPageVisited = new long[]{0L};
        CrawlerImpl[] threads = new CrawlerImpl[5];
        for(int i = 0; i<threads.length;i++) {
            String threadName = "Thread_"+i;
            threads[i] = new CrawlerImpl(threadName,urlQueue,urlSet,req,numPageVisited)
                    .setRecordMap(urlPageMap)
                    .setDomain("csd.uwo.ca")
                    .setMaxNumPage(100);
        }
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
        }
        for (int j = 0; j < threads.length; j++) {
            threads[j].join();
        }

        ArrayList<ArrayList<Integer>> graph = Graph.buildGraph(urlPageMap, numPageVisited);
        int[] group = Graph.strongConnected(graph, (int)numPageVisited[0]);
        Graph.dump(urlPageMap,numPageVisited);
        Graph.dumpWithGroup(urlPageMap,numPageVisited,group);




    }




}
