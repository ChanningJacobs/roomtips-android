package com.example.roomtips;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class Main extends AppCompatActivity {

    private static final String DEBUG_TAG_1 = "Touch";
    private final String TAG = "MainActivity";

    private Assistant watsonAssistant;
    private SpeechToText speechToText;
    private TextToSpeech textToSpeech;
    private SessionResponse watsonSession;
    private StreamPlayer streamPlayer;
    private String myQuestion = "";
    //private String output_path = getApplicationContext().getFilesDir() + "test.3GP";
    private boolean recording = false;
    private MicrophoneHelper microphoneHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streamPlayer = new StreamPlayer();
        microphoneHelper = new MicrophoneHelper(this);
        createServices();
    }
    /*
        Detect touch events on the main activity and update accordingly.
        On tap: Go to full screen camera activity.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getActionMasked();

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(DEBUG_TAG_1,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(DEBUG_TAG_1,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(DEBUG_TAG_1,"Action was UP");
                //startActivity(new Intent(this, CameraActivity.class));
                record();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(DEBUG_TAG_1,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(DEBUG_TAG_1,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
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

    /*
        Show basic message send and receive logic
     */
    private void sessionTest() {
        Log.d(TAG, "Starting session test");
        Log.d(TAG, "Conversation: " + myQuestion);
        new WatsonTask(this).execute(myQuestion);
        Log.d(TAG, "Finished session test");
    }

    private void record() {
        if (!recording) {
            InputStream inputStream = microphoneHelper.getInputStream(true);
            final RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
                    .audio(inputStream)
                    .contentType(ContentType.OPUS.toString())
                    .model("en-US_BroadbandModel")
                    .interimResults(false)
                    .inactivityTimeout(2)
                    .build();
            final BaseRecognizeCallback baseRecognizeCallback = new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechRecognitionResults speechRecognitionResults) {
                    myQuestion = speechRecognitionResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        speechToText.recognizeUsingWebSocket(recognizeOptions, baseRecognizeCallback);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }).start();
            recording = true;
        } else {
            try {
                microphoneHelper.closeInputStream();
                recording = false;
                sessionTest();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /*
        Send message to Watson Assistant test task
     */
    private static class WatsonTask extends AsyncTask<String, Void, String> {
        private final String TAG = "SessionTestTask";
        private WeakReference<Main> mainReference;

        WatsonTask(Main context) {
            mainReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            /*
                Create a new Watson Assistant session if no session is active. Session is used to
                send and receive messages, keeps track of current conversation flow.
             */
            Main main = mainReference.get();
            if (main.watsonSession == null) {
                CreateSessionOptions watsonSessionOptions = new CreateSessionOptions.Builder(main.getString(R.string.assistant_id)).build();
                main.watsonSession = main.watsonAssistant.createSession(watsonSessionOptions).execute();
                Log.d(TAG, main.watsonSession.toString());
            }

            /*
                Take text from parameters and send into service as a message
             */
            String textToSend = params[0];
            Log.d(TAG, textToSend);
            MessageInput input = new MessageInput.Builder().messageType("text").text(textToSend).build();
            MessageOptions messageOptions = new MessageOptions.Builder(main.getString(R.string.assistant_id), main.watsonSession.getSessionId()).input(input).build();
            /*
                Get response from service
             */
            MessageResponse response = main.watsonAssistant.message(messageOptions).execute();
            Log.d(TAG, response.toString());

            if (response.getOutput() != null &&
                    !response.getOutput().getGeneric().isEmpty() &&
                    response.getOutput().getGeneric().get(0).getResponseType().equals("text")) {
                return response.getOutput().getGeneric().get(0).getText();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, s);
            if (!s.isEmpty()) {
                new TextToSpeechTask(mainReference.get()).execute(s);
            }
        }
    }

    /*
        Async task for text to speech feature
     */
    private static class TextToSpeechTask extends AsyncTask<String, Void, String> {
        private final String TAG = "TextToSpeechTask";
        private WeakReference<Main> mainReference;

        TextToSpeechTask(Main context) {
            mainReference = new WeakReference<>(context);
        }
        @Override
        protected String doInBackground(String... params) {
            Main main = mainReference.get();
            String textToRead = params[0];
            Log.d(TAG, textToRead);
            try {
                SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder().text(textToRead).accept(SynthesizeOptions.Accept.AUDIO_WAV).voice(SynthesizeOptions.Voice.EN_US_ALLISONVOICE).build();
                InputStream inputStream = main.textToSpeech.synthesize(synthesizeOptions).execute();
                main.streamPlayer.playStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return "";
        }
    }
}
