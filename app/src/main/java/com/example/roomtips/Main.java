package com.example.roomtips;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

public class Main extends AppCompatActivity {

    private static final String DEBUG_TAG_1 = "Touch";
    private final String TAG = "MainActivity";

    protected Assistant watsonAssistant;
    protected SpeechToText speechToText;
    protected TextToSpeech textToSpeech;
    protected CustomServiceObject services;
    protected RecordAudio recordAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createServices();
        services = new CustomServiceObject(watsonAssistant,textToSpeech, speechToText);
        recordAudio = new RecordAudio(this);
    }
    /*
        Detect touch events on the main activity and update accordingly.
        On tap: Go to full screen camera activity.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG_1, "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG_1, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG_1, "Action was UP");
                //startActivity(new Intent(this, CameraActivity.class));
                recordAudio.record(services);
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG_1, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG_1, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    /*
        Initialize IBM services
        Current Services being used: Watson Assistant, Text to Speech, Speech to Text
     */
    private void createServices() {
        Log.d(TAG, "Creating services");
        watsonAssistant = new Assistant("2018-11-08",
                new IamOptions.Builder().apiKey(getString(R.string.assistant_apikey)).build());
        watsonAssistant.setEndPoint(getString(R.string.assistant_url));

        textToSpeech = new TextToSpeech();
        textToSpeech.setIamCredentials(new IamOptions.Builder().apiKey(getString(R.string.TTS_apikey)).build());
        textToSpeech.setEndPoint(getString(R.string.TTS_url));

        speechToText = new SpeechToText();
        speechToText.setIamCredentials(new IamOptions.Builder().apiKey(getString(R.string.STT_apikey)).build());
        speechToText.setEndPoint(getString(R.string.STT_url));

        Log.d(TAG, "Finished creating services");
    }
}
