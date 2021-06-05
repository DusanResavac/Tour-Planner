package TourProject.DataAccessLayer;

import TourProject.DataAccessLayer.API.Mock;
import TourProject.DataAccessLayer.API.TourAPI;

public class DatabaseLoader {
    private static DatabaseLoader instance = null;
    private DataAccessLayer dataAccessLayer;


    private DatabaseLoader() {

    }

    public static DatabaseLoader getInstance() {
        if (instance == null) {
            instance = new DatabaseLoader();
            instance.readConfigFile();
        }
        return instance;
    }

    private void readConfigFile() {
        switch ((String) Config.getInstance().getAttribute("database")) {
            case "Postgres" -> {
                var db = new Database();
                var tempConfig = Config.getInstance();
                db.openConnection((String) tempConfig.getAttribute("database_url"), (String) tempConfig.getAttribute("database_user"), (String) tempConfig.getAttribute("database_password"));
                dataAccessLayer = db;
            }
            case "Mock" -> dataAccessLayer = new DatabaseMock();
        }
    }

    public DataAccessLayer getDataAccessLayer() {
        return dataAccessLayer;
    }
}
