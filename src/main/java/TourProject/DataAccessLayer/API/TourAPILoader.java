package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;

public class TourAPILoader {
    private static TourAPILoader instance = null;
    private TourAPI tourApi;


    private TourAPILoader () {

    }

    public static TourAPILoader getInstance() {
        if (instance == null) {
            instance = new TourAPILoader();
            instance.readConfigFile();
        }
        return instance;
    }

    private void readConfigFile() {
        switch ((String) Config.getInstance().getAttribute("api_service")) {
            case "MapQuest" -> {
                var mq = new MapQuest();
                mq.loadAPIKey();
                tourApi = mq;
            }
            case "Mock" -> tourApi = new Mock();
        }
    }

    public TourAPI getTourAPI() {
        return tourApi;
    }
}
