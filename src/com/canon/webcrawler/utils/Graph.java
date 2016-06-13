package com.canon.webcrawler.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Canon on 2015-04-21.
 */
public class Graph {
    public static void main(String[] args) throws Exception{
        ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> zeroOut = new ArrayList<Integer>();
        zeroOut.add(1);
        zeroOut.add(2);
        graph.add(zeroOut);
        ArrayList<Integer> oneOut = new ArrayList<Integer>();
        oneOut.add(3);
        graph.add(oneOut);
        ArrayList<Integer> twoOut = new ArrayList<Integer>();
        twoOut.add(3);
        twoOut.add(4);
        graph.add(twoOut);
        ArrayList<Integer> threeOut = new ArrayList<Integer>();
        threeOut.add(5);
        threeOut.add(0);
        graph.add(threeOut);
        ArrayList<Integer> fourOut = new ArrayList<Integer>();
        fourOut.add(5);
        graph.add(fourOut);
        ArrayList<Integer> fiveOut = new ArrayList<Integer>();
        graph.add(fiveOut);
        int[] group = strongConnected(graph,6);
        for(int u: group) {
            System.out.print(u+" ");
        }

    }

    public static ArrayList<ArrayList<Integer>> buildGraph(ConcurrentHashMap<String, Page> urlPageMap, long[] numPageVisited) {
        ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
        int length = (int)numPageVisited[0];
        for(int i = 0; i<length;i++) {
            graph.add(new ArrayList<Integer>());
        }
        Iterator it = urlPageMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String l = (String) entry.getKey();
            Page p = (Page) entry.getValue();
            LinkedList<String> outgoingUrls = p.getOutgoingUrls();
            //System.out.println(p.getUrl());
            int id = (int) p.getPageId();
            for (String outgoingUrl : outgoingUrls) {
                Page p2 = urlPageMap.getOrDefault(outgoingUrl, null);
                if (p2 == null)
                    continue;
                graph.get((int) p.getPageId()).add((int) p2.getPageId());
            }
        }
        return graph;
    }

    public static void dump(ConcurrentHashMap<String, Page> urlPageMap, long[] numPageVisited) {
        int length = (int)numPageVisited[0];
        Iterator it = urlPageMap.entrySet().iterator();
        //System.out.println(urlPageMap.size());
        JSONArray urlList = new JSONArray();
        //JSONArray nodeList = new JSONArray();
        JSONArray linkList = new JSONArray();
        ArrayList nodeList = new ArrayList();
        for(int i = 0;i<length;i++) {
            nodeList.add(new JSONObject());
        }
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String l = (String)entry.getKey();
            Page p = (Page)entry.getValue();
            LinkedList<String> outgoingUrls = p.getOutgoingUrls();
            //System.out.println(p.getUrl());
            int id = (int)p.getPageId();
            JSONObject urlObj = new JSONObject();
            urlObj.put(id, p.getUrl());
            urlList.add(urlObj);
            JSONObject nodeObj = new JSONObject();
            //nodeObj.put("name",String.valueOf(id));
            nodeObj.put("name",p.getUrl());
            int group = 0;
            String mimeType = p.getMimeType();
            if(mimeType.startsWith("application"))
                group = 1;
            else if(mimeType.startsWith("image"))
                group = 2;
            nodeObj.put("group",group);
            nodeList.set(id, nodeObj);
            for(String outgoingUrl: outgoingUrls) {
                Page p2 = urlPageMap.getOrDefault(outgoingUrl,null);
                if(p2 == null)
                    continue;
                JSONObject linkObj = new JSONObject();
                linkObj.put("source",p.getPageId());
                linkObj.put("target",p2.getPageId());
                linkObj.put("type","suit");
                linkList.add(linkObj);
                System.out.println(p.getPageId()+"  --->   "+p2.getPageId());
            }
            JSONObject d3jsObj = new JSONObject();
            d3jsObj.put("nodes",nodeList);
            d3jsObj.put("links",linkList);
            try {
                PrintWriter json = new PrintWriter(new BufferedWriter(new FileWriter("data.json")));
                json.print(d3jsObj.toJSONString());
                json.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                PrintWriter json = new PrintWriter(new BufferedWriter(new FileWriter("url.json")));
                json.print(urlList.toJSONString());
                json.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void dumpWithGroup(ConcurrentHashMap<String, Page> urlPageMap, long[] numPageVisited, int[] group) {
        int length = (int)numPageVisited[0];
        Iterator it = urlPageMap.entrySet().iterator();
        System.out.println("Altogether "+urlPageMap.size()+" pages");
        //JSONArray urlList = new JSONArray();
        //JSONArray nodeList = new JSONArray();
        JSONArray linkList = new JSONArray();
        ArrayList nodeList = new ArrayList();
        for(int i = 0;i<length;i++) {
            nodeList.add(new JSONObject());
        }
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String l = (String)entry.getKey();
            Page p = (Page)entry.getValue();
            LinkedList<String> outgoingUrls = p.getOutgoingUrls();
            //System.out.println(p.getUrl());
            int id = (int)p.getPageId();
            //JSONObject urlObj = new JSONObject();
            //urlObj.put(id, p.getUrl());
            //urlList.add(urlObj);
            JSONObject nodeObj = new JSONObject();
            //nodeObj.put("name",String.valueOf(id));
            nodeObj.put("name",p.getUrl());
            int g = group[id];
            String mimeType = p.getMimeType();
            nodeObj.put("group",g);
            nodeList.set(id, nodeObj);
            for(String outgoingUrl: outgoingUrls) {
                Page p2 = urlPageMap.getOrDefault(outgoingUrl,null);
                if(p2 == null)
                    continue;
                JSONObject linkObj = new JSONObject();
                linkObj.put("source",p.getPageId());
                linkObj.put("target",p2.getPageId());
                linkObj.put("type","suit");
                linkList.add(linkObj);
                //System.out.println(p.getPageId()+"  --->   "+p2.getPageId());
            }
            JSONObject d3jsObj = new JSONObject();
            d3jsObj.put("nodes",nodeList);
            d3jsObj.put("links",linkList);
            try {
                PrintWriter json = new PrintWriter(new BufferedWriter(new FileWriter("dataWithGroup.json")));
                json.print(d3jsObj.toJSONString());
                json.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


    public static int[] strongConnected(ArrayList<ArrayList<Integer>> graph, int numNode) {
        LinkedList<Integer> stack = new LinkedList<Integer>();
        int[] DFN = new int[numNode];
        int[] LOW = new int[numNode];
        int[] id = new int[]{0};
        int[] groupId = new int[]{0};
        boolean[] visited = new boolean[numNode];
        int[] group = new int[numNode];
        for(int i = 0; i<numNode;i++) {
            if(DFN[i] == 0) {
                tarjan(i,DFN,LOW,stack,id,groupId,visited,group,graph);
            }
        }
        return group;
    }

    private static void tarjan(int u, int[] DFN, int[] LOW, LinkedList<Integer> stack,int[] id,int[] groupId,
                                boolean[] visited, int[] group, ArrayList<ArrayList<Integer>> graph) {
        visited[u] = true;
        id[0] +=1;
        DFN[u] = id[0];
        LOW[u] = id[0];
        stack.push(u);
        ArrayList<Integer> outgoing = graph.get(u);
        for(Integer v: outgoing) {
            if(!visited[v]) {
                tarjan(v,DFN,LOW,stack,id,groupId,visited,group,graph);
                if(LOW[v] < LOW[u])
                    LOW[u] = LOW[v];
            } else if(stack.contains(v) && DFN[v] < LOW[u]){
                LOW[u] = DFN[v];
            }
        }
        if (DFN[u] == LOW[u]) {
            int v;
            groupId[0]++;
            do{
                v = stack.pop();
                group[v] = groupId[0];
            } while(u -v !=0);
        }
    }
}
