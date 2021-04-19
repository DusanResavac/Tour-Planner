package TourProject.DataAccessLayer.API;

import TourProject.model.TourInformation;

public interface TourAPI {
    public TourInformation getRouteInformation (String start, String end);
    public String getRouteImage (String start, String end);
}
