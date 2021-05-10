package TourProject.DataAccessLayer.API;

import TourProject.model.api.TourInformation;

import java.util.concurrent.CompletableFuture;

public class Mock implements TourAPI {

    @Override
    public CompletableFuture<TourInformation> getRouteInformation(String start, String end, int tourId) {
        // return mock data
        return null;
    }

}
