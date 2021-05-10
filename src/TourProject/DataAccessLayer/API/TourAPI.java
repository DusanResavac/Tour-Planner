package TourProject.DataAccessLayer.API;

import TourProject.model.api.TourInformation;

import java.util.concurrent.CompletableFuture;

public interface TourAPI {

    public CompletableFuture<TourInformation> getRouteInformation (String start, String end, int tourId);
}
