/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sparkleshare.android.utils;


/**
 *
 * @author richy
 */
public class URLPathDecoder {
   
    public static String decode(String url) {
    
        String path = url.replace("%2F", "/");
        path = path.split("=")[1].split("&")[0];
        
        return path;
    }
    
    
}
