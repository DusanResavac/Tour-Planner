package TourProject.model.config;

import java.util.Arrays;

public class ConfigFile {
    public String apiKey;
    public String api_service;

    @Override
    public String toString() {
        return "ConfigFile{" +
                "apiKey='" + apiKey + '\'' +
                ", apiService='" + api_service + '\'' +
                '}';
    }
}
