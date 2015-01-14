package com.dragonhack2015.mattandjoe.dogears;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    private TextView inputSpeechTV;
    private ImageButton speakButton;
    private Button ipButton;
    private EditText ipText;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private  String server_url = "http://";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputSpeechTV = (TextView) findViewById((R.id.textSpeechInput));
        speakButton = (ImageButton) findViewById(R.id.speak_button);

        inputSpeechTV.setText(getString(R.string.speak_button_text));

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForSpeech();
            }
        });

        ipText = (EditText) findViewById(R.id.ipEditText);

        ipButton = (Button) findViewById(R.id.ipButton);

        ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server_url = server_url + ipText.getText().toString();
                Toast.makeText(getApplicationContext(), "IP set to: " + server_url, Toast.LENGTH_LONG).show();
            }
        });


    }

    public void promptForSpeech() {

        clearDisplayText();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {
            // Toast the user if voice is not supported
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {


                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String shout = result.get(0);

                    if (shout.equals("set")) {
                        shout = "sit";
                    }
                    inputSpeechTV.setText(shout);


                    // Post the spoken text to the server
                    new AsyncPost().execute(shout);
                } else {
                    // Speech input was not successful. Reset TextView
                    inputSpeechTV.setText(getString(R.string.speak_button_text));
                }
                break;
            }
        }
    }

    public void clearDisplayText() {
        inputSpeechTV.setText("");
    }

    public void postData(final String data) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost(server_url);
        try {
            // create a list to store HTTP variables and their values
            List nameValuePairs = new ArrayList();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("shout", data));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // send the variable and value, in other words post, to the URL
            HttpResponse response = httpclient.execute(httppost);

//            Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();

        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        } catch (IllegalArgumentException iae){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncPost extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);

            return null;
        }
    }
}
