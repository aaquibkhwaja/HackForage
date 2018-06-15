package com.example.aaquibkhwaja.hackforage;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InputToolsUtil extends AsyncTask<String, String, String> {

    String transliterated = null;
    boolean flag = false;

    @Override
    protected String doInBackground(String... strings) {
        try {
            setTransliterate(downloadUrl(strings[0]));
        } catch(Exception e){
            Log.d("Background Task", e.toString());
        }
        return transliterated;
    }

    public void setTransliterate(String str) throws InterruptedException {
        synchronized (this) {
            if (flag == true){
                wait();
            }
            this.transliterated = str;
            flag = true;
            notifyAll();
        }
    }

    public String getTransliterated() throws InterruptedException {
        synchronized (this) {
            if (flag == false){
                wait();
            }
            flag = false;
            notifyAll();
            return this.transliterated;
        }
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
            Log.e("Excptn downloading url", e.toString());
        } finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
