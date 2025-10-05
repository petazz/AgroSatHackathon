package dev.gaialabs.smartpotapp.Model;

public class PlantData {
    private final String name;
    private final Float number;

    public PlantData(String name, Float number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public Float getNumber() {
        return number;
    }

    public String getFormattedNumber() {
        if (name.contains("Temperatura")) {
            return number + "°C";
        } else if (name.contains("Humedad")) {
            return Math.round(number) + "%";
        } else if (name.contains("Precipitación")) {
            return number + "mm";
        } else if (name.contains("Radiación")) {
            return number + " MJ/m²";
        } else if (name.contains("Presión")) {
            return number + " kPa";
        } else if (name.contains("Viento") || name.contains("Velocidad")) {
            return number + " m/s";
        } else if (name.contains("Cargando") || name.contains("Sin conexión")) {
            return "...";
        }
        return number.toString();
    }
}
