package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;
import TourProject.model.TourInformation;

public class MapQuest implements TourAPI {

    private String apiKey = null;


    public void loadAPIKey() {
        apiKey = (String) Config.getInstance().getAttribute("apiKey");
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
