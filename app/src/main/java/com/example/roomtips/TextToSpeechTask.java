package com.example.roomtips;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class TextToSpeechTask extends AsyncTask<String, Void, String> {
    private final String TAG = "TextToSpeechTask";
    private CustomServiceObject services;
    private WeakReference<Activity> activity;

    TextToSpeechTask(CustomServiceObject s, Activity a) {
        services = s;
        activity = new WeakReference<>(a);
    }
    @Override
    protected String doInBackground(String... params) {
        String textToRead = params[0];
        Log.d(TAG, textToRead);
        StreamPlayer streamPlayer = new StreamPlayer();
        try {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder().text(textToRead).accept(SynthesizeOptions.Accept.AUDIO_WAV).voice(SynthesizeOptions.Voice.EN_US_ALLISONVOICE).build();
            InputStream inputStream = services.getTextToSpeech().synthesize(synthesizeOptions).execute();
            streamPlayer.playStream(inputStream);
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
            Log.d(TAG, "Turning on camera");
            Intent cameraIntent = new Intent(activity.get(), CameraActivity.class);
            cameraIntent.putExtra("services", new ServiceParcelable(services));
            activity.get().startActivity(new Intent(activity.get(), CameraActivity.class));
        } else if (action.equals("analyzeImage")) {

            Log.d(TAG, "Analyzing image");
        }
    }
}