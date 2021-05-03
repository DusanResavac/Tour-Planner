package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;

public class TourAPILoader {
    private static TourAPILoader instance = null;
    private TourAPI tour;


    private TourAPILoader () {

    }

    public static TourAPILoader getInstance() {
        if (instance == null) {
            instance = new TourAPILoader();
        }
        return instance;
    }

    public void readConfigFile() {
        switch((String)Config.getInstance().getAttribute("api-service")) {
            case "MapQuest":
                var mq = new MapQuest();
                mq.loadAPIKey();
                tour = mq;
                break;
            case "Mock":
                tour = new Mock();
                break;
        }
    }

    public TourAPI getTour() {
        return tour;
    }
}
