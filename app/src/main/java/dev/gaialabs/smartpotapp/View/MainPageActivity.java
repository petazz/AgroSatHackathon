package dev.gaialabs.smartpotapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import dev.gaialabs.smartpotapp.Controller.MainPageController;
import dev.gaialabs.smartpotapp.Controller.RecyclerViews.MainPageAdapter;
import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.R;
import dev.gaialabs.smartpotapp.Service.NASAPowerService;

public class MainPageActivity extends AppCompatActivity {

    private RecyclerView plantsRecyclerView;
    private MainPageAdapter plantAdapter;
    private List<Plant> plants;

    // ✅ Variable para controlar prueba NASA
    private static final boolean ENABLE_NASA_TEST = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        testNasaConnection();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar controlador
        MainPageController.getSingleton().setActivity(this);
        MainPageController.getSingleton().makePetition();

        // ✅ PRUEBA DE CONEXIÓN NASA POWER
        if (ENABLE_NASA_TEST) {
            Log.d("MainActivity", "🧪 === INICIANDO PRUEBA NASA POWER ===");
            Log.d("MainActivity", "🛰️ Probando conexión con satélites NASA...");
            testNasaPowerConnection();
        }
    }

    // ✅ Método separado para la prueba
    private void testNasaPowerConnection() {
        try {
            NASAPowerService.getInstance().testConnection();
            Log.d("MainActivity", "✅ Prueba NASA POWER lanzada correctamente");
        } catch (Exception e) {
            Log.e("MainActivity", "❌ Error lanzando prueba NASA POWER: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setupRecycler() {
        Log.d("MainActivity", "📱 Configurando RecyclerView de parcelas");

        plants = MainPageController.getSingleton().getPlants();

        if (plants == null) {
            Log.w("MainActivity", "⚠️ Lista de plantas está vacía");
            return;
        }

        // ✅ USAR EL ID CORRECTO: plantsRecyclerView
        plantsRecyclerView = findViewById(R.id.plantsRecyclerView);

        // Configurar el adaptador
        plantAdapter = new MainPageAdapter(this, plants);
        plantsRecyclerView.setAdapter(plantAdapter);

        // Configurar GridLayoutManager (como tu código original)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        plantsRecyclerView.setLayoutManager(gridLayoutManager);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Si el número de ítems es impar y es el último ítem, ocupa 2 columnas
                if (plants.size() % 2 != 0 && position == plants.size() - 1) {
                    return 2; // Ocupa todo el ancho
                }
                return 1; // Ocupa una columna
            }
        });

        Log.d("MainActivity", "✅ RecyclerView configurado con " + plants.size() + " parcelas");
    }

    public void notifyDataChanged() {
        Log.d("MainActivity", "🔄 Actualizando datos del RecyclerView");
        if (plantAdapter != null) {
            plantAdapter.notifyDataSetChanged();
            Log.d("MainActivity", "✅ Datos actualizados - " + plants.size() + " parcelas");
        } else {
            Log.w("MainActivity", "⚠️ Adapter es null, no se puede actualizar");
        }
    }

    private void testNasaConnection() {
        Log.d("TEST_NASA", "🚀 Probando NASA POWER (fechas CORREGIDAS)...");

        new Thread(() -> {
            try {
                // ✅ USAR FECHAS VÁLIDAS DE 2024
                String validDate = "20241001"; // 1 de octubre 2024

                String urlStr = "https://power.larc.nasa.gov/api/temporal/daily/point" +
                        "?parameters=T2M,RH2M,PRECTOTCORR" +
                        "&community=Agroclimatology" +
                        "&longitude=-4.4214" +
                        "&latitude=36.7213" +
                        "&start=" + validDate +
                        "&end=" + validDate +
                        "&format=JSON";

                Log.d("TEST_NASA", "📅 Fecha fija: " + validDate);
                Log.d("TEST_NASA", "🌐 URL: " + urlStr);

                java.net.URL url = new java.net.URL(urlStr);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int responseCode = conn.getResponseCode();
                Log.d("TEST_NASA", "📡 Respuesta NASA: " + responseCode);

                if (responseCode == 200) {
                    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String result = response.toString();
                    Log.d("TEST_NASA", "✅ ¡NASA FUNCIONA! Tamaño: " + result.length() + " chars");
                    Log.d("TEST_NASA", "📄 Datos: " + result.substring(0, Math.min(200, result.length())));

                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(this, "🎉 ¡NASA POWER FUNCIONA!", android.widget.Toast.LENGTH_LONG).show();
                    });

                } else {
                    Log.e("TEST_NASA", "❌ Error HTTP: " + responseCode);
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(this, "❌ NASA Error: " + responseCode, android.widget.Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                Log.e("TEST_NASA", "❌ Error: " + e.getMessage());
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(this, "❌ Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

}
