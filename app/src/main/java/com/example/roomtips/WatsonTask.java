package com.example.roomtips;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;

import java.lang.ref.WeakReference;

public class WatsonTask extends AsyncTask<String, Void, MessageResponse> {
    private final String TAG = "SessionTestTask";
    private CustomServiceObject services;
    private WeakReference<Activity> activity;

    WatsonTask(CustomServiceObject s, Activity a) {
        services = s;
        activity = new WeakReference<>(a);
    }

    @Override
    protected MessageResponse doInBackground(String... params) {
            /*
                Take text from parameters and send into service as a message
             */
        String textToSend = params[0];
        Log.d(TAG, "Sending text: " + textToSend);

        if (services.getWatsonSession() == null) {
            CreateSessionOptions watsonSessionOptions = new CreateSessionOptions.Builder(activity.get().getString(R.string.assistant_id)).build();
            services.setWatsonSession(services.getWatsonAssistant().createSession(watsonSessionOptions).execute());
            Log.d(TAG, "Session: " + services.getWatsonSession());
        }

        MessageInput input = new MessageInput.Builder().messageType("text").text(textToSend).build();
        MessageOptions messageOptions = new MessageOptions.Builder(activity.get().getString(R.string.assistant_id), services.getWatsonSession().getSessionId()).input(input).build();
            /*
                Get response from service
             */
        MessageResponse response = services.getWatsonAssistant().message(messageOptions).execute();
        Log.d(TAG, response.toString());

        return response;
    }

    @Override
    protected void onPostExecute(MessageResponse response) {
        super.onPostExecute(response);
        String text = "";
        String action = "";
        if (response.getOutput() != null) {
            if (!response.getOutput().getGeneric().isEmpty() &&
                    response.getOutput().getGeneric().get(0).getResponseType().equals("text")) {
                text = response.getOutput().getGeneric().get(0).getText();
            }
            if (response.getOutput().getActions() != null && !response.getOutput().getActions().isEmpty()) {
                action = response.getOutput().getActions().get(0).getName();
            }
        }
        Log.d(TAG, "Text to be read: " + text);
        Log.d(TAG, "Action to take: " + action);
        new SpeechToTextTask(services, activity.get()).execute(text, action);
    }
}
