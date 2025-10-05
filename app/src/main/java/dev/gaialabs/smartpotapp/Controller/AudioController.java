package dev.gaialabs.smartpotapp.Controller;


import android.content.Context;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import java.util.ArrayList;
import java.util.Locale;
public class AudioController {

    // Interfaz para comunicar el texto reconocido y errores
    public interface AudioControllerListener {
        void onTextRecognized(String text);
        void onError(int error);
        void onSpeechEnded();
    }
    private Context context;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private AudioControllerListener listener;

    public AudioController(Context context, AudioControllerListener listener) {
        this.context = context;
        this.listener = listener;
        initializeSpeechRecognizer();
        setupRecognizerIntent();
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("AudioController", "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("AudioController", "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d("AudioController", "onRmsChanged");
            }
            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("AudioController", "onBufferReceived");
            }
            @Override
            public void onEndOfSpeech() {
                Log.d("AudioController", "onEndOfSpeech");
                listener.onSpeechEnded();
            }
            @Override
            public void onError(int error) {
                Log.d("AudioController", "onError");
                listener.onError(error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    Log.d("AudioController", "onResults: " + recognizedText);
                    listener.onTextRecognized(recognizedText); // Enviar el texto al listener
                }
                listener.onSpeechEnded();
            }
            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("AudioController", "onPartialResults");
            }
            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("AudioController", "onEvent");
            }
        });
    }

    private void setupRecognizerIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    }

    public void startListening() {
        speechRecognizer.startListening(recognizerIntent);
        Log.d("AudioController", "Listening started");
    }

    public void stopListening() {
        speechRecognizer.stopListening();
        Log.d("AudioController", "Listening stopped");
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            Log.d("AudioController", "SpeechRecognizer destroyed");
        }
    }
}
