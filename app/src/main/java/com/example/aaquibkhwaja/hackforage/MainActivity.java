package com.example.aaquibkhwaja.hackforage;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private ImageButton mSpeakBtn;
    private TextView voiceInput;
    private Button searchButton;

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    String finalTextString = "";
    String lanResult[] = new java.lang.String[5];
    String filteredData[] = new java.lang.String[5];
    boolean flag[] = new boolean[100];
    int cnt = 0;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerOnSpinnerItemSelection();

        voiceInput = findViewById(R.id.voiceInput);
        mSpeakBtn = findViewById(R.id.btnSpeak);
        searchButton = findViewById(R.id.button);

        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        final Helper h = new Helper();
        Helper.buildMap();
        Helper.buildFinalMap();
//
//        final AutoComplete<String> adapter = new AutoComplete<>(this,android.R.layout.simple_dropdown_item_1line);
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    if (s.toString().length() < finalTextString.length()) {
//                        flag[--cnt] = false;
//                        String arr[] = finalTextString.split(" ");
//                        finalTextString = "";
//                        for (int i = 0; i < arr.length - 1; i++) {
//                            finalTextString = finalTextString + arr[i] + " ";
//                        }
//                    }
//                    if (s.toString().length() == 0) {
//                        return;
//                    }
//                    if (s.charAt(s.length() - 1) == ' ') {
//                        if (!flag[s.toString().split(" ").length - 1]) {
//                            String input = s.toString();
//                            String arr[] = input.split(" ");
//                            String last = arr[arr.length - 1];
//                            String language1 = spinner.getSelectedItem().toString();
//                            lanResult = h.inputToolOutput(last, language1);
//                            arr[arr.length - 1] = lanResult[0];
//                            finalTextString = "";
//
//                            for (String a : arr) {
//                                finalTextString = finalTextString + a + " ";
//                            }
//
//                            flag[cnt++] = true;
//                            editText.setText(finalTextString);
//                            editText.setSelection(finalTextString.length());
//                        }
//                    } else {
//                        adapter.clear();
//
//                        String input = s.toString();
//                        String arr[] = input.split(" ");
//                        String last = arr[arr.length - 1];
//                        String language1 = spinner.getSelectedItem().toString();
//                        lanResult = h.inputToolOutput(last, language1);
//
//                        for (String a : lanResult) {
//                            String toadd = finalTextString + a;
//                            adapter.add(toadd);
//                        }
//
//                        adapter.notifyDataSetChanged();
//                        editText.setAdapter(adapter);
//                        editText.showDropDown();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = spinner.getSelectedItem().toString();
                System.out.println(language);
                String input = voiceInput.getText().toString();
                System.out.println(input);

//                String query = h.getResult(input, language);

//                System.out.println("query " + query);

                final String query = voiceInput.getText().toString();
                String optimizedQuery = Helper.optimize(query, Helper.getLanCode(language));

                String inputTools = null;
                String translatedQuery = null;

                if (optimizedQuery == null ) {
                    try {
                        inputTools = Helper.inputTools(query, language);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        translatedQuery = Helper.translateText(inputTools, Helper.getLanCode(language));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    translatedQuery = optimizedQuery;
                }

                System.out.println(translatedQuery);
                if (translatedQuery != null) {
                    translatedQuery.replace(" ", "+");
                }
                String finalQuery = Helper.getFinalMap(translatedQuery);
                try {
                    Uri uri = Uri.parse("http://dl.flipkart.com/dl/search?q=" + finalQuery + "&query=" + finalQuery + "&sid=all");
                    Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(launchIntent);
                } catch (Exception e) {
                    System.out.println("Exception = " + e.toString());
                }
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = findViewById(R.id.spinner);

        ArrayList<String> array = new ArrayList<String>();
        array.add("English");
        array.add("हिन्दी");
        array.add("ಕನ್ನಡ");
        array.add("বাঙালি");
        array.add("মালায়ালম");
        array.add("தமிழ்");
        array.add("తెలుగు");
        array.add("मराठी");
        array.add("español");

        ArrayAdapter<String> mAdapter;
        mAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, array);

        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public class AutoComplete<String> extends ArrayAdapter<String> implements Filterable {

        AutoComplete(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter()
            {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence)
                {
                    FilterResults results = new FilterResults();
                    //If there's nothing to filter on, return the original data for your list
                    ArrayList<String> filterResultsData = new ArrayList<String>();

                    results.values = lanResult;
                    results.count = size;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults)
                {
                    filteredData = (java.lang.String[]) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
