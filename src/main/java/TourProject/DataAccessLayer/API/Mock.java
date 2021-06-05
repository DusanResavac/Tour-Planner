package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.Model.api.TourInformation;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

public class Mock implements TourAPI {

    @Override
    public CompletableFuture<TourInformation> getRouteInformation(String start, String end) {
        // return mock data
        return null;
    }

    @Override
    public CompletableFuture<byte[]> getRouteImage(String sessionId, int tourId) {
        return null;
    }



}
