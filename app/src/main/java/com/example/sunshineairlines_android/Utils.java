package com.example.sunshineairlines_android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    public static boolean debug=false;
    public static ExecutorService threads= Executors.newFixedThreadPool(1);
    public static String baseUrl="http://10.0.2.2:5000/api";

    public static String sendRequest(String url,String method,String body,boolean strictOk){
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method);
            if(body!=null) {
                conn.setDoOutput(true);
                try(PrintWriter wr=new PrintWriter(conn.getOutputStream())) {
                    wr.write(body);
                }
            }
            int code=conn.getResponseCode();
            if(strictOk&&code!=200){
                return null;
            }
            StringBuilder sb=new StringBuilder();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String sendGet(String url,String body){
        return sendRequest(url,"GET",body,true);
    }
    public static String sendPost(String url,String body){
        return sendRequest(url,"POST",body,false);
    }
}
