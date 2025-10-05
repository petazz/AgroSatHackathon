package dev.gaialabs.smartpotapp.Controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import dev.gaialabs.smartpotapp.Controller.RecyclerViews.ResetPasswordController;
import dev.gaialabs.smartpotapp.Model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Peticion {
    private static Peticion peticion;
    private static final String URL = "http://79.145.35.97:8888";

    private Peticion()
    {

    }

    public static Peticion getSingleton() {
        if (peticion == null)
            peticion = new Peticion();
        return peticion;
    }

    public void makeLogin(String email, String password)
    {
        String json = "{\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"password\": \""+ password + "\"\n" +
                "}";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(URL + "/api/auth/login")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getCause());
                System.out.println(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res;
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainController.getSingleton().parseDataLogin(res, email);
                        }
                    });
                } else {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        MainController.getSingleton().setLoginError(res);
                    });
                }
            }
        });
    }

    public void getUserPlants(User user)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + user.getApiKey())
                .url(URL + "/api/plants/")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getCause());
                System.out.println(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful())
                            MainPageController.getSingleton().parseUserPlantsData(res);
                        else
                            MainPageController.getSingleton().handleError(res);
                    }
                });
            }
        });
    }

    public void setIAData(String message) {
        Log.d("PETICION_AI", "🤖 === INICIANDO PETICIÓN CHATBOT ===");
        Log.d("PETICION_AI", "📝 Mensaje: " + message);

        // ✅ Obtener prompt dinámico con datos reales de la parcela
        String promptContent = ChatFragController.getSingleton().buildDynamicPrompt(message);

        String data = "{"
                + "\"model\": \"gpt-4o-mini\","
                + "\"messages\": ["
                + "{"
                + "\"role\": \"system\","
                + "\"content\": \"" + promptContent.replace("\"", "\\\"") + "\""
                + "},"
                + "{"
                + "\"role\": \"user\","
                + "\"content\": \"" + message.replace("\"", "\\\"") + "\""
                + "}"
                + "],"
                + "\"temperature\": 0.7"
                + "}";

        Log.d("PETICION_AI", "✅ Prompt dinámico generado - Longitud: " + promptContent.length());

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(data, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + "Your OPENAI API KEY")
                .url("https://api.openai.com/v1/chat/completions")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PETICION_AI", "❌ Error en petición: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String resp = response.body().string();
                    Log.d("PETICION_AI", "✅ Respuesta recibida de OpenAI");

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ChatFragController.getSingleton().parseData(resp);
                        }
                    });
                } else {
                    Log.e("PETICION_AI", "❌ Error HTTP: " + response.code());
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        ChatFragController.getSingleton().setErrorOnResponse();
                    });
                }
            }
        });
    }


    public void restablecerPass(String password, String userEmail) {
        String json = "{\n" +
                "    \"mail\": \"" + userEmail + "\",\n" +
                "    \"password\": \""+ password + "\"\n" +
                "}";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(URL + "/api/auth/change-password")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getCause());
                System.out.println(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res;
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ResetPasswordController.getSingleton().parseResponse(res);
                        }
                    });
                } else {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        Log.d("ERROR: PETIICION: ", res);
                    });
                }
            }
        });
    }

    public void forgotPassword(String email) {
        String json = "{\n" +
                "    \"mail\": \"" + email + "\",\n" +
                "}";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(URL + "/api/auth/forgot-password")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getCause());
                System.out.println(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res;
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ForgotPasswordController.getSingleton().parseResponse(res);
                        }
                    });
                } else {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        ForgotPasswordController.getSingleton().setError(res);
                    });
                }
            }
        });
    }

    public void verifyAccount(String code, String email) {
        String json = "{\n" +
                "    \"code\": \"" + code + "\",\n" +
                "    \"mail\": \"" + email + "\"\n" +
                "}";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(URL + "/api/auth/verify-account")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getCause());
                System.out.println(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res;
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            VerifyCodeController.getSingleton().parseResponse(res);
                        }
                    });
                } else {
                    assert response.body() != null;
                    res = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        ForgotPasswordController.getSingleton().setError(res);
                    });
                }
            }
        });
    }
}
