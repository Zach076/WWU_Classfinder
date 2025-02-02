package com.csci412.classfinder;

import android.content.res.Resources;
import android.util.Log;

import androidx.core.util.Pair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

public class Utilities {

    public static int dpToPx(int dp){
        return dp * (int) Resources.getSystem().getDisplayMetrics().density;
    }

    public static Pair<String, String> getClassInfo (String term, String course){

        HttpsURLConnection connection = null;
        Pair<String, String> classInfo = null;//(class title, class description

        try{
            URL url = new URL("https://admin.wwu.edu/pls/wwis/wwsktime.SelText");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            StringBuilder sbParams = new StringBuilder();
            sbParams.append("subj_crse=");
            sbParams.append(term);
            sbParams.append(course);

            //update content length
            connection.setRequestProperty("Content-Length", Integer.toString(sbParams.toString().getBytes().length));

            connection.setReadTimeout(3000);
            connection.setConnectTimeout(5000);
            connection.connect();

            DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
            outStream.writeBytes(sbParams.toString());
            outStream.flush();
            outStream.close();

            InputStream inStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line + "\n");
            }

            connection.disconnect();

            //response processing
            String result = response.toString();
            int i = result.indexOf("<br />");
            result = result.substring(i);
            i = result.indexOf("<center>");
            result = result.substring(0, i-1);
            result = result.replaceAll("<br />", "");
            result = result.replaceAll("\n", "");

            classInfo = new Pair<>(course, result);
        } catch (Exception e){
            //no classes or some other error
            Log.e("utilities", "No classes or source has changed?");
            return null;
        } finally {
            if(connection != null)
                connection.disconnect();
        }
        return classInfo;
    }

    public static TreeMap<String, List<Course>> getClasses(List<Pair<String, String>> formData){

        HttpsURLConnection connection = null;
        TreeMap<String, List<Course>> classes;

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

            for (Pair<String, String> value : formData) {
                String[] vals = value.second.split(" ");
                for(String val : vals){
                  sbParams.append("&");
                  sbParams.append(value.first)
                          .append("=")
                          .append(val);
                }
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
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                result.append(line + "\n");
            }

            connection.disconnect();

            //get the correct table from the html
            Document doc = Jsoup.parse(result.toString());
            Elements tables = doc.select("table");
            Element classTable = tables.get(1);
            Elements rows = classTable.select("tr");

            //data structures to store information in
            classes = new TreeMap<>();
            String department = null;
            List<Course> courses = null;
            Course course = null;
            //remove all empty rows
            for(Element row : rows){
                if(isEmptyRow(row)){
                    row.remove();
                }
            }
            //removing only removes from doc we need to re get rows
            tables = doc.select("table");
            classTable = tables.get(1);
            rows = classTable.select("tr");

            //parse html
            int size = rows.size();
            int i = 0;
            while (i < size - 5) {
                Element row = rows.get(i);
                //add department and classes to map
                //clear courses and get new department
                if (row.getElementsByClass("ddheader").size() > 0) {
                    if (department != null)
                        classes.put(department, courses);

                    courses = new ArrayList<>();
                    department = row.getElementsByClass("ddheader").get(0).text();
                    i += 2;//skip label row
                    row = rows.get(i);
                }

                //parse and add this course
                course = new Course();
                course.term = formData.get(1).second;
                course.dept = department;

                Elements cols = row.select("td");

                //waitlist available?
                if (cols.get(0).hasText())
                    course.waitlist = true;

                course.course = cols.get(1).text();
                course.title = cols.get(2).text();
                course.crn = cols.get(3).child(0).val();
                course.cap = cols.get(4).text();
                course.enrl = cols.get(5).text();
                course.avail = cols.get(6).text();
                course.instructor = cols.get(7).text();
                course.dates = cols.get(8).text();

                i++;
                row = rows.get(i);
                cols = row.select("td");

                if (cols.get(1).hasText())
                    course.attrs = cols.get(1).text();

                course.location.add(cols.get(3).text());
                course.credits = cols.get(4).text();

                if (cols.size() > 5)
                    course.chrgs = cols.get(5).text();

                course.times.add(cols.get(2).text());

                i++;
                row = rows.get(i);
                cols = row.select("td");

                while (!isEndofClass(row)) {
                    if (!cols.get(0).hasText() || cols.get(0).text().equals("")) {
                        if (cols.size() > 3) {
                            course.times.add(cols.get(1).text());
                            course.location.add(cols.get(2).text());
                        } else if (cols.size() > 2) {
                            course.prereq.concat(cols.get(2).text());
                        } else {
                            course.additional.add(cols.get(1).text());
                        }
                    } else {
                        if (cols.get(0).text().equals("Restrictions:")) {
                            course.restrictions = cols.get(2).text();
                        } else {
                            course.prereq = cols.get(2).text();
                        }
                    }

                    i++;
                    if(i >= size)
                        break;
                    row = rows.get(i);
                    cols = row.select("td");
                }
                courses.add(course);
            }

            classes.put(department, courses);
        } catch (Exception e){
            //no classes or some other error
            //returning null will report no classes to a
            Log.e("utilities", "No classes or source has changed?");
            return null;
        } finally {
            if(connection != null)
                connection.disconnect();
        }
        return classes;
    }

    public static List<HashMap<String, String>> getMenuAttributes(){

        HttpsURLConnection connection = null;
        StringBuilder result = null;
        int linecount = 0;

        try{
            URL url = new URL("https://admin.wwu.edu/pls/wwis/wwsktime.SelClass");
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



        } catch (Exception e){
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();

        }
        try {
        //get the correct table from the html
        Document doc = Jsoup.parse(result.toString());
        Elements tables = doc.select("table");
        Element attributes = tables.get(1);
        Elements rows = attributes.select("tr");

        //todo handle no classes found

        //data structures to store information in
        List<HashMap<String, String>> menuAttributes = new ArrayList<HashMap<String,String>>();
        for(int i = 0; i < 7; i++){
            menuAttributes.add(new HashMap<>());
        }


            //removing only removes from doc we need to re get rows
            tables = doc.select("table");
            attributes = tables.get(1);
            rows = attributes.select("tr");
            int defaultValue = 0;
            //parse html
            //only go through the first 3 rows because those are the only ones that contain scrollable options
            for(int i = 0; i < 3; i++) {
                Element row = rows.get(i);
                Elements td = row.select("td");
                Elements select = td.get(1).select("select");
                Elements options = select.get(0).select("option");

                int size = options.size();
                int j = 0;
                //adding all the options to the hash map
                menuAttributes.get(6).put("" + defaultValue,options.get(j).text());
                defaultValue++;
                while(j < size) {
                    menuAttributes.get(i * 2).put(options.get(j).text(),options.get(j).val());
                    j++;
                }

                select = td.get(3).select("select");
                options = select.get(0).select("option");
                size = options.size();
                j = 0;
                menuAttributes.get(6).put("" + defaultValue,options.get(j).text());
                defaultValue++;
                //adding all the options to the hash map
                while(j < size) {
                    menuAttributes.get(i * 2 + 1).put(options.get(j).text(),options.get(j).val());
                    j++;
                }
            }

        return menuAttributes;
        } catch (Exception e){
            return null;
        }
    }

    private static boolean isEndofClass(Element row) {
        Elements cols = row.select("td");
        if(cols.size() > 8) {
            if(cols.get(1).hasText()) {
                return true;
            }
        }
        return row.getElementsByClass("ddheader").size() > 0;
    }

    private static boolean isEmptyRow(Element row){
        Elements cols = row.select("td");
        for(Element col :cols){
            String text = col.text();
            if(!text.equals("") && !text.equals("TBA")){
                return false;
            }
        }
        return true;
    }
}
