package dev.gaialabs.smartpotapp.Controller;

import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.Model.PlantData;
import dev.gaialabs.smartpotapp.View.ChatFragment;

public class ChatFragController {
    private static final String TAG = "ChatFragController";
    private static ChatFragController myController;
    private ChatFragment myFragment;

    // ✅ Variables para datos de parcela específica
    private Plant currentPlant;
    private ArrayList<PlantData> currentPlantData;

    private ChatFragController() {}

    public static ChatFragController getSingleton() {
        if (myController == null)
            myController = new ChatFragController();
        return myController;
    }

    // ✅ MÉTODO QUE NECESITA ChatFragment
    public void setMyFragment(ChatFragment fragment) {
        myFragment = fragment;
        Log.d(TAG, "📱 ChatFragment asignado al controller");
    }

    public void makePetition(String message) {
        Peticion.getSingleton().setIAData(message);
    }

    public void parseData(String response) {
        String res = Respuesta.getSingleton().parseGPTResponse(response);
        assert res != null;
        if (myFragment != null) {
            myFragment.sendMessageGPT(res);
        }
    }

    public void setErrorOnResponse() {
        if (myFragment != null) {
            myFragment.setError();
        }
    }

    // ✅ MÉTODO CLAVE: Recibir datos de parcela específica
    public void setPlantContext(Plant plant, ArrayList<PlantData> plantData) {
        this.currentPlant = plant;
        this.currentPlantData = new ArrayList<>();

        // Hacer copia profunda de los datos
        if (plantData != null) {
            for (PlantData data : plantData) {
                this.currentPlantData.add(new PlantData(data.getName(), data.getNumber()));
            }
        }

        Log.d(TAG, "📊 CONTEXTO ACTUALIZADO:");
        if (plant != null) {
            Log.d(TAG, "   🌱 Parcela: " + plant.getName());
            Log.d(TAG, "   📍 Ubicación: " + plant.getLatitude() + ", " + plant.getLongitude());
            Log.d(TAG, "   📏 Área: " + plant.getArea() + " ha");
        }
        Log.d(TAG, "   📈 Datos NASA: " + (plantData != null ? plantData.size() : 0) + " parámetros");
    }

    // ✅ GENERAR PROMPT DINÁMICO CON TODOS LOS DATOS
    public String buildDynamicPrompt(String userMessage) {
        Log.d(TAG, "🤖 === GENERANDO PROMPT DINÁMICO ===");
        Log.d(TAG, "📝 Mensaje usuario: " + userMessage);

        String plantInfo = buildPlantInfo();
        String realTimeData = buildRealTimeData();
        String expertKnowledge = buildExpertKnowledge();

        String promptContent =
                "Eres la IA agrícola más avanzada de AgroSat, el sistema ganador del NASA Space Apps Challenge 2025. " +
                        "Tienes acceso directo a datos satelitales de NASA POWER en tiempo real.\\n\\n" +
                        plantInfo +
                        realTimeData +
                        expertKnowledge +
                        "\\nRESPUESTA: Usa SIEMPRE los datos específicos mostrados arriba. " +
                        "Menciona la parcela por nombre, ubicación y valores exactos cuando sea relevante.";

        Log.d(TAG, "✅ Prompt generado - Longitud: " + promptContent.length() + " caracteres");
        return promptContent;
    }

    private String buildPlantInfo() {
        if (currentPlant == null) {
            Log.w(TAG, "⚠️ No hay parcela actual");
            return "ESTADO: No hay parcela seleccionada. Proporciona información general de AgroSat.\\n\\n";
        }

        String cropType = detectCropType(currentPlant.getName());
        String location = getLocationName(currentPlant);
        String growthStage = getGrowthStage(currentPlant.getName());

        String plantInfo = String.format(
                "=== PARCELA ESPECÍFICA ===\\n" +
                        "📝 Nombre: %s\\n" +
                        "📍 Ubicación: %s\\n" +
                        "🗺️  Coordenadas: %.4f°N, %.4f°W\\n" +
                        "📏 Superficie: %.1f hectáreas\\n" +
                        "🌱 Cultivo: %s\\n" +
                        "📊 ID Parcela: %d\\n" +
                        "🌾 Etapa: %s\\n\\n",
                currentPlant.getName(),
                location,
                currentPlant.getLatitude(),
                Math.abs(currentPlant.getLongitude()),
                currentPlant.getArea(),
                cropType,
                currentPlant.getId(),
                growthStage
        );

        Log.d(TAG, "✅ Info parcela: " + currentPlant.getName() + " - " + cropType);
        return plantInfo;
    }

    private String buildRealTimeData() {
        if (currentPlantData == null || currentPlantData.isEmpty()) {
            Log.w(TAG, "⚠️ No hay datos NASA POWER");
            return "=== DATOS SATELITALES ===\\n" +
                    "🛰️ Estado: Conectando con NASA POWER...\\n" +
                    "📡 Satélites: Esperando datos en tiempo real\\n\\n";
        }

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("=== DATOS NASA POWER (TIEMPO REAL) ===\\n");
        dataBuilder.append("🛰️ Fuente: Satélites NASA en órbita\\n");
        dataBuilder.append("🕐 Actualización: Últimas 24 horas\\n");
        dataBuilder.append("📊 Parámetros monitoreados:\\n");

        // Agregar cada dato con análisis
        for (PlantData data : currentPlantData) {
            String analysis = analyzeParameter(data);
            dataBuilder.append("   • ")
                    .append(data.getName())
                    .append(": ")
                    .append(data.getFormattedNumber())
                    .append(" ")
                    .append(analysis)
                    .append("\\n");
        }
        dataBuilder.append("\\n");

        Log.d(TAG, "📊 Datos NASA procesados: " + currentPlantData.size() + " parámetros");
        return dataBuilder.toString();
    }

    private String buildExpertKnowledge() {
        return "=== CONOCIMIENTO ESPECIALIZADO ===\\n" +
                "🎯 Especialización: Agricultura de precisión con tecnología espacial\\n" +
                "🌍 Región: Andalucía Oriental (clima mediterráneo)\\n" +
                "📈 Capacidades:\\n" +
                "   • Interpretación de datos satelitales NASA\\n" +
                "   • Análisis de condiciones microclimáticas\\n" +
                "   • Recomendaciones específicas por cultivo\\n" +
                "   • Predicción de riesgos agrícolas\\n" +
                "   • Optimización de riego y fertilización\\n";
    }

    private String detectCropType(String plantName) {
        String name = plantName.toLowerCase();
        if (name.contains("olivar") || name.contains("olivo")) {
            return "Olivo (Olea europaea) - Cultivo mediterráneo perenne";
        } else if (name.contains("tomate")) {
            return "Tomate (Solanum lycopersicum) - Hortaliza de regadío";
        } else if (name.contains("aguacate")) {
            return "Aguacate (Persea americana) - Cultivo subtropical";
        } else if (name.contains("citrico") || name.contains("naranja")) {
            return "Cítricos - Frutales mediterráneos";
        }
        return "Cultivo mediterráneo adaptado al clima local";
    }

    private String getLocationName(Plant plant) {
        double lat = plant.getLatitude();
        double lon = plant.getLongitude();

        if (lat >= 36.7 && lat <= 36.8 && lon >= -4.5 && lon <= -4.3) {
            return "Málaga, Costa del Sol";
        } else if (lat >= 37.1 && lat <= 37.2 && lon >= -4.0 && lon <= -3.5) {
            return "Granada, Vega de Granada";
        } else if (lat >= 36.8 && lat <= 36.9 && lon >= -2.5 && lon <= -2.4) {
            return "Almería, Campo de Dalías";
        }
        return "Andalucía Oriental";
    }

    private String getGrowthStage(String plantName) {
        // Determinar etapa según época del año (octubre)
        if (plantName.toLowerCase().contains("olivar")) {
            return "Maduración de frutos (recolección próxima)";
        } else if (plantName.toLowerCase().contains("tomate")) {
            return "Producción activa (cosecha continua)";
        } else if (plantName.toLowerCase().contains("aguacate")) {
            return "Desarrollo de frutos (crecimiento activo)";
        }
        return "Desarrollo vegetativo activo";
    }

    private String analyzeParameter(PlantData data) {
        String name = data.getName().toLowerCase();
        float value = data.getNumber();

        if (name.contains("temperatura")) {
            if (value < 10) return "(FRÍO - riesgo)";
            if (value > 35) return "(CALOR - estrés)";
            return "(ÓPTIMO)";
        } else if (name.contains("humedad")) {
            if (value < 30) return "(SECO - regar)";
            if (value > 80) return "(HÚMEDO - ventilación)";
            return "(ADECUADO)";
        } else if (name.contains("precipitacion")) {
            if (value == 0) return "(SIN LLUVIA)";
            if (value > 10) return "(LLUVIA ABUNDANTE)";
            return "(LLUVIA MODERADA)";
        }
        return "(MONITOREADO)";
    }

    // ✅ Método para debugging
    public void logCurrentContext() {
        Log.d(TAG, "=== CONTEXTO ACTUAL DEL CHATBOT ===");
        Log.d(TAG, "Parcela: " + (currentPlant != null ? currentPlant.getName() : "null"));
        Log.d(TAG, "Datos NASA: " + (currentPlantData != null ? currentPlantData.size() : 0) + " parámetros");

        if (currentPlantData != null) {
            for (PlantData data : currentPlantData) {
                Log.d(TAG, "  - " + data.getName() + ": " + data.getFormattedNumber());
            }
        }
    }
}
