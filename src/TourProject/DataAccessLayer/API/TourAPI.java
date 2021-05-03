package TourProject.DataAccessLayer.API;

import TourProject.model.api.TourInformation;

public interface TourAPI {

    public void getRouteInformation (String start, String end, int tourId, CallbackViewModel c);
}
