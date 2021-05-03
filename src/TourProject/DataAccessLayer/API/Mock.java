package TourProject.DataAccessLayer.API;

import TourProject.model.TourInformation;

public class Mock implements TourAPI {

    @Override
    public TourInformation getRouteInformation(String start, String end) {
        // return mock data
        return null;
    }

    @Override
    public String getRouteImage(String start, String end) {
        // return mock data
        return null;
    }
}
