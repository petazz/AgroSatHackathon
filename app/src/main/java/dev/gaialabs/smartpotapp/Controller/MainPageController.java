package dev.gaialabs.smartpotapp.Controller;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.Model.PlantData;
import dev.gaialabs.smartpotapp.Model.User;
import dev.gaialabs.smartpotapp.View.MainActivity;
import dev.gaialabs.smartpotapp.View.MainPageActivity;

public class MainPageController {
    private static MainPageActivity myactivity;
    private static MainPageController controller;
    private ArrayList<Plant> plants;

    private MainPageController() {
        plants = new ArrayList<>();
    }

    public static MainPageController getSingleton() {
        if (controller == null)
            controller = new MainPageController();
        return controller;
    }

    public void setActivity(MainPageActivity activity) {
        myactivity = activity;
    }

    public void makePetition() {
        // En lugar de hacer petición HTTP, añadimos plantas de demo para el hackathon
        System.out.println("Cargando plantas de demo para el hackathon...");

        // Limpiar plantas existentes
        plants.clear();

        // Añadir 3 parcelas/plantas de prueba con datos climáticos
        // Plant(int id, String name, float lat, float lon, float area, int userId)
        plants.add(new Plant(1, "Parcela Olivar - Málaga", 36.7213f, -4.4214f, 2.5f, 1));
        plants.add(new Plant(2, "Parcela Tomates - Granada", 37.1773f, -3.5986f, 1.8f, 1));
        plants.add(new Plant(3, "Parcela Aguacates - Almería", 36.8381f, -2.4597f, 3.2f, 1));

        System.out.println("Se han cargado " + plants.size() + " parcelas de demo");

        // Configurar el RecyclerView con las plantas
        if (myactivity != null) {
            myactivity.setupRecycler();
        } else {
            System.out.println("ERROR: myactivity es null");
        }
    }

    public void parseUserPlantsData(String res) {
        // Este método se usaba para parsear respuesta HTTP - ya no es necesario
        // plants = Respuesta.getSingleton().parseUsersPlantsData(res);
        // myactivity.setupRecycler();
    }

    public void setPlantData(Plant plant) {
        DataFragController.getSingleton().setPlantData(new Gson().toJson(plant));
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void handleError(String res) {
        plants.clear();
        plants.add(new Plant(-1, "ERROR: No se pudieron cargar las parcelas", -1f, -1f, -1f, -1));
        if (myactivity != null) {
            myactivity.setupRecycler();
        }
    }
}
