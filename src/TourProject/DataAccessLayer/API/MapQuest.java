package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;
import TourProject.model.TourInformation;

public class MapQuest implements TourAPI {

    private static MapQuest instance = null;
    private String apiKey = null;

    private MapQuest (){}

    public static MapQuest getInstance() {
        if (instance == null) {
            instance = new MapQuest();
            instance.loadAPIKey();
        }
        return instance;
    }

    private void loadAPIKey() {
        instance.apiKey = (String) Config.getInstance().getAttribute("apiKey");
    }

    @Override
    public TourInformation getRouteInformation(String start, String end) {
        // TODO: Make request to get route information
        return null;
    }

    @Override
    public String getRouteImage(String start, String end) {
        // TODO: Make request to get image of route
        return null;
    }
}
