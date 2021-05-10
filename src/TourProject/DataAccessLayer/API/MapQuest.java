package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.Config;
import TourProject.model.api.RequestBody;
import TourProject.model.api.TourInformation;
import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class MapQuest implements TourAPI {

    private String apiKey = null;
    private TourInformation tourInformation = null;
    private int tourId;

    public void loadAPIKey() {
        apiKey = (String) Config.getInstance().getAttribute("apiKey");
    }

    @Override
    public CompletableFuture<TourInformation> getRouteInformation(String start, String end, int tourId) {
        this.tourId = tourId;
        HttpClient client = HttpClient.newHttpClient();
        StringBuilder website = new StringBuilder("https://www.mapquestapi.com/directions/v2/route");

        //"http://www.mapquestapi.com/directions/v2/route?key=KEY&from=Clarendon Blvd,Arlington,VA&to=2400+S+Glebe+Rd,+Arlington,+VA&routeType=bicycle";

        website.append("?key=").append(apiKey)
                .append("&from=").append(URLEncoder.encode(start, StandardCharsets.UTF_8))
                .append("&to=").append(URLEncoder.encode(end, StandardCharsets.UTF_8))
                .append("&routeType=bicycle");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(website.toString()))
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
                })
                .thenCompose(tourInformation -> {
                    return getRouteImage(tourInformation.getSessionId(), tourId, client)
                            .thenApply((imagePath) -> {
                                tourInformation.setImagePath(imagePath);
                                return tourInformation;
                            })
                            .exceptionally(ex -> {
                                return tourInformation;
                            });
                });


    }

    private CompletableFuture<String> getRouteImage(String sessionId, int tourId, HttpClient client) {
        StringBuilder website = new StringBuilder("https://www.mapquestapi.com/staticmap/v5/map");

        //"http://www.mapquestapi.com/directions/v2/route?key=KEY&from=Clarendon Blvd,Arlington,VA&to=2400+S+Glebe+Rd,+Arlington,+VA&routeType=bicycle";

        website.append("?key=").append(apiKey)
                .append("&session=").append(URLEncoder.encode(sessionId, StandardCharsets.UTF_8))
                .append("&format=jpg");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(website.toString()))
                .build();
        String imagePath = "tourbild-" + tourId + ".jpg";

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(Path.of(imagePath)))
                .thenApply((response) -> {
                    Path body = response.body();
                    if (response.statusCode() != 200) {
                        // TODO: Logging
                        body.toFile().delete();
                        var ex = new NullPointerException("ImagePath must not be null. Error while retrieving Route-image occurred.");
                        //System.err.println(ex.getMessage());
                        throw ex;
                    }
                    return body.toString();
                });
    }
}
