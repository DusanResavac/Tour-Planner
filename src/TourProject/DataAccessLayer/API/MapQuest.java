package TourProject.DataAccessLayer.API;

import TourProject.DataAccessLayer.CallbackRequestBody;
import TourProject.DataAccessLayer.Config;
import TourProject.DataAccessLayer.HTTPRequest;
import TourProject.model.api.RequestBody;
import TourProject.model.api.TourInformation;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MapQuest implements TourAPI, CallbackRequestBody {

    private String apiKey = null;
    private TourInformation tourInformation = null;
    private int tourId;
    private CallbackViewModel callbackViewModel;

    public void loadAPIKey() {
        apiKey = (String) Config.getInstance().getAttribute("apiKey");
    }

    @Override
    public void getRouteInformation(String start, String end, int tourId, CallbackViewModel c) {
        this.tourId = tourId;
        this.callbackViewModel = c;
        Gson gson = new Gson();
        StringBuilder website = new StringBuilder("https://www.mapquestapi.com/directions/v2/route");

        //"http://www.mapquestapi.com/directions/v2/route?key=KEY&from=Clarendon Blvd,Arlington,VA&to=2400+S+Glebe+Rd,+Arlington,+VA&routeType=bicycle";

        website.append("?key=").append(apiKey)
                .append("&from=").append(URLEncoder.encode(start, StandardCharsets.UTF_8))
                .append("&to=").append(URLEncoder.encode(end, StandardCharsets.UTF_8))
                .append("&routeType=bicycle");
        HTTPRequest httpRequest = new HTTPRequest(website.toString());
        httpRequest.request(this, null);
       /* String response = httpRequest.request(null);

        var requestBody = gson.fromJson(response, RequestBody.class);
        if (requestBody == null || requestBody.route == null || requestBody.route.getSessionId() == null) {
            return null;
        }
        var tourInformation = requestBody.route;
        tourInformation.setImagePath(getRouteImage(tourInformation.getSessionId(), tourId));*/

    }

    private void getRouteImage(String sessionId, int tourId) {
        StringBuilder website = new StringBuilder("https://www.mapquestapi.com/staticmap/v5/map");

        //"http://www.mapquestapi.com/directions/v2/route?key=KEY&from=Clarendon Blvd,Arlington,VA&to=2400+S+Glebe+Rd,+Arlington,+VA&routeType=bicycle";

        website.append("?key=").append(apiKey)
                .append("&session=").append(URLEncoder.encode(sessionId, StandardCharsets.UTF_8))
                .append("&format=jpg");
        HTTPRequest httpRequest = new HTTPRequest(website.toString());

        httpRequest.request(this, String.format("tour-%s.jpg", tourId));
    }

    @Override
    public void callback(String response, boolean image) {
        if (image) {
            tourInformation.setImagePath(response);
            callbackViewModel.callback(tourInformation);
        } else {
            Gson gson = new Gson();
            var requestBody = gson.fromJson(response, RequestBody.class);
            if (requestBody == null || requestBody.route == null || requestBody.route.getSessionId() == null) {
                return;
            }
            tourInformation = requestBody.route;
            getRouteImage(tourInformation.getSessionId(), tourId);
        }
    }
}
