package TourProject.DataAccessLayer;

import TourProject.Model.Tour.Tour;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataAccessLayer {
    CompletableFuture<Integer> insertTour (Tour tour);
    CompletableFuture<Boolean> updateTour (Tour tour);
    void setTourList(List<Tour> tourList);
    List<Tour> getTourList();
}
