package dev.gaialabs.smartpotapp.Controller;

import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.MediaPlayer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
public class ElevenLabsTTS {
    public interface TTSListener {
        void onAudioStart();
        void onAudioEnd();
    }
    // Constantes
    private static final String API_KEY = "YOUR API ELEVENLABS "; // Reemplaza con tu API Key
    private static final String VOICE_ID = "K12f1RfmJdu33I0SKO9S"; // Reemplaza con el ID de la voz
    private TTSListener listener;

    public void setTTSListener(TTSListener listener) {
        this.listener = listener;
    }
    // Metodo para convertir texto a voz
    public void convertTextToSpeech(String text) {
        // Aquí implementaremos la solicitud a la API
        OkHttpClient client = new OkHttpClient();

        // Cuerpo de la solicitud
        String jsonBody = "{\"text\": \"" + text + "\", \"voice_settings\": {\"stability\": 0.5, \"similarity_boost\": 0.75}}";
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        // Crear la solicitud
        Request request = new Request.Builder()
                .url("https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID)
                .addHeader("xi-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // Enviar la solicitud
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                // Manejar el error
                                                e.printStackTrace();
                                            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // La respuesta es un archivo de audio (MP3)
                    byte[] audioData = response.body().bytes();

                    // Reproducir el audio
                    playAudio(audioData);
                } else {
                    // Manejar el error
                    System.out.println("Error: " + response.code() + " - " + response.message());
                }
            }
        });
    }

    // Metodo para reproducir el audio
    private void playAudio(byte[] audioData) {
        try {
            File tempFile = File.createTempFile("audio", ".mp3");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(audioData);
            fos.close();

            // Reproducir el audio usando MediaPlayer
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(tempFile.getAbsolutePath());
            mediaPlayer.prepare();

            // Notificar que el audio ha comenzado
            if (listener != null) {
                listener.onAudioStart();
            }

            mediaPlayer.start();

            // Notificar que el audio ha terminado
            mediaPlayer.setOnCompletionListener(mp -> {
                if (listener != null) {
                    listener.onAudioEnd();
                }
                tempFile.delete(); // Limpiar el archivo temporal después de reproducir
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

