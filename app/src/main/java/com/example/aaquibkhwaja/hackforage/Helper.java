package com.example.aaquibkhwaja.hackforage;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    private Map<String, String> LanMap = new HashMap<String, String>(){{
        put("English", "en");
        put("हिन्दी", "hi");
        put("ಕನ್ನಡ", "kn");
        put("বাঙালি", "bn");
        put("মালায়ালম", "ml");
        put("தமிழ்", "ta");
        put("తెలుగు", "te");
        put("मराठी", "mr");
        put("español", "es");
    }};

    private String finalResult;
    private boolean looping = false;
    /** Called when the activity is first created. */
    public String getResult(String InputString, String lan) {
        // TODO Auto-generated method stub
        String LanCode = LanMap.get(lan);
        String OutputString = null;
        try {
            OutputString = translate(InputString, LanCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            OutputString = "Error";
        }
        return OutputString;
    }

    public String[] inputToolOutput(String input, String lan) throws JSONException {
        String input1 = input.replaceAll(" ", "%20");
        lan = LanMap.get(lan);
        StringBuilder url2 = new StringBuilder("https://inputtools.google.com/request?");
        url2.append("text=").append(input1);
        url2.append("&itc=").append(lan).append("-t-i0-und");
        url2.append("&num=10");
        url2.append("&cp=0");
        url2.append("&cs=1");
        url2.append("&ie=utf-8");
        url2.append("&oe=utf-8");
        url2.append("&app=test");
        looping = true;
        boolean called = false;
        while (looping) {
            if (!called) {
                PlacesTask placesTask = new PlacesTask();
                placesTask.execute(url2.toString());
                called = true;
            }
        }
        String OutputString = finalResult;

        JSONArray JO = new JSONArray(OutputString);
        JSONArray JO1 = new JSONArray(JO.get(1).toString());

        JSONArray Output = new JSONArray(new JSONArray(JO1.get(0).toString()).get(1).toString());
        String result[] = new String[Output.length()];
        int size;
        size = Output.length();
        for (int i = 0; i < Output.length() && i < 5; i++) {
            result[i] = Output.get(i).toString();
            if (i == 4) {
                size = 4;
            }
        }

        return result;
    }


    private String translate(String input, String LanCode) {
        String input1 = input.replaceAll(" ", "%20");
        StringBuilder url1 = new StringBuilder("https://translate.google.com/translate_a/single?");
        url1.append("client=z");
        url1.append("&sl=").append(LanCode);
        url1.append("&tl=en-CN");
        url1.append("&ie=UTF-8");
        url1.append("&oe=UTF-8");
        url1.append("&dt=t");
        url1.append("&dt=rm");
        url1.append("&q=").append(input1);
        looping = true;
        boolean called = false;

        while (looping) {
            if (!called) {
                PlacesTask placesTask = new PlacesTask();
                placesTask.execute(url1.toString());
                called = true;
                looping = false;
                finalResult = placesTask.data;
            }
        }
        return finalResult.split("\"")[1];
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            int responseCode = urlConnection.getResponseCode();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        } finally{
            iStream.close();
            urlConnection.disconnect();
        }
        finalResult = data;
        looping = false;

        return data;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
        }
    }
}
