package dev.gaialabs.smartpotapp.Model;

public class Plant {
    private int id;
    private String name;
    private float latitude;
    private float longitude;
    private float area;
    private int userId;

    public Plant(int id, String name, float latitude, float longitude, float area, int userId) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.userId = userId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }
    public float getArea() { return area; }
    public int getUserId() { return userId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLatitude(float latitude) { this.latitude = latitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }
    public void setArea(float area) { this.area = area; }
    public void setUserId(int userId) { this.userId = userId; }
}
