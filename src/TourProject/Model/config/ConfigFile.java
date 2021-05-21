package TourProject.Model.config;

public class ConfigFile {
    public String api_key;
    public String api_service;
    public String database;
    public String route_image_folder;
    public String database_url;
    public String database_user;
    public String database_password;

    @Override
    public String toString() {
        return "ConfigFile{" +
                "api_key='" + api_key + '\'' +
                ", api_service='" + api_service + '\'' +
                ", database='" + database + '\'' +
                ", route_image_folder='" + route_image_folder + '\'' +
                ", database_url='" + database_url + '\'' +
                ", database_user='" + database_user + '\'' +
                ", database_password='" + database_password + '\'' +
                '}';
    }
}
