package TourProject.DataAccessLayer;

import TourProject.model.config.ConfigFile;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {

    public static Config instance = null;
    private ConfigFile configFile = null;
    public static final String configPath = "src/TourProject/config.json";

    private Config() {}

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            Gson gson = new Gson();
            try {
                String b = String.join("", Files.readAllLines(Paths.get(configPath)));
                instance.configFile = gson.fromJson(b, ConfigFile.class);
                //System.out.println(instance.configFile);
            } catch (IOException ioException) {
                instance = null;
                ioException.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Get Value of config by passing an attributeName. Might change this method, since it heavily relies on Reflection
     * @param attributeName The name of the attribute, of which the value should be returned
     */
    public Object getAttribute (String attributeName) {
        Class<?> c = getInstance().configFile.getClass();
        try {
            return (Object) c.getField(attributeName).get(getInstance().configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
