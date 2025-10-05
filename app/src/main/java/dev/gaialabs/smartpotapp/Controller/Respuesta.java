package dev.gaialabs.smartpotapp.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Array;
import java.util.ArrayList;

import dev.gaialabs.smartpotapp.Model.Plant;
import dev.gaialabs.smartpotapp.Model.User;

public class Respuesta {
    private static Respuesta respuesta;
    private Respuesta() {

    }
    public static Respuesta getSingleton() {
        if (respuesta == null)
            respuesta = new Respuesta();
        return respuesta;
    }

    public User parseDataLogin(String body) {
        JsonElement head = JsonParser.parseString(body);
        JsonObject object = head.getAsJsonObject();
        User u = new User(object.get("token").getAsString());
        return (u);
    }

    public ArrayList<Plant> parseUsersPlantsData(String res) {
        JsonElement head = JsonParser.parseString(res);
        JsonArray array = head.getAsJsonArray();
        ArrayList<Plant> plants = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            plants.add(new Plant(
                            object.get("id").getAsInt(),
                            object.get("plantName").getAsString(),
                            object.get("temperature").getAsFloat(),
                            object.get("conductivity").getAsFloat(),
                            object.get("humidity").getAsFloat(),
                            object.get("ownerId").getAsInt()
                    )
            );
        }
        return (plants);
    }

    public String parseGPTResponse(String response) {
        JsonElement jsonElement = JsonParser.parseString(response);
        JsonArray a = jsonElement.getAsJsonObject().get("choices")
                .getAsJsonArray();
        return a.get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();
    }
}
