/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sparkleshare.android.utils;

import java.net.URL;

/**
 *
 * @author richy
 */
public class URLPathDecoder {
   
    // "path=recipes%2F.empty&hash=da1585c347ba6fbe32f75d9ebe5c4531dfe88e5f&name=.empty"
    public static String decode(String url) {
    
        String path = url.replace("%2F", "/");
        path = path.split("=")[1].split("&")[0];
        
        return path;
    }
    
    
}
