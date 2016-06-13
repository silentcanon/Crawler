package com.canon.webcrawler.net;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Canon on 2015-04-15.
 */
public class Response {
    private String baseUrl = null;
    private String status = null;
    private Document doc = null;
    private String mimeType = null;
    private HttpEntity entity;

    public Response(CloseableHttpResponse res,String baseUrl) throws IOException
    {
        this.baseUrl = baseUrl;
        //System.out.print("GET: "+baseUrl+" ");
        //System.out.print(res.getStatusLine().toString()+" ");
        int statusCode = res.getStatusLine().getStatusCode();
        if(statusCode != 200) {
            this.mimeType = null;
            this.doc = null;
        } else {
            this.status = res.getStatusLine().toString();
            entity = res.getEntity();
            //this.content = EntityUtils.toString(entity);
            ContentType contentType = ContentType.getOrDefault(entity);
            this.mimeType = contentType.getMimeType();
            if(this.doc == null && this.mimeType.equals("text/html")) {
                this.doc = Jsoup.parse(entity.getContent(),null,baseUrl);
            }
            //System.out.println(this.mimeType);
        }
    }

    public String getStatus()
    {
        return this.status;
    }

    public String getBaseUrl()
    {
        return this.baseUrl;
    }

    public Document getDoc() throws IOException{

        return this.doc;
    }

    public String getMimeType() {
        return this.mimeType;
    }


}
