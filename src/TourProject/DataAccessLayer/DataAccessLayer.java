package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataAccessLayer {
    CompletableFuture<Long> insertTour (Tour tour);
    CompletableFuture<Boolean> updateTour (Tour tour);
    void setTourList(List<Tour> tourList);
    CompletableFuture<List<Tour>> retrieveData (boolean alsoSetData);
    List<Tour> getTourList();
}
