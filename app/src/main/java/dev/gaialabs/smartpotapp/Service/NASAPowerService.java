package dev.gaialabs.smartpotapp.Service;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import dev.gaialabs.smartpotapp.Model.PlantData;

public class NASAPowerService {
    private static final String TAG = "NASAPowerService";
    private static final String BASE_URL = "https://power.larc.nasa.gov/api/temporal/daily/point";
    private static NASAPowerService instance;
    private OkHttpClient client;

    public interface WeatherCallback {
        void onSuccess(ArrayList<PlantData> weatherData);
        void onError(String error);
    }

    private NASAPowerService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .build();
    }

    public static NASAPowerService getInstance() {
        if (instance == null) {
            instance = new NASAPowerService();
        }
        return instance;
    }

    public void getWeatherData(double latitude, double longitude, WeatherCallback callback) {
        String url = buildApiUrl(latitude, longitude);
        Log.d(TAG, "🚀 === SOLICITANDO DATOS NASA POWER ===");
        Log.d(TAG, "📍 Coordenadas: " + latitude + ", " + longitude);
        Log.d(TAG, "🌐 URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "AgroSat-SpaceAppsChallenge/1.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMsg = "Error de conexión NASA POWER: " + e.getMessage();
                Log.e(TAG, "❌ " + errorMsg);
                e.printStackTrace();
                callback.onError(errorMsg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "📡 Respuesta recibida de NASA - Código: " + response.code());

                if (!response.isSuccessful()) {
                    String errorMsg = "Error HTTP NASA POWER: " + response.code() + " - " + response.message();
                    Log.e(TAG, "❌ " + errorMsg);
                    callback.onError(errorMsg);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "✅ Datos recibidos - Tamaño: " + responseBody.length() + " caracteres");

                    // Log de muestra de la respuesta (primeros 500 chars)
                    Log.d(TAG, "📄 Respuesta (muestra): " + responseBody.substring(0, Math.min(500, responseBody.length())));

                    ArrayList<PlantData> weatherData = parseWeatherData(responseBody, latitude, longitude);
                    Log.d(TAG, "🎉 ÉXITO! Parámetros parseados: " + weatherData.size());
                    callback.onSuccess(weatherData);

                } catch (Exception e) {
                    String errorMsg = "Error procesando datos NASA: " + e.getMessage();
                    Log.e(TAG, "❌ " + errorMsg);
                    e.printStackTrace();
                    callback.onError(errorMsg);
                }
            }
        });
    }

    private String buildApiUrl(double latitude, double longitude) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Calendar calendar = Calendar.getInstance();

        // Fecha final: ayer (NASA POWER tiene 1-2 días de retraso)
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String endDate = dateFormat.format(calendar.getTime());

        // Fecha inicial: 7 días antes
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        String startDate = dateFormat.format(calendar.getTime());

        String url = BASE_URL +
                "?parameters=T2M,RH2M,PRECTOTCORR,ALLSKY_SFC_SW_DWN,PS,WS2M" +
                "&community=Agroclimatology" +
                "&longitude=" + String.format(Locale.US, "%.4f", longitude) +
                "&latitude=" + String.format(Locale.US, "%.4f", latitude) +
                "&start=" + startDate +
                "&end=" + endDate +
                "&format=JSON";

        Log.d(TAG, "📅 Rango de fechas: " + startDate + " a " + endDate);
        return url;
    }

    private ArrayList<PlantData> parseWeatherData(String jsonResponse, double lat, double lon) {
        ArrayList<PlantData> weatherData = new ArrayList<>();

        try {
            Log.d(TAG, "🔄 Parseando respuesta JSON...");
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            // Verificar estructura de respuesta
            if (!jsonObject.has("properties")) {
                Log.e(TAG, "❌ Respuesta no tiene 'properties'");
                return weatherData;
            }

            JsonObject properties = jsonObject.getAsJsonObject("properties");
            if (!properties.has("parameter")) {
                Log.e(TAG, "❌ Respuesta no tiene 'parameter'");
                return weatherData;
            }

            JsonObject parameter = properties.getAsJsonObject("parameter");
            Log.d(TAG, "📊 Parámetros disponibles: " + parameter.keySet());

            // Extraer parámetros individuales
            JsonObject t2m = parameter.has("T2M") ? parameter.getAsJsonObject("T2M") : null;
            JsonObject rh2m = parameter.has("RH2M") ? parameter.getAsJsonObject("RH2M") : null;
            JsonObject prec = parameter.has("PRECTOTCORR") ? parameter.getAsJsonObject("PRECTOTCORR") : null;
            JsonObject rad = parameter.has("ALLSKY_SFC_SW_DWN") ? parameter.getAsJsonObject("ALLSKY_SFC_SW_DWN") : null;
            JsonObject ps = parameter.has("PS") ? parameter.getAsJsonObject("PS") : null;
            JsonObject ws = parameter.has("WS2M") ? parameter.getAsJsonObject("WS2M") : null;

            // Encontrar la fecha más reciente con datos válidos
            String lastDate = getLastAvailableDate(t2m);
            if (lastDate == null) {
                Log.e(TAG, "❌ No hay fechas válidas en los datos");
                return weatherData;
            }

            Log.d(TAG, "📅 Fecha de datos más reciente: " + lastDate);
            Log.d(TAG, "📍 Datos específicos para coordenadas: " + lat + ", " + lon);

            // Extraer valores y crear PlantData
            if (t2m != null && t2m.has(lastDate) && !t2m.get(lastDate).isJsonNull()) {
                float temperature = t2m.get(lastDate).getAsFloat();
                weatherData.add(new PlantData("Temperatura", temperature));
                Log.d(TAG, "🌡️  Temperatura: " + temperature + "°C");
            }

            if (rh2m != null && rh2m.has(lastDate) && !rh2m.get(lastDate).isJsonNull()) {
                float humidity = rh2m.get(lastDate).getAsFloat();
                weatherData.add(new PlantData("Humedad", humidity));
                Log.d(TAG, "💧 Humedad: " + humidity + "%");
            }

            if (prec != null && prec.has(lastDate) && !prec.get(lastDate).isJsonNull()) {
                float precipitation = prec.get(lastDate).getAsFloat();
                weatherData.add(new PlantData("Precipitación", precipitation));
                Log.d(TAG, "🌧️  Precipitación: " + precipitation + "mm");
            }

            if (rad != null && rad.has(lastDate) && !rad.get(lastDate).isJsonNull()) {
                float radiation = rad.get(lastDate).getAsFloat();
                weatherData.add(new PlantData("Radiación Solar", Math.round(radiation * 100f) / 100f));
                Log.d(TAG, "☀️ Radiación: " + radiation + " MJ/m²");
            }

            if (ps != null && ps.has(lastDate) && !ps.get(lastDate).isJsonNull()) {
                float pressure = ps.get(lastDate).getAsFloat();
                weatherData.add(new PlantData("Presión", Math.round(pressure * 100f) / 100f));
                Log.d(TAG, "🎈 Presión: " + pressure + " kPa");
            }

            if (ws != null && ws.has(lastDate) && !ws.get(lastDate).isJsonNull()) {
                float windSpeed = ws.get(lastDate).getAsFloat();
                weatherData.add(new PlantData("Velocidad Viento", Math.round(windSpeed * 100f) / 100f));
                Log.d(TAG, "🌪️  Viento: " + windSpeed + " m/s");
            }

            Log.d(TAG, "✅ Parseado completado - " + weatherData.size() + " parámetros extraídos");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error crítico parseando JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return weatherData;
    }

    private String getLastAvailableDate(JsonObject dateObject) {
        if (dateObject == null) return null;

        String lastDate = null;
        // Iterar sobre todas las fechas para encontrar la más reciente con datos válidos
        for (Map.Entry<String, JsonElement> entry : dateObject.entrySet()) {
            String date = entry.getKey();
            JsonElement value = entry.getValue();

            if (!value.isJsonNull() && value.getAsDouble() > -999.0) {
                lastDate = date; // Las fechas vienen ordenadas, la última será la más reciente
            }
        }

        Log.d(TAG, "🗓️  Última fecha con datos válidos: " + lastDate);
        return lastDate;
    }

    // ✅ Método de prueba para debugging
    public void testConnection() {
        Log.d(TAG, "🧪 === PRUEBA DE CONEXIÓN NASA POWER ===");
        double testLat = 36.7213; // Málaga
        double testLon = -4.4214;

        getWeatherData(testLat, testLon, new WeatherCallback() {
            @Override
            public void onSuccess(ArrayList<PlantData> weatherData) {
                Log.d(TAG, "✅ PRUEBA EXITOSA - " + weatherData.size() + " parámetros");
                for (PlantData data : weatherData) {
                    Log.d(TAG, "  ✓ " + data.getName() + ": " + data.getFormattedNumber());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ PRUEBA FALLIDA: " + error);
            }
        });
    }
}
