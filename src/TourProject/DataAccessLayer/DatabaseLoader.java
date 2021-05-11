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
                dataAccessLayer = new Database();
            }
            case "Mock" -> dataAccessLayer = new DatabaseMock();
        }
    }

    public DataAccessLayer getDataAccessLayer() {
        return dataAccessLayer;
    }
}
