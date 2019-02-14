package com.example.roomtips;

import android.util.Log;

import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.InputStream;

import static android.support.constraint.Constraints.TAG;

public class RecordAudio {

    public static void record(final Main main) {
        if (!main.recording) {
            InputStream inputStream = main.microphoneHelper.getInputStream(true);
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
                    new WatsonTask(main).execute(speechRecognitionResults.getResults().get(0).getAlternatives().get(0).getTranscript());
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        main.speechToText.recognizeUsingWebSocket(recognizeOptions, baseRecognizeCallback);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }).start();
            main.recording = true;
        } else {
            try {
                main.microphoneHelper.closeInputStream();
                main.recording = false;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
