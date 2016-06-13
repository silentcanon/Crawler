package com.canon.webcrawler.net;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by Canon on 2015-04-15.
 */
public class Request {
    private PoolingHttpClientConnectionManager cm = null;
    //private CloseableHttpClient httpClient = null;
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/600.5.17 (KHTML, like Gecko) Version/8.0.5 Safari/600.5.17";
    private String cookie = null;
    private String baseUrl = null;
    public Request() throws Exception {
        /** old code using connection pool
        this.cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);
        this.httpClient = HttpClients.custom().setConnectionManager(this.cm).build();
         */
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Request setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getCookie() {
        return cookie;
    }

    public Request setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public Request setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }


    public Response getResponse(String baseUrl) {
        /**
        HttpClientContext context = HttpClientContext.create();
        HttpGet httpGet = new HttpGet(baseUrl);
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl);
        Response res = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                res = new Response(response,baseUrl);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException ex) {
            System.out.println("Invalid host name: " + baseUrl);
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Failed to get response from " + baseUrl);
            ex.printStackTrace();
        }
        return res;
    }
/**
    public void close() throws IOException{
        this.httpClient.close();
    }
 */


}
