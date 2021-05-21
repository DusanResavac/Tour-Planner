package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;
import TourProject.Model.api.RequestBody;
import TourProject.Model.api.TourInformation;
import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class MapQuest implements TourAPI {

    private String apiKey = null;
    private TourInformation tourInformation = null;
    private HttpClient client;


    public void loadAPIKey() {
        apiKey = (String) Config.getInstance().getAttribute("api_key");
    }

    @Override
    public CompletableFuture<TourInformation> getRouteInformation(String start, String end) {
        client = HttpClient.newHttpClient();

        //"http://www.mapquestapi.com/directions/v2/route?key=KEY&from=Clarendon Blvd,Arlington,VA&to=2400+S+Glebe+Rd,+Arlington,+VA&routeType=bicycle";

        String website = "https://www.mapquestapi.com/directions/v2/route" +
                "?key=" + apiKey +
                "&from=" + URLEncoder.encode(start == null ? "" : start, StandardCharsets.UTF_8) +
                "&to=" + URLEncoder.encode(end == null ? "" : end, StandardCharsets.UTF_8) +
                "&routeType=bicycle";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(website))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply((response)->{
                    Gson gson = new Gson();
                    var requestBody = gson.fromJson(response, RequestBody.class);
                    if (requestBody == null || requestBody.route == null || requestBody.route.getSessionId() == null) {
                        // TODO: Logging
                        throw new NullPointerException("RequestBody and properties must not be null");
                    }

                    return (TourInformation) requestBody.route;
                });
                /*.thenCompose(tourInformation -> {
                    return getRouteImage(tourInformation.getSessionId(), client)
                            .thenApply((imagePath) -> {
                                //tourInformation.setImagePath(imagePath);
                                return tourInformation;
                            });
                });*/


    }

    public CompletableFuture<byte[]> getRouteImage(String sessionId, int tourId) {

        //"http://www.mapquestapi.com/directions/v2/route?key=KEY&from=Clarendon Blvd,Arlington,VA&to=2400+S+Glebe+Rd,+Arlington,+VA&routeType=bicycle";

        String website = "https://www.mapquestapi.com/staticmap/v5/map" +
                "?key=" + apiKey +
                "&session=" + URLEncoder.encode(sessionId, StandardCharsets.UTF_8) +
                "&format=jpg";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(website))
                .build();
        String imagePath = "tourbild-" + tourId + ".jpg";

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply((response) -> {
                    if (response.statusCode() != 200) {
                        // TODO: Logging
                        throw new NullPointerException("Error while retrieving route-image occurred.");
                    }
                    return response.body();
                });
    }

}
