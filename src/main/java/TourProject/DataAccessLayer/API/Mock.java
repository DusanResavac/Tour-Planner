package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.DataAccessLayer;
import TourProject.Model.api.TourInformation;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

public class Mock implements TourAPI {


    @Override
    public CompletableFuture<TourInformation> getRouteInformation(String start, String end) {
        if (start == null || end == null || start.equals("") || end.equals("") ||
                start.length() < 3 || end.length() < 3) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Start- und Endpunkt müssen angegeben werden"));
        }
        TourInformation tourInformation = new TourInformation();
        tourInformation.setDistance(25.0);
        tourInformation.setSessionId((long) (Math.random() * 1_000_000_000) + "");
        tourInformation.setImagePath(start + "_to_" + end + ".jpg");

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(tourInformation);
    }

    @Override
    public CompletableFuture<byte[]> getRouteImage(String sessionId, int tourId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return new byte[] {1,0,2,1};
        });
    }



}
