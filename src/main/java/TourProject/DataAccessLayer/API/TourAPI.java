package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.Model.api.TourInformation;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

public interface TourAPI {

    public CompletableFuture<TourInformation> getRouteInformation (String start, String end);
    public CompletableFuture<byte[]> getRouteImage(String sessionId, int tourId);
}
