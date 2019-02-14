package com.example.roomtips;

import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

class CustomServiceObject {
    private Assistant watsonAssistant;
    private TextToSpeech textToSpeech;
    private SpeechToText speechToText;
    private SessionResponse watsonSession;

    public Assistant getWatsonAssistant() {
        return watsonAssistant;
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public SpeechToText getSpeechToText() {
        return speechToText;
    }

    public void setWatsonSession(SessionResponse r) {
        watsonSession = r;
    }

    public SessionResponse getWatsonSession() {
        return watsonSession;
    }

    public CustomServiceObject(Assistant a, TextToSpeech t, SpeechToText s) {
        this.watsonAssistant = a;
        this.textToSpeech = t;
        this.speechToText = s;
    }
}
