package com.example.aaquibkhwaja.hackforage;

import android.os.AsyncTask;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Util extends AsyncTask<String, String, String> {

    String translated = null;
    boolean flag = false;

    @Override
    protected String doInBackground(String... params) {
        Translate translate = TranslateOptions.newBuilder()
                .setApiKey("AIzaSyCLdSqEFi9ypAsGGJM5A_fe2xKGDjwBSv8")
                .build().getService();
        Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage(params[1]);
        Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage("en");
        Translate.TranslateOption model = Translate.TranslateOption.model("nmt");
        Translation translation = translate.translate(params[0], srcLang, tgtLang, model);

        try {
            setTranslated(translation.getTranslatedText());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return translated;
    }

    public void setTranslated(String s) throws InterruptedException {
        synchronized (this) {
            if (flag == true) {
                wait();
            }
            this.translated = s;
            flag = true;
            notifyAll();
        }
    }

    public String getTranslated() throws InterruptedException {
        synchronized (this) {
            if (flag == false) {
                wait();
            }
            flag = false;
            notifyAll();
            return translated;
        }
    }
}
