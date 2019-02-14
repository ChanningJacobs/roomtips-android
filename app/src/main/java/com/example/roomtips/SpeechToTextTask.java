package com.example.roomtips;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class SpeechToTextTask extends AsyncTask<String, Void, String> {
    private final String TAG = "TextToSpeechTask";
    private WeakReference<Main> mainReference;

    SpeechToTextTask(Main context) {
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
        return params[1];
    }

    @Override
    protected void onPostExecute(String action) {
        runAction(action);
    }

    private void runAction(String action) {
        if (action.equals("turnOnCamera")) {
            mainReference.get().startActivity(new Intent(mainReference.get(), CameraActivity.class));
        } else if (action.equals("analyzeImage")) {
            // Analyze image
        }
    }
}