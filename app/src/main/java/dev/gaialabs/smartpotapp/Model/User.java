package dev.gaialabs.smartpotapp.Model;

public class User {
    private int id;
    private String email;
    private String apiKey;

    public User() {
        this.id = -1;
        this.email = null;
        this.apiKey = null;
    }
    public User(String apiKey)
    {
        this.id = -1;
        this.apiKey = apiKey;
        this.email = null;
    }
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserDetails(User user) {
        this.id = user.id;
        this.apiKey = user.getApiKey();
        this.email = user.getEmail();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
