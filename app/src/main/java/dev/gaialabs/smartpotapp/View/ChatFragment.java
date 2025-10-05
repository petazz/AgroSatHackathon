package dev.gaialabs.smartpotapp.View;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import dev.gaialabs.smartpotapp.Controller.ChatFragController;
import dev.gaialabs.smartpotapp.R;
import dev.gaialabs.smartpotapp.Controller.AudioController;
import com.bumptech.glide.Glide;
import android.widget.ImageView;
import dev.gaialabs.smartpotapp.Controller.ElevenLabsTTS;


public class ChatFragment extends Fragment implements AudioController.AudioControllerListener {
    private LinearLayout messagesContainer;
    private EditText inputMessage;
    private Button sendButton;
    private ImageButton micButton;
    private boolean isRecording = false;
    private TextToSpeech textToSpeech;
    private AudioController audioController;
    private ImageView avatarGif;
    private ElevenLabsTTS tts = new ElevenLabsTTS();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ChatFragController.getSingleton().setMyFragment(this);
        // Inicializar vistas correctamente
        messagesContainer = view.findViewById(R.id.messagesContainer);
        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.sendButton);
        micButton = view.findViewById(R.id.micButton);

        /** Cargar el layout del avatar*/
        View avatarView = inflater.inflate(R.layout.avatar_layout, container, false);
        avatarGif = avatarView.findViewById(R.id.avatar_gif);
        /**Cargar el GIF con Glide*/
        Glide.with(this)
                .load(R.drawable.ic_plant) // Ruta del GIF en res/drawable
                .into(avatarGif);
        /**Añadir el avatar al contenedor de mensajes*/
        LinearLayout messagesContainer = view.findViewById(R.id.GIFContainer);
        messagesContainer.addView(avatarView);
        audioController = new AudioController(getContext(), this);
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(view.getContext(), "Idioma no soportado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Error al inicializar TextToSpeech", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el listener de ElevenLabs TTS
        tts.setTTSListener(new ElevenLabsTTS.TTSListener() {
            @Override
            public void onAudioStart() {
                // Cambiar el GIF a "hablando" cuando el audio comienza
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAvatarSpeaking(true);
                    }
                });
            }

            @Override
            public void onAudioEnd() {
                // Cambiar el GIF a "en reposo" cuando el audio termina
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAvatarSpeaking(false);
                    }
                });
            }
        });


        // Configurar el botón de enviar
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //speakText(inputMessage.getText().toString());
                sendMessage();
            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    micButton.setImageResource(R.drawable.ic_black_24dp);
                    isRecording = false;
                    audioController.stopListening();
                    setAvatarSpeaking(true);
                }
                else {
                    micButton.setImageResource(R.drawable.ic_mic);
                    isRecording = true;
                    audioController.startListening();
                    setAvatarSpeaking(false);
                }
            }
        });
        return view; // Importante retornar la vista inflada
    }

    /** Método para cambiar el GIF del avatar según si está hablando o no **/
    private void setAvatarSpeaking(boolean isSpeaking) {
        if (isSpeaking) {
            Glide.with(this)
                    .load(R.drawable.giftblanco) // GIF del avatar hablando
                    .into(avatarGif);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_plant) // GIF del avatar en reposo
                    .into(avatarGif);
        }
    }

    @Override
    public void onSpeechEnded() {
        // Actualizar el estado del botón y cambiar el icono
        if (isRecording) {
            isRecording = false;
            micButton.setImageResource(R.drawable.ic_black_24dp);
        }
    }

    @Override
    public void onTextRecognized(String text) {
        // Mostrar el texto reconocido en el EditText
        inputMessage.setText(text);
    }

    @Override
    public void onError(int error) {
        String errorMessage;
        switch (error) {
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No se pudo reconocer el habla. Intenta de nuevo.";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "No se detectó habla. Intenta de nuevo.";
                break;
            default:
                errorMessage = "Error en el reconocimiento de voz: " + error;
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void sendMessage() {
        String message = inputMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            // Añadir el mensaje al contenedor
            TextView messageView = new TextView(getContext());
            TextView whitemsg = new TextView(getContext());
            messageView.setText(message);
            messageView.setTextColor(Color.parseColor("#000000"));
            messageView.setPadding(16, 16, 16, 16);
            messageView.setBackgroundResource(R.drawable.rounded_message);
            messagesContainer.addView(messageView);
            messagesContainer.addView(whitemsg);
            ChatFragController.getSingleton().makePetition(message);

            // Limpiar el campo de entrada
            inputMessage.setText("");
        }
    }

    public void sendMessageGPT(String gptRes) {
        if (!gptRes.isEmpty()) {
            // Añadir el mensaje al contenedor
            TextView messageView = new TextView(getContext());
            TextView whitemsg = new TextView(getContext());
            messageView.setText(gptRes);
            messageView.setPadding(16, 16, 16, 16);
            messageView.setBackgroundResource(R.drawable.rounded_msg_gpt);
            messageView.setTextColor(Color.parseColor("#000000"));
            messagesContainer.addView(messageView);
            messagesContainer.addView(whitemsg);
            speakText(gptRes);
        }
    }

    private void speakText(String text) {
            tts.convertTextToSpeech(text);

    }


    public void setError() {
        Toast.makeText(getActivity(), getString(R.string.error_chat_response), Toast.LENGTH_LONG).show();
    }
}