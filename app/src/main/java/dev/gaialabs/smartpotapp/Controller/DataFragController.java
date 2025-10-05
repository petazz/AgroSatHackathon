package dev.gaialabs.smartpotapp.Controller;

import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.Model.PlantData;
import dev.gaialabs.smartpotapp.Service.NASAPowerService;
import dev.gaialabs.smartpotapp.View.DataFragment;

public class DataFragController {
    private static final String TAG = "DataFragController";
    private static DataFragController myController;
    private static DataFragment myFragment;
    private String plantData;
    private ArrayList<PlantData> analizedData;
    private Plant currentPlant;

    private DataFragController() {}

    public static DataFragController getSingleton() {
        if (myController == null)
            myController = new DataFragController();
        return myController;
    }

    public void setFragment(DataFragment fragment) {
        myFragment = fragment;
        Log.d(TAG, "📱 Fragment asignado al controller");
    }

    public void setPlantData(String json) {
        plantData = json;
        try {
            currentPlant = new Gson().fromJson(json, Plant.class);
            Log.d(TAG, "🌱 Planta cargada: " + currentPlant.getName());
            Log.d(TAG, "📍 Coordenadas: " + currentPlant.getLatitude() + ", " + currentPlant.getLongitude());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error parseando planta: " + e.getMessage());
        }
    }

    public ArrayList<PlantData> getAnalizedData() {
        return analizedData;
    }

    public void analizePlantData() {
        Log.d(TAG, "🚀 === INICIANDO ANÁLISIS CON NASA POWER ===");

        if (currentPlant != null) {
            double lat = currentPlant.getLatitude();
            double lon = currentPlant.getLongitude();

            Log.d(TAG, "🗺️  Analizando: " + currentPlant.getName());
            Log.d(TAG, "📍 Coordenadas: " + lat + ", " + lon);
            Log.d(TAG, "📏 Área: " + currentPlant.getArea() + " hectáreas");

            // Mostrar loading mientras esperamos datos NASA
            analizedData = new ArrayList<>();
            analizedData.add(new PlantData("Cargando NASA POWER", 0f));
            if (myFragment != null) {
                myFragment.setupRecycler();
            }

            // Solicitar datos reales de NASA POWER
            NASAPowerService.getInstance().getWeatherData(lat, lon, new NASAPowerService.WeatherCallback() {
                @Override
                public void onSuccess(ArrayList<PlantData> weatherData) {
                    // Este código se ejecutará con DATOS REALES diferentes para cada parcela
                    Log.d(TAG, "🎉 DATOS REALES RECIBIDOS para " + currentPlant.getName());
                    analizedData = weatherData;
                    updateChatContextWithCurrentPlant();
                    // ... resto del código
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "❌ Error: " + error);
                    loadFallbackDataAndUpdateChat();
                }
            });

        } else {
            Log.w(TAG, "⚠️  No hay planta seleccionada");
            loadFallbackDataAndUpdateChat();
        }
    }

    // ✅ MÉTODO CLAVE: Actualizar contexto del chatbot
    private void updateChatContextWithCurrentPlant() {
        if (currentPlant != null && analizedData != null) {
            Log.d(TAG, "🤖 === ACTUALIZANDO CONTEXTO DEL CHATBOT ===");
            Log.d(TAG, "📝 Parcela: " + currentPlant.getName());
            Log.d(TAG, "📍 Ubicación: " + currentPlant.getLatitude() + ", " + currentPlant.getLongitude());
            Log.d(TAG, "📊 Datos a pasar: " + analizedData.size() + " parámetros");

            // Pasar TODOS los datos al chatbot
            ChatFragController.getSingleton().setPlantContext(currentPlant, analizedData);

            // Log detallado de los datos pasados
            for (PlantData data : analizedData) {
                Log.d(TAG, "  ➤ " + data.getName() + ": " + data.getFormattedNumber());
            }

            Log.d(TAG, "✅ Contexto del chatbot actualizado correctamente");

            // Debug: Verificar que el contexto se guardó
            ChatFragController.getSingleton().logCurrentContext();

        } else {
            Log.w(TAG, "⚠️ No se pudo actualizar contexto del chat");
            Log.w(TAG, "   currentPlant: " + (currentPlant != null ? "✓" : "✗"));
            Log.w(TAG, "   analizedData: " + (analizedData != null ? analizedData.size() + " items" : "null"));
        }
    }

    private void loadFallbackDataAndUpdateChat() {
        Log.d(TAG, "🔄 Cargando datos de respaldo...");

        analizedData = new ArrayList<>();
        analizedData.add(new PlantData("Sin conexión NASA", 0f));
        analizedData.add(new PlantData("Temperatura", 18.5f));
        analizedData.add(new PlantData("Humedad", 65.0f));
        analizedData.add(new PlantData("Precipitación", 2.3f));
        analizedData.add(new PlantData("Radiación Solar", 20.1f));
        analizedData.add(new PlantData("Velocidad Viento", 2.8f));

        // ✅ También actualizar chatbot con datos de respaldo
        updateChatContextWithCurrentPlant();

        if (myFragment != null && myFragment.getActivity() != null) {
            myFragment.getActivity().runOnUiThread(() -> {
                myFragment.setupRecycler();
            });
        }
    }

    // ✅ Método público para forzar actualización del chat
    public void forceUpdateChatContext() {
        Log.d(TAG, "🔄 Actualizacion forzada del contexto del chat");
        updateChatContextWithCurrentPlant();
    }

    // ✅ Getter para la planta actual (por si lo necesitas)
    public Plant getCurrentPlant() {
        return currentPlant;
    }
}
