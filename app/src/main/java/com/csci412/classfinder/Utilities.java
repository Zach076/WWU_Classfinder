package com.csci412.classfinder;

import android.support.v4.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Utilities {

    public static int getClasses(List<Pair<String, String>> formData){

        HttpsURLConnection connection = null;
        StringBuilder result = null;
        int linecount = 0;

        try{
            URL url = new URL("https://admin.wwu.edu/pls/wwis/wwsktime.ListClass");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //fixed header values
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Host", "admin.wwu.edu");
            connection.setRequestProperty("Origin", "https://admin.wwu.edu");
            connection.setRequestProperty("Referer", "https://admin.wwu.edu/pls/wwis/wwsktime.SelClass");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Android ClassFinder Application");

            StringBuilder sbParams = new StringBuilder();
            sbParams.append("sel_subj=dummy&sel_subj=dummy&sel_gur=dummy&sel_gur=dummy&sel_attr=dummy&sel_site=dummy&sel_day=dummy&sel_open=dummy");
            int i = 0;
            for (Pair<String, String> value : formData) {
                sbParams.append("&");
                sbParams.append(value.first)
                        .append("=")
                        .append(value.second);
                i++;
            }

            //update content length
            connection.setRequestProperty("Content-Length", Integer.toString(sbParams.toString().getBytes().length));

            connection.setReadTimeout(20000);
            connection.setConnectTimeout(30000);
            connection.connect();

            DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
            outStream.writeBytes(sbParams.toString());
            outStream.flush();
            outStream.close();

            InputStream inStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                linecount++;
                result.append(line + "\n");
            }

            System.out.println(connection.getResponseCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return linecount;
    }
}
