package com.canon.webcrawler.utils;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by Canon on 2015-04-19.
 */
public class Other {
    public static String safeUrl(String url) {
        URL url2 = convertToURLEscapingIllegalCharacters(url);
        if(url2 != null) {
            String u = url2.toString();
            if(u.endsWith("/"))
                u = u.substring(0,u.length()-1);
            return u;
        }
        return null;
    }

    public static URL convertToURLEscapingIllegalCharacters(String string) {
        try {
            //String decodedURL = URLDecoder.decode(string, "UTF-8");
            if(!string.startsWith("http"))
                return null;
            URL url = new URL(string);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}