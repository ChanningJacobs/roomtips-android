package com.example.roomtips;

import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;

import java.lang.ref.WeakReference;

public class WatsonTask extends AsyncTask<String, Void, MessageResponse> {
    private final String TAG = "SessionTestTask";
    private WeakReference<Main> mainReference;

    WatsonTask(Main context) {
        mainReference = new WeakReference<>(context);
    }

    @Override
    protected MessageResponse doInBackground(String... params) {
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
        Log.d(TAG, "Sending text: " + textToSend);
        MessageInput input = new MessageInput.Builder().messageType("text").text(textToSend).build();
        MessageOptions messageOptions = new MessageOptions.Builder(main.getString(R.string.assistant_id), main.watsonSession.getSessionId()).input(input).build();
            /*
                Get response from service
             */
        MessageResponse response = main.watsonAssistant.message(messageOptions).execute();
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
        new SpeechToTextTask(mainReference.get()).execute(text, action);
    }
}
